package com.nador.mobilemed.data.manager;

import android.content.Context;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.nador.mobilemed.data.aws.S3Manager;
import com.nador.mobilemed.data.entity.measurement.ECGGraph;
import com.nador.mobilemed.data.entity.measurement.ECGMeasurements;
import com.nador.mobilemed.data.entity.measurement.Result;
import com.nador.mobilemed.data.entity.measurement.SerializedResult;
import com.nador.mobilemed.data.measurement.ECG;
import com.nador.mobilemed.data.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import rx.Observable;
import timber.log.Timber;

/**
 * Created by nador on 09/08/16.
 */
public abstract class HistoryManager extends Database {

    public HistoryManager(final Context context, final String userId) {
        super(context,
                new RealmConfiguration.Builder(context)
                        .name(userId + "-measurement.realm"));
    }

    public abstract Observable<ArrayList<Result>> getHistory();

    protected Observable<ArrayList<Result>> getTypedHistory(SerializedResult.Type type) {
        return Observable.fromCallable(() -> {
            final Realm realm = realm();
            final RealmResults<SerializedResult> results =
                    realm.where(SerializedResult.class).equalTo("mEnumDescription", type.toString()).findAll();
            ArrayList<Result> resultList = new ArrayList<>();
            for (SerializedResult result : results) {
                resultList.add(result.deserialize());
            }
            return resultList;
        });
    }

    public static class BloodPressureHistory extends HistoryManager {

        public BloodPressureHistory(final Context context, final String userId) {
            super(context, userId);
        }

        @Override
        public Observable<ArrayList<Result>> getHistory() {
            return getTypedHistory(SerializedResult.Type.BLOODPRESSURE);
        }
    }

    public static class TemperatureHistory extends HistoryManager {

        public TemperatureHistory(final Context context, final String userId) {
            super(context, userId);
        }

        @Override
        public Observable<ArrayList<Result>> getHistory() {
            return getTypedHistory(SerializedResult.Type.TEMPERATURE);
        }
    }

    public static class WeightHistory extends HistoryManager {

        public WeightHistory(final Context context, final String userId) {
            super(context, userId);
        }

        @Override
        public Observable<ArrayList<Result>> getHistory() {
            return getTypedHistory(SerializedResult.Type.WEIGHT);
        }
    }

    public static class GlucometerHistory extends HistoryManager {

        public GlucometerHistory(final Context context, final String userId) {
            super(context, userId);
        }

        @Override
        public Observable<ArrayList<Result>> getHistory() {
            return getTypedHistory(SerializedResult.Type.GLUCOSE);
        }
    }

    public static class ECGHistory extends HistoryManager {

        private static final String ECG_DIRECTORY = "/EcgData";
        // FIXME: do not hardcode, it's device specific
        private static final String ZIP_PASSWORD = "i4&gmX5iyBOL";

        private S3Manager mS3Manager;

        public ECGHistory(final Context context, final String userId, final S3Manager s3Manager) {
            super(context, userId);
            mS3Manager = s3Manager;
        }

        @Override
        public Observable<ArrayList<Result>> getHistory() {

            final String ecgHistoryDir = mContext.getFilesDir().getAbsolutePath() + ECG_DIRECTORY;
            final String bucket = mS3Manager.getAPIConstants().getAWSS3ECGBucket();
            return mS3Manager.listBucketContent(bucket, "")
                    .flatMap(s3ObjectSummaries -> {
                        if (s3ObjectSummaries.isEmpty()) {
                            return Observable.just(false);
                        }

                        FileUtils.touchDirectory(ecgHistoryDir);

                        File dir = new File(ecgHistoryDir);
                        if (dir.list().length != 0 && dir.list().length == s3ObjectSummaries.size()) {
                            Timber.d("ECG directory not empty, returning...");
                            return Observable.just(true);
                        }

                        FileUtils.deleteFileRecursive(dir);
                        FileUtils.touchDirectory(ecgHistoryDir);
                        List<Observable<File>> downloads = new ArrayList<>();
                        for (S3ObjectSummary summary : s3ObjectSummaries) {
                            File file = FileUtils.getTempFile(mContext, summary.getKey());
                            downloads.add(mS3Manager.download(bucket, summary.getKey(), file));
                        }

                        return Observable.zip(downloads, results -> {
                            for (int i = 0; i < results.length; i++) {
                                File file = (File) results[i];
                                String resultDir = ecgHistoryDir + "/" + FileUtils.getFileNameFirstPart(file);
                                FileUtils.touchDirectory(resultDir);
                                FileUtils.unzip(file.getAbsolutePath(), resultDir, ZIP_PASSWORD);
                                file.delete();
                            }
                            return true;
                        });
                    })
                    .flatMap(aBoolean -> {

                        final Realm realm = realm();
                        final RealmResults<ECGMeasurements> results =
                                realm.where(ECGMeasurements.class).findAll();

                        ArrayList<ECGMeasurements> measurements = new ArrayList<>();
                        for (ECGMeasurements contact : results) {
                            measurements.add(new ECGMeasurements(contact));
                        }

                        ArrayList<Result> values = new ArrayList<>();
                        if (aBoolean) {
                            File ecgDir = new File(ecgHistoryDir);
                            for (String fileName : ecgDir.list()) {
                                ECGGraph graph = ECG.getGraphFromDir(new File(ecgHistoryDir + "/" + fileName));
                                if (graph != null) {
                                    for (ECGMeasurements meas : measurements) {
                                        if (meas.getECGGraphName().equals(graph.getName())) {
                                            graph.setECGMeasurements(meas);
                                            break;
                                        }
                                    }
                                    values.add(graph);
                                }
                            }
                        }
                        return Observable.just(values);
                    });
        }
    }
}
