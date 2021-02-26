package com.sahula.integrationtestingblogseries.api

import com.sahula.integrationtestingblogseries.server.exception.NotFoundException
import com.sahula.integrationtestingblogseries.server.persistency.Order
import com.sahula.integrationtestingblogseries.server.persistency.OrderRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional
import javax.validation.Valid

@RestController
@RequestMapping("api/orders")
class OrderController(
        private val orderRepository: OrderRepository
) {

    @GetMapping
    fun getOrders(): List<Order> {
        return orderRepository.findAll().toList()
    }

    @GetMapping("/{id}")
    fun getOrder(@PathVariable id: Long): Order {
        return orderRepository.findByIdOrNull(id).let { it }
                ?: run { throw NotFoundException(id, Order::class) }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createOrder(@Valid @RequestBody order: Order) : Order {
        return orderRepository.save(order)
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateOrder(@PathVariable id: Long, @Valid @RequestBody order: Order) {
        orderRepository.findByIdOrNull(id)
                ?: run { throw NotFoundException(id, Order::class) }
        orderRepository.save(order)
    }
}