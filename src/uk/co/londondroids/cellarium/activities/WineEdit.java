package uk.co.londondroids.cellarium.activities;

import java.util.ArrayList;
import java.util.Arrays;

import uk.co.londondroids.cellarium.R;
import uk.co.londondroids.cellarium.providers.CellariumProvider.Wine;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;

public class WineEdit extends Activity {
	private static final String TAG = "Wine";

	/**
	 * Standard projection for the interesting columns of a normal note.
	 */
	private static final String[] PROJECTION = new String[] {
		Wine._ID,
		Wine.ID_REMOTE,
		Wine.CATEGORIA,
		Wine.DENOMINAZIONE,
		Wine.COMUNE,
		Wine.AZIENDA,
		Wine.CANTINA,
		Wine.NOME,
		Wine.ANNATA,
		Wine.GRADAZIONE,
	};

	private static final int Wine_ID = 0;
	private static final int Wine_ID_REMOTE = 1;

	private static final int Wine_CATEGORIA = 2;
	private static final int Wine_DENOMINAZIONE = 3;
	private static final int Wine_COMUNE = 4;
	private static final int Wine_AZIENDA = 5;
	private static final int Wine_CANTINA = 6;
	private static final int Wine_NOME = 7;
	private static final int Wine_ANNATA = 8;
	private static final int Wine_GRADAZIONE = 9;
	
	private static final int MENU_ITEM_DELETE = Menu.FIRST;
	private static final int MENU_ITEM_CANCEL = Menu.FIRST + 1;
	private static final int MENU_ITEM_SAVE = Menu.FIRST + 2;
	private static final int MENU_ITEM_ADDNOTE = Menu.FIRST + 3;

	private Uri mUri;
	private Cursor mCursor;

	private EditText mTextNome;
	private EditText mTextAzienda;
	private EditText mTextCantina;
	private EditText mTextComune;
	private Spinner mSpinnerCategoria;
	private EditText mTextDenominazione;
	private EditText mTextAnnata;
	private EditText mTextGradazione;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView( R.layout.wine_editor );
		final Intent intent = getIntent();
		final String action = intent.getAction();
		if (Intent.ACTION_EDIT.equals(action)) {
			mUri = intent.getData();
		} else if (Intent.ACTION_INSERT.equals(action)) {
			mUri = getContentResolver().insert(intent.getData(), null);

			// If we were unable to create a new note, then just finish
			// this activity.  A RESULT_CANCELED will be sent back to the
			// original activity if they requested a result.
			if (mUri == null) {
				Log.e(TAG, "Failed to insert new note into " + getIntent().getData());
				finish();
				return;
			}

			// The new entry was created, so assume all will end well and
			// set the result to be returned.
			setResult(RESULT_OK, (new Intent()).setAction(mUri.toString()));

		} else {
			// Whoops, unknown action!  Bail.
			Log.e(TAG, "Unknown action, exiting");
			finish();
			return;
		}

		mTextNome = (EditText)findViewById(R.id.wine_name_edit);
		mTextAzienda = (EditText)findViewById(R.id.wine_azienda_edit);
		mTextCantina = (EditText)findViewById(R.id.wine_cantina_edit);
		mTextComune = (EditText)findViewById(R.id.wine_comune_edit);
		mSpinnerCategoria = (Spinner)findViewById(R.id.wine_categoria_spinner);
		mTextDenominazione = (EditText)findViewById(R.id.wine_denominazione_edit);
		mTextAnnata = (EditText)findViewById(R.id.wine_annata_edit);
		mTextGradazione = (EditText)findViewById(R.id.wine_gradazione_edit);

		// Get the note!
		mCursor = managedQuery(mUri, PROJECTION, null, null, null);
		startManagingCursor( mCursor );
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();

