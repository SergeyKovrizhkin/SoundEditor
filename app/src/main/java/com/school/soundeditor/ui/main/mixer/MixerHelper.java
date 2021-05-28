package com.school.soundeditor.ui.main.mixer;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import java.io.IOException;
import java.util.List;

import zeroonezero.android.audio_mixer.AudioMixer;
import zeroonezero.android.audio_mixer.input.AudioInput;
import zeroonezero.android.audio_mixer.input.BlankAudioInput;
import zeroonezero.android.audio_mixer.input.GeneralAudioInput;

public class MixerHelper {

    private static final String outputPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "audio_mixer_output.mp3";
    private static AudioMixer audioMixer = null;

    public static void startMixing(FragmentActivity activity, List<Uri> inputs) {
        //For showing progress
        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Mixing audio...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setProgress(0);

        try {
            audioMixer = new AudioMixer(outputPath);

            for (Uri uri : inputs) {
                AudioInput audioInput;
                if (uri != null) {
                    //GeneralAudioInput ai = new GeneralAudioInput(activity, uri, null);
//                    ai.setStartOffsetUs(uri.startOffsetUs);
//                    ai.setStartTimeUs(uri.startTimeUs); // optional
//                    ai.setEndTimeUs(uri.endTimeUs); // optional
                    //ai.setVolume(0.5f); //optional

                    audioInput = new GeneralAudioInput(activity, uri, null);
                } else {
                    audioInput = new BlankAudioInput(5000000);
                }
                audioMixer.addDataSource(audioInput);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //audioMixer.setSampleRate(44100);  // optional
        //audioMixer.setBitRate(128000); // optional
        //audioMixer.setChannelCount(2); // 1 or 2 // optional
        //audioMixer.setLoopingEnabled(true); // Only works for parallel mixing
        audioMixer.setMixingType(AudioMixer.MixingType.SEQUENTIAL);
        audioMixer.setProcessingListener(new AudioMixer.ProcessingListener() {
            @Override
            public void onProgress(double progress) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.setProgress((int) (progress * 100));
                    }
                });
            }

            @Override
            public void onEnd() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.setProgress(100);
                        progressDialog.dismiss();
                        Toast.makeText(activity, "Success!!! Ouput path: " + outputPath, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "End", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                audioMixer.stop();
                audioMixer.release();
            }
        });

        try {
            audioMixer.start();
            audioMixer.processAsync();
            progressDialog.show();
        } catch (IOException e) {
            audioMixer.release();
        }
    }
}
