package com.hogent.squads.view.stats

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.hogent.squads.databinding.FragmentStatsBinding
import com.hogent.squads.model.domain.Report
import com.hogent.squads.viewmodel.ReportViewModel
import com.hogent.squads.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.github.farshidroohi.ChartEntity
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class StatsFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var userViewModel: UserViewModel
    private lateinit var reportViewModel: ReportViewModel
    private lateinit var reportsViewAdapter: ReportsRecyclerViewAdapter
    private var _binding: FragmentStatsBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
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
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        val view = binding.root

        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        reportViewModel = ViewModelProvider(requireActivity())[ReportViewModel::class.java]

        createReportsView()
        return view
    }

    fun createReportsView() {
        binding.reports.apply {
            setHasFixedSize(true)
            reportsViewAdapter = ReportsRecyclerViewAdapter()
            reportViewModel.fetchReportsForUser().observe(viewLifecycleOwner) { reports ->
                reportsViewAdapter.setReportList(reports)
                reportsViewAdapter.notifyDataSetChanged()
                initGraph(reports)
            }
            adapter = reportsViewAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun initGraph(reports: List<Report>) {
        if (reports.isNotEmpty()) {
            val weightChartEntity =
                ChartEntity(Color.WHITE, reports.map { it.weight.toFloat() }.toFloatArray())

            val list = ArrayList<ChartEntity>().apply {
                add(weightChartEntity)
            }

            binding.lineChart.setLegend(reports.map { it.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) })
            binding.lineChart.setList(list)
        }
    }

}
