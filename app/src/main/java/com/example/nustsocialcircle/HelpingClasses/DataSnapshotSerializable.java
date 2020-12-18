package com.example.nustsocialcircle.HelpingClasses;

import com.google.firebase.database.DataSnapshot;

import java.io.Serializable;
import java.util.Date;

public class DataSnapshotSerializable implements Serializable {

    private transient DataSnapshot dataSnapshot;
    private Date lastModified;

    public DataSnapshotSerializable() {
    }

    public DataSnapshotSerializable(DataSnapshot dataSnapshot, Date lastModified) {
        this.dataSnapshot = dataSnapshot;
        this.lastModified = lastModified;
    }

    public DataSnapshot getDataSnapshot() {
        return dataSnapshot;
    }

    public void setDataSnapshot(DataSnapshot dataSnapshot) {
        this.dataSnapshot = dataSnapshot;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }


}
