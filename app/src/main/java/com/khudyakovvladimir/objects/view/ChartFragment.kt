package com.khudyakovvladimir.objects.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.khudyakovvladimir.objects.R
import com.khudyakovvladimir.objects.application.appComponent
import com.khudyakovvladimir.objects.utils.TimeHelper
import com.khudyakovvladimir.objects.viewmodel.ObjectViewModel
import com.khudyakovvladimir.objects.viewmodel.ObjectViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ChartFragment: Fragment() {

    private lateinit var barChart : BarChart

    @Inject
    lateinit var timeHelper: TimeHelper

    @Inject
    lateinit var factory: ObjectViewModelFactory.Factory
    private lateinit var objectViewModel: ObjectViewModel
    private lateinit var objectViewModelFactory: ObjectViewModelFactory

    var count = 0
    //var list = emptyList<String>()
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

        var arrayOfDays = list
        //await
        CoroutineScope(Dispatchers.IO).launch {
            var go = async {
                getArrayOfDays()
            }
           arrayOfDays = go.await()
        }

        objectViewModel.getCountOfRows().observe(this) {
            count = it
            setBarChart(list.size, list)
        }
    }

    private fun getArrayOfDays(): List<String> {
        var vv = emptyList<String>()
        CoroutineScope(Dispatchers.IO).launch {
            var countOfObjects = async {
                objectViewModel.objectDao.getListOfDays()
            }
            val v = countOfObjects.await()
            vv = v
            list = vv
        }
        return vv
    }

    private fun setBarChart(count: Int, list: List<String>) {
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        //set the y-axis and x-axis
        Log.d("TAG", "count - $count")
        Log.d("TAG", "list - $list")
        for (i in 0..count - 1) {
            Log.d("TAG", "i - $i")
            entries.add(BarEntry(list[i].toFloat(), i))
            labels.add((i + 1).toString())
        }

        Log.d("TAG", "entries - $entries")
        Log.d("TAG", "labels - $labels")

        val barDataSet = BarDataSet(entries, "Объекты")
        val data = BarData(labels, barDataSet)
        barChart.data = data
        barChart.setDescription("Количество посещений")
        barDataSet.color = resources.getColor(R.color.red)
        barChart.animateY(1000)
    }

}