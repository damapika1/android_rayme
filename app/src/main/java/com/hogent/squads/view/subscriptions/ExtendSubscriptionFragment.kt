package com.hogent.squads.view.subscriptions

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.hogent.squads.databinding.FragmentExtendSubscriptionBinding
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ExtendSubscriptionFragment() : DialogFragment() {

    interface ExtendListener {
        fun onAcceptExtension()
        fun onDeclineExtension()
    }

    var listener: ExtendListener? = null
        get() = field
        set(value) { field = value }

    private var expiry:String=""
    private var newExpiry:String=""
    private var price:String=""

    private var _binding: FragmentExtendSubscriptionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater

        _binding = FragmentExtendSubscriptionBinding.inflate(inflater)
        val view = binding.root
        builder.setView(view)

        bindContent()
        setUpButtons(builder)
        if(expiry.isNotBlank()) {
            builder.setTitle("Extend Subscription")
        } else {
            builder.setTitle("Activate New Subscription")
        }

        return builder.create()
    }

    fun setValues(expiry:LocalDate?, newExpiry:LocalDate, price:Double) {
        val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val priceFormatter = DecimalFormat("€#.##")
        if(expiry != null) {
            this.expiry = expiry.format(dateFormatter)
        }
        this.newExpiry = newExpiry.format(dateFormatter)
        this.price = priceFormatter.format(price)
    }

    private fun setUpButtons(builder:AlertDialog.Builder) {
        builder.setPositiveButton("Confirm") { _, _ ->
            listener!!.onAcceptExtension()
        }
        builder.setNegativeButton("Cancel") { _, _ ->
            listener!!.onDeclineExtension()
        }
    }

    private fun bindContent() {
        if(expiry.isNotBlank()) {
            bindExtendContent()
        } else {
            bindNewContent()
        }
    }

    private fun bindExtendContent() {
        _binding!!.expiryDate.visibility = View.VISIBLE
        _binding!!.expiryDateLabel.visibility = View.VISIBLE
        _binding!!.expiryDate.text = expiry
        _binding!!.newExpiryDate.text = newExpiry
        _binding!!.price.text = price
    }

    private fun bindNewContent() {
        _binding!!.expiryDate.visibility = View.INVISIBLE
        _binding!!.expiryDateLabel.visibility = View.INVISIBLE
        _binding!!.newExpiryDate.text = newExpiry
        _binding!!.price.text = price
    }
}
