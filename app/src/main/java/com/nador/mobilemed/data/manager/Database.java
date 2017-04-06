package com.nador.mobilemed.data.manager;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by nador on 27/07/16.
 */
public abstract class Database {

    protected Context mContext;
    private RealmConfiguration mRealmConfiguration;

    public Database(final Context context, final RealmConfiguration.Builder realmConfigurationBuilder) {
        mContext = context;
        mRealmConfiguration = realmConfigurationBuilder.deleteRealmIfMigrationNeeded().build();
    }

    public void storeObjectPrimaryKey(final RealmObject object) {
        final Realm realm = Realm.getInstance(mRealmConfiguration);
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(object);
        realm.commitTransaction();
        realm.close();
    }

    public void storeObject(final RealmObject object) {
        final Realm realm = Realm.getInstance(mRealmConfiguration);
        realm.beginTransaction();
        realm.copyToRealm(object);
        realm.commitTransaction();
        realm.close();
    }

    public void storeObjectListPrimaryKey(final RealmList<? extends RealmObject> objectList) {
        final Realm realm = Realm.getInstance(mRealmConfiguration);
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(objectList);
        realm.commitTransaction();
        realm.close();
    }

    public void storeObjectList(final RealmList<? extends RealmObject> objectList) {
        final Realm realm = Realm.getInstance(mRealmConfiguration);
        realm.beginTransaction();
        realm.copyToRealm(objectList);
        realm.commitTransaction();
        realm.close();
    }

    public final Realm realm() {
        return Realm.getInstance(mRealmConfiguration);
    }
}
