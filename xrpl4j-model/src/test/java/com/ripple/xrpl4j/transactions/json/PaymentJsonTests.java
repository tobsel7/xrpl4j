package com.ripple.xrpl4j.transactions.json;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.primitives.UnsignedInteger;
import com.ripple.xrpl4j.jackson.ObjectMapperFactory;
import com.ripple.xrpl4j.transactions.Address;
import com.ripple.xrpl4j.transactions.IssuedCurrencyAmount;
import com.ripple.xrpl4j.transactions.Payment;
import org.junit.Before;
import org.junit.Test;

public class PaymentJsonTests {

  ObjectMapper objectMapper;

  @Before
  public void setUp() {
    objectMapper = ObjectMapperFactory.create();
  }

  @Test
  public void testJson() throws JsonProcessingException {
    Payment payment = Payment.builder()
      .account(Address.of("r9TeThyi5xiuUUrFjtPKZiHcDxs7K9H6Rb"))
      .destination(Address.of("r4BPgS7DHebQiU31xWELvZawwSG2fSPJ7C"))
      .amount(IssuedCurrencyAmount.builder()
        .currency("USD")
        .issuer(Address.of("r9TeThyi5xiuUUrFjtPKZiHcDxs7K9H6Rb"))
        .value("25000")
        .build()
      )
      .fee("10")
      .tfFullyCanonicalSig(false)
      .sequence(UnsignedInteger.valueOf(2))
      .build();

    String serialized = objectMapper.writeValueAsString(payment);
    Payment deserialized = objectMapper.readValue(serialized, Payment.class);
    assertThat(deserialized).isEqualTo(payment);
  }

}
