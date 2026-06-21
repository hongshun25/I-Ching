package fcu.app.i_ching.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {DivinationRecordEntity.class}, version = 2, exportSchema = true)
@TypeConverters({RecordTypeConverters.class})
public abstract class IChingDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = "i_ching_records.db";

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `divination_records_new` ("
                    + "`accountId` TEXT NOT NULL, "
                    + "`id` INTEGER NOT NULL, "
                    + "`question` TEXT NOT NULL, "
                    + "`hexagramNumber` INTEGER NOT NULL, "
                    + "`relatingHexagramNumber` INTEGER NOT NULL, "
                    + "`method` TEXT NOT NULL, "
                    + "`lineValues` TEXT NOT NULL, "
                    + "`changingLines` TEXT NOT NULL, "
                    + "`createdAt` INTEGER NOT NULL, "
                    + "`note` TEXT NOT NULL, "
                    + "PRIMARY KEY(`accountId`, `id`))");
            database.execSQL("INSERT INTO `divination_records_new` "
                    + "(`accountId`, `id`, `question`, `hexagramNumber`, `relatingHexagramNumber`, "
                    + "`method`, `lineValues`, `changingLines`, `createdAt`, `note`) "
                    + "SELECT '" + AccountStore.GUEST_ACCOUNT_ID + "', `id`, `question`, "
                    + "`hexagramNumber`, `relatingHexagramNumber`, `method`, `lineValues`, "
                    + "`changingLines`, `createdAt`, `note` FROM `divination_records`");
            database.execSQL("DROP TABLE `divination_records`");
            database.execSQL("ALTER TABLE `divination_records_new` RENAME TO `divination_records`");
        }
    };

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
                            .addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        }
        return instance;
    }
}
