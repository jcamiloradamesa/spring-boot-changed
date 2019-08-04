/*
 * Copyright 2012-2018 the original author or authors.
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

package org.springframework.boot.autoconfigure.web.reactive.function.client;

import java.util.function.Function;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.client.reactive.ReactorResourceFactory;

/**
 * Configuration classes for WebClient client connectors.
 * <p>
 * Those should be {@code @Import} in a regular auto-configuration class to guarantee
 * their order of execution.
 *
 * @author Brian Clozel
 */
@Configuration
class ClientHttpConnectorConfiguration {

	@Configuration
	@ConditionalOnClass(reactor.netty.http.client.HttpClient.class)
	@ConditionalOnMissingBean(ClientHttpConnector.class)
	public static class ReactorNetty {

		@Bean
		@ConditionalOnMissingBean
		public ReactorResourceFactory reactorResourceFactory() {
			ReactorResourceFactory factory = new ReactorResourceFactory();
			factory.setGlobalResources(false);
			return factory;
		}

		@Bean
		public ReactorClientHttpConnector reactorClientHttpConnector(
				ReactorResourceFactory reactorResourceFactory) {
			return new ReactorClientHttpConnector(reactorResourceFactory,
					Function.identity());
		}

	}

}
