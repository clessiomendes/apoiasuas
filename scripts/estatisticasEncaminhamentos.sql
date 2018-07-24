--Levantamento sobre os dados legados (versao PREFERIDA)
-- select a.* , c.apelido, c.id from
  (
    SELECT
      b.conteudo_impresso,
      count(b.id) AS count
    FROM campo_formulario_emitido b
    WHERE b.descricao in ('Nome formal', 'Destino (unidade/entidade/ação)')
    GROUP BY b.conteudo_impresso
  ) a, servico c where a.conteudo_impresso = c.nome_formal
order by a.count desc
;

--Levantamento sobre os dados legados (versao 2)
select b.conteudo_impresso, count(a.id) as frequencia
from formulario_emitido a join campo_formulario_emitido b on (a.id = b.formulario_id)
where a.formulario_pre_definido = 'ENCAMINHAMENTO' and b.descricao in ('Nome formal', 'Destino (unidade/entidade/ação)') and a.data_preenchimento is not null
group by b.conteudo_impresso
order by count(a.id) desc;

--Estatistica para nova tabela agregada
select a.servico_destino_id, a.operador_logado_id, a.data_preenchimento
from formulario_emitido a
where a.servico_sistema_seguranca_id = 1 and data_preenchimento is not null
      and a.formulario_pre_definido = 'ENCAMINHAMENTO'
group by a.servico_destino_id