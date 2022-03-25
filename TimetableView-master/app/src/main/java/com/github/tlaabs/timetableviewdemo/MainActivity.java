package com.github.tlaabs.timetableviewdemo;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.TimetableView;
import com.github.tlaabs.timetableviewdemo.alarmmanager.ReminderAlarm;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Context context;
    public static final int REQUEST_ADD = 1;
    public static final int REQUEST_EDIT = 2;
    public static final String REMINDER_NOTIFICATION_CHANNEL_ID = "com.github.tlaabs.timetableviewdemo.reminder.channel";
    private Schedule schedule;
    private Button addBtn;
    private Button clearBtn;
    private Button saveBtn;
    public static int broadcastCode=0;
    private TimetableView timetable;
    private ReminderAlarm reminderAlarm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createchannel();
        }
        init();
        reminderAlarm = new ReminderAlarm(getApplicationContext());
    }

@RequiresApi(api = Build.VERSION_CODES.O)
private void createchannel()
{
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        String name = "Timetable Reminnder";
        String desc = "Remind the user of the upcoming class.";
        int important = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(REMINDER_NOTIFICATION_CHANNEL_ID, name, important);
        channel.setDescription(desc);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        NotificationManager notimanager = getSystemService(NotificationManager.class);
        notimanager.createNotificationChannel(channel);
    }

}

    private void init(){
        this.context = this;
        addBtn = findViewById(R.id.add_btn);
        clearBtn = findViewById(R.id.clear_btn);
        saveBtn = findViewById(R.id.save_btn);

        timetable = findViewById(R.id.timetable);
        timetable.setHeaderHighlight(2);
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
        String savedData = mPref.getString("timetable_demo","");
        if(savedData != null && !savedData.equals(""))
            loadSavedData();
        initView();
        
    }

    private void initView(){
        addBtn.setOnClickListener(this);
        clearBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);


        timetable.setOnStickerSelectEventListener(new TimetableView.OnStickerSelectedListener() {
            @Override
            public void OnStickerSelected(int idx, ArrayList<Schedule> schedules) {
                Intent i = new Intent(context, EditActivity.class);
                i.putExtra("mode",REQUEST_EDIT);
                i.putExtra("idx", idx);
                i.putExtra("schedules", schedules);
                startActivityForResult(i,REQUEST_EDIT);
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_btn:
                Intent i = new Intent(this,EditActivity.class);
                i.putExtra("mode",REQUEST_ADD);
                startActivityForResult(i,REQUEST_ADD);
                break;
            case R.id.clear_btn:
                SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
                String savedData = mPref.getString("timetable_demo","");
                if(savedData != null && !savedData.equals("")) {
                    timetable.removeAll();
                    loadSavedData();
                    Toast.makeText(this, "roll back successful", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.save_btn:
                saveByPreference(timetable.createSaveData());
                break;


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case REQUEST_ADD:
                if(resultCode == EditActivity.RESULT_OK_ADD){
                    ArrayList<Schedule> item = (ArrayList<Schedule>)data.getSerializableExtra("schedules");
                    timetable.add(item);
                }
                break;
            case REQUEST_EDIT:
                /** Edit -> Submit */
                if(resultCode == EditActivity.RESULT_OK_EDIT){
                    assert data != null;
                    int idx = data.getIntExtra("idx",-1);
                    ArrayList<Schedule> item = (ArrayList<Schedule>)data.getSerializableExtra("schedules");
                    timetable.edit(idx,item);
                }
                /** Edit -> Delete */
                else if(resultCode == EditActivity.RESULT_OK_DELETE){
                    int idx = data.getIntExtra("idx",-1);
                    timetable.remove(idx);
                }
                break;
        }
    }

    /** save timetableView's data to SharedPreferences in json format */
    private void saveByPreference(String data){
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString("timetable_demo",data);
        editor.apply();
        Toast.makeText(this,"saved!",Toast.LENGTH_SHORT).show();
    }

    /** get json data from SharedPreferences and then restore the timetable */
    private void loadSavedData(){
        timetable.removeAll();
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
        String savedData = mPref.getString("timetable_demo","");
        if(savedData == null) {
            if (BuildConfig.DEBUG) {
                throw new AssertionError("Assertion failed");
            }
            if (savedData.equals("")) return;
        }
        timetable.load(savedData);

    }
}
