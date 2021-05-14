package vaccinenotifier.domain


interface Session {

    val id: String

    val ageLimit: Int

    val vaccineType: String

    val available: Int

}