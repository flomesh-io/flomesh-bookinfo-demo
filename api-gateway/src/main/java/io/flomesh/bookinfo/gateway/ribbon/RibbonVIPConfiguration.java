package io.flomesh.bookinfo.gateway.ribbon;

import com.netflix.client.AbstractLoadBalancerAwareClient;
import com.netflix.client.RetryHandler;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.servo.monitor.Monitors;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.netflix.ribbon.RibbonClientName;
import org.springframework.cloud.netflix.ribbon.ServerIntrospector;
import org.springframework.cloud.netflix.ribbon.apache.RibbonLoadBalancingHttpClient;
import org.springframework.context.annotation.Bean;

//@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(name = "org.apache.http.client.HttpClient")
@ConditionalOnProperty(name = "ribbon.httpclient.enabled", matchIfMissing = true)
public class RibbonVIPConfiguration {

    @RibbonClientName
    private String name = "client";

    @Value("${DISCOVERY_PREFER_VIP_ADDRESS:false}")
    private boolean preferVIPAddress;

    @Bean
    @ConditionalOnMissingBean(AbstractLoadBalancerAwareClient.class)
    @ConditionalOnMissingClass("org.springframework.retry.support.RetryTemplate")
    public RibbonLoadBalancingHttpClient ribbonLoadBalancingHttpClient(
            IClientConfig config, ServerIntrospector serverIntrospector,
            ILoadBalancer loadBalancer, RetryHandler retryHandler,
            CloseableHttpClient httpClient) {
        RibbonLoadBalancingHttpClient client = new RibbonVIPLoadBalancingHttpClient(
                httpClient, config, serverIntrospector, preferVIPAddress);
        client.setLoadBalancer(loadBalancer);
        client.setRetryHandler(retryHandler);
        Monitors.registerObject("Client_" + this.name, client);
        return client;
    }
}
