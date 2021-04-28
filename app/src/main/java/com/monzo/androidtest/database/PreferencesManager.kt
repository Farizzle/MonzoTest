package com.monzo.androidtest.database

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.datastore.preferences.preferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PreferencesManager"

data class FilterPreferences(val showFavourites: Boolean)

@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext context: Context) {

    object PreferencesKey {
        val FAVOURITES_SELECTED = preferencesKey<Boolean>("favourite_selected")
    }

    private val dataStore = context.createDataStore("user_preferences")

    val preferencesFlow = dataStore.data.catch { exception ->
        if (exception is IOException) {
            Log.e(TAG, "Error reading preferences - ", exception)
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { preferences ->
        val favouritesSelected = preferences[PreferencesKey.FAVOURITES_SELECTED] ?: false
        FilterPreferences(favouritesSelected)
    }

    suspend fun updateFavouritesSelected(favouritesIsSelected: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.FAVOURITES_SELECTED] = favouritesIsSelected
        }
    }
}