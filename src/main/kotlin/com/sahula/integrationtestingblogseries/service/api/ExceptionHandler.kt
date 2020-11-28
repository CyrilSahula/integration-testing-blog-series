package com.sahula.integrationtestingblogseries.service.api

import com.sahula.integrationtestingblogseries.service.exeption.NotFoundException
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(value = [
        NotFoundException::class
    ])
    fun handleExceptionsToNotFoundResponse(exception: Exception): ResponseEntity<String> {
        return ResponseEntity(exception.message, NOT_FOUND)
    }
}