package com.inovex.cpsample.shared

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.ios.NativeSqliteDriver
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import platform.Foundation.NSRunLoop
import platform.Foundation.performBlock
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_global_queue
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_queue_t
import platform.posix.QOS_CLASS_BACKGROUND
import kotlin.coroutines.CoroutineContext

actual fun getDriver(): SqlDriver? {
    return NativeSqliteDriver(Database.Schema, "main.db")
}

// see https://github.com/Kotlin/kotlinx.coroutines/issues/470#issuecomment-414635811
actual val mainDispatcher = object : CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        dispatch_async(dispatch_get_main_queue()) {
            block.run()
        }
    }
}


// At the moment, background threads for corotuines are not supported in Kotlin Native :(
// see https://github.com/Kotlin/kotlinx.coroutines/issues/462
actual val backgroundDispatcher = mainDispatcher

