package com.gpvision.adapter;

import java.util.ArrayList;

import com.gpvision.R;
import com.gpvision.datamodel.Notification;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NotificationAdapter extends BaseAdapter {

	private ArrayList<Notification> notifications;
	private LayoutInflater inflater;

	public NotificationAdapter(ArrayList<Notification> notifications) {
		super();
		this.notifications = notifications;
	}

	public ArrayList<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(ArrayList<Notification> notifications) {
		this.notifications = notifications;
	}

	@Override
	public int getCount() {
		if (notifications != null) {
			return notifications.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return notifications.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		Notification notification = notifications.get(position);
		if (view == null) {
			if (inflater == null) {
				inflater = LayoutInflater.from(parent.getContext());
			}
			view = inflater.inflate(R.layout.layout_notification_list_item,
					parent, false);
			holder = new ViewHolder();
			holder.title = (TextView) view
					.findViewById(R.id.notification_list_title);
			holder.message = (TextView) view
					.findViewById(R.id.notification_list_message);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		holder.title.setText(notification.getTitle());
		holder.message.setText(notification.getMessage());
		return view;
	}

	private class ViewHolder {
		TextView title;
		TextView message;
	}
}
