package com.abhimanyu.Service_Centre;

import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.content.ContentValues;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Arrays;

/**
 * Created by Abhimanyu Jain on 29-12-2014.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.

    public static final String DATABASE_NAME = "ServiceCentre.db";
    public static final int DATABASE_VERSION = 1;
    public Context c;
    private static SQLiteDatabase db;

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        c = context;
        this.db = getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Companies.SQL_CREATE_TABLE);
        db.execSQL(ServiceCentres.SQL_CREATE_TABLE);
        db.execSQL(ServiceCentre_ProductMapping.SQL_CREATE_TABLE);
        String[] list_of_companies = Helper.getcompanyNames(c);

        for (int i = 0; i < list_of_companies.length; i++)
        {
            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(Companies.COLUMN_NAME, list_of_companies[i]);

            // Insert the new row, returning the primary key value of the new row
            long newRowId;
            newRowId = db.insert(Companies.TABLE_NAME, null, values);
        }

        class centre_info{
            private long _id;
            private String name;
            private String address;
            private String phone;
            private String city;
            private String email;
            private String company_id;
            ArrayList<String> products;

            centre_info(String n, String a, String p, String c, String e, String ci, ArrayList<String> pr)
            {
                _id = -1;
                name = n;
                address = a;
                phone = p;
                city = c;
                email = e;
                company_id = ci;
                products = pr;
            }

            long insert_into_db(SQLiteDatabase db)
            {
                // Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();
                values.put(ServiceCentres.COLUMN_NAME, name);
                values.put(ServiceCentres.COLUMN_ADDRESS, address);
                values.put(ServiceCentres.COLUMN_PHONE, phone);
                values.put(ServiceCentres.COLUMN_CITY, city);
                values.put(ServiceCentres.COLUMN_EMAIL, email);
                values.put(ServiceCentres.COLUMN_COMPANY, company_id);

                // Insert the new row, returning the primary key value of the new row
                long newRowId;
                newRowId = db.insert(
                        ServiceCentres.TABLE_NAME,
                        null,
                        values);

                _id = newRowId;
                insert_service_centre_product_mapping(db);
                return newRowId;
            }

            long insert_service_centre_product_mapping(SQLiteDatabase db)
            {
                // Create a new map of values, where column names are the keys
                ContentValues valuez = new ContentValues();
                for(String s : this.products)
                {
                    System.out.println("Service centre id :"+this._id+"insertmapping : "+s);
                    valuez.put(ServiceCentre_ProductMapping.COLUMN_SERVICE_CENTRE_ID, this._id);
                    valuez.put(ServiceCentre_ProductMapping.COLUMN_PRODUCT, s);
                    db.insert(
                            ServiceCentre_ProductMapping.TABLE_NAME,
                            null,
                            valuez);
                }
                return 0;

            }

        };

        //AssetManager assManager = getApplicationContext().getAssets();
        AssetManager assManager = c.getAssets();
        InputStream is = null;
        try {
            is = assManager.open("data.xml");
            InputStream caInput = new BufferedInputStream(is);
            XMLParser X = new XMLParser();
            List<XMLParser.Entry> l = X.parse(caInput);
            centre_info centreInfo[] = new centre_info[l.size()];

            for(int i=0;i<l.size();i++)
            {
                centreInfo[i] = new centre_info(l.get(i).name, l.get(i).address, l.get(i).phone, l.get(i).city, l.get(i).email, l.get(i).company_id, l.get(i).products);
                long index = centreInfo[i].insert_into_db(db);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(ServiceCentres.SQL_DELETE_TABLE);
        db.execSQL(Companies.SQL_DELETE_TABLE);
        db.execSQL(ServiceCentre_ProductMapping.SQL_DELETE_TABLE);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public static Cursor getListCities(Integer message)
    {
        String[] projection = {
                ServiceCentres.COLUMN_ID,
                ServiceCentres.COLUMN_CITY
        };

        Cursor cursor = db.query(
                ServiceCentres.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                ServiceCentres.COLUMN_COMPANY + " = " + message.toString(),                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null,                                 // The sort order
                null
        );

        return cursor;
    }

    public static Cursor getListProducts(Cursor list_of_cities)
    {
        //Get list of centres' ids as array
        String[] list_of_service_centre_ids = getStringArrayFromCursor(list_of_cities, ServiceCentres.COLUMN_ID);

        String[] projection = {
                ServiceCentre_ProductMapping.COLUMN_ID,
                ServiceCentre_ProductMapping.COLUMN_PRODUCT
        };

        Cursor cursor = db.query(
                ServiceCentre_ProductMapping.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                ServiceCentre_ProductMapping.COLUMN_SERVICE_CENTRE_ID + " IN (" + makePlaceholders(list_of_service_centre_ids.length) + ")",                                // The columns for the WHERE clause
                list_of_service_centre_ids,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null,                                 // The sort order
                null
        );

        return cursor;
    }

    public static Cursor getListCenters(Integer company_id, String city, String product)
    {
        String join_query = "SELECT "+
                DatabaseHelper.ServiceCentres.TABLE_NAME + "." + DatabaseHelper.ServiceCentres.COLUMN_ID+
                ", "+DatabaseHelper.ServiceCentres.TABLE_NAME + "." + DatabaseHelper.ServiceCentres.COLUMN_NAME+
                ", "+DatabaseHelper.ServiceCentres.TABLE_NAME + "." +DatabaseHelper.ServiceCentres.COLUMN_ADDRESS+
                ", "+DatabaseHelper.ServiceCentres.TABLE_NAME + "." +DatabaseHelper.ServiceCentres.COLUMN_PHONE+
                ", "+DatabaseHelper.ServiceCentres.TABLE_NAME + "." +DatabaseHelper.ServiceCentres.COLUMN_CITY+
                ", "+DatabaseHelper.ServiceCentres.TABLE_NAME + "." +DatabaseHelper.ServiceCentres.COLUMN_EMAIL+
                " FROM "+DatabaseHelper.ServiceCentres.TABLE_NAME+
                //" INNER JOIN "+DatabaseHelper.ServiceCentre_ProductMapping.TABLE_NAME+
                //" ON "+DatabaseHelper.ServiceCentres.TABLE_NAME + "." +DatabaseHelper.ServiceCentres.COLUMN_ID+ "="+
                //DatabaseHelper.ServiceCentre_ProductMapping.TABLE_NAME + "." +DatabaseHelper.ServiceCentre_ProductMapping.COLUMN_SERVICE_CENTRE_ID +
                " WHERE "+DatabaseHelper.ServiceCentres.TABLE_NAME + "." + DatabaseHelper.ServiceCentres.COLUMN_CITY+ " = ? " +
               // " AND "+ DatabaseHelper.ServiceCentre_ProductMapping.TABLE_NAME + "." + DatabaseHelper.ServiceCentre_ProductMapping.COLUMN_PRODUCT+ " = ?" +
                " AND "+ DatabaseHelper.ServiceCentres.TABLE_NAME + "." + DatabaseHelper.ServiceCentres.COLUMN_COMPANY+ " = ?";

        Cursor cursor = db.rawQuery(join_query, set_selection_args(company_id, city, product));

        return cursor;
    }

    private static String[] set_selection_args(Integer company_id, String city, String product)
    {
        /*if(city.equals("All") && !product.equals("All"))
        {
            String[] selection_args = {
                    product,
                    company_id.toString()
            };

            return selection_args;
        }

        else if(!city.equals("All") && product.equals("All"))
        {*/
            String[] selection_args = {
                    city,
                    company_id.toString()
            };

            return selection_args;
       /* }

        else if(!city.equals("All") && !product.equals("All"))
        {
            String[] selection_args = {
                    city,
                    product,
                    company_id.toString()
            };

            return selection_args;
        }

        return null;*/
    }

    public static String[] getStringArrayFromCursor(Cursor cursor, String column_name)
    {
        // Get the string array
        String[] strings = new String[cursor.getCount()];
        Integer i = 0;
        cursor.moveToFirst();
        do {
            strings[i] = cursor.getString(cursor.getColumnIndex(column_name));
            i++;
        }while (cursor.moveToNext());

        //Get Unique values only
        //First get rid of duplicates
        String[] unique = new HashSet<String>(Arrays.asList(strings)).toArray(new String[strings.length]);
        //Then delete null values by copying to list all non null values and creating new array with non null ones
        List<String> list = new ArrayList<String>();

        for(String s : unique) {
            if(s != null && s.length() > 0) {
                list.add(s);
            }
        }

        unique = list.toArray(new String[list.size()]);

        return unique;
    }

    private static String makePlaceholders(int len){
        String placeholder = new String();
        for(int i=0;i<len;i++)
        {
            placeholder = placeholder+"?";
            if(i<len-1)
            {placeholder = placeholder + ",";}

        }
        return placeholder;
    }


    /* Inner class that defines the table contents */
    public static abstract class Companies implements BaseColumns {
        public static final String TABLE_NAME = "Companies";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_NAME = "Name";

        private static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME + " TEXT" +
                        ")";

        private static final String SQL_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    /* Inner class that defines the table contents */
    public static abstract class ServiceCentres implements BaseColumns {
        public static final String TABLE_NAME = "ServiceCentres";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_NAME = "Name";
        public static final String COLUMN_ADDRESS = "Address";
        public static final String COLUMN_PHONE = "Phone";
        public static final String COLUMN_CITY = "City";
        public static final String COLUMN_EMAIL = "Email";
        public static final String COLUMN_COMPANY = "CompanyID";

        public static final String string_type = " TEXT";

        private static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME + string_type + ","+
                        COLUMN_ADDRESS + string_type + ","+
                        COLUMN_PHONE + " INTEGER,"+
                        COLUMN_CITY + string_type + ","+
                        COLUMN_EMAIL + string_type + ","+
                        COLUMN_COMPANY + " INTEGER"+ "," +
                        "FOREIGN KEY " + "(" + COLUMN_COMPANY + ")" + "REFERENCES Companies(_ID)" +
                        ")";

        private static final String SQL_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }


    /* Inner class that defines the table contents */
    public static abstract class ServiceCentre_ProductMapping implements BaseColumns {
        public static final String TABLE_NAME = "ServiceCentre_ProductMapping";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_SERVICE_CENTRE_ID = "ServiceCentre";
        public static final String COLUMN_PRODUCT = "Product";

        private static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY," +
                        COLUMN_SERVICE_CENTRE_ID + " INTEGER" + "," +
                        COLUMN_PRODUCT + " TEXT" +
                        ")";

        private static final String SQL_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}