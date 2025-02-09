
package com.jsql.model.accessible.vendor.postgres;

import java.io.Serializable;

public class ModelYamlPostgres implements Serializable {

    private Shell shell = new Shell();
    private Rce rce = new Rce();
    private File file = new File();

    public Shell getShell() {
        return this.shell;
    }

    public void setShell(Shell shell) {
        this.shell = shell;
    }

    public Rce getRce() {
        return this.rce;
    }

    public void setRce(Rce rce) {
        this.rce = rce;
    }

    public File getFile() {
        return this.file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}