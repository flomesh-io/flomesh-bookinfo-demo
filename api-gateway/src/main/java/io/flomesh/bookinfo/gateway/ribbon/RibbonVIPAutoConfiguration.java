package io.flomesh.bookinfo.gateway.ribbon;

import org.springframework.cloud.netflix.ribbon.RibbonClients;

@RibbonClients(defaultConfiguration = RibbonVIPConfiguration.class)
public class RibbonVIPAutoConfiguration {
}