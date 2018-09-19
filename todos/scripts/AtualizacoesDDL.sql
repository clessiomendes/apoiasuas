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

-- versao ate aqui: current (local:feito, producao: feito)

alter table monitoramento add column prioritario boolean;
alter table cidadao add column habilitado boolean not null DEFAULT true;
alter table cidadao alter column habilitado DROP DEFAULT;
ALTER TABLE public.cidadao ALTER COLUMN familia_id SET NOT NULL;

alter table servico_sistema add column acesso_seguranca_cadastro_detalhado boolean DEFAULT false;
alter table servico_sistema add column acesso_seguranca_inclusao_familia boolean DEFAULT false;
alter table servico_sistema add column acesso_seguranca_inclusao_membro_familiar boolean DEFAULT false;

-- versao ate aqui: current (local:feito, producao: feito)

alter table acao add column habilitado boolean DEFAULT true;
alter table outro_marcador add column habilitado boolean DEFAULT true;
alter table programa add column habilitado boolean DEFAULT true;
alter table vulnerabilidade add column habilitado boolean DEFAULT true;

alter table monitoramento add column suspenso boolean DEFAULT false;

alter table servico_sistema add column acesso_seguranca_pedidos_certidao boolean DEFAULT false;
alter table servico_sistema add column acesso_seguranca_plano_acompanhamento boolean DEFAULT false;

update servico_sistema set acesso_seguranca_inclusao_membro_familiar = true;
update servico_sistema set acesso_seguranca_pedidos_certidao = true;
update servico_sistema set acesso_seguranca_plano_acompanhamento = true;

alter table servico_sistema add column acesso_seguranca_identificacao_pelo_codigo_legado boolean DEFAULT false;
update servico_sistema set acesso_seguranca_identificacao_pelo_codigo_legado = true;

-- mudando o mapeamento de companhamento familiar para associacao simples
alter table familia add column acompanhamento_familiar_id int8 null;
alter table familia add constraint FK_gb4i0mpvmugpxggdw5y7hqbac foreign key (acompanhamento_familiar_id) references acompanhamento_familiar;
update familia a set acompanhamento_familiar_id = (select id from acompanhamento_familiar b where a.id = b.familia) where a.id in (select familia from acompanhamento_familiar);

_log create table log (id int8 not null, inicio timestamp not null, request varchar(255), parametros varchar(255), valores_parametros varchar(1000), username varchar(255), session_id varchar(255), version int8 not null, jvmmax_memory0 float4, jvmmax_memory1 float4, jvmused_memory0 float4, jvmused_memory1 float4, code_cache0 float4, code_cache1 float4, duracaoms int8, eden_space0 float4, eden_space1 float4, free_physical_memory_size0 float4, free_physical_memory_size1 float4, perm_gen0 float4, perm_gen1 float4, process_cpu_time0 float4, process_cpu_time1 float4, survivor_space0 float4, survivor_space1 float4, tenured_gen0 float4, tenured_gen1 float4, total_physical_memory_size0 float4, total_physical_memory_size1 float4, primary key (id));
_log create sequence sq_log;

-- versao ate aqui: current (local:feito, producao: feito)

alter table telefone add column obs varchar(1000);
ALTER TABLE public.telefone DROP criador_id;
ALTER TABLE public.telefone DROP ultimo_alterador_id;

ALTER TABLE public.telefone ALTER COLUMN numero SET NOT NULL;

-- versao ate aqui: current (local:feito, producao: feito)

DROP INDEX unique_familia_id CASCADE;
alter table cidadao drop constraint unique_familia_id;
CREATE UNIQUE INDEX unique_familia_id ON cidadao (lower(nome_completo), familia_id);

create table atendimento_particularizado (id int8 not null, version int8 not null, data_hora timestamp, nome_cidadao varchar(255), compareceu boolean, familia_id int8, servico_sistema_seguranca_id int8 not null, tecnico_id int8, telefone_contato varchar(255), primary key (id));
create table compromisso (id int8 not null, version int8 not null, descricao varchar(255) not null, inicio timestamp not null, fim timestamp not null, responsavel_id int8, tipo varchar(255) not null, servico_sistema_seguranca_id int8 not null, atendimento_particularizado_id int8, habilitado boolean not null, primary key (id));
alter table atendimento_particularizado add constraint FK_l284tdstdu00cr3cfs3emq3er foreign key (familia_id) references familia;
alter table atendimento_particularizado add constraint FK_ly9f1273nupef57bjjv1ydw83 foreign key (servico_sistema_seguranca_id) references servico_sistema;
alter table atendimento_particularizado add constraint FK_2lp7id10sf6cnt2fr3lpovgal foreign key (tecnico_id) references usuario_sistema;
alter table compromisso add constraint ck_inicio_fim_compromisso CHECK (fim > inicio);
alter table compromisso add constraint ck_atendimento_particularizado CHECK (tipo != 'ATENDIMENTO_PARTICULARIZADO' or (tipo = 'ATENDIMENTO_PARTICULARIZADO' and atendimento_particularizado_id is not null));

