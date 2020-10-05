package fr.intech.cormand.cryptgsm.Conversations;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import fr.intech.cormand.cryptgsm.Msg;
import fr.intech.cormand.cryptgsm.R;

public class ConversationActivity extends Activity {
    private Conversation c = null;
    private TextView displayNameView;
    private TextView phoneNumberView;
    private TextView popupMessageView;
    private ImageView contactPictureView;
    private RecyclerView msgListView;
    private Button sendBtnView;
    private EditText chatboxView;
    private List<Msg> msgList;
    private MsgAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conv_show);

        displayNameView = findViewById(R.id.display_name);
        phoneNumberView = findViewById(R.id.phone_number);
        contactPictureView = findViewById(R.id.imageView3);
        popupMessageView = findViewById(R.id.popup_message_view);
        sendBtnView = findViewById(R.id.button_chatbox_send);
        chatboxView = findViewById(R.id.edittext_chatbox);
        msgList = new ArrayList<>();

        // Get conversation object
        if (getIntent().getExtras() != null) {
            String address_temp = (String) getIntent().getExtras().get("address");
            c = Conversation.loading(this, address_temp);
        }

        if (c != null) {
            displayNameView.setText(c.getDisplayName());
            phoneNumberView.setText(c.getAddress());
            if (c.getContactPicture() != null) {
                contactPictureView.setImageBitmap(c.getContactPicture());
            }

            // Init fait
            if (c.getInit() && c.getInitResponse()) {
                //Affiche la conv etc...
                popupMessageView.setTextColor(Color.rgb(0, 255, 0));
                popupMessageView.setText("Init success !");

                sendBtnView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Send msg in chatboxView
                        String msg = chatboxView.getText().toString();
                        Msg m = new Msg();
                        m.setAddress(c.getAddress());
                        m.setBody(msg);
                        m.setDate_sent(String.valueOf(System.currentTimeMillis()));
                        m.setIsUser(true);
                        msgList.add(m);
                        c.getMsgList().add(m);
                        refreshUI();
                        Log.i("SendSMS", "MSG: " + msg);
                        c.sendSms(msg);
                        chatboxView.setText("");
                        c.saving(v.getContext());
                    }
                });

                // Reload msg
                refreshData();

                msgListView = findViewById(R.id.recyclerview_message_list);
                msgListView.setLayoutManager(new LinearLayoutManager(this));
                adapter = new MsgAdapter(msgList);
                msgListView.setAdapter(adapter);

            } else { // Init non fait
                // Send init
                if (!c.getInit()) {
                    c.sendInitMsg(this);
                }

                // Affiche le message
                popupMessageView.setTextColor(Color.rgb(255, 0, 0));
                popupMessageView.setText("Waiting response of contact...");
            }

        }
    }

    public void refreshUI () {
        adapter.notifyDataSetChanged();
    }

    public void refreshData () {
        //Log.i("DEBUG", "Size : " + c.getMsgList().size());
        c.findAllMsg(this);
        //Log.i("DEBUG", "Size : " + c.getMsgList().size());

        // get all crypted msg
        for (int i = 0; i < c.getMsgList().size(); i++) {
            if (c.getMsgList().get(i).getBody().startsWith("/CryptSMS/")) {
                // Decrypt sms
                String s = c.getMsgList().get(i).getBody();
                s = s.replaceAll("/CryptSMS/", "");
                s = Conversation.decrypt(c.getPrivateKey(), s);
                Log.i("SMS", "body: " + s);
                Msg m = new Msg(c.getMsgList().get(i).getAddress(), s, c.getMsgList().get(i).getDate_sent());
                if (!msgList.contains(m)) {
                    msgList.add(m);
                }
            } else if (c.getMsgList().get(i).getIsUser()) {
                msgList.add(c.getMsgList().get(i));
            }
        }

        //Log.i("DEBUG", "Size : " + c.getMsgList().size());
        //Log.i("DEBUG", "msgList Size : " + msgList.size());


        // Delete duplicates (en theorie :()
        msgList = (List<Msg>) (Object)msgList.stream().distinct().collect(Collectors.toList());

        // SOrt with date
        msgList.sort(new Comparator<Msg>() {
            @Override
            public int compare(Msg o1, Msg o2) {
                if ((o1.getDate_sent() != null && o2.getDate_sent() != null) && Long.parseLong(o1.getDate_sent()) > Long.parseLong(o2.getDate_sent())) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });

        c.saving(this);
    }
}
