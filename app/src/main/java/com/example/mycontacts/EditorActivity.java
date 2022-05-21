package com.example.mycontacts;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // 文本框
    EditText mNameEditText, mNumberEditText, mEmailEditText, mWorkEditText, mHomeEditText;

    private Uri mPhotoUri;
    private Uri mCurrentContactUri;
    private String mType = Contact.ContactEntry.TYPEOFCONTACT_PERSONAL;
    ImageView mPhoto;
    private boolean mContactHasChanged = false;
    Spinner mSpinner;
    public static final int LOADER = 0;

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mContactHasChanged = true;
            return false;
        }
    };

    boolean hasAllRequiredValues = false;

    // 实现LoaderManager.LoaderCallbacks<Cursor>的方法
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentContactUri = intent.getData();

        mNameEditText = findViewById(R.id.nameEditText);
        mNumberEditText = findViewById(R.id.phoneEditText);
        mEmailEditText = findViewById(R.id.emailEditText);
        mWorkEditText = findViewById(R.id.workEditText);
        mHomeEditText = findViewById(R.id.homeEditText);
        mPhoto = findViewById(R.id.profile_image);
        mSpinner = findViewById(R.id.spinner);

        // 新建联系人
        if (mCurrentContactUri == null) {
            mPhoto.setImageResource(R.drawable.photo);
            setTitle("新建联系人");
            // 在新建联系人时隐藏删除按钮
            invalidateOptionsMenu();

            // 修改联系人
        } else {
            setTitle("修改联系人");
            getLoaderManager().initLoader(LOADER, null, this);

        }

        mNameEditText.setOnTouchListener(mOnTouchListener);
        mNumberEditText.setOnTouchListener(mOnTouchListener);
        mEmailEditText.setOnTouchListener(mOnTouchListener);
        mWorkEditText.setOnTouchListener(mOnTouchListener);
        mHomeEditText.setOnTouchListener(mOnTouchListener);
        mPhoto.setOnTouchListener(mOnTouchListener);
        mSpinner.setOnTouchListener(mOnTouchListener);

        mPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trySelector();
                mContactHasChanged = true;
            }
        });

        setUpSpinner();
    }

    private void setUpSpinner() {

        ArrayAdapter spinner = ArrayAdapter.createFromResource(this, R.array.arrayspinner, android.R.layout.simple_spinner_item);
        spinner.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSpinner.setAdapter(spinner);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.homephone))) {
                        mType = Contact.ContactEntry.TYPEOFCONTACT_HOME;
                    } else if (selection.equals(getString(R.string.workphone))) {
                        mType = Contact.ContactEntry.TYPEOFCONTACT_WORK;
                    } else {
                        mType = Contact.ContactEntry.TYPEOFCONTACT_PERSONAL;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mType = Contact.ContactEntry.TYPEOFCONTACT_PERSONAL;
            }
        });
    }

    // 图片选择的代码从这里开始
    public void trySelector() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            return;
        }
        openSelector();
    }

    private void openSelector() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
