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

package org.springframework.cloud.openfeign.async;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.async.test.NoSecurityConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Tests for {@link FeignClientsRegistrar}.
 *
 * @author Olga Maciaszek-Sharma
 */
@SpringBootTest(classes = AsyncFeignClientsRegistrarIntegrationTests.QualifiersTestConfig.class)
class AsyncFeignClientsRegistrarIntegrationTests {

	@Autowired
	ConfigurableApplicationContext context;

	@Test
	void shouldUseQualifiersIfPresent() {
		assertThat(context.getBean("qualifier1")).isNotNull();
		assertThat(context.getBean("qualifier2")).isNotNull();
		assertThatExceptionOfType(NoSuchBeanDefinitionException.class).isThrownBy(() -> context.getBean("qualifier3"));
	}

	@Test
	void shouldUseDefaultQualifierIfQualifiersArrayNotPresent() {
		assertThat(context.getBean("emptyQualifiersAsyncFeignClient")).isNotNull();
	}

	@Test
	void shouldUseDefaultQualifierWhenNonePresent() {
		assertThat(context.getBean("noQualifiersAsyncFeignClient")).isNotNull();
	}

	@Test
	void shouldUseDefaultQualifierWhenWhitespaceQualifiers() {
		assertThat(context.getBean("whitespaceQualifiersAsyncFeignClient")).isNotNull();
	}

	@Test
	void shouldUseDefaultQualifierWhenEmptyQualifiers() {
		assertThat(context.getBean("emptyQualifiersAsyncFeignClient")).isNotNull();
	}

	@AsyncFeignClient(name = "qualifiersClient", qualifiers = { "qualifier1", "qualifier2" })
	protected interface QualifiersClient {

	}

	@AsyncFeignClient(name = "noQualifiers")
	protected interface NoQualifiersClient {

	}

	@AsyncFeignClient(name = "emptyQualifiers", qualifiers = {})
	protected interface EmptyQualifiersClient {

	}

	@AsyncFeignClient(name = "whitespaceQualifiers", qualifiers = { " " })
	protected interface WhitespaceQualifiersClient {

	}

	@Configuration(proxyBeanMethods = false)
	@EnableAutoConfiguration
	@Import(NoSecurityConfiguration.class)
	@EnableAsyncFeignClients(clients = { QualifiersClient.class, NoQualifiersClient.class, EmptyQualifiersClient.class,
			WhitespaceQualifiersClient.class })
	protected static class QualifiersTestConfig {

	}

}
