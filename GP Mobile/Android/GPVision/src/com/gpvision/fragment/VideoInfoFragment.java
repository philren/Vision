package com.gpvision.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.gpvision.R;

public class VideoInfoFragment extends BaseFragment {
    private ListView videoInfoList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_info, container, false);
        videoInfoList = (ListView) view.findViewById(R.id.video_info_list);
        return view;
    }

}
