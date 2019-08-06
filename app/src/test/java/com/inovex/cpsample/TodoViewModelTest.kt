package com.inovex.cpsample

import com.inovex.cpsample.shared.Todo
import com.inovex.cpsample.shared.TodoRepository
import com.inovex.cpsample.shared.TodoViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class TodoViewModelTest {
    private val mockedRepo = mockk<TodoRepository>(relaxed = true)
    private val viewModel  = TodoViewModel(mockedRepo)

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Before
    fun init() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @Test
    fun testObserveTodos() {
        val latch = CountDownLatch(1)
        var resultList: List<Todo>? = null
        val mockedTodo = mockk<Todo>()
        every { mockedRepo.observeTodos(any(), any()) } answers {
            secondArg<(List<Todo>) -> Unit>().invoke(listOf(mockedTodo))
        }
        viewModel.observeTodos {
            resultList = it
            latch.countDown()
        }
        latch.await(500L, TimeUnit.MILLISECONDS)
        assert(resultList != null)
    }

    @Test
    fun testClearTodos() {
        viewModel.clearTodos()
        verify {
            mockedRepo.deleteAll()
        }
    }

    @Test
    fun testOnDestroy() {
        viewModel.onDestroy()
        verify { mockedRepo.stopObservingTodos(any()) }
    }
}