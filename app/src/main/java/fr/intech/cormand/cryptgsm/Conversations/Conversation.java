package fr.intech.cormand.cryptgsm.Conversations;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;

import fr.intech.cormand.cryptgsm.Msg;
import fr.intech.cormand.cryptgsm.R;

public class Conversation implements Serializable {

    private String snippet;
    private String thread_id;
    private String address; // good
    private transient Bitmap contactPicture = null; // good
    private transient List<Msg> msgList = new ArrayList<>();
    private String displayName = "Anonymous"; // good
    private String id = ""; // good
    private Boolean initSend;
    private Boolean initResponse;
    private String publicKey = "";
    private String privateKey = "";
    private String publicKeyContact = "";
    private transient SmsManager smsManager = null;

    public Conversation() {
        initSend = false;
        initResponse = false;
        smsManager = SmsManager.getDefault();
    }

    private void generateKey () throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");

        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

        // 512 is keysize
        keyGen.initialize(512, random);

        KeyPair generateKeyPair = keyGen.generateKeyPair();
        publicKey = Base64.encodeToString(generateKeyPair.getPublic().getEncoded(), Base64.DEFAULT);
        privateKey = Base64.encodeToString(generateKeyPair.getPrivate().getEncoded(), Base64.DEFAULT);

        Log.i("KEY", "PUBLIC: " + publicKey);
        Log.i("KEY", "PRIVATE: " + privateKey);
    }

    public void sendSms (String body) {
        if (smsManager == null) {
            smsManager = SmsManager.getDefault();
        }
        if (publicKeyContact == "") {
            Log.e("PUBLICKKEYCONTACT", "PublicKeyContact is empty !");
        }

        String str = encrypt(publicKey, body);
        String str2 = decrypt(privateKey, str);
        Log.i("Body", body);
        Log.i("BodyEncrypted", str);
        Log.i("BodyDecrypted", str2);


        String bodyCrypt = encrypt(publicKeyContact, body);

        smsManager.sendTextMessage(this.address, null, "/CryptSMS/" + bodyCrypt + "/CryptSMS/", null, null);
    }

    public void sendInitMsg(final Context ctx) {

        // Generate Key
        try {
            if (publicKey == "" && privateKey == "") {
                generateKey();
            }
        } catch (Exception e) {
            Log.e("sendInitMsg", "Generate Key Error !");
            e.printStackTrace();
        }

        // Send SMS init
        // MSG pending intent
        if (smsManager == null) {
            smsManager = SmsManager.getDefault();
        }

        PendingIntent sentPendingIntent = PendingIntent.getBroadcast(ctx, 0, new Intent("SMS_SENT"), 0);
        ctx.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (getResultCode() == Activity.RESULT_OK) {
                    // Message en envoi
                    Log.i("sentPendingIntent", "Envoi !");
                    setInit(true);
                    saving(ctx);
                } else {
                    Toast.makeText(ctx.getApplicationContext(), "Error sending init message !", Toast.LENGTH_LONG).show();
                    Log.e("MSG-Init", "ERROR send PENDING INTENT : " + getResultCode());
                }
            }
        }, new IntentFilter("SMS_SENT"));

        // MSG delivery intent
        PendingIntent deliveryPendingIntent = PendingIntent.getBroadcast(ctx, 0, new Intent("SMS_DELIVERED"), 0);
        ctx.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (getResultCode() == Activity.RESULT_OK) {
                    // Message envoyé
                    Log.i("deliveryPendingIntent", "Envoyer !");
                    setInit(true);
                } else {
                    Toast.makeText(ctx.getApplicationContext(), "Error sending init message !", Toast.LENGTH_LONG).show();
                    Log.e("MSG-Init", "ERROR delivery PENDING INTENT : " + getResultCode());
                }
            }
        }, new IntentFilter("SMS_DELIVERED"));

        int index = this.publicKey.length() / 2;
        String firstPart = this.publicKey.substring(0, index);
        String secondPart = this.publicKey.substring(index);

        smsManager.sendTextMessage(this.address, null, "/CryptSMS-init1/" + firstPart + "/CryptSMS-init1/", sentPendingIntent, deliveryPendingIntent);
        smsManager.sendTextMessage(this.address, null, "/CryptSMS-init2/" + secondPart + "/CryptSMS-init2/", sentPendingIntent, deliveryPendingIntent);

        saving(ctx);
    }

    private static String encrypt(String publicKey, String inputData) {
        try {
            PublicKey key = KeyFactory.getInstance("RSA")
                    .generatePublic(new X509EncodedKeySpec(Base64.decode(publicKey, Base64.DEFAULT)));

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            String encryptedString = Base64.encodeToString(cipher.doFinal(inputData.getBytes()), Base64.DEFAULT);

            return encryptedString;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String decrypt(String privateKey, String inputData) {
        try {
            PrivateKey key = KeyFactory.getInstance("RSA")
                    .generatePrivate(new PKCS8EncodedKeySpec(Base64.decode(privateKey, Base64.DEFAULT)));

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, key);

            String decryptedBytes = new String(cipher.doFinal(Base64.decode(inputData, Base64.DEFAULT)));

            return decryptedBytes;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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

    public void findAllMsg (Context ctx) {
        if (msgList == null) {
            msgList = new ArrayList<>();
        }
        Uri uriSMSURI = Uri.parse("content://sms/inbox");
        Cursor cur = ctx.getContentResolver().query(uriSMSURI, new String[] {"address", "body", "date_sent"},"address='" + this.address + "'", null, null);

        while (cur != null && cur.moveToNext()) {
            Msg m = new Msg(cur.getString(cur.getColumnIndex("address")), cur.getString(cur.getColumnIndexOrThrow("body")), cur.getString(cur.getColumnIndexOrThrow("date_sent")));
            if (!msgList.contains(m)) {
                msgList.add(m);
            }
        }

        if (cur != null) {
            cur.close();
        }
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

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public void setPublicKeyContact(String publicKeyContact) {
        this.publicKeyContact = publicKeyContact;
    }

    public String getPublicKeyContact() {
        return publicKeyContact;
    }

    public void setInit(Boolean init) {
        this.initSend = init;
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
        return initSend;
    }

    public Boolean getInitResponse() {
        return initResponse;
    }

    public void setInitResponse(Boolean initResponse) {
        this.initResponse = initResponse;
    }
}
