package io.github.ing8ar.k8s.gatewayoperator.crd

import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.fabric8.kubernetes.api.model.KubernetesResource

@JsonDeserialize(using = JsonDeserializer.None::class)
data class SpringCloudGatewayRouteConfigStatus(
    var status: String
) : KubernetesResource
