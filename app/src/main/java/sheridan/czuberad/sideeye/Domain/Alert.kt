package sheridan.czuberad.sideeye.Domain

import java.util.*

data class Alert(
    var alertType: String? = null,
    var alertTime: Date? = null,
)