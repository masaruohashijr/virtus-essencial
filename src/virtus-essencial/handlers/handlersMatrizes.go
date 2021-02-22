package handlers

import (
	"html/template"
	"log"
	"net/http"
	"strconv"
	//"strings"
	mdl "virtus-essencial/models"
	sec "virtus-essencial/security"
)

func ListMatrizesHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("List Matrizes Handler")
	currentUser := GetUserInCookie(w, r)
	if sec.IsAuthenticated(w, r) && HasPermission(currentUser, "viewMatriz") {
		log.Println("--------------")
		currentUser := GetUserInCookie(w, r)
		var page mdl.PageEntidadesCiclos
		log.Println(currentUser.Id)
		// Entidades da jurisdição do Escritório ao qual pertenço
		sql := "SELECT DISTINCT d.codigo, b.id_entidade, d.nome, a.abreviatura " +
			" FROM escritorios a " +
			" LEFT JOIN jurisdicoes b ON a.id = b.id_escritorio " +
			" LEFT JOIN membros c ON c.id_escritorio = b.id_escritorio " +
			" LEFT JOIN entidades d ON d.id = b.id_entidade " +
			" LEFT JOIN users u ON u.id = c.id_usuario " +
			" INNER JOIN ciclos_entidades e ON e.id_entidade = b.id_entidade " +
			" INNER JOIN produtos_planos f ON (f.id_entidade = e.id_entidade AND f.id_ciclo = e.id_ciclo) " +
			" WHERE (c.id_usuario = ? AND u.id_role in (3,4)) OR (a.id_chefe = ?)"
		log.Println(sql)
		rows, _ := Db.Query(sql, currentUser.Id, currentUser.Id)
		defer rows.Close()
		var entidades []mdl.Entidade
		var entidade mdl.Entidade
		var i = 1
		for rows.Next() {
			rows.Scan(
				&entidade.Codigo,
				&entidade.Id,
				&entidade.Nome,
				&entidade.EscritorioAbreviatura)
			entidade.Order = i
			i++
			sql = "SELECT b.id, b.nome " +
				" FROM ciclos_entidades a " +
				" LEFT JOIN ciclos b ON a.id_ciclo = b.id " +
				" WHERE a.id_entidade = ? " +
				" ORDER BY id asc"
			rows, _ = Db.Query(sql, entidade.Id)
			defer rows.Close()
			var ciclosEntidade []mdl.CicloEntidade
			var cicloEntidade mdl.CicloEntidade
			i = 1
			for rows.Next() {
				rows.Scan(&cicloEntidade.Id, &cicloEntidade.Nome)
				cicloEntidade.Order = i
				i++
				ciclosEntidade = append(ciclosEntidade, cicloEntidade)
			}
			entidade.CiclosEntidade = ciclosEntidade
			entidades = append(entidades, entidade)
		}
		page.Entidades = entidades
		page.AppName = mdl.AppName
		page.Title = "Matriz de Trabalho" + mdl.Ambiente
		page.LoggedUser = BuildLoggedUser(GetUserInCookie(w, r))
		var tmpl = template.Must(template.ParseGlob("tiles/matrizes/*"))
		tmpl.ParseGlob("tiles/*")
		tmpl.ExecuteTemplate(w, "Main-Entidades-Matrizes", page)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func loadElementosDaMatriz(entidadeId string, cicloId string, pilarId string, componenteId string) []mdl.ElementoDaMatriz {
	sql := " SELECT " +
		" 	     coalesce(R3.id_author,0) as id_author, " +
		" 	     coalesce(q.name,'') as author_name, " +
		" 	     coalesce(R3.motivacao_peso,'') as motivacao_peso, " +
		" 	     coalesce(R3.motivacao_nota,'') as motivacao_nota, " +
		" 	     coalesce(CO.id_supervisor,0) as super_id, coalesce(o.name,'') as supervisor_nome, " +
		" 	     coalesce(CO.id_auditor,0) as id_auditor, coalesce(p.name,'') as auditor_nome, " +
		"		 R1.id_elemento as id_elemento, " +
		"		 COALESCE((SELECT count(1) FROM elementos_componentes " +
		"				WHERE id_componente = " + componenteId + "),0) AS qtdElementos, " +
		"		 COALESCE((SELECT count(1) FROM produtos_planos " +
		"           WHERE id_ciclo = " + cicloId + " AND id_entidade = " + entidadeId + " AND id_pilar = " + pilarId + " AND id_componente = " + componenteId + "),0) AS qtdPlanos, " +
		"        R1.id_ciclo, " +
		"        COALESCE(R1.ciclo_nome, ''), " +
		"        (SELECT coalesce(nota,0) from produtos_ciclos where  id_ciclo = " + cicloId + " AND id_entidade = " + entidadeId + ") AS ciclo_nota, " +
		"   (SELECT count(1) " +
		"    FROM " +
		"      (SELECT id_pilar " +
		"       FROM pilares_ciclos " +
		"       WHERE id_ciclo = R1.id_ciclo " +
		"       GROUP BY id_pilar) R) AS qtdPilares, " +
		"        R1.id_pilar, " +
		"        COALESCE(R1.pilar_nome, ''), " +
		"        COALESCE(PI.peso, 0) AS pilar_peso, " +
		"        COALESCE(PI.nota, 0) AS pilar_nota, " +
		"   (SELECT count(1) " +
		"    FROM " +
		"      (SELECT id_componente " +
		"       FROM componentes_pilares " +
		"       WHERE id_pilar = R1.id_pilar " +
		"       GROUP BY id_componente) R) AS qtdComponentes, " +
		"        R1.id_componente, " +
		"        COALESCE(R1.componente_nome, ''), " +
		"        COALESCE(CO.peso, 0) AS componente_peso, " +
		"        COALESCE(CO.nota, 0) AS componente_nota, " +
		"   (SELECT count(1) " +
		"    FROM " +
		"      (SELECT id_tipo_nota " +
		"       FROM elementos_componentes " +
		"       WHERE id_componente = R1.id_componente " +
		"       GROUP BY id_tipo_nota) R) AS qtdTiposNotas, " +
		"        R1.id_tipo_nota, " +
		"        COALESCE(m.nome,'') AS tipo_nota_nome, " +
		"        COALESCE(m.letra,'') AS tipo_nota_letra, " +
		"        COALESCE(m.cor_letra,'') as tipo_nota_cor_letra, " +
		"        COALESCE(TN.peso, 0) AS tipo_nota_peso, " +
		"        COALESCE(TN.nota, 0) AS tipo_nota_nota, " +
		"      	 COALESCE(n.nome, '') AS elemento_nome, " +
		"     	 COALESCE(EL.peso, 0) AS elemento_peso, " +
		"    	 COALESCE(EL.nota, 0) AS elemento_nota,	" +
		"        " + entidadeId + " AS id_entidade, " +
		"        COALESCE(y.nome,'') as entidade_nome, " +
		"        COALESCE(R2.id_plano, 0) AS id_plano, " +
		"        COALESCE(z.cnpb,'') AS cnpb, " +
		"        CASE WHEN z.recurso_garantidor > 1000000 AND z.recurso_garantidor < 1000000000 THEN concat(format(z.recurso_garantidor/1000000,'N','pt-br'),' Milhões') WHEN z.recurso_garantidor > 1000000000 THEN concat(format(z.recurso_garantidor/1000000000,'N','pt-br'),' Bilhões') ELSE concat(format(z.recurso_garantidor/1000,'N','pt-br'),' Milhares') END as rg, " +
		"        COALESCE(z.modalidade_id,'') as modalidade, " +
		"        COALESCE((SELECT count(1) " +
		"   		FROM " +
		"     		(SELECT b.id_elemento " +
		"      			FROM produtos_planos a " +
		"	   			LEFT JOIN elementos_componentes b ON " +
		"	  			a.id_componente = b.id_componente " +
		"	  			WHERE a.id_ciclo = " + cicloId +
		"      			AND a.id_entidade = " + entidadeId +
		"	  			AND a.id_pilar = " + pilarId +
		"	  			AND a.id_componente = " + componenteId +
		"      			) R),0) as EntidadeRowspan " +
		" FROM " +
		"   (SELECT a.id AS id_ciclo, " +
		"           a.nome AS ciclo_nome, " +
		"           b.id AS id_pilar, " +
		"           c.nome AS pilar_nome, " +
		"           e.id AS id_componente, " +
		"           e.nome AS componente_nome, " +
		"           g.id AS id_tipo_nota, " +
		"           g.nome AS tipo_nota_nome, " +
		"			i.id AS id_elemento, " +
		"			i.nome AS elemento_nome " +
		"    FROM ciclos a " +
		"    INNER JOIN pilares_ciclos b ON b.id_ciclo = a.id " +
		"    INNER JOIN pilares c ON c.id = b.id_pilar " +
		"    INNER JOIN componentes_pilares d ON d.id_pilar = c.id " +
		"    INNER JOIN componentes e ON d.id_componente = e.id " +
		"    INNER JOIN tipos_notas_componentes f ON e.id = f.id_componente " +
		"    INNER JOIN tipos_notas g ON g.id = f.id_tipo_nota " +
		" 	 INNER JOIN elementos_componentes h ON e.id = h.id_componente and g.id = h.id_tipo_nota " +
		"	 INNER JOIN elementos i ON i.id = h.id_elemento " +
		"    WHERE a.id = 1 "
	if pilarId != "" {
		sql += " AND b.id_pilar = " + pilarId
	}
	if componenteId != "" {
		sql += " AND d.id_componente = " + componenteId
	}
	sql += " ) R1 " +
		" LEFT JOIN " +
		"   (SELECT DISTINCT id_entidade, " +
		"                    id_ciclo, " +
		"                    PILAR_ID, " +
		"                    COMPONENTE_ID, " +
		"                    id_plano " +
		"    FROM produtos_planos " +
		"    WHERE id_ciclo = " + cicloId +
		"      AND id_entidade = " + entidadeId +
		"    ) R2 ON (R1.CICLO_id = R2.id_ciclo " +
		"                       AND R1.PILAR_ID = R2.PILAR_ID " +
		"                       AND R1.COMPONENTE_ID = R2.COMPONENTE_ID) " +
		" LEFT JOIN produtos_elementos EL ON (R1.id_ciclo = EL.CICLO_ID " +
		"                                     AND R1.id_pilar = EL.id_pilar " +
		"                                     AND R1.id_componente = EL.id_componente " +
		"                                     AND R1.id_tipo_nota = EL.id_tipo_nota " +
		"								   	  AND R1.id_elemento = EL.id_elemento " +
		"                                     AND EL.id_entidade = R2.id_entidade " +
		"                                     AND EL.id_plano = R2.id_plano) " +
		" LEFT JOIN produtos_tipos_notas TN ON (R1.id_ciclo = TN.CICLO_ID " +
		"                                       AND R1.id_pilar = TN.id_pilar " +
		"                                       AND R1.id_componente = TN.id_componente " +
		"                                       AND R1.id_tipo_nota = TN.id_tipo_nota " +
		"                                       AND TN.id_entidade = R2.id_entidade " +
		"                                       AND TN.id_plano = R2.id_plano) " +
		" LEFT JOIN produtos_componentes CO ON (R1.id_componente = CO.id_componente " +
		"                                       AND R1.id_pilar = CO.id_pilar " +
		"                                       AND R1.id_ciclo = CO.id_ciclo " +
		"                                       AND CO.id_entidade = " + entidadeId + ") " +
		" LEFT JOIN produtos_pilares PI ON (R1.id_pilar = PI.id_pilar " +
		"                                   AND R1.id_ciclo = PI.id_ciclo " +
		"                                   AND PI.id_entidade = R2.id_entidade) " +
		" LEFT JOIN produtos_ciclos CI ON (R1.id_ciclo = CI.id_ciclo " +
		"                                  AND R2.id_entidade = CI.id_entidade) " +
		" LEFT JOIN tipos_notas m ON R1.id_tipo_nota = m.id " +
		" LEFT JOIN elementos n ON R1.id_elemento = n.id " +
		" LEFT JOIN planos z ON R2.id_plano = z.id " +
		" LEFT JOIN entidades y ON y.id = " + entidadeId +
		" LEFT JOIN users o ON co.id_supervisor = o.id " +
		" LEFT JOIN users p ON co.id_auditor = p.id " +
		" LEFT JOIN (SELECT R1.id, R1.id_entidade, R1.id_ciclo, R1.id_pilar, R1.id_plano, R1.id_componente, R1.id_tipo_nota, R1.id_elemento, motivacao_peso, motivacao_nota, id_author, criado_em " +
		" FROM produtos_elementos_historicos R1 " +
		" INNER JOIN (SELECT peh.id_entidade, peh.id_ciclo, peh.id_pilar, peh.id_componente, peh.id_plano, peh.id_elemento, max(peh.id) as id " +
		" FROM produtos_elementos_historicos PEH group by peh.id_entidade,peh.id_ciclo,peh.id_pilar,peh.id_componente,peh.id_plano,peh.id_elemento) R2 " +
		" ON R1.id = R2.id) R3 ON (R3.id_ciclo = EL.CICLO_ID " +
		" AND R3.id_pilar = EL.id_pilar " +
		" AND R3.id_componente = EL.id_componente " +
		" AND R3.id_tipo_nota = EL.id_tipo_nota " +
		" AND R3.id_entidade = EL.id_entidade " +
		" AND R3.id_plano = EL.id_plano) " +
		" LEFT JOIN users q ON R3.id_author = q.id " +
		" ORDER BY id_ciclo, id_pilar, id_componente, id_plano, id_tipo_nota "
	log.Println(sql)
	rows, _ := Db.Query(sql)
	defer rows.Close()
	var elementosMatriz []mdl.ElementoDaMatriz
	var elementoMatriz mdl.ElementoDaMatriz
	var i = 1
	for rows.Next() {
		rows.Scan(
			&elementoMatriz.AutorNotaId,
			&elementoMatriz.AutorNotaName,
			&elementoMatriz.MotivacaoPeso,
			&elementoMatriz.MotivacaoNota,
			&elementoMatriz.SupervisorId,
			&elementoMatriz.SupervisorName,
			&elementoMatriz.AuditorId,
			&elementoMatriz.AuditorName,
			&elementoMatriz.ElementoId,
			&elementoMatriz.ComponenteQtdElementos,
			&elementoMatriz.ComponenteQtdPlanos,
			&elementoMatriz.CicloId,
			&elementoMatriz.CicloNome,
			&elementoMatriz.CicloNota,
			&elementoMatriz.CicloQtdPilares,
			&elementoMatriz.PilarId,
			&elementoMatriz.PilarNome,
			&elementoMatriz.PilarPeso,
			&elementoMatriz.PilarNota,
			&elementoMatriz.PilarQtdComponentes,
			&elementoMatriz.ComponenteId,
			&elementoMatriz.ComponenteNome,
			&elementoMatriz.ComponentePeso,
			&elementoMatriz.ComponenteNota,
			&elementoMatriz.ComponenteQtdTiposNotas,
			&elementoMatriz.TipoNotaId,
			&elementoMatriz.TipoNotaNome,
			&elementoMatriz.TipoNotaLetra,
			&elementoMatriz.TipoNotaCorLetra,
			&elementoMatriz.TipoNotaPeso,
			&elementoMatriz.TipoNotaNota,
			&elementoMatriz.ElementoNome,
			&elementoMatriz.ElementoPeso,
			&elementoMatriz.ElementoNota,
			&elementoMatriz.EntidadeId,
			&elementoMatriz.EntidadeNome,
			&elementoMatriz.PlanoId,
			&elementoMatriz.CNPB,
			&elementoMatriz.RecursoGarantidor,
			&elementoMatriz.Modalidade,
			&elementoMatriz.EntidadeRowspan)
		elementoMatriz.Order = i
		i++
		elementosMatriz = append(elementosMatriz, elementoMatriz)
	}
	return elementosMatriz
}
func loadTiposNotasMatriz(entidadeId string, cicloId string, pilarId string) []mdl.ElementoDaMatriz {
	sql := " SELECT R1.id_ciclo, " +
		"        COALESCE(R1.ciclo_nome, ''), " +
		"        (SELECT coalesce(nota,0) from produtos_ciclos where  id_ciclo = " + cicloId + " AND id_entidade = " + entidadeId + ") AS ciclo_nota, " +
		"   (SELECT count(1) " +
		"    FROM " +
		"      (SELECT id_pilar " +
		"       FROM pilares_ciclos " +
		"       WHERE id_ciclo = R1.id_ciclo " +
		"       GROUP BY id_pilar) R) AS qtdPilares, " +
		"        R1.id_pilar, " +
		"        COALESCE(R1.pilar_nome, ''), " +
		"        COALESCE(PI.peso, 0) AS pilar_peso, " +
		"        COALESCE(PI.nota, 0) AS pilar_nota, " +
		"   (SELECT count(1) " +
		"    FROM " +
		"      (SELECT id_componente " +
		"       FROM componentes_pilares " +
		"       WHERE id_pilar = R1.id_pilar " +
		"       GROUP BY id_componente) R) AS qtdComponentes, " +
		"   (SELECT count(1) " +
		"    FROM " +
		"      (SELECT id_tipo_nota " +
		"       FROM elementos_componentes " +
		"       WHERE id_componente = R1.id_componente " +
		"       GROUP BY id_tipo_nota) R) AS qtdTiposNotas, " +
		"        R1.id_componente, " +
		"        COALESCE(R1.componente_nome, ''), " +
		"        COALESCE(CO.peso, 0) AS componente_peso, " +
		"        COALESCE(CO.nota, 0) AS componente_nota, " +
		"        R1.id_tipo_nota, " +
		"        COALESCE(m.letra,'') AS tipo_nota_letra, " +
		"        COALESCE(m.cor_letra,'') as tipo_nota_cor_letra, " +
		"        COALESCE(TN.peso, 0) AS tipo_nota_peso, " +
		"        COALESCE(TN.nota, 0) AS tipo_nota_nota, " +
		"        " + entidadeId + " AS id_entidade, " +
		"        COALESCE(y.nome,'') as entidade_nome, " +
		"        COALESCE(R2.id_plano, 0) AS id_plano, " +
		"        COALESCE(z.cnpb,'') AS cnpb, " +
		"        CASE WHEN z.recurso_garantidor > 1000000 AND z.recurso_garantidor < 1000000000 THEN concat(format(z.recurso_garantidor/1000000,'N','pt-br'),' Milhões') WHEN z.recurso_garantidor > 1000000000 THEN concat(format(z.recurso_garantidor/1000000000,'N','pt-br'),' Bilhões') ELSE concat(format(z.recurso_garantidor/1000,'N','pt-br'),' Milhares') END as rg, " +
		"        COALESCE(z.modalidade_id,'') as modalidade, " +
		"        (SELECT count(1) FROM (SELECT DISTINCT id_plano FROM produtos_planos WHERE id_entidade = " + entidadeId + " AND id_ciclo = " + cicloId + " GROUP BY id_plano) S) as EntidadeQtdPlanos " +
		" FROM " +
		"   (SELECT a.id AS id_ciclo, " +
		"           a.nome AS ciclo_nome, " +
		"           b.id AS id_pilar, " +
		"           c.nome AS pilar_nome, " +
		"           e.id AS id_componente, " +
		"           e.nome AS componente_nome, " +
		"           g.id AS id_tipo_nota, " +
		"           g.nome AS tipo_nota_nome " +
		"    FROM ciclos a " +
		"    INNER JOIN pilares_ciclos b ON b.id_ciclo = a.id " +
		"    INNER JOIN pilares c ON c.id = b.id_pilar " +
		"    INNER JOIN componentes_pilares d ON d.id_pilar = c.id " +
		"    INNER JOIN componentes e ON d.id_componente = e.id " +
		"    INNER JOIN tipos_notas_componentes f ON e.id = f.id_componente " +
		"    INNER JOIN tipos_notas g ON g.id = f.id_tipo_nota " +
		"    WHERE a.id = 1 "
	if pilarId != "" {
		sql += " AND b.id_pilar = " + pilarId
	}
	sql += " ) R1 " +
		" LEFT JOIN " +
		"   (SELECT DISTINCT id_entidade, " +
		"                    id_ciclo, " +
		"                    PILAR_ID, " +
		"                    COMPONENTE_ID, " +
		"                    id_plano " +
		"    FROM produtos_planos " +
		"    WHERE id_ciclo = " + cicloId +
		"      AND id_entidade = " + entidadeId +
		"    ) R2 ON (R1.CICLO_id = R2.id_ciclo " +
		"                       AND R1.PILAR_ID = R2.PILAR_ID " +
		"                       AND R1.COMPONENTE_ID = R2.COMPONENTE_ID) " +
		" LEFT JOIN produtos_tipos_notas TN ON (R1.id_ciclo = TN.CICLO_ID " +
		"                                       AND R1.id_pilar = TN.id_pilar " +
		"                                       AND R1.id_componente = TN.id_componente " +
		"                                       AND R1.id_tipo_nota = TN.id_tipo_nota " +
		"                                       AND TN.id_entidade = R2.id_entidade " +
		"                                       AND TN.id_plano = R2.id_plano) " +
		" LEFT JOIN produtos_componentes CO ON (R1.id_componente = CO.id_componente " +
		"                                       AND R1.id_pilar = CO.id_pilar " +
		"                                       AND R1.id_ciclo = CO.id_ciclo " +
		"                                       AND CO.id_entidade = R2.id_entidade) " +
		" LEFT JOIN produtos_pilares PI ON (R1.id_pilar = PI.id_pilar " +
		"                                   AND R1.id_ciclo = PI.id_ciclo " +
		"                                   AND PI.id_entidade = R2.id_entidade) " +
		" LEFT JOIN produtos_ciclos CI ON (R1.id_ciclo = CI.id_ciclo " +
		"                                  AND R2.id_entidade = CI.id_entidade) " +
		" LEFT JOIN tipos_notas m ON R1.id_tipo_nota = m.id " +
		" LEFT JOIN planos z ON R2.id_plano = z.id " +
		" LEFT JOIN entidades y ON y.id = " + entidadeId +
		" ORDER BY id_ciclo, id_pilar, id_componente, id_plano, id_tipo_nota "
	log.Println(sql)
	rows, _ := Db.Query(sql)
	defer rows.Close()
	var elementosMatriz []mdl.ElementoDaMatriz
	var elementoMatriz mdl.ElementoDaMatriz
	var i = 1
	for rows.Next() {
		rows.Scan(
			&elementoMatriz.CicloId,
			&elementoMatriz.CicloNome,
			&elementoMatriz.CicloNota,
			&elementoMatriz.CicloQtdPilares,
			&elementoMatriz.PilarId,
			&elementoMatriz.PilarNome,
			&elementoMatriz.PilarPeso,
			&elementoMatriz.PilarNota,
			&elementoMatriz.PilarQtdComponentes,
			&elementoMatriz.ComponenteQtdTiposNotas,
			&elementoMatriz.ComponenteId,
			&elementoMatriz.ComponenteNome,
			&elementoMatriz.ComponentePeso,
			&elementoMatriz.ComponenteNota,
			&elementoMatriz.TipoNotaId,
			&elementoMatriz.TipoNotaLetra,
			&elementoMatriz.TipoNotaCorLetra,
			&elementoMatriz.TipoNotaPeso,
			&elementoMatriz.TipoNotaNota,
			&elementoMatriz.EntidadeId,
			&elementoMatriz.EntidadeNome,
			&elementoMatriz.PlanoId,
			&elementoMatriz.CNPB,
			&elementoMatriz.RecursoGarantidor,
			&elementoMatriz.Modalidade,
			&elementoMatriz.EntidadeQtdPlanos)
		elementoMatriz.Order = i
		i++
		//log.Println(elementoMatriz)
		elementosMatriz = append(elementosMatriz, elementoMatriz)
	}
	return elementosMatriz
}

func ExecutarMatrizHandler(w http.ResponseWriter, r *http.Request) {

	log.Println("Matriz Handler")
	if sec.IsAuthenticated(w, r) {
		entidadeId := r.FormValue("EntidadeId")
		cicloId := r.FormValue("CicloId")
		pilarId := r.FormValue("PilarId")
		componenteId := r.FormValue("ComponenteId")
		definicaoTemplate := "Main-Matriz"
		title := "Matriz"
		var elementosMatriz []mdl.ElementoDaMatriz
		var page mdl.PageMatriz
		if componenteId != "" {
			title = "Visão do Componente"
			definicaoTemplate = "Main-Componente"
			elementosMatriz = loadElementosDaMatriz(entidadeId, cicloId, pilarId, componenteId)
		} else if pilarId != "" {
			title = "Visão do Pilar"
			definicaoTemplate = "Main-Pilar"
			elementosMatriz = loadTiposNotasMatriz(entidadeId, cicloId, pilarId)
		} else {
			elementosMatriz = loadTiposNotasMatriz(entidadeId, cicloId, pilarId)
		}
		page.ElementosDaMatriz = preencherColspans(elementosMatriz, cicloId)
		page.ComponenteQtdTiposNotas = elementosMatriz[0].ComponenteQtdTiposNotas

		sql := " SELECT " +
			" a.id_usuario, " +
			" coalesce(b.name,'') " +
			" FROM integrantes a " +
			" LEFT JOIN users b " +
			" ON a.id_usuario = b.id " +
			" WHERE " +
			" a.id_entidade = " + entidadeId +
			" AND a.id_ciclo = " + cicloId +
			" AND b.id_role = 3 "
		log.Println(sql)
		rows, _ := Db.Query(sql)
		defer rows.Close()
		var supervisores []mdl.User
		var supervisor mdl.User
		for rows.Next() {
			rows.Scan(&supervisor.Id, &supervisor.Name)
			supervisores = append(supervisores, supervisor)
		}
		page.Supervisores = supervisores

		sql = " SELECT " +
			" a.id_usuario, " +
			" b.name " +
			" FROM integrantes a " +
			" LEFT JOIN users b " +
			" ON a.id_usuario = b.id " +
			" WHERE " +
			" a.id_entidade = " + entidadeId +
			" AND a.id_ciclo = " + cicloId +
			" AND b.id_role = 4 "
		log.Println(sql)
		rows, _ = Db.Query(sql)
		defer rows.Close()
		var auditores []mdl.User
		var auditor mdl.User
		for rows.Next() {
			rows.Scan(&auditor.Id, &auditor.Name)
			auditores = append(auditores, auditor)
		}
		page.Supervisores = supervisores
		page.Auditores = auditores
		page.AppName = mdl.AppName
		page.LoggedUser = BuildLoggedUser(GetUserInCookie(w, r))
		page.Dec = func(i int) int {
			return i - 1
		}
		page.Inc = func(i int) int {
			return i + 1
		}
		page.MulTxt = func(i int, j string) int {
			k, _ := strconv.Atoi(j)
			return i * k
		}
		page.Mul = func(i int, j int) int {
			return i * j
		}
		page.SomarTxt = func(i int, j string) int {
			k, _ := strconv.Atoi(j)
			return i + k
		}
		page.Somar = func(i int, j int) int {
			return i + j
		}
		var tmpl = template.Must(template.ParseGlob("tiles/matrizes/*"))
		tmpl.ParseGlob("tiles/*")
		page.Title = title
		tmpl.ExecuteTemplate(w, definicaoTemplate, page)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func calcularColspan(tipo string, identificador int64) int {
	sql := ""
	if tipo == "ciclo" {
		tipo = "pc.ciclo"
	} else if tipo == "pilar" {
		tipo = "pc.pilar"
	} else if tipo == "componente" {
		tipo = "cp.componente"
	}
	sql = " SELECT COUNT(1) FROM ( " +
		" SELECT id_ciclo, pc.id_pilar, cp.id_componente, tnc.id_tipo_nota " +
		" FROM tipos_notas_componentes tnc " +
		" LEFT JOIN componentes c ON tnc.id_componente = c.id " +
		" LEFT JOIN componentes_pilares cp ON c.id = cp.id_componente " +
		" LEFT JOIN pilares p ON p.id = cp.id_pilar " +
		" LEFT JOIN pilares_ciclos pc ON p.id = pc.id_pilar " +
		" WHERE " + tipo + "_id = ?) R "
	log.Println(sql)
	rows, _ := Db.Query(sql, identificador)
	defer rows.Close()
	resultado := 0
	if rows.Next() {
		rows.Scan(&resultado)
		return resultado
	}
	return 0
}

func preencherColspans(elementosMatriz []mdl.ElementoDaMatriz, cicloId string) []mdl.ElementoDaMatriz {
	sql := " WITH R0 AS " +
		" (SELECT DISTINCT a.id_ciclo, " +
		"                 a.id_pilar, " +
		"                 b.id_componente, " +
		"                 c.id_tipo_nota                 " +
		"          FROM pilares_ciclos a " +
		"          INNER JOIN componentes_pilares b ON b.id_pilar = a.id_pilar " +
		"          INNER JOIN tipos_notas_componentes c ON b.id_componente = c.id_componente " +
		"          INNER JOIN elementos_componentes ec ON (ec.id_tipo_nota = c.id_tipo_nota " +
		"          AND b.id_componente = ec.id_componente) " +
		"          WHERE a.id_ciclo = ?), " +
		" R1 AS ( " +
		" SELECT  " +
		" 	id_ciclo,  " +
		" 	id_pilar,  " +
		" 	id_componente,  " +
		" 	COUNT(1) AS qtdCelula " +
		" 	FROM R0 " +
		" 	GROUP BY 	 " +
		" 	id_ciclo,  " +
		" 	id_pilar,  " +
		" 	id_componente) " +
		" SELECT id_ciclo, 0 as id_pilar, 0 as id_componente, sum(qtdCelula) as qtdCelula  " +
		" FROM R1 " +
		" GROUP BY id_ciclo " +
		" UNION " +
		" SELECT id_ciclo, id_pilar, 0 as id_componente, sum(qtdCelula) as qtdCelula  " +
		" FROM R1 " +
		" GROUP BY id_ciclo, id_pilar " +
		" UNION " +
		" SELECT id_ciclo, id_pilar, id_componente, sum(qtdCelula) as qtdCelula  " +
		" FROM R1 " +
		" GROUP BY id_ciclo, id_pilar, id_componente " +
		" ORDER BY 1,2,3"
	rows, _ := Db.Query(sql, cicloId)
	defer rows.Close()
	log.Println(sql)
	var cols []mdl.ColSpan
	var col mdl.ColSpan
	for rows.Next() {
		rows.Scan(&col.CicloId, &col.PilarId, &col.ComponenteId, &col.Qtd)
		cols = append(cols, col)
		// log.Println(col)
	}
	var novosElementos []mdl.ElementoDaMatriz

	for _, elemento := range elementosMatriz {

		for _, col := range cols {

			if col.PilarId == 0 && col.ComponenteId == 0 {
				elemento.CicloColSpan = col.Qtd
			} else if elemento.PilarId == col.PilarId && col.ComponenteId == 0 {
				elemento.PilarColSpan = col.Qtd
			} else if elemento.PilarId == col.PilarId && elemento.ComponenteId == col.ComponenteId {
				elemento.ComponenteColSpan = col.Qtd
			}
		}

		novosElementos = append(novosElementos, elemento)
	}
	return novosElementos
}
