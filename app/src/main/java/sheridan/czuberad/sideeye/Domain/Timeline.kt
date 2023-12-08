package sheridan.czuberad.sideeye.Domain

import java.sql.Timestamp
import java.util.Date

data class Timeline(
    var timelineTime: Date? = null,
    var duration: Int? = null,
    var severity: String? = null,
    var type: String? = null
)