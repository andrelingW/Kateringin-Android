package project.skripsi.kateringin.Util.UtilClass;

import android.os.Parcel;

import androidx.annotation.NonNull;

import com.google.android.material.datepicker.CalendarConstraints;

import java.util.Calendar;

public class CustomBeforeDateValidator implements CalendarConstraints.DateValidator {


    @Override
    public boolean isValid(long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        // Disable dates after today
        return calendar.before(Calendar.getInstance());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {

    }

    public static final Creator<CustomBeforeDateValidator> CREATOR = new Creator<CustomBeforeDateValidator>() {
        @Override
        public CustomBeforeDateValidator createFromParcel(Parcel in) {
            return new CustomBeforeDateValidator();
        }

        @Override
        public CustomBeforeDateValidator[] newArray(int size) {
            return new CustomBeforeDateValidator[size];
        }
    };
}
