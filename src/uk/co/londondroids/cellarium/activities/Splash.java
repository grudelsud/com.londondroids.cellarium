package uk.co.londondroids.cellarium.activities;

import uk.co.londondroids.cellarium.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Splash extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.splash );
    }
    
    public void onClickForward( View view ) {
    	Intent i = new Intent( this, Main.class );
    	startActivity( i );
    }
}