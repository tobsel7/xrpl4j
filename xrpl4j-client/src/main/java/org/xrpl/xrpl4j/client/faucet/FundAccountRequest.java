package org.xrpl.xrpl4j.client.faucet;

/*-
 * ========================LICENSE_START=================================
 * xrpl4j :: client
 * %%
 * Copyright (C) 2020 - 2022 XRPL Foundation and its contributors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value.Immutable;
import org.xrpl.xrpl4j.model.transactions.Address;

/**
 * Request object for POST requests to the /accounts API on the XRPL faucet.
 */
@Immutable
@JsonSerialize(as = ImmutableFundAccountRequest.class)
@JsonDeserialize(as = ImmutableFundAccountRequest.class)
public interface FundAccountRequest {

  /**
   * Construct an {@link ImmutableFundAccountRequest.Builder}.
   * @return An {@link ImmutableFundAccountRequest.Builder}.
   */
  static ImmutableFundAccountRequest.Builder builder() {
    return ImmutableFundAccountRequest.builder();
  }

  /**
   * Construct a {@link FundAccountRequest} for the given address.
   *
   * @param classicAddress The {@link Address} of the account to fund.
   *
   * @return A {@link FundAccountRequest}.
   */
  static FundAccountRequest of(Address classicAddress) {
    return builder().destination(classicAddress).build();
  }

  /**
   * The account to be funded.
   *
   * @return The {@link Address} containing the classic address of the account.
   */
  Address destination();

}
