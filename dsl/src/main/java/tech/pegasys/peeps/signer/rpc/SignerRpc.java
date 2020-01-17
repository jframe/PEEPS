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
package tech.pegasys.peeps.signer.rpc;

import tech.pegasys.peeps.node.model.Hash;
import tech.pegasys.peeps.node.rpc.NodeRpc;
import tech.pegasys.peeps.signer.rpc.eea.SendPrivacyTransactionRequest;
import tech.pegasys.peeps.signer.rpc.eea.SendPrivacyTransactionResponse;
import tech.pegasys.peeps.signer.rpc.eth.SendTransactionRequest;
import tech.pegasys.peeps.signer.rpc.eth.SendTransactionResponse;
import tech.pegasys.peeps.signer.rpc.net.EnodeResponse;

import java.time.Duration;

import io.vertx.core.Vertx;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tuweni.eth.Address;
import org.apache.tuweni.units.ethereum.Wei;

public class SignerRpc extends NodeRpc {

  private static final Logger LOG = LogManager.getLogger();
  private static final Address NO_RECIPIENT = null;

  public SignerRpc(final Vertx vertx, final Duration timeout) {
    super(vertx, timeout, LOG);
  }

  // TODO all the RPCs should use addresses and privacy address instead of String
  public Hash deployContractToPrivacyGroup(
      final Address sender,
      final String binary,
      final String privateSender,
      final String[] privateRecipients) {
    return post(
            "eea_sendTransaction",
            SendPrivacyTransactionResponse.class,
            new SendPrivacyTransactionRequest(
                sender, NO_RECIPIENT, binary, privateSender, privateRecipients))
        .getResult();
  }

  public Hash transfer(final Address sender, final Address receiver, final Wei amount) {
    return post(
            "eth_sendTransaction",
            SendTransactionResponse.class,
            new SendTransactionRequest(sender, receiver, null, amount))
        .getResult();
  }

  public String enode() {
    return post("net_enode", EnodeResponse.class).getResult();
  }
}