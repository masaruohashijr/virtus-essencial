package handlers

import (
	"encoding/json"
	"html/template"
	"log"
	"net/http"
	"strconv"
	"strings"
	mdl "virtus-essencial/models"
	sec "virtus-essencial/security"
)

const sqlAvaliarPlanos = " SELECT a.id_entidade, " +
	" 	   coalesce(b.nome,'') as entidade_nome, " +
	"        a.id_ciclo, " +
	" 	   coalesce(c.nome,'') as ciclo_nome, " +
	" 	   coalesce(pdc.nota,0) as ciclo_nota, " +
	"        a.id_pilar,        " +
	" 	   coalesce(d.nome,'') as pilar_nome, " +
	" 	   coalesce(f.peso,0) as pilar_peso, coalesce(f.nota,0) as pilar_nota, " +
	"        a.id_componente, " +
	" 	   coalesce(e.nome,'') as componente_nome, " +
	" 	   round(coalesce(g.peso,0),2) as componente_peso, coalesce(g.nota,0) as componente_nota, " +
	" 	   coalesce(g.id_supervisor,0) as super_id, coalesce(h.name,'') as supervisor_nome, " +
	" 	   coalesce(g.id_auditor,0) as super_id, coalesce(i.name,'') as auditor_nome, " +
	" 	   a.id_tipo_nota, m.letra, m.cor_letra, m.nome, " +
	" 	   coalesce(o.peso,0) as tipo_nota_peso, coalesce(o.nota,0) as tipo_nota_nota, " +
	"        a.id_elemento, coalesce(k.nome,'') as elemento_nome, " +
	" 	   coalesce(n.peso,0) as elemento_peso, coalesce(n.nota,0) as elemento_nota, " +
	"	   n.id_tipo_pontuacao, ec.peso_padrao, " +
	" 	   cp.tipo_media, cp.peso_padrao, " +
	" 	   pc.tipo_media, pc.peso_padrao, " +
	" 	   a.id_item, coalesce(l.nome,'') as item_nome, " +
	"      a.id_plano, " +
	"	   j.cnpb, CASE WHEN j.recurso_garantidor > 1000000 AND j.recurso_garantidor < 1000000000 THEN concat(format(j.recurso_garantidor/1000000,'N','pt-br'),' Milhões') WHEN j.recurso_garantidor > 1000000000 THEN concat(format(j.recurso_garantidor/1000000000,'N','pt-br'),' Bilhões') ELSE concat(format(j.recurso_garantidor/1000,'N','pt-br'),' Milhares') END, j.id_modalidade, " +
	" 	   coalesce(p.peso,0) as plano_peso, coalesce(p.nota,0) as plano_nota, " +
	"	   coalesce(format(g.inicia_em, 'dd/MM/yyyy'), '') AS inicia_em, " +
	"      coalesce(format(g.termina_em, 'dd/MM/yyyy'), '') AS termina_em, " +
	"	   CASE " +
	"	    WHEN g.inicia_em IS NOT NULL AND " +
	"		g.termina_em IS NOT NULL AND " +
	"		GETDATE() BETWEEN coalesce(g.inicia_em,CAST('0001-01-01' as DATE)) " +
	"	    AND coalesce(dateadd(day,1,g.termina_em),CAST('9999-12-31' as DATE)) " +
	"	    THEN 1 " +
	"	    ELSE 0 " +
	"	   END AS periodo_permitido " +
	" FROM virtus.produtos_itens a " +
	" INNER JOIN virtus.entidades b ON a.id_entidade = b.id_entidade " +
	" INNER JOIN virtus.ciclos c ON a.id_ciclo = c.id_ciclo " +
	" INNER JOIN virtus.pilares d ON a.id_pilar = d.id_pilar " +
	" INNER JOIN virtus.componentes e ON a.id_componente = e.id_componente " +
	" INNER JOIN virtus.produtos_pilares f ON " +
	" ( a.id_pilar = f.id_pilar AND  " +
	"   a.id_ciclo = f.id_ciclo AND  " +
	"   a.id_entidade = f.id_entidade ) " +
	" INNER JOIN virtus.produtos_componentes g ON  " +
	" ( a.id_componente = g.id_componente AND  " +
	"   a.id_pilar = g.id_pilar AND  " +
	"   a.id_ciclo = g.id_ciclo AND  " +
	"   a.id_entidade = g.id_entidade  " +
	" ) " +
	" LEFT JOIN virtus.users h ON g.id_supervisor = h.id_user " +
	" LEFT JOIN virtus.users i ON g.id_auditor = i.id_user " +
	" INNER JOIN virtus.planos j ON a.id_plano = j.id_plano " +
	" INNER JOIN virtus.elementos k ON a.id_elemento = k.id_elemento " +
	" INNER JOIN virtus.itens l ON a.id_item = l.id_item " +
	" INNER JOIN virtus.elementos_componentes ec ON ( a.id_elemento = ec.id_elemento AND a.id_tipo_nota = ec.id_tipo_nota AND a.id_componente = ec.id_componente ) " +
	" INNER JOIN virtus.componentes_pilares cp ON ( a.id_componente = cp.id_componente AND a.id_pilar = cp.id_pilar ) " +
	" INNER JOIN virtus.pilares_ciclos pc ON ( a.id_pilar = pc.id_pilar AND a.id_ciclo = pc.id_ciclo ) " +
	" INNER JOIN virtus.tipos_notas m ON a.id_tipo_nota = m.id_tipo_nota " +
	" INNER JOIN virtus.produtos_elementos n ON  " +
	" 	( a.id_elemento = n.id_elemento AND  " +
	" 	a.id_tipo_nota = n.id_tipo_nota AND  " +
	" 	a.id_plano = n.id_plano AND  " +
	" 	a.id_componente = n.id_componente AND  " +
	" 	a.id_pilar = n.id_pilar AND  " +
	" 	a.id_ciclo = n.id_ciclo AND  " +
	" 	a.id_entidade = n.id_entidade ) " +
	" INNER JOIN virtus.produtos_tipos_notas o ON  " +
	" ( a.id_tipo_nota = o.id_tipo_nota AND  " +
	"   a.id_plano = o.id_plano AND " +
	"   a.id_componente = o.id_componente AND " +
	"   a.id_pilar = o.id_pilar AND  " +
	"   a.id_ciclo = o.id_ciclo AND  " +
	"   a.id_entidade = o.id_entidade) " +
	" INNER JOIN virtus.produtos_planos p ON  " +
	"  (a.id_plano = p.id_plano AND  " +
	"   a.id_componente = p.id_componente AND  " +
	"   a.id_pilar = p.id_pilar AND  " +
	"   a.id_ciclo = p.id_ciclo AND  " +
	"   a.id_entidade = p.id_entidade)   " +
	" INNER JOIN virtus.produtos_ciclos pdc ON " +
	"  (a.id_ciclo = pdc.id_ciclo AND " +
	"   a.id_entidade = pdc.id_entidade) " +
	" WHERE a.id_entidade = ? " +
	"   AND a.id_ciclo = ? " +
	" ORDER BY a.id_ciclo, " +
	"          a.id_pilar, " +
	"          a.id_componente, " +
	"          j.recurso_garantidor DESC, " +
	"          a.id_tipo_nota, " +
	"          a.id_elemento, " +
	"          a.id_item "

func ListAvaliarPlanosHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("List Avaliar Planos Handler")
	currentUser := GetUserInCookie(w, r)
	if sec.IsAuthenticated(w, r) && HasPermission(currentUser, "avaliarPlanos") {
		log.Println("--------------")
		var page mdl.PageEntidadesCiclos
		// Entidades da jurisdição do Escritório ao qual pertenço
		sql := "SELECT DISTINCT d.codigo, b.id_entidade, d.nome, a.abreviatura " +
			" FROM virtus.escritorios a " +
			" LEFT JOIN virtus.jurisdicoes b ON a.id_escritorio = b.id_escritorio " +
			" LEFT JOIN virtus.membros c ON c.id_escritorio = b.id_escritorio " +
			" LEFT JOIN virtus.entidades d ON d.id_entidade = b.id_entidade " +
			" LEFT JOIN virtus.users u ON u.id_user = c.id_usuario " +
			" INNER JOIN virtus.ciclos_entidades e ON e.id_entidade = b.id_entidade " +
			" INNER JOIN virtus.produtos_planos f ON (f.id_entidade = e.id_entidade AND f.id_ciclo = e.id_ciclo) " +
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
				&entidade.Escritorio)
			entidade.Order = i
			i++
			entidades = append(entidades, entidade)
		}
		var entidadesCiclos []mdl.Entidade
		for i, entidade := range entidades {
			var ciclosEntidade []mdl.CicloEntidade
			var cicloEntidade mdl.CicloEntidade
			sql = "SELECT b.id_ciclo, b.nome " +
				" FROM virtus.ciclos_entidades a " +
				" LEFT JOIN virtus.ciclos b ON a.id_ciclo = b.id_ciclo " +
				" WHERE a.id_entidade = ? " +
				" ORDER BY a.id_ciclo_entidade asc"
			rows, _ = Db.Query(sql, entidade.Id)
			defer rows.Close()
			i = 1
			for rows.Next() {
				rows.Scan(&cicloEntidade.Id, &cicloEntidade.Nome)
				cicloEntidade.Order = i
				i++
				ciclosEntidade = append(ciclosEntidade, cicloEntidade)
			}
			entidade.CiclosEntidade = ciclosEntidade
			entidadesCiclos = append(entidadesCiclos, entidade)
		}
		page.Entidades = entidadesCiclos
		page.AppName = mdl.AppName
		page.Title = "Avaliar Planos" + mdl.Ambiente
		page.LoggedUser = BuildLoggedUser(GetUserInCookie(w, r))
		var tmpl = template.Must(template.ParseGlob("tiles/avaliarplanos/*"))
		tmpl.ParseGlob("tiles/*")
		tmpl.ExecuteTemplate(w, "Main-Entidades-Avaliar-Planos", page)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func AvaliarPlanosHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Avaliar Planos Handler")
	if sec.IsAuthenticated(w, r) {
		entidadeId := r.FormValue("EntidadeId")
		cicloId := r.FormValue("CicloId")
		var page mdl.PageProdutosItens
		log.Println(sqlAvaliarPlanos)
		rows, _ := Db.Query(sqlAvaliarPlanos, entidadeId, cicloId)
		defer rows.Close()
		var produtos []mdl.ProdutoItem
		var produto mdl.ProdutoItem
		var i = 1
		var periodoPermitido = 0
		for rows.Next() {
			rows.Scan(
				&produto.EntidadeId,
				&produto.EntidadeNome,
				&produto.CicloId,
				&produto.CicloNome,
				&produto.CicloNota,
				&produto.PilarId,
				&produto.PilarNome,
				&produto.PilarPeso,
				&produto.PilarNota,
				&produto.ComponenteId,
				&produto.ComponenteNome,
				&produto.ComponentePeso,
				&produto.ComponenteNota,
				&produto.SupervisorId,
				&produto.SupervisorName,
				&produto.AuditorId,
				&produto.AuditorName,
				&produto.TipoNotaId,
				&produto.TipoNotaLetra,
				&produto.TipoNotaCorLetra,
				&produto.TipoNotaNome,
				&produto.TipoNotaPeso,
				&produto.TipoNotaNota,
				&produto.ElementoId,
				&produto.ElementoNome,
				&produto.ElementoPeso,
				&produto.ElementoNota,
				&produto.TipoPontuacaoId,
				&produto.PesoPadraoEC,
				&produto.TipoMediaCPId,
				&produto.PesoPadraoCP,
				&produto.TipoMediaPCId,
				&produto.PesoPadraoPC,
				&produto.ItemId,
				&produto.ItemNome,
				&produto.PlanoId,
				&produto.CNPB,
				&produto.RecursoGarantidor,
				&produto.PlanoModalidade,
				&produto.PlanoPeso,
				&produto.PlanoNota,
				&produto.IniciaEm,
				&produto.TerminaEm,
				&periodoPermitido)
			produto.Order = i
			if periodoPermitido == 0 {
				produto.PeriodoPermitido = false
			} else {
				produto.PeriodoPermitido = true
			}
			i++
			//log.Println(produto)
			produtos = append(produtos, produto)
		}
		page.Produtos = produtos

		sql := " SELECT " +
			" a.id_usuario, " +
			" coalesce(b.name,'') " +
			" FROM virtus.integrantes a " +
			" LEFT JOIN virtus.users b " +
			" ON a.id_usuario = b.id_user " +
			" WHERE " +
			" a.id_entidade = " + entidadeId +
			" AND a.id_ciclo = " + cicloId +
			" AND b.id_role = 3 "
		log.Println(sql)
		rows, _ = Db.Query(sql)
		defer rows.Close()
		var supervisores []mdl.User
		var supervisor mdl.User
		i = 1
		for rows.Next() {
			rows.Scan(&supervisor.Id, &supervisor.Name)
			supervisores = append(supervisores, supervisor)
		}
		page.Supervisores = supervisores

		sql = " SELECT " +
			" a.id_usuario, " +
			" b.name " +
			" FROM virtus.integrantes a " +
			" LEFT JOIN virtus.users b " +
			" ON a.id_usuario = b.id_user " +
			" WHERE " +
			" a.id_entidade = " + entidadeId +
			" AND a.id_ciclo = " + cicloId +
			" AND b.id_role in (2,3,4) ORDER BY 2 "
		log.Println(sql)
		rows, _ = Db.Query(sql)
		defer rows.Close()
		var auditores []mdl.User
		var auditor mdl.User
		i = 1
		for rows.Next() {
			rows.Scan(&auditor.Id, &auditor.Name)
			//log.Println("Auditor competente: " + auditor.Name)
			auditores = append(auditores, auditor)
		}
		page.Supervisores = supervisores
		page.Auditores = auditores
		page.AppName = mdl.AppName
		page.Title = "Avaliar Planos" + mdl.Ambiente
		page.LoggedUser = BuildLoggedUser(GetUserInCookie(w, r))
		page.Inc = func(i int) int {
			return i + 1
		}
		var tmpl = template.Must(template.ParseGlob("tiles/avaliarplanos/*"))
		tmpl.ParseGlob("tiles/*")
		tmpl.ExecuteTemplate(w, "Main-Avaliar-Planos", page)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func AtualizarPlanosHandler(entidadeId string, cicloId string, w http.ResponseWriter, r *http.Request) {
	log.Println("Atualizar Planos Handler")
	var page mdl.PageProdutosItens
	log.Println(sqlAvaliarPlanos)
	rows, _ := Db.Query(sqlAvaliarPlanos, entidadeId, cicloId)
	defer rows.Close()
	var produtos []mdl.ProdutoItem
	var produto mdl.ProdutoItem
	var i = 1
	for rows.Next() {
		rows.Scan(
			&produto.EntidadeId,
			&produto.EntidadeNome,
			&produto.CicloId,
			&produto.CicloNome,
			&produto.CicloNota,
			&produto.PilarId,
			&produto.PilarNome,
			&produto.PilarPeso,
			&produto.PilarNota,
			&produto.ComponenteId,
			&produto.ComponenteNome,
			&produto.ComponentePeso,
			&produto.ComponenteNota,
			&produto.SupervisorId,
			&produto.SupervisorName,
			&produto.AuditorId,
			&produto.AuditorName,
			&produto.TipoNotaId,
			&produto.TipoNotaLetra,
			&produto.TipoNotaCorLetra,
			&produto.TipoNotaNome,
			&produto.TipoNotaPeso,
			&produto.TipoNotaNota,
			&produto.ElementoId,
			&produto.ElementoNome,
			&produto.ElementoPeso,
			&produto.ElementoNota,
			&produto.TipoPontuacaoId,
			&produto.PesoPadraoEC,
			&produto.TipoMediaCPId,
			&produto.PesoPadraoCP,
			&produto.TipoMediaPCId,
			&produto.PesoPadraoPC,
			&produto.ItemId,
			&produto.ItemNome,
			&produto.PlanoId,
			&produto.CNPB,
			&produto.RecursoGarantidor,
			&produto.PlanoModalidade,
			&produto.PlanoPeso,
			&produto.PlanoNota,
			&produto.IniciaEm,
			&produto.TerminaEm,
			&produto.PeriodoPermitido)
		produto.Order = i
		i++
		// log.Println(produto)
		produtos = append(produtos, produto)
	}
	page.Produtos = produtos

	sql := " SELECT " +
		" a.id_usuario, " +
		" coalesce(b.name,'') " +
		" FROM virtus.integrantes a " +
		" LEFT JOIN virtus.users b " +
		" ON a.id_usuario = b.id_user " +
		" WHERE " +
		" a.id_entidade = " + entidadeId +
		" AND a.id_ciclo = " + cicloId +
		" AND b.id_role = 3 "
	log.Println(sql)
	rows, _ = Db.Query(sql)
	defer rows.Close()
	var supervisores []mdl.User
	var supervisor mdl.User
	i = 1
	for rows.Next() {
		rows.Scan(&supervisor.Id, &supervisor.Name)
		supervisores = append(supervisores, supervisor)
	}
	page.Supervisores = supervisores

	sql = " SELECT " +
		" a.id_usuario, " +
		" b.name " +
		" FROM virtus.integrantes a " +
		" LEFT JOIN virtus.users b " +
		" ON a.id_usuario = b.id_user " +
		" WHERE " +
		" a.id_entidade = " + entidadeId +
		" AND a.id_ciclo = " + cicloId +
		" AND b.id_role in (2,3,4) "
	log.Println(sql)
	rows, _ = Db.Query(sql)
	defer rows.Close()
	var auditores []mdl.User
	var auditor mdl.User
	i = 1
	for rows.Next() {
		rows.Scan(&auditor.Id, &auditor.Name)
		auditores = append(auditores, auditor)
	}
	page.Supervisores = supervisores
	page.Auditores = auditores
	page.AppName = mdl.AppName
	page.Title = "Avaliar Planos" + mdl.Ambiente
	page.LoggedUser = BuildLoggedUser(GetUserInCookie(w, r))
	page.Inc = func(i int) int {
		return i + 1
	}
	var tmpl = template.Must(template.ParseGlob("tiles/avaliarplanos/*"))
	tmpl.ParseGlob("tiles/*")
	tmpl.ExecuteTemplate(w, "Main-Avaliar-Planos", page)
}

func UpdateAvaliarPlanosHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Update Avaliar Planos Handler")
	entidadeId := ""
	cicloId := ""
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		currentUser := GetUserInCookie(w, r)
		r.ParseForm()
		var produtoElemento mdl.ProdutoElemento
		motivacaoNota := r.FormValue("MotivacaoNota")
		motivacaoPeso := r.FormValue("MotivacaoPeso")
		motivacaoRemocao := r.FormValue("MotivacaoRemocao")
		log.Println("******************")
		log.Println(motivacaoRemocao)
		log.Println("******************")
		for key, value := range r.Form {
			if strings.HasPrefix(key, "AuditorComponente_") {
				s := strings.Split(key, "_")
				log.Println("AuditorId_NEW: " + value[0])
				log.Println("AuditorId_OLD: " + s[5])
				log.Println("Entidade: " + s[1])
				entidadeId = s[1]
				log.Println("Ciclo: " + s[2])
				cicloId = s[2]
				log.Println("Pilar: " + s[3])
				log.Println("Componente: " + s[4])
				produtoElemento.EntidadeId, _ = strconv.ParseInt(s[1], 10, 64)
				produtoElemento.CicloId, _ = strconv.ParseInt(s[2], 10, 64)
				produtoElemento.PilarId, _ = strconv.ParseInt(s[3], 10, 64)
				produtoElemento.ComponenteId, _ = strconv.ParseInt(s[4], 10, 64)
				auditorIdOLD, _ := strconv.ParseInt(s[5], 10, 64)
				produtoElemento.AuditorId, _ = strconv.ParseInt(value[0], 10, 64)
				if auditorIdOLD != produtoElemento.AuditorId {
					produtoElemento.Motivacao = motivacaoRemocao
					//registrarAuditorComponente(produtoElemento)
					//					registrarHistoricoAuditorComponente(produtoElemento, currentUser)
				}
			}
			if strings.HasPrefix(key, "ElementoNota") {
				log.Println("Nota: " + value[0])
				s := strings.Split(key, "_")
				log.Println("Entidade: " + s[1])
				entidadeId = s[1]
				log.Println("Ciclo: " + s[2])
				cicloId = s[2]
				log.Println("Pilar: " + s[3])
				log.Println("Componente: " + s[4])
				log.Println("TipoNota: " + s[5])
				log.Println("Elemento: " + s[6])
				log.Println("NotaAnterior: " + s[7])
				notaOLD, _ := strconv.Atoi(s[7])
				produtoElemento.EntidadeId, _ = strconv.ParseInt(s[1], 10, 64)
				produtoElemento.CicloId, _ = strconv.ParseInt(s[2], 10, 64)
				produtoElemento.PilarId, _ = strconv.ParseInt(s[3], 10, 64)
				produtoElemento.ComponenteId, _ = strconv.ParseInt(s[4], 10, 64)
				produtoElemento.TipoNotaId, _ = strconv.ParseInt(s[5], 10, 64)
				produtoElemento.ElementoId, _ = strconv.ParseInt(s[6], 10, 64)
				produtoElemento.Nota, _ = strconv.Atoi(value[0])
				if notaOLD != produtoElemento.Nota {
					produtoElemento.Motivacao = motivacaoNota
					registrarNotaElemento(produtoElemento, currentUser)
					registrarHistoricoNotaElemento(produtoElemento, currentUser)
				}
			}
			if strings.HasPrefix(key, "ElementoPeso") {
				log.Println("Peso: " + value[0])
				s := strings.Split(key, "_")
				log.Println("Entidade: " + s[1])
				entidadeId = s[1]
				log.Println("Ciclo: " + s[2])
				cicloId = s[2]
				log.Println("Pilar: " + s[3])
				log.Println("Componente: " + s[4])
				log.Println("TipoNota: " + s[5])
				log.Println("Elemento: " + s[6])
				log.Println("PesoAnterior: " + s[7])
				pesoOLD, _ := strconv.ParseFloat(s[7], 10)
				produtoElemento.EntidadeId, _ = strconv.ParseInt(s[1], 10, 64)
				produtoElemento.CicloId, _ = strconv.ParseInt(s[2], 10, 64)
				produtoElemento.PilarId, _ = strconv.ParseInt(s[3], 10, 64)
				produtoElemento.ComponenteId, _ = strconv.ParseInt(s[4], 10, 64)
				produtoElemento.TipoNotaId, _ = strconv.ParseInt(s[5], 10, 64)
				produtoElemento.ElementoId, _ = strconv.ParseInt(s[6], 10, 64)
				produtoElemento.Peso, _ = strconv.ParseFloat(value[0], 64)
				if pesoOLD != produtoElemento.Peso {
					produtoElemento.Motivacao = motivacaoPeso
					registrarPesoElemento(produtoElemento, currentUser)
					registrarHistoricoPesoElemento(produtoElemento, currentUser)
				}
			}
		}
		AtualizarPlanosHandler(entidadeId, cicloId, w, r)
	}
}

