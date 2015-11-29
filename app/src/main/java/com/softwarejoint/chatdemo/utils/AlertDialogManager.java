package com.softwarejoint.chatdemo.utils;

import android.app.AlertDialog.Builder;
import android.content.Context;


public class AlertDialogManager {
	
	public static void showAlertNoInternet(Context context, boolean allowCancel){
		Builder builder = new Builder(context);
		builder.setTitle("Internet Connection Error")
				.setMessage("Please connect to working Internet connection")
				.setCancelable(allowCancel);
		builder.create().show();		
	}
}
