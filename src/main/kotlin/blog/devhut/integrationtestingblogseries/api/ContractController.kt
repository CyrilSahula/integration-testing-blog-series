package blog.devhut.integrationtestingblogseries.api

import blog.devhut.integrationtestingblogseries.server.exception.NotFoundException
import blog.devhut.integrationtestingblogseries.server.persistency.Contract
import blog.devhut.integrationtestingblogseries.server.persistency.ContractRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("api/contracts")
class ContractController(
        private val contractRepository: ContractRepository
) {

    @GetMapping("/{id}")
    fun getContract(@PathVariable id: Long): Contract {
        return contractRepository.findByIdOrNull(id).let { it }
            ?: run { throw NotFoundException(id, Contract::class) }
    }

    @GetMapping
    fun getContracts(): List<Contract> {
        return contractRepository.findAll().toList()
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createContract(@Valid @RequestBody contract: Contract) : Contract {
        return contractRepository.save(contract)
    }

    @PutMapping("/{id}")
    fun updateContract(@PathVariable id: Long, @Valid @RequestBody contract: Contract) : Contract {
        contractRepository.findByIdOrNull(id)
                ?: run { throw NotFoundException(id, Contract::class) }
        return contractRepository.save(contract)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteContract(@PathVariable id: Long) {
        contractRepository.findByIdOrNull(id)
            ?: run { throw NotFoundException(id, Contract::class) }
        return contractRepository.deleteById(id)
    }
}