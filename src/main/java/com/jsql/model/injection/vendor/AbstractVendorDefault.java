package com.jsql.model.injection.vendor;


public abstract class AbstractVendorDefault extends AbstractVendor {
    
    protected Model xmlModel;
	
    @Override
    public String sqlPrivilegeTest() {
        return "";
    }

    @Override
    public String sqlFileRead(String filePath) {
        return "";
    }

    @Override
    public String sqlTextIntoFile(String content, String filePath) {
        return "";
    }

    @Override
    public String[] getListFalseTest() {
        return new String[0];
    }

    @Override
    public String[] getListTrueTest() {
        return new String[0];
    }

    @Override
    public String sqlTestBlindFirst() {
        return null;
    }

    @Override
    public String sqlTestBlind(String check) {
        return "";
    }

    @Override
    public String sqlBitTestBlind(String inj, int indexCharacter, int bit) {
        return "";
    }

    @Override
    public String sqlLengthTestBlind(String inj, int indexCharacter) {
        return "";
    }

    @Override
    public String sqlTimeTest(String check) {
        return "";
    }

    @Override
    public String sqlBitTestTime(String inj, int indexCharacter, int bit) {
        return "";
    }

    @Override
    public String sqlLengthTestTime(String inj, int indexCharacter) {
        return "";
    }

    @Override
    public String sqlBlind(String sqlQuery, String startPosition) {
        return "";
    }

    @Override
    public String sqlCapacityErrorBased() {
        return "";
    }

    @Override
    public String sqlTestErrorBased() {
        return "";
    }

    @Override
    public String sqlErrorBased(String sqlQuery, String startPosition) {
        return "";
    }
    
    @Override
    public String sqlErrorBasedCapacity() {
        return "";
    }

    @Override
    public String sqlTime(String sqlQuery, String startPosition) {
        return "";
    }
    
    public void setXmlModel(Model xmlModel) {
        this.xmlModel = xmlModel;
    }
    
    @Override
    public Model getXmlModel() {
        return this.xmlModel;
    }
    
}