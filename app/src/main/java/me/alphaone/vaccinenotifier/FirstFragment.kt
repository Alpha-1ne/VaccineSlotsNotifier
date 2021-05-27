package me.alphaone.vaccinenotifier

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import me.alphaone.autostart.AutoStartPermissionHelper
import me.alphaone.vaccinenotifier.databinding.FragmentFirstBinding
import vaccinenotifier.data.NotifierService
import vaccinenotifier.domain.Loading
import vaccinenotifier.domain.Success


@AndroidEntryPoint
/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()

    private var _binding: FragmentFirstBinding? = null

    private var selectedState: Int = -1
    private var selectedDistrict: Int = -1
    private var dose1: Boolean = true
    private var dose2: Boolean = true

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().actionBar?.show()
        binding.switchDose1.setOnCheckedChangeListener { _, isChecked ->
            dose1 = isChecked
        }
        binding.switchDose2.setOnCheckedChangeListener { _, isChecked ->
            dose2 = isChecked
        }
        viewModel.states.observe(viewLifecycleOwner) {
            when (it) {
                is Success -> {
                    binding.states.setAdapter(ArrayAdapter(
                        requireContext(),
                        R.layout.list_item,
                        it.data.map { state ->
                            state.name
                        }
                    ))
                    binding.states.setOnItemClickListener { _, _, position, _ ->
                        selectedState = it.data[position].id
                        viewModel.getDistricts(selectedState)
                    }
                    binding.progress.visibility = View.GONE
                }
                is Loading -> {
                    binding.progress.visibility = View.VISIBLE
                }
                else -> {

                }
            }
        }
        viewModel.district.observe(viewLifecycleOwner) {
            when (it) {
                is Success -> {
                    binding.districts.setAdapter(ArrayAdapter(
                        requireContext(),
                        R.layout.list_item,
                        it.data.map { district ->
                            district.name
                        }
                    ))
                    binding.districts.setOnItemClickListener { _, _, position, _ ->
                        selectedDistrict = it.data[position].id
                        viewModel.saveDistrictId(it.data[position])
                    }

                    binding.progress.visibility = View.GONE
                }

                is Loading -> {
                    binding.progress.visibility = View.VISIBLE
                }
                else -> {

                }
            }
        }
        viewModel.isScheduled.observe(viewLifecycleOwner) {
            it?.let {
                if (it.isScheduled) {
                    binding.button.text = getString(R.string.action_update)
                    binding.cardViewScheduler.visibility = View.VISIBLE
                    val doseText = StringBuilder("(")
                    if(it.dose1)
                        doseText.append("Dose 1")
                    if(it.dose1 && it.dose2)
                        doseText.append(" & ")
                    if(it.dose2)
                        doseText.append("Dose 2")
                    doseText.append(")")
                    binding.scheduleText.text = getString(R.string.schedule_text, doseText.toString(),it.district.name)
                    binding.stop.setOnClickListener {
                        stopWork()
                    }
                } else
                    binding.cardViewScheduler.visibility = View.GONE
            }
        }
        binding.button.setOnClickListener {
            binding.progress.visibility = View.VISIBLE
            if (selectedDistrict == -1) {
                showMessage(getString(R.string.warning_district))
                return@setOnClickListener
            }
            if (!dose1 && !dose2) {
                showMessage(getString(R.string.warning_select_dose))
                return@setOnClickListener
            }
            Intent(requireContext(), NotifierService::class.java).run {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    requireActivity().startForegroundService(this)
                }else
                    requireActivity().startService(this)
            }
            viewModel.saveScheduledState(true, dose1, dose2)
            showMessage(getString(R.string.action_scheduled))
            binding.progress.visibility = View.GONE
        }

        binding.setupBattery.setOnClickListener {
            disableBatterySaveMode()
        }
        binding.setupAutoStart.setOnClickListener {
            AutoStartPermissionHelper.getInstance().getAutoStartPermission(requireContext())
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.setupBackground.visibility = View.VISIBLE
            binding.switchBackgroundData.visibility = View.VISIBLE
            binding.setupBackground.setOnClickListener {
                try {
                    val intent =
                        Intent(Settings.ACTION_IGNORE_BACKGROUND_DATA_RESTRICTIONS_SETTINGS)
                    startActivity(intent)
                } catch (
                    exception: Exception
                ) {
                    showMessage(getString(R.string.error))
                }
            }
        }
    }

    private fun stopWork() {
        binding.progress.visibility = View.VISIBLE
        requireActivity().stopService(Intent(requireContext(), NotifierService::class.java))
        viewModel.saveScheduledState(isScheduled = false, dose1 = false, false)
        binding.button.text = getString(R.string.action_notify)
        binding.progress.visibility = View.GONE
    }

    private fun showMessage(message: String, showAction: Boolean = false) {
        val sb = Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_SHORT
        )
        if (showAction)
            sb.setAction(getString(R.string.action_stop)) {
                stopWork()
            }
        sb.show()
    }


    private fun disableBatterySaveMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activity = requireActivity()
            val pm = activity.getSystemService(AppCompatActivity.POWER_SERVICE) as PowerManager?
            if (!pm!!.isIgnoringBatteryOptimizations(activity.packageName)) {
                if (Build.BRAND == "xiaomi") {
                    try {
                        val intent = Intent()
                        intent.component = ComponentName(
                            "com.miui.powerkeeper",
                            "com.miui.powerkeeper.ui.HiddenAppsConfigActivity"
                        )
                        intent.putExtra("package_name", activity.packageName)
                        intent.putExtra("package_label", "Vaccine Notifier")
                        startActivity(intent)
                    } catch (anfe: ActivityNotFoundException) {
                        showMessage(getString(R.string.error))
                    }
                } else {
                    try {
                        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                        startActivity(intent)
                    } catch (
                        exception: Exception
                    ) {
                        showMessage(getString(R.string.error))
                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}