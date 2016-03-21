package com.abhimanyu.Service_Centre;

import android.app.ActionBar;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.content.Intent;

import android.view.inputmethod.InputMethodManager;
import android.content.Context;


public class MyActivity extends Activity {
    public final static String EXTRA_MESSAGE = "com.abhimanyu.Service_Centre.MESSAGE";
    public final static String MESSAGE1 = "MESSAGE1";
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //Setting display of action bar
        ActionBar bar = getActionBar();
        Helper.actionBarDisplaySettings(bar);

        //Set font of textview
        TextView myTextView=(TextView)findViewById(R.id.entername);
        TextView myCityTextView=(TextView)findViewById(R.id.entercity);
        Helper.setFontOfTextView(this, myTextView);
        Helper.setFontOfTextView(this, myCityTextView);


        // Get a reference to the AutoCompleteTextView in the layout
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.autocomplete_company);

        // Get the string array
        //final String[] companyNames = getResources().getStringArray(R.array.companyNames);
        final String[] companyNames = Helper.getcompanyNames(this);

        // Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, companyNames);
        textView.setAdapter(adapter);

        Button button = (Button) findViewById(R.id.button);
        Helper.setFontOfTextView(this, button);

        button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                String city = ((AutoCompleteTextView) findViewById(R.id.autocomplete_city)).getText().toString();
                String name = ((AutoCompleteTextView) findViewById(R.id.autocomplete_company)).getText().toString();
                Integer message = -1;
                //Attempt to find name of the company entered from list
                while (companyNames.length > ++message) {
                    if (companyNames[message].equals(name)) {
                        break;
                    }
                }

                //Company name not found, redirecting to error page
                if (companyNames.length == message) {
                    Intent orderIntent = new Intent(MyActivity.this, ErrorMessage.class);
                    orderIntent.putExtra(EXTRA_MESSAGE, "Sorry, but no such company name was found!!!");
                    startActivity(orderIntent);

                }
                //Company name found, proceeding to select city and product
                else {
                    Intent orderIntent = new Intent(MyActivity.this, CompanyCentres.class);
                    orderIntent.putExtra(EXTRA_MESSAGE, message + 1);//+1 required because arrays start from zero but DB indices start from 1
                    orderIntent.putExtra(MESSAGE1, city);
                    startActivity(orderIntent);
                }
            }
        });

        //Hide keypad once a selection is made
        final AutoCompleteTextView autoCompleteTextViewCity = (AutoCompleteTextView) findViewById(R.id.autocomplete_city);
        autoCompleteTextViewCity.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(autoCompleteTextViewCity.getWindowToken(), 0);
            }
        });

        //Hide keypad once a selection is made
        final AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autocomplete_company);
        autoCompleteTextView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(), 0);

                String name = ((AutoCompleteTextView) findViewById(R.id.autocomplete_company)).getText().toString();

                Integer message = -1;
                //Attempt to find name of the company entered from list
                while(companyNames.length > ++message)
                {
                    if (companyNames[message].equals(name))
                    {
                        break;
                    }
                }
                message = message + 1;//+1 required because arrays start from zero but DB indices start from 1
                DatabaseHelper DbHelper = new DatabaseHelper(getApplicationContext());

                // Get a reference to the AutoCompleteTextView in the layout
                AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.autocomplete_city);
                //Get list of cities
                Cursor list_of_cities = DbHelper.getListCities(message);
                String[] cities = DbHelper.getStringArrayFromCursor(list_of_cities, DatabaseHelper.ServiceCentres.COLUMN_CITY);
                // Create the adapter and set it to the AutoCompleteTextView
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, cities);
                textView.setAdapter(adapter);

            }
        });
    }
}