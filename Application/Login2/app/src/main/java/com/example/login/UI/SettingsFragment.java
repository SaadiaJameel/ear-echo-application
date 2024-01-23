package com.example.login.UI;

import static android.content.Intent.getIntent;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.login.R;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView email;
    FirebaseAuth mAuth;

    LinearLayout linlayout;

    public SettingsFragment() {
        // Required empty public constructor
    }



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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



//        showUserEmail();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView=  inflater.inflate(R.layout.fragment_settings, container, false);

        email= (TextView) myView.findViewById(R.id.mail);
        linlayout= (LinearLayout) myView.findViewById(R.id.linlayout);

        FirebaseUser currentUser = mAuth.getInstance().getCurrentUser();
        if (currentUser != null){
            email.setText(currentUser.getEmail());
        }

        linlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBluetooth();
            }
        });


//        email.setText("Hi");

        return myView;
    }

    public void showUserEmail(){
        FirebaseUser currentUser= mAuth.getCurrentUser();
        String emailUser= currentUser.getEmail();
        email.setText(emailUser);
    }

    public void openBluetooth(){
        Intent intent = new Intent(getActivity(), Bluetooth.class);
        startActivity(intent);
    }
}