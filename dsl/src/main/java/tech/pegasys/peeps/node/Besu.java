/*
 * Copyright 2019 ConsenSys AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package tech.pegasys.peeps.node;

import static com.google.common.base.Preconditions.checkArgument;

import tech.pegasys.peeps.node.rpc.BesuQbftRpcClient;
import tech.pegasys.peeps.node.rpc.QbftRpc;
import tech.pegasys.peeps.util.DockerLogs;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tuweni.bytes.Bytes;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.PullPolicy;
import org.testcontainers.utility.MountableFile;

public class Besu extends Web3Provider {

  private static final Logger LOG = LogManager.getLogger();

  private static final String AM_I_ALIVE_ENDPOINT = "/liveness";
  private static final int ALIVE_STATUS_CODE = 200;

  private static final String BESU_IMAGE = "hyperledger/besu:develop";
  private static final String CONTAINER_GENESIS_FILE = "/etc/besu/genesis.json";
  private static final String CONTAINER_STATIC_NODES_FILE = "/opt/besu/static-nodes.json";
  private static final String CONTAINER_PRIVACY_PUBLIC_KEY_FILE =
      "/etc/besu/privacy_public_key.pub";
  private static final String CONTAINER_NODE_PRIVATE_KEY_FILE = "/etc/besu/keys/node.priv";
  private static final String CONTAINER_PRIVACY_SIGNING_PRIVATE_KEY_FILE =
      "/etc/besu/keys/pmt_signing.priv";

  public Besu(final Web3ProviderConfiguration config) {
    super(
        config,
        new GenericContainer<>(BESU_IMAGE)
            .withImagePullPolicy(PullPolicy.ageBased(Duration.ofHours(1))));
    final List<String> commandLineOptions = standardCommandLineOptions();

    addPeerToPeerHost(config, commandLineOptions);
    addCorsOrigins(config, commandLineOptions);
    addContainerNetwork(config, container);
    addContainerIpAddress(config.getIpAddress(), container);
    addNodePrivateKey(config, commandLineOptions, container);
    addGenesisFile(config, commandLineOptions, container);
    addStaticNodesFile(config, container);
    commandLineOptions.addAll(List.of("--network-id", "15"));

    if (config.isPrivacyEnabled()) {
      addPrivacy(config, commandLineOptions, container);
    }

    LOG.info("Besu command line: {}", commandLineOptions);
    container.withCommand(commandLineOptions.toArray(new String[0])).waitingFor(liveliness());
  }

  @Override
  protected QbftRpc qbftRpc(final Web3ProviderConfiguration config) {
    return new BesuQbftRpcClient(jsonRpcClient);
  }

  @Override
  public String getLogs() {
    return DockerLogs.format("Besu", container);
  }

  private HttpWaitStrategy liveliness() {
    return Wait.forHttp(AM_I_ALIVE_ENDPOINT)
        .forStatusCode(ALIVE_STATUS_CODE)
        .forPort(CONTAINER_HTTP_RPC_PORT);
  }

  private List<String> standardCommandLineOptions() {
    return Lists.newArrayList(
        "--logging",
        "DEBUG",
        "--miner-enabled",
        "--miner-coinbase",
        "1b23ba34ca45bb56aa67bc78be89ac00ca00da00",
        "--min-gas-price",
        "0",
        "--host-whitelist",
        "*",
        "--sync-mode",
        "full",
        "--rpc-http-enabled",
        "--rpc-ws-enabled",
        "--rpc-http-apis",
        "ADMIN,ETH,NET,WEB3,EEA,PRIV,QBFT");
  }

  private void addPeerToPeerHost(
      final Web3ProviderConfiguration config, final List<String> commandLineOptions) {
    commandLineOptions.add("--p2p-host");
    commandLineOptions.add(config.getIpAddress().get());
  }

  private void addContainerNetwork(
      final Web3ProviderConfiguration config, final GenericContainer<?> container) {
    container.withNetwork(config.getContainerNetwork());
  }

  private void addCorsOrigins(
      final Web3ProviderConfiguration config, final List<String> commandLineOptions) {

    config
        .getCors()
        .ifPresent(
            cors -> commandLineOptions.addAll(Lists.newArrayList("--rpc-http-cors-origins", cors)));
  }

  private void addNodePrivateKey(
      final Web3ProviderConfiguration config,
      final List<String> commandLineOptions,
      final GenericContainer<?> container) {

    final Path keyFile =
        createMountableTempFile(
            Bytes.wrap(
                config
                    .getNodeKeys()
                    .secretKey()
                    .bytes()
                    .toUnprefixedHexString()
                    .getBytes(StandardCharsets.UTF_8)));

    container.withCopyFileToContainer(
        MountableFile.forHostPath(keyFile), CONTAINER_NODE_PRIVATE_KEY_FILE);
    commandLineOptions.addAll(
        Lists.newArrayList("--node-private-key-file", CONTAINER_NODE_PRIVATE_KEY_FILE));
  }

  private void addGenesisFile(
      final Web3ProviderConfiguration config,
      final List<String> commandLineOptions,
      final GenericContainer<?> container) {
    commandLineOptions.add("--genesis-file");
    commandLineOptions.add(CONTAINER_GENESIS_FILE);
    container.withCopyFileToContainer(
        MountableFile.forHostPath(config.getGenesisFile()), CONTAINER_GENESIS_FILE);
  }

  private void addStaticNodesFile(
      final Web3ProviderConfiguration config, final GenericContainer<?> container) {
    container.withCopyFileToContainer(
        MountableFile.forHostPath(config.getStaticNodesFile()), CONTAINER_STATIC_NODES_FILE);
  }

  private void addPrivacy(
      final Web3ProviderConfiguration config,
      final List<String> commandLineOptions,
      final GenericContainer<?> container) {

    checkArgument(
        config.getPrivacyUrl() != null && config.getPrivacyUrl().isPresent(),
        "Privacy URL is mandatory when using Privacy");
    checkArgument(
        config.getPrivacyMarkerSigningPrivateKeyFile() != null
            && config.getPrivacyMarkerSigningPrivateKeyFile().isPresent(),
        "Private Marker Transaction key file is mandatory when using Privacy");

    commandLineOptions.add("--privacy-enabled");
    commandLineOptions.add("--privacy-url");
    commandLineOptions.add(config.getPrivacyUrl().get());
    commandLineOptions.add("--privacy-public-key-file");
    commandLineOptions.add(CONTAINER_PRIVACY_PUBLIC_KEY_FILE);
    container.withClasspathResourceMapping(
        config.getPrivacyPublicKeyResource(),
        CONTAINER_PRIVACY_PUBLIC_KEY_FILE,
        BindMode.READ_ONLY);
    commandLineOptions.add("--privacy-marker-transaction-signing-key-file");
    commandLineOptions.add(CONTAINER_PRIVACY_SIGNING_PRIVATE_KEY_FILE);

    container.withClasspathResourceMapping(
        config.getPrivacyMarkerSigningPrivateKeyFile().get(),
        CONTAINER_PRIVACY_SIGNING_PRIVATE_KEY_FILE,
        BindMode.READ_ONLY);
  }
}
