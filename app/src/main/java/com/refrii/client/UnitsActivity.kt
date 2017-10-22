package com.refrii.client

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast

import retrofit2.Call
import retrofit2.Response

class UnitsActivity : AppCompatActivity() {

    private var mUnits: List<Unit>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_units)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val listView = findViewById(R.id.listView) as ListView

        listView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val unit = mUnits!![i]
            val intent = Intent(this@UnitsActivity, UnitActivity::class.java)
            intent.putExtra("unit", unit)
            startActivity(intent)
        }

        setUnits()
    }

    fun setUnits() {
        val service = RetrofitFactory.getClient(UnitService::class.java, this@UnitsActivity)
        val call = service.units
        call.enqueue(object : BasicCallback<List<Unit>>(this@UnitsActivity) {
            override fun onResponse(call: Call<List<Unit>>, response: Response<List<Unit>>) {
                super.onResponse(call, response)

                if (response.code() == 200) {
                    mUnits = response.body()

                    val adapter = ArrayAdapter<String>(this@UnitsActivity, android.R.layout.simple_list_item_1)
                    for (unit in mUnits!!) {
                        adapter.add(unit.label)
                    }
                    val listView = findViewById(R.id.listView) as ListView
                    listView.adapter = adapter
                }
            }
        })
    }
}
