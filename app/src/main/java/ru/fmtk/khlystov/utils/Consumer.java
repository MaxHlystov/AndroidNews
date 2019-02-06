package ru.fmtk.khlystov.utils;

public interface Consumer<T> {
    void apply(T data);
}