package com.gpvision.fragment;

import java.util.ArrayList;

import com.gpvision.R;
import com.gpvision.adapter.NotificationAdapter;
import com.gpvision.datamodel.Notification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class NotificationFragment extends BaseFragment {

	private ArrayList<Notification> notifications;
	private NotificationAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// test only
		notifications = new ArrayList<Notification>();
		Notification notification = new Notification();
		notification.setTitle("test1");
		notification.setMessage("message1");
		Notification notification2 = new Notification();
		notification2.setTitle("test2");
		notification2.setMessage("message2");
		notifications.add(notification);
		notifications.add(notification2);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_notification, container,
				false);
		ListView listView = (ListView) view
				.findViewById(R.id.notification_fragment_list);
		adapter = new NotificationAdapter(notifications);
		listView.setAdapter(adapter);
		return view;
	}

}
