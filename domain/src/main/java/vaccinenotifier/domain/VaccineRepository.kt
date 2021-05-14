package vaccinenotifier.domain

import kotlinx.coroutines.flow.Flow

interface VaccineRepository {

    fun getStates(): Flow<Resource<List<State>>>

    fun getDistrictsByState(stateId: Int): Flow<Resource<List<District>>>

    suspend fun getAppointments(districtId: String, date: String)
            : Flow<Resource<List<Center>>>
}