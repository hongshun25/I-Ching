package fcu.app.i_ching.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent == null ? null : intent.getAction())) {
            new ReminderScheduler(context).rescheduleIfEnabled();
        }
    }
}
