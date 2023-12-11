package sheridan.czuberad.sideeye.Domain

/**
 * SideEye+ created by aydin
 * student ID : 991521740
 * on 2023-11-26 */
data class Questionnaire(
    var uid: String? = null,
    var category: String? = null,
    var date: String? = null,
    val sessionId: String? = null,
    var isPassed: Boolean? = null
)
