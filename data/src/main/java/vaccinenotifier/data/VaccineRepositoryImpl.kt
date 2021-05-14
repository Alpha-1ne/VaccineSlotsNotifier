package vaccinenotifier.data

import android.util.Log
import kotlinx.coroutines.flow.Flow
import vaccinenotifier.data.api.APIService
import vaccinenotifier.domain.*
import javax.inject.Inject


class VaccineRepositoryImpl @Inject constructor(private val service: APIService) :
    VaccineRepository {

    override fun getStates(): Flow<Resource<List<State>>> = networkBoundRequest(
        createRequest = { },
        request = {
            service.getStates()
        },
        onSuccess = { response ->
            response.states ?: emptyList()
        },
        onMappingFailure = { },
        onApiFailure = { }
    )

    override fun getDistrictsByState(stateId: Int): Flow<Resource<List<District>>> =
        networkBoundRequest(
            createRequest = { },
            request = {
                service.getDistrictsByState(stateId)
            },
            onSuccess = { response ->
                response.districts ?: emptyList()
            },
            onMappingFailure = { },
            onApiFailure = { }
        )


    override suspend fun getAppointments(
        districtId: String,
        date: String
    ): Flow<Resource<List<Center>>> = networkBoundRequest(
        createRequest = { },
        request = {
            service.getAppointments(districtId, date)
        },
        onSuccess = { response ->
            response.centers ?: emptyList()
        },
        onMappingFailure = { Log.d("Error",it.toString())},
        onApiFailure = { Log.d("Error",it) }
    )

}