package com.khudyakovvladimir.objects.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.khudyakovvladimir.objects.R
import com.khudyakovvladimir.objects.application.appComponent
import com.khudyakovvladimir.objects.database.ObjectEntity
import com.khudyakovvladimir.objects.viewmodel.ObjectViewModel
import com.khudyakovvladimir.objects.viewmodel.ObjectViewModelFactory
import kotlinx.coroutines.*
import javax.inject.Inject

class ObjectFragment: Fragment() {

    lateinit var button: Button
    lateinit var editTextView1: EditText
    lateinit var editTextView2: EditText
    lateinit var editTextView3: EditText
    lateinit var editTextView4: EditText
    lateinit var editTextView5: EditText
    lateinit var editTextView6: EditText

    @Inject
    lateinit var factory: ObjectViewModelFactory.Factory
    lateinit var objectViewModel: ObjectViewModel
    lateinit var objectViewModelFactory: ObjectViewModelFactory

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.appComponent.injectObjectFragment(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.object_fragment_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var id = arguments?.getInt("objectID",0)
        Log.d("TAG", "id = $id")

        objectViewModelFactory = factory.createObjectViewModelFactory(activity!!.application)
        objectViewModel = ViewModelProvider(this, objectViewModelFactory).get(ObjectViewModel::class.java)

        initViews(view)

        button = view.findViewById(R.id.button)
        button.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {

                val list = objectViewModel.getListObjectAsList()
                val size = list?.size
                val lastObject = list?.get(size - 1)
                val lastObjectId = lastObject?.id
                val newObjectId = lastObjectId!! + 1
                val id = newObjectId

                objectViewModel.objectDao.insertObjectEntity(
                    ObjectEntity(
                        id,
                        editTextView1.text.toString(),
                        editTextView2.text.toString(),
                        editTextView3.text.toString(),
                        editTextView4.text.toString(),
                        editTextView5.text.toString(),
                        editTextView6.text.toString(),
                        "icon"
                    ))

                CoroutineScope(Dispatchers.Main).launch {
                    findNavController().navigate(R.id.listFragment)
                }
            }
        }


        if(id != null) {
            var tempObject = ObjectEntity(1,
                "Object",
                "type",
                "status",
                "duty",
                "coordinates",
                "comment",
                "icon"
            )
            CoroutineScope(Dispatchers.IO).launch {
                var tempObject1 = async {
                    //var id = arguments?.getInt("objectID",0)
                    objectViewModel.objectDao.getObjectById(id!!)
                }
                var objectT = tempObject1.await()
                Log.d("TAG", "AWAIT - objectT.name = ${objectT.name}")

                CoroutineScope(Dispatchers.Main).launch {
                    editTextView1.setText(objectT.name)
                    editTextView2.setText(objectT.status)
                    editTextView3.setText(objectT.duty)
                    editTextView4.setText(objectT.type)
                    editTextView5.setText(objectT.comment)
                    editTextView6.setText(objectT.icon)
                }
             }
        }

    }

    private fun getObject(id : Int): ObjectEntity? {
        var resultObject: ObjectEntity? = null
        runBlocking {
            resultObject = objectViewModel.objectDao.getObjectById(id)
        }
        return resultObject
    }

    private fun initViews(view: View) {
        editTextView1 = view.findViewById(R.id.editText1)
        editTextView2 = view.findViewById(R.id.editText2)
        editTextView3 = view.findViewById(R.id.editText3)
        editTextView4 = view.findViewById(R.id.editText4)
        editTextView5 = view.findViewById(R.id.editText5)
        editTextView6 = view.findViewById(R.id.editText6)
    }
}