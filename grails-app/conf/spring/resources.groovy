import org.apoiasuas.ConfiguracaoService
import org.apoiasuas.importacao.ImportacaoFamiliasController
import org.apoiasuas.importacao.ImportarFamiliasBHService
import org.apoiasuas.importacao.ImportarFamiliasService
import org.apoiasuas.services.ImportarFamiliasJavaService

// Place your Spring DSL code here
beans = {
/*
	importarFamiliasJava(ImportarFamiliasJavaService) {
		daoForJavaService = ref("daoForJavaService")
		importarFamiliasJava = ref("importarFamiliasJava")
		segurancaService = ref("segurancaService")
	}
*/
//	roleHierarchy(RoleHierarchyImpl)

    /**
     * Escolher a implementacao a usar para o servico de importacao de familias
     */
    servicoImportarFamilias(ImportarFamiliasBHService) { bean ->
        bean.autowire = 'byName'
    }

/*
Create Spring bean for Groovy SQL.
groovySql is the name of the bean and can be used
for injection.
*/
    groovySql(groovy.sql.Sql, ref('dataSource'))
//    sintaxeSql(org.apoiasuas.util.SintaxeSQL)
}
