package fcu.app.i_ching.data;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;

import java.util.Calendar;

import fcu.app.i_ching.R;

public class ReminderScheduler {
    public static final String CHANNEL_ID = "daily_insight";
    static final int REQUEST_CODE = 4201;

    private final Context context;

    public ReminderScheduler(Context context) {
        this.context = context.getApplicationContext();
    }

    public boolean canPostNotifications() {
        return Build.VERSION.SDK_INT < 33
                || ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED;
    }

    public void schedule(int hour, int minute) {
        createChannel();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;
        alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                nextTrigger(hour, minute),
                AlarmManager.INTERVAL_DAY,
                pendingIntent(PendingIntent.FLAG_UPDATE_CURRENT)
        );
    }

    public void cancel() {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = pendingIntent(PendingIntent.FLAG_NO_CREATE);
        if (alarmManager != null && pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    public void rescheduleIfEnabled() {
        SettingsStore settings = new SettingsStore(context);
        if (settings.isDailyReminderEnabled() && canPostNotifications()) {
            schedule(settings.dailyReminderHour(), settings.dailyReminderMinute());
        } else {
            cancel();
        }
    }

    void createChannel() {
        if (Build.VERSION.SDK_INT < 26) return;
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null) return;
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.daily_reminder_channel),
                NotificationManager.IMPORTANCE_DEFAULT
        );
        manager.createNotificationChannel(channel);
    }

    private long nextTrigger(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Math.max(0, Math.min(23, hour)));
        calendar.set(Calendar.MINUTE, Math.max(0, Math.min(59, minute)));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        return calendar.getTimeInMillis();
    }

    private PendingIntent pendingIntent(int flags) {
        Intent intent = new Intent(context, DailyReminderReceiver.class);
        int immutable = Build.VERSION.SDK_INT >= 23 ? PendingIntent.FLAG_IMMUTABLE : 0;
        return PendingIntent.getBroadcast(context, REQUEST_CODE, intent, flags | immutable);
    }
}
