package com.hogent.squads.view.userhome

import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.hogent.squads.R
import com.hogent.squads.databinding.FragmentEditUserBinding
import com.hogent.squads.databinding.FragmentSessionViewBinding
import com.hogent.squads.model.domain.User
import com.hogent.squads.viewmodel.UserViewModel
import java.security.InvalidParameterException


class EditUserFragment : Fragment() {

    private lateinit var navController: NavController
    private var _binding: FragmentEditUserBinding? = null
    private val binding get() = _binding!!
    private lateinit var userViewModel: UserViewModel
    private var activeUser:User?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditUserBinding.inflate(inflater, container, false)
        val view = binding.root
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        userViewModel.resetEditStatus()
        setObeservation()
        setEditObservation()
        setNavigation()
        setSaveAction()
        return view
    }

    private fun setEditObservation() {
        userViewModel.editStatus.observe(viewLifecycleOwner) {
            if(it == "loading") {
                binding.saveButton.isEnabled = false
                binding.saveButton.setBackgroundColor(resources.getColor(R.color.backgroundColor))
            } else if(it == "updated") {
                navController.popBackStack()
            }
        }
    }

    private fun setObeservation() {
        userViewModel.activeUser.observe(viewLifecycleOwner) {
            if(it != null) {
                binding.firstNameInput.text =
                    Editable.Factory.getInstance().newEditable(it!!.firstName)
                binding.lastNameInput.text = Editable.Factory.getInstance().newEditable(it.lastName)
                binding.emailInput.text = Editable.Factory.getInstance().newEditable(it.email)
                binding.phoneInput.text = Editable.Factory.getInstance().newEditable(it.phoneNumber)
                binding.addressInput.text = Editable.Factory.getInstance().newEditable(it.address)
                activeUser = it
            }
        }
    }

    private fun setNavigation() {
        binding.backAppBar.setNavigationOnClickListener {
            navController.popBackStack()
        }
    }

    private fun setSaveAction() {
        binding.saveButton.setOnClickListener {
            if(activeUser == null) {
                return@setOnClickListener
            }
            try {
                var updatedUser = User(
                    activeUser!!.userId,
                    ensureNotNullString(binding.emailInput.text),
                    0,
                    ensureNotNullString(binding.firstNameInput.text),
                    ensureNotNullString(binding.lastNameInput.text),
                    ensureNotNullString(binding.addressInput.text),
                    ensureNotNullString(binding.phoneInput.text)
                )
                userViewModel.editUser(updatedUser)
            } catch (e:InvalidParameterException) {
                Snackbar.make(
                    it, "Geen lege velden toegelaten!",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun ensureNotNullString(text:Editable):String {
        val value = text.toString()
        if(!(value.isEmpty() || value.isBlank())){
            return value
        } else {
            throw InvalidParameterException()
        }
    }
}