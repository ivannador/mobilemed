package com.nador.mobilemed.data.manager;

import android.content.Context;

import com.nador.mobilemed.data.entity.measurement.SerializedResult;
import com.nador.mobilemed.data.aws.CognitoManager;
import com.nador.mobilemed.data.entity.measurement.Result;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmConfiguration;
import io.realm.RealmList;
import rx.Observable;

/**
 * Created by nador on 19/08/16.
 */
public class MeasurementManager extends Database {

    private CognitoManager mCognitoManager;

    public MeasurementManager(final Context context, final String userId, final CognitoManager cognitoManager) {
        super(context,
                new RealmConfiguration.Builder(context)
                        .name(userId + "-measurement.realm"));
        mCognitoManager = cognitoManager;
    }

    // FIXME: cleanup this hacky stuff when Realm supports polymorphism
    public Observable<Boolean> storeResult(final Result result) {
        SerializedResult serializedResult = result.serialize();
        return Observable.fromCallable(() -> {
            storeObject(serializedResult);
            return true;
        }).flatMap(aBoolean -> storeOnCognito(serializedResult));
    }

    public Observable<Boolean> storeResultList(final List<Result> resultList) {
        List<SerializedResult> results = new ArrayList<>();
        for (Result result : resultList) {
            results.add(result.serialize());
        }
        return Observable.fromCallable(() -> {
            RealmList<SerializedResult> serializedResultRealmList = new RealmList<>();
            for (SerializedResult result : results) {
                serializedResultRealmList.add(result);
            }

            storeObjectList(serializedResultRealmList);
            return true;
        }).flatMap(aBoolean -> storeListOnCognito(results));
    }

    public Observable<Boolean> storeOnCognito(final SerializedResult result) {
        return mCognitoManager.getMeasurementsDataset()
                .read(result.getType().getString())
                .flatMap(s -> {
                    // TODO: clean up with list serializing
                    if (s == null || s.isEmpty()) {
                        s = "[" + result.getSerializedValue() + "]";
                    } else {
                        s = s.replace("]", "," + result.getSerializedValue() + "]");
                    }
                    return mCognitoManager.getMeasurementsDataset().writeWithSync(result.getType().getString(), s);
                });
    }

    public Observable<Boolean> storeListOnCognito(final List<SerializedResult> resultList) {
        if (resultList.isEmpty()) {
            return Observable.just(true);
        }

        String type = resultList.get(0).getType().getString();
        return mCognitoManager.getMeasurementsDataset()
                .read(type)
                .flatMap(s -> {
                    String serialized = "[";
                    for (int i = 0; i < resultList.size(); i++) {
                        serialized += resultList.get(i).getSerializedValue();
                        if (i != resultList.size() - 1) {
                            serialized += ",";
                        }
                    }
                    serialized += "]";
                    if (s == null || s.isEmpty()) {
                        s = "[" + serialized + "]";
                    } else {
                        s = s.substring(0, s.length() - 1);
                        s += "," + serialized + "]";
                    }
                    return mCognitoManager.getMeasurementsDataset().writeWithSync(type, s);
                });
    }
}
