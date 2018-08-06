package org.apoiasuas.marcador

import org.apoiasuas.cidadao.Familia
import org.apoiasuas.marcador.Marcador
import org.apoiasuas.seguranca.UsuarioSistema

/**
 * Created by clessio on 24/12/2016.
 */
interface AssociacaoMarcador {

    public Marcador getMarcador();
    public void setMarcador(Marcador marcador);
    public Familia getFamilia();
    public void setFamilia(Familia marcador);
    public String getObservacao()
    public void setObservacao(String observacao)
    public Date getData();
    public void setData(Date data);
    public UsuarioSistema getTecnico();
    public void setTecnico(UsuarioSistema tecnico);
    public Boolean getHabilitado();
    public void setHabilitado(Boolean habilitado);

}