package fcu.app.i_ching.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DivinationRecordDao {
    @Query("SELECT * FROM divination_records ORDER BY createdAt DESC")
    LiveData<List<DivinationRecordEntity>> records();

    @Query("SELECT * FROM divination_records ORDER BY createdAt DESC")
    List<DivinationRecordEntity> recordsNow();

    @Query("SELECT * FROM divination_records WHERE id = :id LIMIT 1")
    DivinationRecordEntity find(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(DivinationRecordEntity record);

    @Query("UPDATE divination_records SET note = :note WHERE id = :id")
    int updateNote(long id, String note);

    @Query("DELETE FROM divination_records WHERE id = :id")
    int deleteById(long id);

    @Query("DELETE FROM divination_records")
    void deleteAll();
}
