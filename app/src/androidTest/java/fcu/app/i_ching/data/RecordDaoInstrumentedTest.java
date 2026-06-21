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
        DivinationRecord accountRecord = new DivinationRecord(1L, "帳號測試問題", 29, 29, DivinationMethod.SIMPLE,
                new int[]{8, 7, 8, 8, 7, 8}, Arrays.asList(), 2L, "");

        dao.upsert(DivinationRecordEntity.fromRecord(record, AccountStore.GUEST_ACCOUNT_ID));
        dao.upsert(DivinationRecordEntity.fromRecord(accountRecord, "account-a"));
        assertEquals(1, dao.recordsNow(AccountStore.GUEST_ACCOUNT_ID).size());
        assertEquals(1, dao.recordsNow("account-a").size());

        assertEquals(1, dao.updateNote("account-a", 1L, "更新筆記"));
        assertEquals("", dao.find(AccountStore.GUEST_ACCOUNT_ID, 1L).note);
        assertEquals("更新筆記", dao.find("account-a", 1L).note);

        assertEquals(1, dao.moveAccount(AccountStore.GUEST_ACCOUNT_ID, "account-b"));
        assertTrue(dao.recordsNow(AccountStore.GUEST_ACCOUNT_ID).isEmpty());
        assertEquals(1, dao.recordsNow("account-b").size());

        dao.deleteAll("account-a");
        assertTrue(dao.recordsNow("account-a").isEmpty());
        assertEquals(1, dao.recordsNow("account-b").size());
    }
}
