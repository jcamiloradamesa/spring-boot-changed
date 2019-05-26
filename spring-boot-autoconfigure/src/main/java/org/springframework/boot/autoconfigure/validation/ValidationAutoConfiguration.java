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

package org.springframework.boot.autoconfigure.validation;

import javax.validation.Validator;
import javax.validation.executable.ExecutableValidator;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

/**
 * {@link EnableAutoConfiguration Auto-configuration} to configure the validation
 * infrastructure.
 *
 * @author Stephane Nicoll
 * @since 1.5.0
 */
@Configuration
@ConditionalOnClass(ExecutableValidator.class)
@ConditionalOnResource(resources = "classpath:META-INF/services/javax.validation.spi.ValidationProvider")
@Import({ DefaultValidatorConfiguration.class,
		Jsr303ValidatorAdapterConfiguration.class })
public class ValidationAutoConfiguration {

	@Bean
	@ConditionalOnBean(Validator.class)
	@ConditionalOnMissingBean
	public static MethodValidationPostProcessor methodValidationPostProcessor(
			Environment environment, Validator validator) {
		MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
		processor.setProxyTargetClass(determineProxyTargetClass(environment));
		processor.setValidator(validator);
		return processor;
	}

	private static boolean determineProxyTargetClass(Environment environment) {
		RelaxedPropertyResolver resolver = new RelaxedPropertyResolver(environment,
				"spring.aop.");
		Boolean value = resolver.getProperty("proxyTargetClass", Boolean.class);
		return (value != null ? value : true);
	}

}
