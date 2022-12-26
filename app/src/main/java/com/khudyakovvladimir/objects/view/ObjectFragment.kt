package com.khudyakovvladimir.objects.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
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
import javax.inject.Inject


class ObjectFragment: Fragment() {

    lateinit var buttonAdd: Button
    lateinit var buttonSave: Button
    lateinit var buttonOptions: Button
    lateinit var buttonDelete: Button
    lateinit var buttonMap: ImageView
    lateinit var buttonPhone: ImageView

    private lateinit var checkBoxStatus: CheckBox
    private lateinit var checkBoxDuty: CheckBox

    lateinit var editTextTitle: EditText
    lateinit var editTextStatus: EditText
    lateinit var editTextDuty: EditText
    lateinit var editTextPhone: EditText
    lateinit var editTextCoordinates: EditText
    lateinit var editTextComment: EditText
    lateinit var editTextLongitude: EditText
    lateinit var editTextLatitude: EditText
    lateinit var textViewCall: TextView
    lateinit var imageViewCall: ImageView

    lateinit var objectEntity: ObjectEntity

    @Inject
    lateinit var timeHelper: TimeHelper

    @Inject
    lateinit var factory: ObjectViewModelFactory.Factory
    lateinit var objectViewModel: ObjectViewModel
    lateinit var objectViewModelFactory: ObjectViewModelFactory

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.appComponent.injectObjectFragment(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback: OnBackPressedCallback = object :OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.listFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.object_fragment_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var id = arguments?.getInt("objectID",0)
            //Log.d("TAG", "id = $id")

        objectViewModelFactory = factory.createObjectViewModelFactory(activity!!.application)
        objectViewModel = ViewModelProvider(this, objectViewModelFactory)[ObjectViewModel::class.java]

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

                var isChecked = "не проверен"
                if (checkBoxStatus.isChecked) { isChecked = "проверен" }
                if (!checkBoxStatus.isChecked) { isChecked = "не проверен"}

                objectViewModel.objectDao.insertObjectEntity(
                    generateNewObjectWithCheckbox(id!!, isChecked)
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

            buttonAdd.isEnabled = true
            buttonSave.isEnabled = true
            buttonDelete.isEnabled = true
        }

        buttonMap.setOnClickListener {
            //2GIS
            val longitude = objectEntity.longitude
            val latitude = objectEntity.latitude
            val geoUriString = "geo:${longitude},${latitude}?z=15"

            //Log.d("TAG", "GEO - longitude : ${longitude} , latitude : ${latitude} ")

            val geoUri: Uri = Uri.parse(geoUriString)
            val mapIntent = Intent(Intent.ACTION_VIEW, geoUri)

            startActivity(mapIntent)

        }

        buttonPhone.setOnClickListener {
            //CALL
            val permissionCheck = ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(
                        Manifest.permission.CALL_PHONE
                    ),1111)
            }

