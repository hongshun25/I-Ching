package fcu.app.i_ching.data;

import org.junit.Test;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class DailyInsightProviderTest {
    @Test
    public void sameLocalDateReturnsStableHexagram() {
        DailyInsightProvider provider = new DailyInsightProvider();
        TimeZone zone = TimeZone.getTimeZone("Asia/Taipei");

        DailyInsightProvider.DailyInsight morning = provider.insightFor(timestamp(zone, 2026, Calendar.JUNE, 21, 9), zone);
        DailyInsightProvider.DailyInsight evening = provider.insightFor(timestamp(zone, 2026, Calendar.JUNE, 21, 21), zone);

        assertEquals(morning.hexagram.number, evening.hexagram.number);
        assertEquals("2026年6月21日 星期日", morning.dateText);
    }

    @Test
    public void adjacentDatesCanRotateHexagramAndNeverUseHardcodedStemBranchDate() {
        DailyInsightProvider provider = new DailyInsightProvider();
        TimeZone zone = TimeZone.getTimeZone("Asia/Taipei");

        DailyInsightProvider.DailyInsight today = provider.insightFor(timestamp(zone, 2026, Calendar.JUNE, 21, 9), zone);
        DailyInsightProvider.DailyInsight tomorrow = provider.insightFor(timestamp(zone, 2026, Calendar.JUNE, 22, 9), zone);

        assertNotEquals(today.hexagram.number, tomorrow.hexagram.number);
        assertFalse(today.dateText.contains("甲辰年"));
        assertTrue(today.dateText.contains("2026年6月21日"));
    }

    private static long timestamp(TimeZone zone, int year, int month, int day, int hour) {
        Calendar calendar = Calendar.getInstance(zone, Locale.TAIWAN);
        calendar.set(year, month, day, hour, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
}
