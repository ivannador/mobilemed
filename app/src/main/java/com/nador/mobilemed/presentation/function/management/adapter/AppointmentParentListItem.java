package com.nador.mobilemed.presentation.function.management.adapter;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.nador.mobilemed.data.entity.Appointment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nador on 19/07/16.
 */
public class AppointmentParentListItem implements ParentListItem {

    // ExpandableRecyclerView needs a child list too, use the same Appointment object for parent and child
    // and give it a single element list
    // A little hacky, but ExpandableRecyclerAdapter does not support notifyDataSetChanged()...
    private List<Appointment> mAppointmentList = new ArrayList<>();

    public AppointmentParentListItem(final Appointment appointment) {
        mAppointmentList.add(appointment);
    }

    public Appointment getAppointment() {
        return mAppointmentList.get(0);
    }

    @Override
    public List<?> getChildItemList() {
        return mAppointmentList;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}
