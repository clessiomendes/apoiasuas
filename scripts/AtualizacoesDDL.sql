--versao ate aqui: 0.3

create sequence sq_papel start with 8;
create sequence sq_usuario_sistema start with 22;

-- versao ate aqui: 0.3.1 (local:feito, valid:pendente, producao:feito

ALTER TABLE servico ALTER COLUMN apelido TYPE VARCHAR(60) USING apelido::VARCHAR(60);
alter table servico add column descricao varchar;
alter table servico add column pode_encaminhar boolean default TRUE;
update servico set pode_encaminhar = TRUE;
alter table servico add column telefones varchar;
alter table servico add column site varchar;
drop table endereco;
CREATE OR REPLACE FUNCTION remove_acento(text)
  RETURNS text AS
  $BODY$
SELECT TRANSLATE($1,'áàãâäÁÀÃÂÄéèêëÉÈÊËíìîïÍÌÎÏóòõôöÓÒÕÔÖúùûüÚÙÛÜñÑçÇÿýÝ','aaaaaAAAAAeeeeEEEEiiiiIIIIoooooOOOOOuuuuUUUUnNcCyyY')
$BODY$
LANGUAGE sql IMMUTABLE STRICT
COST 100;
COMMENT ON FUNCTION remove_acento(text) IS 'Remove letras com acentuação';