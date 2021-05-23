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

package org.springframework.cloud.openfeign.core.async;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.ClosedFileSystemException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.cloud.openfeign.FeignContext;
import org.springframework.cloud.openfeign.core.AsyncFeignClient;
import org.springframework.cloud.openfeign.core.async.AsyncFeignClientBuilder.Builder;
import org.springframework.cloud.openfeign.core.async.testclient.TestClient;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ReflectionUtils;


/**
 * @author thanh.nguyen-ky
 */
class AsyncFeignClientBuilderTest {

	private ApplicationContext applicationContext;
	private AsyncFeignClientBuilder asyncFeignClientBuilder;

	@BeforeEach
	void setUp() {
		this.applicationContext = Mockito.mock(ApplicationContext.class);
		this.asyncFeignClientBuilder = new AsyncFeignClientBuilder(this.applicationContext);
	}

	@Test
	void safetyCheckForNewFieldsOnTheFeignClientAnnotation() {
		final List<String> methodNames = new ArrayList<>();
		for (final Method method : AsyncFeignClient.class.getMethods()) {
			methodNames.add(method.getName());
		}
		methodNames.removeAll(Arrays.asList("annotationType", "value", "serviceId", "equals", "hashCode", "toString"));
		Collections.sort(methodNames);
		// If this safety check fails the Builder has to be updated.
		// (1) Either a field was removed from the FeignClient annotation and so it has to
		// be removed
		// on this builder class.
		// (2) Or a new field was added and the builder class has to be extended with this
		// new field.
		assertThat(methodNames).containsExactly("name", "path", "url");
	}

	@Test
	public void forType_preInitializedBuilder() {
		// when:
		final Builder builder = this.asyncFeignClientBuilder.forType(TestFeignClient.class, "TestClient");

		// then:
		assertFactoryBeanField(builder, "applicationContext", this.applicationContext);
		assertFactoryBeanField(builder, "type", TestFeignClient.class);
		assertFactoryBeanField(builder, "name", "TestClient");

		// and:
		assertFactoryBeanField(builder, "url", getDefaultValueFromFeignClientAnnotation("url"));
		assertFactoryBeanField(builder, "path", getDefaultValueFromFeignClientAnnotation("path"));
	}

	@Test
	public void forType_allFieldsSetOnBuilder() {
		// when:
		final Builder builder = this.asyncFeignClientBuilder.forType(TestFeignClient.class, "TestClient")
			.url("Url/").path("/Path");

		// then:
		assertFactoryBeanField(builder, "applicationContext", this.applicationContext);
		assertFactoryBeanField(builder, "type", TestFeignClient.class);
		assertFactoryBeanField(builder, "name", "TestClient");

		// and:
		assertFactoryBeanField(builder, "url", "Url/");
		assertFactoryBeanField(builder, "path", "/Path");
	}

	@Test
	public void forType_clientFactoryBeanProvided() {
		// when:
		final Builder builder = this.asyncFeignClientBuilder
			.forType(TestFeignClient.class, new AsyncFeignClientFactoryBean(), "TestClient")
			.path("Path/").url("Url/");

		// then:
		assertFactoryBeanField(builder, "applicationContext", this.applicationContext);
		assertFactoryBeanField(builder, "type", TestFeignClient.class);
		assertFactoryBeanField(builder, "name", "TestClient");

		// and:
		assertFactoryBeanField(builder, "url", "Url/");
		assertFactoryBeanField(builder, "path", "Path/");
	}

	@Test
	public void forType_build() {
		// given:
		// throw an unusual exception in the FeignClientFactoryBean
		Mockito.when(this.applicationContext.getBean(FeignContext.class)).thenThrow(new ClosedFileSystemException());

		// Then
		final AsyncFeignClientBuilder.Builder builder = this.asyncFeignClientBuilder.forType(TestClient.class, "TestClient");
		assertThatThrownBy(() -> builder.build())
			.isInstanceOf(ClosedFileSystemException.class);
	}

	private static Object getDefaultValueFromFeignClientAnnotation(final String methodName) {
		final Method method = ReflectionUtils.findMethod(AsyncFeignClient.class, methodName);
		assertThat(method).isNotNull();
		return method.getDefaultValue();
	}

	private static void assertFactoryBeanField(final Builder builder, final String fieldName,
		final Object expectedValue) {
		final Object value = getFactoryBeanField(builder, fieldName);
		assertThat(value).as("Expected value for the field '" + fieldName + "':").isEqualTo(expectedValue);
	}

	@SuppressWarnings("unchecked")
	private static <T> T getFactoryBeanField(final Builder builder, final String fieldName) {
		final Field factoryBeanField = ReflectionUtils.findField(Builder.class,
			"asyncFeignClientFactoryBean");
		assertThat(factoryBeanField).isNotNull();
		ReflectionUtils.makeAccessible(factoryBeanField);
		final AsyncFeignClientFactoryBean factoryBean = (AsyncFeignClientFactoryBean) ReflectionUtils.getField(factoryBeanField,
			builder);

		final Field field = ReflectionUtils.findField(AsyncFeignClientFactoryBean.class, fieldName);
		assertThat(field).isNotNull();
		ReflectionUtils.makeAccessible(field);
		return (T) ReflectionUtils.getField(field, factoryBean);
	}

	private interface TestFeignClient {
	}
}
