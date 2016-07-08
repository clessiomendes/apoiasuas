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
create table programa (id int8 not null, version int8 not null, nome varchar(255) not null, primary key (id)); --prod
create sequence sq_programa; --prod
alter table programa add column programa_pre_definido varchar(255); --prod
alter table programa add column sigla varchar(10); --prod
create table programa_familia (id int8 not null, version int8 not null, familia_id int8 not null, programa_id int8 not null, primary key (id)); --prod
alter table programa_familia drop constraint unique_programa_id; --prod
alter table programa_familia add constraint unique_programa_id  unique (familia_id, programa_id); --prod
alter table programa_familia add constraint FK_hdtekopg2cdltjaqd4ricvi40 foreign key (familia_id) references familia; --prod
alter table programa_familia add constraint FK_fqhnti5xh80b4bilwf9r6379m foreign key (programa_id) references programa; --prod
create sequence sq_programa_familia; --prod
ALTER TABLE familia RENAME COLUMN tecnico_acompanhamento_id TO tecnico_referencia_id; --prod
ALTER TABLE familia DROP COLUMN familia_acompanhada; --prod
alter table definicoes_importacao_familias add column colunabpc varchar(255); --prod
alter table definicoes_importacao_familias add column colunapbf varchar(255); --prod

-- versao ate aqui: current (local:feito, valid:feito, producao:feito

alter table usuario_sistema add column matricula varchar(255);
alter table campo_formulario add column exibir_para_preenchimento boolean;

-- versao ate aqui: current (local:feito, producao:feito, valid:feito

update campo_formulario set exibir_para_preenchimento = TRUE;

CREATE OR REPLACE FUNCTION str_2_int(text)
  RETURNS integer AS
  $BODY$
  SELECT cast( REGEXP_REPLACE('0' || COALESCE( $1 ,'0'), '[^0-9]+', '', 'g') as integer)
$BODY$
LANGUAGE sql IMMUTABLE STRICT
COST 100;
COMMENT ON FUNCTION str_2_int(text) IS 'Convert texto para numero ignorando caracteres nao numericos sem levantar excessoes';

-- versao ate aqui: current (local:feito, producao:feito, valid:feito

create table configuracao (id int not null, version int not null, equipamento_nome varchar(80), equipamento_site varchar(80), equipamento_telefone varchar(30), endereco_cep varchar(255), endereco_uf varchar(255), endereco_bairro varchar(255), endereco_complemento varchar(255), endereco_municipio varchar(255), endereco_nome_logradouro varchar(255), endereco_numero varchar(255), endereco_tipo_logradouro varchar(255), primary key (id));
--delete from formulario_emitido where formulario_id in
--                                     ( select id from formulario where formulario_pre_definido in ('GENERICO', 'PLANO_ACOMPANHAMENTO') );
delete from campo_formulario where formulario_id in
                                   ( select id from formulario where formulario_pre_definido in ('GENERICO', 'PLANO_ACOMPANHAMENTO') );
delete from formulario where formulario_pre_definido in ('GENERICO', 'PLANO_ACOMPANHAMENTO');

-- versao ate aqui: current (local:feito, producao:feito, valid:feito

CREATE TABLE abrangencia_territorial
(
  id BIGINT PRIMARY KEY NOT NULL,
  version BIGINT NOT NULL,
  habilitado BOOL NOT NULL,
  nome VARCHAR(255),
  mae_id BIGINT,
  FOREIGN KEY (mae_id) REFERENCES abrangencia_territorial (id)
);
create sequence sq_abrangencia_territorial;

ALTER TABLE public.configuracao RENAME TO servico_sistema;

ALTER TABLE public.servico_sistema RENAME COLUMN equipamento_nome TO nome;
ALTER TABLE public.servico_sistema RENAME COLUMN equipamento_site TO site;
ALTER TABLE public.servico_sistema RENAME COLUMN equipamento_telefone TO telefone;
create sequence sq_servico_sistema;

ALTER TABLE public.servico ALTER COLUMN nome_formal DROP NOT NULL;

alter table servico add column abrangencia_territorial_id int8 null;
alter table servico_sistema add column abrangencia_territorial_id int8 null;
alter table servico add constraint FK_ey6yi8c2853acuaqkpbcl1l1h foreign key (abrangencia_territorial_id) references abrangencia_territorial;
alter table servico_sistema add constraint FK_1o03ry4w1kdm213xnf3l0b1y8 foreign key (abrangencia_territorial_id) references abrangencia_territorial;

alter table servico add column habilitado boolean;
update servico set habilitado = true;
alter table servico_sistema add column habilitado boolean;
update servico_sistema set habilitado = true;

alter table usuario_sistema add column servico_sistema_seguranca_id int8;
alter table usuario_sistema add constraint FK_ityim037jun7r7ij0x73nlspe foreign key (servico_sistema_seguranca_id) references servico_sistema;
update usuario_sistema set servico_sistema_seguranca_id = (select min(id) from servico_sistema);
ALTER TABLE usuario_sistema ALTER COLUMN servico_sistema_seguranca_id SET NOT NULL;

---

alter table cidadao add column servico_sistema_seguranca_id int8 not null default();
alter table familia add column servico_sistema_seguranca_id int8 not null default();
alter table formulario_emitido add column servico_sistema_seguranca_id int8 not null default();
alter table link add column servico_sistema_seguranca_id int8 not null default();
alter table definicoes_importacao_familias add column servico_sistema_seguranca_id int8 not null default();
alter table tentativa_importacao add column servico_sistema_seguranca_id int8 default();
alter table cidadao add constraint FK_dc3xrc7v75llgiylp3codww9d foreign key (servico_sistema_seguranca_id) references servico_sistema;
alter table familia add constraint FK_bmqi116teo3i8ouq4b5f65df1 foreign key (servico_sistema_seguranca_id) references servico_sistema;
alter table formulario_emitido add constraint FK_f8ndymwq3na6dxa8xeebpoo3p foreign key (servico_sistema_seguranca_id) references servico_sistema;
alter table link add constraint FK_9m09qv1t772wq91j4dayxg4ja foreign key (servico_sistema_seguranca_id) references servico_sistema;
alter table definicoes_importacao_familias add constraint FK_p772endoa2o6tg4b717jnisp1 foreign key (servico_sistema_seguranca_id) references servico_sistema;
alter table tentativa_importacao add constraint FK_1oqhcen9f8jisfnvbrrrakoaf foreign key (servico_sistema_seguranca_id) references servico_sistema;
ALTER TABLE definicoes_importacao_familias ADD CONSTRAINT unique_servico_sistema_seguranca_id UNIQUE (servico_sistema_seguranca_id);

ALTER TABLE public.familia DROP CONSTRAINT ;
CREATE UNIQUE INDEX ix_familia_codigo_legado ON familia (codigo_legado, servico_sistema_seguranca_id);

-- versao ate aqui: current (local:feito, producao:feito, validacao:feito

alter table link add column file_label varchar(255);
alter table link add column tipo varchar(255) not null DEFAULT 'URL';
create table file_storage_index (id int8 not null, version int8 not null, bucket varchar(255) not null, nome_arquivo varchar(255) not null, primary key (id));
create sequence sq_file_storage_index;

-- versao ate aqui: current (local:feito, producao:feito

alter table link add column compartilhado_com_id int8;
alter table link add constraint FK_23a8hv1o9otsollmbjddnxqeq foreign key (compartilhado_com_id) references abrangencia_territorial;
