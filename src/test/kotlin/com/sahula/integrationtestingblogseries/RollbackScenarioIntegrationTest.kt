package com.sahula.integrationtestingblogseries

import com.sahula.integrationtestingblogseries.service.persistency.Customer
import com.sahula.integrationtestingblogseries.service.persistency.CustomerRepository
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.reactive.function.client.WebClient
import javax.transaction.Transactional


@Transactional
@ActiveProfiles(ApplicationProfile.INTEGRATION_TESTING)
@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class RollbackScenarioIntegrationTest {

	// TODO add test case new transaction

	lateinit var customer: Customer

	@LocalServerPort
	lateinit var port: Integer

	@Autowired
	lateinit var mockMvc: MockMvc
	@Autowired
	lateinit var customerRepository: CustomerRepository

	lateinit var webClient: WebClient

	@Before
	fun setUp() {
		customer = customerRepository.save(Customer("540218/5678", "John", "Travolta"))
		webClient = WebClient.builder().baseUrl("http://localhost:${port}/api").build()
	}

	@Test
	fun whenClientWorksInSameThreadThenDataCreatedInTestAreVisible() {

		// Testcase has access to customer created in setup method
		mockMvc.perform(get("/api/customers/${customer.id}"))
				.andExpect(status().isOk)
				.andExpect(jsonPath("$.identificationNumber").value("540218/5678"))

		// Testcase has access to customers already in DB
		mockMvc.perform(get("/api/customers/${customer.id}"))
				.andExpect(status().isOk)
				.andExpect(jsonPath("$.identificationNumber").value("821223/3434"))
	}

	@Test
	fun whenClientWorksInDifferentThreadThenDataCreatedInTestAreNotVisible() {

		val customer = webClient.get().uri("/api/customers/${customer.id}")
				.retrieve().bodyToMono(Customer::class.java)
	}
}
