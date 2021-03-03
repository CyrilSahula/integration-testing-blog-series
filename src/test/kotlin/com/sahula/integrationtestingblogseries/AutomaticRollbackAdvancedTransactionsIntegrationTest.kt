package com.sahula.integrationtestingblogseries

import com.fasterxml.jackson.databind.ObjectMapper
import com.sahula.integrationtestingblogseries.server.persistency.Customer
import com.sahula.integrationtestingblogseries.server.persistency.CustomerRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCaseOrder
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Order
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import javax.transaction.Transactional

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@Transactional
class AutomaticRollbackAdvancedTransactionsIntegrationTest(
	@Autowired var mockMvc: MockMvc,
	@Autowired var customerRepository: CustomerRepository,
	var customerCreatedByTest: Customer,
	var customerInDB: Customer,
	val objectMapper: ObjectMapper = ObjectMapper()
) : StringSpec() {

	override fun testCaseOrder(): TestCaseOrder? = TestCaseOrder.Sequential

	@Before
	fun setUp() {
		customerInDB = customerRepository.findCustomerByIdentificationNumber("821223/3434").let { it!! }
		customerCreatedByTest = customerRepository.save(Customer("540218/5678", "John", "Travolta"))
	}

//	@Test
	@Order(1)
	"When service with own transaction is called then test can not rollback data" {

		val customer = Customer("802010/6789", "Peter", "Pan")

		mockMvc.perform(post("/api/customers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(customer)))
				.andExpect(status().isCreated)

		// The fact that the new customer is committed into DB can be assert in the latest test case
	}

//	@Test
//	@Order(2)
//	fun whenServiceWithRequiredTransactionModeIsCalledThenTestCanRollbackData() {
//
//		val customer = Customer("420618/7878", "Paul", "McCartney")
//
//		mockMvc.perform(put("/api/customers/${customerInDB.id}")
//				.contentType(MediaType.APPLICATION_JSON)
//				.content(objectMapper.writeValueAsString(customer)))
//				.andExpect(status().isNoContent)
//
//		// The fact that the updated customer is rollback can be assert in the latest test case
//	}
//
//	@Test
//	@Order(3)
//	fun whenTransactionDoesNotStartInTestCaseThenCanNotRollback() {
//
//		val updatedCustomer = customerRepository.findCustomerByIdentificationNumber("420618/7878")
//		assertThat(updatedCustomer).isNull()
//
//		val createdCustomer = customerRepository.findCustomerByIdentificationNumber("802010/6789")
//		assertThat(createdCustomer).isNotNull
//	}


})
