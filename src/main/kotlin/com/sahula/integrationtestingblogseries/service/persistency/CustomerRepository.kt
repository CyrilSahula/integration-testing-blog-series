package com.sahula.integrationtestingblogseries.service.persistency

import org.springframework.data.repository.CrudRepository

interface CustomerRepository : CrudRepository<Customer, Long> {

    fun findCustomerByIdentificationNumber(identificationNumber: String) : Customer
}