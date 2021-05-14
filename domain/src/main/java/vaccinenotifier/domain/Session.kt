package vaccinenotifier.domain


interface Session {

    val id: Int

    val ageLimit: Int

    val vaccineType: String

    val available: Int

}