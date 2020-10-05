package fr.intech.cormand.cryptgsm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import fr.intech.cormand.cryptgsm.Conversations.Conversation;

public class SMSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String address = "", body = "";

        Log.i("SMSReceive", "SMS RECU");

        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i = 0; i < msgs.length; i++) {
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                address = msgs[i].getOriginatingAddress();
                body = msgs[i].getMessageBody().toString();
            }
            if (body.startsWith("/CryptSMS/")) {
                // Refresh conv !!
                MainActivity.refreshData(address, context);
            }

        }
    }
}
