'versao: 0.3 (local:feito, valid:feito, producao:feito)

ALTER TABLE servico ALTER COLUMN apelido TYPE VARCHAR(60) USING apelido::VARCHAR(60);
alter table servico add column descricao varchar(100000);