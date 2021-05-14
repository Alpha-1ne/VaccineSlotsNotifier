package vaccinenotifier.domain


interface AppSettings {

    suspend fun getDistrictId(): String
    suspend fun setDistrictId(id: Int)
}