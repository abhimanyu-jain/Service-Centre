package com.abhimanyu.Service_Centre;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.content.Context;
import android.widget.TextView;

/**
 * Created by Abhimanyu Jain on 05-02-2015.
 */
public class Helper {

    public static void actionBarDisplaySettings(ActionBar actionBar)
    {
        //set color of action bar
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff21c800")));
    }

    public static void setFontOfTextView(Context context, TextView textView)
    {
        textView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/abhimanyu.ttf"));
    }

    public static String[] getcompanyNames(Context context)
    {
        return context.getResources().getStringArray(R.array.companyNames);
    }
}
