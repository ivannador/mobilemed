package com.nador.mobilemed.data.manager;

import android.content.Context;
import android.net.Uri;

import com.nador.mobilemed.R;
import com.nador.mobilemed.data.aws.S3Manager;

/**
 * Created by nador on 02/08/16.
 */
public class VideoManager {

    private Context mContext;
    private S3Manager mS3Manager;

    public VideoManager(final Context context, final S3Manager s3Manager) {
        mContext = context;
        mS3Manager = s3Manager;
    }

    public Uri getVideoURI() {
        // FIXME: do not hardcode video name
//        return mS3Manager.getURL(mS3Manager.getAPIConstants().getAWSS3MainBucket(), "pfizer.mp4");
        return Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.pfizer);
    }
}
