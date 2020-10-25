package com.sahula.integrationtestingblogseries.service.persistency

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.NotNull

@Entity
data class Customer private constructor(
        var identificationNumber: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @NotNull
    lateinit var firstName: String
    @NotNull
    lateinit var surname: String

    constructor(identificationNumber: String, firstName: String, surname: String) : this(identificationNumber) {
        this.firstName = firstName
        this.surname = surname
    }
}