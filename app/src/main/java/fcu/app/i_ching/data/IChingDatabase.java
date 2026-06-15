package fcu.app.i_ching.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {DivinationRecordEntity.class}, version = 1, exportSchema = false)
@TypeConverters({RecordTypeConverters.class})
public abstract class IChingDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = "i_ching_records.db";

    private static volatile IChingDatabase instance;

    public abstract DivinationRecordDao recordDao();

    public static IChingDatabase get(Context context) {
        if (instance == null) {
            synchronized (IChingDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    IChingDatabase.class,
                                    DATABASE_NAME
                            )
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return instance;
    }
}
