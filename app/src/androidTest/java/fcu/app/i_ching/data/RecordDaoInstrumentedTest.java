package fcu.app.i_ching.data;

import android.content.Context;

import androidx.room.Room;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class RecordDaoInstrumentedTest {
    private IChingDatabase database;
    private DivinationRecordDao dao;

    @Before
    public void setUp() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        database = Room.inMemoryDatabaseBuilder(context, IChingDatabase.class)
                .allowMainThreadQueries()
                .build();
        dao = database.recordDao();
    }

    @After
    public void tearDown() {
        database.close();
    }

    @Test
    public void upsertUpdateDeleteAllRecords() {
        DivinationRecord record = new DivinationRecord(1L, "測試問題", 15, 55, DivinationMethod.COINS,
                new int[]{6, 8, 7, 6, 8, 8}, Arrays.asList(1, 4), 1L, "");

        dao.upsert(DivinationRecordEntity.fromRecord(record));
        assertEquals(1, dao.recordsNow().size());

        assertEquals(1, dao.updateNote(1L, "更新筆記"));
        assertEquals("更新筆記", dao.find(1L).note);

        dao.deleteAll();
        assertTrue(dao.recordsNow().isEmpty());
    }
}
