import grails.test.AbstractCliTestCase

class _DeployTests extends AbstractCliTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void test_Deploy() {

        execute(["deploy"])

        assertEquals 0, waitForProcess()
        verifyHeader()
    }
}
