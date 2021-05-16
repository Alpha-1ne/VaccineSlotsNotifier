package vaccinenotifier.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import vaccinenotifier.domain.AppSettings
import vaccinenotifier.data.api.model.District
import vaccinenotifier.domain.ScheduledData

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = AppSettingsImpl.DATA_STORE_NAME)

class AppSettingsImpl(private val dataStore: DataStore<Preferences>) : AppSettings {

    private val districtIdKey: Preferences.Key<String> = stringPreferencesKey("districtId")
    private val districtNameKey: Preferences.Key<String> = stringPreferencesKey("districtName")
    private val isScheduledKey: Preferences.Key<Boolean> = booleanPreferencesKey("isScheduled")
    private val dose1Key: Preferences.Key<Boolean> = booleanPreferencesKey("doseOne")
    private val dose2Key: Preferences.Key<Boolean> = booleanPreferencesKey("doseTwo")

    override suspend fun setDistrictId(id: Int, name:String?) {
        dataStore.edit {
            it[districtIdKey] = id.toString()
            it[districtNameKey] = name.toString()
        }
    }

    override suspend fun setScheduled(isScheduled: Boolean, dose1:Boolean, dose2:Boolean) {
        dataStore.edit {
            it[isScheduledKey] = isScheduled
        }
    }

    override suspend fun getScheduledData(): ScheduledData {
        val id = dataStore.data.map { it[districtIdKey]?:"-1" }.first()
        val name = dataStore.data.map { it[districtNameKey]?:"" }.first()
        val isScheduled = dataStore.data.map { it[isScheduledKey]?:false }.first()
        val dose1 = dataStore.data.map { it[dose1Key]?:false }.first()
        val dose2 = dataStore.data.map { it[dose2Key]?:false }.first()
        return ScheduledData(isScheduled, District(Integer.parseInt(id), name),dose1,dose2)
    }

    override suspend fun getScheduledDataFlow(): Flow<ScheduledData> {
        return dataStore.data.map { ScheduledData(it[isScheduledKey]?:false,
            District(Integer.parseInt(it[districtIdKey]?:"-1"),it[districtNameKey]?:""
             ), it[dose1Key]?:false, it[dose2Key]?:false) }
    }

    companion object {
        const val DATA_STORE_NAME = "VNDataStore"
    }
}
