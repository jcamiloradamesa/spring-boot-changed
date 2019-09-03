/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sample.data.mongo;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.OutputCapture;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link SampleMongoApplication}.
 *
 * @author Dave Syer
 * @author Andy Wilkinson
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(SampleMongoApplication.class)
@IntegrationTest
public class SampleMongoApplicationTests {

	@ClassRule
	public static OutputCapture outputCapture = new OutputCapture();

	@Test
	public void testDefaultSettings() throws Exception {
		String output = SampleMongoApplicationTests.outputCapture.toString();
		assertTrue("Wrong output: " + output,
				output.contains("firstName='Alice', lastName='Smith'"));
	}

}
