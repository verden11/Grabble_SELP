package io.github.verden11.grabble;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import io.github.verden11.grabble.Constants.Constants;
import io.github.verden11.grabble.Helper.Queries;

public class UserPersonalPages extends AppCompatActivity {
    private final static String TAG = "UserPersonalPages";
    private static int user_id;
    private static Activity thisActivity;

    /**
     * Declare variables for 'keyboard' buttons
     * where all letters are displayed
     */
    // top row
    static Button letter_q;
    static Button letter_w;
    static Button letter_e;
    static Button letter_r;
    static Button letter_t;
    static Button letter_y;
    static Button letter_u;
    static Button letter_i;
    static Button letter_o;
    static Button letter_p;
    // middle row
    static Button letter_a;
    static Button letter_s;
    static Button letter_d;
    static Button letter_f;
    static Button letter_g;
    static Button letter_h;
    static Button letter_j;
    static Button letter_k;
    static Button letter_l;
    // bottom row
    static Button letter_z;
    static Button letter_x;
    static Button letter_c;
    static Button letter_v;
    static Button letter_b;
    static Button letter_n;
    static Button letter_m;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_user_personal_pages);

        /**
         * Init variables
         */

        thisActivity = this;
        // get the intent which started this activity
        Intent intent = getIntent();
        user_id = intent.getIntExtra(Constants.USER_ID, 0);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        Log.d(TAG, "FAB button initialized");
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_personal_pages, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
            Log.d(TAG, "PlaceholderFragment");
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            Log.d(TAG, "newInstance");
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            Log.d(TAG, "onActivityCreated");
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            int sectionNumb = getArguments().getInt(ARG_SECTION_NUMBER);
            Log.d(TAG, "onCreateView " + sectionNumb);
//            View rootView = inflater.inflate(R.layout.fragment_user_personal_pages, container, false);
//            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
//
//            Button button = (Button) rootView.findViewById(R.id.b_first);

            View rootView;


            switch (sectionNumb) {
//                View rootView;
                case 1:
                    Log.d(TAG, "onCreateView 1 in switch");
                    rootView = inflater.inflate(R.layout.fragment_user_personal_pages_1, container, false);
                    populateKeyboard(rootView);

                    break;
                case 2:
                    Log.d(TAG, "onCreateView 2 in switch");
                    rootView = inflater.inflate(R.layout.fragment_user_personal_pages, container, false);
//                    button.setText("2");
                    break;
                case 3:
                    Log.d(TAG, "onCreateView 3 in switch");
                    rootView = inflater.inflate(R.layout.fragment_user_personal_pages, container, false);
//                    button.setText("3");
                    break;
                default:
                    Log.d(TAG, "onCreateView default");
                    rootView = inflater.inflate(R.layout.fragment_user_personal_pages, container, false);
                    break;
            }
            return rootView;
        }


    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            Log.d(TAG, "SectionsPagerAdapter");
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(TAG, "getItem");
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
//            Log.d(TAG, "getCount");
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Log.d(TAG, "getPageTitle");
            switch (position) {
                case 0:
                    Log.d(TAG, "0");
                    return "Collection";
                case 1:
                    Log.d(TAG, "1");
                    return "SECTION 2";
                case 2:
                    Log.d(TAG, "2");
                    return "Preferences";
            }
            return null;
        }
    }


    /**
     * Helpers
     */
    private static void populateKeyboard(View view) {
        // assign all keyboard buttons
        letter_w = (Button) view.findViewById(R.id.b_letterW);
        letter_e = (Button) view.findViewById(R.id.b_letterE);
        letter_r = (Button) view.findViewById(R.id.b_letterR);
        letter_t = (Button) view.findViewById(R.id.b_letterT);
        letter_y = (Button) view.findViewById(R.id.b_letterY);
        letter_u = (Button) view.findViewById(R.id.b_letterU);
        letter_i = (Button) view.findViewById(R.id.b_letterI);
        letter_o = (Button) view.findViewById(R.id.b_letterO);
        letter_p = (Button) view.findViewById(R.id.b_letterP);

        letter_a = (Button) view.findViewById(R.id.b_letterA);
        letter_s = (Button) view.findViewById(R.id.b_letterS);
        letter_d = (Button) view.findViewById(R.id.b_letterD);
        letter_f = (Button) view.findViewById(R.id.b_letterF);
        letter_g = (Button) view.findViewById(R.id.b_letterG);
        letter_h = (Button) view.findViewById(R.id.b_letterH);
        letter_j = (Button) view.findViewById(R.id.b_letterJ);
        letter_k = (Button) view.findViewById(R.id.b_letterK);
        letter_l = (Button) view.findViewById(R.id.b_letterL);

        letter_z = (Button) view.findViewById(R.id.b_letterZ);
        letter_x = (Button) view.findViewById(R.id.b_letterX);
        letter_c = (Button) view.findViewById(R.id.b_letterC);
        letter_v = (Button) view.findViewById(R.id.b_letterV);
        letter_b = (Button) view.findViewById(R.id.b_letterB);
        letter_n = (Button) view.findViewById(R.id.b_letterN);
        letter_m = (Button) view.findViewById(R.id.b_letterM);

        int countW = Queries.getCharCount(thisActivity, user_id, letter_w.getText().charAt(0));




    }


}