//            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setType(getString(R.string.intent_type));
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image)), 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openSelector();
                } else {
//                    Toast.makeText(getContext(), R.string.noPermissionMarshmallow);
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                mPhotoUri = data.getData();
                mPhoto.setImageURI(mPhotoUri);
                mPhoto.invalidate();
            }
        }
    }
    // 到这里结束


    // 菜单列表从这里开始
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menueditor, menu);
        return true;

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // because we want to hide delete option when we are adding a new contact
        super.onPrepareOptionsMenu(menu);
        if (mCurrentContactUri == null) {
            MenuItem item = (MenuItem) menu.findItem(R.id.delete);
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_save:
                saveContact();
                if (hasAllRequiredValues == true) {
                    finish();
                }
                return true;

            case R.id.delete:
                showDeleteConfirmationDialog();
                return true;

            case android.R.id.home:
                if (!mContactHasChanged) {
                    // 按下返回键提醒需不需要保存
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButton = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };
                showUnsavedChangesDialog(discardButton);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean saveContact() {

        // 保存结果
        String name = mNameEditText.getText().toString().trim();
        String email = mEmailEditText.getText().toString().trim();
        String phone = mNumberEditText.getText().toString().trim();
        String work = mWorkEditText.getText().toString().trim();
        String home = mHomeEditText.getText().toString().trim();

        // 当用户没有输入时
        if (mCurrentContactUri == null
                && TextUtils.isEmpty(name)
                && TextUtils.isEmpty(email)
                && TextUtils.isEmpty(phone)
                && TextUtils.isEmpty(work)
                && TextUtils.isEmpty(home)
                && mType == Contact.ContactEntry.TYPEOFCONTACT_PERSONAL && mPhotoUri == null) {

            hasAllRequiredValues = true;
            return hasAllRequiredValues;
        }

        ContentValues values = new ContentValues();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "必须输入姓名", Toast.LENGTH_SHORT).show();
            return hasAllRequiredValues;
        } else {
            values.put(Contact.ContactEntry.COLUMN_NAME, name);
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "必须输入邮箱", Toast.LENGTH_SHORT).show();
            return hasAllRequiredValues;
        } else {
            values.put(Contact.ContactEntry.COLUMN_EMAIL, email);
        }

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "必须输入电话号码", Toast.LENGTH_SHORT).show();
            return hasAllRequiredValues;
        } else {
            values.put(Contact.ContactEntry.COLUMN_PHONENUMBER, phone);
        }

        if (TextUtils.isEmpty(work)) {
            Toast.makeText(this, "必须输入工作单位", Toast.LENGTH_SHORT).show();
            return hasAllRequiredValues;
        } else {
            values.put(Contact.ContactEntry.COLUMN_WORKPLACE, work);
        }

        if (TextUtils.isEmpty(home)) {
            Toast.makeText(this, "必须输入家庭住址", Toast.LENGTH_SHORT).show();
            return hasAllRequiredValues;
        } else {
            values.put(Contact.ContactEntry.COLUMN_HOMEPLACE, home);
        }

        // 可选参数
        values.put(Contact.ContactEntry.COLUMN_TYPEOFCONTACT, mType);

        if(mPhotoUri == null){
            values.put(Contact.ContactEntry.COLUMN_PICTURE, "");
        } else {
            values.put(Contact.ContactEntry.COLUMN_PICTURE, mPhotoUri.toString());
        }

        if (mCurrentContactUri == null) {
            Uri newUri = getContentResolver().insert(Contact.ContactEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, "保存出错", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "成功保存", Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentContactUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, "更新出错", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "成功更新", Toast.LENGTH_SHORT).show();
            }
        }
        hasAllRequiredValues = true;
        return hasAllRequiredValues;
    }
    // 到这里结束

    // 加载的方法
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {Contact.ContactEntry._ID,
                Contact.ContactEntry.COLUMN_NAME,
                Contact.ContactEntry.COLUMN_EMAIL,
                Contact.ContactEntry.COLUMN_PICTURE,
                Contact.ContactEntry.COLUMN_PHONENUMBER,
                Contact.ContactEntry.COLUMN_WORKPLACE,
                Contact.ContactEntry.COLUMN_HOMEPLACE,
                Contact.ContactEntry.COLUMN_TYPEOFCONTACT
        };

        return new CursorLoader(this, mCurrentContactUri,
                projection, null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            // 获取每列的位置
            int name = cursor.getColumnIndex(Contact.ContactEntry.COLUMN_NAME);
            int email = cursor.getColumnIndex(Contact.ContactEntry.COLUMN_EMAIL);
            int type = cursor.getColumnIndex(Contact.ContactEntry.COLUMN_TYPEOFCONTACT);
            int number = cursor.getColumnIndex(Contact.ContactEntry.COLUMN_PHONENUMBER);
            int work = cursor.getColumnIndex(Contact.ContactEntry.COLUMN_WORKPLACE);
            int home = cursor.getColumnIndex(Contact.ContactEntry.COLUMN_HOMEPLACE);
            int picture = cursor.getColumnIndex(Contact.ContactEntry.COLUMN_PICTURE);

            String contactname = cursor.getString(name);
            String contactemail = cursor.getString(email);
            String contactnumber = cursor.getString(number);
            String contactwork = cursor.getString(work);
            String contacthome = cursor.getString(home);
            String contactpicture = cursor.getString(picture);
            String typeof = cursor.getString(type);
            mPhotoUri = Uri.parse(contactpicture);

            mNumberEditText.setText(contactnumber);
            mNameEditText.setText(contactname);
            mEmailEditText.setText(contactemail);
            mWorkEditText.setText(contactwork);
            mHomeEditText.setText(contacthome);
            mPhoto.setImageURI(mPhotoUri);

            switch (typeof) {
                case Contact.ContactEntry.TYPEOFCONTACT_HOME:
                    mSpinner.setSelection(1);
                    break;

                case Contact.ContactEntry.TYPEOFCONTACT_WORK:
                    mSpinner.setSelection(2);
                    break;

                default:
                    mSpinner.setSelection(0);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNumberEditText.setText("");
        mNameEditText.setText("");
        mEmailEditText.setText("");
        mWorkEditText.setText("");
        mHomeEditText.setText("");
        mPhoto.setImageResource(R.drawable.photo);

    }
    // 到这里结束

    // 编辑界面的用户返回确认功能
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // 编辑界面的确认删除功能
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // 删除选中的联系人
    private void deleteProduct() {
        // Only perform the delete if this is an existing product.
        if (mCurrentContactUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentContactUri, null, null);
            if (rowsDeleted == 0) {
                // 如果没有一行被删除，报错toast
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        // Close the activity
        finish();
    }

    // 在编辑页面按下返回键
    @Override
    public void onBackPressed() {
        // If the product hasn't changed, continue with handling back button press
        if (!mContactHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }
}
