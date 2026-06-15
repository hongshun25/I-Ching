package fcu.app.i_ching.ui.presentation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import fcu.app.i_ching.data.DivinationMethod;
import fcu.app.i_ching.data.DivinationRecord;
import fcu.app.i_ching.data.Hexagram;
import fcu.app.i_ching.data.HexagramRepository;

public final class RecordCardPresentation {
    public final String titleText;
    public final String relationText;
    public final String changingText;
    public final String questionText;
    public final String metaText;
    public final String noteText;
    public final String editContentDescription;
    public final String deleteContentDescription;

    private RecordCardPresentation(String titleText, String relationText, String changingText,
                                   String questionText, String metaText, String noteText,
                                   String editContentDescription, String deleteContentDescription) {
        this.titleText = titleText;
        this.relationText = relationText;
        this.changingText = changingText;
        this.questionText = questionText;
        this.metaText = metaText;
        this.noteText = noteText;
        this.editContentDescription = editContentDescription;
        this.deleteContentDescription = deleteContentDescription;
    }

    public static RecordCardPresentation from(DivinationRecord record) {
        Hexagram hexagram = HexagramRepository.get(record.hexagramNumber);
        Hexagram relating = HexagramRepository.get(record.relatingHexagramNumber);
        DivinationMethod method = record.method == null ? DivinationMethod.COINS : record.method;
        return new RecordCardPresentation(
                "第" + hexagram.number + "卦｜" + hexagram.fullName,
                relationText(hexagram, relating),
                changingText(record),
                record.question == null ? "" : record.question,
                method.label + " · " + new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.TAIWAN).format(new Date(record.createdAt)),
                record.note == null ? "" : record.note,
                "編輯第" + hexagram.number + "卦紀錄筆記",
                "刪除第" + hexagram.number + "卦紀錄"
        );
    }

    public boolean hasNote() {
        return noteText != null && !noteText.isEmpty();
    }

    static String relationText(Hexagram hexagram, Hexagram relating) {
        if (hexagram.number == relating.number) return "本卦即之卦｜第" + relating.number + "卦 " + relating.fullName;
        return "本卦 → 之卦｜第" + hexagram.number + "卦 " + hexagram.name
                + " → 第" + relating.number + "卦 " + relating.name;
    }

    static String changingText(DivinationRecord record) {
        if (record.changingLines == null || record.changingLines.isEmpty()) return "無變爻";
        StringBuilder builder = new StringBuilder("變爻 ");
        for (int i = 0; i < record.changingLines.size(); i++) {
            if (i > 0) builder.append("、");
            builder.append(record.changingLines.get(i));
        }
        return builder.toString();
    }
}