		// If we didn't have any trouble retrieving the data, it is now
		// time to get at the stuff.
		if (mCursor != null) {
			// Make sure we are at the one and only row in the cursor.
			mCursor.moveToFirst();
			String nome = mCursor.getString( Wine_NOME );
			String azienda = mCursor.getString( Wine_AZIENDA );
			String cantina = mCursor.getString( Wine_CANTINA );
			String comune = mCursor.getString( Wine_COMUNE );
			String categoria = mCursor.getString( Wine_CATEGORIA );
			String denominazione = mCursor.getString( Wine_DENOMINAZIONE );
			String annata = mCursor.getString( Wine_ANNATA );
			String gradazione = mCursor.getString( Wine_GRADAZIONE );

			String[] categorie = getResources().getStringArray( R.array.wineeditspinner_categoriaentries );
			ArrayList<String> arListCategorie = new ArrayList<String>( Arrays.asList( categorie ));
			int selCategoria = arListCategorie.indexOf( categoria );

			// This is a little tricky: we may be resumed after previously being
			// paused/stopped.  We want to put the new text in the text view,
			// but leave the user where they were (retain the cursor position
			// etc).  This version of setText does that for us.
			mTextNome.setTextKeepState( nome );
			mTextAzienda.setTextKeepState( azienda );
			mTextCantina.setTextKeepState( cantina );
			mTextComune.setTextKeepState( comune );
			mSpinnerCategoria.setSelection( selCategoria );
			mTextDenominazione.setTextKeepState( denominazione );
			mTextAnnata.setTextKeepState( annata );
			mTextGradazione.setTextKeepState( gradazione );
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);

		menu.add(0, MENU_ITEM_DELETE, 0, R.string.menu_delete);
		menu.add(0, MENU_ITEM_CANCEL, 0, R.string.menu_cancel);
		menu.add(0, MENU_ITEM_SAVE, 0, R.string.menu_save);
		menu.add(0, MENU_ITEM_ADDNOTE, 0, R.string.menu_addnote);
		return result;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch( item.getItemId() ) {
		case MENU_ITEM_DELETE:
			deleteWine();
			break;
		case MENU_ITEM_CANCEL:
			cancelWine();
			break;
		case MENU_ITEM_SAVE:
			saveWine();
			break;
		case MENU_ITEM_ADDNOTE:
			Intent i = new Intent( this, WineNoteEdit.class );
			i.setData( mUri );
			i.putExtra( Wine.NOME, mTextNome.getText().toString() );
			i.putExtra( Wine.AZIENDA, mTextAzienda.getText().toString() );
			i.putExtra( Wine.DENOMINAZIONE, mTextDenominazione.getText().toString() );
			i.putExtra( Wine.ANNATA, mTextAnnata.getText().toString() );
			startActivity( i );
		}
		return super.onOptionsItemSelected(item);
	}

	private void saveWine() {
		ContentValues cv = new ContentValues();
		
		String nome = mTextNome.getText().toString();
		String azienda = mTextAzienda.getText().toString();
		String cantina = mTextCantina.getText().toString();
		String comune = mTextComune.getText().toString();
		int categoria = mSpinnerCategoria.getSelectedItemPosition();
		String denominazione = mTextDenominazione.getText().toString();
		String annata = mTextAnnata.getText().toString();
		String gradazione = mTextGradazione.getText().toString();
		Long now = Long.valueOf(System.currentTimeMillis());

		cv.put(Wine.NOME, nome);
		cv.put(Wine.AZIENDA, azienda);
		cv.put(Wine.CANTINA, cantina);
		cv.put(Wine.COMUNE, comune);
		cv.put(Wine.CATEGORIA, getResources().getStringArray( R.array.wineeditspinner_categoriaentries)[categoria]);
		cv.put(Wine.DENOMINAZIONE, denominazione);
		cv.put(Wine.ANNATA, annata);
		cv.put(Wine.GRADAZIONE, gradazione);
		cv.put(Wine.MODIFIED, now);
		
		getContentResolver().update(mUri, cv, null, null);
		setResult( RESULT_OK );
	}

	private void cancelWine() {
		if( mCursor != null ) {
			mCursor.close();
		}
		setResult( RESULT_CANCELED );
		finish();
	}

	private void deleteWine() {
		if( mCursor != null ) {
			mCursor.close();
		}
		getContentResolver().delete(mUri, null, null);
		finish();		
	}
}
