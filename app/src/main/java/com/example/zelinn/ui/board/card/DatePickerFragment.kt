package com.example.zelinn.ui.board.card

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import android.R as androidR
import java.util.*

class DatePickerFragment: DialogFragment(), DatePickerDialog.OnDateSetListener {
    private var onDateSetListener: ((Int, Int, Int) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(requireActivity(), androidR.style.Theme_DeviceDefault_Dialog_Alert, this, year, month, day)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
        onDateSetListener?.invoke(year, month, day)
    }

    fun setOnDateSetListener(listener: ((Int, Int, Int) -> Unit)?) {
        onDateSetListener = listener
    }
}