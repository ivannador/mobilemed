package com.nador.mobilemed.data.manager;

import android.content.Context;

import com.nador.mobilemed.data.entity.Medication;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;
import rx.Observable;

/**
 * Created by nador on 18/08/16.
 */
public class MedicationManager extends Database {

    public MedicationManager(final Context context, final String userId) {
        super(context,
                new RealmConfiguration.Builder(context)
                        .name(userId + "-medication.realm"));
    }

    public Observable<Boolean> storeMedication(final Medication medication) {
        return Observable.fromCallable(() -> {
            storeObject(medication);
            return true;
        });
    }

    public Observable<Boolean> storeMedicationList(final List<Medication> medicationList) {
        return Observable.fromCallable(() -> {
            RealmList<Medication> medicationRealmList = new RealmList<>();
            for (Medication medication : medicationList) {
                medicationRealmList.add(medication);
            }
            storeObjectList(medicationRealmList);
            return true;
        });
    }

    public Observable<ArrayList<Medication>> getMedicationList() {
        return Observable.fromCallable(() -> {
            final Realm realm = realm();
            final RealmResults<Medication> results = realm.where(Medication.class).findAll();
            ArrayList<Medication> medications = new ArrayList<>();
            for (Medication medication : results) {
                medications.add(new Medication(medication));
            }
            return medications;
        });
    }

    public Observable<Medication> getMedication(final String primaryKey) {
        return Observable.fromCallable(() -> {
            final Realm realm = realm();
            final Medication medication = realm.where(Medication.class).equalTo("mName", primaryKey).findFirst();
            if (medication != null) {
                return new Medication(medication);
            }
            return new Medication();
        });
    }

    public Observable<Boolean> editMedication(final Medication medication) {
        return Observable.fromCallable(() -> {
            final Realm realm = realm();
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(medication);
            realm.commitTransaction();
            realm.close();
            return true;
        });
    }
}
