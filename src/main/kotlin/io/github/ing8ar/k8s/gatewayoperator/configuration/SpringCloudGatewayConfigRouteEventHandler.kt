package io.github.ing8ar.k8s.gatewayoperator.configuration

import io.fabric8.kubernetes.client.informers.ResourceEventHandler
import io.github.ing8ar.k8s.gatewayoperator.crd.SpringCloudGatewayRouteConfig
import mu.KotlinLogging
import org.springframework.cloud.gateway.event.RefreshRoutesEvent
import org.springframework.cloud.gateway.route.RouteDefinitionRepository
import org.springframework.context.ApplicationEventPublisher
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux

private val logger = KotlinLogging.logger {}

class SpringCloudGatewayConfigRouteEventHandler(
    private val routeDefinitionRepository: RouteDefinitionRepository,
    private val applicationEventPublisher: ApplicationEventPublisher
) : ResourceEventHandler<SpringCloudGatewayRouteConfig> {

    override fun onAdd(route: SpringCloudGatewayRouteConfig) {
        route.spec.routes.toFlux()
            .flatMap {
                logger.info { "Add route ${it.id}: $it" }
                routeDefinitionRepository.save(Mono.just(it.toRouteDefinition()))
                    .then(Mono.just(it.id))
            }
            .hasElements()
            .subscribe {
                if (it) {
                    applicationEventPublisher.publishEvent(RefreshRoutesEvent(this))
                    logger.info { "Add success: $route" }
                }
            }
    }

    override fun onUpdate(
        oldRoute: SpringCloudGatewayRouteConfig,
        newRoute: SpringCloudGatewayRouteConfig
    ) {
        val routesMap = newRoute.spec.routes.associateBy { route -> route.id }
        val routesConfig = routeDefinitionRepository.routeDefinitions
            .collectMap { it.id }
            .block()

        routesMap.entries.toFlux()
            .flatMap { entry ->
                routesConfig?.get(entry.key)?.let { r ->
                    val rd = entry.value.toRouteDefinition()
                    if (r != rd) {
                        logger.info { "Update ${entry.key}: ${entry.value}" }
                        routeDefinitionRepository.save(Mono.just(rd))
                            .then(Mono.just(entry.key))
                    } else {
                        Mono.empty()
                    }
                } ?: routeDefinitionRepository.save(Mono.just(entry.value.toRouteDefinition()))
                    .then(Mono.just(entry.key))
                    .also {
                        logger.info { "Add on update ${entry.key}: ${entry.value}" }
                    }
            }
            .hasElements()
            .subscribe {
                if (it) {
                    applicationEventPublisher.publishEvent(RefreshRoutesEvent(this))
                    logger.info { "Update success: $newRoute" }
                }
            }
    }

    override fun onDelete(route: SpringCloudGatewayRouteConfig, deletedFinalStateUnknown: Boolean) {
        if (!deletedFinalStateUnknown) {
            route.spec.routes.toFlux()
                .flatMap {
                    logger.info { "Delete route ${it.id}" }
                    routeDefinitionRepository.delete(Mono.just(it.id))
                        .then(Mono.just(it.id))
                }
                .hasElements()
                .subscribe {
                    if (it) {
                        applicationEventPublisher.publishEvent(RefreshRoutesEvent(this))
                        logger.info { "Delete success: $route" }
                    }
                }
        }
    }
}