func SalvarPesoElemento(w http.ResponseWriter, r *http.Request) {
	log.Println("Salvar Peso Elemento")
	r.ParseForm()
	var entidadeId = r.FormValue("entidadeId")
	var cicloId = r.FormValue("cicloId")
	var pilarId = r.FormValue("pilarId")
	var planoId = r.FormValue("planoId")
	var tipoNotaId = r.FormValue("tipoNotaId")
	var componenteId = r.FormValue("componenteId")
	var elementoId = r.FormValue("elementoId")
	motivacaoPeso := r.FormValue("motivacao")
	peso := r.FormValue("peso")
	var produtoElemento mdl.ProdutoElemento
	produtoElemento.EntidadeId, _ = strconv.ParseInt(entidadeId, 10, 64)
	produtoElemento.CicloId, _ = strconv.ParseInt(cicloId, 10, 64)
	produtoElemento.PilarId, _ = strconv.ParseInt(pilarId, 10, 64)
	produtoElemento.PlanoId, _ = strconv.ParseInt(planoId, 10, 64)
	produtoElemento.TipoNotaId, _ = strconv.ParseInt(tipoNotaId, 10, 64)
	produtoElemento.ComponenteId, _ = strconv.ParseInt(componenteId, 10, 64)
	produtoElemento.ElementoId, _ = strconv.ParseInt(elementoId, 10, 64)
	produtoElemento.Peso, _ = strconv.ParseFloat(peso, 64)
	produtoElemento.Motivacao = motivacaoPeso
	currentUser := GetUserInCookie(w, r)
	valoresAtuais := registrarPesoElemento(produtoElemento, currentUser)
	registrarHistoricoPesoElemento(produtoElemento, currentUser)
	jsonValoresAtuais, _ := json.Marshal(valoresAtuais)
	w.Write([]byte(jsonValoresAtuais))
	log.Println("----------")
}

