import org.camunda.bpm.engine.RuntimeService
import org.camunda.bpm.engine.TaskService
import grails.plugin.camunda.test.SampleService
import org.camunda.bpm.engine.runtime.Execution
import org.camunda.bpm.engine.test.mock.Mocks
import spock.lang.Specification

/**
 * Integration Test for camunda PedidoCertidaoProcess 
 */
class PedidoCertidaoProcessSpec extends Specification {

    /**
     * 1) Inject camunda process engine API service beans
     */
    RuntimeService runtimeService
    TaskService taskService

    /**
     * 2) Mock your Grail(s) services called from PedidoCertidaoProcess
     */
    def samplePedidoCertidaoProcessService = Mock(SampleService)

    /**
     * 3) Register your service mocks to make them accessible via PedidoCertidaoProcess
     */
    def setup() {
        Mocks.register("sampleService", samplePedidoCertidaoProcessService)
    }

    def cleanup() {
        Mocks.reset()
    }

    /**
     * 4) Test the various aspects and behaviour of PedidoCertidaoProcess
     */
    void "Testing a happy walk through PedidoCertidaoProcess"() {

        given: "a new instance of PedidoCertidaoProcess"
        runtimeService.startProcessInstanceByKey("PedidoCertidaoProcess")

        when: "completing the user task"
        def task = taskService.createTaskQuery().singleResult()
        taskService.complete(task.id)

        then: "the service method defined for the subsequent service task was called exactly once"
        1 * samplePedidoCertidaoProcessService.serviceMethod(_ as Execution)

        and: "nothing else was called"
        0 * _

        and: "the process instance finished"
        !runtimeService.createProcessInstanceQuery().singleResult()

    }

}
