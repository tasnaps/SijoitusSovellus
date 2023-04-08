package com.tapiodev.sijoitussovellus;
import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
@Database(entities ={CashFlow.class}, version = 2, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class SecondaryDatabase extends RoomDatabase {
    private static SecondaryDatabase instance;
    public abstract CashFlowDao cashFlowDao();
    public static synchronized SecondaryDatabase getDatabase(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    SecondaryDatabase.class, "cashflow_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
        }
    }

