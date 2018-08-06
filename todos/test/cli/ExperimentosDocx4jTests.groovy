import grails.test.AbstractCliTestCase

class ExperimentosDocx4jTests extends AbstractCliTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testExperimentosDocx4j() {

        execute(["experimentos-docx4j"])

        assertEquals 0, waitForProcess()
        verifyHeader()
    }
}
