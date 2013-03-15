package com.gpvision.fragment;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.gpvision.R;
import com.gpvision.adapter.NotificationAdapter;
import com.gpvision.datamodel.Notification;

public class NotificationFragment extends BaseFragment {

	public static final String TAG = Notification.class.getName();
	public static final String ARGS_NOTIFICATION_KEY = "notification";
	private ArrayList<Notification> notifications;
	private NotificationAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if (args == null)
			args = savedInstanceState;
		if (args != null && args.containsKey(ARGS_NOTIFICATION_KEY)) {
			notifications = (ArrayList<Notification>) args
					.getSerializable(ARGS_NOTIFICATION_KEY);
		}
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
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				adapter.setSelected(position);
				adapter.notifyDataSetChanged();
			}
		});

		Button remove = (Button) view
				.findViewById(R.id.notification_fragment_remove_btn);
		remove.setOnClickListener(this);
		Button clean = (Button) view
				.findViewById(R.id.notification_fragment_clean_btn);
		clean.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.notification_fragment_remove_btn:
			int selected = adapter.getSelected();
			if (selected > -1) {
				adapter.getNotifications().remove(selected);
				adapter.setSelected(-1);
				adapter.notifyDataSetChanged();
			}
			break;
		case R.id.notification_fragment_clean_btn:
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(
					R.string.notification_fragment_alert_dialog_title_Clean)
					.setMessage(
							R.string.notification_fragment_alert_dialog_message_clean)
					.setPositiveButton(R.string.base_ok,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									adapter.setNotifications(null);
									adapter.notifyDataSetChanged();
								}
							}).setNegativeButton(R.string.base_cancel, null)
					.create().show();

			break;
		default:
			break;
		}
	}

}
