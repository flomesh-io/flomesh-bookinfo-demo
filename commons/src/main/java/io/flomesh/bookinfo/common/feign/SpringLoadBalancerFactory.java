/*
 * Copyright 2013-2020 the original author or authors.
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

package io.flomesh.bookinfo.common.feign;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.ILoadBalancer;
import io.flomesh.bookinfo.common.feign.loadbalancer.FeignVIPLoadBalancer;
import org.springframework.cloud.client.loadbalancer.LoadBalancedRetryFactory;
import org.springframework.cloud.netflix.ribbon.ServerIntrospector;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.cloud.openfeign.ribbon.CachingSpringLoadBalancerFactory;
import org.springframework.cloud.openfeign.ribbon.FeignLoadBalancer;
import org.springframework.cloud.openfeign.ribbon.RetryableFeignLoadBalancer;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.util.Map;

/**
 * Factory for SpringLoadBalancer instances that caches the entries created.
 *
 * @author Spencer Gibb
 * @author Dave Syer
 * @author Ryan Baxter
 * @author Gang Li
 */
public class SpringLoadBalancerFactory extends CachingSpringLoadBalancerFactory {
    protected final SpringClientFactory factory;
    private final boolean preferVIPAddress;

    protected LoadBalancedRetryFactory loadBalancedRetryFactory = null;

    private volatile Map<String, FeignLoadBalancer> cache = new ConcurrentReferenceHashMap<>();

    public SpringLoadBalancerFactory(SpringClientFactory factory, boolean preferVIPAddress) {
        super(factory);
        this.factory = factory;
        this.preferVIPAddress = preferVIPAddress;
    }

    public SpringLoadBalancerFactory(SpringClientFactory factory,
									 LoadBalancedRetryFactory loadBalancedRetryPolicyFactory, boolean preferVIPAddress) {
        super(factory, loadBalancedRetryPolicyFactory);
        this.factory = factory;
        this.loadBalancedRetryFactory = loadBalancedRetryPolicyFactory;
        this.preferVIPAddress = preferVIPAddress;
    }

    public FeignLoadBalancer create(String clientName) {
        FeignLoadBalancer client = this.cache.get(clientName);
        if (client != null) {
            return client;
        }
        IClientConfig config = this.factory.getClientConfig(clientName);
        ILoadBalancer lb = this.factory.getLoadBalancer(clientName);
        ServerIntrospector serverIntrospector = this.factory.getInstance(clientName,
                ServerIntrospector.class);
        client = this.loadBalancedRetryFactory != null
                ? new RetryableFeignLoadBalancer(lb, config, serverIntrospector,
                this.loadBalancedRetryFactory)
                : new FeignVIPLoadBalancer(lb, config, serverIntrospector, preferVIPAddress);
        this.cache.put(clientName, client);
        return client;
    }
}
