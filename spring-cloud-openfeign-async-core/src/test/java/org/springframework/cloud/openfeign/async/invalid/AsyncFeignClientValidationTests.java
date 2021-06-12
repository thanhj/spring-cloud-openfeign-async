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

package org.springframework.cloud.openfeign.async.invalid;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration;
import org.springframework.cloud.commons.httpclient.HttpClientConfiguration;
import org.springframework.cloud.openfeign.async.AsyncFeignClient;
import org.springframework.cloud.openfeign.async.EnableAsyncFeignClients;
import org.springframework.cloud.openfeign.async.FeignAutoConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Dave Syer
 */
public class AsyncFeignClientValidationTests {

	@Rule
	public ExpectedException expected = ExpectedException.none();

	@Test
	public void testServiceIdAndValue() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				LoadBalancerAutoConfiguration.class, NameAndServiceIdConfiguration.class);
		assertThat(context.getBean(NameAndServiceIdConfiguration.Client.class)).isNotNull();
		context.close();
	}

	@Test
	public void testDuplicatedClientNames() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.setAllowBeanDefinitionOverriding(false);
		context.register(LoadBalancerAutoConfiguration.class, DuplicatedFeignClientNamesConfiguration.class);
		context.refresh();
		assertThat(context.getBean(DuplicatedFeignClientNamesConfiguration.FooClient.class)).isNotNull();
		assertThat(context.getBean(DuplicatedFeignClientNamesConfiguration.BarClient.class)).isNotNull();
		context.close();
	}

	@Test
	public void testNotLegalHostname() {
		this.expected.expectMessage("not legal hostname (foo_bar)");
		new AnnotationConfigApplicationContext(BadHostnameConfiguration.class);
	}

	@Configuration(proxyBeanMethods = false)
	@Import({ FeignAutoConfiguration.class, HttpClientConfiguration.class })
	@EnableAsyncFeignClients(clients = NameAndServiceIdConfiguration.Client.class)
	protected static class NameAndServiceIdConfiguration {

		@AsyncFeignClient(name = "bar")
		interface Client {

			@RequestMapping(method = RequestMethod.GET, value = "/")
			String get();

		}

	}

	@Configuration(proxyBeanMethods = false)
	@Import({ FeignAutoConfiguration.class, HttpClientConfiguration.class })
	@EnableAsyncFeignClients(clients = { DuplicatedFeignClientNamesConfiguration.FooClient.class,
			DuplicatedFeignClientNamesConfiguration.BarClient.class })
	protected static class DuplicatedFeignClientNamesConfiguration {

		@AsyncFeignClient(contextId = "foo", name = "bar")
		interface FooClient {

			@RequestMapping(method = RequestMethod.GET, value = "/")
			String get();

		}

		@AsyncFeignClient(name = "bar")
		interface BarClient {

			@RequestMapping(method = RequestMethod.GET, value = "/")
			String get();

		}

	}

	@Configuration(proxyBeanMethods = false)
	@Import(FeignAutoConfiguration.class)
	@EnableAsyncFeignClients(clients = BadHostnameConfiguration.Client.class)
	protected static class BadHostnameConfiguration {

		@AsyncFeignClient("foo_bar")
		interface Client {

			@RequestMapping(method = RequestMethod.GET, value = "/")
			String get();

		}

	}

}
