package io.flomesh.bookinfo.common.feign;

import com.netflix.loadbalancer.ILoadBalancer;
import feign.Feign;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.loadbalancer.LoadBalancedRetryFactory;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.cloud.openfeign.ribbon.FeignRibbonClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@ConditionalOnClass({ ILoadBalancer.class, Feign.class })
@ConditionalOnProperty(value = "spring.cloud.loadbalancer.ribbon.enabled",
        matchIfMissing = true)
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(FeignRibbonClientAutoConfiguration.class)
public class RibbonClientAutoConfiguration {

    @Value("${DISCOVERY_PREFER_VIP_ADDRESS:false}")
    private boolean preferVIPAddress;

    @Bean
    @Primary
    @ConditionalOnMissingBean
    @ConditionalOnMissingClass("org.springframework.retry.support.RetryTemplate")
    public SpringLoadBalancerFactory fakeLBClientFactory(
            SpringClientFactory factory) {
        return new SpringLoadBalancerFactory(factory, preferVIPAddress);
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean
    @ConditionalOnClass(name = "org.springframework.retry.support.RetryTemplate")
    public SpringLoadBalancerFactory retryabeFakeLBClientFactory(
            SpringClientFactory factory, LoadBalancedRetryFactory retryFactory) {
        return new SpringLoadBalancerFactory(factory, retryFactory, preferVIPAddress);
    }
}
