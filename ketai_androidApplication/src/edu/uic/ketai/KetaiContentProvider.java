package edu.uic.ketai;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class KetaiContentProvider extends ContentProvider{
	
	public KetaiContentProvider(SQLiteDatabase d){
		this.db = d;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		switch(uriMatcher.match(uri))
		{		
		 default:
			throw new IllegalArgumentException("Unsupported URI:" + uri);
		}
	}

	@Override
	public String getType(Uri uri) {
		switch(uriMatcher.match(uri)){
			case ALLROWS:
				return "edu.uic.ketai/sensors";
			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		switch(uriMatcher.match(uri))
		{		
		 default:
			throw new IllegalArgumentException("Unsupported URI:" + uri);
		}
	}

	@Override
	public boolean onCreate() {
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		Cursor c;
		String sql;
		
		switch(uriMatcher.match(uri)){
		case ALLROWS:
		default:
			sql = "select * from sensor_raw";
		
		}
	    c = this.db.rawQuery(sql, new String[0]);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		switch(uriMatcher.match(uri))
		{		
		 default:
			throw new IllegalArgumentException("Unsupported URI:" + uri);
		}
	}

	private static final String myUri = "content://edu.uic.ketai/sensors";
	public static final Uri CONTENT_URI = Uri.parse(myUri);
	
	private static final int ALLROWS = 1;
	private static final int SINGLE_ROW = 2;	
	private static final UriMatcher uriMatcher;
	private SQLiteDatabase db;
	
	static{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI("edu.uic.ketai", "sensors", ALLROWS);
		uriMatcher.addURI("edu.uic.ketai", "sensors/#", SINGLE_ROW);

	}

}
