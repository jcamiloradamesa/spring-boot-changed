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

package org.springframework.boot.autoconfigure.mongo;

import javax.annotation.PreDestroy;

import com.mongodb.async.client.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Reactive Mongo.
 *
 * @author Mark Paluch
 * @since 2.0.0
 */
@Configuration
@ConditionalOnClass(MongoClient.class)
@EnableConfigurationProperties(MongoProperties.class)
public class ReactiveMongoAutoConfiguration {

	private final MongoProperties properties;

	private final MongoClientSettings settings;

	private final Environment environment;

	private final ReactiveMongoClientFactory factory;

	private MongoClient mongo;

	public ReactiveMongoAutoConfiguration(MongoProperties properties,
			ObjectProvider<MongoClientSettings> settings, Environment environment) {
		this.properties = properties;
		this.settings = settings.getIfAvailable();
		this.environment = environment;
		this.factory = new ReactiveMongoClientFactory(properties);
	}

	@PreDestroy
	public void close() {
		if (this.mongo != null) {
			this.mongo.close();
		}
	}

	@Bean
	@ConditionalOnMissingBean
	public MongoClient reactiveStreamsMongoClient() {
		this.mongo = this.factory.createMongoClient(this.settings, this.environment);
		return this.mongo;
	}

}
