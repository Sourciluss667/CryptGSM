package fr.intech.cormand.cryptgsm.Conversations;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.nfc.Tag;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fr.intech.cormand.cryptgsm.Msg;
import fr.intech.cormand.cryptgsm.R;

public class Conversation implements Serializable {

    private String snippet;
    private String thread_id;
    private String address; // good
    private transient Bitmap contactPicture = null; // good
    private List<Msg> msgList = new ArrayList<>();
    private String displayName = "Anonymous"; // good
    private String id = ""; // good
    private Boolean init;

    public Conversation() {
        init = false;
    }

    public Conversation(String snippet, String thread_id, String address, String contactName, String contactId, Bitmap contactPicture, List<Msg> msgList, String displayName, String id) {
        init = false;
        this.snippet = snippet;
        this.thread_id = thread_id;
        this.address = address;
        this.contactPicture = contactPicture;
        this.msgList = msgList;
        this.displayName = displayName;
        this.id = id;
    }

    public boolean saving(Context context) {
        try {
            String path = "save_" + address.replace("+", "").replace(".", "").replace("-", "") + ".cryptmsg";
            FileOutputStream fos = context.openFileOutput(path, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(this);
            os.close();
            fos.close();
            return true;
        } catch (IOException e) {
            Log.e("File Error", e.toString());
            return false;
        }
    }

    static public Conversation loading(Context context, String address) {
        Conversation c = null;

        try {
            // Retrieve object
            String path = "save_" + address.replace("+", "").replace(".", "").replace("-", "") + ".cryptmsg";
            FileInputStream fis = context.openFileInput(path);
            ObjectInputStream is = new ObjectInputStream(fis);
            c = (Conversation) is.readObject();
            is.close();
            fis.close();

            // Retireve Bitmap
                if(c.getId() != null && c.getId() != "") {
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
            Log.e("File Error", "Not found file : " + context.getFilesDir().getAbsolutePath() + "/save_" + address + ".cryptmsg" + e);
        } catch (IOException e) {
            Log.e("File Error", e.toString());
        } catch (ClassNotFoundException e) {
            Log.e("ClassNotFound", e.toString());
        }

        return c;
    }

    static public List<Conversation> loadingAll(Context context) {
        List<Conversation> result = new ArrayList<>();

        File[] filesInFolder = context.getFilesDir().listFiles();
        for (File file : filesInFolder) { //For each of the entries do:
            if (!file.isDirectory() && file.getName().matches("save_[0-9]*.cryptmsg")) { //check that it's not a dir
                Log.i("Files", file.getName());
                // Retrieve and add to conversation list
                try {
                    FileInputStream fis = context.openFileInput(file.getName());
                    ObjectInputStream is = new ObjectInputStream(fis);
                    Conversation c = (Conversation) is.readObject();
                    is.close();
                    fis.close();

                    if(c.getId() != null && c.getId() != "") {
                        InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(),
                                ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(c.getId())));

                        if (inputStream != null) {
                            c.setContactPicture(BitmapFactory.decodeStream(inputStream));
                        }

                        if (inputStream != null) {
                            inputStream.close();
                        }
                    }

                    result.add(c);

                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    public boolean sendInit() {

        return false;
    }


    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getThread_id() {
        return thread_id;
    }

    public void setThread_id(String thread_id) {
        this.thread_id = thread_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Bitmap getContactPicture() {
        return contactPicture;
    }

    public void setContactPicture(Bitmap contactPicture) {
        this.contactPicture = contactPicture;
    }

    public List<Msg> getMsgList() {
        return msgList;
    }

    public void setMsgList(List<Msg> msgList) {
        this.msgList = msgList;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getInit() {
        return init;
    }
}
