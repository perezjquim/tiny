package com.perezjquim.tiny.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.perezjquim.tiny.MainActivity;
import com.perezjquim.tiny.db.DatabaseManager;

import static com.perezjquim.UIHelper.ask;
import static com.perezjquim.UIHelper.askBinary;
import static com.perezjquim.UIHelper.toast;
import static com.perezjquim.tiny.view.BookmarkForm.FLD_NAME;
import static com.perezjquim.tiny.view.BookmarkForm.FLD_URL;

public class BookmarkCardView extends CardView
{
    private LinearLayout content;
    private int id;
    private String url;

    private static final int PADDING = 20;

    private static final int POS_LABEL = 0;

    public static final int REQUEST_CODE_OPEN_BOOKMARK = -999;

    public BookmarkCardView(Context context, int id, String name, String url)
    {
        super(context);

        this.id = id;
        this.url = url;

        setContentPadding(PADDING, PADDING, PADDING, PADDING);

        setOnClickListener((v) ->
        {
            Intent i = new Intent(context, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            i.putExtra("bookmark_url",this.url);
            ((Activity)context).startActivityIfNeeded(i, REQUEST_CODE_OPEN_BOOKMARK);
        });

        content = new LinearLayout(context);
        initLabel(context,name);
        initButtons(context);
        this.addView(content);
    }

    private void initLabel(Context c, String name)
    {
        TextView txtName = new TextView(c);
        txtName.setText(name);
        txtName.setGravity(Gravity.CENTER);
        txtName.setLayoutParams(new TableLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,0.15f));

        content.addView(txtName);
    }

    private void initButtons(Context c)
    {
        String name = ((TextView)(content.getChildAt(POS_LABEL))).getText() + "";

        ImageButton btnEdit = new ImageButton(c);
        btnEdit.setOnClickListener((v) -> editBookmark(id,name,url));
        btnEdit.setImageResource(android.R.drawable.ic_menu_edit);
        btnEdit.setLayoutParams(new TableLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,0.3f));
        content.addView(btnEdit);

        ImageButton btnDelete = new ImageButton(c);
        btnDelete.setOnClickListener((v) -> deleteBookmark(id));
        btnDelete.setImageResource(android.R.drawable.ic_menu_delete);
        btnDelete.setLayoutParams(new TableLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,0.3f));
        content.addView(btnDelete);
    }

    private void editBookmark(int id, String name, String url)
    {
        Context c = getContext();
        BookmarkForm form = new BookmarkForm(c,name,url);

        ask(c,"Edit existing bookmark","Details:","Confirm","Cancel",form,(f) ->
        {
            EditText eName = (EditText) form.getChildAt(FLD_NAME);
            EditText eUrl = (EditText) form.getChildAt(FLD_URL);

            String _name = eName.getText()+"";
            String _url = eUrl.getText()+"";

            DatabaseManager.updateBookmark(id,_name,_url);
            this.url = _url;
            ((TextView)(content.getChildAt(POS_LABEL))).setText(_name);

            toast(c,"Bookmark updated!");
        });
    }

    private void deleteBookmark(int bookmarkID)
    {
        Context c = getContext();
        askBinary(c,"Are you sure you want to delete this bookmark?",null,()->
        {
            DatabaseManager.deleteBookmark(bookmarkID);
            ((ViewGroup)(this.getParent())).removeView(this);
            toast(c,"Bookmark deleted!");
        });
    }
}
