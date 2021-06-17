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

import java.lang.reflect.Field;

import feign.AsyncFeign.AsyncBuilder;
import feign.Feign;
import feign.Logger;
import org.junit.Test;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.util.ReflectionUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Matt King
 * @author Sam Kruglov
 */
public class FeignBuilderCustomizerTests {

	@Test
	public void testBuilderCustomizer() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				FeignBuilderCustomizerTests.SampleConfiguration2.class);

		FeignClientFactoryBean clientFactoryBean = context.getBean(FeignClientFactoryBean.class);
		FeignContext feignContext = context.getBean(FeignContext.class);

		Feign.Builder builder = clientFactoryBean.feign(feignContext);
		assertFeignBuilderField(builder, "logLevel", Logger.Level.HEADERS);
		assertFeignBuilderField(builder, "decode404", true);

		context.close();
	}

	@Test
	public void testAsyncBuilderCustomizer() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				FeignBuilderCustomizerTests.SampleAsyncConfiguration2.class);

		FeignClientFactoryBean clientFactoryBean = context.getBean(FeignClientFactoryBean.class);
		FeignContext feignContext = context.getBean(FeignContext.class);

		AsyncBuilder asyncBuilder = clientFactoryBean.asyncFeign(feignContext);
		Feign.Builder builder = getNestedBuilder(asyncBuilder);
		assertFeignBuilderField(builder, "logLevel", Logger.Level.HEADERS);
		assertAsyncFeignBuilderField(asyncBuilder, "decode404", true);

		context.close();
	}

	@Test
	public void testBuildCustomizerOrdered() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				FeignBuilderCustomizerTests.SampleConfiguration3.class);

		FeignClientFactoryBean clientFactoryBean = context.getBean(FeignClientFactoryBean.class);
		FeignContext feignContext = context.getBean(FeignContext.class);

		Feign.Builder builder = clientFactoryBean.feign(feignContext);
		assertFeignBuilderField(builder, "logLevel", Logger.Level.FULL);
		assertFeignBuilderField(builder, "decode404", true);

		context.close();
	}

	@Test
	public void testBuildAsyncCustomizerOrdered() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				FeignBuilderCustomizerTests.SampleAsyncConfiguration3.class);

		FeignClientFactoryBean clientFactoryBean = context.getBean(FeignClientFactoryBean.class);
		FeignContext feignContext = context.getBean(FeignContext.class);

		AsyncBuilder asyncBuilder = clientFactoryBean.asyncFeign(feignContext);
		Feign.Builder nestedBuilder = getNestedBuilder(asyncBuilder);
		assertFeignBuilderField(nestedBuilder, "logLevel", Logger.Level.FULL);
		assertAsyncFeignBuilderField(asyncBuilder, "decode404", true);

		context.close();
	}

	@Test
	public void testBuildCustomizerOrderedWithAdditional() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				FeignBuilderCustomizerTests.SampleConfiguration3.class);

		FeignClientFactoryBean clientFactoryBean = context.getBean(FeignClientFactoryBean.class);
		clientFactoryBean.addCustomizer(builder -> builder.logLevel(Logger.Level.BASIC));
		clientFactoryBean.addCustomizer(Feign.Builder::doNotCloseAfterDecode);
		FeignContext feignContext = context.getBean(FeignContext.class);

		Feign.Builder builder = clientFactoryBean.feign(feignContext);
		assertFeignBuilderField(builder, "logLevel", Logger.Level.BASIC);
		assertFeignBuilderField(builder, "decode404", true);
		assertFeignBuilderField(builder, "closeAfterDecode", false);

		context.close();
	}

	@Test
	public void testBuildAsyncCustomizerOrderedWithAdditional() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				FeignBuilderCustomizerTests.SampleAsyncConfiguration3.class);

		FeignClientFactoryBean clientFactoryBean = context.getBean(FeignClientFactoryBean.class);
		clientFactoryBean.addAsyncCustomizer(builder -> builder.logLevel(Logger.Level.BASIC));
		clientFactoryBean.addAsyncCustomizer(AsyncBuilder::doNotCloseAfterDecode);
		FeignContext feignContext = context.getBean(FeignContext.class);

		AsyncBuilder asyncBuilder = clientFactoryBean.asyncFeign(feignContext);
		Feign.Builder nestedBuilder = getNestedBuilder(asyncBuilder);
		assertFeignBuilderField(nestedBuilder, "logLevel", Logger.Level.BASIC);
		assertAsyncFeignBuilderField(asyncBuilder, "decode404", true);
		assertAsyncFeignBuilderField(asyncBuilder, "closeAfterDecode", false);

		context.close();
	}

	private void assertFeignBuilderField(Feign.Builder builder, String fieldName, Object expectedValue) {
		Field builderField = ReflectionUtils.findField(Feign.Builder.class, fieldName);
		ReflectionUtils.makeAccessible(builderField);

		Object value = ReflectionUtils.getField(builderField, builder);
		assertThat(value).as("Expected value for the field '" + fieldName + "':").isEqualTo(expectedValue);
	}

	private void assertAsyncFeignBuilderField(AsyncBuilder builder, String fieldName, Object expectedValue) {
		Field builderField = ReflectionUtils.findField(AsyncBuilder.class, fieldName);
		ReflectionUtils.makeAccessible(builderField);

		Object value = ReflectionUtils.getField(builderField, builder);
		assertThat(value).as("Expected value for the field '" + fieldName + "':").isEqualTo(expectedValue);
	}

	private Feign.Builder getNestedBuilder(AsyncBuilder asyncBuilder) {
		Field builderField = ReflectionUtils.findField(AsyncBuilder.class, "builder");
		ReflectionUtils.makeAccessible(builderField);
		return (Feign.Builder) ReflectionUtils.getField(builderField, asyncBuilder);
	}

	private static FeignClientFactoryBean defaultFeignClientFactoryBean() {
		return defaultFeignClientFactoryBean(false);
	}

	private static FeignClientFactoryBean defaultFeignClientFactoryBean(boolean asynchronous) {
		FeignClientFactoryBean feignClientFactoryBean = new FeignClientFactoryBean();
		feignClientFactoryBean.setContextId("test");
		feignClientFactoryBean.setName("test");
		feignClientFactoryBean.setType(AsyncFeignClientFactoryTests.TestType.class);
		feignClientFactoryBean.setPath("");
		feignClientFactoryBean.setUrl("http://some.absolute.url");
		feignClientFactoryBean.setAsynchronous(asynchronous);
		return feignClientFactoryBean;
	}

	@Configuration(proxyBeanMethods = false)
	@Import(FeignClientsConfiguration.class)
	protected static class SampleConfiguration2 {

		@Bean
		FeignContext feignContext() {
			return new FeignContext();
		}

		@Bean
		FeignClientProperties feignClientProperties() {
			return new FeignClientProperties();
		}

		@Bean
		FeignBuilderCustomizer feignBuilderCustomizer() {
			return builder -> builder.logLevel(Logger.Level.HEADERS);
		}

		@Bean
		FeignBuilderCustomizer feignBuilderCustomizer2() {
			return Feign.Builder::decode404;
		}

		@Bean
		FeignClientFactoryBean feignClientFactoryBean() {
			return defaultFeignClientFactoryBean();
		}

	}

	@Configuration(proxyBeanMethods = false)
	@Import(FeignClientsConfiguration.class)
	protected static class SampleAsyncConfiguration2 {

		@Bean
		FeignContext feignContext() {
			return new FeignContext();
		}

		@Bean
		FeignClientProperties feignClientProperties() {
			return new FeignClientProperties();
		}

		@Bean
		@Order(1)
		AsyncFeignBuilderCustomizer asyncFeignBuilderCustomizer() {
			return builder -> builder.logLevel(Logger.Level.HEADERS);
		}

		@Bean
		AsyncFeignBuilderCustomizer asyncFeignBuilderCustomizer2() {
			return AsyncBuilder::decode404;
		}

		@Bean
		FeignClientFactoryBean feignClientFactoryBean() {
			return defaultFeignClientFactoryBean(true);
		}

	}

	@Configuration(proxyBeanMethods = false)
	@Import(FeignClientsConfiguration.class)
	protected static class SampleConfiguration3 {

		@Bean
		FeignContext feignContext() {
			return new FeignContext();
		}

		@Bean
		FeignClientProperties feignClientProperties() {
			return new FeignClientProperties();
		}

		@Bean
		@Order(1)
		FeignBuilderCustomizer feignBuilderCustomizer() {
			return builder -> builder.logLevel(Logger.Level.HEADERS);
		}

		@Bean
		@Order(2)
		FeignBuilderCustomizer feignBuilderCustomizer1() {
			return builder -> builder.logLevel(Logger.Level.FULL);
		}

		@Bean
		FeignBuilderCustomizer feignBuilderCustomizer2() {
			return Feign.Builder::decode404;
		}

		@Bean
		FeignClientFactoryBean feignClientFactoryBean() {
			return defaultFeignClientFactoryBean();
		}

	}

	@Configuration(proxyBeanMethods = false)
	@Import(FeignClientsConfiguration.class)
	protected static class SampleAsyncConfiguration3 {

		@Bean
		FeignContext feignContext() {
			return new FeignContext();
		}

		@Bean
		FeignClientProperties feignClientProperties() {
			return new FeignClientProperties();
		}

		@Bean
		@Order(1)
		AsyncFeignBuilderCustomizer asyncFeignBuilderCustomizer() {
			return builder -> builder.logLevel(Logger.Level.HEADERS);
		}

		@Bean
		@Order(2)
		AsyncFeignBuilderCustomizer asyncFeignBuilderCustomizer1() {
			return builder -> builder.logLevel(Logger.Level.FULL);
		}

		@Bean
		AsyncFeignBuilderCustomizer asyncFeignBuilderCustomizer2() {
			return AsyncBuilder::decode404;
		}

		@Bean
		FeignClientFactoryBean feignClientFactoryBean() {
			return defaultFeignClientFactoryBean();
		}

	}

}
