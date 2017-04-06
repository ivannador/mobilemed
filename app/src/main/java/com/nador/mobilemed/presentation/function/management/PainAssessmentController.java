package com.nador.mobilemed.presentation.function.management;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nador.mobilemed.R;
import com.nador.mobilemed.data.entity.measurement.PainPoint;
import com.nador.mobilemed.presentation.base.BaseController;
import com.nador.mobilemed.presentation.presenter.function.management.PainAssessmentControllerPresenter;
import com.nador.mobilemed.presentation.widget.BulkySliderDialog;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by nador on 25/07/16.
 */
public class PainAssessmentController extends BaseController<IPainAssessmentController, PainAssessmentControllerPresenter> implements IPainAssessmentController {

    @Bind(R.id.painBodyImageView)
    ImageView mPainBodyImageView;
    @Bind(R.id.sendHintTextView)
    TextView mSendHintTextView;
    @Bind(R.id.sendButton)
    Button mSendButton;

    @OnClick(R.id.sendButton)
    protected void onSendButtonTapped() {
        showSentDialog();
    }

    private Bitmap mBodyBitmap;
    private PainNodePainter mPainNodePainter;

    private boolean mPainSelectorLock = false;

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return inflater.inflate(R.layout.controller_assessment_pain, container, false);
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        showSendViews(false);

        mPainNodePainter = new PainNodePainter(getActivity());
        mBodyBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image_pain_assessment_body);

        mPainBodyImageView.setDrawingCacheEnabled(true);
        mPainBodyImageView.setOnTouchListener((v, event) -> {
            if (!mPainSelectorLock && event.getAction() == MotionEvent.ACTION_UP) {
                final Bitmap bmp = Bitmap.createBitmap(v.getDrawingCache());
                int pixel = bmp.getPixel((int) event.getX(), (int) event.getY());
                if (pixel == Color.TRANSPARENT) {
                    return false;
                } else {
                    mPainSelectorLock = true;
                    final float[] coords = getTouchUpCoordinates(mPainBodyImageView, event);
                    showPainLevelDialog(coords[0], coords[1], coords[2], coords[3]);
                    return true;
                }
            } else {
                return true;
            }
        });
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        drawPainPoints(mPainBodyImageView, getPresenter().getPainPoints());
    }

    @NonNull
    @Override
    public PainAssessmentControllerPresenter createPresenter() {
        return new PainAssessmentControllerPresenter();
    }

    private AssessmentFunctionController getFunctionController() {
        return (AssessmentFunctionController) getParentController();
    }

    private void drawPainPoints(final ImageView imageView, final ArrayList<PainPoint> painPoints) {
        Bitmap tempBitmap = Bitmap.createBitmap(mBodyBitmap.getWidth(), mBodyBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas tempCanvas = new Canvas(tempBitmap);
        tempCanvas.drawBitmap(mBodyBitmap, 0, 0, null);

        for (PainPoint painPoint : painPoints) {
            mPainNodePainter.paintOnCanvas(tempCanvas, painPoint);
        }

        imageView.setImageBitmap(tempBitmap);
    }

    private void showPainLevelDialog(final float xCoord, final float yCoord, final float xMax, final float yMax) {
        final Context context = getActivity();
        BulkySliderDialog dialog = new BulkySliderDialog(context, context.getString(R.string.ASSESSMENT_DIALOG_HINT_2_PAIN));
        dialog.addAffirmativeAction(getActivity().getString(R.string.ASSESSMENT_DIALOG_BUTTON_OK), v -> {
            getPresenter().addPainPoint(new PainPoint(dialog.getSliderProgress(), xCoord, yCoord, xMax, yMax));
            drawPainPoints(mPainBodyImageView, getPresenter().getPainPoints());
            showSendViews(true);
            mPainSelectorLock = false;
            dialog.dismiss();
        });
        dialog.addCancelAction(v -> {
            mPainSelectorLock = false;
            dialog.dismiss();
        });
        dialog.show();
    }

    private void showSentDialog() {
        getFunctionController().assessmentDone(getPresenter().getPainPointsAsResult());
    }

    private void showSendViews(boolean show) {
        int visibility = show ? View.VISIBLE : View.GONE;
        mSendHintTextView.setVisibility(visibility);
        mSendButton.setVisibility(visibility);
    }

    private float[] getTouchUpCoordinates(final ImageView imageView, final MotionEvent motionEvent) {
        final float[] coords = new float[] { motionEvent.getX(), motionEvent.getY() };
        Matrix matrix = new Matrix();
        imageView.getImageMatrix().invert(matrix);
        matrix.postTranslate(imageView.getScrollX(), imageView.getScrollY());
        matrix.mapPoints(coords);

//        float[] f = new float[9];
//        imageView.getImageMatrix().getValues(f);
//
//        final float scaleX = f[Matrix.MSCALE_X];
//        final float scaleY = f[Matrix.MSCALE_Y];

        final Drawable d = imageView.getDrawable();
        final int origW = d.getIntrinsicWidth();
        final int origH = d.getIntrinsicHeight();

//        final float actW = origW * scaleX;
//        final float actH = origH * scaleY;

        final float[] data = new float[4];
        data[0] = coords[0];
        data[1] = coords[1];
        data[2] = origW;
        data[3] = origH;

        return data;
    }

    private static class PainNodePainter {
        private static final float STROKE_WIDTH = 5.f;
        private static final float TEXT_SIZE = 15.f;
        private static final float NODE_SIZE = 40.f;

        private Paint mFillPaint;
        private Paint mStrokePaint;
        private Paint mTextPaint;

        private float mDensity;

        private Context mContext;

        public PainNodePainter(final Context context) {
            mContext = context;
            mDensity = mContext.getResources().getDisplayMetrics().density;

            mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mFillPaint.setColor(ContextCompat.getColor(mContext, R.color.colorRedMedium));
            mFillPaint.setStyle(Paint.Style.FILL);

            mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mStrokePaint.setColor(ContextCompat.getColor(mContext, R.color.colorWhiteOff));
            mStrokePaint.setStrokeWidth(mDensity * STROKE_WIDTH);
            mStrokePaint.setStyle(Paint.Style.STROKE);

            mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mTextPaint.setColor(ContextCompat.getColor(mContext, R.color.colorWhiteOff));
            mTextPaint.setTextSize(mDensity * TEXT_SIZE);
            mTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        }

        public void setFillColor(int fillColor) {
            if (mFillPaint != null) {
                mFillPaint.setColor(fillColor);
            }
        }

        public void paintOnCanvas(final Canvas canvas, final PainPoint painPoint) {
            float nodeSize = mDensity * NODE_SIZE;
            float strokeWidth = mDensity * STROKE_WIDTH;

            Rect numberBounds = new Rect();
            String nodeNumber = String.valueOf(painPoint.getLevel());
            mTextPaint.getTextBounds(nodeNumber, 0, nodeNumber.length(), numberBounds);

            canvas.drawCircle(
                    painPoint.getXCoord(),
                    painPoint.getYCoord(),
                    nodeSize / 2 - strokeWidth,
                    mFillPaint);
            canvas.drawCircle(
                    painPoint.getXCoord(),
                    painPoint.getYCoord(),
                    nodeSize / 2 - strokeWidth,
                    mStrokePaint);

            canvas.drawText(nodeNumber, painPoint.getXCoord() - numberBounds.centerX(), painPoint.getYCoord() - numberBounds.centerY(), mTextPaint);
        }
    }
}
