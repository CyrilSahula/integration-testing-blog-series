package com.sahula.integrationtestingblogseries.service.persistency

import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource
interface CustomerRepository : CrudRepository<Customer, Long>