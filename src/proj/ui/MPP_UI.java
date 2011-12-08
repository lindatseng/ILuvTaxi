package proj.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MPP_UI extends Activity {
    /** Called when the activity is first created. */
	
	Button home_bt_route,home_bt_pricer;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        findView();
        setListener();
    }
    
    private void findView(){
    	home_bt_route=(Button)findViewById(R.id.home_bt_route);
    	home_bt_pricer=(Button)findViewById(R.id.home_bt_pricer);
    }
    
    private void setListener(){
    	
    	home_bt_route.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent intent = new Intent();
				intent.setClass(MPP_UI.this, Route.class);
				startActivity(intent);
				
			}
    		
    	});
    	
    	home_bt_pricer.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent intent = new Intent();
				intent.setClass(MPP_UI.this, Pricer.class);
				startActivity(intent);
				
			}
    		
    		
    	});
    }
}