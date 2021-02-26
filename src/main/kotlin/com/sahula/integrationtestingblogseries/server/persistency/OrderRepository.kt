package com.sahula.integrationtestingblogseries.server.persistency

import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository : JpaRepository<Order, Long> {
}