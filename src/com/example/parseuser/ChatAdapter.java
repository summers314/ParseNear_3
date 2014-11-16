package com.example.parseuser;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ChatAdapter extends ArrayAdapter<LeMessages>
{
	private final Context context;
	private final ArrayList<LeMessages> messageList;

	public ChatAdapter(Context context, ArrayList<LeMessages> messageList)
	{
		 super(context, R.layout.chat_item, messageList);
		 
		 this.context=context;
		 this.messageList=messageList;
	}
	
	@Override
	public boolean isEnabled(int position) 
	{
		return true;
	}
	
	@Override
	public boolean hasStableIds() 
	{
	     return true;
	}
	
	public View getView(int position,View convertView,ViewGroup parent)
	{
		//1. Create Inflater
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		//2. Get rowView from inflater
		View rowView = inflater.inflate(R.layout.chat_item,parent, false);
		
		TextView tv_username = (TextView) rowView.findViewById(R.id.tv_username);
		TextView tv_message  = (TextView) rowView.findViewById(R.id.tv_message);
		TextView tv_status   = (TextView) rowView.findViewById(R.id.tv_status);
		
		String uName;
		String message;
		String status;
		
		uName = messageList.get(position).getUsername();
		message = messageList.get(position).getMessage();
		status = messageList.get(position).getStatus();
		
		tv_username.setText(uName);
		tv_message.setText(message);
		tv_status.setText(status);
		
		//5. Return rowView
		return rowView;
	}
}