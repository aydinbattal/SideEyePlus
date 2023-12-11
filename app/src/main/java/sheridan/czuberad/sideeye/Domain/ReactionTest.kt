package sheridan.czuberad.sideeye.Domain

import java.util.*

/**
 * SideEye+ created by aydin
 * student ID : 991521740
 * on 2023-11-26 */
data class ReactionTest (
    var uid: String? = null,
    var avgTime: Long? = null,
    var date: String? = null,
    val sessionId: String? = null,
    var isPassed: Boolean? = null
)