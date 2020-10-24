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
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null
    @NotNull
    lateinit var name: String
    @NotNull
    lateinit var surname: String

    constructor(identificationNumber: String, name: String, surname: String) : this(identificationNumber) {
        this.name = name
        this.surname = surname
    }
}