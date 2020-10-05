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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import fr.intech.cormand.cryptgsm.R;

public class ConversationActivity extends Activity {
    private Conversation c = null;
    private TextView displayNameView;
    private TextView phoneNumberView;
    private TextView popupMessageView;
    private ImageView contactPictureView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conv_show);

        displayNameView = findViewById(R.id.display_name);
        phoneNumberView = findViewById(R.id.phone_number);
        contactPictureView = findViewById(R.id.imageView3);
        popupMessageView = findViewById(R.id.popup_message_view);

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
            if (c.getInit()) {
                //Affiche la conv etc...


            } else { // Init non fait
                // Send init
                c.sendInitMsg(this);

                // Affiche le message
                popupMessageView.setTextColor(Color.rgb(255, 0, 0));
                popupMessageView.setText("Waiting response of contact...");
            }

        }
    }
}
