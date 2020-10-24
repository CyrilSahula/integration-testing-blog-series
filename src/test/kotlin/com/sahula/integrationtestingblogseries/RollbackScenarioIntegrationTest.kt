package com.sahula.integrationtestingblogseries

import com.sahula.integrationtestingblogseries.service.persistency.Customer
import com.sahula.integrationtestingblogseries.service.persistency.CustomerRepository
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.context.transaction.AfterTransaction
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import javax.transaction.Transactional


@Transactional
@ActiveProfiles(ApplicationProfile.INTEGRATION_TESTING)
@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
class RollbackScenarioIntegrationTest {

	lateinit var customer: Customer

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
