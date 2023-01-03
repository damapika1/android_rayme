package com.hogent.squads.view

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.hogent.squads.databinding.FragmentIncreaseTurnsBinding
import java.text.DecimalFormat

class IncreaseTurnsFragment() : DialogFragment() {

    interface IncreaseListener {
        fun onAcceptIncrease()
        fun onDeclineIncrease()
    }

    var listener:IncreaseListener? = null
        get() = field
        set(value) { field = value }

    private var turnsLeft=0
    private var newTurnsLeft=0
    private var price:String=""

    private var _binding: FragmentIncreaseTurnsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater

        _binding = FragmentIncreaseTurnsBinding.inflate(inflater)
        val view = binding.root
        builder.setView(view)

        bindContent()
        setUpButtons(builder)
        builder.setTitle("Extra beurten kopen")

        return builder.create()
    }

    fun setValues(turnsLeft:Int, newTurns:Int, price:Double) {
        val priceFormatter = DecimalFormat("€#.##")
        this.turnsLeft = turnsLeft
        this.newTurnsLeft = newTurns
        this.price = priceFormatter.format(price)
    }

    private fun setUpButtons(builder:AlertDialog.Builder) {
        builder.setPositiveButton("Confirm") { _, _ ->
            listener!!.onAcceptIncrease()
        }
        builder.setNegativeButton("Cancel") { _, _ ->
            listener!!.onDeclineIncrease()
        }
    }

    private fun bindContent() {
        _binding!!.turnsLeft.text = turnsLeft.toString()
        _binding!!.newTurnsLeft.text = newTurnsLeft.toString()
        _binding!!.price.text = price
    }
}
