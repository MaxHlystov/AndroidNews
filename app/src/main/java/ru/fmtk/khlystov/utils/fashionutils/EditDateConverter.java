package ru.fmtk.khlystov.utils.fashionutils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;


public class EditDateConverter implements IDateConverter {

    @NonNull
    private static final String FORMAT_FOR_EDIT_DATE = "dd.MM.yyyy hh:mm:ss";

    @NonNull
    private final WeakReference<Context> weakContext;

    public EditDateConverter(@NonNull Context context) {
        this.weakContext = new WeakReference<>(context);
    }

    @NonNull
    @Override
    public String convert(@Nullable Date date) {
        if (date == null) return "";
        Context context = weakContext.get();
        if (context != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_FOR_EDIT_DATE);
            return simpleDateFormat.format(date);
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
        if (pattern == null) {
            simpledateformat = new SimpleDateFormat(FORMAT_FOR_EDIT_DATE);
        } else {
            simpledateformat = new SimpleDateFormat(pattern);
        }
        return simpledateformat.parse(dateString, pos);

    }
}
