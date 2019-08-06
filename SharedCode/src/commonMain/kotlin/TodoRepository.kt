package com.inovex.cpsample.shared

class TodoRepository {
    fun addTodo(title: String, completed: Boolean) {
        CommonDatabase.addTodo(title, completed)
    }

    fun observeTodos(id: Int, onChangeCallback: (List<Todo>) -> Unit) {
        CommonDatabase.observeTodos(id, onChangeCallback)
    }

    fun stopObservingTodos(id: Int) {
        CommonDatabase.stopObservingTodos(id)
    }

    fun deleteAll() {
        CommonDatabase.deleteAll()
    }
}