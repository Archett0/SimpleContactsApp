package com.example.mycontacts;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class Adapter extends CursorAdapter {

    public Adapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.listitem, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameView, numberView, emailView, workView, homeView, typeofContactView;
        ImageView mContactImageView;

        nameView = view.findViewById(R.id.textName);
        emailView = view.findViewById(R.id.textEmail);
        numberView = view.findViewById(R.id.textNumber);
        workView = view.findViewById(R.id.textWork);
        homeView = view.findViewById(R.id.textHome);
        typeofContactView = view.findViewById(R.id.textTypeofContact);
        mContactImageView = view.findViewById(R.id.imageContact);
        /// 获取View的位置
        int name = cursor.getColumnIndex(Contract.ContactEntry.COLUMN_NAME);
        int email = cursor.getColumnIndex(Contract.ContactEntry.COLUMN_EMAIL);
        int type = cursor.getColumnIndex(Contract.ContactEntry.COLUMN_TYPEOFCONTACT);
        int number = cursor.getColumnIndex(Contract.ContactEntry.COLUMN_PHONENUMBER);
        int work = cursor.getColumnIndex(Contract.ContactEntry.COLUMN_WORKPLACE);
        int home = cursor.getColumnIndex(Contract.ContactEntry.COLUMN_HOMEPLACE);
        int picture = cursor.getColumnIndex(Contract.ContactEntry.COLUMN_PICTURE);

        String contactname = cursor.getString(name);
        String contactemail = cursor.getString(email);
        String contactnumber = cursor.getString(number);
        String contactwork = cursor.getString(work);
        String contacthome = cursor.getString(home);
        String contactpicture = cursor.getString(picture);
        String typeof = cursor.getString(type);
        Uri imageUri = Uri.parse(contactpicture);

        nameView.setText(contactname);
        numberView.setText(contactnumber);
        typeofContactView.setText(typeof);
        emailView.setText(contactemail);
        workView.setText(contactwork);
        homeView.setText(contacthome);
        mContactImageView.setImageURI(imageUri);
    }
}