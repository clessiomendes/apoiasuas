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

alter table campo_formulario add column lista_logradouros_cidadaos boolean not null DEFAULT FALSE; --prod
create table programa (id int8 not null, version int8 not null, nome varchar(255) not null, primary key (id));
create sequence sq_programa;
alter table programa add column programa_pre_definido varchar(255);
alter table programa add column sigla varchar(10);
create table programa_familia (id int8 not null, version int8 not null, familia_id int8 not null, programa_id int8 not null, primary key (id));
alter table programa_familia drop constraint unique_programa_id;
alter table programa_familia add constraint unique_programa_id  unique (familia_id, programa_id);
alter table programa_familia add constraint FK_hdtekopg2cdltjaqd4ricvi40 foreign key (familia_id) references familia;
alter table programa_familia add constraint FK_fqhnti5xh80b4bilwf9r6379m foreign key (programa_id) references programa;
create sequence sq_programa_familia;
ALTER TABLE familia RENAME COLUMN tecnico_acompanhamento_id TO tecnico_referencia_id;
ALTER TABLE familia DROP COLUMN familia_acompanhada;
alter table definicoes_importacao_familias add column colunabpc varchar(255);
alter table definicoes_importacao_familias add column colunapbf varchar(255);