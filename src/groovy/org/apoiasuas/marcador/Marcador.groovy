package org.apoiasuas.marcador

import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.seguranca.UsuarioSistema

/**
 * Created by clessio on 24/12/2016.
 */
interface Marcador {

    public ServicoSistema getServicoSistemaSeguranca();
    public void setServicoSistemaSeguranca(ServicoSistema servicoSistemaSeguranca);
    public String getDescricao();
    public void setDescricao(String descricao);
    public Long getId();
//    public Boolean getSelected();
//    public void setSelected(Boolean selected);

    //transientes:
    public String getObservacao()
    public void setObservacao(String observacao)
    public Date getData();
    public void setData(Date data);
    public UsuarioSistema getTecnico();
    public void setTecnico(UsuarioSistema tecnico);
    public Boolean getHabilitado();
    public void setHabilitado(Boolean habilitado);
//    public Cidadao getCidadao();
//    public void setCidadao(Cidadao cidadao);
}