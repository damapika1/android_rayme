package com.hogent.squads.view.sessions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.hogent.squads.R
import com.hogent.squads.databinding.FragmentSessionViewBinding
import com.hogent.squads.model.network.rest.resources.JoinRequest
import com.hogent.squads.model.network.rest.resources.TraineesData
import com.hogent.squads.viewmodel.CalendarViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@AndroidEntryPoint
class SessionViewFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var sessionViewModel: CalendarViewModel
    private var _binding: FragmentSessionViewBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSessionViewBinding.inflate(inflater, container, false)
        val view = binding.root
        sessionViewModel = ViewModelProvider(requireActivity())[CalendarViewModel::class.java]
        //binding.calendarViewModel = sessionViewModel
        setView()
        return view
    }

    private fun setView() {
        val sessionId = requireArguments().getInt("sessionId")
        sessionViewModel.getSessionLiveData(sessionId).observe(viewLifecycleOwner) {
            Timber.tag("SESSION_VIEW").d("Loading session ${it.title}")
            val startDateTime: LocalDateTime = it.startsAt
            val endDateTime: LocalDateTime = it.endsAt
            val timeStart: String = startDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
            val timeEnd: String = endDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
            binding.backAppBar.title = it.title
            binding.date.text = startDateTime.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
            binding.time.text = String.format("%s - %s uur", timeStart, timeEnd)
            binding.desc.text = it.description
            binding.workoutType.text = it.workoutType
            if(it.workoutType == "Yoga") {
                binding.workoutIcon.setImageResource(R.mipmap.ic_yoga_dark_foreground)
            }

            val traineesAmount = it.trainees
            binding.trainees.text =  "${traineesAmount}/6 Deelnemers"

            val userId = sessionViewModel.getCurrentUserId()?.toInt()

            binding.joinButton.apply {
                val hasJoinedSession = it.userFK != null
                if (hasJoinedSession) {
                    this.text = resources.getText(R.string.leave_session)
                } else {
                    this.text = resources.getText(R.string.join_button)
                }

                setOnClickListener {
                    if (hasJoinedSession) {
                        sessionViewModel.leaveSession(
                            JoinRequest(
                                sessionId = sessionId,
                                TraineesData(traineeId = userId!!)
                            )
                        )
                        Snackbar.make(
                            it, "You have left the session!",
                            Snackbar.LENGTH_LONG
                        ).show()
                        navController.popBackStack()
                    } else {
                        sessionViewModel.joinSession(
                            JoinRequest(
                                sessionId = sessionId,
                                TraineesData(traineeId = userId!!)
                            )
                        )
                        Snackbar.make(
                            it, "You have joined the session!",
                            Snackbar.LENGTH_LONG).show()
                        navController.popBackStack()
                    }
                }
            }
        }

        binding.backAppBar.setNavigationOnClickListener {
                navController.popBackStack()
            }
        }
}
