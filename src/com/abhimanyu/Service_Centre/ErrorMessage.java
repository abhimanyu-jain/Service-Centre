package com.abhimanyu.Service_Centre;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by Abhimanyu Jain on 31-01-2015.
 */
public class ErrorMessage extends Activity {

    @Override
    public void onBackPressed()
    {
        //to go back to main activity directly and not the intermediate company centres page when back is pressed
        super.onBackPressed();
        startActivity(new Intent(ErrorMessage.this, MyActivity.class));
        finish();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.errormessage);

        //Setting display of action bar
        ActionBar bar = getActionBar();
        Helper.actionBarDisplaySettings(bar);

        Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();
        String errorMessage = extras.getString(MyActivity.EXTRA_MESSAGE);

        TextView eM = (TextView) findViewById(R.id.errorMessage);
        eM.setText(errorMessage);

    }
}
