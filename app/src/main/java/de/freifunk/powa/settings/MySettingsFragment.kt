package de.freifunk.powa.settings

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.MultiSelectListPreference
import androidx.preference.PreferenceFragmentCompat
import de.freifunk.powa.R

class MySettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
        if(!findPreference<ListPreference>(resources.getString(R.string.multiscan_key))!!.isEnabled)
            findPreference<ListPreference>(resources.getString(R.string.multiscan_key))!!.value = "2"
    }
}
