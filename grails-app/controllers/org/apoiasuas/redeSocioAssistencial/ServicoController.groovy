package org.apoiasuas.redeSocioAssistencial

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.plugin.springsecurity.annotation.Secured
import org.apoiasuas.cidadao.Cidadao
import org.apoiasuas.cidadao.CidadaoService
import org.apoiasuas.cidadao.Familia
import org.apoiasuas.fileStorage.FileStorageService
import org.apoiasuas.formulario.FormularioService
import org.apoiasuas.formulario.PreDefinidos
import org.apoiasuas.seguranca.DefinicaoPapeis

@Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
class ServicoController extends AncestralServicoController {

    public final static String PREFIXO_URL_IMAGEM = "data:image/png;base64,";
    public static final String LINHA_ENDERECOS = 'linhaEnderecos'
//    final static String URL_IMAGEM_VAZIA = "iVBORw0KGgoAAAANSUhEUgAAAKAAAACgCAYAAACLz2ctAAAI/UlEQVR4Xu3YaXAV5BWH8Sc3kOVeKqFSBAKMmBAEAQkG0XaQRaxCChSYhE0sO1dj2AwMi0LCotAUI2QoWxNDbSGQCjZIobRUIGUEWjRiBSlimRGFJKJASAIkubdjZBz8YPvhyLzG+fP9nPPyu8/cTBJSVFQURP8k4EggRAE6ktfZOgEFqBCcCihAp/w6rgDVgFMBBeiUX8cVoBpwKqAAnfLruAJUA04FFKBTfh1XgGrAqYACdMqv4wpQDTgVUIBO+XVcAaoBpwIK0Cm/jitANeBUQAE65ddxBagGnAooQKf8Oq4A1YBTAQXolF/HFaAacCqgAJ3y67gCVANOBRSgU34dV4BqwKmAAnTKr+MKUA04FVCATvl1XAGqAacCCtApv44rQDXgVEABOuXXcQWoBpwKKECn/DquANWAUwEF6JRfxxWgGnAqoACd8uu4AlQDTgUUoFN+HVeAasCpgAJ0yq/jClANOBVQgE75dVwBqgGnAgrQKb+OK0A14FRAATrl13EFqAacCihAp/w6rgDVgFMBBeiUX8cVoBpwKqAAnfLruAJUA04FFKBTfh1XgGrAqYACdMqv4wpQDTgVUIBO+XVcAaoBpwIK0Cm/jitANeBUQAE65ddxBagGnAooQKf8Oq4A1YBTAQXolF/HFaAacCqgAJ3y67gCVANOBRSgU34dV4BqwKmAAnTKr+MKUA04FVCA38gfYM/SJ1j8bjsK8xfSxOnH9P09Xq8DrDh7krfPlBIMRtAyNo6YFo1vfFLXOH60mAuVAX7YugMd7ozCQznFRcXQugNcPEF5uY/O3btw7cxx/v3pFe6M70FrX+hNn3SQ0pNHOXm5ET26t+WDoiNcaHY3dwTPUFIWIKZrPI0v/qfuftO4e7n7jtvqZqs++5hj73/I9Vpo2iKGDrEtb+y8zqniYs5XBrkrvgefFBcR1aYz7aKjqK2+zKHD7xAIRtK24z20uj2S2poyjrz5Pq1i23H+7Clq+BH339ee00ePUBIIo2N8PLeH1f8w622AFR8d5bFR04n0+fAEa6iobM5Lf/st9zX08ErKGHJPnCcizEPl9SAzN21hcPNS/D0n8l6Ih0Y+LxUVV2jWDC6W+/DUVFF1vS/b9y+gqSfkxqd68zfgU6zoOZT9QKTXR831KsIjAlyr8dIweI3Kax3J27eau6rO81D/ZMK9XhqGBKmoCCUtfyeDoj3sWz2N57a+g9cbQeBqG4KhJ3h0chbPDItmQe9kDkf6aOCppSKyFdkFObSv2s+wAQsobxCGNyyUisoq4uLg7Mc+qq9WEvtAKuuXJdX7AuttgMcKM0l5cRfPrt1Av5gWlH56icioZkRFHmNor6lMyi6kf6cfcCB7PAt3d2b7rkHM6TmR6gFzyJmbyNqxSfz+chsKtvySkLfWkZy2mZX5e+gaHfnNAcaOYvdv/BzOXcjCbad5+bWNtC0pZNDoLMZm72BYpwhKyj4nEHqJv2xZTd7WYlqOXc2mCV14anhvIhJmkTlrADWXdvHzn71A39QsEqP+SUrWbra9+ipNwqtZPn0EH/jGkj2/cV2AQzI2ML5XFLN7J3E4dhR/zvGzJ2s66495+NPGLAXoSqCy5ARpqZN59xzg9dKpcy9Sps2j0+dr6JOyiZqvPSyO/D2zWfzTiTRMWUf2iI7kjRtOTngCRWtncaE4h6Gpef8/wP6LKJrXh4O5i5iz6xOKCtbC+Z0MTFrGE9k7SGxTwvSn0zhR1ZSRj3Tj0OatVD2+ioIpkYzsO4mEWTk80z+u7mXDE3uS8Issul3cQvorh7722uiOQ8j7VXxdgClrtjGgEzz7xTfwjfuvr0zj12/VKkBX8X11Nxjg+Ht/5+Cbh9i7cweedjPZND+CxIHPMzN3Nw+38930xJN1P4JvZYDdyzYwZlEhea+9QUyj0/j7TSTgz2H96OZMGJxI6yGZpI99ADjH0EeSeXBSFn1C9zI79zCvb/8D3jDPV++9Wv6GAnQe2P94wD/yl5C2fh9zX1pJTHUpS5dkcNujL7DK/yCpSQ9T3j6Z+SMSWPfkDE79eCpbl3dm2i0O8CfV2xk+M5fJC1fQpPh3LP/j2zSv+waMZ9mMUew815QVi6fw0cbprDxwlYFPZzGh2xVGj8vg3icXMCGulowZS4mekEnGsHIF+F0O8Iu3nT7wPJPSd1FdDUnjM5g6ru+XTy47ztK0eez+8AI9Rs5h6eREwhvc+m/ApK5eCjKnsarwX/Qcl879pbnkHn2IgoIphHOBTL+fvWdCSc/JJ2vylz+CZyUnUHHyr8zxL6a4JsDo+WvxP3YP+gb8rtdXz973Ynoqn4V3YcncSVC2j/5JzzFm+WZG9WhVz/4n3+5z6+1vwd8uw63fdnDHy2xYU8Dp8nKgOf2SHmeqfzBNvgd/y7PoKUCLnmbNAgrQTKgFFgEFaNHTrFlAAZoJtcAioAAtepo1CyhAM6EWWAQUoEVPs2YBBWgm1AKLgAK06GnWLKAAzYRaYBFQgBY9zZoFFKCZUAssAgrQoqdZs4ACNBNqgUVAAVr0NGsWUIBmQi2wCChAi55mzQIK0EyoBRYBBWjR06xZQAGaCbXAIqAALXqaNQsoQDOhFlgEFKBFT7NmAQVoJtQCi4ACtOhp1iygAM2EWmARUIAWPc2aBRSgmVALLAIK0KKnWbOAAjQTaoFFQAFa9DRrFlCAZkItsAgoQIueZs0CCtBMqAUWAQVo0dOsWUABmgm1wCKgAC16mjULKEAzoRZYBBSgRU+zZgEFaCbUAouAArToadYsoADNhFpgEVCAFj3NmgUUoJlQCywCCtCip1mzgAI0E2qBRUABWvQ0axZQgGZCLbAIKECLnmbNAgrQTKgFFgEFaNHTrFlAAZoJtcAioAAtepo1CyhAM6EWWAQUoEVPs2YBBWgm1AKLgAK06GnWLKAAzYRaYBFQgBY9zZoFFKCZUAssAgrQoqdZs4ACNBNqgUVAAVr0NGsWUIBmQi2wCChAi55mzQIK0EyoBRYBBWjR06xZQAGaCbXAIqAALXqaNQsoQDOhFlgEFKBFT7NmAQVoJtQCi4ACtOhp1izwX2VHn934fHusAAAAAElFTkSuQmCC";

