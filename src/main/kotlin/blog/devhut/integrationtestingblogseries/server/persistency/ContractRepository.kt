package blog.devhut.integrationtestingblogseries.server.persistency

import org.springframework.data.jpa.repository.JpaRepository

interface ContractRepository : JpaRepository<Contract, Long>