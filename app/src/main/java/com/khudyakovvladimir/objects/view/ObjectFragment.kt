package com.khudyakovvladimir.objects.view

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
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

    lateinit var buttonAdd: Button
    lateinit var buttonSave: Button
    lateinit var buttonOptions: Button
    lateinit var checkBox: CheckBox
    lateinit var buttonDelete: Button
    lateinit var editTextTitle: EditText
    lateinit var editTextStatus: EditText
    lateinit var editTextDuty: EditText
    lateinit var editTextType: EditText
    lateinit var editTextCoordinates: EditText
    lateinit var editTextComment: EditText

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

        buttonAdd.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {

                val newID = generateNewID()

                objectViewModel.objectDao.insertObjectEntity(
                    generateNewObject(newID))

                CoroutineScope(Dispatchers.Main).launch {
                    findNavController().navigate(R.id.listFragment)
                }
            }
        }

        buttonSave.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                if(id == null) {
                    id = generateNewID()
                }

                var v = "false"
                if (checkBox.isChecked) { v = "true" }
                if (!checkBox.isChecked) { v = "false"}

                objectViewModel.objectDao.insertObjectEntity(
                    //generateNewObject(id!!))
                    generateNewObjectWithCheckbox(id!!, v)
                )

                CoroutineScope(Dispatchers.Main).launch {
                    findNavController().navigate(R.id.listFragment)
                }
            }
        }

        buttonDelete.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                objectViewModel.objectDao.deleteObjectEntity(
                    //generateNewObject(id!!))
                    generateNewObject(id!!)
                )

                CoroutineScope(Dispatchers.Main).launch {
                    findNavController().navigate(R.id.listFragment)
                }
            }
        }

        buttonOptions.setOnClickListener {
            buttonAdd.setBackgroundColor(Color.GRAY)
            buttonSave.setBackgroundColor(Color.GRAY)
            buttonDelete.setBackgroundColor(Color.RED)

            buttonAdd.setEnabled(true)
            buttonSave.setEnabled(true)
            buttonDelete.setEnabled(true)
        }

        checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            CoroutineScope(Dispatchers.IO).launch {

                if(id == null) {
                    id = generateNewID()
                }

                if(isChecked) {
                    Log.d("TAG", "CHECKBOX - is checked !")
                    objectViewModel.objectDao.insertObjectEntity(
                        generateNewObjectWithCheckbox(id!!, "true"))
                }else {
                    Log.d("TAG", "CHECKBOX - is NOT checked !")
                    objectViewModel.objectDao.insertObjectEntity(
                        generateNewObjectWithCheckbox(id!!, "false"))
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
                    objectViewModel.objectDao.getObjectById(id!!)
                }
                var objectT = tempObject1.await()
                Log.d("TAG", "AWAIT - objectT.name = ${objectT.title}")

                CoroutineScope(Dispatchers.Main).launch {
                    editTextTitle.setText(objectT.title)
                    editTextStatus.setText(objectT.status)
                    editTextDuty.setText(objectT.duty)
                    editTextType.setText(objectT.type)
                    editTextCoordinates.setText(objectT.coordinates)
                    editTextComment.setText(objectT.comment)

                    if (objectT.status == "true") {
                        checkBox.isChecked = true
                        Log.d("TAG", "TRUE")
                    }
                    if (objectT.status == "false") {
                        checkBox.isChecked = false
                        Log.d("TAG", "FALSE")
                    }
                }
             }
        }

    }

    private fun initViews(view: View) {
        buttonAdd = view.findViewById(R.id.buttonAdd)
        buttonSave = view.findViewById(R.id.buttonSave)
        buttonOptions = view.findViewById(R.id.buttonOptions)
        buttonDelete = view.findViewById(R.id.buttonDelete)
        checkBox = view.findViewById(R.id.checkBox)
        editTextTitle = view.findViewById(R.id.editTextTitle)
        editTextStatus = view.findViewById(R.id.editTextStatus)
        editTextDuty = view.findViewById(R.id.editTextDuty)
        editTextType = view.findViewById(R.id.editTextType)
        editTextCoordinates = view.findViewById(R.id.editTextCoordinates)
        editTextComment = view.findViewById(R.id.editTextComment)

        val list = listOf(editTextTitle, editTextStatus, editTextDuty, editTextType, editTextCoordinates, editTextComment)
        setInputTypeForEditText(list)
    }

    private fun generateNewID(): Int {
        val list = objectViewModel.getListObjectAsList()
        val size = list.size
        val lastObject = list[size - 1]
        val lastObjectId = lastObject.id
        return lastObjectId!! + 1
    }

    private fun generateNewObject(id: Int): ObjectEntity {
        return ObjectEntity(
            id,
            editTextTitle.text.toString(),
            editTextStatus.text.toString(),
            editTextDuty.text.toString(),
            editTextType.text.toString(),
            editTextCoordinates.text.toString(),
            editTextComment.text.toString(),
            "icon"
        )
    }

    private fun generateNewObjectWithCheckbox(id: Int, isChecked: String): ObjectEntity {
        return ObjectEntity(
            id,
            editTextTitle.text.toString(),
            editTextType.text.toString(),
            isChecked,
            editTextDuty.text.toString(),
            editTextCoordinates.text.toString(),
            editTextComment.text.toString(),
            "icon"
        )
    }

    private fun setInputTypeForEditText(list: List<EditText>) {
        for (editText in list) {
            editText.inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
        }
    }
}