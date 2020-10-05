package fr.intech.cormand.cryptgsm;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;

import fr.intech.cormand.cryptgsm.Conversations.Conversation;
import fr.intech.cormand.cryptgsm.Conversations.ConversationActivity;

public class NewConversationActivity extends AppCompatActivity {

    private ImageView contactImageView;
    private EditText textPhoneView;
    private TextView textContactName;
    private Button initBtn;
    private Conversation conversation;

    private static final int CONTACT_PICKER_RESULT = 1001;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_conversations);

        conversation = new Conversation();
        contactImageView = findViewById(R.id.imageView);
        textPhoneView = findViewById(R.id.editTextPhone);
        initBtn = findViewById(R.id.button);
        textContactName = findViewById(R.id.textView);

        // Click on contact image can select a contact
        contactImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open contacts
                Log.i("CLICK", "Open Contact for new conv");
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
            }
        });

        // Click on init
        initBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Init conv with address in textPhoneView
                conversation.setAddress(textPhoneView.getText().toString().replaceAll(" ", ""));
                if (PhoneNumberUtils.isGlobalPhoneNumber(conversation.getAddress())) {
                    // Good phone number
                    // Verify if conversation not exist ?
                    // Verify if address is link at a contact

                    // Save conversation object
                    conversation.saving(v.getContext());

                    // Go on conversation view
                    Intent intent = new Intent(v.getContext(), ConversationActivity.class);
                    intent.putExtra("address", conversation.getAddress());
                    startActivity(intent);
                    finish();

                } else {
                    //Phone number not valid
                    Toast.makeText(NewConversationActivity.this, "Phone Number not valid !", Toast.LENGTH_LONG).show();
                }
            }
        });

        // When phone number change
        textPhoneView.addTextChangedListener(new TextWatcher() {
            private CharSequence textBefore;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                textBefore = s;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (textBefore != s) {
                    // Verify if text is link to a contact
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CONTACT_PICKER_RESULT:
                    // handle contact results
                    Uri result = data.getData();
                    Cursor cursor = getContentResolver().query(result, null, null, null, null);
                    cursor.moveToFirst();

                    if (cursor.getInt(cursor.getColumnIndex("has_phone_number")) == 1) {
                        // Get DisplayName & Display
                        conversation.setDisplayName(cursor.getString(cursor.getColumnIndex("display_name")));
                        if (conversation.getDisplayName() != "") {
                            textContactName.setText(conversation.getDisplayName());
                        }

                        conversation.setId(cursor.getString(cursor.getColumnIndex("_id")));
                        // Get Image & Display
                        try {
                            if(conversation.getId() != null) {
                                InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(),
                                        ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(conversation.getId())));

                                if (inputStream != null) {
                                    conversation.setContactPicture(BitmapFactory.decodeStream(inputStream));
                                    contactImageView.setImageBitmap(conversation.getContactPicture());
                                } else {
                                    contactImageView.setImageResource(R.drawable.default_contact_image);
                                    conversation.setContactPicture(((BitmapDrawable)contactImageView.getDrawable()).getBitmap());
                                }

                                if (inputStream != null) {
                                    inputStream.close();
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // Get Address
                        Cursor cursor1 = getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                                new String[]{conversation.getId()}, null);

                        if (cursor1.moveToNext())
                        {
                            conversation.setAddress(cursor1.getString(cursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                            textPhoneView.setText(conversation.getAddress());
                        }


                        if (cursor1 != null) {
                            cursor1.close();
                        }


                    } else {
                        Toast.makeText(NewConversationActivity.this, "Contact don't have phone number !", Toast.LENGTH_LONG).show();
                    }

                    if (cursor != null) {
                        cursor.close();
                    }

                    break;
            }

        } else {
            // gracefully handle failure
            Toast.makeText(NewConversationActivity.this, "Error pick contact !", Toast.LENGTH_LONG).show();
            Log.w("Debug", "Warning: activity result not ok");
        }
    }
}
