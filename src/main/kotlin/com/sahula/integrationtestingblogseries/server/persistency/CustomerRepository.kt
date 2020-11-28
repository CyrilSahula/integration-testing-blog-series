package com.sahula.integrationtestingblogseries.server.persistency

import org.springframework.data.repository.CrudRepository

interface CustomerRepository : CrudRepository<Customer, Long> {

    fun findCustomerByIdentificationNumber(identificationNumber: String) : Customer?
}