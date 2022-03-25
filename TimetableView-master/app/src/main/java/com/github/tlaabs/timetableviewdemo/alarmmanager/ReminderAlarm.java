package com.github.tlaabs.timetableviewdemo.alarmmanager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.github.tlaabs.timetableviewdemo.receiver.ReminderReceiver;

import java.util.Calendar;

public class ReminderAlarm {
    private Context context;
    public static final int REMINDER_ALARM_ID = 1;
    private AlarmManager alarmManager;
    private PendingIntent reminderIntent;
    private int requestCode = 0;
    private int requestcode = 0;
    public ReminderAlarm(Context context) {
        this.context = context;
    }

    public boolean isReminderSet() {
        Intent intent = new Intent(context, ReminderReceiver.class);
        return (PendingIntent
                .getBroadcast(context, requestCode++, intent, PendingIntent.FLAG_NO_CREATE) != null);
    }

    public void initReminder(Calendar calendar) {
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, ReminderReceiver.class);
        reminderIntent = PendingIntent
                .getBroadcast(context, requestcode++, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), reminderIntent);
        Toast.makeText(context, "Alarm Set!", Toast.LENGTH_SHORT).show();
    }

    public void cancelReminder() {
        Intent intent = new Intent(context, ReminderReceiver.class);
        reminderIntent = PendingIntent
                .getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(reminderIntent);
        Toast.makeText(context, "Alarm Cancel!", Toast.LENGTH_SHORT).show();
    }
}
