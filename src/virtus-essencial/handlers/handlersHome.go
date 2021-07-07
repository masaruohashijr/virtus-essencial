package handlers

import (
	//"encoding/json"
	"html/template"
	"log"
	"net/http"
	"strconv"
	mdl "virtus-essencial/models"
	sec "virtus-essencial/security"
)

func ListSobreHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("About")
	currentUser := GetUserInCookie(w, r)
	if sec.IsAuthenticated(w, r) && HasPermission(currentUser, "listChamados") {
		chamadoEndStatusId := GetEndStatus("chamado")
		sql := "SELECT " +
			" a.id_chamado, " +
			" coalesce(a.titulo,''), " +
			" coalesce(a.descricao,''), " +
			" coalesce(a.id_responsavel,0), " +
			" coalesce(d.name,'') as responsavel_name, " +
			" coalesce(a.id_relator, 0), " +
			" coalesce(e.name,'') as relator_name, " +
			" coalesce(format(a.inicia_em,'dd/MM/yyyy'),''), " +
			" coalesce(format(a.pronto_em,'dd/MM/yyyy'),''), " +
			" a.id_tipo_chamado, " +
			//			" case " +
			//			"   when a.id_tipo_chamado = 'A' then 'Adequação' " +
			//			"   when a.id_tipo_chamado = 'C' then 'Correção' " +
			//			"   when a.id_tipo_chamado = 'D' then 'Dúvida' " +
			//			"   when a.id_tipo_chamado = 'M' then 'Melhoria' " +
			//			"   when a.id_tipo_chamado = 'S' then 'Sugestão' " +
			//			"   else 'Tarefa' " +
			//			" end, " +
			" a.id_prioridade, " +
			//			" case " +
			//			"   when a.prioridade_id = 'A' then 'Alta' " +
			//			"   when a.prioridade_id = 'M' then 'Média' " +
			//			"   else 'Baixa' " +
			//			" end, " +
			" coalesce(a.estimativa,0), " +
			" coalesce(a.id_author,0), " +
			" coalesce(b.name,''), " +
			" coalesce(format(a.criado_em, 'dd/MM/yyyy HH:mm:ss'),''), " +
			" coalesce(c.name,'') as cstatus, " +
			" coalesce(a.id_status,0), " +
			" coalesce(a.id_versao_origem,0) " +
			" FROM virtus.chamados a " +
			" LEFT JOIN virtus.users b ON a.id_author = b.id_user " +
			" LEFT JOIN virtus.status c ON a.id_status = c.id_status " +
			" LEFT JOIN virtus.users d ON a.id_responsavel = d.id_user " +
			" LEFT JOIN virtus.users e ON a.id_relator = e.id_user " +
			" WHERE a.id_status = " + strconv.Itoa(chamadoEndStatusId) +
			" order by a.id_chamado desc"
		log.Println(sql)
		rows, _ := Db.Query(sql)
		defer rows.Close()
		var chamados []mdl.Chamado
		var chamado mdl.Chamado
		var i = 1
		for rows.Next() {
			rows.Scan(
				&chamado.Id,
				&chamado.Titulo,
				&chamado.Descricao,
				&chamado.ResponsavelId,
				&chamado.ResponsavelName,
				&chamado.RelatorId,
				&chamado.RelatorName,
				&chamado.IniciaEm,
				&chamado.ProntoEm,
				&chamado.TipoChamadoId,
				&chamado.PrioridadeId,
				&chamado.Estimativa,
				&chamado.AuthorId,
				&chamado.AuthorName,
				&chamado.C_CriadoEm,
				&chamado.CStatus,
				&chamado.StatusId,
				&chamado.IdVersaoOrigem)
			chamado.Order = i
			i++
			if chamado.TipoChamadoId == "A" {
				chamado.TipoChamadoId = "Adequação"
			} else if chamado.TipoChamadoId == "C" {
				chamado.TipoChamadoId = "Correção"
			} else if chamado.TipoChamadoId == "D" {
				chamado.TipoChamadoId = "Dúvida"
			} else if chamado.TipoChamadoId == "M" {
				chamado.TipoChamadoId = "Melhoria"
			} else if chamado.TipoChamadoId == "S" {
				chamado.TipoChamadoId = "Sugestão"
			} else {
				chamado.TipoChamadoId = "Tarefa"
			}

			if chamado.PrioridadeId == "E" {
				chamado.PrioridadeId = "Essencial"
			} else if chamado.PrioridadeId == "A" {
				chamado.PrioridadeId = "Alta"
			} else if chamado.PrioridadeId == "M" {
				chamado.PrioridadeId = "Média"
			} else if chamado.PrioridadeId == "B" {
				chamado.PrioridadeId = "Baixa"
			} else {
				chamado.PrioridadeId = "Desejável"
			}
			chamados = append(chamados, chamado)
		}
		var page mdl.PageChamados
		page.Chamados = chamados
		page.AppName = mdl.AppName
		page.Title = "Sobre" + mdl.Ambiente
		page.LoggedUser = BuildLoggedUser(GetUserInCookie(w, r))
		var tmpl = template.Must(template.ParseGlob("tiles/paineis/sobre/*"))
		tmpl.ParseGlob("tiles/*")
		tmpl.ExecuteTemplate(w, "Main-Sobre", page)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func ChefeHomeHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("*****************************************")
	log.Println("Painel do Chefe")
	log.Println("*****************************************")
	currentUser := GetUserInCookie(w, r)
	if currentUser.Role != 2 {
		home := redirectHome(currentUser.Role)
		http.Redirect(w, r, home, 301)
	}
	if sec.IsAuthenticated(w, r) && HasPermission(currentUser, "listEntidades") {
		msg := r.FormValue("msg")
		errMsg := r.FormValue("errMsg")
		var page mdl.PageChefe

		sql := " SELECT DISTINCT e.sigla as sigla_entidade, " +
			" d.nome as ciclo_nome, " +
			" c.nome as pilar_nome, " +
			" b.nome as componente_nome, " +
			" h.name as status " +
			" FROM virtus.produtos_componentes a " +
			" INNER JOIN virtus.componentes b ON a.id_componente = b.id_componente " +
			" INNER JOIN virtus.pilares c ON a.id_pilar = c.id_pilar " +
			" INNER JOIN virtus.ciclos d ON a.id_ciclo = d.id_ciclo " +
			" INNER JOIN virtus.entidades e ON a.id_entidade = e.id_entidade " +
			" INNER JOIN virtus.jurisdicoes f ON a.id_entidade = f.id_entidade " +
			" INNER JOIN virtus.escritorios g ON  " +
			" (f.id_escritorio = g.id_escritorio AND e.id_entidade = f.id_entidade) " +
			" INNER JOIN virtus.status h ON h.id_status = a.id_status " +
			" INNER JOIN virtus.actions i ON i.id_origin_status = a.id_status " +
			" INNER JOIN virtus.activities j ON j.id_action = i.id_action " +
			" INNER JOIN virtus.activities_roles k ON k.id_activity = j.id_activity " +
			" INNER JOIN virtus.workflows l ON l.id_workflow = j.id_workflow " +
			" WHERE l.entity_type = 'produto_componente' " +
			" AND k.id_role = ? " +
			" AND g.id_chefe = ? "
		log.Println("sql: " + sql)
		rows, _ := Db.Query(sql, currentUser.Role, currentUser.Id)
		defer rows.Close()

		var produto mdl.ProdutoComponente
		var produtos []mdl.ProdutoComponente
		i := 1
		for rows.Next() {
			rows.Scan(
				&produto.EntidadeNome,
				&produto.CicloNome,
				&produto.PilarNome,
				&produto.ComponenteNome,
				&produto.CStatus)
			produto.Order = i
			i++
			log.Println("Componente: " + produto.ComponenteNome)
			produtos = append(produtos, produto)
		}
		println("------------------------------------")
		println("Pendencias")
		println(len(produtos))
		println("------------------------------------")
		page.Pendencias = produtos

		// List-EFPCEmSupervisaoPermanente
		sql = "SELECT a.id_entidade, " +
			" coalesce(a.sigla,''), " +
			" coalesce(a.nome,''), " +
			" coalesce(a.descricao,''), " +
			" coalesce(a.codigo,''), " +
			" coalesce(a.situacao,''), " +
			" a.esi, " +
			" coalesce(a.municipio,''), " +
			" coalesce(a.sigla_uf,''), " +
			" coalesce(c.abreviatura,'') as escritorio_abrev, " +
			" coalesce(f.nome, '') as ciclo_nome, " +
			" a.id_status, " +
			" coalesce(e.name,'') as cstatus " +
			" FROM " +
			" virtus.entidades a " +
			" INNER JOIN virtus.jurisdicoes b ON b.id_entidade = a.id_entidade " +
			" INNER JOIN VIRTUS.escritorios c ON c.id_escritorio = b.id_escritorio " +
			" INNER JOIN virtus.ciclos_entidades d ON b.id_entidade = d.id_entidade " +
			" LEFT JOIN virtus.status e ON a.id_status = e.id_status " +
			" LEFT JOIN virtus.ciclos f ON f.id_ciclo = d.id_ciclo " +
			" WHERE id_chefe = ? " +
			" AND GETDATE() BETWEEN d.inicia_em AND d.termina_em " +
			" AND d.inicia_em IS NOT NULL " +
			" AND d.termina_em IS NOT NULL " +
			" ORDER BY a.nome asc "
		log.Println("sql: " + sql)
		rows, _ = Db.Query(sql, currentUser.Id)
		defer rows.Close()

		var entidade mdl.Entidade
		var entidadesPermanente []mdl.Entidade
		var j = 1
		for rows.Next() {
			rows.Scan(
				&entidade.Id,
				&entidade.Sigla,
				&entidade.Nome,
				&entidade.Descricao,
				&entidade.Codigo,
				&entidade.Situacao,
				&entidade.ESI,
				&entidade.Municipio,
				&entidade.SiglaUF,
				&entidade.EscritorioAbreviatura,
				&entidade.CicloNome,
				&entidade.StatusId,
				&entidade.CStatus)
			entidade.Order = j
			j++
			//log.Println(entidade)
			entidadesPermanente = append(entidadesPermanente, entidade)
		}
		for j := range entidadesPermanente {
			if entidadesPermanente[j].CicloNome != "" {
				entidadesPermanente[j].CiclosEntidade = ListCiclosEntidadeByEntidadeId(strconv.FormatInt(entidadesPermanente[j].Id, 10))
			}
		}
		page.EntidadesPermanente = entidadesPermanente
		var demaisEntidades []mdl.Entidade
		sql = "SELECT a.id_entidade, " +
			" coalesce(a.sigla,''), " +
			" coalesce(a.nome,''), " +
			" coalesce(a.descricao,''), " +
			" coalesce(a.codigo,''), " +
			" coalesce(a.situacao,''), " +
			" a.esi, " +
			" coalesce(a.municipio,''), " +
			" coalesce(a.sigla_uf,''), " +
			" coalesce(c.abreviatura,'') as escritorio_abrev, " +
			" coalesce(f.nome, '') as ciclo_nome, " +
			" a.id_status, " +
			" coalesce(e.name,'') as cstatus " +
			" FROM " +
			" virtus.entidades a " +
			" INNER JOIN virtus.jurisdicoes b ON b.id_entidade = a.id_entidade " +
			" INNER JOIN VIRTUS.escritorios c ON c.id_escritorio = b.id_escritorio " +
			" LEFT JOIN virtus.ciclos_entidades d ON b.id_entidade = d.id_entidade " +
			" LEFT JOIN virtus.status e ON a.id_status = e.id_status " +
			" LEFT JOIN virtus.ciclos f ON f.id_ciclo = d.id_ciclo " +
			" WHERE id_chefe = ? " +
			" AND ( GETDATE() NOT BETWEEN d.inicia_em AND d.termina_em " +
			" OR d.inicia_em IS NULL " +
			" OR d.termina_em IS NULL ) " +
			" ORDER BY a.nome asc "
		log.Println("sql: " + sql)
		rows, _ = Db.Query(sql, currentUser.Id)
		defer rows.Close()
		var k = 1
		for rows.Next() {
			rows.Scan(
				&entidade.Id,
				&entidade.Sigla,
				&entidade.Nome,
				&entidade.Descricao,
				&entidade.Codigo,
				&entidade.Situacao,
				&entidade.ESI,
				&entidade.Municipio,
				&entidade.SiglaUF,
				&entidade.EscritorioAbreviatura,
				&entidade.CicloNome,
				&entidade.StatusId,
				&entidade.CStatus)
			entidade.Order = k
			k++
			//log.Println(entidade)
			demaisEntidades = append(demaisEntidades, entidade)
		}
		for i := range demaisEntidades {
			if demaisEntidades[i].CicloNome != "" {
				demaisEntidades[i].CiclosEntidade = ListCiclosEntidadeByEntidadeId(strconv.FormatInt(demaisEntidades[i].Id, 10))
			}
		}
		page.DemaisEntidades = demaisEntidades
		if errMsg != "" {
			page.ErrMsg = errMsg
		}
		if msg != "" {
			page.Msg = msg
		}
		sql = "SELECT id_ciclo, nome FROM virtus.ciclos ORDER BY id_ciclo asc"
		rows, _ = Db.Query(sql)
		defer rows.Close()
		var ciclos []mdl.Ciclo
		var ciclo mdl.Ciclo
		i = 1
		for rows.Next() {
			rows.Scan(&ciclo.Id, &ciclo.Nome)
			ciclo.Order = i
			i++
			ciclos = append(ciclos, ciclo)
		}
		page.Ciclos = ciclos
		page.AppName = mdl.AppName
		page.Title = "Chefe" + mdl.Ambiente
		nomeEntidade := ""
		sql = "SELECT nome FROM virtus.escritorios WHERE id_chefe = ? "
		row := Db.QueryRow(sql, currentUser.Id)
		log.Println(sql, currentUser.Id)
		row.Scan(&nomeEntidade)
		page.Escritorio = nomeEntidade
		page.LoggedUser = BuildLoggedUser(GetUserInCookie(w, r))
		var tmpl = template.Must(template.ParseGlob("tiles/paineis/chefe/*"))
		tmpl.ParseGlob("tiles/*")
		tmpl.ExecuteTemplate(w, "Main-Chefes", page)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}
func AdminHomeHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Painel do Admin")
	currentUser := GetUserInCookie(w, r)
	if sec.IsAuthenticated(w, r) && HasPermission(currentUser, "listEntidades") {
		msg := r.FormValue("msg")
		errMsg := r.FormValue("errMsg")
		var page mdl.PageEntidades
		sql := "SELECT " +
			" a.id_entidade, " +
			" coalesce(a.sigla,''), " +
			" coalesce(a.nome,''), " +
			" coalesce(a.descricao,''), " +
			" coalesce(a.codigo,''), " +
			" coalesce(a.situacao,''), " +
			" a.esi, " +
			" coalesce(a.municipio,''), " +
			" coalesce(a.sigla_uf,''), " +
			" coalesce(e.abreviatura,''), " +
			" coalesce(g.nome, '') as ciclo_nome, " +
			" a.id_author, " +
			" coalesce(b.name,'') as author_name, " +
			" FORMAT(a.criado_em,'dd/MM/yyyy HH:mm:ss'), " +
			" a.id_status, " +
			" coalesce(c.name,'') as cstatus, " +
			" a.id_versao_origem " +
			" FROM virtus.entidades a LEFT JOIN virtus.users b " +
			" ON a.id_author = b.id_user " +
			" LEFT JOIN virtus.status c ON a.id_status = c.id_status " +
			" LEFT JOIN virtus.jurisdicoes d ON d.id_entidade = a.id_entidade " +
			" LEFT JOIN virtus.escritorios e ON d.id_escritorio = e.id_escritorio " +
			" LEFT JOIN virtus.ciclos_entidades f ON a.id_entidade = f.id_entidade " +
			" LEFT JOIN virtus.ciclos g ON f.id_ciclo = g.id_ciclo " +
			" ORDER BY a.nome asc "
		log.Println("sql: " + sql)
		rows, _ := Db.Query(sql)
		defer rows.Close()
		var entidades []mdl.Entidade
		var entidade mdl.Entidade
		var i = 1
		for rows.Next() {
			rows.Scan(
				&entidade.Id,
				&entidade.Sigla,
				&entidade.Nome,
				&entidade.Descricao,
				&entidade.Codigo,
				&entidade.Situacao,
				&entidade.ESI,
				&entidade.Municipio,
				&entidade.SiglaUF,
				&entidade.EscritorioAbreviatura,
				&entidade.CicloNome,
				&entidade.AuthorId,
				&entidade.AuthorName,
				&entidade.C_CriadoEm,
				&entidade.StatusId,
				&entidade.CStatus,
				&entidade.IdVersaoOrigem)
			entidade.Order = i
			i++
			//log.Println(entidade)
			entidades = append(entidades, entidade)
		}
		for i := range entidades {
			if entidades[i].CicloNome != "" {
				entidades[i].CiclosEntidade = ListCiclosEntidadeByEntidadeId(strconv.FormatInt(entidades[i].Id, 10))
			}
		}
		page.Entidades = entidades
		if errMsg != "" {
			page.ErrMsg = errMsg
		}
		if msg != "" {
			page.Msg = msg
		}
		sql = "SELECT id_ciclo, nome FROM virtus.ciclos ORDER BY id_ciclo asc"
		rows, _ = Db.Query(sql)
		defer rows.Close()
		var ciclos []mdl.Ciclo
		var ciclo mdl.Ciclo
		i = 1
		for rows.Next() {
			rows.Scan(&ciclo.Id, &ciclo.Nome)
			ciclo.Order = i
			i++
			ciclos = append(ciclos, ciclo)
		}
		page.Ciclos = ciclos
		page.AppName = mdl.AppName
		page.Title = "Admin" + mdl.Ambiente
		page.LoggedUser = BuildLoggedUser(GetUserInCookie(w, r))
		var tmpl = template.Must(template.ParseGlob("tiles/entidades/*"))
		tmpl.ParseGlob("tiles/*")
		tmpl.ExecuteTemplate(w, "Main-Entidades", page)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}
func SupervisorHomeHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("*****************************************")
	log.Println("Painel do Supervisor")
	log.Println("*****************************************")
	currentUser := GetUserInCookie(w, r)
	if currentUser.Role != 3 {
		home := redirectHome(currentUser.Role)
		http.Redirect(w, r, home, 301)
	}
	if sec.IsAuthenticated(w, r) && HasPermission(currentUser, "listEntidades") {
		msg := r.FormValue("msg")
		errMsg := r.FormValue("errMsg")
		var page mdl.PageChefe

		sql := " SELECT DISTINCT e.sigla as sigla_entidade, " +
			" d.nome as ciclo_nome, " +
			" c.nome as pilar_nome, " +
			" b.nome as componente_nome, " +
			" h.name as status " +
			" FROM virtus.produtos_componentes a " +
			" INNER JOIN virtus.componentes b ON a.id_componente = b.id_componente " +
			" INNER JOIN virtus.pilares c ON a.id_pilar = c.id_pilar " +
			" INNER JOIN virtus.ciclos d ON a.id_ciclo = d.id_ciclo " +
			" INNER JOIN virtus.entidades e ON a.id_entidade = e.id_entidade " +
			" INNER JOIN virtus.jurisdicoes f ON a.id_entidade = f.id_entidade " +
			" INNER JOIN virtus.escritorios g ON  " +
			" (f.id_escritorio = g.id_escritorio AND e.id_entidade = f.id_entidade) " +
			" INNER JOIN virtus.status h ON h.id_status = a.id_status " +
			" INNER JOIN virtus.actions i ON i.id_origin_status = a.id_status " +
			" INNER JOIN virtus.activities j ON j.id_action = i.id_action " +
			" INNER JOIN virtus.activities_roles k ON k.id_activity = j.id_activity " +
			" INNER JOIN virtus.workflows l ON l.id_workflow = j.id_workflow " +
			" WHERE l.entity_type = 'produto_componente' " +
			" AND k.id_role = ? " +
			" AND a.id_supervisor = ? "
		log.Println("sql: " + sql)
		rows, _ := Db.Query(sql, currentUser.Role, currentUser.Id)
		defer rows.Close()

		var produto mdl.ProdutoComponente
		var produtos []mdl.ProdutoComponente
		i := 1
		for rows.Next() {
			rows.Scan(
				&produto.EntidadeNome,
				&produto.CicloNome,
				&produto.PilarNome,
				&produto.ComponenteNome,
				&produto.CStatus)
			produto.Order = i
			i++
			log.Println("Componente: " + produto.ComponenteNome)
			produtos = append(produtos, produto)
		}
		println("------------------------------------")
		println("Pendencias")
		println(len(produtos))
		println("------------------------------------")
		page.Pendencias = produtos

		// List-EFPCEmSupervisaoPermanente
		sql = "SELECT a.id_entidade, " +
			" coalesce(a.sigla,''), " +
			" coalesce(a.nome,''), " +
			" coalesce(a.descricao,''), " +
			" coalesce(a.codigo,''), " +
			" coalesce(a.situacao,''), " +
			" a.esi, " +
			" coalesce(a.municipio,''), " +
			" coalesce(a.sigla_uf,''), " +
			" coalesce(c.abreviatura,'') as escritorio_abrev, " +
			" coalesce(f.nome, '') as ciclo_nome, " +
			" a.id_status, " +
			" coalesce(e.name,'') as cstatus " +
			" FROM " +
			" virtus.entidades a " +
			" INNER JOIN virtus.jurisdicoes b ON b.id_entidade = a.id_entidade " +
			" INNER JOIN VIRTUS.escritorios c ON c.id_escritorio = b.id_escritorio " +
			" INNER JOIN virtus.ciclos_entidades d ON b.id_entidade = d.id_entidade " +
			" LEFT JOIN virtus.status e ON a.id_status = e.id_status " +
			" LEFT JOIN virtus.ciclos f ON f.id_ciclo = d.id_ciclo " +
			" WHERE d.id_supervisor = ? " +
			" AND GETDATE() BETWEEN d.inicia_em AND d.termina_em " +
			" AND d.inicia_em IS NOT NULL " +
			" AND d.termina_em IS NOT NULL " +
			" ORDER BY a.nome asc "
		log.Println("sql: " + sql)
		rows, _ = Db.Query(sql, currentUser.Id)
		defer rows.Close()

		var entidade mdl.Entidade
		var entidadesPermanente []mdl.Entidade
		var j = 1
		for rows.Next() {
			rows.Scan(
				&entidade.Id,
				&entidade.Sigla,
				&entidade.Nome,
				&entidade.Descricao,
				&entidade.Codigo,
				&entidade.Situacao,
				&entidade.ESI,
				&entidade.Municipio,
				&entidade.SiglaUF,
				&entidade.EscritorioAbreviatura,
				&entidade.CicloNome,
				&entidade.StatusId,
				&entidade.CStatus)
			entidade.Order = j
			j++
			//log.Println(entidade)
			entidadesPermanente = append(entidadesPermanente, entidade)
		}
		for j := range entidadesPermanente {
			if entidadesPermanente[j].CicloNome != "" {
				entidadesPermanente[j].CiclosEntidade = ListCiclosEntidadeByEntidadeId(strconv.FormatInt(entidadesPermanente[j].Id, 10))
			}
		}
		page.EntidadesPermanente = entidadesPermanente
		var demaisEntidades []mdl.Entidade
		sql = "SELECT a.id_entidade, " +
			" coalesce(a.sigla,''), " +
			" coalesce(a.nome,''), " +
			" coalesce(a.descricao,''), " +
			" coalesce(a.codigo,''), " +
			" coalesce(a.situacao,''), " +
			" a.esi, " +
			" coalesce(a.municipio,''), " +
			" coalesce(a.sigla_uf,''), " +
			" coalesce(c.abreviatura,'') as escritorio_abrev, " +
			" coalesce(f.nome, '') as ciclo_nome, " +
			" a.id_status, " +
			" coalesce(e.name,'') as cstatus " +
			" FROM " +
			" virtus.entidades a " +
			" INNER JOIN virtus.jurisdicoes b ON b.id_entidade = a.id_entidade " +
			" INNER JOIN VIRTUS.escritorios c ON c.id_escritorio = b.id_escritorio " +
			" LEFT JOIN virtus.ciclos_entidades d ON b.id_entidade = d.id_entidade " +
			" LEFT JOIN virtus.status e ON a.id_status = e.id_status " +
			" LEFT JOIN virtus.ciclos f ON f.id_ciclo = d.id_ciclo " +
			" WHERE d.id_supervisor = ? " +
			" AND ( GETDATE() NOT BETWEEN d.inicia_em AND d.termina_em " +
			" OR d.inicia_em IS NULL " +
			" OR d.termina_em IS NULL ) " +
			" ORDER BY a.nome asc "
		log.Println("sql: " + sql)
		rows, _ = Db.Query(sql, currentUser.Id)
		defer rows.Close()
		var k = 1
		for rows.Next() {
			rows.Scan(
				&entidade.Id,
				&entidade.Sigla,
				&entidade.Nome,
				&entidade.Descricao,
				&entidade.Codigo,
				&entidade.Situacao,
				&entidade.ESI,
				&entidade.Municipio,
				&entidade.SiglaUF,
				&entidade.EscritorioAbreviatura,
				&entidade.CicloNome,
				&entidade.StatusId,
				&entidade.CStatus)
			entidade.Order = k
			k++
			//log.Println(entidade)
			demaisEntidades = append(demaisEntidades, entidade)
		}
		for i := range demaisEntidades {
			if demaisEntidades[i].CicloNome != "" {
				demaisEntidades[i].CiclosEntidade = ListCiclosEntidadeByEntidadeId(strconv.FormatInt(demaisEntidades[i].Id, 10))
			}
		}
		page.DemaisEntidades = demaisEntidades
		if errMsg != "" {
			page.ErrMsg = errMsg
		}
		if msg != "" {
			page.Msg = msg
		}
		sql = "SELECT id_ciclo, nome FROM virtus.ciclos ORDER BY id_ciclo asc"
		rows, _ = Db.Query(sql)
		defer rows.Close()
		var ciclos []mdl.Ciclo
		var ciclo mdl.Ciclo
		i = 1
		for rows.Next() {
			rows.Scan(&ciclo.Id, &ciclo.Nome)
			ciclo.Order = i
			i++
			ciclos = append(ciclos, ciclo)
		}
		page.Ciclos = ciclos
		page.AppName = mdl.AppName
		page.Title = "Supervisor" + mdl.Ambiente
		nomeEntidade := ""
		sql = " SELECT a.nome FROM virtus.escritorios a " +
			" INNER JOIN virtus.membros b ON a.id_escritorio = b.id_escritorio " +
			" WHERE b.id_usuario = ? "
		row := Db.QueryRow(sql, currentUser.Id)
		log.Println(sql, currentUser.Id)
		row.Scan(&nomeEntidade)
		page.Escritorio = nomeEntidade
		page.LoggedUser = BuildLoggedUser(GetUserInCookie(w, r))
		var tmpl = template.Must(template.ParseGlob("tiles/paineis/supervisor/*"))
		tmpl.ParseGlob("tiles/*")
		tmpl.ExecuteTemplate(w, "Main-Supervisores", page)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}

}
func AuditorHomeHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("*****************************************")
	log.Println("Painel do Auditor")
	log.Println("*****************************************")
	currentUser := GetUserInCookie(w, r)
	if currentUser.Role != 4 {
		home := redirectHome(currentUser.Role)
		http.Redirect(w, r, home, 301)
	}
	if sec.IsAuthenticated(w, r) && HasPermission(currentUser, "listEntidades") {
		msg := r.FormValue("msg")
		errMsg := r.FormValue("errMsg")
		var page mdl.PageChefe

		sql := " SELECT DISTINCT e.id_entidade, e.sigla as sigla_entidade, " +
			" d.id_ciclo, d.nome as ciclo_nome, " +
			" c.id_pilar, c.nome as pilar_nome, " +
			" b.id_componente, b.nome as componente_nome, " +
			" h.name as status " +
			" FROM virtus.produtos_componentes a " +
			" INNER JOIN virtus.componentes b ON a.id_componente = b.id_componente " +
			" INNER JOIN virtus.pilares c ON a.id_pilar = c.id_pilar " +
			" INNER JOIN virtus.ciclos d ON a.id_ciclo = d.id_ciclo " +
			" INNER JOIN virtus.entidades e ON a.id_entidade = e.id_entidade " +
			" INNER JOIN virtus.jurisdicoes f ON a.id_entidade = f.id_entidade " +
			" INNER JOIN virtus.escritorios g ON  " +
			" (f.id_escritorio = g.id_escritorio AND e.id_entidade = f.id_entidade) " +
			" INNER JOIN virtus.status h ON h.id_status = a.id_status " +
			" INNER JOIN virtus.actions i ON i.id_origin_status = a.id_status " +
			" INNER JOIN virtus.activities j ON j.id_action = i.id_action " +
			" INNER JOIN virtus.activities_roles k ON k.id_activity = j.id_activity " +
			" INNER JOIN virtus.workflows l ON l.id_workflow = j.id_workflow " +
			" WHERE l.entity_type = 'produto_componente' " +
			" AND k.id_role = ? " +
			" AND a.id_auditor = ? "
		log.Println("sql: " + sql)
		rows, _ := Db.Query(sql, currentUser.Role, currentUser.Id)
		defer rows.Close()

		var produto mdl.ProdutoComponente
		var produtos []mdl.ProdutoComponente
		i := 1
		for rows.Next() {
			rows.Scan(
				&produto.EntidadeId,
				&produto.EntidadeNome,
				&produto.CicloId,
				&produto.CicloNome,
				&produto.PilarId,
				&produto.PilarNome,
				&produto.ComponenteId,
				&produto.ComponenteNome,
				&produto.CStatus)
			produto.Order = i
			i++
			log.Println("Componente: " + produto.ComponenteNome)
			produtos = append(produtos, produto)
		}
		println("------------------------------------")
		println("Pendencias")
		println(len(produtos))
		println("------------------------------------")
		page.Pendencias = produtos

		// List-EFPCEmSupervisaoPermanente
		sql = "SELECT a.id_entidade, " +
			" coalesce(a.sigla,''), " +
			" coalesce(a.nome,''), " +
			" coalesce(a.descricao,''), " +
			" coalesce(a.codigo,''), " +
			" coalesce(a.situacao,''), " +
			" a.esi, " +
			" coalesce(a.municipio,''), " +
			" coalesce(a.sigla_uf,''), " +
			" coalesce(c.abreviatura,'') as escritorio_abrev, " +
			" coalesce(f.nome, '') as ciclo_nome, " +
			" a.id_status, " +
			" coalesce(e.name,'') as cstatus " +
			" FROM " +
			" virtus.entidades a " +
			" INNER JOIN virtus.jurisdicoes b ON b.id_entidade = a.id_entidade " +
			" INNER JOIN virtus.escritorios c ON c.id_escritorio = b.id_escritorio " +
			" INNER JOIN virtus.ciclos_entidades d ON b.id_entidade = d.id_entidade " +
			" INNER JOIN virtus.integrantes g ON g.id_entidade = d.id_entidade AND g.id_ciclo = d.id_ciclo " +
			" LEFT JOIN virtus.status e ON a.id_status = e.id_status " +
			" LEFT JOIN virtus.ciclos f ON f.id_ciclo = d.id_ciclo " +
			" WHERE g.id_usuario = ? " +
			" AND GETDATE() BETWEEN d.inicia_em AND d.termina_em " +
			" AND d.inicia_em IS NOT NULL " +
			" AND d.termina_em IS NOT NULL " +
			" ORDER BY a.nome asc "
		log.Println("sql: " + sql)
		rows, _ = Db.Query(sql, currentUser.Id)
		defer rows.Close()

		var entidade mdl.Entidade
		var entidadesPermanente []mdl.Entidade
		var j = 1
		for rows.Next() {
			rows.Scan(
				&entidade.Id,
				&entidade.Sigla,
				&entidade.Nome,
				&entidade.Descricao,
				&entidade.Codigo,
				&entidade.Situacao,
				&entidade.ESI,
				&entidade.Municipio,
				&entidade.SiglaUF,
				&entidade.EscritorioAbreviatura,
				&entidade.CicloNome,
				&entidade.StatusId,
				&entidade.CStatus)
			entidade.Order = j
			j++
			//log.Println(entidade)
			entidadesPermanente = append(entidadesPermanente, entidade)
		}
		for j := range entidadesPermanente {
			if entidadesPermanente[j].CicloNome != "" {
				entidadesPermanente[j].CiclosEntidade = ListCiclosEntidadeByEntidadeId(strconv.FormatInt(entidadesPermanente[j].Id, 10))
			}
		}
		page.EntidadesPermanente = entidadesPermanente
		var demaisEntidades []mdl.Entidade
		sql = "SELECT a.id_entidade, " +
			" coalesce(a.sigla,''), " +
			" coalesce(a.nome,''), " +
			" coalesce(a.descricao,''), " +
			" coalesce(a.codigo,''), " +
			" coalesce(a.situacao,''), " +
			" a.esi, " +
			" coalesce(a.municipio,''), " +
			" coalesce(a.sigla_uf,''), " +
			" coalesce(c.abreviatura,'') as escritorio_abrev, " +
			" coalesce(f.nome, '') as ciclo_nome, " +
			" a.id_status, " +
			" coalesce(e.name,'') as cstatus " +
			" FROM " +
			" virtus.entidades a " +
			" INNER JOIN virtus.jurisdicoes b ON b.id_entidade = a.id_entidade " +
			" INNER JOIN virtus.escritorios c ON c.id_escritorio = b.id_escritorio " +
			" INNER JOIN virtus.ciclos_entidades d ON b.id_entidade = d.id_entidade " +
			" INNER JOIN virtus.integrantes g ON g.id_entidade = d.id_entidade AND g.id_ciclo = d.id_ciclo " +
			" LEFT JOIN virtus.status e ON a.id_status = e.id_status " +
			" LEFT JOIN virtus.ciclos f ON f.id_ciclo = d.id_ciclo " +
			" WHERE g.id_usuario = ? " +
			" AND ( GETDATE() NOT BETWEEN d.inicia_em AND d.termina_em " +
			" OR d.inicia_em IS NULL " +
			" OR d.termina_em IS NULL ) " +
			" ORDER BY a.nome asc "
		log.Println("sql: " + sql)
		rows, _ = Db.Query(sql, currentUser.Id)
		defer rows.Close()
		var k = 1
		for rows.Next() {
			rows.Scan(
				&entidade.Id,
				&entidade.Sigla,
				&entidade.Nome,
				&entidade.Descricao,
				&entidade.Codigo,
				&entidade.Situacao,
				&entidade.ESI,
				&entidade.Municipio,
				&entidade.SiglaUF,
				&entidade.EscritorioAbreviatura,
				&entidade.CicloNome,
				&entidade.StatusId,
				&entidade.CStatus)
			entidade.Order = k
			k++
			//log.Println(entidade)
			demaisEntidades = append(demaisEntidades, entidade)
		}
		for i := range demaisEntidades {
			if demaisEntidades[i].CicloNome != "" {
				demaisEntidades[i].CiclosEntidade = ListCiclosEntidadeByEntidadeId(strconv.FormatInt(demaisEntidades[i].Id, 10))
			}
		}
		page.DemaisEntidades = demaisEntidades
		if errMsg != "" {
			page.ErrMsg = errMsg
		}
		if msg != "" {
			page.Msg = msg
		}
		sql = "SELECT id_ciclo, nome FROM virtus.ciclos ORDER BY id_ciclo asc"
		rows, _ = Db.Query(sql)
		defer rows.Close()
		var ciclos []mdl.Ciclo
		var ciclo mdl.Ciclo
		i = 1
		for rows.Next() {
			rows.Scan(&ciclo.Id, &ciclo.Nome)
			ciclo.Order = i
			i++
			ciclos = append(ciclos, ciclo)
		}
		page.Ciclos = ciclos
		page.AppName = mdl.AppName
		page.Title = "Auditor" + mdl.Ambiente
		nomeEntidade := ""
		sql = " SELECT a.nome FROM virtus.escritorios a " +
			" INNER JOIN virtus.membros b ON a.id_escritorio = b.id_escritorio " +
			" WHERE b.id_usuario = ? "
		row := Db.QueryRow(sql, currentUser.Id)
		log.Println(sql, currentUser.Id)
		row.Scan(&nomeEntidade)
		page.Escritorio = nomeEntidade
		page.LoggedUser = BuildLoggedUser(GetUserInCookie(w, r))
		var tmpl = template.Must(template.ParseGlob("tiles/paineis/auditor/*"))
		tmpl.ParseGlob("tiles/*")
		tmpl.ExecuteTemplate(w, "Main-Auditores", page)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}
func VisualizadorHomeHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Painel do Visualizador")
	currentUser := GetUserInCookie(w, r)
	if sec.IsAuthenticated(w, r) && HasPermission(currentUser, "listEntidades") {
		msg := r.FormValue("msg")
		errMsg := r.FormValue("errMsg")
		var page mdl.PageEntidades
		sql := "SELECT " +
			" a.id_entidade, " +
			" coalesce(a.sigla,''), " +
			" coalesce(a.nome,''), " +
			" coalesce(a.descricao,''), " +
			" coalesce(a.codigo,''), " +
			" coalesce(a.situacao,''), " +
			" a.esi, " +
			" coalesce(a.municipio,''), " +
			" coalesce(a.sigla_uf,''), " +
			" coalesce(e.abreviatura,''), " +
			" coalesce(g.nome, '') as ciclo_nome, " +
			" a.id_author, " +
			" coalesce(b.name,'') as author_name, " +
			" FORMAT(a.criado_em,'dd/MM/yyyy HH:mm:ss'), " +
			" a.id_status, " +
			" coalesce(c.name,'') as cstatus, " +
			" a.id_versao_origem " +
			" FROM virtus.entidades a LEFT JOIN virtus.users b " +
			" ON a.id_author = b.id_user " +
			" LEFT JOIN virtus.status c ON a.id_status = c.id_status " +
			" LEFT JOIN virtus.jurisdicoes d ON d.id_entidade = a.id_entidade " +
			" LEFT JOIN virtus.escritorios e ON d.id_escritorio = e.id_escritorio " +
			" LEFT JOIN virtus.ciclos_entidades f ON a.id_entidade = f.id_entidade " +
			" LEFT JOIN virtus.ciclos g ON f.id_ciclo = g.id_ciclo " +
			" ORDER BY a.nome asc "
		log.Println("sql: " + sql)
		rows, _ := Db.Query(sql)
		defer rows.Close()
		var entidades []mdl.Entidade
		var entidade mdl.Entidade
		var i = 1
		for rows.Next() {
			rows.Scan(
				&entidade.Id,
				&entidade.Sigla,
				&entidade.Nome,
				&entidade.Descricao,
				&entidade.Codigo,
				&entidade.Situacao,
				&entidade.ESI,
				&entidade.Municipio,
				&entidade.SiglaUF,
				&entidade.EscritorioAbreviatura,
				&entidade.CicloNome,
				&entidade.AuthorId,
				&entidade.AuthorName,
				&entidade.C_CriadoEm,
				&entidade.StatusId,
				&entidade.CStatus,
				&entidade.IdVersaoOrigem)
			entidade.Order = i
			i++
			//log.Println(entidade)
			entidades = append(entidades, entidade)
		}
		for i := range entidades {
			if entidades[i].CicloNome != "" {
				entidades[i].CiclosEntidade = ListCiclosEntidadeByEntidadeId(strconv.FormatInt(entidades[i].Id, 10))
			}
		}
		page.Entidades = entidades
		if errMsg != "" {
			page.ErrMsg = errMsg
		}
		if msg != "" {
			page.Msg = msg
		}
		sql = "SELECT id_ciclo, nome FROM virtus.ciclos ORDER BY id_ciclo asc"
		rows, _ = Db.Query(sql)
		defer rows.Close()
		var ciclos []mdl.Ciclo
		var ciclo mdl.Ciclo
		i = 1
		for rows.Next() {
			rows.Scan(&ciclo.Id, &ciclo.Nome)
			ciclo.Order = i
			i++
			ciclos = append(ciclos, ciclo)
		}
		page.Ciclos = ciclos
		page.AppName = mdl.AppName
		page.Title = "Visualizador" + mdl.Ambiente
		page.LoggedUser = BuildLoggedUser(GetUserInCookie(w, r))
		var tmpl = template.Must(template.ParseGlob("tiles/entidades/*"))
		tmpl.ParseGlob("tiles/*")
		tmpl.ExecuteTemplate(w, "Main-Entidades", page)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}
func DesenvolvedorHomeHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Painel do Desenvolvedor")
	currentUser := GetUserInCookie(w, r)
	if sec.IsAuthenticated(w, r) && HasPermission(currentUser, "listEntidades") {
		msg := r.FormValue("msg")
		errMsg := r.FormValue("errMsg")
		var page mdl.PageEntidades
		sql := "SELECT " +
			" a.id_entidade, " +
			" coalesce(a.sigla,''), " +
			" coalesce(a.nome,''), " +
			" coalesce(a.descricao,''), " +
			" coalesce(a.codigo,''), " +
			" coalesce(a.situacao,''), " +
			" a.esi, " +
			" coalesce(a.municipio,''), " +
			" coalesce(a.sigla_uf,''), " +
			" coalesce(e.abreviatura,''), " +
			" coalesce(g.nome, '') as ciclo_nome, " +
			" a.id_author, " +
			" coalesce(b.name,'') as author_name, " +
			" FORMAT(a.criado_em,'dd/MM/yyyy HH:mm:ss'), " +
			" a.id_status, " +
			" coalesce(c.name,'') as cstatus, " +
			" a.id_versao_origem " +
			" FROM virtus.entidades a LEFT JOIN virtus.users b " +
			" ON a.id_author = b.id_user " +
			" LEFT JOIN virtus.status c ON a.id_status = c.id_status " +
			" LEFT JOIN virtus.jurisdicoes d ON d.id_entidade = a.id_entidade " +
			" LEFT JOIN virtus.escritorios e ON d.id_escritorio = e.id_escritorio " +
			" LEFT JOIN virtus.ciclos_entidades f ON a.id_entidade = f.id_entidade " +
			" LEFT JOIN virtus.ciclos g ON f.id_ciclo = g.id_ciclo " +
			" ORDER BY a.nome asc "
		log.Println("sql: " + sql)
		rows, _ := Db.Query(sql)
		defer rows.Close()
		var entidades []mdl.Entidade
		var entidade mdl.Entidade
		var i = 1
		for rows.Next() {
			rows.Scan(
				&entidade.Id,
				&entidade.Sigla,
				&entidade.Nome,
				&entidade.Descricao,
				&entidade.Codigo,
				&entidade.Situacao,
				&entidade.ESI,
				&entidade.Municipio,
				&entidade.SiglaUF,
				&entidade.EscritorioAbreviatura,
				&entidade.CicloNome,
				&entidade.AuthorId,
				&entidade.AuthorName,
				&entidade.C_CriadoEm,
				&entidade.StatusId,
				&entidade.CStatus,
				&entidade.IdVersaoOrigem)
			entidade.Order = i
			i++
			//log.Println(entidade)
			entidades = append(entidades, entidade)
		}
		for i := range entidades {
			if entidades[i].CicloNome != "" {
				entidades[i].CiclosEntidade = ListCiclosEntidadeByEntidadeId(strconv.FormatInt(entidades[i].Id, 10))
			}
		}
		page.Entidades = entidades
		if errMsg != "" {
			page.ErrMsg = errMsg
		}
		if msg != "" {
			page.Msg = msg
		}
		sql = "SELECT id_ciclo, nome FROM virtus.ciclos ORDER BY id_ciclo asc"
		rows, _ = Db.Query(sql)
		defer rows.Close()
		var ciclos []mdl.Ciclo
		var ciclo mdl.Ciclo
		i = 1
		for rows.Next() {
			rows.Scan(&ciclo.Id, &ciclo.Nome)
			ciclo.Order = i
			i++
			ciclos = append(ciclos, ciclo)
		}
		page.Ciclos = ciclos
		page.AppName = mdl.AppName
		page.Title = "Desenvolvedor" + mdl.Ambiente
		page.LoggedUser = BuildLoggedUser(GetUserInCookie(w, r))
		var tmpl = template.Must(template.ParseGlob("tiles/entidades/*"))
		tmpl.ParseGlob("tiles/*")
		tmpl.ExecuteTemplate(w, "Main-Entidades", page)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}
