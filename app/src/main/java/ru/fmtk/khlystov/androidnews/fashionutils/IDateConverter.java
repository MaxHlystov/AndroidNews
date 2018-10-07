package ru.fmtk.khlystov.androidnews.fashionutils;

import android.support.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.Date;


public interface IDateConverter {
    @NotNull String convert(@Nullable Date date);
}

