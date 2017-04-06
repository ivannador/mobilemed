package com.nador.mobilemed.data.aws;

import android.app.Application;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.nador.mobilemed.data.APIConstants;

import java.io.File;
import java.net.URL;
import java.util.List;

import rx.Observable;
import timber.log.Timber;

/**
 * Created by nador on 02/08/16.
 */
public class S3Manager {

    private APIConstants mAPIConstants;
    private AmazonS3Client mS3Client;
    private TransferUtility mTransferUtility;

    public S3Manager(Application applicationContext, CognitoCachingCredentialsProvider credentialsProvider, APIConstants apiConstants) {
        mAPIConstants = apiConstants;
        mS3Client = new AmazonS3Client(credentialsProvider);
        mS3Client.setRegion(Region.getRegion(Regions.fromName(mAPIConstants.getAWSS3Region())));
        mTransferUtility = new TransferUtility(mS3Client, applicationContext);
    }

    public APIConstants getAPIConstants() {
        return mAPIConstants;
    }

    public URL getURL(final String bucket, final String fileName) {
        final GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, fileName);
        return mS3Client.generatePresignedUrl(request);
    }

    public Observable<File> download(final String bucket, final String objectName, File targetFile) {
        return Observable.create(subscriber -> {
            TransferObserver observer = mTransferUtility.download(bucket, objectName, targetFile);
            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    Timber.d("Transfer state: %s", state.toString());
                    if (state == TransferState.COMPLETED) {
                        Timber.d("Download completed.");
                        subscriber.onNext(targetFile);
                        subscriber.onCompleted();
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    long percentage = (bytesCurrent / (bytesTotal == 0 ? -1 : bytesTotal)) * 100;
                    Timber.d("%s download progress: %d %%", objectName, percentage);
                }

                @Override
                public void onError(int id, Exception ex) {
                    Timber.e(ex, "Download error.");
                    subscriber.onError(ex);
                }
            });
        });
    }

    public Observable<List<S3ObjectSummary>> listBucketContent(final String bucket, final String prefix) {
        return Observable.fromCallable(() -> {
            ObjectListing listing = mS3Client.listObjects(bucket, prefix);
            List<S3ObjectSummary> summaryList = listing.getObjectSummaries();

            while (listing.isTruncated()) {
                listing = mS3Client.listNextBatchOfObjects (listing);
                summaryList.addAll(listing.getObjectSummaries());
            }
            return summaryList;
        });
    }
}
