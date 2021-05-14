package vaccinenotifier.data.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import vaccinenotifier.data.api.APIConstants

@JsonClass(generateAdapter = true)
data class DistrictResponse(
    @Json(name = APIConstants.District.DISTRICT)
    val districts: List<District>?
    )

@JsonClass(generateAdapter = true)
data class District(
    @Json(name = APIConstants.District.DISTRICT_ID)
    override val id: Int,
    @Json(name = APIConstants.District.DISTRICT_NAME)
    override val name: String?
): vaccinenotifier.domain.District