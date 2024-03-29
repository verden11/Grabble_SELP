package io.github.verden11.grabble;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import io.github.verden11.grabble.Constants.Constants;
import io.github.verden11.grabble.Helper.General;
import io.github.verden11.grabble.Helper.Queries;

import static io.github.verden11.grabble.Constants.Constants.user_id;

public class UserPersonalPages extends AppCompatActivity {
    private final static String TAG = "UserPersonalPages";
    private static Activity thisActivity;
    private static String dictionary = "";
    private static int totalScore = 0;
    private static int totalWords = 0;
    private static Spinner spinner;
    private static String[] daily_tasks;
    private static Button setGoal;
    /**
     * Declare variables for 'keyboard' buttons
     * where all letters are displayed
     */
    static EditText word_enter;
    static Button submit_word;
    static Button backspace;
    static List<Button> buttonsPressedToScrabble;

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
    protected void onResume() {
        super.onResume();
        saveSettings();
    }

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
        daily_tasks = new String[]{"Walk 10km"};

        // Load dictionary
        try {
            InputStream is = this.getResources().openRawResource(R.raw.grabble);
            dictionary = General.convertStreamToString(is).toUpperCase();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        saveSettings();


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
                Snackbar snackbar = Snackbar.make(view, R.string.fab_contact, Snackbar.LENGTH_LONG);
                snackbar.setAction(R.string.fab_action, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                        builder.setMessage(R.string.dialog_back_message)
                                .setPositiveButton(R.string.fab_email_dev, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // Contact Dev, email can be changes in Constants Class
                                        sendEmail(Constants.DEV_EMAIL);
                                    }
                                })
                                .setNegativeButton(R.string.fab_invite, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User will have to enter email address manually
                                        sendEmail("");
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
                snackbar.show();
            }
        });
    }

    private void saveSettings() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(thisActivity);
        int map_style = Integer.valueOf(preferences.getString("map_style", "0"));
        int battery_saver = preferences.getBoolean("battery_saving_mode_switch", false) ? 1 : 0;
        int game_difficulty = Integer.valueOf(preferences.getString("difficulty_list", "0"));
        int[] settings = {battery_saver, game_difficulty, map_style};
        Queries.setSettings(thisActivity, user_id, settings);
    }

    private void sendEmail(String option) {

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:" + option));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.send_email_subject);
        if (option.isEmpty()) {
            emailIntent.putExtra(Intent.EXTRA_TEXT, R.string.send_email_body_dev);
        } else {
            emailIntent.putExtra(Intent.EXTRA_TEXT, R.string.send_email_body_friend);
        }

        try {
            startActivity(Intent.createChooser(emailIntent, "Send email using..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(thisActivity, R.string.send_email_error, Toast.LENGTH_SHORT).show();
        }

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
            Log.d(TAG, "Settings clicked");
            Intent i = new Intent(thisActivity, SettingsActivity.class);
            startActivity(i);
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

            View rootView;
            switch (sectionNumb) {
                case 1:
                    Log.d(TAG, "onCreateView 1 in switch");
                    rootView = inflater.inflate(R.layout.fragment_user_personal_pages_1, container, false);
                    populateKeyboard(rootView);
                    break;
                case 3:
                    Log.d(TAG, "onCreateView 2 in switch");
                    rootView = inflater.inflate(R.layout.fragment_user_personal_pages_2, container, false);
                    populateWords(rootView);
                    populateStatistics(rootView);
                    break;
                case 2:
                    Log.d(TAG, "onCreateView 3 in switch");
                    rootView = inflater.inflate(R.layout.fragment_user_personal_pages_3, container, false);
                    populateSpinner(rootView);
                    break;
                default:
                    rootView = inflater.inflate(R.layout.fragment_user_personal_pages_1, container, false);
                    populateKeyboard(rootView);
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


    private static void populateSpinner(View view) {
        spinner = (Spinner) view.findViewById(R.id.static_spinner);
        setGoal = (Button) view.findViewById(R.id.b_set_goal);
        if (Queries.isGoalSet(thisActivity, user_id)) {
            setGoal.setEnabled(false);
            setGoal.setText(R.string.goal_set);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(thisActivity, android.R.layout.simple_spinner_item, daily_tasks);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        setGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int sp = spinner.getSelectedItemPosition();
                String toastText;
                int randomNum;
                if (sp == 3) {
                    // Daily Task selected
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        randomNum = ThreadLocalRandom.current().nextInt(0, 2 + 1);
                    } else {
                        randomNum = 0;
                    }
                    toastText = daily_tasks[randomNum] + " Set As Goal!";
                } else {
                    toastText = daily_tasks[sp] + " Set As Goal!";
                    randomNum = sp;
                }
                Toast.makeText(thisActivity, toastText, Toast.LENGTH_SHORT).show();
                Queries.setGoal(thisActivity, user_id, randomNum + 1);
                setGoal.setEnabled(false);
                setGoal.setText(R.string.goal_set);
            }
        });
    }


    /**
     * Helpers
     */
    private static void populateKeyboard(View view) {
        buttonsPressedToScrabble = new ArrayList<>();
        word_enter = (EditText) view.findViewById(R.id.et_word);
        word_enter.setInputType(InputType.TYPE_NULL);
        backspace = (Button) view.findViewById(R.id.b_backspace);
        backspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String wordEntered = word_enter.getText().toString();
                char deleted;
                if (!(wordEntered.isEmpty() || buttonsPressedToScrabble.isEmpty())) {
                    // delete last char from word and increment count on keyboard
                    Button b = buttonsPressedToScrabble.remove(buttonsPressedToScrabble.size() - 1);
                    String bWithCount = b.getText().toString();
                    int count = Integer.valueOf(bWithCount.substring(1, bWithCount.length()));
                    count++;
                    // update char count
                    b.setText(getCharWithCount(b, count));
                    // update word input
                    wordEntered = wordEntered.substring(0, wordEntered.length() - 1);
                    word_enter.setText(wordEntered);
                }
            }
        });
        submit_word = (Button) view.findViewById(R.id.b_submit_word);
        submit_word.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String wordToAdd = word_enter.getText().toString().toUpperCase();
                if (wordToAdd.length() == 7 && dictionary.contains(wordToAdd)) {
                    int score = General.calculateWordValue(wordToAdd);
                    // check if user has enough letters
                    boolean enoughLettersInDB = true;
                    List<Character> charList = new ArrayList<>();
                    for (char ch : wordToAdd.toCharArray()) {
                        charList.add(ch);
                    }

                    while (charList.size() > 0) {
                        char ch = charList.get(0);
                        int count = 0;
                        while (charList.contains(ch)) {
                            count++;
                            charList.remove(charList.indexOf(Character.valueOf(ch)));
                        }
                        int countInDB = Queries.getCharCount(thisActivity, user_id, ch);
                        if (count > countInDB) {
                            enoughLettersInDB = false;
                            break;
                        }
                    }

                    if (enoughLettersInDB) {
                        Queries.saveWord(thisActivity, user_id, wordToAdd, score);
                        for (char ch : wordToAdd.toCharArray()) {
                            Queries.removeChar(thisActivity, user_id, ch);
                        }
                        addCountToLetters();
                        buttonsPressedToScrabble.clear();
                        word_enter.setText("");
                        Snackbar.make(view, "Word " + wordToAdd + " inserted", Snackbar.LENGTH_SHORT).show();
                        // update words collected list fragment
                    }
                } else {
                    if (wordToAdd.length() < 7) {
                        Snackbar.make(view, "You must enter 7 letters", Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(view, "Word " + wordToAdd + " does not exist!", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });


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
        addCountToLetters();
    }

    public static void addCountToLetters() {
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
        char ch;
        switch (view.getId()) {
            case R.id.b_letterQ:
                b = letter_q;
                break;
            case R.id.b_letterW:
                b = letter_w;
                break;
            case R.id.b_letterE:
                b = letter_e;
                break;
            case R.id.b_letterR:
                b = letter_r;
                break;
            case R.id.b_letterT:
                b = letter_t;
                break;
            case R.id.b_letterY:
                b = letter_y;
                break;
            case R.id.b_letterU:
                b = letter_u;
                break;
            case R.id.b_letterI:
                b = letter_i;
                break;
            case R.id.b_letterO:
                b = letter_o;
                break;
            case R.id.b_letterP:
                b = letter_p;
                break;
            // second row
            case R.id.b_letterA:
                b = letter_a;
                break;
            case R.id.b_letterS:
                b = letter_s;
                break;
            case R.id.b_letterD:
                b = letter_d;
                break;
            case R.id.b_letterF:
                b = letter_f;
                break;
            case R.id.b_letterG:
                b = letter_g;
                break;
            case R.id.b_letterH:
                b = letter_h;
                break;
            case R.id.b_letterJ:
                b = letter_j;
                break;
            case R.id.b_letterK:
                b = letter_k;
                break;
            case R.id.b_letterL:
                b = letter_l;
                break;
            // third row
            case R.id.b_letterZ:
                b = letter_z;
                break;
            case R.id.b_letterX:
                b = letter_x;
                break;
            case R.id.b_letterC:
                b = letter_c;
                break;
            case R.id.b_letterV:
                b = letter_v;
                break;
            case R.id.b_letterB:
                b = letter_b;
                break;
            case R.id.b_letterN:
                b = letter_n;
                break;
            case R.id.b_letterM:
                b = letter_m;
                break;
            default:
                b = null;
        }
        ch = b.getText().charAt(0);
        text = b.getText().toString();
        count = Integer.valueOf(text.substring(1, text.length()));

        String et_existing = word_enter.getText().toString();
        if (count > 0 && et_existing.length() < 7) {
            buttonsPressedToScrabble.add(b);
            count--;
            b.setText(getCharWithCount(b, count));
            word_enter.setText(et_existing + ch);
        }
    }


    private static void populateWords(View view) {
        String allWords = Queries.getWords(thisActivity, user_id);
        ListView listView = (ListView) view.findViewById(R.id.ll_allwords);
        ArrayList<General.WordScoreLine> objects = new ArrayList<General.WordScoreLine>();
        int wordCount = allWords.length() / 10;
        totalScore = 0;
        totalWords = wordCount;

        for (int i = 0; i < wordCount; i++) {
            String fullWord = allWords.substring(i * 10, i * 10 + 10);
            String word = fullWord.substring(0, fullWord.length() - 3);
            int score = Integer.valueOf(fullWord.substring(7, 10));
            General.WordScoreLine oneLine = new General.WordScoreLine(word, score + "");
            totalScore += score;
            objects.add(oneLine);
        }
        General.CustomAdapter customAdapter = new General.CustomAdapter(thisActivity, objects);
        listView.setAdapter(customAdapter);
    }

    private static void populateStatistics(View view) {
        // Total Score
        TextView tvScore = (TextView) view.findViewById(R.id.tv_totalScore);
        tvScore.setText(R.string.personal_page_2_score);
        String totalScoreStr = tvScore.getText().toString();
        totalScoreStr += " " + totalScore;
        tvScore.setText(totalScoreStr);

        // Words Collected
        TextView tvWordCount = (TextView) view.findViewById(R.id.tv_totalWords);
        tvWordCount.setText(R.string.personal_page_2_wordCount);
        String totalWordsStr = tvWordCount.getText().toString();
        totalWordsStr += " " + totalWords;
        tvWordCount.setText(totalWordsStr);

        // Total Letters Collected
        TextView tvLetterCount = (TextView) view.findViewById(R.id.tv_totalLetters);
        tvLetterCount.setText(R.string.personal_page_2_lettersTotal);
        String letterCountStr = tvLetterCount.getText().toString();
        letterCountStr += " " + Queries.getTotalLetterCount(thisActivity, user_id);
        tvLetterCount.setText(letterCountStr);

        //Total Distance Walked
        TextView tvDistance = (TextView) view.findViewById(R.id.tv_totalDistanceWalked);
        tvDistance.setText(R.string.personal_page_2_distanceWalked);
        String totalDistanceWalked = tvDistance.getText().toString();
        totalDistanceWalked += " " + Queries.getDistanceWalked(thisActivity, user_id);
        tvDistance.setText(totalDistanceWalked);

        // Total Letters Available to use
        TextView tvLettersAvailable = (TextView) view.findViewById(R.id.tv_totalLettersAvailable);
        tvLettersAvailable.setText(R.string.personal_page_2_lettersNow);
        String lettersAvilableStr = tvLettersAvailable.getText().toString();
        lettersAvilableStr += " " + Queries.getAvailableLetterCount(thisActivity, user_id);
        tvLettersAvailable.setText(lettersAvilableStr);
    }
}