            if(editTextPhone.text.toString() != "") {
                val intent = Intent()
                intent.action = Intent.ACTION_CALL
                intent.data = Uri.parse("tel:${editTextPhone.text}")
                startActivity(intent)
            }else
                makeToast("Номер некорректен или отсутствует.")

        }

        checkBoxStatus.setOnCheckedChangeListener { buttonView, isChecked ->
            if(id == null) {
                id = generateNewID()
            }

            if(isChecked) {
                makeToast("Объект обслужен.")
                CoroutineScope(Dispatchers.IO).launch {
                    objectViewModel.objectDao.insertObjectEntity(
                        generateNewObjectWithCheckbox(id!!, "проверен"))
                }
            }else {
                makeToast("Объект не обслужен !")
                CoroutineScope(Dispatchers.IO).launch {
                    objectViewModel.objectDao.insertObjectEntity(
                        generateNewObjectWithCheckbox(id!!, "не проверен"))
                }
            }
        }

        checkBoxDuty.setOnCheckedChangeListener { buttonView, isChecked ->
            CoroutineScope(Dispatchers.IO).launch {
                if(isChecked) {
                }else {
                    editTextDuty.setText("")
                    objectViewModel.objectDao.insertObjectEntity(
                        generateNewObjectWithCheckbox(id!!, "не проверен"))
                }
            }
        }

        imageViewCall.setOnClickListener {
            val currentNumberOfCalls = textViewCall.text.toString().toInt()
            val newNumberOfCalls = currentNumberOfCalls + 1
            textViewCall.text = newNumberOfCalls.toString()
        }

        //________________________________________________________________________

        if(id != null) {
            var tempObject = ObjectEntity(1,
                "Object",
                "phone",
                "status",
                "duty",
                "address",
                "comment",
                "0",
                "",
                ""
            )
            CoroutineScope(Dispatchers.IO).launch {
                var tempObject1 = async {
                    objectViewModel.objectDao.getObjectById(id!!)
                }
                objectEntity = tempObject1.await()
                //Log.d("TAG", "AWAIT - objectT.name = ${objectEntity.title}")

                CoroutineScope(Dispatchers.Main).launch {
                    editTextTitle.setText(objectEntity.title)
                    editTextStatus.setText(objectEntity.status)
                    editTextDuty.setText(objectEntity.duty)
                    editTextPhone.setText(objectEntity.phone)
                    editTextCoordinates.setText(objectEntity.address)
                    editTextComment.setText(objectEntity.comment)
                    editTextLongitude.setText(objectEntity.longitude)
                    editTextLatitude.setText(objectEntity.latitude)
                    textViewCall.text = objectEntity.call
                    //Log.d("TAG", "ENTITY DAY - ${objectEntity.call}")

                    if (objectEntity.status == "проверен") {
                        checkBoxStatus.isChecked = true
                    }
                    if (objectEntity.status == "не проверен") {
                        checkBoxStatus.isChecked = false
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
        buttonPhone = view.findViewById(R.id.buttonPhone)
        checkBoxStatus = view.findViewById(R.id.checkBoxStatus)
        checkBoxDuty = view.findViewById(R.id.checkBoxDuty)
        editTextTitle = view.findViewById(R.id.editTextTitle)
        editTextStatus = view.findViewById(R.id.editTextStatus)
        editTextDuty = view.findViewById(R.id.editTextDuty)
        editTextPhone = view.findViewById(R.id.editTextPhone)
        editTextCoordinates = view.findViewById(R.id.editTextAddress)
        editTextComment = view.findViewById(R.id.editTextComment)
        editTextLongitude = view.findViewById(R.id.editTextLongitude)
        editTextLatitude = view.findViewById(R.id.editTextLatitude)
        textViewCall = view.findViewById(R.id.textViewCall)
        imageViewCall = view.findViewById(R.id.imageViewCall)

        val list = listOf(
            editTextTitle,
            editTextStatus,
            editTextDuty,
            editTextPhone,
            editTextCoordinates,
            editTextComment,
            editTextLongitude,
            editTextLatitude)
        setInputTypeForEditText(list)
    }

    private fun generateNewID(): Int {
        val list = objectViewModel.getListObjectAsList()
        val size = list.size
        val lastObject = list[size - 1]
        val lastObjectId = lastObject.id
        return lastObjectId + 1
    }

    private fun generateNewObject(id: Int): ObjectEntity {
        return ObjectEntity(
            id,
            editTextTitle.text.toString(),
            editTextStatus.text.toString(),
            editTextDuty.text.toString(),
            editTextPhone.text.toString(),
            editTextCoordinates.text.toString(),
            editTextComment.text.toString(),
            textViewCall.text.toString(),
            editTextLongitude.text.toString(),
            editTextLatitude.text.toString()
        )
    }

    private fun generateNewObjectWithCheckbox(id: Int, isChecked: String): ObjectEntity {
        return ObjectEntity(
            id,
            editTextTitle.text.toString(),
            editTextPhone.text.toString(),
            isChecked,
            editTextDuty.text.toString(),
            editTextCoordinates.text.toString(),
            editTextComment.text.toString(),
            textViewCall.text.toString(),
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