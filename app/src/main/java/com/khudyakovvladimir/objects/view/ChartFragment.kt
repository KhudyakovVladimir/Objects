package com.khudyakovvladimir.objects.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.khudyakovvladimir.objects.R
import com.khudyakovvladimir.objects.application.appComponent
import com.khudyakovvladimir.objects.utils.TimeHelper
import com.khudyakovvladimir.objects.viewmodel.ObjectViewModel
import com.khudyakovvladimir.objects.viewmodel.ObjectViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChartFragment: Fragment() {

    private lateinit var barChart : BarChart

    @Inject
    lateinit var timeHelper: TimeHelper

    @Inject
    lateinit var factory: ObjectViewModelFactory.Factory
    private lateinit var objectViewModel: ObjectViewModel
    private lateinit var objectViewModelFactory: ObjectViewModelFactory

    private lateinit var fab : FloatingActionButton

    var count = 0
    var list = listOf("0")

    override fun onAttach(context: Context) {
        super.onAttach(context)

        context.appComponent.injectChartFragment(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.chart_fragment_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        objectViewModelFactory = factory.createObjectViewModelFactory(activity!!.application)
        objectViewModel = ViewModelProvider(this, objectViewModelFactory)[ObjectViewModel::class.java]

        barChart = view.findViewById(R.id.barChart)

        fab = view.findViewById(R.id.floatingActionButton)
        fab.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                statusRefresh()
                findNavController().navigate(R.id.listFragment)
            }
        }

        var arrayOfDays = list
        //await
        CoroutineScope(Dispatchers.Main).launch {
            var go = async {
                getArrayOfDays()
            }
           arrayOfDays = go.await()
        }

        objectViewModel.getCountOfRows().observe(this) {
            count = it
        }
    }

    private fun getArrayOfDays(): List<String> {
        var mockList = emptyList<String>()
        CoroutineScope(Dispatchers.IO).launch {
            var countOfObjects = async {
                objectViewModel.objectDao.getListOfDays()
            }
            val coroutineList = countOfObjects.await()
            mockList = coroutineList
            list = mockList
            CoroutineScope(Dispatchers.Main).launch {
                setBarChart(mockList.size, mockList)
            }
        }
        return mockList
    }

    private fun setBarChart(count: Int, list: List<String>) {
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        //set the y-axis and x-axis
        for (i in 0 until count) {
            entries.add(BarEntry(list[i].toFloat(), i))
            labels.add((i + 1).toString())
        }

        val barDataSet = BarDataSet(entries, "Объекты")
        val data = BarData(labels, barDataSet)
        barChart.data = data
        barChart.setDescription("Количество посещений")
        barDataSet.color = resources.getColor(R.color.red)
        barChart.animateY(1000)
    }

    private fun statusRefresh() {
        CoroutineScope(Dispatchers.IO).launch {
            var list = objectViewModel.getListObjectAsList()

            var listSize = list.size
            for (x in 1..listSize) {
                var object1 = objectViewModel.objectDao.getObjectById(x)
                object1.status = "не проверен"
                object1.call = "0"
                objectViewModel.objectDao.insertObjectEntity(object1)
            }
        }
    }

}