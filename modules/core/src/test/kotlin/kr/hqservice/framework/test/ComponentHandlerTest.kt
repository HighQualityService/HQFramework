package kr.hqservice.framework.test

import be.seeseemelk.mockbukkit.MockBukkit
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.spyk
import kr.hqservice.framework.core.HQFrameworkModule
import kr.hqservice.framework.core.HQPlugin
import kr.hqservice.framework.core.component.Component
import kr.hqservice.framework.core.component.HQFactory
import kr.hqservice.framework.core.component.HQModule
import kr.hqservice.framework.core.component.HQSingleton
import kr.hqservice.framework.core.component.error.NoBeanDefinitionsFoundException
import kr.hqservice.framework.core.component.registry.impl.ComponentRegistryImpl
import kr.hqservice.framework.core.extension.print
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.*
import org.koin.ksp.generated.module
import java.util.logging.Logger
import kotlin.reflect.KClass

@OptIn(ExperimentalStdlibApi::class)
@ExtendWith(MockKExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class ComponentHandlerTest : KoinComponent {
    @MockK
    private lateinit var plugin: HQPlugin

    @BeforeEach
    fun setup() {
        MockBukkit.mock()
        startKoin {
            modules(HQFrameworkModule().module)
        }
        every { plugin.logger } returns Logger.getLogger("TEST")
        every { plugin.config } returns mockk()
    }

    interface TestHQModule : HQModule {
        override fun onEnable() {
            println("onEnable, ${this::class.simpleName}")
            assert(true)
        }

        override fun onDisable() {
            println("onDisable, ${this::class.simpleName}")
            assert(true)
        }
    }

    @Component
    class TestComponentA : TestHQModule

    @Component
    class TestComponentB : TestHQModule

    @HQFactory(binds = [TestComponentC::class])
    @Component
    class TestComponentC : TestHQModule

    @Component
    class TestComponentD(testComponentC: TestComponentC) : TestHQModule, KoinComponent {
        private val testComponentG: TestComponentG by inject()

        override fun onEnable() {
            super.onEnable()
            testComponentG.toString().print("testComponentG: ")
        }
    }

    @Component
    class TestComponentE(dummy: Dummy) : TestHQModule

    @Component
    class TestComponentF(componentG: TestComponentG) : TestHQModule

    @HQSingleton(binds = [TestComponentG::class])
    @Component
    class TestComponentG : TestHQModule

    class Dummy

    @Test
    fun component_handler_test() {
        val mock = spyk(ComponentRegistryImpl(plugin), recordPrivateCalls = true)
        every { mock["getAllPluginClasses"]() } returns listOf<Class<*>>(
            TestComponentA::class.java,
            TestComponentB::class.java,
            TestComponentC::class.java,
            TestComponentD::class.java,
            TestComponentE::class.java,
            TestComponentF::class.java,
            TestComponentG::class.java
        )
        try {
            mock.setup()
        } catch (exception: NoBeanDefinitionsFoundException) {
            assert(exception.classes.size == 1)
        }
    }

    @AfterEach
    fun teardown() {
        MockBukkit.unmock()
        stopKoin()
    }
}