alter table compromisso add constraint FK_6jv5upwhyw8hde3b3kraeafxy foreign key (atendimento_particularizado_id) references atendimento_particularizado;
alter table compromisso add constraint FK_eld7ltohoh9mirxbaru8889cl foreign key (responsavel_id) references usuario_sistema;
alter table compromisso add constraint FK_67ye2ii152dpky5cqu114j9pb foreign key (servico_sistema_seguranca_id) references servico_sistema;
create sequence sq_atendimento_particularizado;
create sequence sq_compromisso;

ALTER TABLE public.atendimento_particularizado ADD sem_telefone BOOL NULL;

-- versao ate aqui: current (local:feito, producao: feito)

ALTER TABLE public.usuario_sistema ADD configuracao_agenda VARCHAR(10000) NULL;
ALTER TABLE public.atendimento_particularizado ADD familia_sem_cadastro BOOL NULL;

-- versao ate aqui: current (local:feito, producao: feito)

create table compromisso_usuario_sistema (compromisso_participantes_id int8 not null, usuario_sistema_id int8 not null, participantes_idx int4 not null);
alter table compromisso_usuario_sistema add constraint FK_asamgomnny9f67otreqkfeikg foreign key (usuario_sistema_id) references usuario_sistema;
alter table compromisso_usuario_sistema add constraint FK_yebda03yqels82367fbol12f foreign key (compromisso_participantes_id ) references compromisso;

insert into compromisso_usuario_sistema select id, responsavel_id, 0 from compromisso where responsavel_id is not null;

-- versao ate aqui: current (local:feito, producao: feito)

/*
CREATE TABLE ambiente(id INT PRIMARY KEY, descricao VARCHAR(255));
INSERT into ambiente (id, descricao) values (0, );
_log CREATE TABLE ambiente(id INT PRIMARY KEY, descricao VARCHAR(255));
_log INSERT into ambiente (id, descricao) values (0, );
*/
alter table cidadao rename constraint unique_familia_id to unique_nome_completo;
DROP INDEX public.unique_familia_id CASCADE;
CREATE UNIQUE INDEX unique_nome_completo ON cidadao (lower(nome_completo), familia_id);

CREATE INDEX inicio_index ON public.compromisso (inicio);
CREATE INDEX fim_index ON public.compromisso (fim);
create table modelo_formulario (id int8 not null, version int8 not null, arquivo bytea, descricao varchar(255), formulario_id int8, padrao boolean not null, primary key (id));
alter table modelo_formulario add constraint FK_il6iv2mtvadvdfm83wyd171h4 foreign key (formulario_id) references formulario;
create sequence sq_modelo_formulario;
alter table servico_sistema add column email varchar(255);

-- versao ate aqui: current (local:feito, producao: feito)

alter table campo_formulario add column opcoes varchar(4096);
ALTER TABLE public.cidadao ALTER COLUMN nome_completo SET NOT NULL;
alter table cidadao add column detalhes varchar(1000000);
alter table familia add column detalhes varchar(1000000);
alter table cidadao add column data_nascimento_aproximada timestamp;
CREATE INDEX data_nascimento_index ON public.cidadao (data_nascimento);
CREATE INDEX data_nascimento_aprox_index ON public.cidadao (data_nascimento_aproximada);
alter table abrangencia_territorial add column codigo_customizacoes varchar(255);
--CUIDADO: confirmar os ids em cada ambiente
update abrangencia_territorial set codigo_customizacoes = 'BELO_HORIZONTE' where id =; --2;
update abrangencia_territorial set codigo_customizacoes = 'BELO_HORIZONTE_HAVAI_VENTOSA' where id =; --4;
update abrangencia_territorial set codigo_customizacoes = 'BELO_HORIZONTE_VISTA_ALEGRE' where id =; --5;
alter table cidadao add column nome_social varchar(255);
alter table cidadao add column analfabeto boolean;
alter table cidadao add column escolaridade varchar(255);
alter table cidadao add column sexo varchar(20);
alter table familia add constraint unique_codigo_legado  unique (servico_sistema_seguranca_id, codigo_legado);
alter table familia add column bolsa_familia boolean;
alter table familia add column ex_bolsa_familia boolean;
alter table familia add column bpc boolean;

