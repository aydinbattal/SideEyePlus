package sheridan.czuberad.sideeye.Domain

import java.util.Date
import java.sql.Timestamp

data class Session(
    var sessionUUID: String? = null,
    var startSession: Date? = null,
    var endSession: Date? = null,
    var alertUUIDList: ArrayList<String>? = null,
    var fatigueList: ArrayList<Timestamp>? = null
    )