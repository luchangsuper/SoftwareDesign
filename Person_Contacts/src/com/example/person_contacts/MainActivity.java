package com.example.person_contacts;

import static com.example.person_contacts.AOpenHelper.DB_NAME;
import static com.example.person_contacts.AOpenHelper.EMAIL;
import static com.example.person_contacts.AOpenHelper.ID;
import static com.example.person_contacts.AOpenHelper.MOBILE;
import static com.example.person_contacts.AOpenHelper.NAME;
import static com.example.person_contacts.AOpenHelper.PHONE;
import static com.example.person_contacts.AOpenHelper.TABLE_NAME;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    AOpenHelper myHelper;
    String[] contactsName;
    String[] contactsPhone;
    int[] contactsId;
    final int MENU_ADD = Menu.FIRST;
    final int MENU_DELETE = Menu.FIRST + 1;
    final int DIALOG_DELETE = 0;
    ListView lv;
    int[] textIds = { R.id.etName, R.id.etPhone, R.id.etMobile, R.id.etEmail, };
    EditText[] textArray;
    BaseAdapter myAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            if (contactsName != null) {
                return contactsName.length;
            } else {
                return 0;
            }
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout ll = new LinearLayout(MainActivity.this);
            ll.setOrientation(LinearLayout.HORIZONTAL);
            TextView tv = new TextView(MainActivity.this);
            tv.setText(contactsName[position]);
            tv.setTextSize(32);
            tv.setTextColor(Color.BLACK);
            tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            tv.setGravity(Gravity.CENTER_VERTICAL);
            TextView tv2 = new TextView(MainActivity.this);
            tv2.setText("[" + contactsPhone[position] + "]");
            tv2.setTextSize(28);
            tv2.setTextColor(Color.BLACK);
            tv2.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            tv2.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
            ll.addView(tv);
            ll.addView(tv2);
            return ll;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myHelper = new AOpenHelper(this, DB_NAME, null, 1);
        lv = (ListView) findViewById(R.id.lv);
        lv.setAdapter(myAdapter);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("cmd", 0); // 0查询 1添加
                intent.putExtra("id", contactsId[position]);
                startActivity(intent);
            }
        });

        lv.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("选择操作");
                menu.add(0, 1, 0, "删除");
            }

        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        String id = String.valueOf(info.position+1);
        switch (item.getItemId()) {
        case 1:
            deleteContact(id); // 删除事件的方法
            getBasicInfo(myHelper);
            myAdapter.notifyDataSetChanged();
            Toast.makeText(this, "删除成功！", Toast.LENGTH_LONG).show();
            return true;
        default:
            Toast.makeText(this, "删除失败，请重试！", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    @Override
    protected void onResume() {
        getBasicInfo(myHelper);
        myAdapter.notifyDataSetChanged();
        super.onResume();
    }

    public void getBasicInfo(AOpenHelper helper) {
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor c = db.query(TABLE_NAME, new String[] { ID, NAME, PHONE, MOBILE,EMAIL}, null, null, null, null, ID);
        int idIndex = c.getColumnIndex(ID);
        int nameIndex = c.getColumnIndex(NAME);
        int phoneindex = c.getColumnIndex(PHONE);
        int mobileIndex = c.getColumnIndex(MOBILE);
        int emailindex = c.getColumnIndex(EMAIL);
        contactsName = new String[c.getCount()];
        contactsPhone = new String[c.getCount()];
        contactsId = new int[c.getCount()];
        int i = 0;
        ContentValues values=new ContentValues();
        for (c.moveToFirst(); !(c.isAfterLast()); c.moveToNext()) {
            contactsName[i] = c.getString(nameIndex);
            contactsPhone[i] = c.getString(phoneindex);
            contactsId[i] = c.getInt(idIndex);                
            i++;
            values.put(ID,i);
            values.put(NAME, c.getString(nameIndex));
            values.put(PHONE, c.getString(phoneindex));
            values.put(MOBILE,c.getString(mobileIndex));
            values.put(EMAIL, c.getString(emailindex));
            db.update(TABLE_NAME, values, ID+"=?", new String[] {c.getInt(idIndex)+""});
        }
        c.close();
        db.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_ADD, 0, R.string.menu_add).setIcon(R.drawable.add);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_ADD:
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("cmd", 1);
            startActivity(intent);
            break;
        case MENU_DELETE:

            showDialog(DIALOG_DELETE);
            break;
        }
        return super.onOptionsItemSelected(item);
    }



    public void deleteContact(String id) {
        SQLiteDatabase db = myHelper.getWritableDatabase();
        db.delete(TABLE_NAME, ID + "=?", new String[] { id });
        db.close();
    }
}
