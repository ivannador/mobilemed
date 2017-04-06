package com.nador.mobilemed.presentation.function.management.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.nador.mobilemed.data.entity.Appointment;
import com.nador.mobilemed.presentation.utils.DateUtil;
import com.nador.mobilemed.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;

/**
 * Created by nador on 19/07/16.
 */
public class AppointmentListAdapter extends ExpandableRecyclerAdapter<AppointmentListAdapter.AppointmentParentViewHolder, AppointmentListAdapter.AppointmentChildViewHolder> {

    public interface AppointmentStatusListener {
        void onAcceptClicked(final Appointment appointment);
        void onCancelClicked(final Appointment appointment);
        void onRejectClicked(final Appointment appointment);
    }

    private Context mContext;
    private LayoutInflater mInflater;

    private ArrayList<AppointmentParentListItem> mAppointmentList;

    private AppointmentStatusListener mListener;

    public AppointmentListAdapter(final Context context, final AppointmentStatusListener listener, final ArrayList<AppointmentParentListItem> appointmentList) {
        super(appointmentList);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mAppointmentList = appointmentList;
        mListener = listener;
    }

    @Override
    public AppointmentParentViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        View view = mInflater.inflate(R.layout.listitem_appointment_parent, parentViewGroup, false);
        return new AppointmentParentViewHolder(view);
    }

    @Override
    public void onBindParentViewHolder(AppointmentParentViewHolder parentViewHolder, int position, ParentListItem parentListItem) {
        Appointment appointment = ((AppointmentParentListItem) parentListItem).getAppointment();
        parentViewHolder.mAppointmentDateTextView.setText(DateUtil.getDateString(appointment.getAppointmentDate()));
        parentViewHolder.mDoctorTextView.setText(appointment.getDoctor());
        parentViewHolder.mSendingDateTextView.setText(DateUtils.getRelativeTimeSpanString(appointment.getSendingDate(), System.currentTimeMillis(), DateUtils.DAY_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE));
        switch (appointment.getAppointmentStatus()) {
            case AWAITING_DOCTOR:
                parentViewHolder.mStatusImageView.setImageResource(R.drawable.icon_status_waiting);
                parentViewHolder.mStatusTextView.setText(R.string.APPOINTMENT_TEXT_LIST_STATUS_AWAITING_DOCTOR);
                break;
            case AWAITING_PATIENT:
                parentViewHolder.mStatusImageView.setImageResource(R.drawable.icon_status_waiting);
                parentViewHolder.mStatusTextView.setText(R.string.APPOINTMENT_TEXT_LIST_STATUS_AWAITING_PATIENT);
                break;
            case ACCEPTED:
                parentViewHolder.mStatusImageView.setImageResource(R.drawable.icon_status_accepted);
                parentViewHolder.mStatusTextView.setText(R.string.APPOINTMENT_TEXT_LIST_STATUS_ACCEPTED);
                break;
            case CANCELED:
                parentViewHolder.mStatusImageView.setImageResource(R.drawable.icon_status_canceled);
                parentViewHolder.mStatusTextView.setText(R.string.APPOINTMENT_TEXT_LIST_STATUS_CANCELED);
                break;
            case REJECTED:
                parentViewHolder.mStatusImageView.setImageResource(R.drawable.icon_status_rejected);
                parentViewHolder.mStatusTextView.setText(R.string.APPOINTMENT_TEXT_LIST_STATUS_REJECTED);
                break;
        }

        parentViewHolder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, (position % 2 == 0) ? android.R.color.white : R.color.colorWhiteOff));
    }

    @Override
    public AppointmentChildViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        View view = mInflater.inflate(R.layout.listitem_appointment_child, childViewGroup, false);
        return new AppointmentChildViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(AppointmentChildViewHolder childViewHolder, int position, Object childListItem) {
        Appointment appointment = (Appointment) childListItem;
        childViewHolder.mAppointmentDateTextView.setText(DateUtil.getDateString(appointment.getAppointmentDate()));
        childViewHolder.mDoctorCommentTextView.setText(appointment.getDoctorComment());
        childViewHolder.mAcceptButton.setOnClickListener(v -> mListener.onAcceptClicked(appointment));
        childViewHolder.mCancelButton.setOnClickListener(v -> mListener.onCancelClicked(appointment));
        childViewHolder.mRejectButton.setOnClickListener(v -> mListener.onRejectClicked(appointment));
        switch (appointment.getAppointmentStatus()) {
            case AWAITING_DOCTOR:
                childViewHolder.mAcceptButton.setVisibility(View.INVISIBLE);
                childViewHolder.mCancelButton.setVisibility(View.VISIBLE);
                childViewHolder.mRejectButton.setVisibility(View.INVISIBLE);
                break;
            case AWAITING_PATIENT:
                childViewHolder.mAcceptButton.setVisibility(View.VISIBLE);
                childViewHolder.mCancelButton.setVisibility(View.VISIBLE);
                childViewHolder.mRejectButton.setVisibility(View.VISIBLE);
                break;
            case ACCEPTED:
                childViewHolder.mAcceptButton.setVisibility(View.INVISIBLE);
                childViewHolder.mCancelButton.setVisibility(View.VISIBLE);
                childViewHolder.mRejectButton.setVisibility(View.INVISIBLE);
                break;
            case CANCELED:
                childViewHolder.mAcceptButton.setVisibility(View.INVISIBLE);
                childViewHolder.mCancelButton.setVisibility(View.INVISIBLE);
                childViewHolder.mRejectButton.setVisibility(View.INVISIBLE);
                break;
            case REJECTED:
                childViewHolder.mAcceptButton.setVisibility(View.INVISIBLE);
                childViewHolder.mCancelButton.setVisibility(View.INVISIBLE);
                childViewHolder.mRejectButton.setVisibility(View.INVISIBLE);
                break;
        }

    }

    static class AppointmentParentViewHolder extends ParentViewHolder {

        private static final float INITIAL_POSITION = 0.0f;
        private static final float ROTATED_POSITION = 180f;

        @Bind(R.id.expandArrowContainer)
        ViewGroup mExpandArrowContainer;
        @Bind(R.id.expandArrowImageView)
        ImageView mExpandArrowImageView;
        @Bind(R.id.statusImageView)
        ImageView mStatusImageView;
        @Bind(R.id.statusTextView)
        TextView mStatusTextView;
        @Bind(R.id.appointmentDateTextView)
        TextView mAppointmentDateTextView;
        @Bind(R.id.doctorTextView)
        TextView mDoctorTextView;
        @Bind(R.id.sendingDateTextView)
        TextView mSendingDateTextView;

        @BindColor(R.color.colorBlueMedium)
        int mBlueMedium;
        @BindColor(R.color.colorGreyLight)
        int mGreyLight;

        public AppointmentParentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void setExpanded(boolean expanded) {
            super.setExpanded(expanded);
            if (expanded) {
                mExpandArrowContainer.setBackgroundColor(mBlueMedium);
                mExpandArrowImageView.setRotation(ROTATED_POSITION);
            } else {
                mExpandArrowContainer.setBackgroundColor(mGreyLight);
                mExpandArrowImageView.setRotation(INITIAL_POSITION);
            }
        }

        @Override
        public void onExpansionToggled(boolean expanded) {
            super.onExpansionToggled(expanded);
            RotateAnimation rotateAnimation;
            if (expanded) { // rotate clockwise
                rotateAnimation = new RotateAnimation(ROTATED_POSITION,
                        INITIAL_POSITION,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            } else { // rotate counterclockwise
                rotateAnimation = new RotateAnimation(-1 * ROTATED_POSITION,
                        INITIAL_POSITION,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            }

            rotateAnimation.setDuration(200);
            rotateAnimation.setFillAfter(true);
            mExpandArrowImageView.startAnimation(rotateAnimation);
        }
    }

    static class AppointmentChildViewHolder extends ChildViewHolder {

        @Bind(R.id.acceptButton)
        Button mAcceptButton;
        @Bind(R.id.cancelButton)
        Button mCancelButton;
        @Bind(R.id.rejectButton)
        Button mRejectButton;
        @Bind(R.id.appointmentDateTextView)
        TextView mAppointmentDateTextView;
        @Bind(R.id.doctorCommentTextView)
        TextView mDoctorCommentTextView;

        public AppointmentChildViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
