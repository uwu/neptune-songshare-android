package dev.xinto.neptunesongshare;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PostActivity extends Activity {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (action != null && type != null &&
                action.equals(Intent.ACTION_SEND) && type.equals("text/plain")) {
            String songUrl = intent.getStringExtra(Intent.EXTRA_TEXT);

            if (songUrl != null) {
                requestAudioFocus(AudioManager.AUDIOFOCUS_GAIN);

                executorService.execute(() -> {
                    try {
                        URL url = new URL("http://neptune-songshare.local:16257");
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setDoOutput(true);

                        OutputStream os = connection.getOutputStream();
                        os.write(songUrl.getBytes());
                        os.flush();
                        os.close();

                        if (connection.getResponseCode() == 200) {
                            showToast("Successfully sent the URL to the server");
                        } else {
                            logDebug("Neptune", connection.getResponseMessage());
                        }
                    } catch (MalformedURLException e) {
                        showToast("Invalid URL");
                        printDebugStackTrace(e);
                    } catch (IOException e) {
                        showToast("An IO Exception occurred");
                        printDebugStackTrace(e);
                    }
                });
            }
        }

        finish();
    }

    private void requestAudioFocus(int flag) {
        AudioManager audioManager = getSystemService(AudioManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioManager.requestAudioFocus(
                    new AudioFocusRequest.Builder(flag)
                            .setAudioAttributes(
                                    new AudioAttributes.Builder()
                                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                            .build()
                            )
                            .build()
            );
        } else {
            audioManager.requestAudioFocus(focusChange -> {}, AudioManager.STREAM_MUSIC, flag);
        }
    }

    private void printDebugStackTrace(Exception e) {
        if (BuildConfig.DEBUG) {
            e.printStackTrace();
        }
    }

    private void logDebug(String tag, String data) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, data);
        }
    }

    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_LONG).show());
    }
}
