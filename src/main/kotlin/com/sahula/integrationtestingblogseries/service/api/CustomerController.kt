package com.sahula.integrationtestingblogseries.service.api

import com.sahula.integrationtestingblogseries.service.exeption.NotFoundException
import com.sahula.integrationtestingblogseries.service.persistency.Customer
import com.sahula.integrationtestingblogseries.service.persistency.CustomerRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/customers")
class AddressNormalizationController(
        private val customerRepository: CustomerRepository
) {

    @GetMapping
    fun getCustomers(): List<Customer> {
        return customerRepository.findAll().toList()
    }

    @GetMapping("/{id}")
    fun getCustomer(@PathVariable id: Long): Customer {
        return customerRepository.findByIdOrNull(id).let { it }
                ?: run { throw NotFoundException(id, Customer::class) }
    }
}