-- versao ate aqui: current (local:feito, producao: feito)

create table auditoria (id int8 not null, version int8 not null, cidadao_id int8, criador_id int8 not null, date_created timestamp not null, descricao varchar(1024), detalhes varchar(1000000), familia_id int8, servico_sistema_seguranca_id int8 not null, tipo varchar(255) not null, primary key (id));
create index Auditoria_Cidadao_Idx on auditoria (cidadao_id);
create index Auditoria_Criador_Idx on auditoria (criador_id);
create index Auditoria_Familia_Idx on auditoria (familia_id);
create index Auditoria_Servico_Sistema_Idx on auditoria (servico_sistema_seguranca_id);
create index Auditoria_Tipo_Idx on auditoria (tipo);
alter table auditoria add constraint FK_a0ysw90h9e6kxlb6i725yhejx foreign key (cidadao_id) references cidadao;
alter table auditoria add constraint FK_fawr6jplcj4ofytc4okmuxtke foreign key (criador_id) references usuario_sistema;
alter table auditoria add constraint FK_k0yccasy4806gb716pi2gbv0x foreign key (familia_id) references familia;
alter table auditoria add constraint FK_q414r0tvmcqy0xenwn6vdnwlw foreign key (servico_sistema_seguranca_id) references servico_sistema;
create sequence sq_auditoria;

-- criacao de auditorias aa partir de registros ja existentes em outras entidades (programaFamilia)
select 0,
  -- select nextval('sq_auditoria'),
  0 as version, tecnico_id, data,
  'Família inserida em ' || c.descricao as descricao,
  a.familia_id, b.servico_sistema_seguranca_id, 'PROGRAMA_FAMILIA' as tipo
from programa_familia a join usuario_sistema b on (a.tecnico_id = b.id)
  join programa c on (a.programa_id = c.id)
where a.habilitado is true;

-- versao ate aqui: current (local:feito, producao: feito)

alter table servico_sistema add column acesso_seguranca_inibir_atendimento_apos varchar(255);
update servico_sistema set acesso_seguranca_inibir_atendimento_apos = '2,4' where id = 1;
/*
select familia, ddd, numero, count(*), max(id)
from telefone group by familia, ddd, numero
having count(*) > 1
*/
alter table telefone add constraint unique_numero  unique (familia, ddd, numero);

alter table servico add column imagem_file_storage varchar(255);
alter table servico add column documentos varchar(1000000);
alter table servico add column contatos_internos varchar(1000000);
alter table servico add column fluxo varchar(1000000);
alter table servico add column enderecos varchar(1000000);
alter table servico add column publico varchar(1000000);
--ALTER TABLE public.servico ALTER COLUMN telefones TYPE VARCHAR(1000000) USING telefones::VARCHAR(1000000);

update servico set enderecos = trim(
    COALESCE(endereco_tipo_logradouro || ' ' || endereco_nome_logradouro, '') ||
    COALESCE(', ' || endereco_numero, '') ||
    COALESCE(', ' || servico.endereco_complemento, '') ||
    COALESCE(', ' || servico.endereco_bairro, '')
--   || COALESCE(', ' || servico.endereco_municipio, '') ||
--    COALESCE(', ' || servico.endereco_uf, '') ||
--    COALESCE(', CEP ' || servico.endereco_cep, '')
);

/*
alter table file_storage_index add column descricao varchar(10000);
alter table file_storage_index add column date_created timestamp;
create table servico_file_storage_index (servico_anexos_id int8, file_storage_index_id int8);
alter table servico_file_storage_index add constraint FK_22bpptv6sgpg2mtmxpbvk0r3q foreign key (file_storage_index_id) references file_storage_index;
alter table servico_file_storage_index add constraint FK_b3vwr3i9jx7fhchjcqs0wt4ha foreign key (servico_anexos_id) references servico;

FIXME: RODAR SOMENTE APÓS TESTAR O UPDATE ANTERIOR
ALTER TABLE public.servico DROP endereco_cep;
ALTER TABLE public.servico DROP endereco_uf;
ALTER TABLE public.servico DROP endereco_bairro;
ALTER TABLE public.servico DROP endereco_complemento;
ALTER TABLE public.servico DROP endereco_municipio;
ALTER TABLE public.servico DROP endereco_nome_logradouro;
ALTER TABLE public.servico DROP endereco_numero;
ALTER TABLE public.servico DROP endereco_tipo_logradouro;
*/

