package com.khudyakovvladimir.objects.view

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.khudyakovvladimir.objects.R
import com.khudyakovvladimir.objects.application.appComponent
import com.khudyakovvladimir.objects.database.DBHelper
import com.khudyakovvladimir.objects.database.ObjectEntity
import com.khudyakovvladimir.objects.recyclerview.ObjectAdapter
import com.khudyakovvladimir.objects.viewmodel.ObjectViewModel
import com.khudyakovvladimir.objects.viewmodel.ObjectViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

class ListFragment: Fragment() {

    @Inject
    lateinit var factory: ObjectViewModelFactory.Factory
    private lateinit var objectViewModel: ObjectViewModel
    private lateinit var objectViewModelFactory: ObjectViewModelFactory

    private lateinit var recyclerView: RecyclerView
    private lateinit var objectAdapter: ObjectAdapter

    private lateinit var fab: FloatingActionButton
    private lateinit var button: Button
    private lateinit var button2: Button
    private lateinit var button3: Button
    lateinit var textView: TextView
    lateinit var textView2: TextView
    private var isDatabaseCreated = false
    private var isSortByDuty = false

    lateinit var list1: List<ObjectEntity>
    lateinit var list2: List<ObjectEntity>

    override fun onAttach(context: Context) {
        super.onAttach(context)

        context.appComponent.injectListFragment(this)

        val sharedPreferences = activity?.applicationContext!!.getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)

        if (sharedPreferences.contains("database")) {
            isDatabaseCreated = sharedPreferences.getBoolean("database", false)
        }

        if (sharedPreferences.contains("sortByDuty")) {
            isSortByDuty = sharedPreferences.getBoolean("sortByDuty", false)
        }

        if (!isDatabaseCreated) {
            val dbHelper = activity?.let { DBHelper(it) }
            dbHelper?.createDatabase()

            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putBoolean("database", true)
            editor.apply()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.list_fragment_layout, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        objectViewModelFactory = factory.createObjectViewModelFactory(activity!!.application)
        objectViewModel = ViewModelProvider(this, objectViewModelFactory)[ObjectViewModel::class.java]

        var list = listOf(ObjectEntity(1,
            "Object",
            "type",
            "status",
            "duty",
            "coordinates",
            "comment",
            "0",
            "",
            "",
            ""
        ))

        recyclerView = view.findViewById(R.id.recyclerView)
        fab = view.findViewById(R.id.floatingActionButton)
        button = view.findViewById(R.id.button)
        button2 = view.findViewById(R.id.button2)
        button3 = view.findViewById(R.id.button3)
        textView = view.findViewById(R.id.textView)
        textView2 = view.findViewById(R.id.textView2)
        recyclerView.layoutManager = LinearLayoutManager(activity?.applicationContext)

        val unicode = 0x1F9EF
        val textEmoji = String(Character.toChars(unicode))
        button.text = textEmoji
        val unicode2 = 0x1F692
        val textEmoji2 = String(Character.toChars(unicode2))
        button2.text = textEmoji2
        val unicode3 = 0x1F4C8
        val textEmoji3 = String(Character.toChars(unicode3))
        button3.text = textEmoji3

        val itemClick = { objectEntity: ObjectEntity -> navigateToSingleObject(objectEntity.id)}

        objectAdapter = ObjectAdapter(
            activity!!.applicationContext,
            list,
            itemClick
        )
        recyclerView.adapter = objectAdapter

        objectViewModel.getListObjects().observe(this) {
            list1 = it
            objectAdapter.list = it
            objectAdapter.notifyDataSetChanged()
        }

        objectViewModel.getListDutyObjects().observe(this) {
            list2 = it

            if(isSortByDuty) {
                objectAdapter.list = list2
                objectAdapter.notifyDataSetChanged()
            }
        }

        objectViewModel.getCountOfRows().observe(this) {
        }

        objectViewModel.getStatus().observe(this) {
            textView.text = "$it"
        }

        objectViewModel.getDuty().observe(this) {
            textView2.text = "$it"
        }

        fab.setOnClickListener {
            findNavController().navigate(R.id.objectFragment)
            //CoroutineScope(Dispatchers.Main).launch { statusRefresh() }
        }

        button.setOnClickListener {
            objectAdapter.list = list2
            objectAdapter.notifyDataSetChanged()

            val sharedPreferences = activity?.applicationContext!!.getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)

            if (sharedPreferences.contains("sortByDuty")) {
                isSortByDuty = sharedPreferences.getBoolean("sortByDuty", false)
            }

            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putBoolean("sortByDuty", true)
            editor.apply()
        }

        button2.setOnClickListener {
            objectAdapter.list = list1
            objectAdapter.notifyDataSetChanged()

            val sharedPreferences = activity?.applicationContext!!.getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)

            if (sharedPreferences.contains("sortByDuty")) {
                isSortByDuty = sharedPreferences.getBoolean("sortByDuty", false)
            }

            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putBoolean("sortByDuty", false)
            editor.apply()
        }

        button3.setOnClickListener {
            findNavController().navigate(R.id.chartFragment)
        }
    }

    private fun navigateToSingleObject(objectID: Int) {
        val bundle = Bundle()
        bundle.putInt("objectID", objectID)
        findNavController().navigate(R.id.objectFragment, bundle)
    }
}