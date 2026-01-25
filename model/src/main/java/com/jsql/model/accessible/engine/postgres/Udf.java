package com.jsql.model.accessible.engine.postgres;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class Udf implements Serializable {

    private Archive archive = new Archive();
    private Library library = new Library();
    private Extension extension = new Extension();
    private Program program = new Program();
    private String plpython = StringUtils.EMPTY;
    private String plperl = StringUtils.EMPTY;
    private String plsh = StringUtils.EMPTY;
    private String pltcl = StringUtils.EMPTY;
    private String plr = StringUtils.EMPTY;
    private String pllua = StringUtils.EMPTY;
    private String runFunc = StringUtils.EMPTY;
    private String dropFunc = StringUtils.EMPTY;
    private Sql sql = new Sql();

    public Extension getExtension() {
        return this.extension;
    }

    public void setExtension(Extension extension) {
        this.extension = extension;
    }

    public String getPlpython() {
        return this.plpython;
    }

    public void setPlpython(String plpython) {
        this.plpython = plpython;
    }

    public String getPlperl() {
        return this.plperl;
    }

    public void setPlperl(String plperl) {
        this.plperl = plperl;
    }

    public String getPlsh() {
        return this.plsh;
    }

    public void setPlsh(String plsh) {
        this.plsh = plsh;
    }

    public String getRunFunc() {
        return this.runFunc;
    }

    public void setRunFunc(String runFunc) {
        this.runFunc = runFunc;
    }

    public Sql getSql() {
        return this.sql;
    }

    public void setSql(Sql sql) {
        this.sql = sql;
    }

    public String getDropFunc() {
        return this.dropFunc;
    }

    public void setDropFunc(String dropFunc) {
        this.dropFunc = dropFunc;
    }

    public Archive getArchive() {
        return this.archive;
    }

    public void setArchive(Archive archive) {
        this.archive = archive;
    }

    public Program getProgram() {
        return this.program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public String getPltcl() {
        return this.pltcl;
    }

    public void setPltcl(String pltcl) {
        this.pltcl = pltcl;
    }

    public String getPlr() {
        return this.plr;
    }

    public void setPlr(String plr) {
        this.plr = plr;
    }

    public String getPllua() {
        return this.pllua;
    }

    public void setPllua(String pllua) {
        this.pllua = pllua;
    }

    public Library getLibrary() {
        return this.library;
    }

    public void setLibrary(Library library) {
        this.library = library;
    }
}