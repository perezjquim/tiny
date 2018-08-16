package com.perezjquim.tiny;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.perezjquim.tiny.db.Bookmark;
import com.perezjquim.tiny.db.DatabaseManager;
import com.perezjquim.tiny.view.BookmarkCardView;
import com.perezjquim.tiny.view.BookmarkForm;

import static com.perezjquim.UIHelper.ask;
import static com.perezjquim.UIHelper.toast;
import static com.perezjquim.tiny.view.BookmarkForm.FLD_NAME;
import static com.perezjquim.tiny.view.BookmarkForm.FLD_URL;

public class BookmarksActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);
        listBookmarks();
    }

    private void listBookmarks()
    {
        Cursor bookmarks = DatabaseManager.getBookmarks();
        LinearLayout list = findViewById(R.id.list);
        while(bookmarks.moveToNext())
        {
            final int id = bookmarks.getInt(Bookmark.ID.ordinal());
            final String name = bookmarks.getString(Bookmark.NAME.ordinal());
            final String url = bookmarks.getString(Bookmark.URL.ordinal());

            final BookmarkCardView[] card = new BookmarkCardView[1];
            card[0] = new BookmarkCardView(this, id,name,url);
            list.addView(card[0]);
        }
    }

    public void addBookmark(View v)
    {
        BookmarkForm form = new BookmarkForm(this);

        ask(this,"New bookmark","Details:","Confirm","Cancel",form,(f) ->
        {
            EditText eName = (EditText) form.getChildAt(FLD_NAME);
            EditText eUrl = (EditText) form.getChildAt(FLD_URL);

            String _name = eName.getText()+"";
            String _url = eUrl.getText()+"";

            DatabaseManager.insertBookmark(_name,_url);

            LinearLayout list = findViewById(R.id.list);
            list.removeAllViews();
            listBookmarks();

            toast(this,"Bookmark created!");
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_MENU)
        {
            Intent i = new Intent(this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivityIfNeeded(i, 0);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
