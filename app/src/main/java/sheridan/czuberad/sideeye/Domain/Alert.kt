package sheridan.czuberad.sideeye.Domain

import java.util.*

data class Alert(
    var alertSeverity: String? = null,
    var alertTime: Date? = null,
)