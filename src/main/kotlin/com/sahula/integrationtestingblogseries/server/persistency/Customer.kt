package com.sahula.integrationtestingblogseries.server.persistency

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@Entity
data class Customer private constructor(
        @NotEmpty
        var identificationNumber: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @NotEmpty
    lateinit var firstName: String
    @NotEmpty
    lateinit var surname: String

    constructor(identificationNumber: String, firstName: String, surname: String) : this(identificationNumber) {
        this.firstName = firstName
        this.surname = surname
    }
}