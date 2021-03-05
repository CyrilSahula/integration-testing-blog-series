package com.sahula.integrationtestingblogseries

import com.fasterxml.jackson.databind.ObjectMapper
import com.sahula.integrationtestingblogseries.server.persistency.Customer
import com.sahula.integrationtestingblogseries.server.persistency.CustomerRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.annotation.Order
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
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
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.reactive.function.client.WebClient
import javax.transaction.Transactional


@ExtendWith(value = [RestDocumentationExtension::class, SpringExtension::class])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@AutoConfigureRestDocs
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
class ManualCleaningIntegrationTest {

    var customerId: Long? = null
    lateinit var mockMvc: MockMvc
    val objectMapper = ObjectMapper()
    @Autowired
    lateinit var context: WebApplicationContext
    @Autowired
    lateinit var customerRepository: CustomerRepository

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider?) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply<DefaultMockMvcBuilder>(documentationConfiguration(restDocumentation)).build()
    }

    @AfterEach
    fun clean() {
        cleaningDBs()
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
    fun whenHttpClientWorksInDifferentThreadThenDataCreatedInTestAreAlsoReachable() {
        val webClient = WebClient.builder().baseUrl("http://localhost:8080/api").build()
        val customerTest =
            webClient.get().uri("/customers/$customerId").retrieve().bodyToMono(Customer::class.java).blockOptional()
        assertThat(customerTest).isPresent
    }

    fun cleaningDBs() {
        customerRepository.deleteAllInBatch()
    }
}
