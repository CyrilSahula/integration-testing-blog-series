package blog.devhut.integrationtestingblogseries.server.persistency

import javax.persistence.Entity
import javax.validation.constraints.NotEmpty

@Entity
class Customer(
    @NotEmpty
    var identificationNumber: String,
    @NotEmpty
    var firstName: String,
    @NotEmpty
    var surname: String

) : IdentifiableEntity()