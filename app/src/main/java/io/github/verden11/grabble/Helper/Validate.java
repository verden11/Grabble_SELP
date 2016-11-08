package io.github.verden11.grabble.Helper;

import android.util.Patterns;

/**
 * Created by verden on 08/11/16.
 */

public class Validate {
    public static boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isPasswordValid(String password) {
        // TODO: better password security
        return password.length() > 5;
    }

}
