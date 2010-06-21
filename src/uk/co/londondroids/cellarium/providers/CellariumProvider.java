package uk.co.londondroids.cellarium.providers;

import java.text.SimpleDateFormat;
import java.util.Date;

import uk.co.londondroids.cellarium.R;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

/**
 * Content provider for all tables in cellarium.sqlite database
 * 
 * @author alisi
 *
 */
public class CellariumProvider extends ContentProvider {

	public static final String AUTHORITY = "uk.co.londondroids.cellarium.providers.cellariumprovider";

	private static final String TAG = "CellariumProvider";

	private static final String DATABASE_NAME = "cellarium.sqlite";
	private static final int DATABASE_VERSION = 1;

	private static final String WINE_TABLE_NAME = "wine";
	private static final String WINENOTE_TABLE_NAME = "winenote";

	private static final int WINE = 1;
	private static final int WINE_ID = 2;
	private static final int WINENOTE = 3;
	private static final int WINENOTE_ID = 4;

	private static final UriMatcher sUriMatcher;

	private DatabaseHelper mOpenHelper;

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, WINE_TABLE_NAME, WINE);
		sUriMatcher.addURI(AUTHORITY, WINE_TABLE_NAME+"/#", WINE_ID);
		sUriMatcher.addURI(AUTHORITY, WINENOTE_TABLE_NAME, WINENOTE);
		sUriMatcher.addURI(AUTHORITY, WINENOTE_TABLE_NAME+"/#", WINENOTE_ID);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		String where = "";
		switch (sUriMatcher.match(uri)) {
		case WINE:
			count = db.delete(WINE_TABLE_NAME, selection, selectionArgs);
			break;

		case WINE_ID:
			String wineId = uri.getPathSegments().get(1);
			where = Wine._ID + "=" + wineId + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
			count = db.delete(WINE_TABLE_NAME, where, selectionArgs);
			break;

		case WINENOTE:
			count = db.delete(WINENOTE_TABLE_NAME, selection, selectionArgs);
			break;

		case WINENOTE_ID:
			String wineNoteId = uri.getPathSegments().get(1);
			where = WineNote._ID + "=" + wineNoteId + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
			count = db.delete(WINENOTE_TABLE_NAME, where, selectionArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case WINE:
			return Wine.CONTENT_TYPE;
		case WINE_ID:
			return Wine.CONTENT_ITEM_TYPE;
		case WINENOTE:
			return WineNote.CONTENT_TYPE;
		case WINENOTE_ID:
			return WineNote.CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		String emptyString = getContext().getResources().getString( R.string.emptyField );
		Long now = Long.valueOf(System.currentTimeMillis());
		Long rowId;

		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}

		switch( sUriMatcher.match(uri) ) {
		case WINE:
			if( values.containsKey(Wine.ID_REMOTE) == false ) {
				values.put(Wine.ID_REMOTE, 0);
			}
			if( values.containsKey(Wine.CATEGORIA) == false ) {
				values.put(Wine.CATEGORIA, emptyString);
			}
			if( values.containsKey(Wine.DENOMINAZIONE) == false ) {
				values.put(Wine.DENOMINAZIONE, emptyString);
			}
			if( values.containsKey(Wine.COMUNE) == false ) {
				values.put(Wine.COMUNE, emptyString);
			}
			if( values.containsKey(Wine.AZIENDA) == false ) {
				values.put(Wine.AZIENDA, emptyString);
			}
			if( values.containsKey(Wine.NOME) == false ) {
				values.put(Wine.NOME, emptyString);
			}
			if( values.containsKey(Wine.CANTINA) == false ) {
				values.put(Wine.CANTINA, emptyString);
			}
			if( values.containsKey(Wine.ANNATA) == false ) {
				values.put(Wine.ANNATA, 0);
			}
			if( values.containsKey(Wine.GRADAZIONE) == false ) {
				values.put(Wine.GRADAZIONE, 0);
			}
			if( values.containsKey(Wine.CREATED) == false ) {
				values.put(Wine.CREATED, now);
			}
			if( values.containsKey(Wine.MODIFIED) == false ) {
				values.put(Wine.MODIFIED, now);
			}
			rowId = db.insert(WINE_TABLE_NAME, Wine.ID_REMOTE, values);
			if (rowId > 0) {
				Uri wineUri = ContentUris.withAppendedId(Wine.CONTENT_URI, rowId);
				getContext().getContentResolver().notifyChange(wineUri, null);
				return wineUri;
			}

			throw new SQLException("Failed to insert row into " + uri);

		case WINENOTE:
			if( values.containsKey(WineNote.CAMPIONE) == false ) {
				values.put(WineNote.CAMPIONE, emptyString);
			}
			if( values.containsKey(WineNote.TEMP_VINO) == false ) {
				values.put(WineNote.TEMP_VINO, 0);
			}
			if( values.containsKey(WineNote.TEMP_AMB) == false ) {
				values.put(WineNote.TEMP_AMB, 0);
			}
			if( values.containsKey(WineNote.DATETIME) == false ) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				values.put( WineNote.DATETIME, sdf.format(new Date(now)) );
			}
			if( values.containsKey(WineNote.LUOGO) == false ) {
				values.put(WineNote.LUOGO, emptyString);
			}
			if( values.containsKey(WineNote.TIPO) == false ) {
				values.put(WineNote.TIPO, getContext().getResources().getString(R.string.winenote_default));
			}
			if( values.containsKey(WineNote.GIUDIZI) == false ) {
				values.put(WineNote.GIUDIZI, getContext().getResources().getString(R.string.winenote_defaultscores));
			}
			if( values.containsKey(WineNote.PUNTEGGIO) == false ) {
				values.put(WineNote.PUNTEGGIO, 0);
			}
			if( values.containsKey(WineNote.OSSERVAZIONI) == false ) {
				values.put(WineNote.OSSERVAZIONI, emptyString);
			}
			if( values.containsKey(WineNote.CREATED) == false ) {
				values.put(WineNote.CREATED, now);
			}
			if( values.containsKey(WineNote.MODIFIED) == false ) {
				values.put(WineNote.MODIFIED, now);
			}
			rowId = db.insert(WINENOTE_TABLE_NAME, WineNote.CAMPIONE, values);
			if (rowId > 0) {
				Uri wineNoteUri = ContentUris.withAppendedId(WineNote.CONTENT_URI, rowId);
				getContext().getContentResolver().notifyChange(wineNoteUri, null);
				return wineNoteUri;
			}

			throw new SQLException("Failed to insert row into " + uri);

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

		String orderBy = "";
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		switch (sUriMatcher.match(uri)) {

		case WINE:
			qb.setTables(WINE_TABLE_NAME);
			// If no sort order is specified use the default
			if (TextUtils.isEmpty(sortOrder)) {
				orderBy = Wine.DEFAULT_SORT_ORDER;
			} else {
				orderBy = sortOrder;
			}
			break;

		case WINE_ID:
			qb.setTables(WINE_TABLE_NAME);
			// If no sort order is specified use the default
			if (TextUtils.isEmpty(sortOrder)) {
				orderBy = Wine.DEFAULT_SORT_ORDER;
			} else {
				orderBy = sortOrder;
			}
			qb.appendWhere(Wine._ID + "=" + uri.getPathSegments().get(1));
			break;

		case WINENOTE:
			qb.setTables(WINENOTE_TABLE_NAME);
			// If no sort order is specified use the default
			if (TextUtils.isEmpty(sortOrder)) {
				orderBy = WineNote.DEFAULT_SORT_ORDER;
			} else {
				orderBy = sortOrder;
			}
			break;

		case WINENOTE_ID:
			qb.setTables(WINENOTE_TABLE_NAME);
			// If no sort order is specified use the default
			if (TextUtils.isEmpty(sortOrder)) {
				orderBy = WineNote.DEFAULT_SORT_ORDER;
			} else {
				orderBy = sortOrder;
			}
			qb.appendWhere(WineNote._ID + "=" + uri.getPathSegments().get(1));
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		// Get the database and run the query
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

		// Tell the cursor what uri to watch, so it knows when its source data changes
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		String where = "";

		switch (sUriMatcher.match(uri)) {

		case WINE:
			count = db.update(WINE_TABLE_NAME, values, selection, selectionArgs);
			break;

		case WINE_ID:
			String wineId = uri.getPathSegments().get(1);
			where = Wine._ID + "=" + wineId + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
			count = db.update(WINE_TABLE_NAME, values, where, selectionArgs);
			break;

		case WINENOTE:
			count = db.update(WINENOTE_TABLE_NAME, values, selection, selectionArgs);
			break;

		case WINENOTE_ID:
			String wineNoteId = uri.getPathSegments().get(1);
			where = WineNote._ID + "=" + wineNoteId + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
			count = db.update(WINENOTE_TABLE_NAME, values, where, selectionArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	/**
	 * This class helps open, create, and upgrade the database file.
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String sql = "";

			sql = "CREATE TABLE " + WINE_TABLE_NAME + " (" +
			Wine._ID + " INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL, " +
			Wine.ID_REMOTE + " INTEGER, " +
			Wine.CATEGORIA + " VARCHAR, " +
			Wine.DENOMINAZIONE + " VARCHAR, " +
			Wine.COMUNE + " VARCHAR, " +
			Wine.AZIENDA + " VARCHAR, " +
			Wine.CANTINA + " VARCHAR, " +
			Wine.NOME + " VARCHAR, " +
			Wine.ANNATA + " INTEGER, " +
			Wine.GRADAZIONE + " FLOAT, " +
			Wine.CREATED + " INTEGER, " +
			Wine.MODIFIED + " INTEGER)";
			db.execSQL( sql );

			sql = "CREATE TABLE " + WINENOTE_TABLE_NAME + " (" +
			WineNote._ID + " INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL, " + 
			WineNote.ID_WINE + " INTEGER NOT NULL, " +
			WineNote.CAMPIONE + " VARCHAR, " +
			WineNote.TEMP_VINO + " FLOAT, " +
			WineNote.TEMP_AMB + " FLOAT, " +
			WineNote.DATETIME + " DATETIME, " +
			WineNote.LUOGO + " VARCHAR, " +
			WineNote.TIPO + " VARCHAR, " +
			WineNote.GIUDIZI + " VARCHAR, " +
			WineNote.PUNTEGGIO + " INTEGER, " +
			WineNote.OSSERVAZIONI + " TEXT, " +
			WineNote.CREATED + " INTEGER, " +
			WineNote.MODIFIED + " INTEGER)";
			db.execSQL( sql );
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + WINE_TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + WINENOTE_TABLE_NAME);
			onCreate(db);
		}
	}

	/**
	 * Table: vino
	 * @author alisi
	 *
	 */
	public static final class Wine implements BaseColumns {

		/**
		 * prevent instantiation
		 */
		private Wine() {}

		/**
		 * The content:// style URL for this table
		 */
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + WINE_TABLE_NAME);

		/**
		 * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.londondroids.wine";

		/**
		 * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.londondroids.wine";

		/**
		 * The default sort order for this table
		 */
		public static final String DEFAULT_SORT_ORDER = "azienda ASC, nome ASC, modified DESC";

		/**
		 * Reference to a remote record, usually containing a full description of the wine
		 * Type: INTEGER
		 */
		public static final String ID_REMOTE = "id_remote";

		/**
		 * Can be: rosso, bianco, frizzante, rosato, meditazione.
		 * Type: VARCHAR
		 */
		public static final String CATEGORIA = "categoria"; 

		/**
		 * Contains full definition of denominazione, including the type (e.g. "Langhe Nebbiolo DOC").
		 * Type: VARCHAR
		 */
		public static final String DENOMINAZIONE = "denominazione";

		/**
		 * Type: VARCHAR
		 */
		public static final String COMUNE = "comune";

		/**
		 * Type: VARCHAR
		 */
		public static final String AZIENDA = "azienda";

		/**
		 * Where applicable. Some wines have definitions of both AZIENDA and CANTINA
		 * Type: VARCHAR
		 */
		public static final String CANTINA = "cantina";

		/**
		 * The name of the game!
		 * Type: VARCHAR
		 */
		public static final String NOME = "nome";

		/**
		 * Type: INTEGER
		 */
		public static final String ANNATA = "annata";

		/**
		 * Type: FLOAT
		 */
		public static final String GRADAZIONE = "gradazione";

		/**
		 * Type: INTEGER
		 */
		public static final String CREATED = "created";

		/**
		 * Type: INTEGER
		 */
		public static final String MODIFIED = "modified";
	}

	/**
	 * @author alisi
	 *
	 */
	public static final class WineNote implements BaseColumns {

		/**
		 * prevent instantiation
		 */
		private WineNote() {}

		/**
		 * The content:// style URL for this table
		 */
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + WINENOTE_TABLE_NAME);

		/**
		 * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.londondroids.winenote";

		/**
		 * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.londondroids.winenote";

		/**
		 * The default sort order for this table
		 */
		public static final String DEFAULT_SORT_ORDER = "";

		/**
		 * Reference to Wine._ID
		 * Type: INTEGER
		 */
		public static final String ID_WINE = "id_wine";

		/**
		 * Type: VARCHAR
		 */
		public static final String CAMPIONE = "campione";

		/**
		 * Wine temperature
		 * Type: FLOAT
		 */
		public static final String TEMP_VINO = "temp_vino";

		/**
		 * Ambient temperature
		 * Type: FLOAT
		 */
		public static final String TEMP_AMB = "temp_amb";

		/**
		 * Date and time of the tasting
		 * Type: DATETIME
		 */
		public static final String DATETIME = "date_time";

		/**
		 * Location of the tasting
		 * Type: VARCHAR
		 */
		public static final String LUOGO = "luogo";

		/**
		 * Can be AIS, ONAV or any other for which an array of scores is defined
		 * Type: VARCHAR
		 */
		public static final String TIPO = "tipo";

		/**
		 * Array of scores, comma separated
		 * Type: VARCHAR
		 */
		public static final String GIUDIZI = "giudizi";

		/**
		 * Even if can be calculated dynamically from the array of scores, 
		 * it is easier to keep a stored reference
		 * Type: INTEGER
		 */
		public static final String PUNTEGGIO = "punteggio";

		/**
		 * Additional notes
		 * Type: TEXT
		 */
		public static final String OSSERVAZIONI = "osservazioni";

		/**
		 * Type: INTEGER
		 */
		public static final String CREATED = "created";

		/**
		 * Type: INTEGER
		 */
		public static final String MODIFIED = "modified";
	}
}
