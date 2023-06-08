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

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import feign.Headers;
import feign.RequestLine;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.optionals.OptionalDecoder;
import okhttp3.HttpUrl;
import org.xrpl.xrpl4j.client.RetryStatusDecoder;
import org.xrpl.xrpl4j.model.jackson.ObjectMapperFactory;

import java.time.Duration;
import java.util.Objects;

/**
 * A feign HTTP client for interacting with the Testnet Faucet REST API.
 */
public interface FaucetClient {

  /** The Accept header name used in RPC requests. */
  String HEADER_ACCEPT = "Accept";
  /** The Content-Type header name used in RPC requests. */
  String HEADER_CONTENT_TYPE = "Content-Type";
  /** The Accept and Content-Type header value used in RPC requests. */
  String APPLICATION_JSON = "application/json";

  /**
   * An array of http statuses that can be retried.
   * 503 - Rate limiting will return a service unavailable and can be retried.
   */
  Integer[] RETRY_HTTP_STATUSES = {
    503 // Service unavailable.
  };
  /** The retry interval after which a failed request should be repeated. */
  Duration RETRY_INTERVAL = Duration.ofSeconds(1);

  /**
   * Constructs a new client for the given url.
   *
   * @param faucetUrl url for the faucet server.
   *
   * @return A {@link FaucetClient}.
   */
  static FaucetClient construct(final HttpUrl faucetUrl) {
    Objects.requireNonNull(faucetUrl);

    final ObjectMapper objectMapper = ObjectMapperFactory.create();
    return Feign.builder()
      .encoder(new JacksonEncoder(objectMapper))
      .errorDecoder(new RetryStatusDecoder(RETRY_INTERVAL, RETRY_HTTP_STATUSES))
      .decoder(new OptionalDecoder(new JacksonDecoder(objectMapper)))
      .target(FaucetClient.class, faucetUrl.toString());
  }

  /**
   * Request a new account to be created and funded by the test faucet.
   *
   * @param request A {@link FundAccountRequest} to send.
   *
   * @return A {@link FaucetAccountResponse} containing the faucet response fields.
   */
  @RequestLine("POST /accounts")
  @Headers( {
    HEADER_ACCEPT + ": " + APPLICATION_JSON,
    HEADER_CONTENT_TYPE + ": " + APPLICATION_JSON,
  })
  FaucetAccountResponse fundAccount(FundAccountRequest request);

}
