package vaccinenotifier.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import vaccinenotifier.domain.AppSettings

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = AppSettingsImpl.DATA_STORE_NAME)

class AppSettingsImpl(private val dataStore: DataStore<Preferences>) : AppSettings {

    private val districtId: Preferences.Key<String> = stringPreferencesKey("districtId")

    override suspend fun setDistrictId(id: Int) {
        dataStore.edit { it[districtId] = id.toString() }
    }

    override suspend fun getDistrictId(): String {
        return dataStore.data.map { it[districtId]?:"-1" }.first()
    }

    companion object {
        const val DATA_STORE_NAME = "VNDataStore"
    }
}
