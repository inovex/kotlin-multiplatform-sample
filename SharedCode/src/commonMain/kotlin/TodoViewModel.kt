package com.inovex.cpsample.shared

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TodoViewModel(private val repo: TodoRepository = TodoRepository()) {
    private val id: Int = idCounter.getAndIncrement()

    private val api by lazy { Api(repo) }

    fun observeTodos(onChangeCallback: (List<Todo>) -> Unit) {
        repo.observeTodos(id) {
            GlobalScope.launch(mainDispatcher) {
                onChangeCallback(it)
            }
        }
    }

    fun clearTodos() {
        repo.deleteAll()
    }

    fun triggerSync() {
        api.getTodos()
    }

    fun onDestroy() {
        repo.stopObservingTodos(id)
    }
}
