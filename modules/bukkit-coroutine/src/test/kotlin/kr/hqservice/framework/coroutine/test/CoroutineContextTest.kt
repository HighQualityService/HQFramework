package kr.hqservice.framework.coroutine.test

import be.seeseemelk.mockbukkit.MockBukkit
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.*
import kr.hqservice.framework.bukkit.core.HQBukkitPlugin
import kr.hqservice.framework.bukkit.core.component.*
import kr.hqservice.framework.global.core.extension.print
import kr.hqservice.framework.test.HQFrameworkBukkitMock
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@ExtendWith(MockKExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class CoroutineContextTest : KoinComponent {
    private lateinit var plugin: HQBukkitPlugin
    private val testCoroutineContext: TestCoroutineScope by inject()

    @BeforeEach
    fun setup() {
        MockBukkit.mock()
        plugin = HQFrameworkBukkitMock.mock("CoroutineContextTest")
    }

    @Test
    fun coroutine_context_exception_handling_test(): Unit = runBlocking {
        assertDoesNotThrow {
            testCoroutineContext.launch {
                throw Exception("caught exception and not propagate to the parent coroutine")
            }
        }
    }

    @Test
    fun coroutine_supervisor_waiting_flow_test(): Unit = runBlocking {
        CoroutineScope(Dispatchers.Default).launch {
            delay(100)
            val children = testCoroutineContext.getSupervisor().children
            val first = children.first()
            val last = children.last()
            children.count().print("2 == ")
            delay(300)
            assert(first.isCompleted)
            assert(!last.isCompleted)
            delay(200)
            assert(false)
        }

        testCoroutineContext.launch {
            delay(300)
            assert(true)
        }

        testCoroutineContext.launch {
            delay(500)
            assert(true)
        }

        testCoroutineContext.getSupervisor().children.forEach {
            it.join()
        }
    }

    @AfterEach
    fun teardown() {
        HQFrameworkBukkitMock.unmock()
        MockBukkit.unmock()
    }
}