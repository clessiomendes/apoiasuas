select b.codigo_legado, b.id, b.data_ultima_importacao, count(a.id) from
  familia b join cidadao a on (a.familia_id = b.id and a.parentesco_referencia = 'REFERENCIA')
group by b.codigo_legado, b.id, b.data_ultima_importacao
HAVING count(a.id) > 1
order by b.data_ultima_importacao desc;

