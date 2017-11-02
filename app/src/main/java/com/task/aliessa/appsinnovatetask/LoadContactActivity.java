package com.task.aliessa.appsinnovatetask;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class LoadContactActivity extends AppCompatActivity {
    private static final int CONTACTS_PERMISSIONS_REQUEST = 120;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_contact);
        getContacts("");
    }

    private void getContacts(String token) {

        ArrayList<String> contactFriends = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, CONTACTS_PERMISSIONS_REQUEST);
        } else {
            ContentResolver cr = this.getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

            if (cur != null && cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));

                    if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        if (pCur != null && pCur.moveToFirst()) {
                            String phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            String name = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                            String  contact = name + ":  " + phone.replaceAll(" ", "").replaceAll("-", "");
                            contactFriends.add(contact);
                            pCur.close();
                        }
                    }
                }
                cur.close();
        }
    }
    ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.activity_listview, contactFriends); // simple textview for list item
    ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);
}
}