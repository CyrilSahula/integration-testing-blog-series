package blog.devhut.integrationtestingblogseries.api

import blog.devhut.integrationtestingblogseries.server.exception.NotFoundException
import blog.devhut.integrationtestingblogseries.server.persistency.Customer
import blog.devhut.integrationtestingblogseries.server.persistency.CustomerRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional
import javax.validation.Valid

@RestController
@RequestMapping("api/customers")
class CustomerController(
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    fun createCustomer(@Valid @RequestBody customer: Customer) : Customer {
        return customerRepository.save(customer)
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateCustomer(@PathVariable id: Long, @Valid @RequestBody customer: Customer) {
        customerRepository.findByIdOrNull(id)
                ?: run { throw NotFoundException(id, Customer::class) }

        customerRepository.save(customer)
    }
}