package blog.devhut.integrationtestingblogseries.manualcleaning

import com.fasterxml.jackson.databind.ObjectMapper
import blog.devhut.integrationtestingblogseries.server.persistency.Customer
import blog.devhut.integrationtestingblogseries.server.persistency.CustomerRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.reactive.function.client.WebClient

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(value = MethodOrderer.OrderAnnotation::class)
class ManualCleaningWithoutTransactionIntegrationTest {

    var customerId: Long? = null
    val objectMapper = ObjectMapper()
    @Autowired
    lateinit var customerRepository: CustomerRepository
    @Autowired
    lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        cleaningDBs()
        customerId = customerRepository.save(Customer("540218/5678", "John", "Travolta")).id
    }

    @Test
    @Order(1)
    fun whenCustomerIsCommittedByRequiresNewPropagationThenIsVisibleIsTest() {

        // Valid initial state
        assertThat(customerRepository.count()).isEqualTo(1)
        assertThat(customerRepository.findCustomerByIdentificationNumber("821223/3434"))

        // Create a new customer which is committed directly into DB because of REQUIRES_NEW propagation
        val customer = Customer("802010/6789", "Peter", "Pan")
        mockMvc.perform(MockMvcRequestBuilders.post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)))
            .andExpect(status().isCreated)

        // Validate that all data are visible in current transaction context
        assertThat(customerRepository.count()).isEqualTo(2)
        assertThat(customerRepository.findCustomerByIdentificationNumber("802010/6789"))
    }

    @Test
    @Order(2)
    fun whenSecondTestCaseRunsThenDataInDBAreCleaned() {
        assertThat(customerRepository.count()).isEqualTo(1)
        assertThat(customerRepository.findCustomerByIdentificationNumber("540218/5678"))
    }

    @Test
    fun whenHttpClientWorksInDifferentThreadThenDataCreatedInTestAreAlsoReachable() {
        val customerTest = WebClient.builder().baseUrl("http://localhost:8080/api").build()
            .get().uri("/customers/$customerId").retrieve().bodyToMono(Customer::class.java).blockOptional()
        assertThat(customerTest).isPresent
    }

    fun cleaningDBs() {
        customerRepository.deleteAllInBatch()
    }
}
