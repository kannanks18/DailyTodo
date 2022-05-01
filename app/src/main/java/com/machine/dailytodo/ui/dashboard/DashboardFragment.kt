package com.machine.dailytodo.ui.dashboard

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.machine.dailytodo.R
import com.machine.dailytodo.databinding.FragmentDashboardBinding
import com.machine.dailytodo.ui.dashboard.model.TodoModel
import com.machine.dailytodo.utils.showProgressDialog
import com.machine.dailytodo.work.*
import com.machine.dailytodo.work.Notification
import com.machine.dailytodo.work.Notification.Companion.channelID
import com.machine.dailytodo.work.Notification.Companion.messageExtra
import com.machine.dailytodo.work.Notification.Companion.notificationID
import com.machine.dailytodo.work.Notification.Companion.titleExtra
import dagger.hilt.android.AndroidEntryPoint

import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
open class DashboardFragment : Fragment(R.layout.fragment_dashboard), TODOClickListener {

    private lateinit var binding: FragmentDashboardBinding
    private val viewModel: DashboardViewModel by viewModels()
    private var bottomSheetDialog: BottomSheetDialog? = null
    private var bottomSheetDialog1: BottomSheetDialog? = null
    private var bottomSheetDialog2: BottomSheetDialog? = null
    var mobile: String? = ""
    private val db = Firebase.firestore
    var databaseReference: DatabaseReference? = null
    private val dashboardAdapter by lazy { DashboardAdapter(this) }
    private lateinit var todoList: ArrayList<TodoModel>
    var loading: Dialog? = null
    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDashboardBinding.bind(view)
        databaseReference = FirebaseDatabase.getInstance().reference.child("database")

