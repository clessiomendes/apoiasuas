package org.apoiasuas.marcador

import org.apoiasuas.cidadao.Familia
import org.apoiasuas.seguranca.AuditoriaService
import org.apoiasuas.seguranca.UsuarioSistema

/**
 * Many to Many entre programas e familias
 */
class ProgramaFamilia implements AssociacaoMarcador {

    //Necessario acesso ao servico para executar rotinas de auditoria durante qualquer gravacao
    def auditoriaService;

    Programa programa
    Familia familia
    Date data;
    UsuarioSistema tecnico;
    Boolean habilitado;
    String observacao;

    static belongsTo = [familia: Familia]
    static transients = ['marcador']

    static mapping = {
        id generator: 'native', params: [sequence: 'sq_programa_familia']
        programa fetch: 'join'
    }
    static constraints = {
        programa(nullable: false, unique: ['familia']) //por enquanto, só pode existir uma ligação entre as entidades
        familia(nullable: false)
        tecnico(nullable: false)
    }

    @Override
    Marcador getMarcador() {
        return programa;
    }

    @Override
    void setMarcador(Marcador marcador) {
        programa = marcador;
    }

    def beforeInsert() {
        auditoriaService.auditaPrograma(this, AuditoriaService.Operacao.INCLUSAO);
    }

    def beforeUpdate() {
        auditoriaService.auditaPrograma(this, AuditoriaService.Operacao.ALTERACAO);
    }

    def beforeDelete() {
        auditoriaService.auditaPrograma(this, AuditoriaService.Operacao.EXCLUSAO);
    }

}