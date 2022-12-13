package com.example.frontend.domain

import android.content.Context
import android.os.Build
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.*

val Context.settingsDataStore : DataStore<Preferences> by preferencesDataStore(name = "MyDataStore");

object DataStoreManager {

    suspend fun deleteAllPreferences(context : Context) {
        context.settingsDataStore.edit {preferences ->
            preferences.clear()
        }
    }

    suspend fun removeValue(context: Context, key : String){
        val wrappedKey = stringPreferencesKey(key);
        context.settingsDataStore.edit { preferences ->
            preferences.remove(wrappedKey)
        }
    }

    suspend fun saveValue(context: Context, key : String, value : String)
    {
        val wrappedKey = stringPreferencesKey(key);
        context.settingsDataStore.edit {
            it[wrappedKey] = value
        }
    }

    suspend fun saveValue(context: Context, key : String, value : Int)
    {
        val wrappedKey = intPreferencesKey(key);
        context.settingsDataStore.edit {
            it[wrappedKey] = value
        }
    }

    suspend fun getStringValue(context: Context, key : String, default : String = "") : String
    {
        val wrappedKey = stringPreferencesKey(key);
        val valueFlow : Flow<String> = context.settingsDataStore.data.map {
            it[wrappedKey] ?: default
        }
        return valueFlow.first()
    }

    suspend fun getIntValue(context: Context, key : String, default: Int = 0) : Int
    {
        val wrappedKey = intPreferencesKey(key);
        val valueFlow : Flow<Int> = context.settingsDataStore.data.map {
            it[wrappedKey] ?: default
        }
        return valueFlow.first()
    }

    suspend fun getLongValue(context: Context, key : String, default: Int = 0) : Long{
        val intValue = getIntValue(context,key, default);
        return intValue.toLong()
    }

    fun decodeToken(jwt : String) : String
    {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return "Requires SDK 26";

        val parts = jwt.split(".");
        return try{
            var charset = charset("UTF-8");
            val header = String(Base64.getUrlDecoder().decode(parts[0].toByteArray(charset)), charset);
            val payload = String(Base64.getUrlDecoder().decode(parts[1].toByteArray(charset)), charset);
            "$header"
            "$payload"
        }
        catch( e : Exception ){
            "Error parsing jwt!";
        }
    }
}