package com.example.login.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.login.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationView extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_view);
        replaceFragment(new HomeFragment());

        BottomNavigationView nav= (BottomNavigationView) findViewById(R.id.bottomNavigationView);

        nav.setOnItemSelectedListener(item -> {

            switch(item.getItemId()){

                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;

                case R.id.profile:
                    replaceFragment(new ProfileFragment());
                    break;

                case R.id.settings:
                    replaceFragment(new SettingsFragment());
                    break;
            }


            return true;
        });
    }


    private void replaceFragment(Fragment fragment){

        FragmentManager fragmentManager= getSupportFragmentManager();
        FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}