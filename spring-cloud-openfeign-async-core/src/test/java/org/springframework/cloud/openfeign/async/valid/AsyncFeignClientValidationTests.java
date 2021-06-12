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

package org.springframework.cloud.openfeign.async.valid;

import org.junit.Test;

import org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration;
import org.springframework.cloud.commons.httpclient.HttpClientConfiguration;
import org.springframework.cloud.openfeign.async.AsyncFeignClient;
import org.springframework.cloud.openfeign.async.EnableAsyncFeignClients;
import org.springframework.cloud.openfeign.async.FeignAutoConfiguration;
import org.springframework.cloud.openfeign.async.loadbalancer.FeignLoadBalancerAutoConfiguration;
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

	@Test
	public void validNotLoadBalanced() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(GoodUrlConfiguration.class);
		assertThat(context.getBean(GoodUrlConfiguration.Client.class)).isNotNull();
		context.close();
	}

	@Test
	public void validPlaceholder() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				PlaceholderUrlConfiguration.class);
		assertThat(context.getBean(PlaceholderUrlConfiguration.Client.class)).isNotNull();
		context.close();
	}

	@Test
	public void validLoadBalanced() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				LoadBalancerAutoConfiguration.class,
				org.springframework.cloud.loadbalancer.config.LoadBalancerAutoConfiguration.class,
				FeignLoadBalancerAutoConfiguration.class, GoodServiceIdConfiguration.class);
		assertThat(context.getBean(GoodServiceIdConfiguration.Client.class)).isNotNull();
		context.close();
	}

	@Configuration(proxyBeanMethods = false)
	@Import({ FeignAutoConfiguration.class, HttpClientConfiguration.class })
	@EnableAsyncFeignClients(clients = GoodUrlConfiguration.Client.class)
	protected static class GoodUrlConfiguration {

		@AsyncFeignClient(name = "example", url = "https://example.com")
		interface Client {

			@RequestMapping(method = RequestMethod.GET, value = "/")
			@Deprecated
			String get();

		}

	}

	@Configuration(proxyBeanMethods = false)
	@Import({ FeignAutoConfiguration.class, HttpClientConfiguration.class })
	@EnableAsyncFeignClients(clients = PlaceholderUrlConfiguration.Client.class)
	protected static class PlaceholderUrlConfiguration {

		@AsyncFeignClient(name = "example", url = "${feignClient.url:https://example.com}")
		interface Client {

			@RequestMapping(method = RequestMethod.GET, value = "/")
			@Deprecated
			String get();

		}

	}

	@Configuration(proxyBeanMethods = false)
	@Import({ FeignAutoConfiguration.class, HttpClientConfiguration.class })
	@EnableAsyncFeignClients(clients = GoodServiceIdConfiguration.Client.class)
	protected static class GoodServiceIdConfiguration {

		@AsyncFeignClient("foo")
		interface Client {

			@RequestMapping(method = RequestMethod.GET, value = "/")
			@Deprecated
			String get();

		}

	}

}
