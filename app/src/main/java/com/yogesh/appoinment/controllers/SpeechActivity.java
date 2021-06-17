package com.yogesh.appoinment.controllers;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Environment;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.yogesh.appoinment.R;

import org.alicebot.ab.AIMLProcessor;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.MagicStrings;
import org.alicebot.ab.PCAIMLProcessorExtension;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class SpeechActivity extends AppCompatActivity {
    private SpeechRecognizer mySpeechRecognizer;
    private Bot bot;
    public static Chat chat;
    private ChatMessageAdapter adapter;
    ListView listView;
    FloatingActionButton btnSend;
    EditText edtTextMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);
        //ask for permission at runtime
        btnSend = (FloatingActionButton) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = edtTextMsg.getText().toString();
                String response = chat.multisentenceRespond(message);
                if (TextUtils.isEmpty(message)) {
                    Toast.makeText(SpeechActivity.this, "Please enter query", Toast.LENGTH_SHORT).show();
                    return;
                }
                processResult(message);
                edtTextMsg.setText("");
            }
        });
        edtTextMsg = (EditText) findViewById(R.id.edtTextMsg);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                mySpeechRecognizer.startListening(intent);
            }
        });
        listView = (ListView) findViewById(R.id.listview);
        adapter = new ChatMessageAdapter(this, new ArrayList<ChatMessage>());
        listView.setAdapter(adapter);
        if (checkAndRequestPermissions()) {
            loadLibrary();
        }

    }

    private void loadLibrary() {
        boolean available = isSDCardAvailable();
        AssetManager assets = getResources().getAssets();
        File fileName = new File(Environment.getExternalStorageDirectory().toString() + "/TBC/bots/TBC");

        boolean makeFile = fileName.mkdirs();

        if (fileName.exists()) {
            //read the line
            try {
                for (String dir : assets.list("TBC")) {
                    File subDir = new File(fileName.getPath() + "/" + dir);
                    boolean subDir_Check = subDir.mkdirs();
                    for (String file : assets.list("TBC/" + dir)) {
                        File newFile = new File(fileName.getPath() + "/" + dir + "/" + file);
                        if (newFile.exists()) {
                            continue;
                        }
                        InputStream in;
                        OutputStream out;
                        String str;
                        in = assets.open("TBC/" + dir + "/" + file);
                        out = new FileOutputStream(fileName.getPath() + "/" + dir + "/" + file);
                        //copy files from assets to the mobile's SD card or any secondary memory available
                        copyFile(in, out);
                        in.close();
                        out.flush();
                        out.close();
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //get the working directory

        MagicStrings.root_path = Environment.getExternalStorageDirectory().toString() + "/TBC";
        AIMLProcessor.extension = new PCAIMLProcessorExtension();
        bot = new Bot("TBC", MagicStrings.root_path, "chat");
        chat = new Chat(bot);
        initializeTextToSpeech();
        initializeSpeechRecognizer();
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;

        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public static boolean isSDCardAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ? true : false;
    }

    private void botsReply(String response) {
        ChatMessage chatMessage = new ChatMessage(false, false, response);
        adapter.add(chatMessage);
    }

    private void sendMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(false, true, message);
        adapter.add(chatMessage);
    }


    private void initializeSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            mySpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            mySpeechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle bundle) {

                }

                @Override
                public void onBeginningOfSpeech() {

                }

                @Override
                public void onRmsChanged(float v) {

                }

                @Override
                public void onBufferReceived(byte[] bytes) {

                }

                @Override
                public void onEndOfSpeech() {

                }

                @Override
                public void onError(int i) {

                }

                @Override
                public void onResults(Bundle bundle) {
                    List<String> results = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    processResult(results.get(0));
                }

                @Override
                public void onPartialResults(Bundle bundle) {

                }

                @Override
                public void onEvent(int i, Bundle bundle) {

                }
            });
        }
    }

    private void processResult(String command) {

        String message = command;
        String response = chat.multisentenceRespond(message);
        if (response.contains("<oob>")) {
            response = response.substring(0, response.indexOf("<oob>"));
        }
        if (TextUtils.isEmpty(message)) {
            Toast.makeText(SpeechActivity.this, "Please enter query", Toast.LENGTH_SHORT).show();
            return;
        }

        sendMessage(message);
//        speak(response);
        botsReply(response);
        if (response.contains("please visit")) {
            response = response.replace("please visit", "");
            if (!response.startsWith("http://") && !response.startsWith("https://"))
                response = "http://" + response;
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(response));
            startActivity(browserIntent);
        }

        listView.setSelection(adapter.getCount() - 1);
    }

    private void initializeTextToSpeech() {
        botsReply("Hi, Health Bot Here. But it's nice to meet you..");
    }

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    private boolean checkAndRequestPermissions() {

        int audio = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int storage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (audio != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }
        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];
                if (permission.equals(Manifest.permission.SEND_SMS)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        loadLibrary();
                    } else {
                        checkAndRequestPermissions();
                    }
                }
            }
        }
    }
}