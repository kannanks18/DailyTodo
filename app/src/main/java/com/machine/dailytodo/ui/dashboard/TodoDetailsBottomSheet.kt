package com.machine.dailytodo.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.machine.dailytodo.R

class TodoDetailsBottomSheet : BottomSheetDialogFragment() {

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? = inflater.inflate(R.layout.todo_details_bottom_sheet, container, false)

        companion object {
            const val TAG = "ModalBottomSheet"
        }

}