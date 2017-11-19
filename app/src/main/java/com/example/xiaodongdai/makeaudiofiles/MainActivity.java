package com.example.xiaodongdai.makeaudiofiles;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import static android.speech.tts.TextToSpeech.QUEUE_ADD;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "voicetotext";
    private EditText input;
    private Button buttonTry;
    private Button buttonMakeFiles;
    private Spinner spinner;
    private TextToSpeech tts;
    private TextView textView;
    private static final int REQUEST_WRITE_STORAGE = 112;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private String storagePath = "";
    private String storageAppPath = "";
    private ClickMakefileListener clickMakefileListener = null;

    class ClickMakefileListener implements View.OnClickListener {

        private void makeVoiceFile(String toSpeak, String fileName) {
            String pathFile = storageAppPath + File.separator + fileName;
            File myFile = new File(pathFile);
            try {
                myFile.createNewFile();
            } catch(IOException e) {
                Log.d(TAG, "Writting to file" + pathFile + " Failed");
            }
            tts.synthesizeToFile(toSpeak, null, myFile, pathFile);
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "writting path is: " + storageAppPath);
            boolean ret = new File(storageAppPath).mkdirs();
            if(ret == false) {
                Log.d(TAG, "the directory perhaps exists. ");
            }

            tts.setSpeechRate((float)1.2);
            for (int i = 1; i < 100; i++) {
                // get the voice name.
                String toSpeak = Integer.toString(i);
                String fileName = toSpeak + ".wav";
                makeVoiceFile(toSpeak, fileName);
            }
            makeVoiceFile("plus", "plus.wav");
            makeVoiceFile("minus", "minus.wav");
            makeVoiceFile("multiplied by", "multiplied_by.wav");
            makeVoiceFile("divide by", "devided_by.wav");
            makeVoiceFile("great", "great.wav");
            makeVoiceFile("incredible", "incredible.wav");
            makeVoiceFile("good job", "good_job.wav");
            makeVoiceFile("fantastic job", "fantastic_job.wav");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        input = (EditText) findViewById(R.id.plain_text_input);
        buttonTry = (Button) findViewById(R.id.tryBtn);
        buttonMakeFiles = (Button) findViewById(R.id.makefilesBtn);
        spinner = (Spinner) findViewById(R.id.voices_spinner);
        textView = (TextView) findViewById(R.id.textView);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        clickMakefileListener = new ClickMakefileListener();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                // An item was selected. You can retrieve the selected item using
                // parent.getItemAtPosition(pos)
                String voiceName = adapter.getItem(pos);
                Voice voice = (Voice)tts.getVoices().toArray()[pos];
                tts.setVoice(voice);
                storageAppPath = storagePath + File.separator + "myApp" +
                        File.separator + voiceName;
                Log.d(TAG, "item selected! pos = " + pos + " id=" + id + " Voice Name: " + voiceName );
            }
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

        // initiate the TTS
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.UK);
                    // find the storage directories.
                    String dirInfo = "ExternalStorageDirectory:" + Environment.getExternalStorageDirectory() + "\n";
                    File[] externalFilesDirs = getExternalFilesDirs(null);
                    dirInfo += "\ngetExternalFilesDirs(null):\n";
                    String internalPath = "";
                    for(File f : externalFilesDirs) {
                        if (Environment.isExternalStorageRemovable(f)) {
                            dirInfo += "Removable:" + f.getAbsolutePath() + "\n";
                            storagePath = f.getAbsolutePath();
                        } else {
                            dirInfo += "Internal:" + f.getAbsolutePath() + "\n";
                            internalPath = f.getAbsolutePath();
                        }
                    }
                    textView.setText(dirInfo);
                    // no external storage found, use internal
                    if(storagePath == "") {
                        storagePath = internalPath;
                    }

                    storageAppPath = storagePath + File.separator + "myApp" +
                            File.separator + tts.getVoice().getName();

                    // initialize the spinner
                    for(Voice v : tts.getVoices()) {
                        adapter.add(v.getName());
                    }
                    Log.d(TAG, "Initialize the button click...");
                    clickMakefileListener = new ClickMakefileListener();
                    buttonMakeFiles.setOnClickListener(clickMakefileListener);
                }
            }
        });

        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {
                String str =  " Started" + s;
                Log.d(TAG, str);
            }
            @Override
            public void onDone(String s) {
                String str = " Done " + s;
                Log.d(TAG, str);

            }
            @Override
            public void onError(String s) {
                String str = " Error " + s;
                Log.d(TAG, str);
            }

            @Override
            public void onStop(String s,  boolean interrupted) {
                String str = " Stop " + s;
                Log.d(TAG, str);
            }
        });
        // request for the permission to write in the external storage.
        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    // MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }
        }

        buttonTry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toSpeak = input.getText().toString();
                tts.speak(toSpeak, QUEUE_ADD, null, "mytest");
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d(TAG, "Approved");

                } else {
                    Log.d(TAG, "Denied");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
