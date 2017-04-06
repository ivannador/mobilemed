package com.nador.mobilemed.data.manager;

import android.content.Context;

import com.nador.mobilemed.data.entity.Appointment;

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
public class AppointmentManager extends Database {

    public AppointmentManager(final Context context, final String userId) {
        super(context,
                new RealmConfiguration.Builder(context)
                        .name(userId + "-appointments.realm"));
    }

    public Observable<Boolean> storeAppointment(final Appointment appointment) {
        return Observable.fromCallable(() -> {
            storeObject(appointment);
            return true;
        });
    }

    public Observable<Boolean> storeAppointmentList(final List<Appointment> appointmentList) {
        return Observable.fromCallable(() -> {
            RealmList<Appointment> appointmentRealmList = new RealmList<>();
            for (Appointment appointment : appointmentList) {
                appointmentRealmList.add(appointment);
            }
            storeObjectList(appointmentRealmList);
            return true;
        });
    }

    public Observable<ArrayList<Appointment>> getAppointmentList() {
        return Observable.fromCallable(() -> {
            final Realm realm = realm();
            final RealmResults<Appointment> results = realm.where(Appointment.class).findAll();
            ArrayList<Appointment> appointments = new ArrayList<>();
            for (Appointment appointment : results) {
                appointments.add(new Appointment(appointment));
            }
            return appointments;
        });
    }

    public Observable<Appointment> getAppointment(final long primaryKey) {
        return Observable.fromCallable(() -> {
            final Realm realm = realm();
            final Appointment appointment = realm.where(Appointment.class).equalTo("mSendingDate", primaryKey).findFirst();
            if (appointment != null) {
                return new Appointment(appointment);
            }
            return new Appointment();
        });
    }

    public Observable<Boolean> editAppointment(final Appointment appointment) {
        return Observable.fromCallable(() -> {
            final Realm realm = realm();
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(appointment);
            realm.commitTransaction();
            realm.close();
            return true;
        });
    }
}
