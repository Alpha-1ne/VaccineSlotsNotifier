package vaccinenotifier.data.api

internal object APIConstants {

    object State {
        const val STATE = "states"
        const val STATE_ID = "state_id"
        const val STATE_NAME = "state_name"
    }

    object District {
        const val DISTRICT = "districts"
        const val DISTRICT_ID = "district_id"
        const val DISTRICT_NAME = "district_name"
    }

    object Appointment {
        const val CENTER = "centers"

        object Center{
            const val ID = "center_id"
            const val NAME = "name"
            const val SESSIONS = "sessions"

            object Session{
                const val ID = "session_id"
                const val AVAILABILITY = "available_capacity"
                const val AGE_LIMIT = "min_age_limit"
                const val VACCINE_TYPE = "vaccine"
            }
        }
    }


}