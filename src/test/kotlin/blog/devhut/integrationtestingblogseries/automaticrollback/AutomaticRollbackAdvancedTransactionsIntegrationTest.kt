package blog.devhut.integrationtestingblogseries.automaticrollback

import blog.devhut.integrationtestingblogseries.server.persistency.Customer
import blog.devhut.integrationtestingblogseries.server.persistency.CustomerRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@Transactional
@TestMethodOrder(value = MethodOrderer.OrderAnnotation::class)
class AutomaticRollbackAdvancedTransactionsIntegrationTest {

	lateinit var customerCreatedByTest: Customer
	lateinit var customerInDB: Customer

	@Autowired
	lateinit var mockMvc: MockMvc
	@Autowired
	lateinit var customerRepository: CustomerRepository

	val objectMapper = ObjectMapper()

	@BeforeEach
	fun setUp() {
		customerInDB = customerRepository.findCustomerByIdentificationNumber("821223/3434").let { it!! }
		customerCreatedByTest = customerRepository.save(Customer("540218/5678", "John", "Travolta"))
	}

	@Test
	@Order(1)
	fun whenServiceWithOwnTransactionIsCalledThenTestCanNotRollbackData() {

		val customer = Customer("802010/6789", "Peter", "Pan")

		mockMvc.perform(post("/api/customers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(customer)))
				.andExpect(status().isCreated)

		// The fact that the new customer is committed into DB can be assert in the latest test case
	}

	@Test
	@Order(2)
	fun whenServiceWithRequiredTransactionModeIsCalledThenTestCanRollbackData() {

		val customer = Customer("420618/7878", "Paul", "McCartney")

		mockMvc.perform(put("/api/customers/${customerInDB.id}")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(customer)))
				.andExpect(status().isNoContent)

		// The fact that the updated customer is rollback can be assert in the latest test case
	}

	@Test
	@Order(3)
	fun whenTransactionDoesNotStartInTestCaseThenCanNotRollback() {

		val updatedCustomer = customerRepository.findCustomerByIdentificationNumber("420618/7878")
		assertThat(updatedCustomer).isNull()

		val createdCustomer = customerRepository.findCustomerByIdentificationNumber("802010/6789")
		assertThat(createdCustomer).isNotNull
	}


}
