package com.hogent.squads.view.sessions

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hogent.squads.R
import com.hogent.squads.databinding.SessionCardBinding
import com.hogent.squads.model.domain.Constants
import com.hogent.squads.model.domain.Session
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class CalendarAdapter(private val clickListener: SessionsListener) :ListAdapter<Session, SessionViewHolder>(SessionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        return SessionViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        val item= getItem(position)
        holder.bind(clickListener,item)
    }

}
class SessionViewHolder(val binding: SessionCardBinding): RecyclerView.ViewHolder(binding.root){

    fun bind(clickListener: SessionsListener, session: Session) {
        val startDateTime: LocalDateTime = session.startsAt
        val endDateTime: LocalDateTime = session.endsAt
        val timeStart: String =startDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
        val timeEnd: String = endDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))

        binding.date.text=startDateTime.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
        binding.time.text=String.format("%s - %s uur",timeStart,timeEnd)
        binding.workoutType.text = session.workoutType
        binding.session = session
        binding.clickListener = clickListener
        if(session.workoutType == "Yoga") {
            binding.icon.setImageResource(R.mipmap.ic_yoga_white_foreground)
        }

        if(session.trainees>= Constants.MAX_TRAINEES_AMOUNT){
            binding.root.setBackgroundColor(Color.RED)
        }
        val traineesAmount = session.trainees
        binding.hasJoined.text = "${traineesAmount}/6 Deelnemers"
        binding.executePendingBindings()
    }
    //this way the viewHolder knows how to inflate.
    //better than having this in the adapter.
    companion object {

        fun from(parent: ViewGroup): SessionViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = SessionCardBinding.inflate(layoutInflater, parent, false)
            return SessionViewHolder(binding)
        }
    }
}
class SessionDiffCallback:DiffUtil.ItemCallback<Session>(){
    override fun areItemsTheSame(oldItem: Session, newItem: Session): Boolean {
        return oldItem.sessionId==newItem.sessionId
    }

    override fun areContentsTheSame(oldItem: Session, newItem: Session): Boolean {
        return oldItem==newItem
    }
}
class SessionsListener(val clickListener: (session:Session)->Unit){

    fun onClick(session:Session) = clickListener(session)
}
