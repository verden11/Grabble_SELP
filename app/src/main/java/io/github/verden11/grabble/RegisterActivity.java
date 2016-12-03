package io.github.verden11.grabble;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import io.github.verden11.grabble.Helper.DbHelper;
import io.github.verden11.grabble.Helper.Hashes;
import io.github.verden11.grabble.Helper.Validate;

/**
 * A register screen that offers register via nickname/email/password.
 */
public class RegisterActivity extends AppCompatActivity {
    private final String TAG = "RegisterActivityTAG";
    private SQLiteDatabase db;


    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;


    // UI references.
    private EditText mNicknameView;
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // get SQLite
        DbHelper mDbHelper = new DbHelper(this);
        // Gets the data repository in write mode
        db = mDbHelper.getWritableDatabase();


        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);
        mNicknameView = (EditText) findViewById(R.id.nickname);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mRegisterLogin = (Button) findViewById(R.id.register_login_button);
        mRegisterLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }


    /**
     * Attempts to register the account specified by the register form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mNicknameView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the register&login attempt.
        String nickname = mNicknameView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // TODO check if nickname is taken
        // Check if nickname is valid
        if (TextUtils.isEmpty(nickname)) {
            mNicknameView.setError(getString(R.string.error_field_required));
            focusView = mNicknameView;
            cancel = true;
        } else if (isNicknameTaken(nickname)) {
            mNicknameView.setError(getString(R.string.error_nickname_taken));
            focusView = mNicknameView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!Validate.isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!Validate.isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        } else if (isEmailTaken(email)) {
            mEmailView.setError(getString(R.string.error_email_taken));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(nickname, email, password);
            mAuthTask.execute((Void) null);
        }
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    public void openMapsActivity() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mNickname;
        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String nickname, String email, String password) {
            mNickname = nickname;
            mEmail = email.toLowerCase();
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            // Create a new map of values, where column names are the keys
            String mPasswordHash = Hashes.hashPassword(mEmail, mPassword);

            ContentValues valuesUser = new ContentValues();
            valuesUser.put(DbHelper.UsersEntry.COLUMN_NICKNAME, mNickname);
            valuesUser.put(DbHelper.UsersEntry.COLUMN_EMAIL, mEmail);
            valuesUser.put(DbHelper.UsersEntry.COLUMN_PASSWORD, mPasswordHash);

            // Insert the new row, returning the primary key value of the new row
            long rowID = db.insert(DbHelper.UsersEntry.TABLE_NAME, null, valuesUser);

            ContentValues valuesStats = new ContentValues();
            valuesStats.put(DbHelper.Stats.COLUMN_USER_ID, rowID);
            valuesStats.put(DbHelper.Stats.COLUMN_DISTANCE_WALKED, 0);
            valuesStats.put(DbHelper.Stats.COLUMN_LETTERS, "");
            valuesStats.put(DbHelper.Stats.COLUMN_WORDS, "");
            valuesStats.put(DbHelper.Stats.COLUMN_SCORE, 0);

            db.insert(DbHelper.Stats.TABLE_NAME, null, valuesStats);


            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                openMapsActivity();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    private boolean isNicknameTaken(String nickname) {
        // check if user email exists in database
        Cursor c = db.rawQuery("SELECT count(1) FROM " + DbHelper.UsersEntry.TABLE_NAME + " WHERE " + DbHelper.UsersEntry.COLUMN_NICKNAME + " = '" + nickname + "'", null);
        c.moveToFirst();
        int count = c.getInt(0);
        c.close();
        if (count == 1) {
            return true;
        }
        return false;
    }

    private boolean isEmailTaken(String email) {
        // check if user email exists in database
        Cursor c = db.rawQuery("SELECT count(1) FROM " + DbHelper.UsersEntry.TABLE_NAME + " WHERE " + DbHelper.UsersEntry.COLUMN_EMAIL + " = '" + email + "'", null);
        c.moveToFirst();
        int count = c.getInt(0);
        c.close();
        if (count == 1) {
            return true;
        }
        return false;
    }
}

