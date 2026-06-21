package fcu.app.i_ching.data;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class DailyInsightProvider {
    public static class DailyInsight {
        public final String dateText;
        public final Hexagram hexagram;

        DailyInsight(String dateText, Hexagram hexagram) {
            this.dateText = dateText;
            this.hexagram = hexagram;
        }
    }

    public DailyInsight insightFor(long timestamp, TimeZone timeZone) {
        TimeZone resolvedZone = timeZone == null ? TimeZone.getDefault() : timeZone;
        Calendar calendar = Calendar.getInstance(resolvedZone, Locale.TAIWAN);
        calendar.setTimeInMillis(timestamp);
        int hexagramNumber = hexagramNumberFor(calendar);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年M月d日 EEEE", Locale.TAIWAN);
        formatter.setTimeZone(resolvedZone);
        return new DailyInsight(formatter.format(calendar.getTime()), HexagramRepository.get(hexagramNumber));
    }

    public DailyInsight today() {
        return insightFor(System.currentTimeMillis(), TimeZone.getDefault());
    }

    private int hexagramNumberFor(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        return Math.floorMod((year * 37) + (dayOfYear * 17), 64) + 1;
    }
}
