package fr.intech.cormand.cryptgsm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import fr.intech.cormand.cryptgsm.Conversations.Conversation;
import fr.intech.cormand.cryptgsm.Conversations.ConversationActivity;
import fr.intech.cormand.cryptgsm.Conversations.ConversationsAdapter;

public class MainActivity extends AppCompatActivity {

    ImageView btnNewConversation;
    RecyclerView conversationListView;
    List<Conversation> conversationList;
    TextView noConversationsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_conversations);

        // Hide Title Bar & Fullscreen
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);

        // Get & Verify if permissions granted
        if (!getPermissions()) {
            // Don't get permissions
            Log.e("PERMISSIONS", "NO PERMISSIONS !!");
        }

        // RecyclerView
        conversationListView = findViewById(R.id.recycler_view_conversations);
        conversationList = new ArrayList<>();
        // Put conversations in recyclerview
        conversationListView.setLayoutManager(new LinearLayoutManager(this));


        // New conv btn
        btnNewConversation = findViewById(R.id.btn_new_conversation);
        btnNewConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open New conv
                Toast.makeText(MainActivity.this, "New Conversation", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(v.getContext(), NewConversationActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        conversationList = Conversation.loadingAll(this);

        verifyInitRecept();

        // No conversations
        if (conversationList.size() < 1) {
            noConversationsTextView = findViewById(R.id.textViewNoConversations);
            noConversationsTextView.setText("No conversations...");
        }

        conversationListView.setAdapter(new ConversationsAdapter(conversationList));
        conversationListView.invalidate();
    }

    // Messages methods
    private boolean getPermissions() {
        if (checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            // Get Permission
            requestPermissions(new String[] { Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS, Manifest.permission.READ_CONTACTS }, 2);
            return true;
        }
        return true;
    }

    private boolean addressAlreadyInit (String address) {
        for (int i = 0; i < conversationList.size(); i++) {
            if (conversationList.get(i).getAddress().contentEquals(address) && conversationList.get(i).getInitResponse()) {
                return true;
            }
        }
        return false;
    }

    private void verifyInitRecept () {
        Log.i("INITRECEPT", "Verify init recept!");
        List<Msg> sms = new ArrayList<>();
        Uri uriSMSURI = Uri.parse("content://sms/inbox");
        Cursor cur = getContentResolver().query(uriSMSURI, null, null, null, null);

        while (cur != null && cur.moveToNext()) {
            Msg m = new Msg(cur.getString(cur.getColumnIndex("address")), cur.getString(cur.getColumnIndexOrThrow("body")), cur.getString(cur.getColumnIndexOrThrow("date_sent")));
            sms.add(m);
        }

        if (cur != null) {
            cur.close();
        }

        for (int i = 0; i < sms.size(); i++) {
            Msg m = sms.get(i);
            //Log.i("Msg", m.getAddress() + ": " + m.getBody());
            if (m.getBody().startsWith("/CryptSMS-init1/") && !addressAlreadyInit(m.getAddress())) {
                String publicKey1 = m.getBody().replaceAll("/CryptSMS-init1/", "");
                String publicKey2 = "";

                // Find fast or for while
                if (sms.get(i - 1).getAddress().contentEquals(m.getAddress())) {
                    if (sms.get(i - 1).getBody().startsWith("/CryptSMS-init2/")) {
                        publicKey2 = sms.get(i-1).getBody().replaceAll("/CryptSMS-init2/", "");
                    }
                } else {
                    Log.i("SMS", "adr: " + sms.get(i).getAddress() + ", body: " + sms.get(i).getBody());
                    Log.i("SMS", "adr: " + sms.get(i - 1).getAddress() + ", body: " + sms.get(i - 1).getBody());
                    Log.i("SMS", "Go boucle!");
                    for (int o = 0; o < sms.size(); o++) {
                        if (sms.get(o).getAddress().contentEquals(m.getAddress())) {
                            // Same address
                            if (sms.get(o).getBody().startsWith("/CryptSMS-init2/")) {
                                publicKey2 = sms.get(o).getBody().replaceAll("/CryptSMS-init2/", "");
                            }
                        }
                    }
                }

                if (publicKey2 == "") {
                    Log.e("RecieveInit", "PUBLIC KEY 2 don't find: " + publicKey2);

                } else {
                    String publicKey = publicKey1 + publicKey2;
                    Log.i("PUBLICKEY", publicKey);

                    Conversation c = new Conversation();
                    c.setPublicKeyContact(publicKey);
                    c.setAddress(m.getAddress());
                    c.setInitResponse(true);
                    c.saving(this);

                    if (!conversationList.contains(c)) {
                        conversationList.add(c);
                    }

                    if (!c.getInit()) {
                        c.sendInitMsg(this);
                    }
                }
            }
        }
    }
}