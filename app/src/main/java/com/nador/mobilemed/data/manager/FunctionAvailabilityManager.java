package com.nador.mobilemed.data.manager;

import android.content.Context;

import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.nador.mobilemed.data.APIConstants;
import com.nador.mobilemed.data.aws.CognitoManager;
import com.nador.mobilemed.data.entity.FunctionWithLastUsed;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import timber.log.Timber;

/**
 * Created by nador on 27/07/16.
 */
public class FunctionAvailabilityManager extends Database {

    public static final String GLUCOMETER_FUNCTION_ID = "blood_glucose";
    public static final String BLOODPRESSURE_FUNCTION_ID = "blood_pressure";
    public static final String ECG_FUNCTION_ID = "ecg";
    public static final String OXIMETER_FUNCTION_ID = "pulseoxy";
    public static final String TEMPERATURE_FUNCTION_ID = "temperature";
    public static final String WEIGHT_FUNCTION_ID = "weight";
    public static final String ASSESSMENT_FUNCTION_ID = "self_assessment";
    public static final String APNEA_FUNCTION_ID = "apnea";
    public static final String MEDICATION_FUNCTION_ID = "medication_management";

    public static final String MEASUREMENT_FUNCTIONS_KEY = "measurement_functions";

    private CognitoManager mCognitoManager;

    public FunctionAvailabilityManager(final Context context, final APIConstants apiConstants, final CognitoManager cognitoManager) {
        super(context,
                new RealmConfiguration.Builder(context)
                        .name(apiConstants.getConfigurationRealmName()));
        mCognitoManager = cognitoManager;

        if (getFunctionList().isEmpty()) {
            addFunction(new FunctionWithLastUsed(GLUCOMETER_FUNCTION_ID));
            addFunction(new FunctionWithLastUsed(BLOODPRESSURE_FUNCTION_ID));
            addFunction(new FunctionWithLastUsed(ECG_FUNCTION_ID));
            addFunction(new FunctionWithLastUsed(OXIMETER_FUNCTION_ID));
            addFunction(new FunctionWithLastUsed(TEMPERATURE_FUNCTION_ID));
            addFunction(new FunctionWithLastUsed(WEIGHT_FUNCTION_ID));
            addFunction(new FunctionWithLastUsed(ASSESSMENT_FUNCTION_ID));
            addFunction(new FunctionWithLastUsed(APNEA_FUNCTION_ID));
            addFunction(new FunctionWithLastUsed(MEDICATION_FUNCTION_ID));
        }

        mCognitoManager.getSettingsDataset()
                .read(MEASUREMENT_FUNCTIONS_KEY)
                .subscribe(s -> {
                    try {
                        List<FunctionItem> functions = LoganSquare.parseList(s, FunctionItem.class);
                        for (FunctionItem function : functions) {
                            setEnabled(function.getId(), function.isEnabled());
                        }
                    } catch (IOException e) {
                        Timber.e(e, "Couldn't parse Function List");
                    }
                }, throwable -> {
                    Timber.e(throwable, "Couldn't get function list");
                });
    }

    public void setLastUsed(final String id, final long timestamp) {
        final Realm realm = realm();
        realm.beginTransaction();
        FunctionWithLastUsed result = realm.where(FunctionWithLastUsed.class).equalTo("mId", id).findFirst();
        if (result != null) {
            result.setLastUsed(timestamp);
        }
        realm.commitTransaction();
        realm.close();
    }

    public void setEnabled(final String id, final boolean enabled) {
        final Realm realm = realm();
        realm.beginTransaction();
        FunctionWithLastUsed result = realm.where(FunctionWithLastUsed.class).equalTo("mId", id).findFirst();
        if (result != null) {
            result.setEnabled(enabled);
        }
        realm.commitTransaction();
        realm.close();
    }

    public ArrayList<FunctionWithLastUsed> getFunctionList() {
        final Realm realm = realm();
        final RealmResults<FunctionWithLastUsed> results = realm.where(FunctionWithLastUsed.class).findAll();
        ArrayList<FunctionWithLastUsed> functions = new ArrayList<>();
        for (FunctionWithLastUsed function : results) {
            functions.add(function);
        }
        return functions;
    }

    public void addFunction(final FunctionWithLastUsed function) {
        storeObject(function);
    }

    @JsonObject
    static public class FunctionItem {

        @JsonField(name = "id")
        private String mId;
        @JsonField(name = "enabled")
        private boolean mEnabled;

        public FunctionItem() {}

        public FunctionItem(String mId, boolean enabled) {
            this.mId = mId;
            this.mEnabled = enabled;
        }

        public void setId(String mId) {
            this.mId = mId;
        }

        public void setEnabled(boolean enabled) {
            this.mEnabled = enabled;
        }

        public String getId() {
            return mId;
        }

        public boolean isEnabled() {
            return mEnabled;
        }
    }
}
