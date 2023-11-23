package sheridan.czuberad.sideeye.Domain

import java.sql.Timestamp
import java.util.Date
import java.util.UUID

data class Session(
    var sessionUUID: UUID? = null,
    var startSession: Date? = null,
    var endSession: Date? = null,
    var alertUUIDList: ArrayList<UUID>? = null,
    var fatigueList: ArrayList<Timestamp>? = null
    )