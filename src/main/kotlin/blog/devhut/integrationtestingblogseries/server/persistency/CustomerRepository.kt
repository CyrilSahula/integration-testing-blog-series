package blog.devhut.integrationtestingblogseries.server.persistency

import org.springframework.data.jpa.repository.JpaRepository

interface CustomerRepository : JpaRepository<Customer, Long> {
    fun findCustomerByIdentificationNumber(identificationNumber: String) : Customer?
}