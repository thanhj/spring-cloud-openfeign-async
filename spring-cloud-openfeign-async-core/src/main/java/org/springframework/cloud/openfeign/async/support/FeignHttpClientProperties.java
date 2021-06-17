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

package org.springframework.cloud.openfeign.async.support;

import java.util.concurrent.TimeUnit;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Ryan Baxter
 * @author Nguyen Ky Thanh
 */
@ConfigurationProperties(prefix = "feign.httpclient")
public class FeignHttpClientProperties {

	/**
	 * Default value for disabling SSL validation.
	 */
	public static final boolean DEFAULT_DISABLE_SSL_VALIDATION = false;

	/**
	 * Default value for max number od connections.
	 */
	public static final int DEFAULT_MAX_CONNECTIONS = 200;

	/**
	 * Default value for max number od connections per route.
	 */
	public static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 50;

	/**
	 * Default value for time to live.
	 */
	public static final long DEFAULT_TIME_TO_LIVE = 900L;

	/**
	 * Default time to live unit.
	 */
	public static final TimeUnit DEFAULT_TIME_TO_LIVE_UNIT = TimeUnit.SECONDS;

	/**
	 * Default value for following redirects.
	 */
	public static final boolean DEFAULT_FOLLOW_REDIRECTS = true;

	/**
	 * Default value for connection timeout.
	 */
	public static final int DEFAULT_CONNECTION_TIMEOUT = 2000;

	/**
	 * Default value for connection timer repeat.
	 */
	public static final int DEFAULT_CONNECTION_TIMER_REPEAT = 3000;

	private boolean disableSslValidation = DEFAULT_DISABLE_SSL_VALIDATION;

	private int maxConnections = DEFAULT_MAX_CONNECTIONS;

	private int maxConnectionsPerRoute = DEFAULT_MAX_CONNECTIONS_PER_ROUTE;

	private long timeToLive = DEFAULT_TIME_TO_LIVE;

	private TimeUnit timeToLiveUnit = DEFAULT_TIME_TO_LIVE_UNIT;

	private boolean followRedirects = DEFAULT_FOLLOW_REDIRECTS;

	private int connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;

	private int connectionTimerRepeat = DEFAULT_CONNECTION_TIMER_REPEAT;

	/**
	 * Apache Async HttpClient5 additional properties.
	 */
	private AsyncHc5Properties asyncHc5 = new AsyncHc5Properties();

	public int getConnectionTimerRepeat() {
		return this.connectionTimerRepeat;
	}

	public void setConnectionTimerRepeat(int connectionTimerRepeat) {
		this.connectionTimerRepeat = connectionTimerRepeat;
	}

	public boolean isDisableSslValidation() {
		return this.disableSslValidation;
	}

	public void setDisableSslValidation(boolean disableSslValidation) {
		this.disableSslValidation = disableSslValidation;
	}

