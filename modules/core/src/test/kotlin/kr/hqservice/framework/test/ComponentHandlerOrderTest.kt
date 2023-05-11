package kr.hqservice.framework.test

import be.seeseemelk.mockbukkit.MockBukkit
import io.mockk.junit5.MockKExtension
import io.mockk.spyk
import kr.hqservice.framework.core.component.Component
import kr.hqservice.framework.core.component.HQComponent
import kr.hqservice.framework.core.component.handler.ComponentHandler
import kr.hqservice.framework.core.component.handler.HQComponentHandler
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.reflect.KClass

@ExtendWith(MockKExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class ComponentHandlerOrderTest {
    private lateinit var plugin: HQFrameworkMock
    companion object {
        private val setupOrderResult: MutableList<KClass<out HQComponentHandler<*>>> = mutableListOf()
    }

    interface TestComponentTypeA : HQComponent

    interface TestComponentTypeB : HQComponent

    interface TestComponentTypeC : HQComponent

    @Component
    class TestComponentTypeAImpl : TestComponentTypeA

    @Component
    class TestComponentTypeBImpl : TestComponentTypeB

    @Component
    class TestComponentTypeCImpl : TestComponentTypeC

    @ComponentHandler(depends = [TestComponentTypeCHandler::class])
    class TestComponentTypeAHandler : HQComponentHandler<TestComponentTypeA> {
        override fun setup(element: TestComponentTypeA) {
            setupOrderResult.add(this::class)
        }

        override fun teardown(element: TestComponentTypeA) {}
    }

    @ComponentHandler(depends = [TestComponentTypeAHandler::class, TestComponentTypeCHandler::class])
    class TestComponentTypeBHandler : HQComponentHandler<TestComponentTypeB> {
        override fun setup(element: TestComponentTypeB) {
            setupOrderResult.add(this::class)
        }

        override fun teardown(element: TestComponentTypeB) {}
    }

    @ComponentHandler
    class TestComponentTypeCHandler : HQComponentHandler<TestComponentTypeC> {
        override fun setup(element: TestComponentTypeC) {
            setupOrderResult.add(this::class)
        }

        override fun teardown(element: TestComponentTypeC) {}
    }


    @BeforeEach
    fun setup() {
        MockBukkit.mock()
        plugin = spyk(HQFrameworkMock.mock(), recordPrivateCalls = true)
    }

    @AfterEach
    fun teardown() {
        HQFrameworkMock.unmock()
        MockBukkit.unmock()
    }

    @Test
    fun component_handler_order_sorting_test() {
        assert(setupOrderResult[0] == TestComponentTypeCHandler::class)
        assert(setupOrderResult[1] == TestComponentTypeAHandler::class)
        assert(setupOrderResult[2] == TestComponentTypeBHandler::class)
    }
}