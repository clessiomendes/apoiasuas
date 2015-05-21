import grails.test.AbstractCliTestCase

class DeployTests extends AbstractCliTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testDeploy() {

        execute(["deploy"])

        assertEquals 0, waitForProcess()
        verifyHeader()
    }
}
