package handlers

import (
	//"encoding/json"
	"html/template"
	"log"
	"net/http"
	"strconv"
	"strings"
	mdl "virtus-essencial/models"
	route "virtus-essencial/routes"
	sec "virtus-essencial/security"
)

func CreateAnotacaoHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Create Anotacao")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		currentUser := GetUserInCookie(w, r)
		entidade := r.FormValue("Entidade")
		log.Println(entidade)
		assunto := r.FormValue("Assunto")
		log.Println(assunto)
		risco := r.FormValue("Risco")
		log.Println(risco)
		tendencia := r.FormValue("Tendencia")
		log.Println(tendencia)
		relator := r.FormValue("Relator")
		log.Println(relator)
		responsavel := r.FormValue("Responsavel")
		log.Println(responsavel)
		descricao := r.FormValue("Descricao")
		log.Println(descricao)
		matriz := r.FormValue("Matriz")
		log.Println(matriz)
		sqlStatement := "INSERT INTO virtus.anotacoes(" +
			" id_entidade, " +
			" assunto, " +
			" risco, " +
			" tendencia, " +
			" id_relator, " +
			" id_responsavel, " +
			" descricao, " +
			" matriz, " +
			" id_author, " +
			" criado_em) " +
			" OUTPUT INSERTED.id_anotacao " +
			" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE())"
		idAnotacao := 0
		row := Db.QueryRow(sqlStatement,
			entidade,
			assunto,
			risco,
			tendencia,
			relator,
			responsavel,
			descricao,
			matriz,
			currentUser.Id)
		log.Println(sqlStatement)
		err := row.Scan(&idAnotacao)
		if err != nil {
			log.Println(err.Error())
		}
		log.Println("INSERT: Id: " + strconv.Itoa(idAnotacao) + " - Assunto: " + assunto)
		/*
			for key, value := range r.Form {
				if strings.HasPrefix(key, "anotacaoAnotacao") {
					array := strings.Split(value[0], "#")
					log.Println(value[0])
					anotacaoAnotacaoId := 0
					anotacaoId := strings.Split(array[3], ":")[1]
					sqlStatement := " INSERT INTO " +
						" anotacoes_radares( " +
						" radar_id, " +
						" id_anotacao, " +
						" registro_ata, " +
						" id_author, " +
						" criado_em ) " +
						" VALUES (?, ?, ?, ?, ?) RETURNING id"
					log.Println(sqlStatement)
					err := Db.QueryRow(
						sqlStatement,
						idAnotacao,
						anotacaoId,
						tipoMediaId,
						pesoPadrao,
						currentUser.Id,
						time.Now()).Scan(&anotacaoAnotacaoId)
					if err != nil {
						log.Println(err.Error())
					}
				}
			}*/
		http.Redirect(w, r, route.AnotacoesRoute+"?msg=Anotação criada com sucesso.", 301)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func UpdateAnotacaoHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Update Anotacao")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		//currentUser := GetUserInCookie(w, r)
		anotacaoId := r.FormValue("Id")
		log.Println(anotacaoId)
		entidade := r.FormValue("Entidade")
		log.Println(entidade)
		assunto := r.FormValue("Assunto")
		log.Println(assunto)
		risco := r.FormValue("Risco")
		log.Println(risco)
		tendencia := r.FormValue("Tendencia")
		log.Println(tendencia)
		relator := r.FormValue("Relator")
		log.Println(relator)
		responsavel := r.FormValue("Responsavel")
		log.Println(responsavel)
		descricao := r.FormValue("Descricao")
		log.Println(descricao)
		matriz := r.FormValue("Matriz")
		log.Println(matriz)
		sqlStatement := " UPDATE virtus.anotacoes " +
			" SET id_entidade=?, " +
			"     assunto=?, " +
			"     risco=?, " +
			"     tendencia=?, " +
			"     id_relator=?, " +
			"     id_responsavel=?, " +
			"     descricao=?, " +
			"     matriz=? " +
			" WHERE id_anotacao = ? "
		updtForm, _ := Db.Prepare(sqlStatement)
		_, err := updtForm.Exec(entidade,
			assunto,
			risco,
			tendencia,
			relator,
			responsavel,
			descricao,
			matriz, anotacaoId)
		if err != nil {
			log.Println(err.Error())
		}
		log.Println("UPDATE: Id: " + anotacaoId + " | Assunto: " + assunto)

		// Anotacoes Anotacoes
		/*var anotacoesAnotacaoDB = ListAnotacoesByAnotacaoId(radarId)
		var anotacoesAnotacaoPage []mdl.PilarAnotacao
		var pilarAnotacaoPage mdl.PilarAnotacao
		for key, value := range r.Form {
			if strings.HasPrefix(key, "pilarAnotacao") {
				log.Println(value[0])
				array := strings.Split(value[0], "#")
				id := strings.Split(array[1], ":")[1]
				log.Println("Id -------- " + id)
				pilarAnotacaoPage.Id, _ = strconv.ParseInt(id, 10, 64)
				pilarAnotacaoPage.AnotacaoId, _ = strconv.ParseInt(radarId, 10, 64)
				pilarId := strings.Split(array[3], ":")[1]
				log.Println("pilarId -------- " + pilarId)
				pilarAnotacaoPage.PilarId, _ = strconv.ParseInt(pilarId, 10, 64)
				pilarNome := strings.Split(array[4], ":")[1]
				log.Println("pilarNome -------- " + pilarNome)
				pilarAnotacaoPage.PilarNome = pilarNome
				tipoMediaId := strings.Split(array[5], ":")[1]
				log.Println("tipoMediaId -------- " + tipoMediaId)
				pilarAnotacaoPage.TipoMediaId, _ = strconv.Atoi(tipoMediaId)
				tipoMedia := strings.Split(array[6], ":")[1]
				log.Println("tipoMedia -------- " + tipoMedia)
				pilarAnotacaoPage.TipoMedia = tipoMedia
				pesoPadrao := strings.Split(array[7], ":")[1]
				log.Println("pesoPadrao -------- " + pesoPadrao)
				pilarAnotacaoPage.PesoPadrao = pesoPadrao
				authorId := strings.Split(array[8], ":")[1]
				log.Println("authorId -------- " + authorId)
				pilarAnotacaoPage.AuthorId, _ = strconv.ParseInt(authorId, 10, 64)
				authorName := strings.Split(array[9], ":")[1]
				log.Println("authorName -------- " + authorName)
				pilarAnotacaoPage.AuthorName = authorName
				criadoEm := strings.Split(array[10], ":")[1]
				log.Println("criadoEm -------- " + criadoEm)
				pilarAnotacaoPage.CriadoEm = criadoEm
				idVersaoOrigem := strings.Split(array[11], ":")[1]
				log.Println("idVersaoOrigem -------- " + idVersaoOrigem)
				pilarAnotacaoPage.IdVersaoOrigem, _ = strconv.ParseInt(idVersaoOrigem, 10, 64)
				statusId := strings.Split(array[12], ":")[1]
				log.Println("StatusId -------- " + statusId)
				pilarAnotacaoPage.StatusId, _ = strconv.ParseInt(statusId, 10, 64)
				cStatus := strings.Split(array[13], ":")[1]
				log.Println("cStatus -------- " + cStatus)
				pilarAnotacaoPage.CStatus = cStatus
				anotacoesAnotacaoPage = append(anotacoesAnotacaoPage, pilarAnotacaoPage)
			}
		}
		if len(anotacoesAnotacaoPage) < len(anotacoesAnotacaoDB) {
			log.Println("Quantidade de Anotacoes do Anotacao da Página: " + strconv.Itoa(len(anotacoesAnotacaoPage)))
			if len(anotacoesAnotacaoPage) == 0 {
				DeleteAnotacoesAnotacaoByAnotacaoId(radarId) //DONE
			} else {
				var diffDB []mdl.PilarAnotacao = anotacoesAnotacaoDB
				for n := range anotacoesAnotacaoPage {
					if containsPilarAnotacao(diffDB, anotacoesAnotacaoPage[n]) {
						diffDB = removePilarAnotacao(diffDB, anotacoesAnotacaoPage[n])
					}
				}
				DeleteAnotacoesAnotacaoHandler(diffDB) //DONE
			}
		} else {
			var diffPage []mdl.PilarAnotacao = anotacoesAnotacaoPage
			for n := range anotacoesAnotacaoDB {
				if containsPilarAnotacao(diffPage, anotacoesAnotacaoDB[n]) {
					diffPage = removePilarAnotacao(diffPage, anotacoesAnotacaoDB[n])
				}
			}
			var pilarAnotacao mdl.PilarAnotacao
			pilarAnotacaoId := 0
			// statusItemId := GetStartStatus("plano")
			for i := range diffPage {
				pilarAnotacao = diffPage[i]
				log.Println("Anotacao Id: " + radarId)
				sqlStatement := "INSERT INTO anotacoes_radares ( " +
					" id_ciclo, " +
					" id_pilar, " +
					" tipo_media, " +
					" peso_padrao, " +
					" id_author, " +
					" criado_em " +
					" ) " +
					" VALUES (?, ?, ?, ?, ?, ?) RETURNING id"
				log.Println(sqlStatement)
				Db.QueryRow(
					sqlStatement,
					radarId,
					pilarAnotacao.PilarId,
					pilarAnotacao.TipoMediaId,
					pilarAnotacao.PesoPadrao,
					currentUser.Id,
					time.Now()).Scan(&pilarAnotacaoId)
			}
		}
		UpdateAnotacoesAnotacaoHandler(anotacoesAnotacaoPage, anotacoesAnotacaoDB)*/

		http.Redirect(w, r, route.AnotacoesRoute+"?msg=Anotação atualizada com sucesso.", 301)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func DeleteAnotacaoHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Delete Anotacao")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		errMsg := "A Anotação está associada a um registro e não pode ser removida."
		id := r.FormValue("Id")
		sqlStatement := "DELETE FROM virtus.anotacoes WHERE id_anotacao=?"
		log.Println(sqlStatement)
		deleteForm, _ := Db.Prepare(sqlStatement)
		_, err := deleteForm.Exec(id)
		if err != nil && strings.Contains(err.Error(), "violates foreign key") {
			log.Println("ENTROU NO ERRO " + errMsg)
			http.Redirect(w, r, route.AnotacoesRoute+"?errMsg="+errMsg, 301)
		} else {
			http.Redirect(w, r, route.AnotacoesRoute+"?msg=Anotação removida com sucesso.", 301)
		}
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func ListAnotacoesHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("List Anotacoes")
	currentUser := GetUserInCookie(w, r)
	if sec.IsAuthenticated(w, r) && HasPermission(currentUser, "listAnotacoes") {
		msg := r.FormValue("msg")
		errMsg := r.FormValue("errMsg")
		sql := "SELECT " +
			" a.id_anotacao, " +
			" a.id_entidade, " +
			" d.sigla as entidade_sigla, " +
			" a.assunto, " +
			" case when a.risco = 'A' then 'Baixo' when a.risco = 'M' then 'Médio' else 'Baixo' end, " +
			" case when a.tendencia = 'M' then 'Melhora' when a.tendencia = 'E' then 'Estabilidade' else 'Piora' end, " +
			" a.id_relator, " +
			" a.id_responsavel, " +
			" a.descricao, " +
			" a.matriz, " +
			" a.id_author, " +
			" b.name, " +
			" format(a.criado_em, 'dd/MM/yyyy HH:mm:ss'), " +
			" coalesce(a.id_ciclo,0), " +
			" coalesce(a.id_pilar,0), " +
			" coalesce(a.id_componente,0), " +
			" coalesce(a.id_plano,0), " +
			" coalesce(a.id_tipo_nota,0), " +
			" coalesce(a.id_elemento,0), " +
			" coalesce(a.id_item,0), " +
			" coalesce(c.name,'') as cstatus, " +
			" coalesce(a.id_status,0), " +
			" a.id_versao_origem " +
			" FROM virtus.anotacoes a " +
			" LEFT JOIN virtus.users b ON a.id_author = b.id_user " +
			" LEFT JOIN virtus.status c ON a.id_status = c.id_status " +
			" LEFT JOIN virtus.entidades d ON a.id_entidade = d.id_entidade " +
			" order by a.id_anotacao asc"
		log.Println(sql)
		rows, _ := Db.Query(sql)
		defer rows.Close()
		var anotacoes []mdl.Anotacao
		var anotacao mdl.Anotacao
		var i = 1
		for rows.Next() {
			rows.Scan(
				&anotacao.Id,
				&anotacao.EntidadeId,
				&anotacao.EntidadeSigla,
				&anotacao.Assunto,
				&anotacao.Risco,
				&anotacao.Tendencia,
				&anotacao.RelatorId,
				&anotacao.ResponsavelId,
				&anotacao.Descricao,
				&anotacao.Matriz,
				&anotacao.AuthorId,
				&anotacao.AuthorName,
				&anotacao.C_CriadoEm,
				&anotacao.CicloId,
				&anotacao.PilarId,
				&anotacao.ComponenteId,
				&anotacao.PlanoId,
				&anotacao.TipoNotaId,
				&anotacao.ElementoId,
				&anotacao.ItemId,
				&anotacao.CStatus,
				&anotacao.StatusId,
				&anotacao.IdVersaoOrigem)
			anotacao.Order = i
			i++
			log.Println(anotacao)
			anotacoes = append(anotacoes, anotacao)
		}

		sql = "SELECT j.id_entidade, e.codigo, e.nome as nome_entidade " +
			" FROM virtus.users u  " +
			" INNER JOIN virtus.membros m ON m.id_usuario = u.id_user  " +
			" INNER JOIN virtus.jurisdicoes j ON j.id_escritorio = m.id_escritorio " +
			" INNER JOIN virtus.entidades e ON e.id_entidade = j.id_entidade  " +
			" LEFT JOIN virtus.integrantes i ON (i.id_entidade = e.id_entidade and i.id_usuario = m.id_usuario) " +
			" LEFT JOIN virtus.ciclos_entidades ce ON (ce.id_entidade = e.id_entidade) " +
			" WHERE  " +
			" (u.id_role = 2 and u.id_user = ?) " +
			" OR (u.id_role = 3 and ce.id_supervisor = ?) " +
			" OR (u.id_role = 4 and i.id_usuario is not null and u.id_user = ?) " +
			" ORDER by nome_entidade "
		log.Println(sql)
		rows, _ = Db.Query(sql, currentUser.Id, currentUser.Id, currentUser.Id)
		defer rows.Close()
		var entidade mdl.Entidade
		var entidades []mdl.Entidade
		for rows.Next() {
			rows.Scan(
				&entidade.Id,
				&entidade.Sigla,
				&entidade.Nome)
			entidades = append(entidades, entidade)
		}

		sql = " SELECT a.id_usuario, " +
			"        c.name, " +
			"        c.id_role, " +
			"        d.name " +
			" FROM virtus.membros a " +
			" INNER JOIN virtus.escritorios b ON a.id_escritorio = b.id_escritorio " +
			" INNER JOIN virtus.users c ON a.id_usuario = c.id_user " +
			" INNER JOIN virtus.roles d ON c.id_role = d.id_role " +
			" WHERE b.id_escritorio in " +
			"     (SELECT id_escritorio " +
			"      FROM virtus.membros " +
			"      WHERE id_usuario = " + strconv.FormatInt(currentUser.Id, 10) + ") " +
			" UNION  " +
			" SELECT e.id_user, " +
			"        e.name, " +
			"        e.id_role, " +
			"        f.name " +
			" FROM virtus.users e	    " +
			" INNER JOIN virtus.roles f ON e.id_role = f.id_role " +
			" WHERE e.id_role in (1,6) " +
			" ORDER BY 2 ASC "
		log.Println(sql)
		rows, _ = Db.Query(sql)
		defer rows.Close()
		var atribuicoes []mdl.User
		var atribuicao mdl.User
		i = 1
		for rows.Next() {
			rows.Scan(
				&atribuicao.Id,
				&atribuicao.Name,
				&atribuicao.Role,
				&atribuicao.RoleName)
			atribuicao.Order = i
			i++
			atribuicoes = append(atribuicoes, atribuicao)
		}
		var page mdl.PageAnotacoes
		if errMsg != "" {
			page.ErrMsg = errMsg
		}
		if msg != "" {
			page.Msg = msg
		}
		page.Anotacoes = anotacoes
		log.Println(len(entidades))
		page.Entidades = entidades
		page.Relatores = atribuicoes
		page.Responsaveis = atribuicoes
		page.AppName = mdl.AppName
		page.Title = "Anotações" + mdl.Ambiente
		page.LoggedUser = BuildLoggedUser(GetUserInCookie(w, r))
		var tmpl = template.Must(template.ParseGlob("tiles/anotacoes/*"))
		tmpl.ParseGlob("tiles/*")
		tmpl.ExecuteTemplate(w, "Main-Anotacoes", page)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func LoadAnotacoesByAnotacaoId(w http.ResponseWriter, r *http.Request) {
	log.Println("Load Anotacoes Anotacoes By Anotacao Id")
	r.ParseForm()
	var radarId = r.FormValue("radarId")
	log.Println("radarId: " + radarId)
	/*anotacoesAnotacao := ListAnotacoesByAnotacaoId(radarId)
	jsonAnotacoesAnotacao, _ := json.Marshal(anotacoesAnotacao)
	w.Write([]byte(jsonAnotacoesAnotacao))*/
	log.Println("JSON Anotacoes de Anotacoes")
}
