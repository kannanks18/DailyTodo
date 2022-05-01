package com.machine.dailytodo.utils

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat.getDrawable
import com.machine.dailytodo.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


fun showProgressDialog(context: Context?): Dialog? {
    val warningDialog = Dialog(context!!)
    warningDialog.setContentView(R.layout.layout_progress_dialog)
    warningDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    warningDialog.setCanceledOnTouchOutside(false)
    warningDialog.setCancelable(false)
    warningDialog.show()
    return warningDialog
}
@SuppressLint("SimpleDateFormat")
fun dateChange(dates: String?): String? {
    var finaldate: String? = null
    finaldate = try {
        val formatter: DateFormat = SimpleDateFormat("dd-MM-yyyy")
        val date = formatter.parse(dates!!) as Date
        val newFormat = SimpleDateFormat("dd-MMM-yyyy")
        newFormat.format(date)
    } catch (e: Exception) {
        null
    }
    return finaldate
}

fun Context.vectorToBitmap(drawableId: Int): Bitmap? {
    val drawable = getDrawable(this, drawableId) ?: return null
    val bitmap = createBitmap(
        drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
    ) ?: return null
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}


