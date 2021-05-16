package vaccinenotifier.domain


interface Session {

    val id: String

    val ageLimit: Int

    val vaccineType: String

    val available: Int

    val availableDose1: Int

    val availableDose2: Int

}