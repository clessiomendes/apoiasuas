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
alter table link add constraint unique_descricao  unique (servico_sistema_seguranca_id, descricao);

-- versao ate aqui: current (local:feito, producao:feito

create table acao (id serial not null, version int8 default 0 not null, descricao varchar(255) not null, servico_sistema_seguranca_id int8, primary key (id));
create table acao_cidadao (id serial not null, version int8 default 0 not null, acao_id int8 not null, cidadao_id int8 not null, primary key (id));
create table acao_familia (id serial not null, version int8 default 0 not null, acao_id int8 not null, familia_id int8 not null, primary key (id));

alter table acao add constraint FK_evc1r0030i11cfqw9kpghddit foreign key (servico_sistema_seguranca_id) references servico_sistema;
alter table acao_familia add constraint FK_i25ya4068kcl6dcw8jhuow37x foreign key (acao_id) references acao;
alter table acao_familia add constraint FK_a3kgk0xbncj11urcvxr3o3dwf foreign key (familia_id) references familia;

create sequence sq_acao;
create sequence sq_acao_familia;


alter table programa add column servico_sistema_seguranca_id int8;
alter table programa add constraint FK_mw3sdi6y7nc3kwd88r8bpaf5y foreign key (servico_sistema_seguranca_id) references servico_sistema;

INSERT INTO public.acao (id, descricao, servico_sistema_seguranca_id) VALUES (nextval('sq_acao'::regclass), 'Encaminhar para o SCFV 0 a 6', null);
INSERT INTO public.acao (id, descricao, servico_sistema_seguranca_id) VALUES (nextval('sq_acao'::regclass), 'Encaminhar para trabalho protegido/jovem aprendiz', null);
INSERT INTO public.acao (id, descricao, servico_sistema_seguranca_id) VALUES (nextval('sq_acao'::regclass), 'Intervenções técnica para o fortalecimento da capacidade protetiva da família', null);
INSERT INTO public.acao (id, descricao, servico_sistema_seguranca_id) VALUES (nextval('sq_acao'::regclass), 'Intervenções no âmbito da vulnerabilidade relacional', null);
INSERT INTO public.acao (id, descricao, servico_sistema_seguranca_id) VALUES (nextval('sq_acao'::regclass), 'Garantir acesso à documentação civil', null);
INSERT INTO public.acao (id, descricao, servico_sistema_seguranca_id) VALUES (nextval('sq_acao'::regclass), 'Garantir acesso à educação básica', null);
INSERT INTO public.acao (id, descricao, servico_sistema_seguranca_id) VALUES (nextval('sq_acao'::regclass), 'Garantir acesso ao ensino superior', null);
INSERT INTO public.acao (id, descricao, servico_sistema_seguranca_id) VALUES (nextval('sq_acao'::regclass), 'Encaminhar para o SCFV Idoso', null);
INSERT INTO public.acao (id, descricao, servico_sistema_seguranca_id) VALUES (nextval('sq_acao'::regclass), 'Encaminhar para proteção social de alta complexidade', null);
INSERT INTO public.acao (id, descricao, servico_sistema_seguranca_id) VALUES (nextval('sq_acao'::regclass), 'Encaminhar para o CREAS/PAEFI', null);
INSERT INTO public.acao (id, descricao, servico_sistema_seguranca_id) VALUES (nextval('sq_acao'::regclass), 'Encaminhar para inclusão/atualização do Cad Único', null);
INSERT INTO public.acao (id, descricao, servico_sistema_seguranca_id) VALUES (nextval('sq_acao'::regclass), 'Encaminhar para inserção no BPC', null);
INSERT INTO public.acao (id, descricao, servico_sistema_seguranca_id) VALUES (nextval('sq_acao'::regclass), 'Encaminhar para inserção no mercado de trabalho', null);
INSERT INTO public.acao (id, descricao, servico_sistema_seguranca_id) VALUES (nextval('sq_acao'::regclass), 'Encaminhar para qualificação profissional', null);
INSERT INTO public.acao (id, descricao, servico_sistema_seguranca_id) VALUES (nextval('sq_acao'::regclass), 'Encaminhar para rede de proteção à pessoa com deficiência', null);
INSERT INTO public.acao (id, descricao, servico_sistema_seguranca_id) VALUES (nextval('sq_acao'::regclass), 'Encaminhar para rede especializada em álcool e outras drogas', null);
INSERT INTO public.acao (id, descricao, servico_sistema_seguranca_id) VALUES (nextval('sq_acao'::regclass), 'Encaminhar para rede especializada em saúde mental', null);


create table vulnerabilidade (id serial not null, version int8 default 0 not null, descricao varchar(255) not null, servico_sistema_seguranca_id int8, primary key (id));
alter table vulnerabilidade add constraint FK_f12nn7qflvxmp9o5ea8g07741 foreign key (servico_sistema_seguranca_id) references servico_sistema;
create sequence sq_vulnerabilidade;
create sequence sq_vulnerabilidade_familia;

create table vulnerabilidade_familia (id serial not null, version int8 default 0 not null, familia_id int8 not null, vulnerabilidade_id int8 not null, primary key (id));
alter table vulnerabilidade_familia add constraint FK_ky2sxpmuij31lkxx4u87bijda foreign key (familia_id) references familia;
alter table vulnerabilidade_familia add constraint FK_m6nq4mdstbot3murvnfuqbddc foreign key (vulnerabilidade_id) references vulnerabilidade;

INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Membros sem documentação civil completa');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Membro(s) sob ameaça ou com restrição de trânsito pelo território');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Membro(s) com histórico de situação de rua.');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Mulheres vítimas ou com histórico de violência doméstica');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Crianças ou adolescentes vítimas ou com histórico de violência infantil');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Membro sujeito a sobrecarga de cuidado com criança, idoso  ou pessoa com deficiência');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Família elegível ao PBF, mas não recebe o benefício');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Família elegível ao BPC, mas não recebe o benefício');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Família beneficiaria do PBF, em descumprimento de condicionalidades');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Idoso dependente ou semidependente');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Idoso morando sozinho ou como único responsável no domicílio ');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Membros adultos analfabetos');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Membros adultos sem Ensino Fundamental Completo');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Membros adultos sem Ensino Médio Completo');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Membros adultos sem inserção no mercado de trabalho');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Membros adultos com inserção informal no mercado de trabalho');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Criança/adolescento com histórico ou em situação de trabalho infantil ');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Demanda não atendida de qualificação profissional');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Acesso restrito ao direito ao transporte');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Acesso restrito a alimentação básica');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Sem acesso, mesmo que momentâneo, ao SCFV');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Sem acesso, mesmo que momentâneo, ao CREAS/PAEFI');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Sem acesso, mesmo que momentâneo, a serviços da Proteção Social de Alta Complexidade');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Sem acesso, mesmo que momentâneo, a algum serviço da política de Saúde');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Pessoa com deficiência');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Pessoa com sofrimento mental');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Membro com saúde gravemente debilitada (exceto deficiência e sofrimento mental)');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Membros adultos que fazem uso abusivo de álcool ou outras drogas');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Crianças ou adolescentes que fazem uso abusivo de álcool ou outras drogas');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Pai ou mãe menor de 18 anos, ou com filhos em gestação');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Membro familiar no sistema prisional ou egresso do sistema');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Adolescente cumprindo medida ou com passagem pelo sistema sócio-educativo (PSC, LA, semi-liberdade ou restrição de liberdade)');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Criança de 0 a 3 anos fora da educação infantil');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Criança/adolescente de 4 a 17 anos fora da escola');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Domicílio em situação de risco geológico ou construtivo');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Acesso restrito à política de Lazer');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Acesso restrito à política de Cultura');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Família em extrema pobreza  (renda per cápita até 1/4 do salário mínimo)');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Família abaixo da linha da pobreza  (renda per cápita entre 1/4 e 1/2 do salário mínimo)');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Família ou membros familiares em situação de Conflito');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Família ou membros familiares em situação de Preconceito/Discriminação');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Família ou membros familiares em situação de Abandono');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Família ou membros familiares em situação de Apartação');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Família ou membros familiares em situação de Confinamento');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Família ou membros familiares em situação de Isolamento');
INSERT INTO vulnerabilidade (id, descricao) VALUES (nextval('sq_vulnerabilidade'::regclass), 'Família ou membros familiares em situação de Violência');

