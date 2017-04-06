package com.nador.mobilemed.presentation.function.management.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nador.mobilemed.data.entity.Medication;
import com.nador.mobilemed.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by nador on 20/07/16.
 */
public class MedicationListAdapter extends RecyclerView.Adapter<MedicationListAdapter.ViewHolder> {

    private Context mContext;
    private List<Medication> mMedicationList;

    public MedicationListAdapter(final Context context) {
        mContext = context;
    }

    @Override
    public MedicationListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.listitem_medication, parent, false);
        return new MedicationListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MedicationListAdapter.ViewHolder holder, int position) {
        final Medication medication = mMedicationList.get(position);
        holder.mMedicationNameTextView.setText(medication.getName());

        holder.mMedicationListItemContainer.setBackgroundColor(ContextCompat.getColor(mContext, (position % 2 == 0) ? android.R.color.white : R.color.colorWhiteOff));
    }

    @Override
    public int getItemCount() {
        return (mMedicationList == null) ? 0 : mMedicationList.size();
    }

    public void setDataList(final List<Medication> medicationList) {
        mMedicationList = medicationList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.medicationListItemContainer)
        ViewGroup mMedicationListItemContainer;
        @Bind(R.id.medicationNameTextView)
        TextView mMedicationNameTextView;
        @Bind(R.id.timesOfDayTextView)
        TextView mTimesOfDayTextView;
        @Bind(R.id.startDateTextView)
        TextView mStartDateTextView;
        @Bind(R.id.endDateTextView)
        TextView mEndDateTextView;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
