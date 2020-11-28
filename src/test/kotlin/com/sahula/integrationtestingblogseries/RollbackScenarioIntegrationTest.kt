package com.sahula.integrationtestingblogseries

import com.fasterxml.jackson.databind.ObjectMapper
import com.sahula.integrationtestingblogseries.server.persistency.Customer
import com.sahula.integrationtestingblogseries.server.persistency.CustomerRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Order
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import javax.transaction.Transactional

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@Transactional
class RollbackScenarioIntegrationTest {

	lateinit var customerCreatedByTest: Customer
	lateinit var customerInDB: Customer

	@Autowired
	lateinit var mockMvc: MockMvc
	@Autowired
	lateinit var customerRepository: CustomerRepository

	val objectMapper = ObjectMapper()

	@Before
	fun setUp() {
		customerInDB = customerRepository.findCustomerByIdentificationNumber("821223/3434").let { it!! }
		customerCreatedByTest = customerRepository.save(Customer("540218/5678", "John", "Travolta"))
	}

	@Test
	@Order(1)
	fun whenClientWorksInSameThreadThenDataCreatedInTestAreVisible() {

		mockMvc.perform(get("/api/customers/${customerCreatedByTest.id}"))
				.andExpect(status().isOk)

		mockMvc.perform(get("/api/customers/${customerInDB.id}"))
				.andExpect(status().isOk)
	}

	@Test
	@Order(2)
	fun whenClientWorksInDifferentThreadThenDataCreatedInTestAreNotVisible() {

		val webClient = WebClient.builder().baseUrl("http://localhost:8080/api").build()

		val exception: WebClientResponseException = assertThrows(WebClientResponseException::class.java) {
			webClient.get().uri("/customers/${customerCreatedByTest.id}").retrieve().bodyToMono(Customer::class.java).blockOptional()
		}
		assertThat(exception.statusCode).isEqualTo(HttpStatus.NOT_FOUND)

		val customer = webClient.get().uri("/customers/${customerInDB.id}").retrieve().bodyToMono(Customer::class.java).blockOptional()
		assertThat(customer).isPresent
	}

	@Test
	@Order(3)
	fun whenServiceWithOwnTransactionIsCalledThenTestCanNotRollbackData() {

		val customer = Customer("802010/6789", "Peter", "Pan")

		mockMvc.perform(post("/api/customers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(customer)))
				.andExpect(status().isCreated)

		// The fact that the new customer is committed into DB can be assert in the latest test case
	}

	@Test
	@Order(4)
	fun whenServiceWithRequiredTransactionModeIsCalledThenTestCanRollbackData() {

		val customer = Customer("420618/7878", "Paul", "McCartney")

		mockMvc.perform(put("/api/customers/${customerInDB.id}")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(customer)))
				.andExpect(status().isNoContent)

		// The fact that the updated customer is rollback can be assert in the latest test case
	}

	@Test
	@Order(5)
	fun whenTransactionDoesNotStartInTestCaseThenCanNotRollback() {

		val updatedCustomer = customerRepository.findCustomerByIdentificationNumber("420618/7878")
		assertThat(updatedCustomer).isNull()

		val createdCustomer = customerRepository.findCustomerByIdentificationNumber("802010/6789")
		assertThat(createdCustomer).isNotNull
	}


}
