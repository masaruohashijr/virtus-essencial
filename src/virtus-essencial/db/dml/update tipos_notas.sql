" WITH R2 AS "+
"   (SELECT id_componente, "+
"           id_tipo_nota, "+
"           round(round(cast(peso_padrao AS numeric(10, 5))/cast(total AS numeric(10, 5)), 4)*cast(100 AS numeric), 2) AS peso_padrao "+
"    FROM "+
"      (WITH TMP AS "+
"         (SELECT id_componente, "+
"                 SUM(peso_padrao) AS peso_padrao "+
"          FROM virtus.elementos_componentes a "+
"          WHERE id_componente = a.id_componente "+
"          GROUP BY id_componente) SELECT a.id_componente, "+
"                                         a.id_tipo_nota, "+
"                                         tmp.peso_padrao AS total, "+
"                                         sum(a.peso_padrao) AS peso_padrao "+
"       FROM virtus.elementos_componentes a "+
"       LEFT JOIN TMP ON a.id_componente = TMP.id_componente "+
"       GROUP BY a.id_componente, "+
"                a.id_tipo_nota, "+
"                tmp.peso_padrao) R1 "+
"    ORDER BY 1, "+
"             2) "+
" UPDATE tipos_notas_componentes "+
" SET peso_padrao = R2.peso_padrao "+
" FROM R2 "+
" WHERE tipos_notas_componentes.id_componente = R2.id_componente "+
"   AND tipos_notas_componentes.id_tipo_nota = R2.id_tipo_nota "