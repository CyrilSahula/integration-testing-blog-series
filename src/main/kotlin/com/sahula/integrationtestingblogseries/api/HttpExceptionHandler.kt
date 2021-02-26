package com.sahula.integrationtestingblogseries.api

import com.sahula.integrationtestingblogseries.server.exception.NotFoundException
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class HttpExceptionHandler {

    @ExceptionHandler(value = [
        NotFoundException::class
    ])
    fun handleExceptionsToNotFoundResponse(exception: Exception): ResponseEntity<String> {
        return ResponseEntity(exception.message, NOT_FOUND)
    }
}