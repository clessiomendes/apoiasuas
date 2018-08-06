import grails.test.AbstractCliTestCase

class TesteGruposFormulariosTests extends AbstractCliTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testTesteGruposFormularios() {

        execute(["teste-grupos-formularios"])

        assertEquals 0, waitForProcess()
        verifyHeader()
    }
}