alter table formulario_emitido add column servico_destino_id int8;
alter table formulario_emitido add constraint FK_d6wuh45ei1ntdldea847h9pj5 foreign key (servico_destino_id) references servico;

alter table servico add column ultima_verificacao timestamp;
alter table servico add column date_created timestamp;
alter table servico add column last_updated timestamp;

alter table atendimento_particularizado add column observacoes_agendamento varchar(1000000);

-- versao ate aqui: current (local:feito, producao: feito)

create table estatistica_consulta_servico (id int8 not null, version int8 not null, mes timestamp not null, quantidade int8 not null, servico_id int8 not null, servico_sistema_seguranca_id int8, usuario_sistema_id int8 not null, primary key (id));
create table estatistica_encaminhamento (id int8 not null, version int8 not null, mes timestamp not null, quantidade int8 not null, servico_id int8 not null, servico_sistema_seguranca_id int8, usuario_sistema_id int8 not null, primary key (id));
create index idx_estatistica_cs_m on estatistica_consulta_servico (mes);
create index idx_estatistica_cs_ss on estatistica_consulta_servico (servico_sistema_seguranca_id);
create index idx_estatistica_e_m on estatistica_encaminhamento (mes);
create index idx_estatistica_e_ss on estatistica_encaminhamento (servico_sistema_seguranca_id);
alter table estatistica_consulta_servico add constraint FK_8bf7ntujb4m9gq3etrhwcarre foreign key (servico_id) references servico;
alter table estatistica_consulta_servico add constraint FK_mip3k86o0y9g1gia37k8vyc9s foreign key (servico_sistema_seguranca_id) references servico_sistema;
alter table estatistica_consulta_servico add constraint FK_1dgtr6bhohn4y8udsqeioi9n8 foreign key (usuario_sistema_id) references usuario_sistema;
alter table estatistica_encaminhamento add constraint FK_kxqpc4gcn7p6dd5e8aq5d6fjf foreign key (servico_id) references servico;
alter table estatistica_encaminhamento add constraint FK_g1egodpi1c11o57tiu7pbip0j foreign key (servico_sistema_seguranca_id) references servico_sistema;
alter table estatistica_encaminhamento add constraint FK_rrh7dhjyllg5k0k9thv12esah foreign key (usuario_sistema_id) references usuario_sistema;
create sequence sq_estatistica_consulta_servico;
create sequence sq_estatistica_encaminhamento;

-- versao ate aqui: current (casa:feito, notebook: feito, producao: feito, demo:feito)

alter table compromisso add column dia_inteiro boolean default FALSE;
--alter table compromisso drop constraint ck_inicio_fim_compromisso;
--alter table compromisso add constraint ck_inicio_fim_compromisso CHECK (fim >= inicio);

-- versao ate aqui: current (casa:feito, notebook: feito, producao: feito, demo: feito)

create SCHEMA pedidocertidao;

--cabecalho
create table pedidocertidao.pedido_certidao (id int8 not null, version int8 not null, familia_id int8, servico_sistema_seguranca_id int8, situacao varchar(255) not null, operador_responsavel_id int8 not null, date_created timestamp, last_updated timestamp,
--certidao
tipo_certidao varchar(255) not null, cidadao_registro_id int8, nome_registro varchar(255) not null, data_registro timestamp, nome_conjuge_registro varchar(255), folha varchar(255), livro varchar(255), termo varchar(255), observacoes_registro varchar(10000),
--solicitante
cidadao_solicitante_id int8, nome_solicitante varchar(10000) not null, cpf_solicitante varchar(255), identidade_solicitante varchar(255), estado_civil_solicitante varchar(255), mae_solicitante varchar(255), pai_solicitante varchar(255), nacionalidade_solicitante varchar(255), profissao_solicitante varchar(255), uniao_estavel_solicitante varchar(255), convivente_solicitante varchar(255), contatos_solicitante varchar(10000), endereco_solicitante varchar(255), municipio_solicitante varchar(255), uf_solicitante varchar(255),
--cartorio
nome_cartorio varchar(10000), endereco_cartorio varchar(255), bairro_cartorio varchar(255), cep_cartorio varchar(255), municipio_cartorio varchar(255), uf_cartorio varchar(255), observacoes_cartorio varchar(10000), contatos_cartorio varchar(10000),
primary key (id));

