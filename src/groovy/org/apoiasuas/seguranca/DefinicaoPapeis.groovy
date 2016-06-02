package org.apoiasuas.seguranca;

public class DefinicaoPapeis {

	//ATENCAO!!!  ADICIONAR ENTRADA PARA NOVOS PAPEIS EM DOIS LOCAIS: PROPRIEDADE CONSTANTE E NA IMPLEMENTACAO DO METODO GETHIERARQUIA()
	public static final String STR_SUPER_USER = "ROLE_SUPER_USUARIO"
	public static final String STR_ADMINISTRATIVO = "ROLE_ADMINISTRATIVO"  //Atencao! Nao confundir com o papel SUPER_USUARIO
	public static final String STR_COORDENACAO = "ROLE_COORDENACAO"
	public static final String STR_TECNICO = "ROLE_TECNICO"
	public static final String STR_WEB_SERVICE = "ROLE_WEB_SERVICE"
	public static final String STR_RECEPCAO = "ROLE_RECEPCAO"
	public static final String STR_USUARIO = "ROLE_USUARIO"
	public static final String STR_USUARIO_LEITURA = "ROLE_USUARIO_LEITURA"
	//ATENCAO!!!  ADICIONAR ENTRADA PARA NOVOS PAPEIS EM DOIS LOCAIS: PROPRIEDADE CONSTANTE E NA IMPLEMENTACAO DO METODO GETHIERARQUIA()

	private final String definicaoPapel;
	private final List filhos;

	public String getDefinicaoPapel() {
		return definicaoPapel
	}

	public List getFilhos() {
		return filhos
	}

	/**
	 * Devolve uma lista contendo TODOS os papeis disponiveis e os respectivos papeis englobados (filhos)
	 * @return
	 */
	static List<DefinicaoPapeis> getHierarquia() {
		List<DefinicaoPapeis> papeis = [
//				Listar primeiro os papeis e seus respectivos descendentes exceto o papel SUPER_USER, que possuira todos os outros como descendentes automaticamente
				new DefinicaoPapeis(STR_ADMINISTRATIVO, [STR_USUARIO]),
				new DefinicaoPapeis(STR_COORDENACAO, [STR_USUARIO]),
				new DefinicaoPapeis(STR_TECNICO, [STR_USUARIO]),
				new DefinicaoPapeis(STR_RECEPCAO, [STR_USUARIO]),
				new DefinicaoPapeis(STR_USUARIO, [STR_USUARIO_LEITURA]),
                new DefinicaoPapeis(STR_WEB_SERVICE, []),
				new DefinicaoPapeis(STR_USUARIO_LEITURA, [])
		]
		List filhosSuper = []
		papeis.each { filhosSuper << it.definicaoPapel }
		papeis.add(new DefinicaoPapeis(STR_SUPER_USER, filhosSuper))
		return papeis
	}

	static List getValues() {
		List result = []
		getHierarquia().each { result.add(it.definicaoPapel) }
		return result
	}

	/**
	 * Devolve uma string contendo toda a hierarquia de papeis no formato esperado para configuracao do plugin springsecurity
	 * @return
	 */
	static String getHierarquiaFormatada() {
		String result = "'\n";
		getHierarquia().each { papel ->
			papel?.filhos?.each { filho ->
				result += formataLinha(papel.definicaoPapel, filho)
			}
		}
		return result += "'"
	}

	private static String formataLinha(String pai, String filho) {
		return pai + ' > ' + filho + '\n'
	}

	DefinicaoPapeis(String definicaoPapel, List listaFilhos) {
		this.definicaoPapel = definicaoPapel
		this.filhos = listaFilhos;
	}

}

