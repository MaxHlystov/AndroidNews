package ru.fmtk.khlystov.androidnews.fashionutils;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.fmtk.khlystov.androidnews.R;

import static android.text.TextUtils.isEmpty;

public class NYTDateConverter implements IDateConverter {

    private final boolean is24HourFormat;

    @NonNull
    private final DateFormat dateFormat;

    @NonNull
    private final Locale currentLocale;

    @NonNull
    private final String justNowFormat;

    @NonNull
    private final String anHourFormat;

    @NonNull
    private final String someHoursFormat;

    @NonNull
    private final String yesterdayFormat;

    @NonNull
    private final String dayOfMonthFormat;

    public NYTDateConverter(@NonNull Context context) {
        is24HourFormat = android.text.format.DateFormat.is24HourFormat(context);
        dateFormat = android.text.format.DateFormat.getDateFormat(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            currentLocale = context.getResources().getConfiguration().getLocales().get(0);
        }
        else {
            currentLocale = context.getResources().getConfiguration().locale;
        }
        justNowFormat = context.getString(R.string.nyt_data_converter__just_now_format);
        anHourFormat = context.getString(R.string.nyt_data_converter__an_hour_format);
        someHoursFormat = context.getString(R.string.nyt_data_converter__some_hours_format);
        yesterdayFormat = context.getString(R.string.nyt_data_converter__yesterday_format);
        dayOfMonthFormat = context.getString(R.string.nyt_data_converter__day_of_month_format);
    }

    @NonNull
    @Override
    public String convert(@Nullable Date date) {
        if (date != null) {
            return convertNN(date);
        }
        return "";
    }

    @NonNull
    private String convertNN(@NonNull Date date) {
        Date now = new Date();
        long nowLong = now.getTime();
        long dateLong = date.getTime();
        // it may be the date of a publication in future, just for fun
        long duration = Math.abs(nowLong - dateLong);
        String timeString = " " + convertByFormat(date, getTimeFormat());
        if (duration < 5L * 60L * 1000L) {
            return String.format(justNowFormat, timeString);
        } else if (duration <= 60L * 60L * 1000L) {
            return String.format(anHourFormat, timeString);
        } else if (duration < 24L * 60L * 60L * 1000L) {
            long hoursAgo = duration / 1000L / 60L / 60L;
            return String.format(someHoursFormat, hoursAgo, timeString);
        } else if (duration < 48L * 60L * 60L * 1000L) {
            return String.format(yesterdayFormat, timeString);
        } else if (duration < 29L * 24L * 60L * 60L * 1000L) {
            return String.format(dayOfMonthFormat, timeString);
        }
        return dateFormat.format(date);
    }

    @NonNull
    private String convertByFormat(@NonNull Date date, @Nullable String format) {
        if (!isEmpty(format)) {
            DateFormat df = new SimpleDateFormat(format, currentLocale);
            return df.format(date);
        }
        return "";
    }

    @NonNull
    private String getTimeFormat() {
        if (is24HourFormat) {
            return "HH:mm";
        }
        return "hh:mm a";
    }
}
