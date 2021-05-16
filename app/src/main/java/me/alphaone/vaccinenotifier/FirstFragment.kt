package me.alphaone.vaccinenotifier

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import me.alphaone.vaccinenotifier.databinding.FragmentFirstBinding
import vaccinenotifier.data.scheduleWork
import vaccinenotifier.data.stopWork
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
    private var dose1: Boolean = false
    private var dose2: Boolean = false

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
        viewModel.isScheduled.observe(viewLifecycleOwner){
            it?.let {
                if(it.isScheduled)
                {
                    binding.button.text = getString(R.string.action_update)
                    binding.cardViewScheduler.visibility = View.VISIBLE
                    binding.scheduleText.text = getString(R.string.schedule_text,it.district.name)
                    binding.switchDose1.isChecked = it.dose1
                    binding.switchDose2.isChecked = it.dose2
                    binding.stop.setOnClickListener {
                        stopWork()
                    }
                }
                else
                    binding.cardViewScheduler.visibility = View.GONE
            }
        }
        binding.button.setOnClickListener {
            binding.progress.visibility = View.VISIBLE
            if(selectedDistrict==-1)
            {
                Toast.makeText(requireContext(),"Please select district.",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            scheduleWork(requireActivity().applicationContext)
            viewModel.saveScheduledState(true, dose1,dose2)
            Snackbar.make(binding.root, "You will be notified whenever the vaccine slots are available", Snackbar.LENGTH_SHORT)
                .setAction("Stop"){
                    stopWork()
                }
                .show()
            binding.progress.visibility = View.GONE
        }
        binding.dismiss.setOnClickListener {
            binding.instructions.visibility = View.GONE
        }
        //AutoStartPermissionHelper.getInstance().getAutoStartPermission(requi
    // reContext())
    }

    private fun stopWork() {
        binding.progress.visibility = View.VISIBLE
        stopWork(requireContext())
        viewModel.saveScheduledState(false,dose1,dose2)
        binding.button.text = getString(R.string.action_notify)
        binding.progress.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}