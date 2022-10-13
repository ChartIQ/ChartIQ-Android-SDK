package com.chartiq.sdk.model.study

data class StudySimplifiedEntity(
    val studyName: String,
    var parsed_outputs: List<Map<String, Object>>?,
    val type: String?,
)

fun StudySimplifiedEntity.toStudySimplified(): StudySimplified {
    return StudySimplified(
        studyName = studyName,
        outputs = parsed_outputs?.firstOrNull() ?: emptyMap(),
        type = type
    )
}
