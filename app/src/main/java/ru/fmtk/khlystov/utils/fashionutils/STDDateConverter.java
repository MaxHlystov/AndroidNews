package ru.fmtk.khlystov.utils.fashionutils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;

import java.lang.ref.WeakReference;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.text.format.DateUtils.FORMAT_ABBREV_RELATIVE;

public class STDDateConverter implements IDateConverter {

    @NonNull
    private final WeakReference<Context> weakContext;

    public STDDateConverter(@NonNull Context context) {
        this.weakContext = new WeakReference<>(context);
    }

    @NonNull
    @Override
    public String convert(@Nullable Date date) {
        if(date == null) return "";
        Context context = weakContext.get();
        if(context != null) {
            return DateUtils.getRelativeDateTimeString(context,
                    date.getTime(),
                    DateUtils.SECOND_IN_MILLIS,
                    DateUtils.WEEK_IN_MILLIS,
                    FORMAT_ABBREV_RELATIVE)
                    .toString();
        }
        return date.toString();
    }

    @Nullable
    public static Date unconvert(@Nullable String dateString,
                                    @Nullable String pattern) {
        if (dateString == null) {
            return null;
        }
        ParsePosition pos = new ParsePosition(0);
        SimpleDateFormat simpledateformat;
        if(pattern == null) {
            simpledateformat = new SimpleDateFormat();
        } else {
            simpledateformat = new SimpleDateFormat(pattern, Locale.getDefault());
        }
        return simpledateformat.parse(dateString, pos);

    }
}
