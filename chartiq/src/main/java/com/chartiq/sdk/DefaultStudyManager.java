package com.chartiq.sdk;

import android.webkit.ValueCallback;

import com.chartiq.sdk.model.Study;
import com.google.gson.Gson;

import java.util.Map;

import static com.chartiq.sdk.Util.buildArgumentStringFromArgs;

// TODO: 18.08.20 Review the following class
public class DefaultStudyManager implements StudyManager {

    @Override
    public String addStudy(Study study, boolean firstLoad) {
        Map<String, Object> inputs = study.getInputs();
        Map<String, Object> outputs = study.getOutputs();
        Map<String, Object> parameters = study.getParameters();
        if(firstLoad){
            inputs = null;
            outputs = null;
        }
        if (study.getType() == null) {
            return addStudy(study.getShortName(), inputs, outputs, parameters);
        } else {
            return addStudy(study.getType(), inputs, outputs, parameters);
        }
    }

    @Override
    public String addStudy(String studyName, Map<String, Object> inputs, Map<String, Object> outputs, Map<String, Object> parameters) {
        String script = "addStudy(" + buildArgumentStringFromArgs(studyName, inputs, outputs, parameters) + ")";
        return script;
    }

    @Override
    public String removeStudy(String studyName) {
        return "removeStudy(\"" + studyName + "\");";
    }

    @Override
    public String removeAllStudies() {
        return "removeAllStudies();";
    }

    @Override
    public Promise<Study[]> getStudyList() {
        String script = "getStudyList();";
            final Promise<Study[]> promise = new Promise<>();
//            executeJavascript(script, new ValueCallback<String>() {
//                @Override
//                public void onReceiveValue(String value) {
//                    promise.setResult(new Gson().fromJson(value, Study[].class));
//                }
//            });
            return promise;
    }

    @Override
    public Promise<Study[]> getActiveStudies() {
        String script = "getActiveStudies();";
            final Promise<Study[]> promise = new Promise<>();
//            executeJavascript(script, new ValueCallback<String>() {
//                @Override
//                public void onReceiveValue(String value) {
//                    if (value.equalsIgnoreCase("null")) {
//                        value = "[]";
//                    }
//                    promise.setResult(new Gson().fromJson(value, Study[].class));
//                }
//            });
            return promise;
    }

    @Override
    public Promise<String> getStudyInputParameters(String studyName) {
        String script = "getStudyParameters(\"" + studyName + "\" , \"inputs\");";
            final Promise<String> promise = new Promise<>();
//            executeJavascript(script, new ValueCallback<String>() {
//                @Override
//                public void onReceiveValue(String value) {
//                    promise.setResult(value);
//                }
//            });
            return promise;
    }

    @Override
    public Promise<String> getStudyOutputParameters(String studyName) {
        String script = "getStudyParameters(\"" + studyName + "\" , \"outputs\");";
            final Promise<String> promise = new Promise<>();
//            executeJavascript(script, new ValueCallback<String>() {
//                @Override
//                public void onReceiveValue(String value) {
//                    promise.setResult(value);
//                }
//            });
            return promise;
    }

    @Override
    public Promise<String> getStudyParameters(String studyName) {
        String script = "getStudyParameters(\"" + studyName + "\" , \"parameters\");";
            final Promise<String> promise = new Promise<>();
//            executeJavascript(script, new ValueCallback<String>() {
//                @Override
//                public void onReceiveValue(String value) {
//                    promise.setResult(value);
//                }
//            });
            return promise;
    }

    @Override
    public String setStudyInputParameter(String studyName, String parameter, String value) {
        String args = buildArgumentStringFromArgs(studyName, parameter, value);
        String script = "setStudyParameter(" + args + ", true);";
        return script;
    }

    @Override
    public String setStudyOutputParameter(String studyName, String parameter, String value) {
        String args = buildArgumentStringFromArgs(studyName, parameter, value);
        String script = "setStudyParameter(" + args + ", false);";
        return script;
    }
}
