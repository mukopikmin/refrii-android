package com.refrii.client.views.activities

import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.refrii.client.BasicCallback
import com.refrii.client.R
import com.refrii.client.RetrofitFactory
import com.refrii.client.services.UnitService
import com.refrii.client.models.Unit

import java.text.SimpleDateFormat

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response

class UnitActivity : AppCompatActivity() {

    private var mUnit: Unit? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unit)
        val toolbar = findViewById<Toolbar>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val intent = intent
        mUnit = intent.getSerializableExtra("unit") as Unit

        val labelTextView = findViewById<TextView>(R.id.labelTextView) as TextView
        val stepTextView = findViewById<TextView>(R.id.stepTextView) as TextView
        val createdTextView = findViewById<TextView>(R.id.createdTextView) as TextView
        val updatedTextView = findViewById<TextView>(R.id.updatedTextView) as TextView
        val labelImageView = findViewById<ImageView>(R.id.labelImageView) as ImageView
        val stepImageView = findViewById<ImageView>(R.id.stepImageView) as ImageView
        val fab = findViewById<FloatingActionButton>(R.id.fab) as FloatingActionButton

        setUnit(mUnit)

        labelImageView.setOnClickListener {
            val fragment = EditLabelDialogFragment()
            fragment.setUnit(mUnit)
            fragment.show(fragmentManager, "edit_label")
        }

        stepImageView.setOnClickListener {
            val fragment = EditStepDialogFragment()
            fragment.setUnit(mUnit)
            fragment.show(fragmentManager, "edit_step")
        }

        fab.setOnClickListener { view -> updateUnit(mUnit, view) }
    }

    fun setUnit(unit: Unit?) {
        val labelTextView = findViewById<TextView>(R.id.labelTextView) as TextView
        val stepTextView = findViewById<TextView>(R.id.stepTextView) as TextView
        val createdTextView = findViewById<TextView>(R.id.createdTextView) as TextView
        val updatedTextView = findViewById<TextView>(R.id.updatedTextView) as TextView

        val formatter = SimpleDateFormat("yyyy/MM/dd HH/mm")

        labelTextView.text = unit!!.label
        stepTextView.text = unit.step.toString()
        createdTextView.text = formatter.format(unit.createdAt)
        updatedTextView.text = formatter.format(unit.updatedAt)
    }

    fun updateUnit(unit: Unit?, view: View) {
        val service = RetrofitFactory.getClient(UnitService::class.java, this@UnitActivity)
        val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("label", unit!!.label!!)
                .addFormDataPart("step", unit.step.toString())
                .build()
        val call = service.updateUnit(unit.id, body)
        call.enqueue(object : BasicCallback<Unit>(this@UnitActivity) {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                super.onResponse(call, response)

                if (response.code() == 200) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                }
            }
        })
    }

    class EditLabelDialogFragment : DialogFragment() {
        private var mEditText: EditText? = null
        private var mUnit: Unit? = null

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val content = inflater.inflate(R.layout.edit_text_dialog, null)
            mEditText = content.findViewById(R.id.editText)
            mEditText!!.setText(mUnit!!.label)

            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Label")
                    .setView(content)
                    .setPositiveButton("OK") { dialogInterface, i ->
                        val label = mEditText!!.text.toString()
                        mUnit!!.label = label
                        (activity as UnitActivity).setUnit(mUnit)
                    }
                    .setNegativeButton("Cancel") { dialogInterface, i -> }
            return builder.create()
        }

        fun setUnit(unit: Unit?) {
            mUnit = unit
        }
    }

    class EditStepDialogFragment : DialogFragment() {
        private var mEditText: EditText? = null
        private var mUnit: Unit? = null

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val content = inflater.inflate(R.layout.edit_text_dialog, null)
            mEditText = content.findViewById(R.id.editText)
            mEditText!!.inputType = InputType.TYPE_CLASS_NUMBER
            mEditText!!.setText(mUnit!!.step.toString())

            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Step size")
                    .setView(content)
                    .setPositiveButton("OK") { dialogInterface, i ->
                        val step = java.lang.Double.valueOf(mEditText!!.text.toString())!!
                        mUnit!!.step = step
                        (activity as UnitActivity).setUnit(mUnit)
                    }
                    .setNegativeButton("Cancel") { dialogInterface, i -> }
            return builder.create()
        }

        fun setUnit(unit: Unit?) {
            mUnit = unit
        }
    }
}
