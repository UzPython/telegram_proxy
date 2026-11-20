package uz.telegramproxy.mobile;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProxyService extends Service {
    static final String ACTION_START = "uz.telegramproxy.mobile.START";
    static final String ACTION_STOP = "uz.telegramproxy.mobile.STOP";
    static final String EXTRA_SECRET = "secret";

    private static final String TAG = "TelegramProxy";
    private static final String CHANNEL_ID = "telegram_proxy";
    private static final int NOTIFICATION_ID = 1;

    private ExecutorService pythonExecutor;
    private volatile boolean active;
    private volatile boolean stopRequested;
    private volatile boolean pythonMainStarted;
    private final Object lifecycleLock = new Object();

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent == null ? ACTION_START : intent.getAction();
        if (ACTION_STOP.equals(action)) {
            stopProxy(startId);
            return START_NOT_STICKY;
        }
        if (active) {
            return START_STICKY;
        }

        String secret = intent == null ? null : intent.getStringExtra(EXTRA_SECRET);
        if (secret == null || !secret.matches("[0-9a-f]{32}")) {
            stopWithError(startId, getString(R.string.invalid_secret));
            return START_NOT_STICKY;
        }

        active = true;
        stopRequested = false;
        pythonMainStarted = false;
        MainActivity.saveStatus(this, getString(R.string.starting));
        startForeground(NOTIFICATION_ID, buildNotification(getString(R.string.starting)));

        pythonExecutor = Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable, "telegram-proxy-python");
            thread.setDaemon(true);
            return thread;
        });
        pythonExecutor.execute(() -> runProxy(secret, startId));
        return START_STICKY;
    }

    private void runProxy(String secret, int startId) {
        try {
            if (!Python.isStarted()) {
                Python.start(new AndroidPlatform(getApplicationContext()));
            }

            synchronized (lifecycleLock) {
                if (stopRequested) {
                    return;
                }
                pythonMainStarted = true;
            }

            Python.getInstance()
                    .getModule("android_entry")
                    .callAttr("run", secret, (Runnable) this::markRunning);
        } catch (RuntimeException exception) {
            Log.e(TAG, "Proxy startup failed", exception);
            if (stopRequested) {
                MainActivity.saveStatus(this, getString(R.string.stopped));
                stopSelf(startId);
            } else {
                stopWithError(startId, getString(R.string.start_error, exception.getMessage()));
            }
        } finally {
            pythonMainStarted = false;
        }
    }

    private void markRunning() {
        String address = "127.0.0.1:8444";
        updateStatus(getString(R.string.proxy_running, address));
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, buildNotification(getString(R.string.notification_running, address)));
        }
    }

    private void stopProxy(int startId) {
        boolean signalPython;
        String status = getString(R.string.stopped);
        synchronized (lifecycleLock) {
            stopRequested = true;
            signalPython = pythonMainStarted;
        }
        if (signalPython && Python.isStarted()) {
            try {
                Python.getInstance().getModule("android_entry").callAttr("stop");
            } catch (RuntimeException exception) {
                Log.e(TAG, "Could not stop proxy cleanly", exception);
                status = getString(R.string.stop_error, exception.getMessage());
            }
        }
        active = false;
        MainActivity.saveStatus(this, status);
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.cancel(NOTIFICATION_ID);
        }
        stopForeground(STOP_FOREGROUND_REMOVE);
        if (!status.equals(getString(R.string.stopped)) && manager != null) {
            manager.notify(NOTIFICATION_ID, buildNotification(status));
        }
        stopSelf(startId);
    }

    private void stopWithError(int startId, String message) {
        active = false;
        MainActivity.saveStatus(this, message);
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.cancel(NOTIFICATION_ID);
        }
        stopForeground(STOP_FOREGROUND_REMOVE);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, buildNotification(message));
        }
        stopSelf(startId);
    }

    private void updateStatus(String status) {
        MainActivity.saveStatus(this, status);
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, buildNotification(status));
        }
    }

    private Notification buildNotification(String text) {
        Intent activityIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification.Builder builder = Build.VERSION.SDK_INT >= 26
                ? new Notification.Builder(this, CHANNEL_ID)
                : new Notification.Builder(this);
        return builder
                .setContentTitle(getString(R.string.app_name))
                .setContentText(text)
                .setSmallIcon(android.R.drawable.stat_sys_upload_done)
                .setContentIntent(pendingIntent)
                .setOngoing(active)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
    }

    @Override
    public void onDestroy() {
        boolean signalPython;
        synchronized (lifecycleLock) {
            stopRequested = true;
            signalPython = active && pythonMainStarted;
        }
        if (signalPython && Python.isStarted()) {
            try {
                Python.getInstance().getModule("android_entry").callAttr("stop");
            } catch (RuntimeException exception) {
                Log.e(TAG, "Could not stop proxy during service shutdown", exception);
            }
        }
        if (pythonExecutor != null) {
            pythonExecutor.shutdown();
        }
        if (active) {
            MainActivity.saveStatus(this, getString(R.string.stopped));
        }
        active = false;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
