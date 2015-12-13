package org.apoiasuas.seguranca;

public class DefinicaoPapeis {

	//ATENCAO!!!  ADICIONAR ENTRADA PARA NOVOS PAPEIS EM DOIS LOCAIS: PROPRIEDADE CONSTANTE E NA IMPLEMENTACAO DO METODO GETHIERARQUIA()
	public static final String SUPER_USER = "ROLE_SUPER_USUARIO"
	public static final String ADMINISTRATIVO = "ROLE_ADMINISTRATIVO"  //Atencao! Nao confundir com o papel SUPER_USUARIO
	public static final String COORDENACAO = "ROLE_COORDENACAO"
	public static final String TECNICO = "ROLE_TECNICO"
	public static final String WEB_SERVICE = "ROLE_WEB_SERVICE"
	public static final String RECEPCAO = "ROLE_RECEPCAO"
	public static final String USUARIO = "ROLE_USUARIO"
	public static final String USUARIO_LEITURA = "ROLE_USUARIO_LEITURA"
	//ATENCAO!!!  ADICIONAR ENTRADA PARA NOVOS PAPEIS EM DOIS LOCAIS: PROPRIEDADE CONSTANTE E NA IMPLEMENTACAO DO METODO GETHIERARQUIA()

	private final String pai;
	private final List filhos;

	public String getPai() {
		return pai
	}

	public List getFilhos() {
		return filhos
	}

	/**
	 * Devolve uma lista contendo TODOS os papeis disponiveis e os respectivos papeis englobados (filhos)
	 * @return
	 */
	static List<DefinicaoPapeis> getHierarquia() {
		List papeis = [
//				Listar primeiro os papeis e seus respectivos descendentes exceto o papel SUPER_USER, que possuira todos os outros como descendentes automaticamente
				new DefinicaoPapeis(ADMINISTRATIVO, [USUARIO]),
				new DefinicaoPapeis(COORDENACAO, [USUARIO]),
				new DefinicaoPapeis(TECNICO, [USUARIO]),
				new DefinicaoPapeis(RECEPCAO, [USUARIO]),
				new DefinicaoPapeis(USUARIO, [USUARIO_LEITURA]),
                new DefinicaoPapeis(WEB_SERVICE, []),
				new DefinicaoPapeis(USUARIO_LEITURA, [])
		]
		List filhosSuper = []
		papeis.each { filhosSuper << it.pai }
		papeis.add(new DefinicaoPapeis(SUPER_USER, filhosSuper))
		return papeis
	}

	static List getValues() {
		List result = []
		getHierarquia().each { result.add(it.pai) }
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
				result += formataLinha(papel.pai, filho)
			}
		}
		return result += "'"
	}

	private static String formataLinha(String pai, String filho) {
		return pai + ' > ' + filho + '\n'
	}

	DefinicaoPapeis(String pai, List listaFilhos) {
		this.pai = pai
		this.filhos = listaFilhos;
	}

}

