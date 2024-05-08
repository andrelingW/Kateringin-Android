package project.skripsi.kateringin.Util.UtilClass;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.android.material.datepicker.CalendarConstraints;

import java.util.Calendar;

public class CustomDateValidator implements CalendarConstraints.DateValidator {


    @Override
    public boolean isValid(long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        // Disable dates before today
        return calendar.after(Calendar.getInstance());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {

    }

    public static final Parcelable.Creator<CustomDateValidator> CREATOR = new Parcelable.Creator<CustomDateValidator>() {
        @Override
        public CustomDateValidator createFromParcel(Parcel in) {
            return new CustomDateValidator();
        }

        @Override
        public CustomDateValidator[] newArray(int size) {
            return new CustomDateValidator[size];
        }
    };
}