create table monitoramento (id int8 not null, version int8 not null, data_efetivada timestamp, data_prevista timestamp, data_criacao timestamp not null, efetivado boolean default FALSE, familia int8, memo varchar(1000000), responsavel_id int8 not null, primary key (id));
create index Monitoramento_Familia_Idx on monitoramento (familia);
alter table monitoramento add constraint FK_jq7q15oahmrh09cf3kjcx2hb7 foreign key (familia) references familia;
alter table monitoramento add constraint FK_5m5j2qait364q5w49509r7ujr foreign key (responsavel_id) references usuario_sistema;
create sequence sq_monitoramento;

create table acompanhamento_familiar (id int8 not null, version int8 not null, analise_tecnica varchar(1000000), data_fim timestamp, data_inicio timestamp, familia int8 not null, resultados varchar(1000000), primary key (id));
create index Acompanhamento_Familia_Idx on acompanhamento_familiar (familia);
alter table acompanhamento_familiar add constraint FK_lf3tvhjd34cp11xsa52vbsvq foreign key (familia) references familia;
create sequence sq_acompanhamento_familiar;

alter table acao_familia add column data timestamp;
alter table acao_familia add column habilitado boolean;
alter table acao_familia add column observacao varchar(255);
alter table acao_familia add column tecnico_id int8;
alter table programa add column descricao varchar(255);
alter table programa_familia add column data timestamp;
alter table programa_familia add column habilitado boolean;
alter table programa_familia add column observacao varchar(255);
alter table programa_familia add column tecnico_id int8;
alter table vulnerabilidade_familia add column data timestamp;
alter table vulnerabilidade_familia add column habilitado boolean;
alter table vulnerabilidade_familia add column observacao varchar(255);
alter table vulnerabilidade_familia add column tecnico_id int8;
alter table acao_familia add constraint FK_7au1wpbkne9e5rk03pjuw1t4y foreign key (tecnico_id) references usuario_sistema;
alter table programa_familia add constraint FK_qwr3q6rkm3m36gdse09oidl3j foreign key (tecnico_id) references usuario_sistema;
alter table vulnerabilidade_familia add constraint FK_3eqf3ydyopp2cyuh1hg1vstqk foreign key (tecnico_id) references usuario_sistema;

