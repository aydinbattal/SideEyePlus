package sheridan.czuberad.sideeye.`Application Logic`

import java.sql.Timestamp
import java.util.Date
class EyeDetectionLogic {

    fun getTimeStamp(): Date {
        val timeStamp = Timestamp(System.currentTimeMillis())
        return Date(timeStamp.time)
    }
}