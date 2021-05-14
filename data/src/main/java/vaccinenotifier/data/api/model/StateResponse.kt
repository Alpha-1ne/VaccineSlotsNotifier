package vaccinenotifier.data.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import vaccinenotifier.data.api.APIConstants

@JsonClass(generateAdapter = true)
data class StateResponse(
    @Json(name = APIConstants.State.STATE)
    val states: List<State>?
    )

@JsonClass(generateAdapter = true)
data class State(
    @Json(name = APIConstants.State.STATE_ID)
    override val id: Int,
    @Json(name = APIConstants.State.STATE_NAME)
    override val name: String?
): vaccinenotifier.domain.State