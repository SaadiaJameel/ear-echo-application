package com.example.login.UI;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.example.login.R;
import com.google.android.material.snackbar.Snackbar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConfigureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConfigureFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // New variables

    VideoView video;
    Uri uri;
    RelativeLayout relativelayout;
    int count;

    public ConfigureFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConfigureFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConfigureFragment newInstance(String param1, String param2) {
        ConfigureFragment fragment = new ConfigureFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView= inflater.inflate(R.layout.fragment_configure, container, false);

        video= (VideoView) myView.findViewById(R.id.video);

        uri= Uri.parse("android.resource://" + getActivity().getPackageName()+ "/"+ R.raw.dance);
        video.setVideoURI(uri);

        relativelayout= (RelativeLayout) myView.findViewById(R.id.relativelayout);

        Button playButton= (Button) myView.findViewById(R.id.play);
        Button stopButton= (Button) myView.findViewById(R.id.stop);
        count= 0;
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                video.start();
                playButton.setClickable(false);
                count= count+1;
                video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        snackBar(count);

                    }

                });
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                video.stopPlayback();
                playButton.setClickable(true);
                uri= Uri.parse("android.resource://" + getActivity().getPackageName()+ "/"+ R.raw.dance);
                video.setVideoURI(uri);
            }
        });
//        playVideo(playButton);
        return myView;
    }

    private void snackBar(int count){
        if(count + 1 >2){
            Snackbar snackbar = Snackbar.make(relativelayout, "Device configuration completed.", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }else {
            Snackbar snackbar = Snackbar.make(relativelayout, "Please record next sample (" + (count+1) + "/20). Follow the video again.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Record", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Snackbar snackbarView = Snackbar.make(relativelayout, "Recording started", Snackbar.LENGTH_SHORT);
                            snackbarView.show();
                            playVideo(video, count);
                        }
                    })
                    .setActionTextColor(getResources().getColor(android.R.color.holo_blue_dark));

            snackbar.show();
        }

    }

    private void playVideo(VideoView video, int count){
        count= count+1;
        int val= count;
        video.start();
        video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                snackBar(val);
            }
        });

    }

}