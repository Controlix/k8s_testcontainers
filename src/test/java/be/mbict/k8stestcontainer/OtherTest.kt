package be.mbict.k8stestcontainer

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class OtherTest {

    @Test
    fun alwaysFail(): Unit = Assertions.fail<Nothing>("because we can")
}