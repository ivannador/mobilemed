package com.nador.mobilemed.data.entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by nador on 18/07/16.
 */
public class Appointment extends RealmObject {

    public enum AppointmentStatus {
        AWAITING_DOCTOR, AWAITING_PATIENT, ACCEPTED, REJECTED, CANCELED
    }

    private String mEnumDescription;
    private String mDoctor;

    private String mComment;
    private String mDoctorComment;

    private long mAppointmentDate = 0;

    @PrimaryKey
    private long mSendingDate = 0;

    public Appointment() {}

    public Appointment(final AppointmentStatus appointmentStatus, final String doctor, final String comment, final long sendingDate) {
        saveStatusEnum(appointmentStatus);
        this.mDoctor = doctor;
        this.mComment = comment;
        this.mSendingDate = sendingDate;
    }

    public Appointment(final AppointmentStatus appointmentStatus, final String doctor, final String comment, final long appointmentDate, final long sendingDate) {
        saveStatusEnum(appointmentStatus);
        this.mDoctor = doctor;
        this.mComment = comment;
        this.mAppointmentDate = appointmentDate;
        this.mSendingDate = sendingDate;
    }

    public Appointment(final AppointmentStatus appointmentStatus, final String doctor, final String comment, final String doctorComment, final long appointmentDate, final long sendingDate) {
        saveStatusEnum(appointmentStatus);
        this.mDoctor = doctor;
        this.mComment = comment;
        this.mDoctorComment = doctorComment;
        this.mAppointmentDate = appointmentDate;
        this.mSendingDate = sendingDate;
    }

    // Copy constructor for RealmObject to Object mapping
    public Appointment(final Appointment appointment) {
        saveStatusEnum(appointment.getAppointmentStatus());
        this.mDoctor = appointment.getDoctor();
        this.mComment = appointment.getComment();
        this.mDoctorComment = appointment.getDoctorComment();
        this.mAppointmentDate = appointment.getAppointmentDate();
        this.mSendingDate = appointment.getSendingDate();
    }

    public AppointmentStatus getAppointmentStatus() {
        return getStatusEnum();
    }

    public void setAppointmentStatus(final AppointmentStatus appointmentStatus) {
        saveStatusEnum(appointmentStatus);
    }

    public String getDoctor() {
        return mDoctor;
    }

    public void setDoctor(final String doctor) {
        this.mDoctor = doctor;
    }

    public long getAppointmentDate() {
        return mAppointmentDate;
    }

    public void setAppointmentDate(final long appointmentDate) {
        this.mAppointmentDate = appointmentDate;
    }

    public long getSendingDate() {
        return mSendingDate;
    }

    public void setSendingDate(final long sendingDate) {
        this.mSendingDate = sendingDate;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(final String comment) {
        this.mComment = comment;
    }

    public String getDoctorComment() {
        return mDoctorComment;
    }

    public void setDoctorComment(final String doctorComment) {
        this.mDoctorComment = doctorComment;
    }

    public void saveStatusEnum(AppointmentStatus val) {
        mEnumDescription = val.toString();
    }

    public AppointmentStatus getStatusEnum() {
        return AppointmentStatus.valueOf(mEnumDescription);
    }
}
