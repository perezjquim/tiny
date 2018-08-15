package com.perezjquim.tiny.view;

import android.content.Context;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.perezjquim.tiny.R;

public class BookmarkForm extends LinearLayout
{
    public static final int FLD_NAME = 0;
    public static final int FLD_URL = 1;

    public BookmarkForm(Context c, String name, String url)
    {
        this(c);

        EditText eName = (EditText) getChildAt(FLD_NAME);
        eName.setText(name);

        EditText eUrl = (EditText) getChildAt(FLD_URL);
        eUrl.setText(url);
    }

    public BookmarkForm(Context c)
    {
        super(c);

        setOrientation(VERTICAL);

        EditText eName = new EditText(c);
        eName.setHint(R.string.hint_name);
        eName.setInputType(InputType.TYPE_CLASS_TEXT);
        addView(eName,FLD_NAME);

        EditText eUrl = new EditText(c);
        eUrl.setHint(R.string.hint_url);
        eUrl.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        addView(eUrl,FLD_URL);
    }
}
