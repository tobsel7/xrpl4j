package org.xrpl.xrpl4j.model.flags;

/*-
 * ========================LICENSE_START=================================
 * xrpl4j :: core
 * %%
 * Copyright (C) 2020 - 2023 XRPL Foundation and its contributors
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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class NfTokenOfferFlagsTests extends AbstractFlagsTest {

  public static Stream<Arguments> data() {
    return getBooleanCombinations(2);
  }

  @ParameterizedTest
  @MethodSource("data")
  public void testDeriveIndividualFlagsFromFlags(
    boolean lsfBuyToken,
    boolean lsfAuthorized
  ) {
    long expectedFlags = (lsfBuyToken ? NfTokenOfferFlags.BUY_TOKEN.getValue() : 0L) |
      (lsfAuthorized ? NfTokenOfferFlags.AUTHORIZED.getValue() : 0L);

    NfTokenOfferFlags flags = NfTokenOfferFlags.of(expectedFlags);

    assertThat(flags.getValue()).isEqualTo(expectedFlags);
    assertThat(flags.lsfBuyToken()).isEqualTo(lsfBuyToken);
    assertThat(flags.lsfAuthorized()).isEqualTo(lsfAuthorized);
  }
}
