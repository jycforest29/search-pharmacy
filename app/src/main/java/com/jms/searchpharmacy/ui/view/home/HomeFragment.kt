package com.jms.searchpharmacy.ui.view.home

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.jms.searchpharmacy.R
import com.jms.searchpharmacy.databinding.FragmentHomeBinding
import com.jms.searchpharmacy.ui.view.MainActivity
import com.jms.searchpharmacy.ui.viewmodel.MainViewModel
import com.jms.searchpharmacy.util.Constants.PERMISSION_REQUEST_CODE
import com.naver.maps.geometry.LatLng
import java.util.jar.Manifest


class HomeFragment : Fragment() {

    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel : MainViewModel by lazy {
        (activity as MainActivity).mainViewModel
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.homeTextInfoSingleLine.isSelected = true
        binding.searchSubway.setOnClickListener{
            val action = HomeFragmentDirections.actionHomeFragmentToSelectSubwayFragment()
            findNavController().navigate(action)
        }
        binding.searchAddr.setOnClickListener{
            val action = HomeFragmentDirections.actionHomeFragmentToBriefFragment(null)
            findNavController().navigate(action)
        }

        binding.searchMyPlace.setOnClickListener{
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                onCheckPermission()
            } else {
                val locationManager: LocationManager =
                    requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val currentLocation: Location =
                    locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!

                onCheckInSeoul(currentLocation)
            }
        }




    }

    //현재 위치가 서울인지 파악
    private fun onCheckInSeoul(currentLocation: Location) {
        viewModel.convertCoordsToAddr(LatLng(currentLocation))
        viewModel.regionNameLiveData.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(),it,Toast.LENGTH_SHORT).show()
        }
    }


    private fun onCheckPermission() {
        if(ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(requireContext(), "위치 권한을 허용해야 사용 가능합니다", Toast.LENGTH_SHORT).show()

                ActivityCompat.requestPermissions(requireActivity(),arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION),PERMISSION_REQUEST_CODE)


            } else {

                ActivityCompat.requestPermissions(requireActivity(),arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION),PERMISSION_REQUEST_CODE)

            }

        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                    Toast.makeText(requireContext(), "위치 권한이 설정되었습니다", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "위치 권한이 취소되었습니다", Toast.LENGTH_SHORT).show()
                }
            }
            else -> { }
        }

    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}