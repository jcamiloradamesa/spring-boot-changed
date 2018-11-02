/*
 * Copyright 2012-2013 the original author or authors.
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
package org.springframework.bootstrap.actuate.autoconfigure;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Convenient collector for all the management endpoints (stuff that goes in the
 * management context whether it is a child context or not).
 * 
 * @author Dave Syer
 * 
 */
@Configuration
@ConditionalOnManagementContext
@Import({ MetricsConfiguration.class, HealthConfiguration.class,
		ShutdownConfiguration.class, TraceConfiguration.class })
public class ManagementEndpointsRegistration {

}