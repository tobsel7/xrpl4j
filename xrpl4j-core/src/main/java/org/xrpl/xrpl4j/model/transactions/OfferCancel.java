package org.xrpl.xrpl4j.model.transactions;

/*-
 * ========================LICENSE_START=================================
 * xrpl4j :: model
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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.primitives.UnsignedInteger;
import org.immutables.value.Value;
import org.immutables.value.Value.Immutable;
import org.xrpl.xrpl4j.model.flags.TransactionFlags;

import java.util.Optional;

/**
 * An OfferCancel transaction removes an Offer object from the XRP Ledger.
 *
 * @see "https://xrpl.org/offercancel.html"
 */
@Immutable
@JsonSerialize(as = ImmutableOfferCancel.class)
@JsonDeserialize(as = ImmutableOfferCancel.class)
public interface OfferCancel extends Transaction {

  /**
   * Construct a builder for this class.
   *
   * @return An {@link ImmutableOfferCancel.Builder}.
   */
  static ImmutableOfferCancel.Builder builder() {
    return ImmutableOfferCancel.builder();
  }

  /**
   * Set of {@link TransactionFlags}s for this {@link OfferCancel}, which only allows {@code tfFullyCanonicalSig}
   * flag.
   *
   * <p>The value of the flags cannot be set manually, but exists for JSON serialization/deserialization only and for
   * proper signature computation in rippled.
   *
   * @return Always {@link TransactionFlags} with {@code tfFullyCanonicalSig} set.
   */
  @JsonProperty("Flags")
  @Value.Default
  default TransactionFlags flags() {
    return new TransactionFlags.Builder().build();
  }

  /**
   * The sequence number of a previous {@link OfferCreate} transaction.
   *
   * @return An {@link Optional} of type {@link UnsignedInteger} representing the offer sequence.
   */
  @JsonProperty("OfferSequence")
  Optional<UnsignedInteger> offerSequence();

}
