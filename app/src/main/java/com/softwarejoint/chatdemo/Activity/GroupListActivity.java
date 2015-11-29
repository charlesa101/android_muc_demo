package com.softwarejoint.chatdemo.Activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.softwarejoint.chatdemo.BaseActivity;
import com.softwarejoint.chatdemo.R;
import com.softwarejoint.chatdemo.common.ListGroupAdapter;


public class GroupListActivity extends BaseActivity
{
	private ListView mListView;
	private ListGroupAdapter mListGroupAdapter;
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_group);
		mListView = (ListView)findViewById(R.id.listview);
		Button createGroup = (Button)findViewById(R.id.createGroup);
		createGroup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view)
			{
				final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(GroupListActivity.this);
				final AlertDialog alertDialog = dialogBuilder.create();
				LayoutInflater inflater = GroupListActivity.this.getLayoutInflater();
				View dialogView = inflater.inflate(R.layout.custom_dialog_view, null);
				dialogBuilder.setView(dialogView);
				EditText editText = (EditText)dialogView.findViewById(R.id.createGroup);
				TextView actionOk = (TextView)dialogView.findViewById(R.id.ok_action);
				TextView actionCancel = (TextView)dialogView.findViewById(R.id.cancel_action);
				actionOk.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view)
					{
						alertDialog.dismiss();
					}
				});

				actionCancel.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view)
					{
						alertDialog.dismiss();
					}
				});
				alertDialog.show();
			}
		});
	}
}
