package com.example.zelinn.ui.board.card

import android.R
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class TimePickerFragment: DialogFragment(), TimePickerDialog.OnTimeSetListener {
    private var onTimeSetListener: ((Int, Int) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        return TimePickerDialog(requireActivity(), R.style.Theme_DeviceDefault_Dialog_Alert, this, hour, minute, DateFormat.is24HourFormat(requireContext()))
    }

    override fun onTimeSet(view: TimePicker?, hour: Int, minute: Int) {
        onTimeSetListener?.invoke(hour, minute)
    }

    fun setOnTimeSetListener(listener: ((Int, Int) -> Unit)?) {
        onTimeSetListener = listener
    }
}