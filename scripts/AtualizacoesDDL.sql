--versao ate aqui: 0.3

create sequence sq_papel start with 8; --valid prod
create sequence sq_usuario_sistema start with 22; --valid prod

-- versao ate aqui: 0.3.1 (local:feito, valid:feito, producao:feito

ALTER TABLE servico ALTER COLUMN apelido TYPE VARCHAR(60) USING apelido::VARCHAR(60); --valid prod
alter table servico add column descricao varchar; --valid prod
alter table servico add column pode_encaminhar boolean default TRUE; --valid prod
update servico set pode_encaminhar = TRUE; --valid prod
alter table servico add column telefones varchar; --valid prod
alter table servico add column site varchar; --valid prod
drop table endereco; --valid prod
CREATE OR REPLACE FUNCTION remove_acento(text)
  RETURNS text AS
  $BODY$
SELECT TRANSLATE($1,'áàãâäÁÀÃÂÄéèêëÉÈÊËíìîïÍÌÎÏóòõôöÓÒÕÔÖúùûüÚÙÛÜñÑçÇÿýÝ','aaaaaAAAAAeeeeEEEEiiiiIIIIoooooOOOOOuuuuUUUUnNcCyyY')
$BODY$
LANGUAGE sql IMMUTABLE STRICT
COST 100; --valid prod
COMMENT ON FUNCTION remove_acento(text) IS 'Remove letras com acentuação'; --valid prod
create sequence sq_papel; --valid prod
create sequence sq_usuario_sistema; --valid prod

-- versao ate aqui: 0.3.2 (local:feito, valid:feito, producao:feito
