package com.softwarejoint.chatdemo.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.softwarejoint.chatdemo.R;

import java.util.ArrayList;


public class ListGroupAdapter extends BaseAdapter
{
	private Context mContext;
	private static LayoutInflater inflater = null;
	private ArrayList<String> mList;

	ListGroupAdapter(Context context, ArrayList<String> li)
	{
		mContext = context;
		mList = li;
		inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount()
	{
		return mList.size();
	}

	@Override
	public Object getItem(int position)
	{
		return position;
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	private class ViewHolder
	{
		TextView sender, recentMessage, timeStamp, groupName;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder;
		if (convertView == null)
		{
			convertView = inflater.inflate(R.layout.cell_list_group, null);
			holder = new ViewHolder();
			holder.sender = (TextView) convertView.findViewById(R.id.sender);
			holder.recentMessage = (TextView) convertView.findViewById(R.id.recentmessage);
			holder.groupName = (TextView) convertView.findViewById(R.id.groupname);
			holder.timeStamp = (TextView) convertView.findViewById(R.id.timestamp);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		return convertView;
	}
}
