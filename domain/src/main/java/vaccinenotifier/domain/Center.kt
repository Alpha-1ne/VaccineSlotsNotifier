package vaccinenotifier.domain


interface Center {

    val id: Int

    val name: String?

    val sessions: List<Session>?
}