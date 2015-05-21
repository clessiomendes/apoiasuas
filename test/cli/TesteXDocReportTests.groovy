import grails.test.AbstractCliTestCase

class TesteXDocReportTests extends AbstractCliTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testTesteXDocReport() {

        execute(["teste-xd-oc-report"])

        assertEquals 0, waitForProcess()
        verifyHeader()
    }
}
