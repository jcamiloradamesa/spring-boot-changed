/*
 * Copyright 2012-2015 the original author or authors.
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

package org.springframework.boot.configurationmetadata;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link DescriptionExtractor}.
 *
 * @author Stephane Nicoll
 */
public class DescriptionExtractorTests {

	private DescriptionExtractor extractor = new DescriptionExtractor();

	@Test
	public void extractShortDescription() {
		assertEquals("My short description.",
				this.extractor.getShortDescription("My short description. More stuff."));
	}

	@Test
	public void extractShortDescriptionNewLineBeforeDot() {
		assertEquals("My short description.",
				this.extractor.getShortDescription("My short\ndescription.\nMore stuff."));
	}

	@Test
	public void extractShortDescriptionNewLineBeforeDotWithSpaces() {
		assertEquals("My short description.",
				this.extractor
						.getShortDescription("My short  \n description.  \nMore stuff."));
	}

	@Test
	public void extractShortDescriptionNoDot() {
		assertEquals("My short description",
				this.extractor.getShortDescription("My short description"));
	}

	@Test
	public void extractShortDescriptionNoDotMultipleLines() {
		assertEquals("My short description",
				this.extractor.getShortDescription("My short description  \n More stuff"));
	}

	@Test
	public void extractShortDescriptionNull() {
		assertEquals(null, this.extractor.getShortDescription(null));
	}

}
