package com.jose.energytracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.zip.Inflater;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.//
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        FragmentOne f1 = ((FragmentOne) getSupportFragmentManager().getFragments().get(0));
        FragmentTwo f2 = ((FragmentTwo) getSupportFragmentManager().getFragments().get(1));

        switch(view.getId()) {
            case R.id.fab:
                if(mViewPager.getCurrentItem() == 0) {
                    f1.fabClicked(f2);
                } else {
                    f2.fabClicked();
                }
                break;

            default:
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        FragmentOne f1;
        AlertDialog.Builder builder;

        switch (itemId) {
            case R.id.action_objetive:
                //editar objetivo
                f1 = ((FragmentOne) getSupportFragmentManager().getFragments().get(0));
                builder = new AlertDialog.Builder(this);
                LayoutInflater inflater = this.getLayoutInflater();
                View viewDialog = inflater.inflate(R.layout.new_goal_dialog, null);
                EditText editTextGoal = (EditText) viewDialog.findViewById(R.id.new_goal);

                builder.setTitle("Fijar un objetivo");
                builder.setView(viewDialog);
                builder.setMessage("¡Tu marcas tus metas! Te avisaremos cuando llegues a tus kcal objetivo");
                builder.setPositiveButton("Aceptar", (dialog, id) -> f1.maybeSetGoal(editTextGoal));
                builder.setNegativeButton("Cancelar", null);
                builder.create().show();
                break;

            case R.id.action_reset:
                f1 = ((FragmentOne) getSupportFragmentManager().getFragments().get(0));
                builder = new AlertDialog.Builder(this);
                builder.setTitle("Reiniciar diario");
                builder.setMessage(R.string.reset_mensaje);
                builder.setPositiveButton("Aceptar", (dialog, id) -> f1.resetDay());
                builder.setNegativeButton("Cancelar", null);
                builder.create().show();
                break;

            case R.id.action_help:
                Intent intent = new Intent(this, HelpActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }




    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:
                    FragmentOne f1 = new FragmentOne();
                    return f1;

                case 1:
                    FragmentTwo f2 = new FragmentTwo();
                    return f2;

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }
    }


}
