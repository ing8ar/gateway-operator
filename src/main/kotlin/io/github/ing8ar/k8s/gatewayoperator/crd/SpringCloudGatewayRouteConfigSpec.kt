package io.github.ing8ar.k8s.gatewayoperator.crd

import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.fabric8.kubernetes.api.model.KubernetesResource
import org.springframework.cloud.gateway.filter.FilterDefinition
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition
import org.springframework.cloud.gateway.route.RouteDefinition
import java.net.URI

@JsonDeserialize(using = JsonDeserializer.None::class)
data class SpringCloudGatewayRouteConfigSpec(
    val routes: List<Route>
) : KubernetesResource {
    data class Route(
        val id: String,
        val uri: String,
        val predicates: List<String>?,
        val filters: List<String>?
    ) {
        fun toRouteDefinition(): RouteDefinition =
            RouteDefinition().apply {
                this.id = this@Route.id
                this.uri = URI(this@Route.uri)
                this.filters = this@Route.filters?.map { filter -> FilterDefinition(filter) } ?: emptyList()
                this.predicates =
                    this@Route.predicates?.map { predicate -> PredicateDefinition(predicate) } ?: emptyList()
            }
    }
}
