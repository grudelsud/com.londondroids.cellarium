package uk.co.londondroids.cellarium.activities;

import java.util.Calendar;

import uk.co.londondroids.cellarium.R;
import uk.co.londondroids.cellarium.providers.CellariumProvider.Wine;
import uk.co.londondroids.cellarium.providers.CellariumProvider.WineNote;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.AdapterView.OnItemSelectedListener;

public class WineNoteEdit extends Activity {

	public class SpinnerTipoSelListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			mTipo = parent.getItemAtPosition( pos ).toString();
			updateScoreFields();
		}

		public void onNothingSelected(AdapterView<?> parent) {
			// do nothing
		}
	}

	private static final String TAG = "WineNote";

	/**
	 * Standard projection for the interesting columns of a normal note.
	 */
	private static final String[] PROJECTION = new String[] {
		WineNote._ID,
		WineNote.ID_WINE,
		WineNote.CAMPIONE,
		WineNote.TEMP_VINO,
		WineNote.TEMP_AMB,
		WineNote.DATETIME,
		WineNote.LUOGO,
		WineNote.TIPO,
		WineNote.GIUDIZI,
		WineNote.PUNTEGGIO,
		WineNote.OSSERVAZIONI
	};

	private static final int WineNote_ID = 0;
	private static final int WineNote_ID_WINE = 1;

	private static final int WineNote_CAMPIONE = 2;
	private static final int WineNote_TEMP_VINO = 3;
	private static final int WineNote_TEMP_AMB = 4;
	private static final int WineNote_DATETIME = 5;
	private static final int WineNote_LUOGO = 6;
	private static final int WineNote_TIPO = 7;
	private static final int WineNote_GIUDIZI = 8;
	private static final int WineNote_PUNTEGGIO = 9;
	private static final int WineNote_OSSERVAZIONI = 10;

	private static final int MENU_ITEM_DELETE = Menu.FIRST;
	private static final int MENU_ITEM_CANCEL = Menu.FIRST + 1;
	private static final int MENU_ITEM_SAVE = Menu.FIRST + 2;

	protected static final int DATE_DIALOG_ID = 0;
	protected static final int TIME_DIALOG_ID = 1;

	private Uri mUri;
	private Cursor mCursor;
	private String mTipo;
	private int mYear;
	private int mMonth;
	private int mDay;
	private int mHour;
	private int mMinute;

	private TextView mTextVino;
	private EditText mTextCampione;
	private EditText mTextTempVino;
	private EditText mTextTempAmb;
	private TextView mTextDate;
	private TextView mTextTime;
	private EditText mTextLuogo;
	private Spinner mSpinnerTipo;
	private EditText mTextOsservazioni;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView( R.layout.winenote_editor );
		final Intent intent = getIntent();
		final String action = intent.getAction();

		String nome = intent.getStringExtra( Wine.NOME );
		String azienda = intent.getStringExtra( Wine.AZIENDA );
		String denominazione = intent.getStringExtra( Wine.DENOMINAZIONE );
		String annata = intent.getStringExtra( Wine.ANNATA );

		String vino = azienda + ", " + nome + " - " + denominazione + " " + annata;

		mTextVino = (TextView)findViewById( R.id.winenotetextview_vino );
		mTextCampione = (EditText)findViewById( R.id.winenote_campione_edit );
		mTextTempVino = (EditText)findViewById( R.id.winenote_tempvino_edit );
		mTextTempAmb = (EditText)findViewById( R.id.winenote_tempamb_edit );
		mTextDate = (TextView)findViewById( R.id.winenotetextview_date );
		mTextTime = (TextView)findViewById( R.id.winenotetextview_time );
		mTextLuogo = (EditText)findViewById( R.id.winenote_luogo_edit );
		mSpinnerTipo = (Spinner)findViewById( R.id.winenote_tipo_spinner );
		mTextOsservazioni = (EditText)findViewById( R.id.winenote_notes_edit );

		mTextVino.setText( vino );
		mTextDate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});
		mTextTime.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(TIME_DIALOG_ID);
			}
		});

		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH) + 1;
		mDay = c.get(Calendar.DAY_OF_MONTH);
		mHour = c.get(Calendar.HOUR_OF_DAY);
		mMinute = c.get(Calendar.MINUTE);

		updateDateTime();

		mSpinnerTipo.setOnItemSelectedListener( new SpinnerTipoSelListener() );
		mTipo = mSpinnerTipo.getSelectedItem().toString();
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
		return result;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch( item.getItemId() ) {
		case MENU_ITEM_DELETE:
			deleteWineNote();
			break;
		case MENU_ITEM_CANCEL:
			cancelWineNote();
			break;
		case MENU_ITEM_SAVE:
			saveWineNote();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void saveWineNote() {
		// TODO Auto-generated method stub

	}

	private void cancelWineNote() {
		if( mCursor != null ) {
			mCursor.close();
		}
		setResult( RESULT_CANCELED );
		finish();
	}

	private void deleteWineNote() {
		if( mCursor != null ) {
			mCursor.close();
		}
		getContentResolver().delete(mUri, null, null);
		finish();		
	}

	private void updateScoreFields() {
		// TODO change the list of score fields visualized according to selection made on the spinner

	}

	private void updateDateTime() {
		mTextDate.setText( mDay + "-" + mMonth  + "-" + mYear );
		mTextTime.setText( mHour + ":" + mMinute );
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener =
		new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear + 1;
			mDay = dayOfMonth;
			updateDateTime();
		}
	};

	private TimePickerDialog.OnTimeSetListener mTimeSetListener =
		new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			mHour = hourOfDay;
			mMinute = minute;
			updateDateTime();
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth - 1, mDay);
		case TIME_DIALOG_ID:
			return new TimePickerDialog(this, mTimeSetListener, mHour, mMinute, false);
		}
		return null;
	}
}
