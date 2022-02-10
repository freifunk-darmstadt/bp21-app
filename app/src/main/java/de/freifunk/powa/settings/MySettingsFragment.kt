package de.freifunk.powa.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import de.freifunk.powa.R

class MySettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
    }
}