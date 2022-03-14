package de.freifunk.powa.settings

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.MultiSelectListPreference
import androidx.preference.PreferenceFragmentCompat
import de.freifunk.powa.R

class MySettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
        findPreference<ListPreference>(getString(R.string.multiscan_key))?.apply {
            val entryDisplay =
                listOf("1", "2", "3", "4").toTypedArray()
            val entryValuesApps = listOf(
                "1","2","3","4"
            ).toTypedArray()

            entries = entryDisplay
            this.entryValues = entryValuesApps
        }
    }
}
