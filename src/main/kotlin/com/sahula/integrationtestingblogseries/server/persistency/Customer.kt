package com.sahula.integrationtestingblogseries.server.persistency

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@Entity
class Customer(
    @NotEmpty
    var identificationNumber: String,
    @NotEmpty
    var firstName: String,
    @NotEmpty
    var surname: String
) : IdentifiableEntity()