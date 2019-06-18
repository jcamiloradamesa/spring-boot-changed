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

package org.springframework.boot.system;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link JavaVersion}.
 *
 * @author Stephane Nicoll
 */
public class JavaVersionTests {

	@Test
	public void currentVersionIsAvailable() {
		assertThat(JavaVersion.getJavaVersion()).isNotNull();
	}

	@Test
	public void java8IsOlderThanJava9() {
		assertThat(JavaVersion.EIGHT.compareTo(JavaVersion.NINE)).isLessThan(0);
	}

	@Test
	public void java9IsNewerThanJava8() {
		assertThat(JavaVersion.NINE.compareTo(JavaVersion.EIGHT)).isGreaterThan(0);
	}

	@Test
	public void comparisonOfSameVersion() {
		assertThat(JavaVersion.EIGHT.compareTo(JavaVersion.EIGHT)).isEqualTo(0);
	}

}
