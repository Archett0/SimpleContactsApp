package com.example.mycontacts;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public ListView listView;
    public Adapter mAdapter;
    public static final int CONTACTLOADER = 0;
    public static int LIST_COUNT = 0;
    public static ArrayList<ContactModel> RECENT_CONTACT = new ArrayList<>();
    public Context mainContext = null;
    public Handler handler;
    public Timer timer;


    // 模糊搜索名字
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.searchmenu, menu); // render the search button
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("输入要搜索的姓名");

        String[] projection = {Contact.ContactEntry._ID,
                Contact.ContactEntry.COLUMN_NAME,
                Contact.ContactEntry.COLUMN_EMAIL,
                Contact.ContactEntry.COLUMN_PICTURE,
                Contact.ContactEntry.COLUMN_PHONENUMBER,
                Contact.ContactEntry.COLUMN_WORKPLACE,
                Contact.ContactEntry.COLUMN_HOMEPLACE,
                Contact.ContactEntry.COLUMN_TYPEOFCONTACT
        };
        Context thisContext = this;

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String selection;
                String[] args;
                if (TextUtils.isEmpty(s)) {
                    selection = null;
                    args = null;
                } else {
                    selection = Contact.ContactEntry.COLUMN_NAME + " like ?";
                    args = new String[]{"%" + s + "%"};
                }
                mAdapter = new Adapter(thisContext,
                        getContentResolver().query(Contact.ContactEntry.CONTENT_URI,
                                projection,
                                selection,
                                args,
                                null));
                listView.setAdapter(mAdapter);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                String selection;
                String[] args;
                if (TextUtils.isEmpty(s)) {
                    selection = null;
                    args = null;
                } else {
                    selection = Contact.ContactEntry.COLUMN_NAME + " like ?";
                    args = new String[]{"%" + s + "%"};
                }
                mAdapter = new Adapter(thisContext,
                        getContentResolver().query(Contact.ContactEntry.CONTENT_URI,
                                projection,
                                selection,
                                args,
                                null));
                if (args == null) {
                    Log.i("QueryName", "selection: " + "null" + " args: " + "null" + " ");
                } else {
                    Log.i("QueryName", "selection: " + selection + " args[0]: " + args[0] + " ");
                }
                listView.setAdapter(mAdapter);
                return false;
            }
        });

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 添加按钮
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // 列表
        listView = findViewById(R.id.list);
        mAdapter = new Adapter(this, null);
        listView.setAdapter(mAdapter);

        // 定时Toast
        mainContext = this;
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (RECENT_CONTACT != null && !RECENT_CONTACT.isEmpty()) {
                    String countMessage = "目前条目数量:" + LIST_COUNT;
                    Toast.makeText(mainContext, countMessage, Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < RECENT_CONTACT.size(); ++i) {
                        String message = RECENT_CONTACT.get(i).showContact();
                        Toast.makeText(mainContext, message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mainContext, "没有最近更改的项目", Toast.LENGTH_SHORT).show();
                }
            }
        };
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                handler.sendMessage(message);
            }
        }, 0, 20000);

        // 点击列表项的事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri newUri = ContentUris.withAppendedId(Contact.ContactEntry.CONTENT_URI, id);
                intent.setData(newUri);
                startActivity(intent);
            }
        });

        // 激活Loader
        getLoaderManager().initLoader(CONTACTLOADER, null, this);
    }

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

        return new CursorLoader(this, Contact.ContactEntry.CONTENT_URI,
                projection, null,
                null,
                null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        LIST_COUNT = data.getCount();
        Log.i("LIST COUNT", String.valueOf(LIST_COUNT));
        Log.i("LIST COUNT", String.valueOf(data.getCount()));
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
}