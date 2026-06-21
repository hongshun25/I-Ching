package fcu.app.i_ching.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DivinationRecordDao {
    @Query("SELECT * FROM divination_records WHERE accountId = :accountId ORDER BY createdAt DESC")
    LiveData<List<DivinationRecordEntity>> records(String accountId);

    @Query("SELECT * FROM divination_records WHERE accountId = :accountId ORDER BY createdAt DESC")
    List<DivinationRecordEntity> recordsNow(String accountId);

    @Query("SELECT * FROM divination_records WHERE accountId = :accountId AND id = :id LIMIT 1")
    DivinationRecordEntity find(String accountId, long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(DivinationRecordEntity record);

    @Query("UPDATE divination_records SET note = :note WHERE accountId = :accountId AND id = :id")
    int updateNote(String accountId, long id, String note);

    @Query("DELETE FROM divination_records WHERE accountId = :accountId AND id = :id")
    int deleteById(String accountId, long id);

    @Query("DELETE FROM divination_records")
    void deleteAll();

    @Query("DELETE FROM divination_records WHERE accountId = :accountId")
    void deleteAll(String accountId);

    @Query("UPDATE divination_records SET accountId = :targetAccountId WHERE accountId = :sourceAccountId")
    int moveAccount(String sourceAccountId, String targetAccountId);
}
