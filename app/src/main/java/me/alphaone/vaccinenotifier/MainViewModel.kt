package me.alphaone.vaccinenotifier

import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import vaccinenotifier.domain.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: VaccineRepository,
    private val appSettings: AppSettings,
) : ViewModel() {


    private val _states: MutableLiveData<Resource<List<State>>> = MutableLiveData()
    val states: LiveData<Resource<List<State>>>
     get() = _states

    private val _district: MutableLiveData<Resource<List<District>>> = MutableLiveData()
    val district: LiveData<Resource<List<District>>>
     get() = _district

    private val _isScheduled: MutableLiveData<ScheduledData> = MutableLiveData()
    val isScheduled: LiveData<ScheduledData>
     get() = _isScheduled


    init {
     loadStates()
        loadScheduledState()
    }

    private fun loadScheduledState() {
        viewModelScope.launch {
            appSettings.getScheduledDataFlow().collect {
                _isScheduled.postValue(it)
            }
        }
    }

    private fun loadStates(){
        viewModelScope.launch(Dispatchers.Default) {
            repository.getStates().collect{ res: Resource<List<State>> ->
                _states.postValue(res)
            }
        }
    }

    fun getDistricts(stateId:Int){
        viewModelScope.launch {
            repository.getDistrictsByState(stateId ).collect{ res: Resource<List<District>> ->
                _district.postValue(res)
            }
        }
    }

    fun test(context:Context, district: String){
        viewModelScope.launch {
            repository.getAppointments(district,"14-05-2021").collect {
                if(it is Success)
                    processData(context, it.data)
            }
        }
    }


    private fun processData(
        context: Context,
        appointmentsResponse: List<Center>?
    ) {
        var vaccineCount = 0
        appointmentsResponse?.filter { center ->
            center.sessions?.any { session -> session.available>0 && session.ageLimit == 45 }?:false
        }?.forEach {
            it.sessions?.forEach {
                    session ->
                vaccineCount+=session.available
            }
        }
        if(vaccineCount>0)
            showNotification(context,"New Slots Available", "$vaccineCount slots available in your area")
    }

    private fun showNotification(
        context: Context,
        messageTitle: String?,
        messageText: String?
    ) {
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(
            context,
            "VC_CHANNEL_ID"
        )
            .setContentTitle(messageTitle)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentText(messageText)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageText))

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(
            102,
            notificationBuilder.build()
        )
    }

    fun saveDistrictId(district: District){
        viewModelScope.launch {
            appSettings.setDistrictId(district.id,district.name)
        }
    }

    fun saveScheduledState(isScheduled: Boolean){
        viewModelScope.launch {
            appSettings.setScheduled(isScheduled)
        }
    }

}