func SalvarNotaElemento(w http.ResponseWriter, r *http.Request) {
	log.Println("Salvar Nota Elemento")
	r.ParseForm()
	var entidadeId = r.FormValue("entidadeId")
	var cicloId = r.FormValue("cicloId")
	var pilarId = r.FormValue("pilarId")
	var planoId = r.FormValue("planoId")
	var tipoNotaId = r.FormValue("tipoNotaId")
	var componenteId = r.FormValue("componenteId")
	var elementoId = r.FormValue("elementoId")
	motivacaoNota := r.FormValue("motivacao")
	nota := r.FormValue("nota")
	var produtoElemento mdl.ProdutoElemento
	produtoElemento.EntidadeId, _ = strconv.ParseInt(entidadeId, 10, 64)
	produtoElemento.CicloId, _ = strconv.ParseInt(cicloId, 10, 64)
	produtoElemento.PilarId, _ = strconv.ParseInt(pilarId, 10, 64)
	produtoElemento.PlanoId, _ = strconv.ParseInt(planoId, 10, 64)
	produtoElemento.TipoNotaId, _ = strconv.ParseInt(tipoNotaId, 10, 64)
	produtoElemento.ComponenteId, _ = strconv.ParseInt(componenteId, 10, 64)
	produtoElemento.ElementoId, _ = strconv.ParseInt(elementoId, 10, 64)
	produtoElemento.Nota, _ = strconv.Atoi(nota)
	produtoElemento.Motivacao = motivacaoNota
	currentUser := GetUserInCookie(w, r)
	valoresAtuais := registrarNotaElemento(produtoElemento, currentUser)
	registrarHistoricoNotaElemento(produtoElemento, currentUser)
	//log.Println(notasAtuais)
	jsonValoresAtuais, _ := json.Marshal(valoresAtuais)
	w.Write([]byte(jsonValoresAtuais))
	log.Println("----------")

}

