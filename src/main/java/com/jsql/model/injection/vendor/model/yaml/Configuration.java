
package com.jsql.model.injection.vendor.model.yaml;


public class Configuration {

    private String slidingWindow;
    private String limit;
    private String failsafe;
    private String calibrator;
    private Integer limitBoundary;
    private String endingComment;
    private Fingerprint fingerprint;

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

    public Integer getLimitBoundary() {
        return this.limitBoundary;
    }

    public void setLimitBoundary(Integer limitBoundary) {
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
