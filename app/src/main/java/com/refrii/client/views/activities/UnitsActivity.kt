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
import com.refrii.client.factories.RetrofitFactory
import com.refrii.client.models.Unit
import com.refrii.client.services.UnitService

import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

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

    private fun setUnits() {
        RetrofitFactory.getClient(UnitService::class.java, this@UnitsActivity)
                .getUnits()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: Subscriber<List<Unit>>() {
                    override fun onError(e: Throwable?) {
                    }

                    override fun onCompleted() {
                    }

                    override fun onNext(t: List<Unit>) {
                        mUnits = t.toMutableList()

                        val adapter = ArrayAdapter<String>(this@UnitsActivity, android.R.layout.simple_list_item_1)
                        t.forEach { adapter.add(it.label) }

                        val listView = findViewById<ListView>(R.id.listView) as ListView
                        listView.adapter = adapter
                    }
                })
    }

    companion object {
        private val REQUEST_CODE = 1
    }
}
