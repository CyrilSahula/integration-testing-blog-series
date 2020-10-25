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
import org.springframework.test.context.transaction.AfterTransaction
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.client.WebClient
import javax.transaction.Transactional


@Transactional
@ActiveProfiles(ApplicationProfile.INTEGRATION_TESTING)
@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class RollbackScenarioIntegrationTest {

	lateinit var customer: Customer

	@LocalServerPort
	lateinit var port: Integer

	@Autowired
	lateinit var mockMvc: MockMvc
	@Autowired
	lateinit var customerRepository: CustomerRepository


	@Before
	fun setUp() {
		customer = customerRepository.save(Customer("540218/5678", "John", "Travolta"))
	}

	@Test
	fun contextLoads() {

		val uri = WebClient.create("http://localhost:${port}").get().uri("/api/customers/${customer.id}").retrieve().bodyToMono(Customer::class.java).block()

//		val forObject = restTemplate.getForObject("http://localhost:${port}/api/customers/${customer.id}", Customer::class.java)


		mockMvc.perform(get("/api/customers/${customer.id}"))
				.andExpect(status().isOk)
				.andExpect(jsonPath("$.identificationNumber").value("540218/5678"))
				.andExpect(jsonPath("$.name").value("John"))
				.andExpect(jsonPath("$.surname").value("Travolta"))
	}

	@AfterTransaction
	fun afterTransaction() {
		customerRepository.findAll()
	}
}
