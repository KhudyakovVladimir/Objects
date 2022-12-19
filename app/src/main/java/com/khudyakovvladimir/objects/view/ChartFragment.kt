package com.khudyakovvladimir.objects.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.khudyakovvladimir.objects.R

class ChartFragment: Fragment() {

    private lateinit var barChart : BarChart

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.chart_fragment_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBarChart()
    }

    private fun setBarChart() {
        barChart = view!!.findViewById(R.id.barChart)

        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(8f, 0))
        entries.add(BarEntry(2f, 1))
        entries.add(BarEntry(5f, 2))
        entries.add(BarEntry(20f, 3))
        entries.add(BarEntry(15f, 4))
        entries.add(BarEntry(19f, 5))
        entries.add(BarEntry(8f, 6))
        entries.add(BarEntry(2f, 7))
        entries.add(BarEntry(5f, 8))
        entries.add(BarEntry(20f, 9))
        entries.add(BarEntry(15f, 10))
        entries.add(BarEntry(19f, 11))
        entries.add(BarEntry(8f, 12))
        entries.add(BarEntry(2f, 13))
        entries.add(BarEntry(5f, 14))
        entries.add(BarEntry(20f, 15))
        entries.add(BarEntry(15f, 16))
        entries.add(BarEntry(19f, 17))
        entries.add(BarEntry(19f, 18))
        entries.add(BarEntry(8f, 19))
        entries.add(BarEntry(2f, 20))
        entries.add(BarEntry(5f, 21))
        entries.add(BarEntry(20f, 22))
        entries.add(BarEntry(15f, 23))
        entries.add(BarEntry(19f, 24))
        entries.add(BarEntry(8f, 25))
        entries.add(BarEntry(2f, 26))
        entries.add(BarEntry(5f, 27))
        entries.add(BarEntry(20f, 28))
        entries.add(BarEntry(15f, 29))
        entries.add(BarEntry(19f, 30))

        val barDataSet = BarDataSet(entries, "Дни месяца")

        val labels = ArrayList<String>()
        labels.add("18-Jan")
        labels.add("19-Jan")
        labels.add("20-Jan")
        labels.add("21-Jan")
        labels.add("22-Jan")
        labels.add("23-Jan")
        labels.add("18-Jan")
        labels.add("19-Jan")
        labels.add("20-Jan")
        labels.add("21-Jan")
        labels.add("22-Jan")
        labels.add("23-Jan")
        labels.add("18-Jan")
        labels.add("19-Jan")
        labels.add("20-Jan")
        labels.add("21-Jan")
        labels.add("22-Jan")
        labels.add("23-Jan")
        labels.add("18-Jan")
        labels.add("19-Jan")
        labels.add("20-Jan")
        labels.add("21-Jan")
        labels.add("22-Jan")
        labels.add("23-Jan")
        labels.add("18-Jan")
        labels.add("19-Jan")
        labels.add("20-Jan")
        labels.add("21-Jan")
        labels.add("22-Jan")
        labels.add("23-Jan")
        labels.add("18-Jan")
        labels.add("19-Jan")

        val data = BarData(labels, barDataSet)
        barChart.data = data

        barChart.setDescription("Посещение объектов по дням недели")

        barDataSet.color = resources.getColor(R.color.red)

        barChart.animateY(2000)
    }

}