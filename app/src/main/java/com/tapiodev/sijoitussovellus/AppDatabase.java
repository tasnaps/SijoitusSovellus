package com.tapiodev.sijoitussovellus;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Investment.class}, version = 2, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract InvestmentDao investmentDao();

    public static synchronized AppDatabase getDatabase(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "investments_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
