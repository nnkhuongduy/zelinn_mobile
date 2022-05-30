package com.example.zelinn.ui.board.card

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zelinn.R
import com.example.zelinn.ZelinnApp
import com.example.zelinn.classes.RetrofitInstance
import com.example.zelinn.databinding.FragmentCreateCardBinding
import com.example.zelinn.interfaces.CreateCardBody
import com.example.zelinn.ui.board.BoardViewModel
import com.example.zelinn.ui.board.card.participant.ParticipantFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateCardFragment : Fragment() {
    private lateinit var cardNameInput: TextInputEditText
    private lateinit var descriptionInput: TextInputEditText
    private lateinit var closeBtn: Button
    private lateinit var createBtn: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var participantBtn: Button
    private lateinit var progressView: ProgressBar

    private lateinit var startDateBtn: LinearLayout
    private lateinit var dueDateBtn: LinearLayout
    private lateinit var startTimeBtn: LinearLayout
    private lateinit var dueTimeBtn: LinearLayout

    private lateinit var startDateText: TextView
    private lateinit var dueDateText: TextView
    private lateinit var startTimeText: TextView
    private lateinit var dueTimeText: TextView

    private lateinit var startDateImage: ImageView
    private lateinit var dueDateImage: ImageView
    private lateinit var startTimeImage: ImageView
    private lateinit var dueTimeImage: ImageView

    private lateinit var startDateImage2: ImageView
    private lateinit var dueDateImage2: ImageView
    private lateinit var startTimeImage2: ImageView
    private lateinit var dueTimeImage2: ImageView

    private var _binding: FragmentCreateCardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val boardModel: BoardViewModel by activityViewModels()
    private val cardModel: CardViewModel by activityViewModels()
    private val adapter = CardParticipantAdapter()
    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            closeParticipantFragment()
        }
    }

    private var valid = false
    private var loading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateCardBinding.inflate(inflater, container, false)
        val root = binding.root

        findViews()
        populate()
        watch()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun findViews() {
        closeBtn = binding.root.findViewById(R.id.create_card_close_btn)
        createBtn = binding.root.findViewById(R.id.create_card_create_btn)
        cardNameInput = binding.root.findViewById(R.id.create_card_name_input)
        descriptionInput = binding.root.findViewById(R.id.create_card_description_input)
        recyclerView = binding.root.findViewById(R.id.create_card_rv)
        participantBtn = binding.root.findViewById(R.id.create_card_participant_btn)
        progressView = binding.root.findViewById(R.id.create_card_progress)

        startDateText = binding.root.findViewById(R.id.create_card_start_date_text)
        dueDateText = binding.root.findViewById(R.id.create_card_due_date_text)
        dueTimeText = binding.root.findViewById(R.id.create_card_due_time_text)
        startTimeText = binding.root.findViewById(R.id.create_card_start_time_text)

        startDateBtn = binding.root.findViewById(R.id.create_card_start_date_btn)
        dueDateBtn = binding.root.findViewById(R.id.create_card_due_date_btn)
        startTimeBtn = binding.root.findViewById(R.id.create_card_start_time_btn)
        dueTimeBtn = binding.root.findViewById(R.id.create_card_due_time_btn)

        startDateImage = binding.root.findViewById(R.id.create_card_start_date_image)
        dueDateImage = binding.root.findViewById(R.id.create_card_due_date_image)
        startTimeImage = binding.root.findViewById(R.id.create_card_start_time_image)
        dueTimeImage = binding.root.findViewById(R.id.create_card_due_time_image)

        startDateImage2 = binding.root.findViewById(R.id.create_card_start_date_image_2)
        dueDateImage2 = binding.root.findViewById(R.id.create_card_due_date_image_2)
        startTimeImage2 = binding.root.findViewById(R.id.create_card_start_time_image_2)
        dueTimeImage2 = binding.root.findViewById(R.id.create_card_due_time_image_2)
    }

    private fun populate() {
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter

        adapter.apply {
            submitList(cardModel.participants.value)
        }
    }

    private fun watch() {
        closeBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }
        createBtn.setOnClickListener {
            createCard()
        }
        cardNameInput.doOnTextChanged { text, start, before, count ->
            cardModel.setName(text.toString())
        }
        descriptionInput.doOnTextChanged { text, start, before, count ->
            cardModel.setDescription(text.toString())
        }

        startDateBtn.setOnClickListener {
            if (!loading) {
                val fragment = DatePickerFragment()

                fragment.setOnDateSetListener { year, month, date ->
                    cardModel.setStartDate(year, month + 1, date)
                }
                fragment.show(parentFragmentManager, "START_DATE_PICKER")
            }
        }
        dueDateBtn.setOnClickListener {
            if (!loading) {
                val fragment = DatePickerFragment()

                fragment.setOnDateSetListener { year, month, date ->
                    cardModel.setDueDate(year, month + 1, date)
                }
                fragment.show(parentFragmentManager, "DUE_DATE_PICKER")
            }
        }
        startTimeBtn.setOnClickListener {
            if (!loading) {
                val fragment = TimePickerFragment()

                fragment.setOnTimeSetListener { hour, minute ->
                    cardModel.setStartTime(hour, minute)
                }
                fragment.show(parentFragmentManager, "START_TIME_PICKER")
            }
        }
        dueTimeBtn.setOnClickListener {
            if (!loading) {
                val fragment = TimePickerFragment()

                fragment.setOnTimeSetListener { hour, minute ->
                    cardModel.setDueTime(hour, minute)
                }
                fragment.show(parentFragmentManager, "DUE_TIME_PICKER")
            }
        }
        participantBtn.setOnClickListener {
            createParticipantFragment()
        }
        cardModel.changed.observe(viewLifecycleOwner) {
            checkValid()
        }
        cardModel.start.observe(viewLifecycleOwner) {
            var dateText = ""
            var timeText = ""
            if (it.isDateValid())
                dateText =
                    "${it.date.toString().padStart(2, '0')}/${it.month.toString().padStart(2, '0')}"

            startDateText.text = dateText
            startDateImage.imageTintList = ContextCompat.getColorStateList(
                requireContext(),
                if (it.isDateValid()) R.color.primary else R.color.background_light
            )
            startDateImage2.imageTintList = ContextCompat.getColorStateList(
                requireContext(),
                if (it.isDateValid()) R.color.primary else R.color.background_light
            )

            if (it.isTimeValid())
                timeText =
                    "${it.hour.toString().padStart(2, '0')}:${
                        it.minute.toString().padStart(2, '0')
                    }"

            startTimeText.text = timeText
            startTimeImage.imageTintList = ContextCompat.getColorStateList(
                requireContext(),
                if (it.isTimeValid()) R.color.primary else R.color.background_light
            )
            startTimeImage2.imageTintList = ContextCompat.getColorStateList(
                requireContext(),
                if (it.isTimeValid()) R.color.primary else R.color.background_light
            )
        }
        cardModel.due.observe(viewLifecycleOwner) {
            var dateText = ""
            var timeText = ""

            if (it.isDateValid())
                dateText =
                    "${it.date.toString().padStart(2, '0')}/${it.month.toString().padStart(2, '0')}"

            dueDateText.text = dateText
            dueDateImage.imageTintList = ContextCompat.getColorStateList(
                requireContext(),
                if (it.isDateValid()) R.color.primary else R.color.background_light
            )
            dueDateImage2.imageTintList = ContextCompat.getColorStateList(
                requireContext(),
                if (it.isDateValid()) R.color.white else R.color.background_light
            )

            if (it.isTimeValid())
                timeText =
                    "${it.hour.toString().padStart(2, '0')}:${
                        it.minute.toString().padStart(2, '0')
                    }"

            dueTimeText.text = timeText
            dueTimeImage.imageTintList = ContextCompat.getColorStateList(
                requireContext(),
                if (it.isTimeValid()) R.color.primary else R.color.background_light
            )
            dueTimeImage2.imageTintList = ContextCompat.getColorStateList(
                requireContext(),
                if (it.isTimeValid()) R.color.white else R.color.background_light
            )
        }
        cardModel.participants.observe(viewLifecycleOwner) {
            adapter.notifyDataSetChanged()
        }
    }

    private fun checkValid() {
        val name = cardModel.name.value
        val description = cardModel.description.value
        val start = cardModel.start.value
        val due = cardModel.due.value
        val participants = cardModel.participants.value

        valid =
            !name.isNullOrEmpty() && !description.isNullOrEmpty() && start != null && due != null && start.isValid() && due.isValid() && !participants.isNullOrEmpty()

        createBtn.isEnabled = valid
    }

    private fun createParticipantFragment() {
        val activity = requireActivity()
        val fragment = ParticipantFragment()

        activity.onBackPressedDispatcher.addCallback(backPressedCallback)
        activity.supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_bottom,
                R.anim.slide_out_bottom,
                R.anim.slide_in_bottom,
                R.anim.slide_out_bottom
            )
            .replace(
                R.id.board_activity_fragment_container,
                fragment,
                "BOARD_CARD_SELECT_PARTICIPANT"
            )
            .addToBackStack("BOARD_CARD_SELECT_PARTICIPANT")
            .commit()
    }

    private fun closeParticipantFragment() {
        val activity = requireActivity()

        backPressedCallback.remove()
        activity.supportFragmentManager.popBackStack(
            "BOARD_CARD_SELECT_PARTICIPANT",
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }

    private fun toggleState() {
        loading = !loading

        closeBtn.isEnabled = !loading
        createBtn.isEnabled = !loading
        cardNameInput.isEnabled = !loading
        descriptionInput.isEnabled = !loading
        participantBtn.isEnabled = !loading

        createBtn.text = if (loading) "" else getString(R.string.create_action)
        progressView.visibility = if (loading) View.VISIBLE else View.GONE
    }

    private fun showSuccessDialog() {
        val builder =
            MaterialAlertDialogBuilder(binding.root.context).setView(R.layout.dialog_success)
        val dialog = builder.show()

        val titleView = dialog.findViewById<TextView>(R.id.dialog_success_title)!!

        titleView.text = getString(R.string.create_card_success)

        dialog.setOnDismissListener {
            requireActivity().onBackPressed()
        }
    }

    private fun showErrorDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext()).setView(R.layout.dialog_error)
        val dialog = builder.show()

        val titleView = dialog.findViewById<TextView>(R.id.dialog_error_title)!!

        titleView.text = getString(R.string.create_card_failed)
    }

    private fun createCard() {
        val body = CreateCardBody(
            cardModel.list.value!!,
            cardModel.name.value!!,
            cardModel.start.value?.toISO(),
            cardModel.due.value?.toISO(),
            cardModel.participants.value?.map { it.id },
            cardModel.description.value!!
        )
        toggleState()

        RetrofitInstance.retrofit.createCard(body).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                toggleState()

                if (response.isSuccessful) {
                    showSuccessDialog()
                    ZelinnApp.prefs.push(getString(R.string.preference_board_flag), true)
                    boardModel.resetList()
                } else showErrorDialog()
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                toggleState()
                showErrorDialog()
            }
        })
    }
}