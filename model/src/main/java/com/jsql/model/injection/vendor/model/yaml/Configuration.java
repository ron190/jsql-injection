
package com.jsql.model.injection.vendor.model.yaml;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("serial")
public class Configuration implements Serializable {

    private String slidingWindow = StringUtils.EMPTY;
    private String limit = StringUtils.EMPTY;
    private String failsafe = StringUtils.EMPTY;
    private String calibrator = StringUtils.EMPTY;
    private String limitBoundary = "0";
    private String endingComment = StringUtils.EMPTY;
    private Fingerprint fingerprint = new Fingerprint();

    public String getSlidingWindow() {
        return this.slidingWindow;
    }

    public void setSlidingWindow(String slidingWindow) {
        this.slidingWindow = slidingWindow;
    }

    public String getLimit() {
        return this.limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public String getFailsafe() {
        return this.failsafe;
    }

    public void setFailsafe(String failsafe) {
        this.failsafe = failsafe;
    }

    public String getCalibrator() {
        return this.calibrator;
    }

    public void setCalibrator(String calibrator) {
        this.calibrator = calibrator;
    }

    public String getLimitBoundary() {
        return this.limitBoundary;
    }

    public void setLimitBoundary(String limitBoundary) {
        this.limitBoundary = limitBoundary;
    }

    public String getEndingComment() {
        return this.endingComment;
    }

    public void setEndingComment(String endingComment) {
        this.endingComment = endingComment;
    }

    public Fingerprint getFingerprint() {
        return this.fingerprint;
    }

    public void setFingerprint(Fingerprint fingerprint) {
        this.fingerprint = fingerprint;
    }
}
