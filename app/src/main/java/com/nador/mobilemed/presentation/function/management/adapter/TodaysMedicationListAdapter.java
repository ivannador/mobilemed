package com.nador.mobilemed.presentation.function.management.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.nador.mobilemed.data.entity.MedicationTaking;
import com.nador.mobilemed.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by nador on 20/07/16.
 */
public class TodaysMedicationListAdapter extends RecyclerView.Adapter<TodaysMedicationListAdapter.ViewHolder> {

    private Context mContext;

    private List<MedicationTaking> mTodaysTakingList;
    private OnTakeMedicationClickListener mListener;

    public TodaysMedicationListAdapter(final Context context, final OnTakeMedicationClickListener listener) {
        mContext = context;
        mListener = listener;
    }

    @Override
    public TodaysMedicationListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.listitem_medication_today, parent, false);
        return new TodaysMedicationListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TodaysMedicationListAdapter.ViewHolder holder, int position) {
        final MedicationTaking taking = mTodaysTakingList.get(position);
        holder.mDateTextView.setText(taking.dateToString());
        holder.mTakeMedicationButton.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onTakeMedicationClicked();
            }
        });

        final String text = "<font color=#000000>" + taking.getName() + "</font>"
                + "<font color=#5cc3f3>" + " x" + String.valueOf(taking.getAmount()) + "</font>";
        holder.mMedicationTextView.setText(Html.fromHtml(text));

        holder.mTodaysMedicationListItemContainer.setBackgroundColor(ContextCompat.getColor(mContext, (position % 2 == 0) ? android.R.color.white : R.color.colorWhiteOff));
    }

    @Override
    public int getItemCount() {
        return (mTodaysTakingList == null) ? 0 : mTodaysTakingList.size();
    }

    public void setDataList(final List<MedicationTaking> todaysTakingList) {
        mTodaysTakingList = todaysTakingList;
        notifyDataSetChanged();
    }

    public interface OnTakeMedicationClickListener {
        void onTakeMedicationClicked();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.todaysMedicationListItemContainer)
        ViewGroup mTodaysMedicationListItemContainer;
        @Bind(R.id.dateTextView)
        TextView mDateTextView;
        @Bind(R.id.medicationTextView)
        TextView mMedicationTextView;
        @Bind(R.id.takeMedicationButton)
        Button mTakeMedicationButton;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
