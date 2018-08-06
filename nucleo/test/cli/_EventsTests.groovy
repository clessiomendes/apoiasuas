import grails.test.AbstractCliTestCase

class _EventsTests extends AbstractCliTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void test_Events() {

        execute(["events"])

        assertEquals 0, waitForProcess()
        verifyHeader()
    }
}
