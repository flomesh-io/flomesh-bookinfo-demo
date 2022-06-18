package io.flomesh.bookinfo.common.feign.loadbalancer;

import com.netflix.client.ClientException;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import org.springframework.cloud.netflix.ribbon.ServerIntrospector;
import org.springframework.cloud.openfeign.ribbon.FeignLoadBalancer;

import javax.annotation.Nullable;
import java.net.URI;

public class FeignVIPLoadBalancer extends FeignLoadBalancer {
    private final boolean preferVIPAddress;

    public FeignVIPLoadBalancer(ILoadBalancer lb, IClientConfig clientConfig, ServerIntrospector serverIntrospector, boolean preferVIPAddress) {
        super(lb, clientConfig, serverIntrospector);
        this.preferVIPAddress = preferVIPAddress;
    }

    @Override
    public Server getServerFromLoadBalancer(@Nullable URI original, @Nullable Object loadBalancerKey) throws ClientException {
        Server server = super.getServerFromLoadBalancer(original, loadBalancerKey);
        if (preferVIPAddress)
            server.setHost(server.getMetaInfo().getServiceIdForDiscovery());
        return server;
    }
}
