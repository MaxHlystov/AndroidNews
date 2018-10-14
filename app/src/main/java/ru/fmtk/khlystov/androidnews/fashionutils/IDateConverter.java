package ru.fmtk.khlystov.androidnews.fashionutils;

import android.support.annotation.Nullable;
import android.support.annotation.NonNull;


import java.util.Date;


public interface IDateConverter {
    @NonNull String convert(@Nullable Date date);
}

