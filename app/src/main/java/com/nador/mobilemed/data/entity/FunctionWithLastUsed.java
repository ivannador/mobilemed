package com.nador.mobilemed.data.entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by nador on 26/01/2017.
 */

public class FunctionWithLastUsed extends RealmObject {

    @PrimaryKey
    private String mId;
    private long mLastUsed;

    private boolean mEnabled = false;

    public FunctionWithLastUsed() {}

    public FunctionWithLastUsed(String mId) {
        this.mId = mId;
    }

    public void setLastUsed(long lastUsed) {
        this.mLastUsed = lastUsed;
    }

    public void setEnabled(boolean enabled) {
        this.mEnabled = enabled;
    }

    public String getId() {
        return mId;
    }

    public long getLastUsed() {
        return mLastUsed;
    }

    public boolean isEnabled() {
        return mEnabled;
    }
}
