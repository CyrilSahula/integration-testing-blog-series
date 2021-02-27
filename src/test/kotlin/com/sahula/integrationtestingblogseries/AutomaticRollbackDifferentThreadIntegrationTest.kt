package com.sahula.integrationtestingblogseries

import com.sahula.integrationtestingblogseries.server.persistency.Customer
import com.sahula.integrationtestingblogseries.server.persistency.CustomerRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import javax.transaction.Transactional

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@Transactional
class AutomaticRollbackDifferentThreadIntegrationTest {

	lateinit var customerInDB: Customer
	lateinit var customerCreatedByTest: Customer

	@Autowired lateinit var mockMvc: MockMvc
	@Autowired lateinit var customerRepository: CustomerRepository

	@Before
	fun setUp() {
		customerInDB = customerRepository.findCustomerByIdentificationNumber("821223/3434").let { it!! }
		customerCreatedByTest = customerRepository.save(Customer("540218/5678", "John", "Travolta"))
	}

	@Test
	fun whenClientWorksInSameThreadThenDataCreatedInTestAreVisible() {

		mockMvc.perform(get("/api/customers/${customerCreatedByTest.id}"))
				.andExpect(status().isOk)

		mockMvc.perform(get("/api/customers/${customerInDB.id}"))
				.andExpect(status().isOk)
	}

	@Test
	fun whenClientWorksInDifferentThreadThenDataCreatedInTestAreNotVisible() {

		val webClient = WebClient.builder().baseUrl("http://localhost:8080/api").build()

		val exception: WebClientResponseException = assertThrows(WebClientResponseException::class.java) {
			webClient.get().uri("/customers/${customerCreatedByTest.id}").retrieve().bodyToMono(Customer::class.java).blockOptional()
		}
		assertThat(exception.statusCode).isEqualTo(HttpStatus.NOT_FOUND)

		val customer = webClient.get().uri("/customers/${customerInDB.id}").retrieve().bodyToMono(Customer::class.java).blockOptional()
		assertThat(customer).isPresent
	}
}
