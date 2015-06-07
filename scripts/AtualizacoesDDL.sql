--versao: 0.3 (local:feito, valid:feito, producao:feito)

create sequence sq_papel start with 8;
create sequence sq_usuario_sistema start with 22;

-- versao: 0.3.1 (local:feito, valid:pendente, producao:feito

ALTER TABLE servico ALTER COLUMN apelido TYPE VARCHAR(60) USING apelido::VARCHAR(60);
alter table servico add column descricao varchar;
alter table servico add column pode_encaminhar boolean default TRUE;
alter table servico add column telefones varchar;
alter table servico add column site varchar;
drop table endereco;
