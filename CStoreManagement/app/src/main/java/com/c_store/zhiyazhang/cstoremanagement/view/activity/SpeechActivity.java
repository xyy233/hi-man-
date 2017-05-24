package com.c_store.zhiyazhang.cstoremanagement.view.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.c_store.zhiyazhang.cstoremanagement.R;
import com.c_store.zhiyazhang.cstoremanagement.utils.activity.MyActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by zhiya.zhang
 * on 2017/5/22 8:45.
 */

public class SpeechActivity extends MyActivity implements View.OnClickListener {
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
    @BindView(R.id.speechText)
    TextView speechText;
    @BindView(R.id.speechBtn)
    Button speakButton;
    @BindView(R.id.list)
    ListView mList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check to see if a recognition activity is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() != 0) {
            speakButton.setOnClickListener(this);
        } else {
            speakButton.setEnabled(false);
            speakButton.setText("Recognizer not present");
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_speech;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.speechBtn) {
            startVoiceRecognitionActivity();
        }
    }

    /**
     * Fire an intent to start the speech recognition activity.
     */
    private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech recognition demo");
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    /**
     * Handle the results from the recognition activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            // Fill the list view with the strings the recognizer thought it could have heard
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            mList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                    matches));
            if (matches.size() > 0) {
                speechText.setText(matches.get(0));
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
