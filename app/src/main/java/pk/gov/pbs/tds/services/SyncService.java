package pk.gov.pbs.tds.services;

import android.app.Notification;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import pk.gov.pbs.formbuilder.database.FormBuilderRepository;
import pk.gov.pbs.formbuilder.models.Option;
import pk.gov.pbs.tds.CustomApplication;
import pk.gov.pbs.tds.R;
import pk.gov.pbs.tds.database.FormRepository;
import pk.gov.pbs.tds.database.DataProvider;
import pk.gov.pbs.utils.Constants;
import pk.gov.pbs.utils.StaticUtils;

public class SyncService extends Service {
    private static SyncService instance;
    private static final String TAG = "SyncService";
    private static final int SERVICE_NOTIFICATION_ID = 2;
    private static final int SYNC_JOB_KEY = 101;
    private static final int OPTIONS_SYNC_JOB_KEY = 102;
    public static final int RESCHEDULE_PERIOD = 30 * 60 * 1000;
    private final SyncServiceBinder mBinder = new SyncServiceBinder();
    private JobScheduler mScheduler;
    private Runnable messageLoop;
    public static final String BROADCAST_EXTRA_HOUSEHOLD = "hh_identity";
    public static final String BROADCAST_ACTION_HOUSEHOLD_COMPLETED = SyncService.class.getCanonicalName() + ".SYNC_READY";
    public static final String Notification_Channel_ID = "Sync_Service_Channel";
    public static final String Notification_Channel_Name = "TDS Sync Service";
    private BroadcastReceiver syncBroadcastReceiver;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: SyncService onCreated executing");
        super.onCreate();
        mScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

        syncBroadcastReceiver = new SyncBroadcastReceiver();
        IntentFilter filter = new IntentFilter(BROADCAST_ACTION_HOUSEHOLD_COMPLETED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(syncBroadcastReceiver, filter, RECEIVER_EXPORTED);
        } else
            registerReceiver(syncBroadcastReceiver, filter);
        instance = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: SyncService onStartCommand executing");
        Notification notification = new NotificationCompat.Builder(this, Notification_Channel_ID)
                .setContentTitle(Notification_Channel_Name)
                .setContentText("Data synchronization service")
                .setSmallIcon(R.drawable.ic_sync)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        startForeground(SERVICE_NOTIFICATION_ID, notification);
        syncMessageLoop();
        return START_STICKY;
    }

    private void syncMessageLoop(){
        messageLoop = () -> {
            StaticUtils.getHandler().postDelayed(
                    messageLoop,
                    RESCHEDULE_PERIOD
            );
//            scheduleSyncTask();
        };
        messageLoop.run();
    }

//    public void scheduleSyncTask(String[] unsynced_hhId){
//        if (unsynced_hhId != null && unsynced_hhId.length > 0) {
//            Log.d(TAG, "scheduleSyncTask: " + unsynced_hhId.length + " syncable completed forms identifiers found !!");
//
//            ComponentName task = new ComponentName(this, SyncScheduler.class);
//            PersistableBundle bundle = new PersistableBundle();
//            bundle.putStringArray("hhIds", unsynced_hhId);
//            JobInfo.Builder builder = new JobInfo.Builder(SYNC_JOB_KEY, task)
//                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
//                    .setPersisted(true)
//                    .setExtras(bundle);
//            mScheduler.schedule(builder.build());
//        } else
//            Log.d(TAG, "scheduleSyncTask: no syncable completed data found!!!");
//    }
//
//    private void scheduleSyncTask(){
//        String[] unsynced_hhId = getSyncableHousehold();
//        scheduleSyncTask(unsynced_hhId);
//
//        List<Option> options = DataProvider.getNewOptions(FormBuilderRepository.getInstance(getApplication()));
//        if (options != null && !options.isEmpty()){
//            String optionsJson = StaticUtils.getSimpleGson().toJson(options);
//            ComponentName task = new ComponentName(this, SyncScheduler.class);
//            PersistableBundle bundle = new PersistableBundle();
//            bundle.putString("optionsJson", optionsJson);
//            JobInfo.Builder builder = new JobInfo.Builder(OPTIONS_SYNC_JOB_KEY, task)
//                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
//                    .setPersisted(true)
//                    .setExtras(bundle);
//            mScheduler.schedule(builder.build());
//        }
//
//        ComponentName task = new ComponentName(this, SyncScheduler.class);
//        PersistableBundle bundle = new PersistableBundle();
//        bundle.putBoolean("fetchOptions", true);
//        JobInfo.Builder builder = new JobInfo.Builder(OPTIONS_SYNC_JOB_KEY + 1, task)
//                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
//                .setPersisted(true)
//                .setExtras(bundle);
//        mScheduler.schedule(builder.build());
//    }
//
//    private String[] getSyncableHousehold(){
//        if (CustomApplication.getLoginPayload() == null)
//            return null;
//
//        return DataProvider
//                .getSyncableCompletedForms(
//                        FormRepository.getInstance(getApplication())
//                );
//    }

    public static boolean isRunning(){
        return instance != null;
    }

    public static SyncService getInstance(){
        return instance;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(syncBroadcastReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class SyncServiceBinder extends Binder {
        public SyncService getService(){
            return SyncService.this;
        }
    }

    public static class SyncBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: SyncService$SyncBroadcastReceiver executing");
            if (Objects.equals(intent.getAction(), BROADCAST_ACTION_HOUSEHOLD_COMPLETED) && isRunning()){
                String household = intent.getStringExtra(BROADCAST_EXTRA_HOUSEHOLD);
                Log.d(TAG, "onReceive: BROADCAST_HOUSEHOLD_CHANGE received with household: " + household);
                if (household != null && !household.isEmpty()){
                    String[] hhIds = household.split(",");
//                    instance.scheduleSyncTask(hhIds);
                }
            }
        }
    }
}