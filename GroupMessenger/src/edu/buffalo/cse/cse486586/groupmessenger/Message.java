package edu.buffalo.cse.cse486586.groupmessenger;

import android.content.Context;
import android.widget.Toast;

public class Message {
	
	public static void message(Context context, String message1)
	{
		
		Toast.makeText(context,message1,Toast.LENGTH_LONG).show();
	}

}
