package io.accroo.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import io.accroo.android.R;
import io.accroo.android.activities.ChangeEmailActivity;
import io.accroo.android.activities.KeyDecryptionActivity;
import io.accroo.android.services.CredentialService;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        Preference emailPreference = findPreference("change_email");
        emailPreference.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(requireActivity(), ChangeEmailActivity.class);
            try {
                String username = CredentialService.getInstance(requireActivity())
                        .getEntry(CredentialService.USERNAME_KEY);
                intent.putExtra("username", username);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(requireActivity(), R.string.general_error, Toast.LENGTH_LONG).show();
            }
            return true;
        });

        Preference passwordPreference = findPreference("change_password");
        passwordPreference.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(requireActivity(), KeyDecryptionActivity.class);
            intent.putExtra("action", KeyDecryptionActivity.UPDATE_PASSWORD);
            try {
                String username = CredentialService.getInstance(requireActivity())
                        .getEntry(CredentialService.USERNAME_KEY);
                intent.putExtra("username", username);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(requireActivity(), R.string.general_error, Toast.LENGTH_LONG).show();
            }
            return true;
        });
    }


}