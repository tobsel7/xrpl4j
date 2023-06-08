package org.xrpl.xrpl4j.client;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.Beta;
import feign.Feign;
import feign.Headers;
import feign.RequestLine;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.optionals.OptionalDecoder;
import okhttp3.HttpUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xrpl.xrpl4j.model.client.XrplResult;
import org.xrpl.xrpl4j.model.jackson.ObjectMapperFactory;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

/**
 * A feign HTTP client for interacting with the rippled JSON RPC API. This client is strictly responsible for making
 * network calls and deserializing responses. All higher order functionality such as signing and serialization should be
 * implemented in a wrapper class.
 *
 * <p>Note: This client is currently marked as {@link Beta}, and should be used as a reference implementation ONLY.
 */
@Beta
public interface JsonRpcClient {

  /** The default logger for client. */
  Logger logger = LoggerFactory.getLogger(JsonRpcClient.class);

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
   * A default object mapper for JSON serialization.
   */
  ObjectMapper OBJECT_MAPPER = ObjectMapperFactory.create();

  /**
   * Constructs a new client for the given url.
   *
   * @param rippledUrl url for the faucet server.
   *
   * @return A {@link JsonRpcClient} that can make request to {@code rippledUrl}
   */
  static JsonRpcClient construct(final HttpUrl rippledUrl) {
    Objects.requireNonNull(rippledUrl);

    return Feign.builder()
      .encoder(new JacksonEncoder(OBJECT_MAPPER))
      .errorDecoder(new RetryStatusDecoder(RETRY_INTERVAL, RETRY_HTTP_STATUSES))
      .decoder(new OptionalDecoder(new JacksonDecoder(OBJECT_MAPPER)))
      .target(JsonRpcClient.class, rippledUrl.toString());
  }

  /**
   * Send a POST request to the rippled server with {@code rpcRequest} in the request body.
   *
   * @param rpcRequest A rippled JSON RPC API request object.
   *
   * @return A {@link JsonNode} which can be manually parsed containing the response.
   */
  @RequestLine("POST /")
  @Headers( {
    HEADER_ACCEPT + ": " + APPLICATION_JSON,
    HEADER_CONTENT_TYPE + ": " + APPLICATION_JSON,
  })
  JsonNode postRpcRequest(JsonRpcRequest rpcRequest);

  /**
   * Send a given request to rippled.
   *
   * @param request    The {@link JsonRpcRequest} to send to the server.
   * @param resultType The type of {@link XrplResult} that should be returned.
   * @param <T>        The extension of {@link XrplResult} corresponding to the request method.
   *
   * @return The {@link T} representing the result of the request.
   *
   * @throws JsonRpcClientErrorException If rippled returns an error message, or if the response could not be
   *                                     deserialized to the provided {@link JsonRpcRequest} type.
   */
  default <T extends XrplResult> T send(
    JsonRpcRequest request,
    Class<T> resultType
  ) throws JsonRpcClientErrorException {
    JavaType javaType = OBJECT_MAPPER.constructType(resultType);
    return send(request, javaType);
  }

  /**
   * Send a given request to rippled. Unlike {@link JsonRpcClient#send(JsonRpcRequest, Class)}, this override requires a
   * {@link JavaType} as the resultType, which can be useful when expecting a {@link XrplResult} with type parameters.
   * In this case, you can use an {@link ObjectMapper}'s {@link com.fasterxml.jackson.databind.type.TypeFactory} to
   * construct parameterized types.
   *
   * @param request    The {@link JsonRpcRequest} to send to the server.
   * @param resultType The type of {@link XrplResult} that should be returned, converted to a {@link JavaType}.
   * @param <T>        The extension of {@link XrplResult} corresponding to the request method.
   *
   * @return The {@link T} representing the result of the request.
   *
   * @throws JsonRpcClientErrorException If rippled returns an error message, or if the response could not be
   *                                     deserialized to the provided {@link JsonRpcRequest} type.
   */
  default <T extends XrplResult> T send(
    JsonRpcRequest request,
    JavaType resultType
  ) throws JsonRpcClientErrorException {
    JsonNode response = postRpcRequest(request);
    JsonNode result = response.get("result");
    checkForError(response);
    try {
      return OBJECT_MAPPER.readValue(result.toString(), resultType);
    } catch (JsonProcessingException e) {
      throw new JsonRpcClientErrorException(e);
    }
  }

  /**
   * Parse the response JSON to detect a possible error response message.
   *
   * @param response The {@link JsonNode} containing the JSON response from rippled.
   *
   * @throws JsonRpcClientErrorException If rippled returns an error message.
   */
  default void checkForError(JsonNode response) throws JsonRpcClientErrorException {
    if (response.has("result")) {
      JsonNode result = response.get("result");
      if (result.has("error")) {
        String errorMessage = Optional.ofNullable(result.get("error_exception"))
          .map(JsonNode::asText)
          .orElseGet(() -> result.get("error_message").asText());
        throw new JsonRpcClientErrorException(errorMessage);
      }
    }
  }

}
