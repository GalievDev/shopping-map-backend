package dev.ise.shoppingmap.setup

import dev.ise.shoppingmap.setup.util.SharedEnvironments
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class SetupTest {
    companion object {
        @JvmStatic
        @BeforeAll
        fun initEnvironment() {
            SharedEnvironments
        }
    }
}