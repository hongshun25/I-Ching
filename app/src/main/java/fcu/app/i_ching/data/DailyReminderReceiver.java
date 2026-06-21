package fcu.app.i_ching.data;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import fcu.app.i_ching.MainActivity;
import fcu.app.i_ching.R;

public class DailyReminderReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 4202;

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        ReminderScheduler scheduler = new ReminderScheduler(context);
        SettingsStore settings = new SettingsStore(context);
        if (!settings.isDailyReminderEnabled() || !scheduler.canPostNotifications()) {
            return;
        }
        scheduler.createChannel();
        Intent openIntent = new Intent(context, MainActivity.class);
        openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int immutable = Build.VERSION.SDK_INT >= 23 ? PendingIntent.FLAG_IMMUTABLE : 0;
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, openIntent, immutable | PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ReminderScheduler.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_today_24)
                .setContentTitle(context.getString(R.string.daily_reminder_title))
                .setContentText(context.getString(R.string.daily_reminder_body))
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build());
    }
}
