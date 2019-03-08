/*
 * Copyright 2012-2014 the original author or authors.
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

package sample.ws.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

@Service
public class StubHumanResourceService implements HumanResourceService {

	private final Logger logger = LoggerFactory.getLogger(StubHumanResourceService.class);

	@Override
	public void bookHoliday(Date startDate, Date endDate, String name) {
		this.logger.info("Booking holiday for [{} - {}] for [{}] ", startDate, endDate,
				name);
	}

}
