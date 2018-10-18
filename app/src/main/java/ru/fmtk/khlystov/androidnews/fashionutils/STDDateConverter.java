package ru.fmtk.khlystov.androidnews.fashionutils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;

import java.lang.ref.WeakReference;
import java.util.Date;

public class STDDateConverter implements IDateConverter {

    @NonNull
    private final WeakReference<Context> weakContext;

    public STDDateConverter(@NonNull Context context) {
        this.weakContext = new WeakReference<>(context);
    }

    @NonNull
    @Override
    public String convert(@Nullable Date date) {
        Context context = weakContext.get();
        if(context != null) {
            return DateUtils.getRelativeDateTimeString(context,
                    date.getTime(),
                    DateUtils.SECOND_IN_MILLIS,
                    DateUtils.WEEK_IN_MILLIS,
                    0) // try FORMAT_ABBREV_RELATIVE
                    .toString();
        }
        return date.toString();
    }
}
