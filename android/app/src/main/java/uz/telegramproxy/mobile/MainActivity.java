package uz.telegramproxy.mobile;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {
    private static final int REQUEST_NOTIFICATIONS = 1;
    private static final String PREFERENCES = "telegram_proxy";
    private static final String SECRET_KEY = "secret";

    private String secret;
    private String proxyLink;
    private TextView statusView;
    private final Handler statusHandler = new Handler(Looper.getMainLooper());
    private final Runnable statusRefresh = new Runnable() {
        @Override
        public void run() {
            updateStatus();
            statusHandler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        secret = loadOrCreateSecret();
        proxyLink = createProxyLink(secret);
        buildScreen();
    }

    @Override
    protected void onResume() {
        super.onResume();
        statusHandler.removeCallbacks(statusRefresh);
        statusHandler.post(statusRefresh);
    }

    @Override
    protected void onPause() {
        statusHandler.removeCallbacks(statusRefresh);
        super.onPause();
    }

    private String loadOrCreateSecret() {
        SharedPreferences preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        String stored = preferences.getString(SECRET_KEY, null);
        if (stored != null && stored.matches("[0-9a-f]{32}")) {
            return stored;
        }

        byte[] bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        StringBuilder result = new StringBuilder(32);
        for (byte value : bytes) {
            result.append(String.format("%02x", value & 0xff));
        }
        String generated = result.toString();
        preferences.edit().putString(SECRET_KEY, generated).apply();
        return generated;
    }

    private String createProxyLink(String key) {
        byte[] domain = "www.google.com".getBytes(StandardCharsets.UTF_8);
        StringBuilder domainHex = new StringBuilder(domain.length * 2);
        for (byte value : domain) {
            domainHex.append(String.format("%02x", value & 0xff));
        }
        return "tg://proxy?server=127.0.0.1&port=8444&secret=ee" + key + domainHex;
    }

    private void buildScreen() {
        int padding = dp(20);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(padding, padding, padding, padding);

        TextView title = new TextView(this);
        title.setText(R.string.app_name);
        title.setTextSize(26);
        title.setGravity(Gravity.CENTER);
        content.addView(title, matchWrap());

        TextView description = new TextView(this);
        description.setText(R.string.description);
        description.setTextSize(16);
        description.setPadding(0, dp(16), 0, dp(16));
        content.addView(description, matchWrap());

        statusView = new TextView(this);
        statusView.setTextSize(16);
        statusView.setPadding(0, 0, 0, dp(16));
        content.addView(statusView, matchWrap());

        Button startButton = new Button(this);
        startButton.setText(R.string.start_proxy);
        startButton.setOnClickListener(view -> requestPermissionAndStart());
        content.addView(startButton, matchWrap());

        Button stopButton = new Button(this);
        stopButton.setText(R.string.stop_proxy);
        stopButton.setOnClickListener(view -> stopProxy());
        content.addView(stopButton, matchWrap());

        TextView link = new TextView(this);
        link.setText(proxyLink);
        link.setTextIsSelectable(true);
        link.setTextSize(14);
        link.setPadding(0, dp(16), 0, dp(8));
        content.addView(link, matchWrap());

        Button copyButton = new Button(this);
        copyButton.setText(R.string.copy_link);
        copyButton.setOnClickListener(view -> copyLink());
        content.addView(copyButton, matchWrap());

        Button telegramButton = new Button(this);
        telegramButton.setText(R.string.connect_telegram);
        telegramButton.setOnClickListener(view -> openTelegram());
        content.addView(telegramButton, matchWrap());

        TextView note = new TextView(this);
        note.setText(R.string.usage_note);
        note.setTextSize(14);
        note.setPadding(0, dp(16), 0, 0);
        content.addView(note, matchWrap());

        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(content);
        setContentView(scrollView);
    }

    private void requestPermissionAndStart() {
        if (Build.VERSION.SDK_INT >= 33
                && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    REQUEST_NOTIFICATIONS);
            return;
        }
        startProxy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_NOTIFICATIONS) {
            startProxy();
        }
    }

    private void startProxy() {
        Intent intent = new Intent(this, ProxyService.class)
                .setAction(ProxyService.ACTION_START)
                .putExtra(ProxyService.EXTRA_SECRET, secret);
        if (Build.VERSION.SDK_INT >= 26) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        statusView.setText(R.string.starting);
    }

    private void stopProxy() {
        Intent intent = new Intent(this, ProxyService.class).setAction(ProxyService.ACTION_STOP);
        startService(intent);
        statusView.setText(R.string.stopping);
    }

    private void copyLink() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText("Telegram proxy link", proxyLink));
        Toast.makeText(this, R.string.link_copied, Toast.LENGTH_SHORT).show();
    }

    private void openTelegram() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(proxyLink)));
        } catch (android.content.ActivityNotFoundException exception) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setPrimaryClip(ClipData.newPlainText("Telegram proxy link", proxyLink));
            Toast.makeText(this, R.string.telegram_missing_copied, Toast.LENGTH_LONG).show();
        }
    }

    private void updateStatus() {
        SharedPreferences preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        statusView.setText(preferences.getString("status", getString(R.string.stopped)));
    }

    static void saveStatus(Context context, String status) {
        context.getSharedPreferences(PREFERENCES, MODE_PRIVATE)
                .edit()
                .putString("status", status)
                .apply();
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
