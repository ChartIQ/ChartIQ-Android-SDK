package com.chartiq.sdk.model.study

import com.chartiq.sdk.OnReturnCallback

interface ChartIQStudy {
    /**
     * Gets a list of available studies [Study]
     * @param callback A callback to subscribe to to get a list of studies
     */
    fun getStudyList(callback: OnReturnCallback<List<Study>>)

    /**
     * Gets a list of active/selected studies [Study]
     * @param callback A callback to subscribe to to get a list of studies
     */
    fun getActiveStudies(callback: OnReturnCallback<List<Study>>)

    /**
     * Removes a selected study [Study] from the list of active studies
     * @param study A [Study] to be deleted
     */
    fun removeStudy(study: Study)

    /**
     * Adds a study [Study] to a list of active studies
     * @param study A study to add/clone
     * @param forClone If true, clones a selected active study and adds it to the list of active studies with changed
     * parameters.
     * If false, adds a new study with default parameters to a list of active studies
     * if [study] is from  [getStudyList] use `false`,
     * if [study] is from [getActiveStudies] use `true`
     */
    fun addStudy(study: Study, forClone: Boolean)

    /**
     * Modifies a selected study [Study] with a single parameter
     * @param study A study to change
     * @param parameter A changed parameter for selected study, [StudyParameterModel] that contains key-value to be
     * updated
     * To set a parameter of [StudyParameterType.Parameters] type it's [StudyParameter.name] should be transformed to
     * [StudyParameter.name] + [StudyParameter.StudyParameterNamePostfix]
     * <p>
     *     For example, for the color parameter with a original name `line` a generated name should be `lineColor`
     * </p>
     */
    fun setStudyParameter(study: Study, parameter: StudyParameterModel)

    /**
     * Modifies a selected study [Study] with a given list of parameters
     * @param study A study to change
     * @param parameters A list of changed parameters [StudyParameterModel] that contains values for selected study to
     * be updated
     */
    fun setStudyParameters(study: Study, parameters: List<StudyParameterModel>)

    /**
     * Gets a list of parameters [StudyParameter] of a selected study [Study]
     * @param study A selected study [Study]
     * @param type A type [StudyParameterType] of parameters to get
     * @param callback A callback to subscribe to to get a list of parameters [StudyParameter]
     */
    fun getStudyParameters(
        study: Study,
        type: StudyParameterType,
        callback: OnReturnCallback<List<StudyParameter>>
    )
}
