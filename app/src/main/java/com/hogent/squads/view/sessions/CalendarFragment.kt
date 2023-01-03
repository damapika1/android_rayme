package com.hogent.squads.view.sessions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.hogent.squads.R
import com.hogent.squads.databinding.FragmentCalendarBinding
import com.hogent.squads.model.domain.Constants
import com.hogent.squads.model.domain.Session
import com.hogent.squads.model.domain.User
import com.hogent.squads.viewmodel.CalendarViewModel
import com.hogent.squads.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class CalendarFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var sessionViewModel: CalendarViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var sessionsAdapter: CalendarAdapter

    private var _binding: FragmentCalendarBinding? = null

    private val binding get() = _binding!!
    private var userSessionsOnly:Boolean=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val view = binding.root
        sessionViewModel = ViewModelProvider(requireActivity())[CalendarViewModel::class.java]
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        userSessionsOnly = requireArguments().getBoolean("userSessionsOnly")

        initAppbar()
        userViewModel.activeUser.observe(viewLifecycleOwner) {
            if (it != null) {
                initSessionView()
            }
        }

        return view
    }

    fun initAppbar() {
        if(userSessionsOnly) {
            binding.backAppBar.title = String.format(resources.getText(R.string.sessions).toString(), "Mijn")
        } else {
            binding.backAppBar.title = String.format(resources.getText(R.string.sessions).toString(), "")
        }

        binding.backAppBar.setNavigationOnClickListener {
            navController.popBackStack()
        }
    }

    fun initSessionView() {
        binding.sessions.apply {
            sessionsAdapter = CalendarAdapter(SessionsListener { session ->
                if(userSessionsOnly) {
                    setUpNavigationJoinedSessions(session)
                } else {
                    setUpNavigationAllSessions(session)
                }
            })

            Timber.i("fetchSessions()")

            observeSessions()
            adapter = sessionsAdapter
        }
    }

    private fun setUpNavigationJoinedSessions(session: Session) {
        navController.navigate(CalendarFragmentDirections.actionCalendarFragmentToSessionViewFragment(session.sessionId))
    }

    private fun setUpNavigationAllSessions(session: Session) {
        if (session.trainees >= Constants.MAX_TRAINEES_AMOUNT) {
            Snackbar.make(
                binding.root, "De sessie is vol!",
                Snackbar.LENGTH_LONG
            ).show()
        } else {
            navController.navigate(
                CalendarFragmentDirections.actionCalendarFragmentToSessionViewFragment(session.sessionId)
            )
        }
    }

    private fun observeSessions() {
        if(userSessionsOnly) {
            sessionViewModel.userSessions
                .observe(viewLifecycleOwner) {
                    sessionsAdapter.submitList(it?.toMutableList())
                }
        } else {
            sessionViewModel.allSessions
                .observe(viewLifecycleOwner) {
                    sessionsAdapter.submitList(it?.toMutableList())
                }
        }
    }
}
