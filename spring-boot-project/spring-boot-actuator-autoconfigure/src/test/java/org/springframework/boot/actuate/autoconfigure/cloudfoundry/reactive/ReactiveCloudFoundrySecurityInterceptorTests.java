/*
 * Copyright 2012-2017 the original author or authors.
 *
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
 */

package org.springframework.boot.actuate.autoconfigure.cloudfoundry.reactive;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import org.springframework.boot.actuate.autoconfigure.cloudfoundry.AccessLevel;
import org.springframework.boot.actuate.autoconfigure.cloudfoundry.CloudFoundryAuthorizationException;
import org.springframework.boot.actuate.autoconfigure.cloudfoundry.CloudFoundryAuthorizationException.Reason;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.util.Base64Utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * Tests for {@link ReactiveCloudFoundrySecurityInterceptor}.
 *
 * @author Madhura Bhave
 */
public class ReactiveCloudFoundrySecurityInterceptorTests {

	@Mock
	private ReactiveTokenValidator tokenValidator;

	@Mock
	private ReactiveCloudFoundrySecurityService securityService;

	private ReactiveCloudFoundrySecurityInterceptor interceptor;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.interceptor = new ReactiveCloudFoundrySecurityInterceptor(
				this.tokenValidator, this.securityService, "my-app-id");
	}

	@Test
	public void preHandleWhenRequestIsPreFlightShouldBeOk() throws Exception {
		MockServerWebExchange request = MockServerWebExchange.from(MockServerHttpRequest
				.options("/a").header(HttpHeaders.ORIGIN, "http://example.com")
				.header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET").build());
		StepVerifier.create(this.interceptor.preHandle(request, "/a")).consumeNextWith(
				(response) -> assertThat(response.getStatus()).isEqualTo(HttpStatus.OK))
				.verifyComplete();
	}

	@Test
	public void preHandleWhenTokenIsMissingShouldReturnMissingAuthorization()
			throws Exception {
		MockServerWebExchange request = MockServerWebExchange
				.from(MockServerHttpRequest.get("/a").build());
		StepVerifier.create(this.interceptor.preHandle(request, "/a"))
				.consumeNextWith((response) -> assertThat(response.getStatus())
						.isEqualTo(Reason.MISSING_AUTHORIZATION.getStatus()))
				.verifyComplete();
	}

	@Test
	public void preHandleWhenTokenIsNotBearerShouldReturnMissingAuthorization()
			throws Exception {
		MockServerWebExchange request = MockServerWebExchange.from(MockServerHttpRequest
				.get("/a").header(HttpHeaders.AUTHORIZATION, mockAccessToken()).build());
		StepVerifier.create(this.interceptor.preHandle(request, "/a"))
				.consumeNextWith((response) -> assertThat(response.getStatus())
						.isEqualTo(Reason.MISSING_AUTHORIZATION.getStatus()))
				.verifyComplete();
	}

	@Test
	public void preHandleWhenApplicationIdIsNullShouldReturnError() throws Exception {
		this.interceptor = new ReactiveCloudFoundrySecurityInterceptor(
				this.tokenValidator, this.securityService, null);
		MockServerWebExchange request = MockServerWebExchange
				.from(MockServerHttpRequest.get("/a")
						.header(HttpHeaders.AUTHORIZATION, "bearer " + mockAccessToken())
						.build());
		StepVerifier.create(this.interceptor.preHandle(request, "/a"))
				.consumeErrorWith((ex) -> assertThat(
						((CloudFoundryAuthorizationException) ex).getReason())
								.isEqualTo(Reason.SERVICE_UNAVAILABLE))
				.verify();
	}

	@Test
	public void preHandleWhenCloudFoundrySecurityServiceIsNullShouldReturnError()
			throws Exception {
		this.interceptor = new ReactiveCloudFoundrySecurityInterceptor(
				this.tokenValidator, null, "my-app-id");
		MockServerWebExchange request = MockServerWebExchange.from(MockServerHttpRequest
				.get("/a").header(HttpHeaders.AUTHORIZATION, mockAccessToken()).build());
		StepVerifier.create(this.interceptor.preHandle(request, "/a"))
				.consumeErrorWith((ex) -> assertThat(
						((CloudFoundryAuthorizationException) ex).getReason())
								.isEqualTo(Reason.SERVICE_UNAVAILABLE))
				.verify();
	}

	@Test
	public void preHandleWhenAccessIsNotAllowedShouldReturnAccessDenied()
			throws Exception {
		given(this.securityService.getAccessLevel(mockAccessToken(), "my-app-id"))
				.willReturn(Mono.just(AccessLevel.RESTRICTED));
		given(this.tokenValidator.validate(any())).willReturn(Mono.empty());
		MockServerWebExchange request = MockServerWebExchange
				.from(MockServerHttpRequest.get("/a")
						.header(HttpHeaders.AUTHORIZATION, "bearer " + mockAccessToken())
						.build());
		StepVerifier.create(this.interceptor.preHandle(request, "/a"))
				.consumeNextWith((response) -> assertThat(response.getStatus())
						.isEqualTo(Reason.ACCESS_DENIED.getStatus()))
				.verifyComplete();
	}

	@Test
	public void preHandleSuccessfulWithFullAccess() throws Exception {
		String accessToken = mockAccessToken();
		given(this.securityService.getAccessLevel(accessToken, "my-app-id"))
				.willReturn(Mono.just(AccessLevel.FULL));
		given(this.tokenValidator.validate(any())).willReturn(Mono.empty());
		MockServerWebExchange exchange = MockServerWebExchange
				.from(MockServerHttpRequest.get("/a")
						.header(HttpHeaders.AUTHORIZATION, "bearer " + mockAccessToken())
						.build());
		StepVerifier.create(this.interceptor.preHandle(exchange, "/a"))
				.consumeNextWith((response) -> {
					assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
					assertThat((AccessLevel) exchange
							.getAttribute("cloudFoundryAccessLevel"))
									.isEqualTo(AccessLevel.FULL);
				}).verifyComplete();
	}

	@Test
	public void preHandleSuccessfulWithRestrictedAccess() throws Exception {
		String accessToken = mockAccessToken();
		given(this.securityService.getAccessLevel(accessToken, "my-app-id"))
				.willReturn(Mono.just(AccessLevel.RESTRICTED));
		given(this.tokenValidator.validate(any())).willReturn(Mono.empty());
		MockServerWebExchange exchange = MockServerWebExchange
				.from(MockServerHttpRequest.get("/info")
						.header(HttpHeaders.AUTHORIZATION, "bearer " + mockAccessToken())
						.build());
		StepVerifier.create(this.interceptor.preHandle(exchange, "info"))
				.consumeNextWith((response) -> {
					assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
					assertThat((AccessLevel) exchange
							.getAttribute("cloudFoundryAccessLevel"))
									.isEqualTo(AccessLevel.RESTRICTED);
				}).verifyComplete();
	}

	private String mockAccessToken() {
		return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJ0b3B0YWwu"
				+ "Y29tIiwiZXhwIjoxNDI2NDIwODAwLCJhd2Vzb21lIjp0cnVlfQ."
				+ Base64Utils.encodeToString("signature".getBytes());
	}

}
