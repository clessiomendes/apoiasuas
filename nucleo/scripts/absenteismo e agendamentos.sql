select to_char(data_hora, 'day'), compareceu, count(*) from atendimento_particularizado where data_hora >= '21/06/2017' and data_hora < '25/09/2017'
group by to_char(data_hora, 'day'), compareceu
ORDER BY to_char(data_hora, 'day')