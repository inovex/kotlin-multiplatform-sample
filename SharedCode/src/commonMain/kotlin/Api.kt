package com.inovex.cpsample.shared
import io.ktor.client.HttpClient
import io.ktor.client.call.call
import io.ktor.client.request.url
import io.ktor.client.response.readText
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.list

internal class Api(private val repo: TodoRepository) {
    private val client = HttpClient()

    val baseUrl = "https://jsonplaceholder.typicode.com/"

    @Serializable
    data class ApiTodo(val title: String, val completed: Boolean)

    fun getTodos(): Job {
        val url = baseUrl + "todos/"
        return GlobalScope.launch(backgroundDispatcher) {
            val result = client.call {
                url(url)
            }.response.readText()
            val apiTodos = Json.nonstrict.parse(ApiTodo.serializer().list, result)
            CommonDatabase.database.transaction {
                apiTodos.forEach {
                    repo.addTodo(it.title, it.completed)
                }
            }
        }
    }
}
