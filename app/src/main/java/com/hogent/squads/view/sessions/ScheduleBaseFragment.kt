package com.hogent.squads.view.sessions

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.hogent.squads.R
import com.hogent.squads.databinding.FragmentScheduleBaseBinding
import com.hogent.squads.model.domain.Session
import com.hogent.squads.viewmodel.CalendarViewModel
import com.hogent.squads.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.time.Duration
import java.time.LocalDateTime

@AndroidEntryPoint
class ScheduleBaseFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var sessionViewModel: CalendarViewModel
    private lateinit var userViewModel: UserViewModel

    private var _binding:FragmentScheduleBaseBinding? = null
    private val binding get() = _binding!!
    private var timer:CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScheduleBaseBinding.inflate(inflater, container, false)
        val view = binding.root
        sessionViewModel = ViewModelProvider(requireActivity())[CalendarViewModel::class.java]
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]

        userViewModel.activeUser.observe(viewLifecycleOwner) {
            if (it != null) {
                sessionViewModel.updateSessions()
                setUpObserveLoading()
                initCountdown()
                initNavigation()
            }
        }

        return view
    }

    private fun setUpObserveLoading() {
        sessionViewModel.loading.observe(viewLifecycleOwner) {
            if(it) {
                setLoadingState()
            } else {
                setLoadedState()
            }
        }
    }

    private fun setLoadingState() {
        binding.countDown.visibility = View.INVISIBLE
        binding.upcomingSessions.visibility = View.INVISIBLE
        binding.plannedSessions.visibility = View.INVISIBLE
        binding.progressBar.visibility = View.VISIBLE
    }
    private fun setLoadedState() {
        binding.countDown.visibility = View.VISIBLE
        binding.upcomingSessions.visibility = View.VISIBLE
        binding.plannedSessions.visibility = View.VISIBLE
        binding.progressBar.visibility = View.INVISIBLE
    }

    private fun initCountdown() {
            sessionViewModel.userSessions
                .observe(viewLifecycleOwner) {
                    if(it.isEmpty())
                        sessionViewModel.setLoading()
                    val first = getFirstSessionStart(it)
                    if(first == null) {
                        val text = resources.getText(R.string.nothing_planned)
                        binding.countDown.text = text
                    } else {
                        timer?.cancel()
                        val time = Duration.between(LocalDateTime.now(), first.startsAt)
                        timer = object : CountDownTimer(time.toMillis(), 1000) {
                            override fun onTick(millisUntilFinished: Long) {
                                val hours = millisUntilFinished/3600000
                                val remainderHours = millisUntilFinished % 3600000
                                val minutes = remainderHours / 60000
                                val remainderMinutes = remainderHours % 60000
                                val seconds = remainderMinutes / 1000
                                val text = "${hours}:${minutes}:${seconds}"
                                binding.countDown.text = text
                            }

                            override fun onFinish() {
                                binding.countDown.text = resources.getText(R.string.start)
                            }
                        }.start()
                    }
                }
    }

    private fun initNavigation() {
        binding.plannedSessions.setOnClickListener {
            navController.navigate(ScheduleBaseFragmentDirections.actionScheduleBaseFragmentToCalendarFragment(true))
        }
        binding.upcomingSessions.setOnClickListener {
            navController.navigate(ScheduleBaseFragmentDirections.actionScheduleBaseFragmentToCalendarFragment(false))
        }
    }

    private fun getFirstSessionStart(sessions:List<Session>) : Session? {
        val now = LocalDateTime.now()
        var first:Session?=null
        sessions.forEach {
            if(it.startsAt.isBefore(now))
                return@forEach
            if(first == null || it.startsAt.isBefore(first!!.startsAt)) {
                first = it
            }
        }
        return first
    }
}