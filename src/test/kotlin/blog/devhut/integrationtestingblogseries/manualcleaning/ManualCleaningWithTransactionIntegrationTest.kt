package blog.devhut.integrationtestingblogseries.manualcleaning

import blog.devhut.integrationtestingblogseries.server.persistency.Customer
import blog.devhut.integrationtestingblogseries.server.persistency.CustomerRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.jdbc.SqlScriptsTestExecutionListener
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.context.support.DirtiesContextBeforeModesTestExecutionListener
import org.springframework.test.context.support.DirtiesContextTestExecutionListener
import org.springframework.test.context.web.ServletTestExecutionListener
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.reactive.function.client.WebClient
import javax.transaction.Transactional

@ExtendWith(SpringExtension::class)
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@TestExecutionListeners( // I must put all listeners to list because the annotation does not support remove listener operation.
    listeners = [
        ServletTestExecutionListener::class,
        DirtiesContextBeforeModesTestExecutionListener::class,
        DependencyInjectionTestExecutionListener::class,
        DirtiesContextTestExecutionListener::class,
//		TransactionalTestExecutionListener::class, // This listener must be remove to enable a Spring transaction management not a test one
        SqlScriptsTestExecutionListener::class
    ]
)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation::class)
class ManualCleaningWithTransactionIntegrationTest {

    var customerId: Long? = null
    val objectMapper = ObjectMapper()
    @Autowired
    lateinit var mockMvc: MockMvc
    @Autowired
    lateinit var customerRepository: CustomerRepository

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
        assertThat(customerRepository.findCustomerByIdentificationNumber("540218/5678"))

        // Create a new customer which is committed directly into DB because of REQUIRES_NEW propagation
        val customer = Customer("802010/6789", "Peter", "Pan")

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer))
        )
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
    @Order(3)
    fun whenHttpClientWorksInDifferentThreadThenDataCreatedInTestAreAlsoReachable() {
        val customerTest = WebClient.builder().baseUrl("http://localhost:8080/api").build()
            .get().uri("/customers/$customerId").retrieve().bodyToMono(Customer::class.java).blockOptional()
        assertThat(customerTest).isPresent
    }

    fun cleaningDBs() {
        customerRepository.deleteAllInBatch()
    }
}