func SalvarAuditorComponente(w http.ResponseWriter, r *http.Request) {
	log.Println("Salvar Auditor Componente")
	r.ParseForm()
	var entidadeId = r.FormValue("entidadeId")
	var cicloId = r.FormValue("cicloId")
	var pilarId = r.FormValue("pilarId")
	var componenteId = r.FormValue("componenteId")
	motivacao := r.FormValue("motivacao")
	auditorAnterior := r.FormValue("auditorAnterior")
	auditorNovo := r.FormValue("auditorNovo")
	var produtoComponente mdl.ProdutoComponente
	produtoComponente.EntidadeId, _ = strconv.ParseInt(entidadeId, 10, 64)
	produtoComponente.CicloId, _ = strconv.ParseInt(cicloId, 10, 64)
	produtoComponente.PilarId, _ = strconv.ParseInt(pilarId, 10, 64)
	produtoComponente.ComponenteId, _ = strconv.ParseInt(componenteId, 10, 64)
	produtoComponente.AuditorId, _ = strconv.ParseInt(auditorNovo, 10, 64)
	produtoComponente.AuditorAnteriorId, _ = strconv.ParseInt(auditorAnterior, 10, 64)
	produtoComponente.Motivacao = motivacao
	currentUser := GetUserInCookie(w, r)
	registrarAuditorComponente(produtoComponente, currentUser)
	registrarHistoricoAuditorComponente(produtoComponente, currentUser)
	jsonOK, _ := json.Marshal("OK")
	w.Write(jsonOK)
	log.Println("----------")

}

