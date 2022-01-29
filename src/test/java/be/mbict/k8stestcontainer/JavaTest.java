package be.mbict.k8stestcontainer;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class JavaTest {

    @Test
    public void iShouldFail() {
        Assertions.fail("right away");
    }
}
