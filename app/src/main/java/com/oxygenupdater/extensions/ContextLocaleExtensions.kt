package com.oxygenupdater.extensions

import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.oxygenupdater.R
import java.util.*

/**
 * This file can't use any Koin-managed singletons, because most functions
 * here can be called before [android.app.Application.onCreate]
 * (which is where Koin is initialized)
 */

fun Context.attachWithLocale() = persistAndSetLocale(
    PreferenceManager.getDefaultSharedPreferences(this).getString(
        getString(R.string.key_language_id),
        Locale.getDefault().language
    )!!
)

fun Context.setLocale(
    languageCode: String
) = Locale(languageCode).let {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        LocaleList.setDefault(LocaleList(it))
    } else {
        Locale.setDefault(it)
    }
    updateResources(it)
}

fun Context.persistAndSetLocale(
    languageCode: String
) = PreferenceManager.getDefaultSharedPreferences(this).edit {
    putString(getString(R.string.key_language_id), languageCode)
}.let {
    setLocale(languageCode)
}

private fun Context.updateResources(
    locale: Locale
): Context = resources.configuration.let { config ->
    config.setLocale(locale)

    @Suppress("DEPRECATION")
    resources.updateConfiguration(config, resources.displayMetrics)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        createConfigurationContext(config)
    } else {
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
        this
    }
}
