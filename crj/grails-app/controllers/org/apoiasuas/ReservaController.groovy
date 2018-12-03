package org.apoiasuas

import grails.converters.JSON
import org.apoiasuas.agenda.Compromisso
import org.apoiasuas.crj.Reserva
import org.apoiasuas.seguranca.DefinicaoPapeis
import org.apoiasuas.ambienteExecucao.AmbienteExecucao
import org.hibernate.Hibernate
import org.springframework.security.access.annotation.Secured

@Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
class ReservaController extends AncestralController {

    static responseFormats = ['json', 'xml'];

/*
    GET	/books	            index
    GET	/books/${id}	    show
    GET	/books/create	    create
    GET	/books/${id}/edit	edit
    POST	/books	        save
    PUT	/books/${id}	    update
    DELETE	/books/${id}	delete
*/

    def index = {
        if (AmbienteExecucao.desenvolvimento)
            initTest();
        render view: 'calendario';
    }

    def initTest() {
/*
        if (Espaco.countByServicoSistemaSeguranca(segurancaService.servicoLogado) == 0) {
            Espaco auditorio = new Espaco(descricao: 'auditório', servicoSistemaSeguranca: segurancaService.servicoLogado).save();
            Espaco sala1 = new Espaco(descricao: 'sala1', servicoSistemaSeguranca: segurancaService.servicoLogado).save();
            new Reserva(servicoSistemaSeguranca: segurancaService.servicoLogado,
                    tipo: Reserva.Tipo.SIMPLES, descricao: ''
                    dataInicio: new Date(2018, 09, 21), dataFim:
            )
        }
*/
    }

//GET
    def show(Reserva reserva, String servicoSeguranca) {
//        Reserva.get(params.id)
        log.debug('show')
        Hibernate.initialize(reserva.servicoSistemaSeguranca);
//        respond compromisso;
        if (reserva)
            render reserva as JSON
        else
            render status: 404;
    }

    //PUT
    def update(Reserva reserva) {
        log.debug('show')
//        respond compromisso;
        if (reserva)
            render reserva as JSON
        else
            render status: 404;
    }

    //DELETE
    def delete(Reserva reserva) {
//        respond compromisso;
        render status: 204;
    }

}
