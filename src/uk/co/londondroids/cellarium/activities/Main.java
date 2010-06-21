package uk.co.londondroids.cellarium.activities;

import uk.co.londondroids.cellarium.R;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class Main extends TabActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView( R.layout.main );

	    Resources       res     = getResources(); // Resource object to get Drawables
	    TabHost         tabHost = getTabHost();   // The activity TabHost
	    TabHost.TabSpec spec;                     // Resusable TabSpec for each tab
	    Intent          intent;                   // Reusable Intent for each tab
	    
	    intent = new Intent( this, WineList.class );
	    spec   = tabHost.newTabSpec( res.getString( R.string.tabTitleWines ) );
	    
	    spec.setIndicator( res.getString( R.string.tabTitleWines ) );
	    spec.setContent( intent );
	    tabHost.addTab( spec );

	    intent = new Intent( this, WineNoteList.class );
	    spec   = tabHost.newTabSpec( res.getString( R.string.tabTitleCards ) );
	    
	    spec.setIndicator( res.getString( R.string.tabTitleCards ) );
	    spec.setContent( intent );
	    tabHost.addTab( spec );
	}

}
