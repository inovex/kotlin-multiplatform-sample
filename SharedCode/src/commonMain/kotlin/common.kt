package com.inovex.cpsample.shared

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineDispatcher
import kotlin.native.concurrent.ThreadLocal

expect fun getDriver(): SqlDriver?

@ThreadLocal
expect val mainDispatcher: CoroutineDispatcher

@ThreadLocal
expect val backgroundDispatcher: CoroutineDispatcher

@ThreadLocal
object CommonDatabase {
    var driver: SqlDriver? = getDriver()
    val database: Database by lazy {
        Database(driver!!)
    }

    private val observers: MutableMap<Int, Query.Listener> = mutableMapOf()

    fun addTodo(title: String, completed: Boolean) {
        val completedNum = if (completed) 1L else 0L
        database.databaseQueries.insert(title, completedNum)
    }

    fun observeTodos(id: Int, onChangeCallback: (List<Todo>) -> Unit) {
        if (observers.containsKey(id)) {
            throw RuntimeException("Already observing with id $id")
        } else {
            val listener = object : Query.Listener {
                override fun queryResultsChanged() {
                    onChangeCallback(database.databaseQueries.selectAll().executeAsList())
                }
            }
            observers[id] = listener
            database.databaseQueries.selectAll().addListener(listener)
        }
    }

    fun stopObservingTodos(id: Int) {
        observers[id]?.let {
            database.databaseQueries.selectAll().removeListener(it)
        }
    }

    fun deleteAll() {
        database.databaseQueries.deleteAll()
    }
}

internal val idCounter = atomic(0)
