import grails.test.AbstractCliTestCase

class DeployProdTests extends AbstractCliTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testDeployProd() {

        execute(["deploy-prod"])

        assertEquals 0, waitForProcess()
        verifyHeader()
    }
}
