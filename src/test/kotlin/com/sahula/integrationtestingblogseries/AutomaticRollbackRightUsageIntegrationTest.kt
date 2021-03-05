package com.sahula.integrationtestingblogseries

import com.fasterxml.jackson.databind.ObjectMapper
import com.sahula.integrationtestingblogseries.server.persistency.Contract
import com.sahula.integrationtestingblogseries.server.persistency.ContractRepository
import org.hamcrest.collection.IsCollectionWithSize.hasSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import javax.transaction.Transactional

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@Transactional
class AutomaticRollbackRightUsageIntegrationTest {

	lateinit var contract: Contract
	@Autowired lateinit var mockMvc: MockMvc
	@Autowired lateinit var orderRepository: ContractRepository
	val objectMapper = ObjectMapper()

	@BeforeEach
	fun setUp() {
		contract = orderRepository.save(Contract("GR35334", 3456.5))
	}

	@Test
	fun whenGetOneIsCalledThenIsReturned200AndBody() {
		mockMvc.perform(get("/api/contracts/${contract.id}"))
				.andExpect(status().isOk)
				.andExpect(jsonPath("id").value(contract.id.toString()))
				.andExpect(jsonPath("number").value("GR35334"))
				.andExpect(jsonPath("price").value("3456.5"))
	}

	@Test
	fun whenGetListIsCalledThenIsReturned200AndBody() {

		orderRepository.save(Contract("GR35335", 1000.5))

		mockMvc.perform(get("/api/contracts"))
			.andExpect(status().isOk)
			.andExpect(jsonPath("$").value(hasSize<Any>(2)))
			.andExpect(jsonPath("$[0].id").value(contract.id.toString()))
			.andExpect(jsonPath("$[0].number").value("GR35334"))
			.andExpect(jsonPath("$[0].price").value("3456.5"))
	}

	@Test
	fun whenCreateIsCalledThenIsReturned201AndBody() {
		mockMvc.perform(post("/api/contracts")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(Contract("AV35322", 500.0))))
			.andExpect(status().isCreated)
			.andExpect(jsonPath("id").isNotEmpty)
			.andExpect(jsonPath("number").value("AV35322"))
			.andExpect(jsonPath("price").value("500.0"))
	}

	@Test
	fun whenUpdateIsCalledThenIsReturned200AndBody() {

		val updatedContract = Contract("AV35322", 500.0)
		updatedContract.id = contract.id

		mockMvc.perform(put("/api/contracts/${contract.id}")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(updatedContract)))
			.andExpect(status().isOk)
			.andExpect(jsonPath("id").value(contract.id.toString()))
			.andExpect(jsonPath("number").value("AV35322"))
			.andExpect(jsonPath("price").value("500.0"))
	}

	@Test
	fun whenDeleteIsCalledThenIsReturned204() {
		mockMvc.perform(delete("/api/contracts/${contract.id}"))
			.andExpect(status().isNoContent)
	}


}
