package com.asadeltacom.wherecovid;

import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class StatisticsActivity extends AppCompatActivity {

    FrameLayout simpleFrameLayout;
    TabLayout tabLayout;
    Button btnAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

// get the reference of FrameLayout and TabLayout
        simpleFrameLayout = (FrameLayout) findViewById(R.id.simpleFrameLayout);
        tabLayout = (TabLayout) findViewById(R.id.simpleTabLayout);

// Create a new Tab named "World"
        TabLayout.Tab firstTab = tabLayout.newTab();
        firstTab.setText(R.string.btGlobal); // set the Text for the first Tab
        firstTab.setIcon(R.drawable.world_covid); // set an icon for the
        tabLayout.addTab(firstTab); // add  the tab at in the TabLayout
// Create a new Tab named "Country"
        TabLayout.Tab secondTab = tabLayout.newTab();
        secondTab.setText(R.string.btCountry); // set the Text for the second Tab
        secondTab.setIcon(R.drawable.flag_country); // set an icon for the second tab
        tabLayout.addTab(secondTab); // add  the tab  in the TabLayout
// Create a new Tab named "Brazil"
        TabLayout.Tab thirdTab = tabLayout.newTab();
        thirdTab.setText(R.string.btBrazil); // set the Text for the third Tab
        thirdTab.setIcon(R.mipmap.brazil_map_color); // set an icon for the third tab
        tabLayout.addTab(thirdTab); // add  the tab at in the TabLayout
// Create a new Tab named "About"
        TabLayout.Tab fourthTab = tabLayout.newTab();
        fourthTab.setText(R.string.btAbout); // set the Text for the fourth Tab
        fourthTab.setIcon(R.mipmap.pergunta); // set an icon for the fourth tab
        tabLayout.addTab(fourthTab); // add  the tab at in the TabLayout


// perform setOnTabSelectedListener event on TabLayout
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // get the current selected tab's position and replace the fragment accordingly
                Fragment fragment;
                switch (tab.getPosition()) {
                    case 0:
                        fragment = new WorldFragment();
                        break;
                    case 1:
                        fragment = new CountryFragment();
                        break;
                    case 2:
                        fragment = new BrazilFragment();
                        break;
                    case 3:
                        fragment = new AboutFragment();
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + tab.getPosition());
                }
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.simpleFrameLayout, fragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}