package ds.meterscanner.mvvm.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.preference.EditTextPreference
import android.support.v7.preference.Preference
import com.evernote.android.job.rescheduled
import com.github.salomonbrys.kodein.conf.KodeinGlobalAware
import com.github.salomonbrys.kodein.erased.instance
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat
import ds.bindingtools.runActivity
import ds.meterscanner.R
import ds.meterscanner.data.Prefs
import ds.meterscanner.mvvm.SettingsView
import ds.meterscanner.mvvm.viewmodel.SettingsViewModel
import ds.meterscanner.scheduler.Scheduler
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

@SuppressLint("CommitTransaction")
class SettingsActivity : BaseActivity<ViewDataBinding, SettingsViewModel>(), SettingsView {

    override fun instantiateViewModel(state: Bundle?): SettingsViewModel = SettingsViewModel(this)
    override fun getLayoutId(): Int = R.layout.activity_settings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.content, Fragment.instantiate(this, SettingsFragment::class.java.name))
                .commitNow()
        }
    }


    class SettingsFragment : PreferenceFragmentCompat(), KodeinGlobalAware, SharedPreferences.OnSharedPreferenceChangeListener {

        val prefs: Prefs = instance()
        val scheduler: Scheduler = instance()

        val scanTries: EditTextPreference by PreferenceDelegate()
        val city: EditTextPreference by PreferenceDelegate()
        val alarms: Preference by PreferenceDelegate()
        val jpegQuality: EditTextPreference by PreferenceDelegate()
        val boilerTemp: EditTextPreference by PreferenceDelegate()
        val correctionThreshold: EditTextPreference by PreferenceDelegate()
        val shotTimeout: EditTextPreference by PreferenceDelegate()

        override fun onCreatePreferencesFix(savedInstanceState: Bundle?, rootKey: String?) {
            preferenceManager.sharedPreferencesName = "main_prefs"
            addPreferencesFromResource(R.xml.prefs)

            alarms.setOnPreferenceClickListener {
                activity.runActivity<AlarmsActivity>()
                true
            }

        }

        override fun onStart() {
            super.onStart()
            initView()
            preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        }

        override fun onStop() {
            super.onStop()
            preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        }

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
            initView()
        }

        private fun initView() {
            scanTries.summary = scanTries.text
            city.summary = city.text
            jpegQuality.summary = jpegQuality.text
            boilerTemp.summary = boilerTemp.text
            correctionThreshold.summary = correctionThreshold.text
            shotTimeout.summary = shotTimeout.text
            alarms.summary = scheduler.getScheduledJobs().filter { !it.rescheduled }.size.toString()
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

        }

    }

}

@Suppress("UNCHECKED_CAST")
class PreferenceDelegate<out T : Preference> : ReadOnlyProperty<PreferenceFragmentCompat, T> {
    override fun getValue(thisRef: PreferenceFragmentCompat, property: KProperty<*>): T = thisRef.findPreference(property.name) as T

}
