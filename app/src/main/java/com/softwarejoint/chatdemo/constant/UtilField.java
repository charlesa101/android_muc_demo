package com.softwarejoint.chatdemo.constant;

import android.util.Log;

import java.util.UUID;
import java.util.regex.Pattern;

public class UtilField {

    public static final boolean debugOn = true;

    public static void debug(String tag, String stmt) {
        if (debugOn && tag != null && stmt != null) {
            Log.e(tag, stmt);
        }
    }

    public static String getUUID4() {
        return UUID.randomUUID().toString();
    }

    public static boolean isEmailAddressValid(String email) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        return pattern.matcher(email.toLowerCase().trim()).find();
    }

    public enum LOCAL_DIR {
        PROFILE("profile"),
        DB_FILE("db"),
        OTHER("other");

        private final String path;

        LOCAL_DIR(String p) {
            path = p;
        }

        public boolean equalsName(String p) {
            return path.equalsIgnoreCase(p);
        }

        public String toString() {
            return path;
        }
    }
}
