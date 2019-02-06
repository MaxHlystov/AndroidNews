package ru.fmtk.khlystov.newsgetter.database;

import android.arch.persistence.room.TypeConverter;
import android.support.annotation.Nullable;

import java.util.Date;

public class DateConverter {
    @TypeConverter
    @Nullable
    public static Date toDate(@Nullable Long dateLong) {
        return dateLong == null ? null : new Date(dateLong);
    }

    @TypeConverter
    @Nullable
    public static Long fromDate(@Nullable Date date) {
        return date == null ? null : date.getTime();
    }
}
