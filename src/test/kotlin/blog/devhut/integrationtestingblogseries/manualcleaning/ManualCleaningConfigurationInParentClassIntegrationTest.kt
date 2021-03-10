package blog.devhut.integrationtestingblogseries.manualcleaning

import blog.devhut.integrationtestingblogseries.server.persistency.Customer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.reactive.function.client.WebClient

class ManualCleaningConfigurationInParentClassIntegrationTest : AbstractManualCleaningWithTransactionIntegrationTest() {

    var customerId: Long? = null

    @BeforeEach
    override fun setUp() {
        super.setUp() // Unfortunately JUnit 5 requires write this line of the code. In Spock or JUnit 4 it is not necessary
        customerId = customerRepository.save(Customer("540218/5678", "John", "Travolta")).id
    }

    @Test
    fun whenCustomerIsCommittedByRequiresNewPropagationThenIsVisibleIsTest() {

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
    fun whenHttpClientWorksInDifferentThreadThenDataCreatedInTestAreAlsoReachable() {
        val customerTest = WebClient.builder().baseUrl("http://localhost:8080/api").build()
            .get().uri("/customers/$customerId").retrieve().bodyToMono(Customer::class.java).blockOptional()
        assertThat(customerTest).isPresent
    }
}
