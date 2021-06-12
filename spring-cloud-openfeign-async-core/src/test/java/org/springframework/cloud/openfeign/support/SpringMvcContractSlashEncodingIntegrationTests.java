/*
 * Copyright 2013-2021 the original author or authors.
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

package org.springframework.cloud.openfeign.support;

import feign.Response;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link SpringMvcContract}.
 *
 * @author Ram Anaswara
 */
@SpringBootTest(classes = SpringMvcContractSlashEncodingIntegrationTests.Config.class,
		webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = { "feign.client.decodeSlash=false" })
public class SpringMvcContractSlashEncodingIntegrationTests extends AbstractSpringMvcContractIntegrationTests {

	@Autowired
	private TestClient client;

	@Test
	public void feignClientShouldNotDecodeEncodedSlash() {
		Response response = (Response) client.getMessage("https://www.google.com");

		String urlQueryParam = getUrlQueryParam(response);
		assertThat(urlQueryParam).isEqualTo("https%3A%2F%2Fwww.google.com");
	}

}