        binding.datePick.text = SimpleDateFormat("dd-MMM-yyyy").format(Calendar.getInstance().time)
        mobile = viewModel.mobileNumber()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            createNotificationChannel()
        }
        binding.recyclerView.adapter = dashboardAdapter
        todoList = arrayListOf()
        val todoDatePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Select date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build()

        binding.addBtn.setOnClickListener {
            addBottomSheetDialog()

        }
        binding.bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.hamburger -> {
                    menuBottomSheet()
                    true
                }

                else -> false
            }
        }
        binding.datePick.setOnClickListener {
            todoDatePicker.show(parentFragmentManager, "tags")
        }
        todoDatePicker.addOnPositiveButtonClickListener {
            // formatting date in dd-mm-yyyy format.
            val dateFormatter = SimpleDateFormat("dd-MMM-yyyy")
            val date = dateFormatter.format(Date(it))
            binding.datePick.text = date.toString()

            fetchData(binding.datePick.text.toString())
        }
        fetchData(binding.datePick.text.toString())
    }

    @SuppressLint("SimpleDateFormat")
    private fun saveData(
        todoHeader: String,
        todoDesc: String,
        todoDatePick: String,
        todoTimePick: String
    ) {
        val key: String? = databaseReference!!.push().key
        val date = SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().time)
        val time = SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Calendar.getInstance().time)

        val user = hashMapOf(
            "mobile" to mobile,
            "todoDate" to todoDatePick,
            "priority" to "0",
            "todoTime" to todoTimePick,
            "todoName" to todoHeader,
            "todoDesc" to todoDesc,
            "key" to key,
            "todoId" to key,
            "createdDate" to date,
            "createdTime" to time,
            "status" to "0",
            "comments" to ""
        )

        db.collection("todo").document(mobile!!).collection(mobile!!).document(key!!)
            .set(user)
            .addOnSuccessListener {
                fetchData(binding.datePick.text.toString())
                Toast.makeText(requireContext(), "Detailed Added ", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_SHORT).show()
            }

    }

    private fun detailsBottomSheetDialog(todo: TodoModel) {

        val view: View =
            LayoutInflater.from(requireContext()).inflate(R.layout.layout_todo_show, null)
        val bottomUpdate: MaterialButton = view.findViewById(R.id.btnUpdate)
        val bottomDiscard: MaterialButton = view.findViewById(R.id.btnDiscard)
        val headerDesc: TextView = view.findViewById(R.id.headerDesc)
        val messageHeader: TextView = view.findViewById(R.id.header_title)
        val commentTitle: TextView = view.findViewById(R.id.commentTitle)
        val todoDate: MaterialButton = view.findViewById(R.id.todoDate)
        val todoTime: MaterialButton = view.findViewById(R.id.todoTime)
        val completeCheckBox: CheckBox = view.findViewById(R.id.checkbox)
        val comments: TextInputLayout = view.findViewById(R.id.comments)
        val dataComment: TextInputEditText = view.findViewById(R.id.dataComment)
        val btnAlert: MaterialButton = view.findViewById(R.id.btnAlert)

        messageHeader.text = todo.todoName
        headerDesc.text = todo.todoDesc
        todoDate.text = todo.todoDate
        todoTime.text = todo.todoTime
        commentTitle.text = todo.comments
        dataComment.setText(todo.comments)
        var status: String = todo.status

        if (status == "1") {
            completeCheckBox.visibility = View.GONE
            bottomUpdate.visibility = View.GONE
            bottomDiscard.visibility = View.GONE
            btnAlert.visibility = View.GONE
            comments.visibility = View.GONE
            commentTitle.visibility = View.VISIBLE
        } else {
            completeCheckBox.visibility = View.VISIBLE
            bottomUpdate.visibility = View.VISIBLE
            bottomDiscard.visibility = View.VISIBLE
            comments.visibility = View.VISIBLE
            btnAlert.visibility = View.VISIBLE
            commentTitle.visibility = View.GONE
        }
        btnAlert.setOnClickListener {
            bottomSheetDialog1!!.cancel()
            scheduleNotification(todo)
        }
        bottomUpdate.setOnClickListener {
            updateData(status, comments.editText?.text.toString(), todo.todoDate, todo.key)
            bottomSheetDialog1!!.cancel()
        }

        completeCheckBox.setOnClickListener(View.OnClickListener {
            status = if (completeCheckBox.isChecked) {
                "1"
            } else {
                "0"
            }
        })
        bottomDiscard.setOnClickListener {

            bottomSheetDialog1!!.cancel()
        }
        bottomSheetDialog1 = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        bottomSheetDialog1!!.setContentView(view)
        bottomSheetDialog1!!.setCancelable(false)
        bottomSheetDialog1!!.show()
    }

    @SuppressLint("SimpleDateFormat")
    private fun updateData(status: String, comments: String, todoDate: String, key: String) {
        val updateRef = db.collection("todo").document(mobile!!).collection(mobile!!).document(key)
        val updateTime = SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Calendar.getInstance().time)

        updateRef.update(
            mapOf(
                "status" to status,
                "comments" to comments,
                "updateTime" to updateTime
            )
        )
            .addOnSuccessListener {
                fetchData(binding.datePick.text.toString())
                Log.d("TAG", "DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error updating document", e)
            }

    }

    private fun fetchData(date: String) {
        initLoading()
        todoList.clear()
        db.collection("todo").document(mobile!!).collection(mobile!!)
            .whereEqualTo("todoDate", date)
            .whereEqualTo("status","0")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val todo: TodoModel = document.toObject(TodoModel::class.java)
                    Log.d("TAG", "${document.id} => ${document.data}")
                    todoList.add(todo)
                }
                fillData(todoList)

            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents.", exception)
            }
    }

    private fun fillData(todoList: List<TodoModel>) {
        Log.d("TAG", "$todoList")
        if (todoList.isNotEmpty()) {
            binding.recyclerView.visibility = View.VISIBLE
            binding.nodatamessage.visibility = View.GONE
            dashboardAdapter.submitList(todoList)
            dashboardAdapter.notifyDataSetChanged()
        } else {
            binding.recyclerView.visibility = View.GONE
            binding.nodatamessage.visibility = View.VISIBLE
            dashboardAdapter.submitList(todoList)
            dashboardAdapter.notifyDataSetChanged()

        }
        cancelLoading()
    }

    override fun onClick(todo: TodoModel) {

        detailsBottomSheetDialog(todo)
    }

    @SuppressLint("SimpleDateFormat")
    private fun addBottomSheetDialog() {

        val view: View =
            LayoutInflater.from(requireContext()).inflate(R.layout.layout_add_todo, null)
        val btnAdd: MaterialButton = view.findViewById(R.id.btnAdd)
        val btnCancel: MaterialButton = view.findViewById(R.id.btnCancel)
        val todoHeader: TextInputLayout = view.findViewById(R.id.todoHeader)
        val todoDescription: TextInputLayout = view.findViewById(R.id.todoDescription)
        val todoDatePick: MaterialButton = view.findViewById(R.id.todoDatePick)
        val todoTimePick: MaterialButton = view.findViewById(R.id.todoTimePick)
        val datePick = SimpleDateFormat("dd-MMM-yyyy").format(Calendar.getInstance().time)
        val timePick = SimpleDateFormat("hh:MM a").format(Calendar.getInstance().time)
        todoDatePick.text = datePick.toString()
        todoTimePick.text = timePick.toString()

        val datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Select date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build()
        val timePicker =
            MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_12H).setHour(12)
                .setMinute(10).setTitleText("Select time").build()
        btnAdd.setOnClickListener {
            if (TextUtils.isEmpty(todoHeader.editText?.text.toString())) {
                todoHeader.error = "Please Enter TO DO"
            } else {
                bottomSheetDialog!!.cancel()
                saveData(
                    todoHeader.editText?.text.toString(),
                    todoDescription.editText?.text.toString(),
                    todoDatePick.text.toString(),
                    todoTimePick.text.toString()
                )
            }
        }
        btnCancel.setOnClickListener {

            bottomSheetDialog!!.cancel()
        }
        todoDatePick.setOnClickListener {

            datePicker.show(parentFragmentManager, "tag")
        }
        todoTimePick.setOnClickListener {
            timePicker.show(parentFragmentManager, "tag")
        }
        datePicker.addOnPositiveButtonClickListener {
            // formatting date in dd-mm-yyyy format.
            val dateFormatter = SimpleDateFormat("dd-MMM-yyyy")
            val date = dateFormatter.format(Date(it))
            todoDatePick.text = date.toString()

        }
        timePicker.addOnPositiveButtonClickListener {
            // formatting date in dd-mm-yyyy format.
            val pickedHour: Int = timePicker.hour
            val pickedMinute: Int = timePicker.minute

            // check for single digit hour hour and minute
            // and update TextView accordingly
            val formattedTime: String = when {
                pickedHour > 12 -> {
                    if (pickedMinute < 10) {
                        "${timePicker.hour - 12}:0${timePicker.minute} PM"
                    } else {
                        "${timePicker.hour - 12}:${timePicker.minute} PM"
                    }
                }
                pickedHour == 12 -> {
                    if (pickedMinute < 10) {
                        "${timePicker.hour}:0${timePicker.minute} PM"
                    } else {
                        "${timePicker.hour}:${timePicker.minute} PM"
                    }
                }
                pickedHour == 0 -> {
                    if (pickedMinute < 10) {
                        "${timePicker.hour + 12}:0${timePicker.minute} AM"
                    } else {
                        "${timePicker.hour + 12}:${timePicker.minute} AM"
                    }
                }
                else -> {
                    if (pickedMinute < 10) {
                        "${timePicker.hour}:0${timePicker.minute} AM"
                    } else {
                        "${timePicker.hour}:${timePicker.minute} AM"
                    }
                }
            }

            todoTimePick.text = formattedTime
        }

        bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        bottomSheetDialog!!.setContentView(view)
        bottomSheetDialog!!.setCancelable(false)
        bottomSheetDialog!!.show()

    }

    private fun scheduleNotification(todo: TodoModel) {
        val intent = Intent(requireContext(), Notification::class.java)
        val title = todo.todoName
        val message = todo.todoDesc
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = getTime(todo.todoDate, todo.todoTime)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                time,
                pendingIntent
            )
            showAlert(time, "To Do", "Alert Enabled")
        }

    }


    @SuppressLint("SimpleDateFormat")
    private fun getTime(todoDate: String, todoTime: String): Long {
        val dateFormatter = SimpleDateFormat("dd-MM-yyyy")
        val date = dateFormatter.format(Date(todoDate))
        var timeHour: String = "0"
        var timeMinute: String = "0"
        if (todoTime.split(" ")[1] == "PM" && (todoTime.split(" ")[0].split(":")[0].toInt()) == 12) {
            timeHour = todoTime.split(" ")[0].split(":")[0]
            timeMinute = todoTime.split(" ")[0].split(":")[1]
        } else if (todoTime.split(" ")[1] == "PM" && (todoTime.split(" ")[0].split(":")[0].toInt()) > 0) {
            timeHour = (todoTime.split(" ")[0].split(":")[0].toInt() + 12).toString()
            timeMinute = todoTime.split(" ")[0].split(":")[1]
        } else if (todoTime.split(" ")[1] == "AM") {
            timeHour = (todoTime.split(" ")[0].split(":")[0].toInt()).toString()
            timeMinute = todoTime.split(" ")[0].split(":")[1]
        }

        val calendar = Calendar.getInstance()
        calendar.set(
            date.split("-")[2].toInt(),
            date.split("-")[1].toInt() - 1,
            date.split("-")[0].toInt(),
            timeHour.toInt(),
            timeMinute.toInt()
        )
        return calendar.timeInMillis
    }

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.N)
    private fun createNotificationChannel() {
        val name = "Notification Channel"
        val desc = "A Description of the Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        val notificationManager =
            requireContext().getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun showAlert(time: Long, title: String, message: String) {
        val date = Date(time)
        val dateFormat = android.text.format.DateFormat.getLongDateFormat(requireContext())
        val timeFormat = android.text.format.DateFormat.getTimeFormat(requireContext())
        var dateMsg = dateFormat.format(date)
        var timeMsg = timeFormat.format(date)
        Snackbar.make(
            binding.bottomAppBar,
            "$title $message at $dateMsg , $timeMsg",
            Snackbar.LENGTH_SHORT
        ).show()
    }
    private fun menuBottomSheet() {

        val view: View =LayoutInflater.from(requireContext()).inflate(R.layout.layout__show_menu, null)
        val todoAll: MaterialButton = view.findViewById(R.id.todoAll)
        val profile: MaterialButton = view.findViewById(R.id.profile)
        val btnLogout: MaterialButton = view.findViewById(R.id.btnLogout)
        todoAll.setOnClickListener {
            bottomSheetDialog2!!.cancel()
            findNavController().navigate(DashboardFragmentDirections.actionDashboardFragmentToAllDataFragment())
        }
        btnLogout.setOnClickListener {
            bottomSheetDialog2!!.cancel()
            viewModel.logout()
            findNavController().navigate(DashboardFragmentDirections.actionGlobalToSplashFragment())

        }
        profile.setOnClickListener {
            bottomSheetDialog2!!.cancel()

            findNavController().navigate(DashboardFragmentDirections.actionDashboardFragmentToProfileFragment())

        }
        bottomSheetDialog2 = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        bottomSheetDialog2!!.setContentView(view)
        bottomSheetDialog2!!.setCancelable(true)
        bottomSheetDialog2!!.show()

    }
    private fun initLoading() {
        loading = showProgressDialog(requireContext())
    }

    private fun cancelLoading() {
        if (loading != null) {
            loading?.cancel()
            loading = null
        }
    }

}