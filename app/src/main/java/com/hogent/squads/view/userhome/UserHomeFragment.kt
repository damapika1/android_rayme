package com.hogent.squads.view.userhome

import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.drawable.ColorDrawable
import android.opengl.Visibility
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
import com.hogent.squads.databinding.FragmentUserHomeBinding
import com.hogent.squads.model.domain.Subscription
import com.hogent.squads.model.domain.User
import com.hogent.squads.view.ExtendSubscriptionFragment
import com.hogent.squads.view.IncreaseTurnsFragment
import com.hogent.squads.viewmodel.CalendarViewModel
import com.hogent.squads.viewmodel.UserViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.net.ConnectException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


@AndroidEntryPoint
class UserHomeFragment : Fragment(), ExtendSubscriptionFragment.ExtendListener, IncreaseTurnsFragment.IncreaseListener {

    private lateinit var navController: NavController
    private lateinit var userViewModel: UserViewModel
    private lateinit var calendarViewModel: CalendarViewModel
    private var _binding: FragmentUserHomeBinding? = null

    private var turns=-1
    private var subscription : Subscription? = null
    private var user: User? = null

    private var hasActiveSubscription:Boolean = false;

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        calendarViewModel = ViewModelProvider(requireActivity())[CalendarViewModel::class.java]
        observeUser()
        observeSubscription()
        observeTurns()
        setUpNavigation()

        binding.logoutButton.setOnClickListener {
            userViewModel.deleteCurrentUser()
        }

        return view
    }

    private fun setUpNavigation() {
        binding.edit.setOnClickListener {
            navController.navigate(UserHomeFragmentDirections.actionUserHomeFragmentToEditUserFragment())
        }
    }

    private fun observeUser() {
        userViewModel.activeUser.observe(viewLifecycleOwner) { foundUser ->
            Timber.tag("USER_HOME_FRAGMENT").d("Got a new user.")
            Timber.tag("USER_HOME_FRAGMENT").d("user: $foundUser")
            if (foundUser != null) {
                user = foundUser
                userViewModel.getUserInfo(foundUser)
                Picasso
                    .get()
                    .load("https://www.squads.training/images/hero2.jpg")
                    .fit()
                    .into(binding.bannerView)
                Picasso.get().load(foundUser.imageUrl).into(binding.imageviewAccountProfile)
                binding.firstNameInput.text = foundUser.firstName
            }
        }
    }

    private fun observeSubscription() {
        userViewModel.subscription.observe(viewLifecycleOwner) { sub ->
            if(sub != null) {
                subscription = sub
                var days = ChronoUnit.DAYS.between(LocalDate.now(), sub.validTill).toInt()
                if(days>0) {
                    hasActiveSubscription = true
                } else {
                    days = 0
                    hasActiveSubscription = false
                }
                var text = resources.getQuantityString(R.plurals.numberOfDays, days, days)
                _binding!!.expiryDate.text = text
            } else {
                hasActiveSubscription = false
                _binding!!.expiryDate.text = ""
            }
            updateSate()
        }
    }

    private fun observeTurns() {
        userViewModel.turns.observe(viewLifecycleOwner) {
            turns = it
            val text = resources.getQuantityString(R.plurals.numberOfTurns, it, it)
            _binding!!.turnsleft.text = text
            updateSate()
        }
    }

    private fun showSubExtension() {
        val dialog = createSubscriptionDialog()
        dialog.listener = this
        dialog.show(requireActivity().supportFragmentManager, "EXTEND_SUB")
    }

    private fun showCardPurchase() {
        val dialog = createTurnsDialog()
        dialog.listener = this
        dialog.show(requireActivity().supportFragmentManager, "INCREASE_TURNS")
    }

    fun createSubscriptionDialog() : ExtendSubscriptionFragment {
        val fragment = ExtendSubscriptionFragment()
        var expiry:LocalDate?=null
        var newExpiry:LocalDate
        var price:Double
        val now = LocalDate.now()
        if(subscription != null) {
            expiry = subscription!!.validTill
            newExpiry = if(expiry.isAfter(now)) expiry.plusMonths(1) else now.plusMonths(1)
        } else {
            Timber.tag("USER_HOME_FRAGMENT").d("Date: ${now.format(DateTimeFormatter.ISO_LOCAL_DATE)}")
            newExpiry = now.plusMonths(1)
        }

        if(user != null) {
            price = user!!.pricePlan.subscriptionPrice
        } else {
            price = 0.0
        }

        if(price == -1.0) {
            throw RuntimeException("Race condition, price was not received yet.")
        }
        fragment.setValues(expiry, newExpiry, price)
        return fragment
    }

    fun createTurnsDialog() : IncreaseTurnsFragment {
        val fragment = IncreaseTurnsFragment()
        var turnsLeft=-1
        var newTurnsLeft=-1
        var price:Double

        if(turns == -1) {
            throw RuntimeException("Not good..")
        }

        turnsLeft = turns
        newTurnsLeft = turnsLeft + 10

        if(user != null) {
            price = user!!.pricePlan.turnPrice
        } else {
            price = 0.0
        }

        if(price == -1.0) {
            throw RuntimeException("Race condition, price was not received yet.")
        }
        fragment.setValues(turnsLeft, newTurnsLeft, price!!)
        return fragment
    }

    override fun onAcceptExtension() {
        userViewModel.extendSubscription(user!!)
    }

    override fun onDeclineExtension() {
        Timber.tag("USER_HOME_FRAGMENT").d("Cancelled")
    }

    override fun onAcceptIncrease() {
        userViewModel.increaseTurns(user!!)
    }

    override fun onDeclineIncrease() {
        Timber.tag("USER_HOME_FRAGMENT").d("Cancelled")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun updateSate() {
        if(hasActiveSubscription) {
            darkenTurncards()
            lightenSubscription()
        } else {
            darkenSubscription()
            lightenTurncards()
        }
    }

    fun darkenSubscription() {
        val color = Color.parseColor("#66000000")
        val drawable = ColorDrawable(color)
        _binding!!.subscriptions.foreground = drawable
        _binding!!.activateButton.visibility = View.VISIBLE
        _binding!!.activateButton.setOnClickListener {
            showSubExtension()
        }
        _binding!!.subscriptions.setOnClickListener {}
    }

    fun lightenSubscription() {
        _binding!!.subscriptions.foreground = null
        _binding!!.activateButton.visibility = View.INVISIBLE
        _binding!!.activateButton.setTextColor(Color.parseColor("#ffffff"))
        _binding!!.subscriptions.setOnClickListener {
            showSubExtension()
        }
    }

    fun darkenTurncards() {
        val color = Color.parseColor("#66000000")
        val drawable = ColorDrawable(color)
        _binding!!.turncards.foreground = drawable
        _binding!!.turncards.setOnClickListener {}
    }

    fun lightenTurncards() {
        _binding!!.turncards.foreground = null
        _binding!!.turncards.setOnClickListener {
            showCardPurchase()
        }
    }
}
