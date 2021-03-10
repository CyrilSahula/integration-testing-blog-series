package blog.devhut.integrationtestingblogseries.server.persistency

import javax.persistence.*
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@Entity
class Contract(
    @NotEmpty
    var number: String,
    @NotNull
    var price: Double
) : IdentifiableEntity()