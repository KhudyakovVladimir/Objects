package com.khudyakovvladimir.objects.view

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.khudyakovvladimir.objects.R
import com.khudyakovvladimir.objects.application.appComponent
import com.khudyakovvladimir.objects.utils.Receiver
import com.khudyakovvladimir.objects.utils.TimeHelper
import com.khudyakovvladimir.objects.viewmodel.ObjectViewModel
import com.khudyakovvladimir.objects.viewmodel.ObjectViewModelFactory
import javax.inject.Inject

class NotificationFragment: Fragment() {

    @Inject
    lateinit var timeHelper: TimeHelper

    @Inject
    lateinit var factory: ObjectViewModelFactory.Factory
    lateinit var objectViewModel: ObjectViewModel
    lateinit var objectViewModelFactory: ObjectViewModelFactory

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.appComponent.injectNotificationFragment(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.notification_layout, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        objectViewModelFactory = factory.createObjectViewModelFactory(activity!!.application)
        objectViewModel = ViewModelProvider(this, objectViewModelFactory)[ObjectViewModel::class.java]

        val buttonNotification = view.findViewById<ImageView>(R.id.buttonNotification)
        val date = view.findViewById<DatePicker>(R.id.date)
        val time = view.findViewById<com.khudyakovvladimir.objects.utils.TimePickerCustom>(R.id.time)

        //get object id for notification
        val id = arguments!!.getInt("id")

        buttonNotification.setOnClickListener {
            val customCalendar = Calendar.getInstance()
            customCalendar.set(
                date.year, date.month, date.dayOfMonth, time.hour, time.minute, 0
            )
            val customTime = customCalendar.timeInMillis
            val currentTime = System.currentTimeMillis()
            val delay = customTime - currentTime

            if (customTime > currentTime) {
                makeToast(timeHelper.getCurrentTimeForNotification(customCalendar.time))
                //makeToast(delay.toString())
                doInReceiver(delay, id)
            }
        }
    }

    private fun doInReceiver(delay: Long, id: Int) {
        val alarmManager = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, Receiver::class.java)

        intent.putExtra("id", "$id")

        //Used for filtering inside Broadcast receiver
        intent.action = "MyBroadcastReceiverAction"
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE)

        //time
        //val msUntilTriggerHour: Long = 15000
        //val alarmTimeAtUTC: Long = System.currentTimeMillis() + msUntilTriggerHour
        val alarmTimeAtUTC: Long = System.currentTimeMillis() + delay

        //Depending on the version of Android use different function for setting an
        //Alarm.
        //setAlarmClock() - used for everything lower than Android M
        //setExactAndAllowWhileIdle() - used for everything on Android M and higher
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
            alarmManager.setAlarmClock(
                AlarmManager.AlarmClockInfo(alarmTimeAtUTC, pendingIntent),
                pendingIntent
            )
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                alarmTimeAtUTC,
                pendingIntent
            )
        }
        findNavController().navigate(R.id.listFragment)
    }

    private fun Fragment.makeToast(text: String, duration: Int = Toast.LENGTH_LONG) {
        activity?.let {
            val toast = Toast.makeText(it, text, duration)
            toast.setGravity(Gravity.TOP, 0, 150)
            toast.show()
        }
    }
}