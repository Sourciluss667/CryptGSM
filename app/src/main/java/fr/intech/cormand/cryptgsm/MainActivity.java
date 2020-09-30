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
import fr.intech.cormand.cryptgsm.Conversations.ConversationsAdapter;

public class MainActivity extends AppCompatActivity {

    ImageView btnNewConversation;
    RecyclerView conversationListView;
    List<Conversation> conversationList;
    RecyclerView.Adapter conversationsAdapter;
    TextView noConversationsTextView;

    public RecyclerView.Adapter getConversationsAdapter() {
        return conversationsAdapter;
    }

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
        // Get Conversations
        conversationList = Conversation.loadingAll(this);
        if (conversationList == null) {
            conversationList = new ArrayList<>();
        }
        // Put conversations in recyclerview
        conversationsAdapter = new ConversationsAdapter(conversationList);
        conversationListView.setLayoutManager(new LinearLayoutManager(this));
        conversationListView.setAdapter(conversationsAdapter);

        // No conversations
        if (conversationList.size() < 1) {
            noConversationsTextView = findViewById(R.id.textViewNoConversations);
            noConversationsTextView.setText("No conversations...");
        }

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
        conversationsAdapter.notifyDataSetChanged();
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
        return false;
    }
/*
    private List<Conversation> getAllConversations() {
        List<Conversation> conversations = new ArrayList<>();

        try {
            // Retrieve object
            FileInputStream fis = context.openFileInput(SAVE_CONVERSATIONS_PATH + "save_" + address + ".cryptmsg");
            ObjectInputStream is = new ObjectInputStream(fis);
            c = (Conversation) is.readObject();
            is.close();
            fis.close();

            // Retireve Bitmap
            if(c.getId() != null) {
                InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(),
                        ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(c.getId())));

                if (inputStream != null) {
                    c.setContactPicture(BitmapFactory.decodeStream(inputStream));
                }

                if (inputStream != null) {
                    inputStream.close();
                }
            }

        } catch (FileNotFoundException e) {
            Log.e("File Error", "Not found file : " + SAVE_CONVERSATIONS_PATH + "save_" + address + ".cryptmsg" + e);
        } catch (IOException e) {
            Log.e("File Error", e.toString());
        } catch (ClassNotFoundException e) {
            Log.e("ClassNotFound", e.toString());
        }

        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/conversations"), null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Conversation c = new Conversation();
                for(int i = 0; i < cursor.getColumnCount(); i++)
                {
                    switch (cursor.getColumnName(i)) {
                        case "snippet":
                            c.setSnippet(cursor.getString(i));
                            break;
                        case "thread_id":
                            c.setThread_id(cursor.getString(i));
                            break;
                    }
                }

                // Thread async
                // ConversationTask ct = new ConversationTask(this);
                // ct.execute(c);

                conversations.add(c);
            } while (cursor.moveToNext());
        } else {
            Log.i("MSG", "NO SMS !!!");
        }

        return conversations;
    }
    */
}