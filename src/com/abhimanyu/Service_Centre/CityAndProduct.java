package com.abhimanyu.Service_Centre;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.database.Cursor;
import android.app.Activity;
import android.widget.AutoCompleteTextView;

/**
 * Created by Abhimanyu Jain on 31-01-2015.
 */
public class CityAndProduct extends Activity {

    public final static String MESSAGE1 = "MESSAGE1";
    public final static String MESSAGE2 = "MESSAGE2";
    public final static String MESSAGE3 = "MESSAGE3";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city_and_product);

        //Setting display of action bar
        ActionBar bar = getActionBar();
        Helper.actionBarDisplaySettings(bar);

        //Receive company id from MyActivity.java
        Intent intent = getIntent();
        final Integer message = intent.getIntExtra(MyActivity.EXTRA_MESSAGE, 0) +1; //+1 required because arrays start from zero but DB indices start from 1

        DatabaseHelper DbHelper = new DatabaseHelper(this);

        // Get a reference to the AutoCompleteTextView in the layout
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.autocomplete_city);
        //Get list of cities
        Cursor list_of_cities = DbHelper.getListCities(message);
        String[] cities = DbHelper.getStringArrayFromCursor(list_of_cities, DatabaseHelper.ServiceCentres.COLUMN_CITY);
        // Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, cities);
        textView.setAdapter(adapter);

        // Get a reference to the AutoCompleteTextView in the layout
        AutoCompleteTextView textView2 = (AutoCompleteTextView) findViewById(R.id.autocomplete_product);
        //Get list of products
        String[] products = DbHelper.getStringArrayFromCursor(DbHelper.getListProducts(list_of_cities), DatabaseHelper.ServiceCentre_ProductMapping.COLUMN_PRODUCT);
        // Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, products);
        textView2.setAdapter(adapter2);

        Button button = (Button) findViewById(R.id.button);
        Helper.setFontOfTextView(this, button);
        TextView tv1 = (TextView) findViewById(R.id.textView);
        Helper.setFontOfTextView(this, tv1);
        TextView tv2 = (TextView) findViewById(R.id.textView2);
        Helper.setFontOfTextView(this, tv2);

        button.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    String city_name = ((AutoCompleteTextView) findViewById(R.id.autocomplete_city)).getText().toString();
                    String product_name = ((AutoCompleteTextView) findViewById(R.id.autocomplete_product)).getText().toString();
                    Intent orderIntent = new Intent(CityAndProduct.this, CompanyCentres.class);
                    orderIntent.putExtra(MESSAGE1, city_name);
                    orderIntent.putExtra(MESSAGE2, product_name);
                    orderIntent.putExtra(MESSAGE3, message);
                    startActivity(orderIntent);
                }
            });

        final AutoCompleteTextView autoCompleteTextView_product = (AutoCompleteTextView) findViewById(R.id.autocomplete_product);
        autoCompleteTextView_product.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(autoCompleteTextView_product.getWindowToken(), 0);

            }
        });

        final AutoCompleteTextView autoCompleteTextView_city = (AutoCompleteTextView) findViewById(R.id.autocomplete_city);
        autoCompleteTextView_city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(autoCompleteTextView_city.getWindowToken(), 0);

            }
        });
    }
}