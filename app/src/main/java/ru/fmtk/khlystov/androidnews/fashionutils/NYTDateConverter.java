package ru.fmtk.khlystov.androidnews.fashionutils;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import ru.fmtk.khlystov.androidnews.ContextUtils;
import ru.fmtk.khlystov.androidnews.R;

import static android.text.TextUtils.isEmpty;
import static android.text.format.DateFormat.is24HourFormat;

public class NYTDateConverter implements IDateConverter {

    private static final long FIVE_MINUTES_IN_MILLS = TimeUnit.MINUTES.toMillis(5L);
    private static final long AN_HOUR_IN_MILLS = TimeUnit.HOURS.toMillis(1L);
    private static final long DAY_NIGHT_IN_MILLS = TimeUnit.DAYS.toMillis(1L);
    private static final long TWO_DAY_NIGHT_IN_MILLS = 2L * DAY_NIGHT_IN_MILLS;
    private static final long MIN_MONTH_IN_MILLS = 29L * DAY_NIGHT_IN_MILLS;

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
        is24HourFormat = is24HourFormat(context);
        dateFormat = android.text.format.DateFormat.getDateFormat(context);
        currentLocale = ContextUtils.getCurrentLocale(context);
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
        long duration_ms = Math.abs(nowLong - dateLong);
        String timeString = " " + convertByFormat(date, getTimeFormat());
        if (duration_ms < FIVE_MINUTES_IN_MILLS) {
            return String.format(justNowFormat, timeString);
        } else if (duration_ms <= AN_HOUR_IN_MILLS) {
            return String.format(anHourFormat, timeString);
        } else if (duration_ms < DAY_NIGHT_IN_MILLS) {
            long hoursAgo = duration_ms / AN_HOUR_IN_MILLS;
            return String.format(someHoursFormat, hoursAgo, timeString);
        } else if (duration_ms < TWO_DAY_NIGHT_IN_MILLS) {
            return String.format(yesterdayFormat, timeString);
        } else if (duration_ms < MIN_MONTH_IN_MILLS) {
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
