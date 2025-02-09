package com.jsql.model.accessible.vendor.postgres;

public class File {

    private Read read = new Read();
    private Write write = new Write();

    public Read getRead() {
        return this.read;
    }

    public void setRead(Read read) {
        this.read = read;
    }

    public Write getWrite() {
        return this.write;
    }

    public void setWrite(Write write) {
        this.write = write;
    }
}
