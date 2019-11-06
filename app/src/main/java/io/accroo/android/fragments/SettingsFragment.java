package io.accroo.android.fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import io.accroo.android.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}