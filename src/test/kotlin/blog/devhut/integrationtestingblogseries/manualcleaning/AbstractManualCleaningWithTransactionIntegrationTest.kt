package blog.devhut.integrationtestingblogseries.manualcleaning

import blog.devhut.integrationtestingblogseries.server.persistency.CustomerRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.jdbc.SqlScriptsTestExecutionListener
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.context.support.DirtiesContextBeforeModesTestExecutionListener
import org.springframework.test.context.support.DirtiesContextTestExecutionListener
import org.springframework.test.context.web.ServletTestExecutionListener
import org.springframework.test.web.servlet.MockMvc
import javax.transaction.Transactional

@ExtendWith(SpringExtension::class)
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@TestExecutionListeners(
    listeners = [
        ServletTestExecutionListener::class,
        DirtiesContextBeforeModesTestExecutionListener::class,
        DependencyInjectionTestExecutionListener::class,
        DirtiesContextTestExecutionListener::class,
        SqlScriptsTestExecutionListener::class
    ]
)
abstract class AbstractManualCleaningWithTransactionIntegrationTest {

    val objectMapper = ObjectMapper()
    @Autowired
    lateinit var mockMvc: MockMvc
    @Autowired
    lateinit var customerRepository: CustomerRepository

    @BeforeEach
    fun setUp() {
        cleaningDBs()
    }

    fun cleaningDBs() {
        customerRepository.deleteAllInBatch()
    }
}
