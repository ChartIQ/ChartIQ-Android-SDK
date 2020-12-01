package com.chartiq.sdk

import com.chartiq.sdk.model.*
import java.util.*

interface ChartIQStudy {


    fun getStudyList(callback: OnReturnCallback<List<Study>>)

    fun getActiveStudies(callback: OnReturnCallback<List<Study>>)

    fun removeStudy(study: Study)

    fun addStudy(study: Study, forClone: Boolean)

    fun setStudyParameter(study: Study, parameter: StudyParameterModel)

    fun setStudyParameters(study: Study, parameters: List<StudyParameterModel>)

    fun getStudyParameters(
            study: Study,
            type: StudyParameterType,
            callback: OnReturnCallback<List<StudyParameter>>
    )
}
