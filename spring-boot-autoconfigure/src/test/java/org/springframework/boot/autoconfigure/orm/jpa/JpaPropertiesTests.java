/*
 * Copyright 2012-2016 the original author or authors.
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

package org.springframework.boot.autoconfigure.orm.jpa;

import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

import org.hibernate.cfg.AvailableSettings;
import org.junit.After;
import org.junit.Test;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link JpaProperties}.
 *
 * @author Stephane Nicoll
 */
public class JpaPropertiesTests {

	private AnnotationConfigApplicationContext context;

	@After
	public void close() {
		if (this.context != null) {
			this.context.close();
		}
	}

	@Test
	public void noCustomNamingStrategy() throws Exception {
		JpaProperties properties = load();
		Map<String, String> hibernateProperties = properties
				.getHibernateProperties(mockStandaloneDataSource());
		assertThat(hibernateProperties)
				.doesNotContainKeys("hibernate.ejb.naming_strategy");
		assertThat(hibernateProperties).containsEntry(
				"hibernate.physical_naming_strategy",
				SpringPhysicalNamingStrategy.class.getName());
		assertThat(hibernateProperties).containsEntry(
				"hibernate.implicit_naming_strategy",
				SpringImplicitNamingStrategy.class.getName());
	}

	@Test
	public void hibernate5CustomNamingStrategies() throws Exception {
		JpaProperties properties = load(
				"spring.jpa.hibernate.naming.implicit-strategy:com.example.Implicit",
				"spring.jpa.hibernate.naming.physical-strategy:com.example.Physical");
		Map<String, String> hibernateProperties = properties
				.getHibernateProperties(mockStandaloneDataSource());
		assertThat(hibernateProperties).contains(
				entry("hibernate.implicit_naming_strategy", "com.example.Implicit"),
				entry("hibernate.physical_naming_strategy", "com.example.Physical"));
		assertThat(hibernateProperties)
				.doesNotContainKeys("hibernate.ejb.naming_strategy");
	}

	@Test
	public void hibernate5CustomNamingStrategiesViaJpaProperties() throws Exception {
		JpaProperties properties = load(
				"spring.jpa.properties.hibernate.implicit_naming_strategy:com.example.Implicit",
				"spring.jpa.properties.hibernate.physical_naming_strategy:com.example.Physical");
		Map<String, String> hibernateProperties = properties
				.getHibernateProperties(mockStandaloneDataSource());
		// You can override them as we don't provide any default
		assertThat(hibernateProperties).contains(
				entry("hibernate.implicit_naming_strategy", "com.example.Implicit"),
				entry("hibernate.physical_naming_strategy", "com.example.Physical"));
		assertThat(hibernateProperties)
				.doesNotContainKeys("hibernate.ejb.naming_strategy");
	}

	@Test
	public void useNewIdGeneratorMappingsDefault() throws Exception {
		JpaProperties properties = load();
		Map<String, String> hibernateProperties = properties
				.getHibernateProperties(mockStandaloneDataSource());
		assertThat(hibernateProperties)
				.containsEntry(AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS, "false");
	}

	@Test
	public void useNewIdGeneratorMappingsTrue() throws Exception {
		JpaProperties properties = load(
				"spring.jpa.hibernate.use-new-id-generator-mappings:true");
		Map<String, String> hibernateProperties = properties
				.getHibernateProperties(mockStandaloneDataSource());
		assertThat(hibernateProperties)
				.containsEntry(AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS, "true");
	}

	@SuppressWarnings("unchecked")
	private DataSource mockStandaloneDataSource() throws SQLException {
		DataSource ds = mock(DataSource.class);
		given(ds.getConnection()).willThrow(SQLException.class);
		return ds;
	}

	private JpaProperties load(String... environment) {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		EnvironmentTestUtils.addEnvironment(ctx, environment);
		ctx.register(TestConfiguration.class);
		ctx.refresh();
		this.context = ctx;
		return this.context.getBean(JpaProperties.class);
	}

	@Configuration
	@EnableConfigurationProperties(JpaProperties.class)
	static class TestConfiguration {

	}

}
