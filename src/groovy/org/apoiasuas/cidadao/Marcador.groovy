package org.apoiasuas.cidadao

import org.apoiasuas.redeSocioAssistencial.ServicoSistema

/**
 * Created by clessio on 24/12/2016.
 */
interface Marcador {

    public ServicoSistema getServicoSistemaSeguranca();
    public void setServicoSistemaSeguranca(ServicoSistema servicoSistemaSeguranca);
    public String getDescricao();
    public void setDescricao(String descricao);
    public Long getId();
    public Boolean getSelected();
    public void setSelected(Boolean selected);

}