    ServicoService servicoService
    FormularioService formularioService
    CidadaoService cidadaoService
    def familiaService
    FileStorageService fileStorageService

    static defaultAction = "list"

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def getServico(Long idServico, Long idFamilia, Long idCidadao) {
        Servico servico = Servico.get(idServico)

        if (! servico) {
            response.status = 500
            return render ([errorMessage: "Servico $idServico nao encontrado"] as JSON)
        }

/*
        String endereco = ""
        if (servico.endereco)
            endereco += servico.endereco.toString()
        if (servico.telefones) {
            String telefones = endereco ? ", telefone: " : "telefone: "
            endereco += servico.telefones ? telefones + servico.telefones : ""
        }
*/

        if (servico.encaminhamentoPadrao) {
            Cidadao cidadao = cidadaoService.obtemCidadao(idCidadao)
            Familia familia = familiaService.obtemFamilia(idFamilia)
            servico.encaminhamentoPadrao = servico.encaminhamentoPadrao
                    .replaceAll("(?i)%nome%", cidadao?.getNomeCompleto() ?: "________")
                    .replaceAll("(?i)%endereco%", familia?.endereco?.obtemEnderecoCompleto() ?: "________")
                    .replaceAll("(?i)%telefone%", familia?.getTelefonesToString() ?: "________")
                    .replaceAll("(?i)%nis%", cidadao?.getNis() ?: "________")
        }

        render servico.properties + [ultimaVerificacaoStr: servico.ultimaVerificacao?.format('dd/MM/yyyy')] as JSON
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def list(String palavraChave) {
        params.max = 10
        PagedResultList servicos = servicoService.procurarServico(palavraChave, params);
        //Exibir tela listando todos os serviços que respondem ao criterio buscado
        render view: 'list', model: [servicoInstanceList: servicos, servicoInstanceCount: servicos.totalCount]
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def show(Servico servicoInstance) {
        if (! servicoInstance)
            return notFound()
        servicoService.registraEstatisticaConsultaServico(servicoInstance);
        render view: 'show', model: getModelExibicao(servicoInstance)
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def create() {
        Servico servico = new Servico(params)
        servico.podeEncaminhar = true
        servico.habilitado = true
        servico.ultimaVerificacao = new Date();
        render view: 'create', model: getModelEdicao(servico)
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def save(Servico servicoInstance, String urlImagem) {
        if (! servicoInstance)
            return notFound()

        boolean modoCriacao = servicoInstance.id == null

        servicoInstance.abrangenciaTerritorial = atribuiAbrangenciaTerritorial();

        //converte as linhas individuais de endereço em um campo memo único
        servicoInstance.enderecos = '';
        params.list(LINHA_ENDERECOS)?.each { String linhaEndereco ->
            if (linhaEndereco?.trim())
                servicoInstance.enderecos += linhaEndereco + '\n'
        }
/*
        if (request instanceof MultipartHttpServletRequest) {
            ((MultipartHttpServletRequest)request).getFiles('inputAnexo').each { MultipartFile multipartFile ->
                FileStorageDTO file = null;
                //FIXME: configurar o servidor de aplicacao para vetar arquivos grandes antes que eles cheguem ao servidor e causem grandes danos. Cuidado para nao vetar os tamanhos de arquivos de importacao de bancos de dados
                if (multipartFile.size > FileStorageDTO.MAX_FILE_SIZE)
                    throw new ApoiaSuasException("Tamanho do arquivo (${multipartFile.size}) maior do que o permitido (${FileStorageDTO.MAX_FILE_SIZE})");
                file = new FileStorageDTO(multipartFile.originalFilename, multipartFile.bytes);
            }

        } else {
            throw new ApoiaSuasException("Tipo inesperado de request para upload de arquivos: "+request.getClass().name)
        }
*/
        //Validações:
        boolean validado = servicoInstance.validate();
        validado = validado & validaVersao(servicoInstance);
        //Grava
        if (validado) {
            servicoService.grava(servicoInstance, urlImagem)
        } else {
            //exibe o formulario novamente em caso de problemas na validacao
            return render(view: modoCriacao ? "create" : "edit" , model: getModelEdicao(servicoInstance, urlImagem))
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'servico.label', default: 'Serviço'), servicoInstance.apelido])
        render view: 'show', model: getModelExibicao(servicoInstance)
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def edit(Servico servicoInstance) {
        if (! servicoInstance)
            return notFound()
        render view: 'edit', model: getModelEdicao(servicoInstance)
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def clone(Servico servicoInstance) {
        if (! servicoInstance)
            return notFound()
        String urlImagem = servicoService.getImagemAsString(servicoInstance);
        if (urlImagem)
            urlImagem = PREFIXO_URL_IMAGEM + urlImagem;
        Servico novoServico = new Servico();
        novoServico.properties = servicoInstance.properties;
/*
        novoServico.id = null;
        novoServico.dateCreated = new Date();
*/
        novoServico.with {
            it.id = null;
            it.imagemFileStorage = null;
            it.apelido = null;
            it.nomeFormal = null;
            it.podeEncaminhar = true
            it.habilitado = true
            it.ultimaVerificacao = new Date();
            it.dateCreated = null;
            it.version = 0;
        }
        render view: 'create', model: getModelEdicao(novoServico, urlImagem);
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def delete(Servico servicoInstance) {
        if (! servicoInstance)
            return notFound()

        servicoService.apaga(servicoInstance)
        flash.message = message(code: 'default.deleted.message', args: [message(code: 'servico.label', default: 'Servico'), servicoInstance.apelido])
        redirect action:"list"
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    protected def notFound() {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'servico.label', default: 'Servico'), params.id])
        return redirect(action: "list")
    }

    private LinkedHashMap<String, Object> getModelEdicao(Servico servico, String imagemServico = null) {
        if (! imagemServico) {
            imagemServico = servicoService.getImagemAsString(servico);
            if (imagemServico)
                imagemServico = PREFIXO_URL_IMAGEM + imagemServico
        }
        [servicoInstance: servico,
         imagemServico: imagemServico,
         JSONAbrangenciaTerritorial: getAbrangenciasTerritoriaisEdicao(servico?.abrangenciaTerritorial)]
    }

    private Map<String, Object> getModelExibicao(Servico servico) {
        String imagemServico = servicoService.getImagemAsString(servico);
        if (imagemServico)
            imagemServico = PREFIXO_URL_IMAGEM + imagemServico;
        [servicoInstance: servico,
         imagemServico: imagemServico,
         formularioEncaminhamento: formularioService.getFormularioPreDefinido(PreDefinidos.ENCAMINHAMENTO),
         JSONAbrangenciaTerritorial: getAbrangenciasTerritoriaisExibicao(servico.abrangenciaTerritorial)]
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def download(Servico servico) {
        baixarArquivoDiretamente([servicoService.imprimir(servico)]);
    }

    def imagem(String imagemFileStorage) {
        response.reset();
        response.setContentType('image/png');
        response.setHeader('Content-Disposition','inline');
        response.setHeader('Cache-Control','public; max-age=31536000');
        OutputStream output = response.getOutputStream();
        output.write(servicoService.getImagemAsBytes(imagemFileStorage));
        output.close();
    }

}
