package fcu.app.i_ching.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import java.util.ArrayList;

@Entity(tableName = "divination_records", primaryKeys = {"accountId", "id"})
public class DivinationRecordEntity {
    @NonNull
    public String accountId = AccountStore.GUEST_ACCOUNT_ID;

    public long id;

    @NonNull
    public String question = "";

    public int hexagramNumber;
    public int relatingHexagramNumber;

    @NonNull
    public String method = DivinationMethod.COINS.name();

    @NonNull
    public int[] lineValues = new int[0];

    @NonNull
    public java.util.List<Integer> changingLines = new ArrayList<>();

    public long createdAt;

    @NonNull
    public String note = "";

    public DivinationRecordEntity() {}

    public static DivinationRecordEntity fromRecord(DivinationRecord record) {
        return fromRecord(record, AccountStore.GUEST_ACCOUNT_ID);
    }

    public static DivinationRecordEntity fromRecord(DivinationRecord record, String accountId) {
        DivinationRecordEntity entity = new DivinationRecordEntity();
        entity.accountId = accountId == null || accountId.isEmpty() ? AccountStore.GUEST_ACCOUNT_ID : accountId;
        entity.id = record.id;
        entity.question = record.question == null ? "" : record.question;
        entity.hexagramNumber = record.hexagramNumber;
        entity.relatingHexagramNumber = record.relatingHexagramNumber;
        entity.method = record.method == null ? DivinationMethod.COINS.name() : record.method.name();
        entity.lineValues = record.lineValues == null ? new int[0] : record.lineValues;
        entity.changingLines = record.changingLines == null ? new ArrayList<>() : new ArrayList<>(record.changingLines);
        entity.createdAt = record.createdAt;
        entity.note = record.note == null ? "" : record.note;
        return entity;
    }

    public DivinationRecord toRecord() {
        return new DivinationRecord(
                id,
                question,
                hexagramNumber,
                relatingHexagramNumber,
                DivinationResult.methodFromName(method),
                lineValues,
                changingLines,
                createdAt,
                note
        );
    }
}