	public int getMaxConnections() {
		return this.maxConnections;
	}

	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}

	public int getMaxConnectionsPerRoute() {
		return this.maxConnectionsPerRoute;
	}

	public void setMaxConnectionsPerRoute(int maxConnectionsPerRoute) {
		this.maxConnectionsPerRoute = maxConnectionsPerRoute;
	}

	public long getTimeToLive() {
		return this.timeToLive;
	}

	public void setTimeToLive(long timeToLive) {
		this.timeToLive = timeToLive;
	}

	public TimeUnit getTimeToLiveUnit() {
		return this.timeToLiveUnit;
	}

	public void setTimeToLiveUnit(TimeUnit timeToLiveUnit) {
		this.timeToLiveUnit = timeToLiveUnit;
	}

	public boolean isFollowRedirects() {
		return this.followRedirects;
	}

	public void setFollowRedirects(boolean followRedirects) {
		this.followRedirects = followRedirects;
	}

	public int getConnectionTimeout() {
		return this.connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public AsyncHc5Properties getAsyncHc5() {
		return asyncHc5;
	}

	public void setAsyncHc5(AsyncHc5Properties asyncHc5) {
		this.asyncHc5 = asyncHc5;
	}

	public static class AsyncHc5Properties {

		/**
		 * Default value for pool concurrency policy.
		 */
		public static final PoolConcurrencyPolicy DEFAULT_POOL_CONCURRENCY_POLICY = PoolConcurrencyPolicy.STRICT;

		/**
		 * Default value for pool reuse policy.
		 */
		public static final PoolReusePolicy DEFAULT_POOL_REUSE_POLICY = PoolReusePolicy.FIFO;

		/**
		 * Default value for response timeout.
		 */
		public static final int DEFAULT_RESPONSE_TIMEOUT = 5;

		/**
		 * Default value for response timeout unit.
		 */
		public static final TimeUnit DEFAULT_RESPONSE_TIMEOUT_UNIT = TimeUnit.SECONDS;

		/**
		 * Default HTTP protocol version policy.
		 */
		private static final HttpVersionPolicy DEFAULT_HTTP_VERSION_POLICY = HttpVersionPolicy.FORCE_HTTP_1;

		/**
		 * Pool concurrency policies.
		 */
		private PoolConcurrencyPolicy poolConcurrencyPolicy = DEFAULT_POOL_CONCURRENCY_POLICY;

		/**
		 * Pool connection re-use policies.
		 */
		private PoolReusePolicy poolReusePolicy = DEFAULT_POOL_REUSE_POLICY;

		/**
		 * Determines the timeout until arrival of a response from the opposite endpoint.
		 * A timeout value of zero is interpreted as an infinite timeout. Please note that
		 * response timeout may be unsupported by HTTP transports with message
		 * multiplexing.
		 */
		private int responseTimeout = DEFAULT_RESPONSE_TIMEOUT;

		/**
		 * Default value for response timeout unit.
		 */
		private TimeUnit responseTimeoutUnit = DEFAULT_RESPONSE_TIMEOUT_UNIT;

		/**
		 * HTTP protocol version policy.
		 */
		private HttpVersionPolicy httpVersionPolicy = DEFAULT_HTTP_VERSION_POLICY;

		public PoolConcurrencyPolicy getPoolConcurrencyPolicy() {
			return this.poolConcurrencyPolicy;
		}

		public void setPoolConcurrencyPolicy(PoolConcurrencyPolicy poolConcurrencyPolicy) {
			this.poolConcurrencyPolicy = poolConcurrencyPolicy;
		}

		public PoolReusePolicy getPoolReusePolicy() {
			return poolReusePolicy;
		}

		public void setPoolReusePolicy(PoolReusePolicy poolReusePolicy) {
			this.poolReusePolicy = poolReusePolicy;
		}

		public int getResponseTimeout() {
			return responseTimeout;
		}

		public void setResponseTimeout(int responseTimeout) {
			this.responseTimeout = responseTimeout;
		}

		public TimeUnit getResponseTimeoutUnit() {
			return responseTimeoutUnit;
		}

		public void setResponseTimeoutUnit(TimeUnit responseTimeoutUnit) {
			this.responseTimeoutUnit = responseTimeoutUnit;
		}

		public HttpVersionPolicy getHttpVersionPolicy() {
			return httpVersionPolicy;
		}

		public void setHttpVersionPolicy(HttpVersionPolicy httpVersionPolicy) {
			this.httpVersionPolicy = httpVersionPolicy;
		}

	}

	/**
	 * Enumeration of pool concurrency policies.
	 */
	public enum PoolConcurrencyPolicy {

		/**
		 * Higher concurrency but with lax connection max limit guarantees.
		 */
		LAX,

		/**
		 * Strict connection max limit guarantees.
		 */
		STRICT

	}

	/**
	 * Enumeration of pooled connection re-use policies.
	 */
	public enum PoolReusePolicy {

		/**
		 * Re-use as few connections as possible making it possible for connections to
		 * become idle and expire.
		 */
		LIFO,

		/**
		 * Re-use all connections equally preventing them from becoming idle and expiring.
		 */
		FIFO

	}

	/**
	 * HTTP protocol version policy.
	 */
	public enum HttpVersionPolicy {

		/**
		 * Force to use HTTP v1.
		 */
		FORCE_HTTP_1,

		/**
		 * Force to use HTTP v2.
		 */
		FORCE_HTTP_2,

		/**
		 * Try to use HTTP v2 otherwise fallback to HTTP v1.
		 */
		NEGOTIATE

	}

}
