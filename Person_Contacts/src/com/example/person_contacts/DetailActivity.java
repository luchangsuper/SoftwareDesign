package com.example.person_contacts;

import static com.example.person_contacts.AOpenHelper.*;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

@SuppressLint("NewApi")
public class DetailActivity extends Activity {
    AOpenHelper myHelper;
    final int MENU_ADD = Menu.FIRST;
    final int MENU_MODIFY = Menu.FIRST + 1;
    final int MENU_DELETE = Menu.FIRST + 2;
    final int MENU_SAVE = Menu.FIRST + 3;
    int id = -1;
    int[] textIds = { R.id.etName, R.id.etPhone, R.id.etMobile, R.id.etEmail, };
    EditText[] textArray;
    ImageButton ibSave;
    int status = -1;// 0是查看 1是添加
    View.OnClickListener myListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            String[] strArray = new String[textArray.length];
            for (int i = 0; i < strArray.length; i++) {
                strArray[i] = textArray[i].getText().toString().trim();
            }
            if (strArray[0].equals("") || strArray[1].equals("")) {
                Toast.makeText(DetailActivity.this, "对不起，姓名和电话必须填写完整!", Toast.LENGTH_LONG).show();
                status = -1;
            }
            Intent intent = new Intent();
            switch (status) {
            case 0:
                updateContact(strArray);
                intent = new Intent();
                intent.setClass(DetailActivity.this, MainActivity.class);
                startActivity(intent);
                DetailActivity.this.finish();
                break;
            case 1:
                insertContact(strArray);
                intent = new Intent();
                intent.setClass(DetailActivity.this, MainActivity.class);
                startActivity(intent);
                DetailActivity.this.finish();
                break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);
        textArray = new EditText[textIds.length];
        for (int i = 0; i < textIds.length; i++) {
            textArray[i] = (EditText) findViewById(textIds[i]);
        }
        ibSave = (ImageButton) findViewById(R.id.idSave);
        ibSave.setOnClickListener(myListener);
        myHelper = new AOpenHelper(this, AOpenHelper.DB_NAME, null, 1);
        Intent intent = getIntent();
        status = intent.getExtras().getInt("cmd");// 读命令类型
        switch (status) {
        case 0: // 查询联系人详细信息
            id = intent.getExtras().getInt("id");
            SQLiteDatabase db = myHelper.getWritableDatabase();
            Cursor c = db.query(AOpenHelper.TABLE_NAME, new String[] { NAME, PHONE, MOBILE, EMAIL }, ID + "=?",
                    new String[] { id + "" }, null, null, null);
            if (c.getCount() == 0) {
                Toast.makeText(this, "对不起，没有找到对应联系人", Toast.LENGTH_LONG).show();
            } else {
                c.moveToFirst();
                textArray[0].setText(c.getString(0));
                textArray[1].setText(c.getString(1));
                textArray[2].setText(c.getString(2));
                textArray[3].setText(c.getString(3));
            }
            c.close();
            db.close();
            break;
        case 1: // 新建
            for (EditText et : textArray) {
                et.getEditableText().clear();
            }
            break;
        }
    }

    public void insertContact(String[] strArray) {
        SQLiteDatabase db = myHelper.getWritableDatabase();
        Cursor c = db.query(TABLE_NAME, new String[] { ID, NAME, PHONE }, null, null, null, null, ID);
        ContentValues values = new ContentValues();
        values.put(ID, c.getCount() + 1);
        values.put(NAME, strArray[0]);
        values.put(PHONE, strArray[1]);
        values.put(MOBILE, strArray[2]);
        values.put(EMAIL, strArray[3]);
        long count = db.insert(TABLE_NAME, ID, values);
        db.close();
        if (count == -1) {
            Toast.makeText(this, "添加联系人失败！", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "添加联系人成功！", Toast.LENGTH_LONG).show();
        }
    }

    public void updateContact(String[] strArray) {
        SQLiteDatabase db = myHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME, strArray[0]);
        values.put(PHONE, strArray[1]);
        values.put(MOBILE, strArray[2]);
        values.put(EMAIL, strArray[3]);
        int count = db.update(TABLE_NAME, values, ID + "=?", new String[] { id + "" });
        db.close();
        if (count == 1) {
            Toast.makeText(this, "修改联系人成功！", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "修改联系人失败！", Toast.LENGTH_LONG).show();
        }
    }
}
