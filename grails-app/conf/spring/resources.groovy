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

/*
Create Spring bean for Groovy SQL.
groovySql is the name of the bean and can be used
for injection.
*/
    groovySql(groovy.sql.Sql, ref('dataSource'))
}
