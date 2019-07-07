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

package org.springframework.boot.actuate.autoconfigure.metrics.export.datadog;

import io.micrometer.core.instrument.Clock;
import io.micrometer.datadog.DatadogConfig;
import io.micrometer.datadog.DatadogMeterRegistry;

import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.export.simple.SimpleMetricsExportAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for exporting metrics to Datadog.
 *
 * @author Jon Schneider
 * @since 2.0.0
 */
@Configuration
@AutoConfigureBefore({ MetricsAutoConfiguration.class,
		SimpleMetricsExportAutoConfiguration.class })
@ConditionalOnClass(DatadogMeterRegistry.class)
@ConditionalOnProperty("management.metrics.export.datadog.api-key")
@EnableConfigurationProperties(DatadogProperties.class)
public class DatadogMetricsExportAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public DatadogConfig datadogConfig(DatadogProperties datadogProperties) {
		return new DatadogPropertiesConfigAdapter(datadogProperties);
	}

	@Bean
	@ConditionalOnMissingBean
	public DatadogMeterRegistry datadogMeterRegistry(DatadogConfig datadogConfig,
			Clock clock) {
		return new DatadogMeterRegistry(datadogConfig, clock);
	}

}
