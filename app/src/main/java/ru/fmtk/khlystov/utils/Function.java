package ru.fmtk.khlystov.utils;

public interface Function<T, R> {
    R apply(T data);
}