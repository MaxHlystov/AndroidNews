package ru.fmtk.khlystov.newsgetter;

import android.support.annotation.Nullable;

import java.io.IOException;

public interface INewsSupplier {
    @Nullable
    String get() throws IOException;
}