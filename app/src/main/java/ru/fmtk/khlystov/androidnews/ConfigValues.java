package ru.fmtk.khlystov.androidnews;

public class ConfigValues implements IConfigValues {

    public static IConfigValues getConfig() {
        if(config == null)
            config = new ConfigValues();
        return config;
    }

    @Override
    public String getMyEmail() {
        return "maxvls@gmail.com";
    }

    @Override
    public String getURL_Telegram() {
        return "https://t.me/max1c";
    }

    @Override
    public String getURL_Github() {
        return "https://github.com/MaxHlystov";
    }

    @Override
    public String getURL_Linkedin() {
        return "https://linkedin.com/pub/maxim-khlystov/a7/6ba/123";
    }

    @Override
    public String getURL_Stepik() {
        return "https://stepik.org/leaders/knowledge";
    }

    private ConfigValues() {}

    private static IConfigValues config = null;
}
