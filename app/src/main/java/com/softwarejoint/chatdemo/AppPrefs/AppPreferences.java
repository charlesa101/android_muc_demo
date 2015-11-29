package com.softwarejoint.chatdemo.AppPrefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.softwarejoint.chatdemo.constant.AppConstant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class AppPreferences {

    public static AppPreferences sAppPreference;

    private static final String SHARED_PREFERENCE_NAME = "ChatApp";
    private Editor mEditor;
    private SharedPreferences mPreferences;

    private AppPreferences(Context context) {
        mPreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }

    public static AppPreferences getInstance(Context context) {
        if (sAppPreference == null)
            sAppPreference = new AppPreferences(context);
        return sAppPreference;
    }

    public String getPushSubscriptionToken() {
        return mPreferences.getString(SharedPreferenceKeys.push_subscription_token.toString(), null);
    }

    public void setPushSubscriptionToken(String token) {
        mEditor.putString(SharedPreferenceKeys.push_subscription_token.toString(), token);
        mEditor.commit();
    }

    public String getEmailId() {
        return mPreferences.getString(SharedPreferenceKeys.email_id.toString(), null);
    }

    public String getUserName() {
        return mPreferences.getString(SharedPreferenceKeys.username.toString(), null);
    }

    public String getPassword() {
        return mPreferences.getString(SharedPreferenceKeys.password.toString(), null);
    }

    public void setUserName(String userName) {
        mEditor.putString(SharedPreferenceKeys.username.toString(), userName);
        mEditor.commit();
    }

    public void setPassword(String password) {
        mEditor.putString(SharedPreferenceKeys.password.toString(), password);
        mEditor.commit();
    }

    public void removeAccount() {
        mEditor.remove(SharedPreferenceKeys.username.toString());
        mEditor.remove(SharedPreferenceKeys.password.toString());
        mEditor.commit();
    }

    public void setRegistrationState(int state) {
        mEditor.putInt(SharedPreferenceKeys.registration_state.toString(), state);
        mEditor.commit();
    }

    public int getRegitrationState() {
        return mPreferences.getInt(SharedPreferenceKeys.registration_state.toString(), 0);
    }

    public String getDisplayName() {
        return mPreferences.getString(SharedPreferenceKeys.display_name.toString(), null);
    }

    public void setDisplayName(String displayName) {
        mEditor.putString(SharedPreferenceKeys.display_name.toString(), displayName);
        mEditor.commit();
    }

    public String getStatus() {
        return mPreferences.getString(SharedPreferenceKeys.status.toString(), AppConstant.DEFAULT_STATUS);
    }

    public void setStatus(String status) {
        mEditor.putString(SharedPreferenceKeys.status.toString(), status);
        mEditor.commit();
    }

    public boolean clearAllPreferences() {
        mEditor.clear();
        return mEditor.commit();
    }

    public LinkedHashSet<String> getBlockedUsers() {
        Set<String> blockedUsers = mPreferences.getStringSet(SharedPreferenceKeys.privacy_list.toString(), null);
        if (blockedUsers == null) {
            return new LinkedHashSet<>();
        } else {
            return new LinkedHashSet<>(blockedUsers);
        }
    }

    public void addToBlockedUsers(String username) {
        LinkedHashSet<String> blockedUsers = getBlockedUsers();
        blockedUsers.add(username);
        mEditor.putStringSet(SharedPreferenceKeys.privacy_list.toString(), blockedUsers);
        mEditor.commit();
    }

    public void removeFromBlockedUsers(String username) {
        LinkedHashSet<String> blockedUsers = getBlockedUsers();
        blockedUsers.remove(username);
        mEditor.putStringSet(SharedPreferenceKeys.privacy_list.toString(), blockedUsers);
        mEditor.commit();
    }

    public ArrayList<String> getAvailableStatusMsgs() {
        Set<String> statusMsgs = mPreferences.getStringSet(SharedPreferenceKeys.availableStatusMsgs.toString(), null);
        if (statusMsgs == null) {
            return new ArrayList<>(Arrays.asList(AppConstant.AVAILABLE_STATUS_MSGS));
        } else {
            return new ArrayList<>(statusMsgs);
        }
    }

    public void setAvailableStatusMsgs(ArrayList<String> statusList) {
        mEditor.putStringSet(SharedPreferenceKeys.availableStatusMsgs.toString(), new LinkedHashSet<String>(statusList));
        mEditor.commit();
    }

    public boolean isPresenceVisible() {
        return mPreferences.getBoolean(SharedPreferenceKeys.presenceVisible.toString(), true);
    }

	public void setResourceName(String resource)
	{
		mEditor.putString(SharedPreferenceKeys.resource.toString(), resource);
		mEditor.apply();
	}

	public  String getResourceName()
	{
		return mPreferences.getString(SharedPreferenceKeys.resource.toString(), null);
	}
    enum SharedPreferenceKeys {
        username, password, token, registration_state, status, display_name,
        presenceVisible, email_id, push_subscription_token, privacy_list, availableStatusMsgs, resource
    }
}
