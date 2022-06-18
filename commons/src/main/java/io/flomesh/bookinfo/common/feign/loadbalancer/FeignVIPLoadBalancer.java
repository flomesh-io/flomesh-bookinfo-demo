package io.flomesh.bookinfo.common.feign.loadbalancer;

import com.netflix.client.ClientException;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import org.springframework.cloud.netflix.ribbon.ServerIntrospector;
import org.springframework.cloud.openfeign.ribbon.FeignLoadBalancer;

import javax.annotation.Nullable;
import java.net.URI;
import java.util.Map;

public class FeignVIPLoadBalancer extends FeignLoadBalancer {
    private final boolean preferVIPAddress;

    public FeignVIPLoadBalancer(ILoadBalancer lb, IClientConfig clientConfig, ServerIntrospector serverIntrospector, boolean preferVIPAddress) {
        super(lb, clientConfig, serverIntrospector);
        this.preferVIPAddress = preferVIPAddress;
    }

    @Override
    public Server getServerFromLoadBalancer(@Nullable URI original, @Nullable Object loadBalancerKey) throws ClientException {
        Server server = super.getServerFromLoadBalancer(original, loadBalancerKey);
        if (preferVIPAddress && server instanceof DiscoveryEnabledServer) {
            DiscoveryEnabledServer discoveryEnabledServer = (DiscoveryEnabledServer) server;
            Map<String, String> metadata = discoveryEnabledServer.getInstanceInfo().getMetadata();
            String fqdn = metadata.get("fqdn");
            if (fqdn != null && fqdn.length() > 0) {
                server.setHost(fqdn);
            }
        }
        return server;
    }
}
