SELECT e.id_status, e.name, a.id_feature, c.name, d.name FROM virtus.features_activities a
inner join virtus.activities b ON a.id_activity = b.id_activity
inner join virtus.features c ON a.id_feature = c.id_feature
inner join virtus.actions d ON b.id_action = d.id_action
inner join virtus.status e ON e.id_status = d.id_origin_status
inner join virtus.produtos_componentes f ON f.id_status = e.id_status
WHERE f.id_componente = 1 
and f.id_pilar = 1
and f.id_ciclo = 1
and f.id_entidade = 216
and c.code = 'tramitacaoAutomatica'