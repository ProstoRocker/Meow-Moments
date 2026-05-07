package com.ilyadev.meowmoments.presentation.ui.calendar

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ilyadev.meowmoments.R
import com.ilyadev.meowmoments.databinding.DialogFactListBinding
import com.ilyadev.meowmoments.domain.model.CatFact

class FactListDialogFragment : DialogFragment() {

    private var _binding: DialogFactListBinding? = null
    private val binding get() = _binding!!

    private lateinit var factAdapter: FactListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFactListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.setCanceledOnTouchOutside(true)

        val date = arguments?.getString(ARG_DATE) ?: ""
        val facts = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelableArrayList(ARG_FACTS, CatFact::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelableArrayList(ARG_FACTS)
        } ?: emptyList<CatFact>()

        binding.tvDialogTitle.text = getString(R.string.facts_for_date, date)
        binding.btnDismiss.setOnClickListener { dismiss() }

        factAdapter = FactListAdapter()
        binding.rvFacts.apply {
            adapter = factAdapter
            layoutManager = LinearLayoutManager(context)
        }

        factAdapter.submitList(facts)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_DATE = "arg_date"
        private const val ARG_FACTS = "arg_facts"

        fun newInstance(facts: List<CatFact>, date: String): FactListDialogFragment {
            val args = Bundle().apply {
                putString(ARG_DATE, date)
                putParcelableArrayList(ARG_FACTS, ArrayList(facts))
            }
            return FactListDialogFragment().apply {
                arguments = args
            }
        }
    }
}