package sheridan.czuberad.sideeye.Domain

import java.util.Date
import java.sql.Timestamp
import java.util.*

data class Alert(
    var alertUUID: String? = null,
    var alertSeverity: String? = null,
    var alertTime: Date? = null,
    var alertDuration: Int? = null
)