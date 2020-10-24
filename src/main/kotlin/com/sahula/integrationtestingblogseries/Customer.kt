package com.sahula.integrationtestingblogseries

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.NotNull

@Entity
data class Customer(
        var identificationNumber: String,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null
    @NotNull
    lateinit var name: String
    @NotNull
    lateinit var surname: String
}