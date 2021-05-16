package vaccinenotifier.data.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import vaccinenotifier.data.api.APIConstants

@JsonClass(generateAdapter = true)
data class AppointmentsResponse(
    @Json(name = APIConstants.Appointment.CENTER)
    val centers: List<Center>?
    )

@JsonClass(generateAdapter = true)
data class Center(
    @Json(name = APIConstants.Appointment.Center.ID)
    override val id: Int,
    @Json(name = APIConstants.Appointment.Center.NAME)
    override val name: String?,
    @Json(name = APIConstants.Appointment.Center.SESSIONS)
    override val sessions: List<Session>?
): vaccinenotifier.domain.Center

@JsonClass(generateAdapter = true)
data class Session(
    @Json(name = APIConstants.Appointment.Center.Session.ID)
    override val id: String,
    @Json(name = APIConstants.Appointment.Center.Session.AGE_LIMIT)
    override val ageLimit: Int,
    @Json(name = APIConstants.Appointment.Center.Session.VACCINE_TYPE)
    override val vaccineType: String,
    @Json(name = APIConstants.Appointment.Center.Session.AVAILABILITY)
    override val available: Int,
    @Json(name = APIConstants.Appointment.Center.Session.AVAILABILITY_DOSE1)
    override val availableDose1: Int,
    @Json(name = APIConstants.Appointment.Center.Session.AVAILABILITY_DOSE2)
    override val availableDose2: Int,
): vaccinenotifier.domain.Session