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

import com.google.common.collect.ImmutableList;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import org.xrpl.xrpl4j.client.JsonRpcClient;

import java.time.Duration;
import java.util.Date;
import java.util.List;

/**
 * Feign error decoder for retrying requests based on HTTP status.
 */
public class RetryStatusDecoder implements ErrorDecoder {

  private final ErrorDecoder defaultErrorDecoder = new Default();

  private final Duration retryInterval;
  private final List<Integer> retryStatuses;

  /**
   * Constructs the custom http response status decoder.
   *
   * @param retryInterval        the duration after which a retry will be initiated.
   * @param retryHttpStatusCodes http status codes that can be retried
   */
  public RetryStatusDecoder(Duration retryInterval, Integer... retryHttpStatusCodes) {
    this.retryStatuses = ImmutableList.<Integer>builder()
            .add(retryHttpStatusCodes)
            .build();
    this.retryInterval = retryInterval;
  }

  @Override
  public Exception decode(String methodKey, Response response) {
    Exception exception = defaultErrorDecoder.decode(methodKey, response);
    if (retryStatuses.contains(response.status())) {
      JsonRpcClient.logger.error(
              String.format("##### Got %s response from %s #######", response.status(), methodKey)
      );
      return new RetryableException(
        response.status(),
        exception.getMessage(),
        response.request().httpMethod(),
        Date.from(new Date().toInstant().plus(retryInterval)),
        response.request()
      );
    }
    return exception;
  }
}
