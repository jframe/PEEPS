/*
 * Copyright 2020 ConsenSys AG.
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
package tech.pegasys.peeps.node.genesis.ibft;

import static tech.pegasys.peeps.node.genesis.bft.BftConfig.DEFAULT_BLOCK_PERIOD_SECONDS;
import static tech.pegasys.peeps.node.genesis.bft.BftConfig.DEFAULT_EPOCH_LENGTH;
import static tech.pegasys.peeps.node.genesis.bft.BftConfig.DEFAULT_REQUEST_TIMEOUT_SECONDS;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonGetter;

public class GoQuorumIbftOptions {

  private final int blockPeriodSeconds;
  private final int epochLength;
  private final int requestTimeoutSeconds;
  private final Optional<Integer> qbftBlock;
  private final Optional<Integer> ceil2nBy3Block;
  private final int policy;

  public GoQuorumIbftOptions() {
    this(
        DEFAULT_BLOCK_PERIOD_SECONDS,
        DEFAULT_EPOCH_LENGTH,
        DEFAULT_REQUEST_TIMEOUT_SECONDS,
        Optional.empty(),
        Optional.empty(),
        0);
  }

  public static GoQuorumIbftOptions createQbft() {
    return new GoQuorumIbftOptions(
        DEFAULT_BLOCK_PERIOD_SECONDS,
        DEFAULT_EPOCH_LENGTH,
        DEFAULT_REQUEST_TIMEOUT_SECONDS,
        Optional.of(0),
        Optional.of(0),
        0);
  }

  public GoQuorumIbftOptions(
      final int blockPeriodSeconds,
      final int epochLength,
      final int requestTimeoutSeconds,
      final Optional<Integer> qbftBlock,
      final Optional<Integer> ceil2nBy3Block,
      final int policy) {
    this.blockPeriodSeconds = blockPeriodSeconds;
    this.epochLength = epochLength;
    this.requestTimeoutSeconds = requestTimeoutSeconds;
    this.qbftBlock = qbftBlock;
    this.ceil2nBy3Block = ceil2nBy3Block;
    this.policy = policy;
  }

  @JsonGetter("blockperiod")
  public int getBlockPeriodSeconds() {
    return blockPeriodSeconds;
  }

  @JsonGetter("epoch")
  public int getEpochLength() {
    return epochLength;
  }

  @JsonGetter("requesttimeout")
  public int getRequestTimeoutSeconds() {
    return requestTimeoutSeconds;
  }

  @JsonGetter("qbftblock")
  public Optional<Integer> getQbftBlock() {
    return qbftBlock;
  }

  @JsonGetter("ceil2nby3block")
  public Optional<Integer> getCeil2nBy3Block() {
    return ceil2nBy3Block;
  }

  @JsonGetter("policy")
  public Integer getPolicy() {
    return policy;
  }
}
