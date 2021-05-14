package vaccinenotifier.data.api

import retrofit2.Response
import retrofit2.http.*
import vaccinenotifier.data.api.model.AppointmentsResponse
import vaccinenotifier.data.api.model.DistrictResponse
import vaccinenotifier.data.api.model.StateResponse

interface APIService {

    @GET(GET_STATES)
    suspend fun getStates(): Response<StateResponse>

    @GET(GET_DISTRICTS_BY_STATES)
    suspend fun getDistrictsByState(@Path("state_id") stateId: Int): Response<DistrictResponse>

    @GET(GET_APPOINTMENTS)
    suspend fun getAppointments(@Query("district_id") districtId: String,
                                @Query("date") date: String)
    : Response<AppointmentsResponse>

    /**
     * The retailer specific API.
     */
    @Suppress("SpellCheckingInspection")
    private companion object APIEndpoints {

        private const val GET_STATES =
            "admin/location/states"

        private const val GET_DISTRICTS_BY_STATES =
            "admin/location/districts/{state_id}"

        private const val GET_APPOINTMENTS =
            "appointment/sessions/public/calendarByDistrict"
    }
}