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

import java.util.ArrayList;
import java.util.List;

import fr.intech.cormand.cryptgsm.Msg;
import fr.intech.cormand.cryptgsm.R;

public class ConversationActivity extends Activity {
    private Conversation c = null;
    private TextView displayNameView;
    private TextView phoneNumberView;
    private TextView popupMessageView;
    private ImageView contactPictureView;
    private Button sendBtnView;
    private EditText chatboxView;
    private List<Msg> msgList;

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
                        Log.i("SendSMS", "MSG: " + msg);
                        c.sendSms(msg);
                    }
                });

                // Reload msg
                c.findAllMsg(this);
                // get all crypted msg
                for (int i = 0; i < c.getMsgList().size(); i++) {
                    if (c.getMsgList().get(i).getBody().startsWith("/CryptSMS/")) {
                        Log.i("MSG", "Address: " + c.getMsgList().get(i).getAddress() + " | " + c.getMsgList().get(i).getBody());
                        msgList.add(new Msg(c.getMsgList().get(i).getAddress(), c.getMsgList().get(i).getBody(), c.getMsgList().get(i).getDate_sent()));
                    }
                }



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
}
