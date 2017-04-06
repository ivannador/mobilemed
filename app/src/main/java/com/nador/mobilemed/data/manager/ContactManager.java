package com.nador.mobilemed.data.manager;

import android.content.Context;

import com.nador.mobilemed.data.entity.Contact;

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
public class ContactManager extends Database {

    public ContactManager(final Context context, final String userId) {
        super(context,
                new RealmConfiguration.Builder(context)
                        .name(userId + "-contacts.realm"));
    }

    public Observable<Boolean> storeContact(final Contact contact) {
        return Observable.fromCallable(() -> {
            storeObject(contact);
            return true;
        });
    }

    public Observable<Boolean> storeContactList(final List<Contact> contactList) {
        return Observable.fromCallable(() -> {
            RealmList<Contact> contactRealmList = new RealmList<>();
            for (Contact contact : contactList) {
                contactRealmList.add(contact);
            }
            storeObjectList(contactRealmList);
            return true;
        });
    }

    public Observable<ArrayList<Contact>> getContactList() {
        return Observable.fromCallable(() -> {
            final Realm realm = realm();
            final RealmResults<Contact> results = realm.where(Contact.class).findAll();
            ArrayList<Contact> contacts = new ArrayList<>();
            for (Contact contact : results) {
                contacts.add(new Contact(contact));
            }
            return contacts;
        });
    }

    public Observable<Contact> getContact(final String primaryKey) {
        return Observable.fromCallable(() -> {
            final Realm realm = realm();
            final Contact contact = realm.where(Contact.class).equalTo("mHash", primaryKey).findFirst();
            if (contact != null) {
                return new Contact(contact);
            }
            return new Contact();
        });
    }

    public Observable<Boolean> editContact(final Contact contact) {
        return Observable.fromCallable(() -> {
            final Realm realm = realm();
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(contact);
            realm.commitTransaction();
            realm.close();
            return true;
        });
    }
}
