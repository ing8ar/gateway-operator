package io.github.ing8ar.k8s.gatewayoperator.configuration

import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.fabric8.kubernetes.client.KubernetesClient
import io.fabric8.kubernetes.client.informers.SharedInformer
import io.fabric8.kubernetes.client.utils.Serialization
import io.github.ing8ar.k8s.gatewayoperator.crd.SpringCloudGatewayRouteConfig
import org.springframework.cloud.gateway.route.RouteDefinitionRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RouteInformerConfiguration {
    @Bean(destroyMethod = "stop")
    fun routeInformer(
        client: KubernetesClient,
        routeDefinitionRepository: RouteDefinitionRepository,
        applicationEventPublisher: ApplicationEventPublisher
    ): SharedInformer<SpringCloudGatewayRouteConfig> {
        Serialization.jsonMapper().registerModule(KotlinModule.Builder().build())
        val sharedInformerFactory = client.informers()
        val routeInformer = sharedInformerFactory.sharedIndexInformerFor(
            SpringCloudGatewayRouteConfig::class.java,
            60 * 1000L
        )
        routeInformer.addEventHandler(
            SpringCloudGatewayConfigRouteEventHandler(
                routeDefinitionRepository,
                applicationEventPublisher
            )
        )
        routeInformer.run()
        return routeInformer
    }
}

