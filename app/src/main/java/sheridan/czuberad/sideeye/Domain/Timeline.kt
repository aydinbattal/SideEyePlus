package sheridan.czuberad.sideeye.Domain

data class Timeline(
    var timelineTime: String? = null,
    var duration: Int? = null,
    var severity: String? = null,
    var type: String? = null
)