ALTER TABLE public.programa drop COLUMN descricao;
ALTER TABLE public.programa RENAME COLUMN nome TO descricao;

create table outro_marcador (id int8 not null, version int8 default 0 not null, descricao varchar(255) not null, servico_sistema_seguranca_id int8, primary key (id));
create table outro_marcador_familia (id int8 not null, version int8 default 0 not null, data timestamp, familia_id int8 not null, habilitado boolean, observacao varchar(255), outro_marcador_id int8 not null, tecnico_id int8 not null, primary key (id));
alter table outro_marcador add constraint FK_fsc92pus2rni17wrdiknj9lv6 foreign key (servico_sistema_seguranca_id) references servico_sistema;
alter table outro_marcador_familia add constraint FK_7g2b8gmrdvkcr9sng88pr8pta foreign key (familia_id) references familia;
alter table outro_marcador_familia add constraint FK_nyr2r05ykc6da4v1vx7ojt0x4 foreign key (outro_marcador_id) references outro_marcador;
alter table outro_marcador_familia add constraint FK_rau0oeaqqqgyep4chlhy4c3yg foreign key (tecnico_id) references usuario_sistema;
create sequence sq_outro_marcador;
create sequence sq_outro_marcador_familia;

-- versao ate aqui: current (local:feito
