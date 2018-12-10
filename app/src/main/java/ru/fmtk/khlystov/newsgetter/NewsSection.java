package ru.fmtk.khlystov.newsgetter;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public enum NewsSection {
    HOME("home"),
    OPINION("opinion"),
    WORLD("world"),
    NATIONAL("national"),
    POLITICS("politics"),
    UPSHOT("upshot"),
    NYREGION("nyregion"),
    BUSINESS("business"),
    TECHNOLOGY("technology"),
    SCIENCE("science"),
    HEALTH("health"),
    SPORTS("sports"),
    ARTS("arts"),
    BOOKS("books"),
    MOVIES("movies"),
    THEATER("theater"),
    SUNDAYREVIEW("sundayreview"),
    FASHION("fashion"),
    TMAGAZINE("tmagazine"),
    FOOD("food"),
    TRAVEL("travel"),
    MAGAZINE("magazine"),
    REALESTATE("realestate"),
    AUTOMOBILES("automobiles"),
    OBITUARIES("obituaries"),
    INSIDER("insider");

    @NonNull
    private static final Map<String, NewsSection> idToNewsSectionMap = new HashMap<>();

    @NonNull
    private final String id;

    NewsSection(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getID() {
        return id;
    }

    @NonNull
    public static NewsSection getByID(@NonNull String id) {
        if (idToNewsSectionMap.isEmpty()) {
            initMap();
        }
        NewsSection section = idToNewsSectionMap.get(id.toLowerCase());
        if(section != null) { return section; }
        return getDefault();
    }

    @NonNull
    public static NewsSection getDefault() {
        return HOME;
    }

    private static void initMap() {
        for (NewsSection section : NewsSection.values()) {
            idToNewsSectionMap.put(section.getID().toLowerCase(), section);
        }
    }
}