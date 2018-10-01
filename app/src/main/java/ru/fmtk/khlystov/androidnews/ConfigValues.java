package ru.fmtk.khlystov.androidnews;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ConfigValues implements IConfigValues {

    @Nullable
    private static IConfigValues config = null;

    @NonNull
    public static IConfigValues getConfig() {
        if(config == null) {
            config = new ConfigValues();
        }
        return config;
    }

    private ConfigValues() {}

    @NonNull
    @Override
    public String getMyEmail() {
        return "maxvls@gmail.com";
    }

    @NonNull
    @Override
    public SocialNetwork getURLTelegram() {
        return SocialNetwork.TELEGRAM;
    }

    @NonNull
    @Override
    public SocialNetwork getURLGithub() {
        return SocialNetwork.GITHUB;
    }

    @NonNull
    @Override
    public SocialNetwork getURLLinkedin() { return SocialNetwork.LINKEDIN; }

    @NonNull
    @Override
    public SocialNetwork getURLStepik() {
        return SocialNetwork.STEPIK;
    }
}
