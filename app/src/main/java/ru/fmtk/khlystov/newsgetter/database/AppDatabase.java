package ru.fmtk.khlystov.newsgetter.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ru.fmtk.khlystov.newsgetter.NewsSection;

@Database(entities = {NewsEntity.class, SectionEntity.class},
        version = 1,
        exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    @NonNull
    private static final String LOG_TAG = "NewsAppAppDatabase";
    @NonNull
    private static final String DATABASE_NAME = "NewsDb.db";

    @Nullable
    private static AppDatabase appDatabase;

    public abstract NewsDAO newsDAO();

    public abstract SectionDAO newsSectionDAO();

    public abstract NewsAndSectionDAO newsAndSectionDAO();

    @NonNull
    public static AppDatabase getAppDatabase(Context context) {
        synchronized (AppDatabase.class) {
            if (appDatabase == null) {
                appDatabase = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class,
                        DATABASE_NAME)
                        .addCallback(new RoomDatabase.Callback() {
                            @Override
                            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                super.onCreate(db);
                                Log.d("MoviesDatabase", "populating with data...");
                                new PopulateDbAsync(appDatabase, NewsSection.getIds()).execute();
                            }
                        })
                        .build();
            }
        }
        return appDatabase;
    }

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {
        private final SectionDAO newsSectionDAO;
        private final List<String> newsSectionsWebIds;

        public PopulateDbAsync(AppDatabase instance, List<String> newsSectionsWebIds) {
            newsSectionDAO = instance.newsSectionDAO();
            this.newsSectionsWebIds = newsSectionsWebIds;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            newsSectionDAO.deleteAll();
            List<SectionEntity> sectionsEntitiesToInsert =
                    new ArrayList<>(newsSectionsWebIds.size());
            for (String sectionId : newsSectionsWebIds) {
                sectionsEntitiesToInsert.add(new SectionEntity(sectionId));
            }
            newsSectionDAO.insertAll(sectionsEntitiesToInsert);
            return null;
        }
    }
}
