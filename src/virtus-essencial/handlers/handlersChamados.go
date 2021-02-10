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

func CreateChamadoHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Create Chamado")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		currentUser := GetUserInCookie(w, r)
		tipoChamado := r.FormValue("TipoChamado")
		log.Println("TipoChamado: " + tipoChamado)
		titulo := r.FormValue("Titulo")
		log.Println("Titulo: " + titulo)
		descricao := r.FormValue("Descricao")
		log.Println("Descricao: " + descricao)
		acompanhamento := r.FormValue("Acompanhamento")
		log.Println("Acompanhamento: " + acompanhamento)
		prioridade := r.FormValue("Prioridade")
		log.Println("Prioridade: " + prioridade)
		relator := r.FormValue("Relator")
		log.Println("Relator: " + relator)
		if relator == "" {
			relator = strconv.FormatInt(currentUser.Id, 10)
		}
		responsavel := r.FormValue("Responsavel")
		log.Println("Responsavel: " + responsavel)
		if responsavel == "" {
			responsavel = strconv.FormatInt(currentUser.Id, 10)
		}
		iniciaEm := r.FormValue("IniciaEm")
		log.Println("IniciaEm: " + iniciaEm)
		prontoEm := r.FormValue("ProntoEm")
		log.Println("ProntoEm: " + prontoEm)
		estimativa := r.FormValue("Estimativa")
		log.Println("Estimativa: " + estimativa)
		if estimativa == "" {
			estimativa = "0"
		}
		statusChamadoId := GetStartStatus("chamado")
		sqlStatement := "INSERT INTO chamados(" +
			" tipo_chamado_id, " +
			" titulo, " +
			" descricao, " +
			" acompanhamento, " +
			" prioridade_id, " +
			" relator_id, " +
			" responsavel_id, "
		if iniciaEm != "" {
			sqlStatement += " inicia_em, "
		}
		if prontoEm != "" {
			sqlStatement += " pronto_em, "
		}
		sqlStatement += " estimativa, " +
			" author_id, " +
			" criado_em, " +
			" status_id) " +
			" OUTPUT INSERTED.id " +
			" VALUES ('" +
			tipoChamado + "', '" +
			titulo + "', '" +
			descricao + "', '" +
			acompanhamento + "', '" +
			prioridade + "', " +
			relator + ", " +
			responsavel + ", "
		if iniciaEm != "" {
			sqlStatement += "'" + iniciaEm + "', "
		}
		if prontoEm != "" {
			sqlStatement += "'" + prontoEm + "', "
		}
		sqlStatement += estimativa + ", ?, GETDATE(), ? ) "
		idChamado := 0
		row := Db.QueryRow(sqlStatement,
			currentUser.Id,
			statusChamadoId)
		err := row.Scan(&idChamado)
		if err != nil {
			log.Println(err.Error())
		}
		log.Println(sqlStatement + " - ")
		log.Println("INSERT: Id: " + strconv.Itoa(idChamado) + " - Titulo: " + titulo)
		http.Redirect(w, r, route.ChamadosRoute+"?msg=Chamado criado com sucesso.", 301)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func UpdateChamadoHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Update Chamado")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		//currentUser := GetUserInCookie(w, r)
		chamadoId := r.FormValue("Id")
		log.Println(chamadoId)
		tipo := r.FormValue("TipoChamado")
		log.Println(tipo)
		prioridade := r.FormValue("Prioridade")
		log.Println(prioridade)
		titulo := r.FormValue("Titulo")
		log.Println(titulo)
		descricao := r.FormValue("Descricao")
		log.Println(descricao)
		acompanhamento := r.FormValue("Acompanhamento")
		log.Println(acompanhamento)
		responsavel := r.FormValue("Responsavel")
		log.Println(responsavel)
		relator := r.FormValue("Relator")
		log.Println(relator)
		iniciaEm := r.FormValue("IniciaEm")
		log.Println(iniciaEm)
		prontoEm := r.FormValue("ProntoEm")
		log.Println(prontoEm)
		estimativa := r.FormValue("Estimativa")
		log.Println(estimativa)
		sqlStatement := "UPDATE chamados SET " +
			" tipo_chamado_id = '" + tipo + "', " +
			" prioridade_id = '" + prioridade + "', " +
			" titulo = '" + titulo + "', " +
			" descricao = '" + descricao + "', " +
			" acompanhamento = '" + acompanhamento + "', " +
			" responsavel_id = " + responsavel + ", " +
			" relator_id = " + relator + ", " +
			" estimativa = " + estimativa + " "
		if iniciaEm != "" {
			sqlStatement += ", inicia_em = '" + iniciaEm + "'"
		}
		if prontoEm != "" {
			sqlStatement += ", pronto_em = '" + prontoEm + "' "
		}
		sqlStatement += " WHERE id = " + chamadoId
		log.Println(sqlStatement)
		updtForm, _ := Db.Prepare(sqlStatement)
		updtForm.Exec()
		log.Println("UPDATE: Id: " + chamadoId + " | Titulo: " + titulo + " | Descrição: " + descricao)

		// Questoes Chamados
		/*var questoesChamadoDB = ListQuestoesByChamadoId(chamadoId)
		var questoesChamadoPage []mdl.PilarChamado
		var pilarChamadoPage mdl.PilarChamado
		for key, value := range r.Form {
			if strings.HasPrefix(key, "pilarChamado") {
				log.Println(value[0])
				array := strings.Split(value[0], "#")
				id := strings.Split(array[1], ":")[1]
				log.Println("Id -------- " + id)
				pilarChamadoPage.Id, _ = strconv.ParseInt(id, 10, 64)
				pilarChamadoPage.ChamadoId, _ = strconv.ParseInt(chamadoId, 10, 64)
				pilarId := strings.Split(array[3], ":")[1]
				log.Println("pilarId -------- " + pilarId)
				pilarChamadoPage.PilarId, _ = strconv.ParseInt(pilarId, 10, 64)
				pilarNome := strings.Split(array[4], ":")[1]
				log.Println("pilarNome -------- " + pilarNome)
				pilarChamadoPage.PilarNome = pilarNome
				tipoMediaId := strings.Split(array[5], ":")[1]
				log.Println("tipoMediaId -------- " + tipoMediaId)
				pilarChamadoPage.TipoMediaId, _ = strconv.Atoi(tipoMediaId)
				tipoMedia := strings.Split(array[6], ":")[1]
				log.Println("tipoMedia -------- " + tipoMedia)
				pilarChamadoPage.TipoMedia = tipoMedia
				pesoPadrao := strings.Split(array[7], ":")[1]
				log.Println("pesoPadrao -------- " + pesoPadrao)
				pilarChamadoPage.PesoPadrao = pesoPadrao
				authorId := strings.Split(array[8], ":")[1]
				log.Println("authorId -------- " + authorId)
				pilarChamadoPage.AuthorId, _ = strconv.ParseInt(authorId, 10, 64)
				authorName := strings.Split(array[9], ":")[1]
				log.Println("authorName -------- " + authorName)
				pilarChamadoPage.AuthorName = authorName
				criadoEm := strings.Split(array[10], ":")[1]
				log.Println("criadoEm -------- " + criadoEm)
				pilarChamadoPage.CriadoEm = criadoEm
				idVersaoOrigem := strings.Split(array[11], ":")[1]
				log.Println("idVersaoOrigem -------- " + idVersaoOrigem)
				pilarChamadoPage.IdVersaoOrigem, _ = strconv.ParseInt(idVersaoOrigem, 10, 64)
				statusId := strings.Split(array[12], ":")[1]
				log.Println("StatusId -------- " + statusId)
				pilarChamadoPage.StatusId, _ = strconv.ParseInt(statusId, 10, 64)
				cStatus := strings.Split(array[13], ":")[1]
				log.Println("cStatus -------- " + cStatus)
				pilarChamadoPage.CStatus = cStatus
				questoesChamadoPage = append(questoesChamadoPage, pilarChamadoPage)
			}
		}
		if len(questoesChamadoPage) < len(questoesChamadoDB) {
			log.Println("Quantidade de Questoes do Chamado da Página: " + strconv.Itoa(len(questoesChamadoPage)))
			if len(questoesChamadoPage) == 0 {
				DeleteQuestoesChamadoByChamadoId(chamadoId) //DONE
			} else {
				var diffDB []mdl.PilarChamado = questoesChamadoDB
				for n := range questoesChamadoPage {
					if containsPilarChamado(diffDB, questoesChamadoPage[n]) {
						diffDB = removePilarChamado(diffDB, questoesChamadoPage[n])
					}
				}
				DeleteQuestoesChamadoHandler(diffDB) //DONE
			}
		} else {
			var diffPage []mdl.PilarChamado = questoesChamadoPage
			for n := range questoesChamadoDB {
				if containsPilarChamado(diffPage, questoesChamadoDB[n]) {
					diffPage = removePilarChamado(diffPage, questoesChamadoDB[n])
				}
			}
			var pilarChamado mdl.PilarChamado
			pilarChamadoId := 0
			// statusItemId := GetStartStatus("plano")
			for i := range diffPage {
				pilarChamado = diffPage[i]
				log.Println("Chamado Id: " + chamadoId)
				sqlStatement := "INSERT INTO questoes_chamados ( " +
					" ciclo_id, " +
					" pilar_id, " +
					" tipo_media, " +
					" peso_padrao, " +
					" author_id, " +
					" criado_em " +
					" ) " +
					" VALUES (?, ?, ?, ?, ?, ?) RETURNING id"
				log.Println(sqlStatement)
				Db.QueryRow(
					sqlStatement,
					chamadoId,
					pilarChamado.PilarId,
					pilarChamado.TipoMediaId,
					pilarChamado.PesoPadrao,
					currentUser.Id,
					time.Now()).Scan(&pilarChamadoId)
			}
		}
		UpdateQuestoesChamadoHandler(questoesChamadoPage, questoesChamadoDB)*/

		http.Redirect(w, r, route.ChamadosRoute+"?msg=Chamado atualizado com sucesso.", 301)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func DeleteChamadoHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Delete Chamado")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		errMsg := "O Chamado está associado a um registro e não pôde ser removido."
		id := r.FormValue("Id")
		sqlStatement := "DELETE FROM chamados WHERE id=?"
		log.Println(sqlStatement)
		deleteForm, _ := Db.Prepare(sqlStatement)
		_, err := deleteForm.Exec(id)
		if err != nil && strings.Contains(err.Error(), "violates foreign key") {
			log.Println("ENTROU NO ERRO " + errMsg)
			http.Redirect(w, r, route.ChamadosRoute+"?errMsg="+errMsg, 301)
		} else {
			http.Redirect(w, r, route.ChamadosRoute+"?msg=Chamado removido com sucesso.", 301)
		}
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func ListChamadosHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("List Chamados")
	currentUser := GetUserInCookie(w, r)
	if sec.IsAuthenticated(w, r) && HasPermission(currentUser, "listChamados") {
		statusEndChamado := strconv.Itoa(GetEndStatus("chamado"))
		msg := r.FormValue("msg")
		errMsg := r.FormValue("errMsg")
		sql := "SELECT " +
			" a.id, " +
			" coalesce(a.titulo,''), " +
			" coalesce(a.descricao,''), " +
			" coalesce(a.acompanhamento,''), " +
			" coalesce(a.responsavel_id,0), " +
			" coalesce(d.name,'') as responsavel_name, " +
			" coalesce(a.relator_id, 0), " +
			" coalesce(e.name,'') as relator_name, " +
			" coalesce(format(a.inicia_em,'dd/MM/yyyy'),''), " +
			" coalesce(format(a.pronto_em,'dd/MM/yyyy'),''), " +
			" case " +
			"   when a.tipo_chamado_id = 'A' then 'Adequação ' " +
			"   when a.tipo_chamado_id = 'C' then 'Correcão ' " +
			"   when a.tipo_chamado_id = 'D' then 'Dúvida ' " +
			"   when a.tipo_chamado_id = 'M' then 'Melhoria ' " +
			"   when a.tipo_chamado_id = 'S' then 'Sugestão ' " +
			"   else 'Tarefa' " +
			" end, " +
			" case " +
			"   when a.prioridade_id = 'E' then 'Essencial' " +
			"   when a.prioridade_id = 'A' then 'Alta' " +
			"   when a.prioridade_id = 'M' then 'Média' " +
			"   when a.prioridade_id = 'B' then 'Baixa' " +
			"   else 'Desejável' " +
			" end, " +
			" coalesce(a.estimativa,0), " +
			" coalesce(a.author_id,0), " +
			" coalesce(b.name,''), " +
			" coalesce(format(a.criado_em, 'dd/MM/yyyy HH:mm:ss'),''), " +
			" coalesce(c.name,'') as cstatus, " +
			" coalesce(a.status_id,0), " +
			" coalesce(a.id_versao_origem,0) " +
			" FROM chamados a " +
			" LEFT JOIN users b ON a.author_id = b.id " +
			" LEFT JOIN status c ON a.status_id = c.id " +
			" LEFT JOIN users d ON a.responsavel_id = d.id " +
			" LEFT JOIN users e ON a.relator_id = e.id "
		if statusEndChamado != "0" {
			sql += " WHERE a.status_id <> " + statusEndChamado
		}
		sql += " order by a.id asc"
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
				&chamado.Acompanhamento,
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
			chamados = append(chamados, chamado)
		}
		sql = " SELECT a.usuario_id, " +
			"        c.name, " +
			"        c.role_id, " +
			"        d.name " +
			" FROM membros a " +
			" INNER JOIN escritorios b ON a.escritorio_id = b.id " +
			" INNER JOIN users c ON a.usuario_id = c.id " +
			" INNER JOIN ROLES d ON c.role_id = d.id " +
			" WHERE b.id in " +
			"     (SELECT escritorio_id " +
			"      FROM membros " +
			"      WHERE usuario_id = " + strconv.FormatInt(currentUser.Id, 10) + ") " +
			" UNION  " +
			" SELECT e.id, " +
			"        e.name, " +
			"        e.role_id, " +
			"        f.name " +
			" FROM users e	    " +
			" INNER JOIN ROLES f ON e.role_id = f.id " +
			" WHERE e.role_id in (1,2,3,4,5,6) " +
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
		var page mdl.PageChamados
		page.Relatores = atribuicoes
		page.Responsaveis = atribuicoes
		if errMsg != "" {
			page.ErrMsg = errMsg
		}
		if msg != "" {
			page.Msg = msg
		}
		page.Chamados = chamados
		page.AppName = mdl.AppName
		page.Title = "Chamados" + mdl.Ambiente
		page.LoggedUser = BuildLoggedUser(GetUserInCookie(w, r))
		var tmpl = template.Must(template.ParseGlob("tiles/chamados/*"))
		tmpl.ParseGlob("tiles/*")
		tmpl.ExecuteTemplate(w, "Main-Chamados", page)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func LoadComentariosByChamadoId(w http.ResponseWriter, r *http.Request) {
	log.Println("Load Comentarios By Chamado Id")
	r.ParseForm()
	var chamadoId = r.FormValue("chamadoId")
	log.Println("chamadoId: " + chamadoId)
	/*questoesChamado := ListQuestoesByChamadoId(chamadoId)
	jsonQuestoesChamado, _ := json.Marshal(questoesChamado)
	w.Write([]byte(jsonQuestoesChamado))*/
	log.Println("JSON Questoes de Chamados")
}