func SalvarPesoPilar(w http.ResponseWriter, r *http.Request) {
	log.Println("Salvar Peso Pilar")
	r.ParseForm()
	var entidadeId = r.FormValue("entidadeId")
	var cicloId = r.FormValue("cicloId")
	var pilarId = r.FormValue("pilarId")
	motivacaoPeso := r.FormValue("motivacao")
	peso := r.FormValue("peso")
	var produtoPilar mdl.ProdutoPilar
	produtoPilar.EntidadeId, _ = strconv.ParseInt(entidadeId, 10, 64)
	produtoPilar.CicloId, _ = strconv.ParseInt(cicloId, 10, 64)
	produtoPilar.PilarId, _ = strconv.ParseInt(pilarId, 10, 64)
	produtoPilar.Peso, _ = strconv.ParseFloat(peso, 64)
	produtoPilar.Motivacao = motivacaoPeso
	currentUser := GetUserInCookie(w, r)
	cicloNota := registrarPesoPilar(produtoPilar)
	registrarHistoricoPesoPilar(produtoPilar, currentUser)
	w.Write([]byte(cicloNota))
	log.Println("----------")
}

func LoadAnalise(w http.ResponseWriter, r *http.Request) {
	log.Println("Load Analise")
	r.ParseForm()
	var rota = r.FormValue("btn")
	analise := getAnalise(rota)
	w.Write([]byte(analise))
	log.Println("Fim Load Analise")
}

func SalvarAnalise(w http.ResponseWriter, r *http.Request) {
	log.Println("Salvar Analise")
	r.ParseForm()
	var rota = r.FormValue("acionadoPor")
	var analise = r.FormValue("analise")
	retorno := setAnalise(rota, analise)
	w.Write([]byte(retorno))
	log.Println("----------")
}

func LoadDescricao(w http.ResponseWriter, r *http.Request) {
	log.Println("Load Descrição")
	r.ParseForm()
	var rota = r.FormValue("btn")
	descricao := getDescricao(rota)
	jsonDescricao, _ := json.Marshal(descricao)
	w.Write([]byte(jsonDescricao))
	log.Println("JSON Descrição")
}
