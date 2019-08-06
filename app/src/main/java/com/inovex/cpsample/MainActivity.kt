package com.inovex.cpsample

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.inovex.cpsample.shared.CommonDatabase
import com.inovex.cpsample.shared.Database
import com.inovex.cpsample.shared.TodoViewModel
import com.squareup.sqldelight.android.AndroidSqliteDriver


class MainActivity : AppCompatActivity() {
    val todoList by lazy { findViewById<ListView>(R.id.list_todo) }
    var adapter: ArrayAdapter<String>? = null

    private val viewModel = TodoViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Init driver (with multiple activities, this should be moved to the Application class)
        val driver = AndroidSqliteDriver(Database.Schema, this.applicationContext, "main.db")
        CommonDatabase.driver = driver

        viewModel.observeTodos { todos ->
            if (adapter == null) {
                adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, todos.map { it.title })
                todoList.adapter = adapter
            } else {
                adapter!!.apply {
                    clear()
                    todos.forEach { insert(it.title, count) }
                    notifyDataSetChanged()
                }
            }
            Toast.makeText(this, "Updated TODO list", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_sync -> {
                viewModel.triggerSync()
                true
            }
            R.id.action_clear -> {
                viewModel.clearTodos()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        viewModel.onDestroy()
        super.onDestroy()
    }
}
