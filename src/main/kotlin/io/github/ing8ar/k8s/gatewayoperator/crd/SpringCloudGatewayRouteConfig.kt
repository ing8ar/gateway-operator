package io.github.ing8ar.k8s.gatewayoperator.crd

import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Version
import io.github.ing8ar.k8s.gatewayoperator.crd.SpringCloudGatewayRouteConfig.Companion.GROUP
import io.github.ing8ar.k8s.gatewayoperator.crd.SpringCloudGatewayRouteConfig.Companion.VERSION

@Group(GROUP)
@Version(VERSION)
class SpringCloudGatewayRouteConfig : CustomResource<SpringCloudGatewayRouteConfigSpec,
    SpringCloudGatewayRouteConfigStatus>(), Namespaced {
    companion object {
        const val GROUP = "k8s.jpoint.ru"
        const val VERSION = "v1"
    }
}