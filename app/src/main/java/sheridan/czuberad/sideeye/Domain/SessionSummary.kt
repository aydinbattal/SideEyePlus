package sheridan.czuberad.sideeye.Domain

import java.sql.Timestamp

data class SessionSummary(
    var startSession: Timestamp? = null,
    var endSession: Timestamp? = null,
    var alertCount: Int? = null,
    var fatigueCount: Int? = null,
    //ADD FATIGUE AND ALERT HISTORY LIST
)