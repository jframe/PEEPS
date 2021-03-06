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
package tech.pegasys.peeps.privacy.model;

import static com.google.common.base.Preconditions.checkArgument;

public class PrivacyManagerIdentifier {

  private final String id;

  public PrivacyManagerIdentifier(final String id) {
    checkArgument(id != null, "Identifier is mandatory");
    this.id = id;
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public boolean equals(final Object other) {
    return other instanceof PrivacyManagerIdentifier
        && id.equals(((PrivacyManagerIdentifier) other).id);
  }
}
