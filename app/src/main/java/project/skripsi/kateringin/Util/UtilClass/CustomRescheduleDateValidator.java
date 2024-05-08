package project.skripsi.kateringin.Util.UtilClass;

import android.os.Parcel;

import androidx.annotation.NonNull;

import com.google.android.material.datepicker.CalendarConstraints;

import java.util.Calendar;

public class CustomRescheduleDateValidator implements CalendarConstraints.DateValidator {


    @Override
    public boolean isValid(long date) {
        Calendar selected = Calendar.getInstance();
        Calendar filter = Calendar.getInstance();
        filter.add(Calendar.DATE, 2);
        selected.setTimeInMillis(date);

        return !selected.before(filter);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {

    }

    public static final Creator<CustomRescheduleDateValidator> CREATOR = new Creator<CustomRescheduleDateValidator>() {
        @Override
        public CustomRescheduleDateValidator createFromParcel(Parcel in) {
            return new CustomRescheduleDateValidator();
        }

        @Override
        public CustomRescheduleDateValidator[] newArray(int size) {
            return new CustomRescheduleDateValidator[size];
        }
    };
}
