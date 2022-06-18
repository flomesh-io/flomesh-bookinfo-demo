package io.flomesh.bookinfo.gateway.ribbon;

import com.netflix.client.ClientException;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.Server;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.cloud.netflix.ribbon.ServerIntrospector;
import org.springframework.cloud.netflix.ribbon.apache.RibbonLoadBalancingHttpClient;

import java.net.URI;

public class RibbonVIPLoadBalancingHttpClient extends RibbonLoadBalancingHttpClient {
    private final boolean preferVIPAddress;

    public RibbonVIPLoadBalancingHttpClient(CloseableHttpClient httpClient, IClientConfig config, ServerIntrospector serverIntrospector, boolean preferVIPAddress) {
        super(config, serverIntrospector);
        this.preferVIPAddress = preferVIPAddress;
    }

    @Override
    public Server getServerFromLoadBalancer(URI original, Object loadBalancerKey) throws ClientException {
        Server server = super.getServerFromLoadBalancer(original, loadBalancerKey);
        if (preferVIPAddress)
            server.setHost(server.getMetaInfo().getServiceIdForDiscovery());
        return server;
    }
}
