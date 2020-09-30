package fr.intech.cormand.cryptgsm.OldConversations;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import fr.intech.cormand.cryptgsm.Msg;

public class Conversation {
    private String snippet;
    private String thread_id;
    private String msg_count;
    private String address;
    private String contactName = "";
    private String contactId = null;
    private Bitmap contactPicture = null;
    private List<Msg> msgList = new ArrayList<>();
    private Context context;

    public Conversation () {

    }

    public Conversation(Context context) {
        this.context = context;
    }

    public Conversation(Context context, String snippet, String thread_id, String msg_count) {
        this.msg_count = msg_count;
        this.snippet = snippet;
        this.thread_id = thread_id;
    }

    public boolean startMore () {
        getMoreOnConversation();
        getAllMsg();
        return true;
    }

    private void getMoreOnConversation () {
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms"), new String[] {"thread_id", "address"}, "thread_id LIKE " + thread_id, null, null);
        if (cursor.moveToFirst()) {
            address = cursor.getString(cursor.getColumnIndex("address"));
        }

        // Retrieve if in contact
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));

        String[] projection = new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID};
        cursor = context.getContentResolver().query(uri, projection,null,null,null);
        if (cursor != null) {
            if(cursor.moveToFirst()) {
                contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
            }
            cursor.close();
        }

        try {
            if(contactId != null) {
                InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(),
                        ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(contactId)));

                if (inputStream != null) {
                    contactPicture = BitmapFactory.decodeStream(inputStream);
                }

                if (inputStream != null) {
                    inputStream.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void getAllMsg () {
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms"), new String[] {"_id", "thread_id", "address", "body", "date_sent"}, "thread_id LIKE " + thread_id, null, "date desc");
        if (cursor.moveToFirst()) {
            for (int x = 0; x < cursor.getCount(); x++) {
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    String addr = cursor.getString(cursor.getColumnIndex("address"));
                    String body = cursor.getString(cursor.getColumnIndex("body"));
                    String date_sent = cursor.getString(cursor.getColumnIndex("date_sent"));
                    msgList.add(new Msg(addr, body, date_sent));
                }
                cursor.moveToNext();
            }
        } else {
            Log.e("CURSOR", "Error with cursor READ SMS");
        }
    }

    public void setMsg_count(String msg_count) {
        this.msg_count = msg_count;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public void setThread_id(String thread_id) {
        this.thread_id = thread_id;
    }

    public void setContactPicture(Bitmap contactPicture) {
        this.contactPicture = contactPicture;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public String getMsg_count() {
        return msg_count;
    }

    public String getThread_id() {
        return thread_id;
    }

    public String getAddress() {
        return address;
    }

    public String getContactId() {
        return contactId;
    }

    public Bitmap getContactPicture() {
        return contactPicture;
    }

    public String getContactName() {
        return contactName;
    }

    public List<Msg> getMsgList() {
        return msgList;
    }

    public String getSnippet() {
        return snippet;
    }
}
