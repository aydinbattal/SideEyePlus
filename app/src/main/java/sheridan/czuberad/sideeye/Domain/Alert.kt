package sheridan.czuberad.sideeye.Domain

import java.sql.Timestamp
import java.util.*

data class Alert(
    var alertUUID: UUID? = null,
    var alertSeverity: String? = null,
    var alertTime: Timestamp? = null,
)