alter table pedidocertidao.pedido_certidao add constraint FK_srfdfee4tkf4p50sno5h73d2o foreign key (familia_id) references familia;
alter table pedidocertidao.pedido_certidao add constraint FK_9ha7j30of75rsfw73fmblf5bi foreign key (servico_sistema_seguranca_id) references servico_sistema;
alter table pedidocertidao.pedido_certidao add constraint FK_emscmm79bi6g4dmy49i3d7wt4 foreign key (operador_responsavel_id) references usuario_sistema;
alter table pedidocertidao.pedido_certidao add constraint FK_om2tt6tsks4tuvlxguk6uq8d8 foreign key (cidadao_registro_id) references cidadao;
alter table pedidocertidao.pedido_certidao add constraint FK_ivfib5gtc2p67j5ndn0ofnhri foreign key (cidadao_solicitante_id) references cidadao;
create sequence pedidocertidao.sq_pedidocertidao;

create table pedidocertidao.historico_pedido_certidao (id int8 not null, version int8 not null, data_hora timestamp not null, descricao varchar(10000) not null, operador_id int8 not null, pedido_id int8 not null, acao varchar(255), primary key (id));
alter table pedidocertidao.historico_pedido_certidao add constraint FK_fvbrmu7yuv334q8ktry4xotsh foreign key (operador_id) references usuario_sistema;
alter table pedidocertidao.historico_pedido_certidao add constraint FK_k83p3jbm6arui3cfirn3sv4lx foreign key (pedido_id) references pedidocertidao.pedido_certidao;
create sequence pedidocertidao.sq_historico_pedidocertidao;

/*
alter table pedidocertidao.pedido_certidao add column bairro_cartorio varchar(255);
alter table pedidocertidao.pedido_certidao add column cep_cartorio varchar(255);
alter table pedidocertidao.pedido_certidao add column contatos_cartorio varchar(10000);
alter table pedidocertidao.pedido_certidao add column contatos_solicitante varchar(10000);
alter table pedidocertidao.pedido_certidao add column convivente_solicitante varchar(255);
alter table pedidocertidao.pedido_certidao add column cpf_solicitante varchar(255);
alter table pedidocertidao.pedido_certidao add column endereco_cartorio varchar(255);
alter table pedidocertidao.pedido_certidao add column estado_civil_solicitante varchar(255);
alter table pedidocertidao.pedido_certidao add column folha varchar(255);
alter table pedidocertidao.pedido_certidao add column identidade_solicitante varchar(255);
alter table pedidocertidao.pedido_certidao add column livro varchar(255);
alter table pedidocertidao.pedido_certidao add column mae_solicitante varchar(255);
alter table pedidocertidao.pedido_certidao add column municipio_cartorio varchar(255);
alter table pedidocertidao.pedido_certidao add column nacionalidade_solicitante varchar(255);
alter table pedidocertidao.pedido_certidao add column nome_conjuge_registro varchar(255);
alter table pedidocertidao.pedido_certidao add column observacoes_cartorio varchar(10000);
alter table pedidocertidao.pedido_certidao add column observacoes_registro varchar(10000);
alter table pedidocertidao.pedido_certidao add column pai_solicitante varchar(255);
alter table pedidocertidao.pedido_certidao add column profissao_solicitante varchar(255);
alter table pedidocertidao.pedido_certidao add column termo varchar(255);
alter table pedidocertidao.pedido_certidao add column tipo_certidao varchar(255);
alter table pedidocertidao.pedido_certidao add column uf_cartorio varchar(255);
alter table pedidocertidao.pedido_certidao add column uniao_estavel_solicitante varchar(255);
alter table pedidocertidao.pedido_certidao add column cidadao_registro_id int8;
alter table pedidocertidao.pedido_certidao add column cidadao_solicitante_id int8;

alter table pedidocertidao.pedido_certidao add column date_created timestamp;
alter table pedidocertidao.pedido_certidao add column last_updated timestamp;

alter table pedidocertidao.pedido_certidao add column endereco_solicitante varchar(255);
alter table pedidocertidao.pedido_certidao add column municipio_solicitante varchar(255);
alter table pedidocertidao.pedido_certidao add column uf_solicitante varchar(255);

*/

alter table servico_sistema add column recursos varchar(10000);
alter table servico_sistema add column token varchar(255);
update servico_sistema set token = 'CRJ' where id = 7;

-- versao ate aqui: current (casa:feito, notebook: feito, producao: feito, demo:feito)

alter table campo_formulario drop constraint fk_1rvtfmohx49wybiwxo3dporwj,
add constraint fk_1rvtfmohx49wybiwxo3dporwj foreign key (formulario_id)
   references formulario(id) on delete cascade;
alter table modelo_formulario drop constraint fk_il6iv2mtvadvdfm83wyd171h4,
add constraint fk_il6iv2mtvadvdfm83wyd171h4 foreign key (formulario_id)
   references formulario(id) on delete cascade;