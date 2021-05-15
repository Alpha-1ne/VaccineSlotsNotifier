package vaccinenotifier.domain

import kotlinx.coroutines.flow.Flow


interface AppSettings {

    suspend fun getScheduledData(): ScheduledData
    suspend fun getScheduledDataFlow(): Flow<ScheduledData>
    suspend fun setDistrictId(id: Int,name:String?)
    suspend fun setScheduled(isScheduled:Boolean)
}