package com.khudyakovvladimir.objects.view

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.khudyakovvladimir.objects.R
import com.khudyakovvladimir.objects.application.appComponent
import com.khudyakovvladimir.objects.database.ObjectEntity
import com.khudyakovvladimir.objects.viewmodel.ObjectViewModel
import com.khudyakovvladimir.objects.viewmodel.ObjectViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject


class ObjectFragment: Fragment() {

    lateinit var buttonAdd: Button
    lateinit var buttonSave: Button
    lateinit var buttonOptions: Button
    lateinit var buttonDelete: Button
    lateinit var buttonMap: ImageView
    lateinit var buttonMap2: ImageView

    lateinit var checkBoxStatus: CheckBox
    lateinit var checkBoxDuty: CheckBox

    lateinit var editTextTitle: EditText
    lateinit var editTextStatus: EditText
    lateinit var editTextDuty: EditText
    lateinit var editTextNearest: EditText
    lateinit var editTextCoordinates: EditText
    lateinit var editTextComment: EditText
    lateinit var editTextLongitude: EditText
    lateinit var editTextLatitude: EditText

    lateinit var objectEntity: ObjectEntity

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
                if (checkBoxStatus.isChecked) { v = "true" }
                if (!checkBoxStatus.isChecked) { v = "false"}

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

        buttonMap.setOnClickListener {
            //2GIS
            val longitude = objectEntity.longitude
            val latitude = objectEntity.latitude
            val geoUriString = "geo:${longitude},${latitude}?z=15"

            Log.d("TAG", "GEO - longitude : ${longitude} , latitude : ${latitude} ")

            //val geoUriString = "geo:53.245071,34.356090?z=15"
            val geoUri: Uri = Uri.parse(geoUriString)
            val mapIntent = Intent(Intent.ACTION_VIEW, geoUri)

            startActivity(mapIntent)

        }

        buttonMap2.setOnClickListener {
            if(objectEntity.nearest != "") {
                val bundle = Bundle()
                bundle.putInt("objectID", objectEntity.nearest.toInt())
                findNavController().navigate(R.id.objectFragment, bundle)
            }else {
                makeToast("Добавьте ближайший объект!")
            }
        }

        checkBoxStatus.setOnCheckedChangeListener { buttonView, isChecked ->
            if(id == null) {
                id = generateNewID()
            }

            if(isChecked) {
                makeToast("Объект обслужен.")
                CoroutineScope(Dispatchers.IO).launch {
                    Log.d("TAG", "CHECKBOX - is checked !")
                    objectViewModel.objectDao.insertObjectEntity(
                        generateNewObjectWithCheckbox(id!!, "true"))
                }
            }else {
                makeToast("Объект не обслужен !")
                CoroutineScope(Dispatchers.IO).launch {
                    Log.d("TAG", "CHECKBOX - is NOT checked !")
                    objectViewModel.objectDao.insertObjectEntity(
                        generateNewObjectWithCheckbox(id!!, "false"))
                }
            }
        }

        checkBoxDuty.setOnCheckedChangeListener { buttonView, isChecked ->
            CoroutineScope(Dispatchers.IO).launch {
                if(isChecked) {
                    Log.d("TAG", "CHECKBOX - is checked !")
                }else {
                    Log.d("TAG", "CHECKBOX - is NOT checked !")
                    editTextDuty.setText("")
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
                "icon",
                "",
                ""
            )
            CoroutineScope(Dispatchers.IO).launch {
                var tempObject1 = async {
                    objectViewModel.objectDao.getObjectById(id!!)
                }
                objectEntity = tempObject1.await()
                Log.d("TAG", "AWAIT - objectT.name = ${objectEntity.title}")

                CoroutineScope(Dispatchers.Main).launch {
                    editTextTitle.setText(objectEntity.title)
                    editTextStatus.setText(objectEntity.status)
                    editTextDuty.setText(objectEntity.duty)
                    editTextNearest.setText(objectEntity.nearest)
                    editTextCoordinates.setText(objectEntity.address)
                    editTextComment.setText(objectEntity.comment)
                    editTextLongitude.setText(objectEntity.longitude)
                    editTextLatitude.setText(objectEntity.latitude)

                    if (objectEntity.status == "true") {
                        checkBoxStatus.isChecked = true
                        Log.d("TAG", "TRUE")
                    }
                    if (objectEntity.status == "false") {
                        checkBoxStatus.isChecked = false
                        Log.d("TAG", "FALSE")
                    }

                    if(objectEntity.duty != "") {
                        checkBoxDuty.isChecked = true
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
        buttonMap = view.findViewById(R.id.buttonMap)
        buttonMap2 = view.findViewById(R.id.buttonMap2)
        checkBoxStatus = view.findViewById(R.id.checkBoxStatus)
        checkBoxDuty = view.findViewById(R.id.checkBoxDuty)
        editTextTitle = view.findViewById(R.id.editTextTitle)
        editTextStatus = view.findViewById(R.id.editTextStatus)
        editTextDuty = view.findViewById(R.id.editTextDuty)
        editTextNearest = view.findViewById(R.id.editTextNearest)
        editTextCoordinates = view.findViewById(R.id.editTextAddress)
        editTextComment = view.findViewById(R.id.editTextComment)
        editTextLongitude = view.findViewById(R.id.editTextLongitude)
        editTextLatitude = view.findViewById(R.id.editTextLatitude)

        val list = listOf(editTextTitle, editTextStatus, editTextDuty, editTextNearest, editTextCoordinates, editTextComment, editTextLongitude, editTextLatitude)
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
            editTextNearest.text.toString(),
            editTextCoordinates.text.toString(),
            editTextComment.text.toString(),
            "icon",
            editTextLongitude.text.toString(),
            editTextLatitude.text.toString()
        )
    }

    private fun generateNewObjectWithCheckbox(id: Int, isChecked: String): ObjectEntity {
        return ObjectEntity(
            id,
            editTextTitle.text.toString(),
            editTextNearest.text.toString(),
            isChecked,
            editTextDuty.text.toString(),
            editTextCoordinates.text.toString(),
            editTextComment.text.toString(),
            "icon",
            editTextLongitude.text.toString(),
            editTextLatitude.text.toString()
        )
    }

    private fun setInputTypeForEditText(list: List<EditText>) {
        for (editText in list) {
            editText.inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
        }
    }

    private fun Fragment.makeToast(text: String, duration: Int = Toast.LENGTH_LONG) {
        activity?.let {
            val toast = Toast.makeText(it, text, duration)
            toast.setGravity(Gravity.TOP, 0, 150)
            toast.show()
        }
    }
}