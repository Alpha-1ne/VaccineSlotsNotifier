package vaccinenotifier.domain

data class ScheduledData(val isScheduled: Boolean, val district: District, val dose1:Boolean, val dose2:Boolean)
