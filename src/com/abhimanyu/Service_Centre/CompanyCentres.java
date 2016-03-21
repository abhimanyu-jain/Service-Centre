package com.abhimanyu.Service_Centre;

import android.app.ActionBar;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.*;
import android.database.Cursor;
import android.app.ListActivity;
import android.net.Uri;

/**
 * Created by Abhimanyu Jain on 31-12-2014.
 */
public class CompanyCentres extends ListActivity
 {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.companycentres);

        //Setting display of action bar
        ActionBar bar = getActionBar();
        Helper.actionBarDisplaySettings(bar);

        Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();
        String city_name = extras.getString(MyActivity.MESSAGE1);
        String product_name =  "";
        //extras.getString(CityAndProduct.MESSAGE2);
        Integer message = intent.getIntExtra(MyActivity.EXTRA_MESSAGE, 0);
        DatabaseHelper DbHelper = new DatabaseHelper(getApplicationContext());
        Cursor cursor = DbHelper.getListCenters(message, city_name, product_name);

        if (cursor.getCount() == 0)
        {
            String EXTRA_MESSAGE = "com.abhimanyu.Service_Centre.MESSAGE";
            Intent orderIntent = new Intent(CompanyCentres.this, ErrorMessage.class);
            orderIntent.putExtra(EXTRA_MESSAGE, "Sorry, but no service centres were found!!!");
            startActivity(orderIntent);
        }

        else {
            String[] fromColumns = {
                    DatabaseHelper.ServiceCentres.COLUMN_NAME,
                    DatabaseHelper.ServiceCentres.COLUMN_ADDRESS,
                    DatabaseHelper.ServiceCentres.COLUMN_PHONE,
                    DatabaseHelper.ServiceCentres.COLUMN_CITY,
                    DatabaseHelper.ServiceCentres.COLUMN_EMAIL
            };

            int[] toViews = {R.id.name, R.id.address, R.id.phone};
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.simple_list_item, cursor, fromColumns, toViews, 0);
            ListView listView = getListView();
            listView.setAdapter(adapter);
        }
    }

    public void onPhoneClick(View v) {
                // Perform action on click
                String num = ((TextView)v).getText().toString();
                String number = "tel:" + num;
                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(number));
                startActivity(callIntent);
            }
        }