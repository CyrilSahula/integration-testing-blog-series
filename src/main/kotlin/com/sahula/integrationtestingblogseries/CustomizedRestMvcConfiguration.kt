package com.sahula.integrationtestingblogseries

import org.springframework.context.annotation.Import
import org.springframework.data.rest.core.config.RepositoryRestConfiguration
import org.springframework.data.rest.core.mapping.RepositoryDetectionStrategy
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration
import org.springframework.stereotype.Component
import javax.persistence.EntityManager

@Component
@Import(RepositoryRestMvcConfiguration::class)
class CustomizedRestMvcConfiguration : RepositoryRestConfigurer {

    override fun configureRepositoryRestConfiguration(config: RepositoryRestConfiguration) {
        config.repositoryDetectionStrategy = RepositoryDetectionStrategy.RepositoryDetectionStrategies.ANNOTATED;
        config.isReturnBodyOnCreate = true
        config.setBasePath("/api")
    }
}