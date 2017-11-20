package com.refrii.client.views.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.refrii.client.*
import com.refrii.client.models.Unit
import com.refrii.client.services.UnitService

import retrofit2.Call
import retrofit2.Response

class UnitsActivity : AppCompatActivity() {

    private var mUnits: MutableList<Unit>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_units)
        val toolbar = findViewById<Toolbar>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val fab = findViewById<FloatingActionButton>(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            val intent = Intent(this@UnitsActivity, NewUnitActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }

        val listView = findViewById<ListView>(R.id.listView) as ListView

        listView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val unit = mUnits!![i]
            val intent = Intent(this@UnitsActivity, UnitActivity::class.java)
            intent.putExtra("unit", unit)
            startActivity(intent)
        }

        setUnits()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val unit = data.getSerializableExtra("unit") as Unit
                mUnits!!.add(unit)
                val listView = findViewById<ListView>(R.id.listView) as ListView
                val adapter = listView.adapter as ArrayAdapter<String>
                adapter.add(unit.label)
                listView.deferNotifyDataSetChanged()
            }
        }
    }

    fun setUnits() {
        val service = RetrofitFactory.getClient(UnitService::class.java, this@UnitsActivity)
        val call = service.units
        call.enqueue(object : BasicCallback<MutableList<Unit>>(this@UnitsActivity) {
            override fun onResponse(call: Call<MutableList<Unit>>, response: Response<MutableList<Unit>>) {
                super.onResponse(call, response)

                if (response.code() == 200) {
                    mUnits = response.body()

                    val adapter = ArrayAdapter<String>(this@UnitsActivity, android.R.layout.simple_list_item_1)
                    for (unit in mUnits!!) {
                        adapter.add(unit.label)
                    }
                    val listView = findViewById<ListView>(R.id.listView) as ListView
                    listView.adapter = adapter
                }
            }
        })
    }

    companion object {
        private val REQUEST_CODE = 1
    }
}
