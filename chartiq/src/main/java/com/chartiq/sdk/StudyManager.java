package com.chartiq.sdk;

import com.chartiq.sdk.model.Study;

import java.util.Map;

public interface StudyManager {

    public String addStudy(Study study, boolean firstLoad);

    public String addStudy(String studyName, Map<String, Object> inputs, Map<String, Object> outputs, Map<String, Object> parameters);

    public String removeStudy(String studyName);

    public String removeAllStudies();

    public Promise<Study[]> getStudyList();

    public Promise<Study[]> getActiveStudies();

    public Promise<String> getStudyInputParameters(String studyName);

    public Promise<String> getStudyOutputParameters(String studyName);

    public Promise<String> getStudyParameters(String studyName);

    public String setStudyInputParameter(String studyName, String parameter, String value);

    public String setStudyOutputParameter(String studyName, String parameter, String value);
}
