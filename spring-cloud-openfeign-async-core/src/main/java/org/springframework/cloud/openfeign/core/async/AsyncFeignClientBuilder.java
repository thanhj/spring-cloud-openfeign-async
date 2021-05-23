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

import org.springframework.cloud.openfeign.core.AsyncFeignClient;
import org.springframework.context.ApplicationContext;

/**
 * A builder for creating Feign clients without using the {@link AsyncFeignClient} annotation.
 * <p>
 * This builder builds the Feign client exactly like it would be created by using the
 * {@link AsyncFeignClient} annotation.
 *
 * @author thanh.nguyen-ky
 */
public class AsyncFeignClientBuilder {
	private ApplicationContext applicationContext;

	public AsyncFeignClientBuilder(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public <T> Builder<T> forType(Class<T> type, String name) {
		return new Builder<>(this.applicationContext, type, name);
	}

	public <T> Builder<T> forType(final Class<T> type, final AsyncFeignClientFactoryBean asyncClientFactoryBean, final String name) {
		return new Builder<>(this.applicationContext, asyncClientFactoryBean, type, name);
	}

	public static class Builder<T> {
		private AsyncFeignClientFactoryBean asyncFeignClientFactoryBean;

		public Builder(ApplicationContext applicationContext, Class<T> type, String name) {
			this(applicationContext, new AsyncFeignClientFactoryBean(), type, name);
		}

		public Builder(final ApplicationContext applicationContext, AsyncFeignClientFactoryBean asyncFeignClientFactoryBean, Class<T> type, String name) {
			this.asyncFeignClientFactoryBean = asyncFeignClientFactoryBean;
			this.asyncFeignClientFactoryBean.setApplicationContext(applicationContext);
			this.asyncFeignClientFactoryBean.setName(name);
			this.asyncFeignClientFactoryBean.setType(type);
			this.url("").path("");
		}

		public Builder<T> url(final String url) {
			this.asyncFeignClientFactoryBean.setUrl(url);
			return this;
		}

		public Builder<T> path(final String path) {
			this.asyncFeignClientFactoryBean.setPath(path);
			return this;
		}

		/**
		 * @return the created AsyncFeign client
		 */
		public T build() {
			return this.asyncFeignClientFactoryBean.getTarget();
		}
	}
}
