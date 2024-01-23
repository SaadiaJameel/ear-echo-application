package com.example.login.UI;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.login.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.*;
import java.util.Objects;

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
    static int count = 0;

    FirebaseAuth mAuth;
    FirebaseFirestore db= FirebaseFirestore.getInstance();

    // Reference to the collection
    CollectionReference collection= db.collection("ear_data");

    // Create a document with specific data

    Map<String, Object> city = new HashMap<>();



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

        if(!Python.isStarted())
            Python.start(new AndroidPlatform(getActivity()));

        Python py= Python.getInstance();
        final PyObject pyobj = py.getModule("add");

        video= (VideoView) myView.findViewById(R.id.video);

        uri= Uri.parse("android.resource://" + getActivity().getPackageName()+ "/"+ R.raw.dance);
        video.setVideoURI(uri);

        relativelayout= (RelativeLayout) myView.findViewById(R.id.relativelayout);

        ImageButton playButton= (ImageButton) myView.findViewById(R.id.play);
        ImageButton stopButton= (ImageButton) myView.findViewById(R.id.stop);

        addData(city);
//        city.put("1", "Los Angeles");
//        city.put("state", "CA");
//        city.put("country", "USA");
//        count= 0;
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                video.start();
                playButton.setClickable(false);
//                count= count+1;
                video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        PyObject obj= pyobj.callAttr("main");
//
                        Toast.makeText(getActivity(), obj.toString(), Toast.LENGTH_SHORT).show();
                        snackBar(count, playButton, stopButton);


                    }

                });
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                video.stopPlayback();
//                count= count-1;
                playButton.setClickable(true);
                uri= Uri.parse("android.resource://" + getActivity().getPackageName()+ "/"+ R.raw.dance);
                video.setVideoURI(uri);

            }

        });
//        playVideo(playButton);
        return myView;
    }

    private void snackBar(int count, ImageButton startbtn, ImageButton stopbtn){
        count= count+1;
        int val= count;
        if(count + 1 >1){
            Snackbar snackbar = Snackbar.make(relativelayout, "Device configuration completed.", Snackbar.LENGTH_SHORT);
            snackbar.show();
            startbtn.setClickable(false);
            stopbtn.setClickable(false);


            // First do the pre processing here.
            // Then create the HashMap to send to the database.


            collection.document(mAuth.getInstance().getCurrentUser().getEmail()).set(city)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getActivity(), "Data stored successfully.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();

                        }
                    });
        }else {
            Snackbar snackbar = Snackbar.make(relativelayout, "Please record next sample (" + (count+1) + "/20). Follow the video again.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Record", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Snackbar snackbarView = Snackbar.make(relativelayout, "Recording started", Snackbar.LENGTH_SHORT);
                            snackbarView.show();
                            playVideo(video, val, startbtn, stopbtn);
                        }
                    })
                    .setActionTextColor(getResources().getColor(android.R.color.holo_blue_dark));

            snackbar.show();
        }

    }

    private void playVideo(VideoView video, int count, ImageButton startbtn, ImageButton stopbtn){
        video.start();
        video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                snackBar(count, startbtn, stopbtn);
            }
        });

    }

    public void addData(Map m){
        int i= 0;
        while (i<1440){
            m.put(String.valueOf(i), String.valueOf(i));
            i= i+1;
        }
    }

}