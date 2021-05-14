package me.alphaone.vaccinenotifier

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import me.alphaone.vaccinenotifier.databinding.FragmentFirstBinding
import vaccinenotifier.data.scheduleWork
import vaccinenotifier.data.stopWork
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
                        viewModel.saveDistrictId(selectedDistrict)
                    }
                }
                else -> {

                }
            }
        }
        binding.button.setOnClickListener {
            scheduleWork(requireActivity().applicationContext)
            Snackbar.make(binding.root, "You will be notified once the vaccine are available", Snackbar.LENGTH_INDEFINITE)
                .setAction("Stop"){
                    stopWork(requireContext())
                }
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}