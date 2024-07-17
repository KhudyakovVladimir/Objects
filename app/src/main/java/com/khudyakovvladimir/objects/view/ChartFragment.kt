package com.khudyakovvladimir.objects.view

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.khudyakovvladimir.objects.R
import com.khudyakovvladimir.objects.application.appComponent
import com.khudyakovvladimir.objects.database.ObjectEntity
import com.khudyakovvladimir.objects.utils.TimeHelper
import com.khudyakovvladimir.objects.viewmodel.ObjectViewModel
import com.khudyakovvladimir.objects.viewmodel.ObjectViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import javax.inject.Inject

class ChartFragment: Fragment() {

    private lateinit var barChart : BarChart

    @Inject
    lateinit var timeHelper: TimeHelper

    @Inject
    lateinit var factory: ObjectViewModelFactory.Factory
    private lateinit var objectViewModel: ObjectViewModel
    private lateinit var objectViewModelFactory: ObjectViewModelFactory

    private lateinit var buttonClear : Button
    private lateinit var buttonSave : Button
    private lateinit var buttonLoad : Button

    var count = 0
    var list = listOf("0")

    override fun onAttach(context: Context) {
        super.onAttach(context)

        context.appComponent.injectChartFragment(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.chart_fragment_layout, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        objectViewModelFactory = factory.createObjectViewModelFactory(activity!!.application)
        objectViewModel = ViewModelProvider(this, objectViewModelFactory)[ObjectViewModel::class.java]

        barChart = view.findViewById(R.id.barChart)

        buttonClear = view.findViewById(R.id.buttonClear)
        buttonSave = view.findViewById(R.id.buttonSave)
        buttonLoad = view.findViewById(R.id.buttonLoad)

        buttonClear.setOnClickListener {
                        CoroutineScope(Dispatchers.Main).launch {
                statusRefresh()
                findNavController().navigate(R.id.listFragment)
            }
        }

        buttonSave.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                writeTextToFile(convertDatabaseDataToText())
                findNavController().navigate(R.id.listFragment)
            }
        }

        buttonLoad.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                convertTextToDataBaseEntity(readTextFromFile())
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

    private suspend fun convertDatabaseDataToText() : String = withContext(Dispatchers.IO) {
        val list = objectViewModel.getListObjectAsList()

        val string = StringBuilder()

        val listSize = list.size
        for (x in 1..listSize) {
            val object1 = objectViewModel.objectDao.getObjectById(x)
            string.append("^")
            string.append(object1.id)
            string.append(",")
            string.append(object1.title)
            string.append(",")
            string.append(object1.phone)
            string.append(",")
            string.append(object1.status)
            string.append(",")
            string.append(object1.duty)
            string.append(",")
            string.append(object1.address)
            string.append(",")
            string.append(object1.comment)
            string.append(",")
            string.append(object1.call)
            string.append(",")
            string.append(object1.longitude)
            string.append(",")
            string.append(object1.latitude)
            string.append(",")
            string.append(object1.person)
        }
        return@withContext string.toString()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun writeTextToFile(text : String) {
        Log.d("TAG", "writeTextToFile")

        val pathDocumentsFolder = "/storage/emulated/0/documents"

        val backUpFileDocumentsFolder = File(pathDocumentsFolder, "backUpFileDocumentsFolder")

        if (backUpFileDocumentsFolder.exists()) {
            backUpFileDocumentsFolder.delete()
            backUpFileDocumentsFolder.createNewFile()
            backUpFileDocumentsFolder.appendText(text)
        }
        else {
            backUpFileDocumentsFolder.createNewFile()
            backUpFileDocumentsFolder.appendText(text)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun readTextFromFile() : String {
        val pathDocumentsFolder = "/storage/emulated/0/documents"
        val backUpFileDocumentsFolder = File(pathDocumentsFolder, "backUpFileDocumentsFolder")

        val readResultBackUpFileDocumentsFolder =
            FileInputStream(backUpFileDocumentsFolder)
                .bufferedReader()
                .use { it.readText() }

        Log.d("TAG", "readTextFromFile() - readResultBackUpFileDocumentsFolder = $readResultBackUpFileDocumentsFolder")

        return readResultBackUpFileDocumentsFolder
    }

    private suspend fun convertTextToDataBaseEntity(text: String) = withContext(Dispatchers.IO) {

        val countOfCells = text.length - 1
        var countOfEntities = 0
        val listOfObjects: ArrayList<String> = text.split("^") as ArrayList<String>
        listOfObjects.removeAt(0)

        for (i in 0..countOfCells) {
            val a = text[i]
            if (a == '^') {
                countOfEntities++
            }
        }

        for (i in 0 until listOfObjects.size) {
            val str = listOfObjects[i].split(",")
            val objectEntity = ObjectEntity(str[0].toInt(), str[1], str[2], str[3], str[4], str[5], str[6], str[7], str[8], str[9], str[10])
            objectViewModel.objectDao.insertObjectEntity(objectEntity)
            Log.d("TAG", "convertTextToDataBaseEntity() - str = $str")
        }

        Log.d("TAG", "convertTextToDataBaseEntity() - count2 = $countOfEntities")
        Log.d("TAG", "convertTextToDataBaseEntity() - listOfObjects = $listOfObjects")
        Log.d("TAG", "convertTextToDataBaseEntity() - listOfObjects = ${listOfObjects.size}")
    }
}