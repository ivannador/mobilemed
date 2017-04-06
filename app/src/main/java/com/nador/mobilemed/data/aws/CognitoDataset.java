package com.nador.mobilemed.data.aws;

import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.mobileconnectors.cognito.Record;
import com.amazonaws.mobileconnectors.cognito.SyncConflict;
import com.amazonaws.mobileconnectors.cognito.exceptions.DataStorageException;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import timber.log.Timber;

/**
 * Created by nador on 26/05/16.
 */
public class CognitoDataset {

    private final String mDatasetName;
    private Dataset mDataset;

    CognitoSyncManager mSyncManager;

    public CognitoDataset(String datasetName, CognitoSyncManager syncManager) {
        mDatasetName = datasetName;
        mSyncManager = syncManager;
    }

    public void open() {
        mDataset = mSyncManager.openOrCreateDataset(mDatasetName);
    }

    public void close() {
        mDataset = null;
    }

    public Observable<Boolean> writeWithSync(String key, String data) {
        return write(key, data).flatMap(aBoolean -> synchronize());
    }

    public Observable<Boolean> write(String key, String data) {
        return Observable.fromCallable(() -> {
            if (mDataset == null) {
                open();
            }

            mDataset.put(key, data);
            return true;
        });
    }

    public Observable<String> read(String key) {
        return Observable.fromCallable(() -> {
            if (mDataset == null) {
                open();
            }

            return mDataset.get(key);
        });
    }

    public Observable<Boolean> synchronize() {
        if (mDataset == null) {
            open();
        }

        return Observable.create(subscriber -> {
            mDataset.synchronize(new Dataset.SyncCallback() {
                @Override
                public void onSuccess(Dataset dataset, List<Record> updatedRecords) {
                    Timber.d("Synchronization of dataset %s successful, updated records number: %s", mDatasetName, updatedRecords.size());
                    subscriber.onNext(true);
                    subscriber.onCompleted();
                }

                @Override
                public boolean onConflict(Dataset dataset, List<SyncConflict> conflicts) {
                    Timber.d("Synchronization of dataset %s conflicted, conflicting records number: %s", mDatasetName, conflicts.size());
                    Timber.d("onConflict resolved with DefaultSyncCallback's default implementation (last writer wins).");
                    List<Record> resolvedConflicts = new ArrayList<>(conflicts.size());
                    for (SyncConflict conflict : conflicts) {
                        resolvedConflicts.add(conflict.resolveWithLastWriterWins());
                    }
                    dataset.resolve(resolvedConflicts);

                    return true;
                }

                @Override
                public boolean onDatasetDeleted(Dataset dataset, String datasetName) {
                    Timber.d("Dataset %s deleted, deleting local copy", datasetName);
                    return true;
                }

                @Override
                public boolean onDatasetsMerged(Dataset dataset, List<String> datasetNames) {
                    Timber.d("Dataset %s merged, dataset names:", mDatasetName);
                    for (String name : datasetNames) {
                        Timber.d(name);
                    }
                    return false;
                }

                @Override
                public void onFailure(DataStorageException dse) {
                    Timber.d("Synchronization of dataset %s unsuccessful, error is: %s", mDatasetName, dse.getMessage());
                    subscriber.onError(dse);
                }
            });
        });
    }
}
