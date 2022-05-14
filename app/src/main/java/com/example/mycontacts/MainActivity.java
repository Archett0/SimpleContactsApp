package com.example.mycontacts;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
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

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public ListView listView;
    public Adapter mAdapter;
    public static final int CONTACTLOADER = 0;

    // 模糊搜索名字
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.searchmenu, menu); // render the search button
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("输入要搜索的姓名");

        String[] projection = {Contract.ContactEntry._ID,
                Contract.ContactEntry.COLUMN_NAME,
                Contract.ContactEntry.COLUMN_EMAIL,
                Contract.ContactEntry.COLUMN_PICTURE,
                Contract.ContactEntry.COLUMN_PHONENUMBER,
                Contract.ContactEntry.COLUMN_WORKPLACE,
                Contract.ContactEntry.COLUMN_HOMEPLACE,
                Contract.ContactEntry.COLUMN_TYPEOFCONTACT
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
                    selection = Contract.ContactEntry.COLUMN_NAME + " like ?";
                    args = new String[]{"%"+s+"%"};
                }
                mAdapter = new Adapter(thisContext,
                        getContentResolver().query(Contract.ContactEntry.CONTENT_URI,
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
                    selection = Contract.ContactEntry.COLUMN_NAME + " like ?";
                    args = new String[]{"%"+s+"%"};
                }
                mAdapter = new Adapter(thisContext,
                        getContentResolver().query(Contract.ContactEntry.CONTENT_URI,
                                projection,
                                selection,
                                args,
                                null));
                if (args == null) {
                    Log.i("Query", "selection=" + "null" + " args=" + "null" + " ");
                } else {
                    Log.i("Query", "selection=" + selection + " args[0]=" + args[0] + " ");
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

        // 点击列表项的事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri newUri = ContentUris.withAppendedId(Contract.ContactEntry.CONTENT_URI, id);
                intent.setData(newUri);
                startActivity(intent);

            }
        });

        // 激活Loader
        getLoaderManager().initLoader(CONTACTLOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {Contract.ContactEntry._ID,
                Contract.ContactEntry.COLUMN_NAME,
                Contract.ContactEntry.COLUMN_EMAIL,
                Contract.ContactEntry.COLUMN_PICTURE,
                Contract.ContactEntry.COLUMN_PHONENUMBER,
                Contract.ContactEntry.COLUMN_WORKPLACE,
                Contract.ContactEntry.COLUMN_HOMEPLACE,
                Contract.ContactEntry.COLUMN_TYPEOFCONTACT
        };

        return new CursorLoader(this, Contract.ContactEntry.CONTENT_URI,
                projection, null,
                null,
                null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mAdapter.swapCursor(null);
    }
}