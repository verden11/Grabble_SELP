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
import android.text.Spannable;
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
import android.view.WindowManager;
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
        // hide keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

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
        letter_q = (Button) view.findViewById(R.id.b_letterQ);
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

        // set number of how many of each letter is collected

        letter_q.setText(getCharWithCount(letter_q));
        letter_w.setText(getCharWithCount(letter_w));
        letter_e.setText(getCharWithCount(letter_e));
        letter_r.setText(getCharWithCount(letter_r));
        letter_t.setText(getCharWithCount(letter_t));
        letter_y.setText(getCharWithCount(letter_y));
        letter_u.setText(getCharWithCount(letter_u));
        letter_i.setText(getCharWithCount(letter_i));
        letter_o.setText(getCharWithCount(letter_o));
        letter_p.setText(getCharWithCount(letter_p));

        letter_a.setText(getCharWithCount(letter_a));
        letter_s.setText(getCharWithCount(letter_s));
        letter_d.setText(getCharWithCount(letter_d));
        letter_f.setText(getCharWithCount(letter_f));
        letter_g.setText(getCharWithCount(letter_g));
        letter_h.setText(getCharWithCount(letter_h));
        letter_j.setText(getCharWithCount(letter_j));
        letter_k.setText(getCharWithCount(letter_k));
        letter_l.setText(getCharWithCount(letter_l));

        letter_z.setText(getCharWithCount(letter_z));
        letter_x.setText(getCharWithCount(letter_x));
        letter_c.setText(getCharWithCount(letter_c));
        letter_v.setText(getCharWithCount(letter_v));
        letter_b.setText(getCharWithCount(letter_b));
        letter_n.setText(getCharWithCount(letter_n));
        letter_m.setText(getCharWithCount(letter_m));
    }

    public static SpannableStringBuilder getCharWithCount(Button b) {
        char ch = b.getText().charAt(0);
        int count = Queries.getCharCount(thisActivity, user_id, ch);
        String text = "" + ch + count;
        SpannableStringBuilder ssb = new SpannableStringBuilder(text);
        ssb.setSpan(new SuperscriptSpan(), 1, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(new RelativeSizeSpan(0.7f), 1, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ssb;
    }

    public static SpannableStringBuilder getCharWithCount(Button b, int count) {
        char ch = b.getText().charAt(0);
        String text = "" + ch + count;
        SpannableStringBuilder ssb = new SpannableStringBuilder(text);
        ssb.setSpan(new SuperscriptSpan(), 1, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(new RelativeSizeSpan(0.7f), 1, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ssb;
    }

    public void charButtonPress(View view) {
        view.getId();
        Button b;
        int count;
        String text;
        switch (view.getId()) {
            case R.id.b_letterQ:
                b = letter_q;
                text = b.getText().toString();
                count = Integer.valueOf(text.substring(1, text.length()));
                b.setText(getCharWithCount(b, --count));
                break;
            case R.id.b_letterW:
                b = letter_w;
                text = b.getText().toString();
                count = Integer.valueOf(text.substring(1, text.length()));
                b.setText(getCharWithCount(b, --count));
                break;
            case R.id.b_letterE:
                b = letter_e;
                text = b.getText().toString();
                count = Integer.valueOf(text.substring(1, text.length()));
                b.setText(getCharWithCount(b, --count));
                break;
            case R.id.b_letterR:
                b = letter_r;
                text = b.getText().toString();
                count = Integer.valueOf(text.substring(1, text.length()));
                b.setText(getCharWithCount(b, --count));
                break;
            case R.id.b_letterT:
                b = letter_t;
                text = b.getText().toString();
                count = Integer.valueOf(text.substring(1, text.length()));
                b.setText(getCharWithCount(b, --count));
                break;
            case R.id.b_letterY:
                b = letter_y;
                text = b.getText().toString();
                count = Integer.valueOf(text.substring(1, text.length()));
                b.setText(getCharWithCount(b, --count));
                break;
            case R.id.b_letterU:
                b = letter_u;
                text = b.getText().toString();
                count = Integer.valueOf(text.substring(1, text.length()));
                b.setText(getCharWithCount(b, --count));
                break;
            case R.id.b_letterI:
                b = letter_i;
                text = b.getText().toString();
                count = Integer.valueOf(text.substring(1, text.length()));
                b.setText(getCharWithCount(b, --count));
                break;
            case R.id.b_letterO:
                b = letter_o;
                text = b.getText().toString();
                count = Integer.valueOf(text.substring(1, text.length()));
                b.setText(getCharWithCount(b, --count));
                break;
            case R.id.b_letterP:
                b = letter_p;
                text = b.getText().toString();
                count = Integer.valueOf(text.substring(1, text.length()));
                b.setText(getCharWithCount(b, --count));
                break;
            // second row
            case R.id.b_letterA:
                b = letter_a;
                text = b.getText().toString();
                count = Integer.valueOf(text.substring(1, text.length()));
                b.setText(getCharWithCount(b, --count));
                break;
            case R.id.b_letterS:
                b = letter_s;
                text = b.getText().toString();
                count = Integer.valueOf(text.substring(1, text.length()));
                b.setText(getCharWithCount(b, --count));
                break;
            case R.id.b_letterD:
                b = letter_d;
                text = b.getText().toString();
                count = Integer.valueOf(text.substring(1, text.length()));
                b.setText(getCharWithCount(b, --count));
                break;
            case R.id.b_letterF:
                b = letter_f;
                text = b.getText().toString();
                count = Integer.valueOf(text.substring(1, text.length()));
                b.setText(getCharWithCount(b, --count));
                break;
            case R.id.b_letterG:
                b = letter_g;
                text = b.getText().toString();
                count = Integer.valueOf(text.substring(1, text.length()));
                b.setText(getCharWithCount(b, --count));
                break;
            case R.id.b_letterH:
                b = letter_h;
                text = b.getText().toString();
                count = Integer.valueOf(text.substring(1, text.length()));
                b.setText(getCharWithCount(b, --count));
                break;
            case R.id.b_letterJ:
                b = letter_j;
                text = b.getText().toString();
                count = Integer.valueOf(text.substring(1, text.length()));
                b.setText(getCharWithCount(b, --count));
                break;
            case R.id.b_letterK:
                b = letter_k;
                text = b.getText().toString();
                count = Integer.valueOf(text.substring(1, text.length()));
                b.setText(getCharWithCount(b, --count));
                break;
            case R.id.b_letterL:
                b = letter_l;
                text = b.getText().toString();
                count = Integer.valueOf(text.substring(1, text.length()));
                b.setText(getCharWithCount(b, --count));
                break;
            // third row
            case R.id.b_letterZ:
                b = letter_z;
                text = b.getText().toString();
                count = Integer.valueOf(text.substring(1, text.length()));
                b.setText(getCharWithCount(b, --count));
                break;
            case R.id.b_letterX:
                b = letter_x;
                text = b.getText().toString();
                count = Integer.valueOf(text.substring(1, text.length()));
                b.setText(getCharWithCount(b, --count));
                break;
            case R.id.b_letterC:
                b = letter_c;
                text = b.getText().toString();
                count = Integer.valueOf(text.substring(1, text.length()));
                b.setText(getCharWithCount(b, --count));
                break;
            case R.id.b_letterV:
                b = letter_v;
                text = b.getText().toString();
                count = Integer.valueOf(text.substring(1, text.length()));
                b.setText(getCharWithCount(b, --count));
                break;
            case R.id.b_letterB:
                b = letter_b;
                text = b.getText().toString();
                count = Integer.valueOf(text.substring(1, text.length()));
                b.setText(getCharWithCount(b, --count));
                break;
            case R.id.b_letterN:
                b = letter_n;
                text = b.getText().toString();
                count = Integer.valueOf(text.substring(1, text.length()));
                b.setText(getCharWithCount(b, --count));
                break;
            case R.id.b_letterM:
                b = letter_m;
                text = b.getText().toString();
                count = Integer.valueOf(text.substring(1, text.length()));
                b.setText(getCharWithCount(b, --count));
                break;
            default:
                Log.d(TAG, "UNKNOWN");
        }
    }


}
