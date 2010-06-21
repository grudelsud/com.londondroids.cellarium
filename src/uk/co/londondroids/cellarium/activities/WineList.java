package uk.co.londondroids.cellarium.activities;

import uk.co.londondroids.cellarium.R;
import uk.co.londondroids.cellarium.providers.CellariumProvider.Wine;
import android.app.ListActivity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class WineList extends ListActivity {
	private static final String TAG = "WineList";

	// Menu item ids
	public static final int MENU_ITEM_DELETE = Menu.FIRST;
	public static final int MENU_ITEM_INSERT = Menu.FIRST + 1;

	private static final String[] PROJECTION = new String[] {
		Wine._ID,
		Wine.AZIENDA,
		Wine.CANTINA,
		Wine.DENOMINAZIONE,
		Wine.NOME,
		Wine.ANNATA
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		if (intent.getData() == null) {
			intent.setData( Wine.CONTENT_URI );
		}

		// Perform a managed query. The Activity will handle closing and requerying the cursor when needed.
		Cursor cursor = managedQuery(getIntent().getData(), PROJECTION, null, null, Wine.DEFAULT_SORT_ORDER );
		startManagingCursor(cursor);

		// Used to map notes entries from the database to views
		String[] from = new String[] { Wine.DENOMINAZIONE, Wine.AZIENDA, Wine.NOME, Wine.ANNATA };
		int[] to = new int[] { R.id.list_denominazione, R.id.list_azienda, R.id.list_nome, R.id.list_annata };
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.winelist_item, cursor, from, to);
		setListAdapter(adapter);
		registerForContextMenu(getListView());
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
//	@Override
//	public boolean onPrepareOptionsMenu(Menu menu) {
//		return super.onPrepareOptionsMenu(menu);
//	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);

		// This is our one standard application action -- inserting a new wine into the list.
		menu.add(0, MENU_ITEM_INSERT, 0, R.string.menu_insert);
		return result;
	}

	/**
	 * An action has been selected from the application menu. Menu items are presented to the user
	 * depending on what is instantiated by onPrepare- and onCreate- OptionsMenu
	 * 
	 * (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch( item.getItemId() ) {
		case MENU_ITEM_INSERT:
			startActivity(new Intent(Intent.ACTION_INSERT, getIntent().getData()));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/* (non-Javadoc)
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);

		String action = getIntent().getAction();
		if (Intent.ACTION_PICK.equals(action) || Intent.ACTION_GET_CONTENT.equals(action)) {
			// The caller is waiting for us to return a note selected by
			// the user.  The have clicked on one, so return it now.
			setResult(RESULT_OK, new Intent().setData(uri));
		} else {
			// Launch activity to view/edit the currently selected item
			startActivity(new Intent(Intent.ACTION_EDIT, uri));
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo info;
		try {
			info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		} catch (ClassCastException e) {
			Log.e(TAG, "bad menuInfo", e);
			return;
		}

		Cursor cursor = (Cursor) getListAdapter().getItem(info.position);
		if (cursor == null) {
			// For some reason the requested item isn't available, do nothing
			return;
		}
		// Add a menu item to delete the note
		menu.add(0, MENU_ITEM_DELETE, 0, R.string.menu_delete);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info;
		try {
			info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		} catch (ClassCastException e) {
			Log.e(TAG, "bad menuInfo", e);
			return false;
		}

		switch (item.getItemId()) {
		case MENU_ITEM_DELETE:
			// Delete the note that the context menu is for
			Uri noteUri = ContentUris.withAppendedId(getIntent().getData(), info.id);
			getContentResolver().delete(noteUri, null, null);
			return true;
		}
		return false;
	}
}
