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
package tech.pegasys.peeps.node.genesis.bft;

import com.fasterxml.jackson.annotation.JsonGetter;

// NOTE: This is only for Besu (GoQuorum needs their own version
public class BftConfig {

  public static final int DEFAULT_BLOCK_PERIOD_SECONDS = 2;
  public static final int DEFAULT_EPOCH_LENGTH = 30000;
  public static final int DEFAULT_REQUEST_TIMEOUT_SECONDS = 10;

  private final int blockPeriodSeconds;
  private final int epochLength;
  private final int requestTimeoutSeconds;

  public BftConfig() {
    this(DEFAULT_BLOCK_PERIOD_SECONDS, DEFAULT_EPOCH_LENGTH, DEFAULT_REQUEST_TIMEOUT_SECONDS);
  }

  public BftConfig(
      final int blockPeriodSeconds, final int epochLength, final int requestTimeoutSeconds) {
    this.blockPeriodSeconds = blockPeriodSeconds;
    this.epochLength = epochLength;
    this.requestTimeoutSeconds = requestTimeoutSeconds;
  }

  @JsonGetter("blockperiodseconds")
  public int getBlockPeriodSeconds() {
    return blockPeriodSeconds;
  }

  @JsonGetter("epochlength")
  public int getEpochLength() {
    return epochLength;
  }

  @JsonGetter("requesttimeoutseconds")
  public int getRequestTimeoutSeconds() {
    return requestTimeoutSeconds;
  }
}
