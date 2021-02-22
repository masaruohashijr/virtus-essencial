package handlers

import (
	//"encoding/json"
	"html/template"
	"log"
	"net/http"
	"strconv"
	"strings"
	"time"
	mdl "virtus-essencial/models"
	route "virtus-essencial/routes"
	sec "virtus-essencial/security"
)

func CreateProcessoHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Create Processo")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		currentUser := GetUserInCookie(w, r)
		nome := r.FormValue("Nome")
		descricao := r.FormValue("Descricao")
		referencia := r.FormValue("Referencia")
		dataProcesso := r.FormValue("DataProcesso")
		sqlStatement := "INSERT INTO radares(" +
			" nome, descricao, referencia, data_radar, id_author, criado_em) " +
			" VALUES (?, ?, ?, ?, ?, ?) RETURNING id"
		idProcesso := 0
		Db.QueryRow(sqlStatement, nome, descricao, referencia, dataProcesso, currentUser.Id, time.Now()).Scan(&idProcesso)
		log.Println(sqlStatement + " - " + nome)
		log.Println("INSERT: Id: " + strconv.Itoa(idProcesso) + " - Nome: " + nome)
		/*
			for key, value := range r.Form {
				if strings.HasPrefix(key, "questaoProcesso") {
					array := strings.Split(value[0], "#")
					log.Println(value[0])
					questaoProcessoId := 0
					questaoId := strings.Split(array[3], ":")[1]
					sqlStatement := " INSERT INTO " +
						" questoes_radares( " +
						" radar_id, " +
						" id_questao, " +
						" registro_ata, " +
						" id_author, " +
						" criado_em ) " +
						" VALUES (?, ?, ?, ?, ?) RETURNING id"
					log.Println(sqlStatement)
					err := Db.QueryRow(
						sqlStatement,
						idProcesso,
						questaoId,
						tipoMediaId,
						pesoPadrao,
						currentUser.Id,
						time.Now()).Scan(&questaoProcessoId)
					if err != nil {
						log.Println(err.Error())
					}
				}
			}*/
		http.Redirect(w, r, route.ProcessosRoute+"?msg=Processo criado com sucesso.", 301)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func UpdateProcessoHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Update Processo")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		//currentUser := GetUserInCookie(w, r)
		radarId := r.FormValue("Id")
		nome := r.FormValue("Nome")
		descricao := r.FormValue("Descricao")
		referencia := r.FormValue("Referencia")
		sqlStatement := "UPDATE radares SET nome = ?, " +
			" descricao = ?, " +
			" referencia = ? " +
			" WHERE id = ? "
		updtForm, _ := Db.Prepare(sqlStatement)
		updtForm.Exec(nome, descricao, referencia, radarId)
		log.Println("UPDATE: Id: " + radarId + " | Nome: " + nome + " | Descrição: " + descricao)

		// Questoes Processos
		/*var questoesProcessoDB = ListQuestoesByProcessoId(radarId)
		var questoesProcessoPage []mdl.PilarProcesso
		var pilarProcessoPage mdl.PilarProcesso
		for key, value := range r.Form {
			if strings.HasPrefix(key, "pilarProcesso") {
				log.Println(value[0])
				array := strings.Split(value[0], "#")
				id := strings.Split(array[1], ":")[1]
				log.Println("Id -------- " + id)
				pilarProcessoPage.Id, _ = strconv.ParseInt(id, 10, 64)
				pilarProcessoPage.ProcessoId, _ = strconv.ParseInt(radarId, 10, 64)
				pilarId := strings.Split(array[3], ":")[1]
				log.Println("pilarId -------- " + pilarId)
				pilarProcessoPage.PilarId, _ = strconv.ParseInt(pilarId, 10, 64)
				pilarNome := strings.Split(array[4], ":")[1]
				log.Println("pilarNome -------- " + pilarNome)
				pilarProcessoPage.PilarNome = pilarNome
				tipoMediaId := strings.Split(array[5], ":")[1]
				log.Println("tipoMediaId -------- " + tipoMediaId)
				pilarProcessoPage.TipoMediaId, _ = strconv.Atoi(tipoMediaId)
				tipoMedia := strings.Split(array[6], ":")[1]
				log.Println("tipoMedia -------- " + tipoMedia)
				pilarProcessoPage.TipoMedia = tipoMedia
				pesoPadrao := strings.Split(array[7], ":")[1]
				log.Println("pesoPadrao -------- " + pesoPadrao)
				pilarProcessoPage.PesoPadrao = pesoPadrao
				authorId := strings.Split(array[8], ":")[1]
				log.Println("authorId -------- " + authorId)
				pilarProcessoPage.AuthorId, _ = strconv.ParseInt(authorId, 10, 64)
				authorName := strings.Split(array[9], ":")[1]
				log.Println("authorName -------- " + authorName)
				pilarProcessoPage.AuthorName = authorName
				criadoEm := strings.Split(array[10], ":")[1]
				log.Println("criadoEm -------- " + criadoEm)
				pilarProcessoPage.CriadoEm = criadoEm
				idVersaoOrigem := strings.Split(array[11], ":")[1]
				log.Println("idVersaoOrigem -------- " + idVersaoOrigem)
				pilarProcessoPage.IdVersaoOrigem, _ = strconv.ParseInt(idVersaoOrigem, 10, 64)
				statusId := strings.Split(array[12], ":")[1]
				log.Println("StatusId -------- " + statusId)
				pilarProcessoPage.StatusId, _ = strconv.ParseInt(statusId, 10, 64)
				cStatus := strings.Split(array[13], ":")[1]
				log.Println("cStatus -------- " + cStatus)
				pilarProcessoPage.CStatus = cStatus
				questoesProcessoPage = append(questoesProcessoPage, pilarProcessoPage)
			}
		}
		if len(questoesProcessoPage) < len(questoesProcessoDB) {
			log.Println("Quantidade de Questoes do Processo da Página: " + strconv.Itoa(len(questoesProcessoPage)))
			if len(questoesProcessoPage) == 0 {
				DeleteQuestoesProcessoByProcessoId(radarId) //DONE
			} else {
				var diffDB []mdl.PilarProcesso = questoesProcessoDB
				for n := range questoesProcessoPage {
					if containsPilarProcesso(diffDB, questoesProcessoPage[n]) {
						diffDB = removePilarProcesso(diffDB, questoesProcessoPage[n])
					}
				}
				DeleteQuestoesProcessoHandler(diffDB) //DONE
			}
		} else {
			var diffPage []mdl.PilarProcesso = questoesProcessoPage
			for n := range questoesProcessoDB {
				if containsPilarProcesso(diffPage, questoesProcessoDB[n]) {
					diffPage = removePilarProcesso(diffPage, questoesProcessoDB[n])
				}
			}
			var pilarProcesso mdl.PilarProcesso
			pilarProcessoId := 0
			// statusItemId := GetStartStatus("plano")
			for i := range diffPage {
				pilarProcesso = diffPage[i]
				log.Println("Processo Id: " + radarId)
				sqlStatement := "INSERT INTO questoes_radares ( " +
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
					pilarProcesso.PilarId,
					pilarProcesso.TipoMediaId,
					pilarProcesso.PesoPadrao,
					currentUser.Id,
					time.Now()).Scan(&pilarProcessoId)
			}
		}
		UpdateQuestoesProcessoHandler(questoesProcessoPage, questoesProcessoDB)*/

		http.Redirect(w, r, route.ProcessosRoute+"?msg=Processo atualizado com sucesso.", 301)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func DeleteProcessoHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Delete Processo")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		errMsg := "O Processo está associado a um registro e não pôde ser removido."
		id := r.FormValue("Id")
		sqlStatement := "DELETE FROM radares WHERE id_radar=?"
		log.Println(sqlStatement)
		deleteForm, _ := Db.Prepare(sqlStatement)
		_, err := deleteForm.Exec(id)
		log.Println(err.Error())
		if err != nil && strings.Contains(err.Error(), "violates foreign key") {
			log.Println("ENTROU NO ERRO " + errMsg)
			http.Redirect(w, r, route.ProcessosRoute+"?errMsg="+errMsg, 301)
		} else {
			http.Redirect(w, r, route.ProcessosRoute+"?msg=Processo removido com sucesso.", 301)
		}
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func ListProcessosHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("List Processos")
	currentUser := GetUserInCookie(w, r)
	if sec.IsAuthenticated(w, r) && HasPermission(currentUser, "listProcessos") {
		msg := r.FormValue("msg")
		errMsg := r.FormValue("errMsg")
		sql := "SELECT " +
			" a.id, " +
			" a.nome, " +
			" a.descricao, " +
			" a.referencia, " +
			" format(a.data_radar,'dd/MM/yyyy'), " +
			" a.id_author, " +
			" b.name, " +
			" format(a.criado_em, 'dd/MM/yyyy HH:mm:ss'), " +
			" coalesce(c.name,'') as cstatus, " +
			" a.id_status, " +
			" a.id_versao_origem " +
			" FROM radares a LEFT JOIN users b " +
			" ON a.id_author = b.id " +
			" LEFT JOIN status c ON a.id_status = c.id " +
			" order by a.id asc"
		log.Println(sql)
		rows, _ := Db.Query(sql)
		defer rows.Close()
		var radares []mdl.Processo
		var radar mdl.Processo
		var i = 1
		for rows.Next() {
			rows.Scan(
				&radar.Id,
				&radar.Numero,
				&radar.Descricao,
				&radar.Referencia,
				&radar.AuthorId,
				&radar.AuthorName,
				&radar.C_CriadoEm,
				&radar.CStatus,
				&radar.StatusId,
				&radar.IdVersaoOrigem)
			radar.Order = i
			i++
			radares = append(radares, radar)
		}
		/*sql = "SELECT " +
			" a.id, " +
			" a.nome " +
			" FROM questoes a " +
			" order by a.id asc"
		log.Println(sql)
		rows, _ = Db.Query(sql)
		defer rows.Close()
		var questoes []mdl.Pilar
		var pilar mdl.Pilar
		i = 1
		for rows.Next() {
			rows.Scan(
				&pilar.Id,
				&pilar.Nome)
			pilar.Order = i
			i++
			questoes = append(questoes, pilar)
		}
		sql = "SELECT a.id, a.sigla, a.codigo, a.nome " +
			"FROM entidades a " +
			"WHERE NOT EXISTS " +
			"(SELECT 1 FROM radares_entidades b " +
			" WHERE b.id_entidade = a.id) " +
			"ORDER BY a.sigla"
		log.Println(sql)
		rows, _ = Db.Query(sql)
		defer rows.Close()
		var entidades []mdl.Entidade
		var entidade mdl.Entidade
		i = 1
		for rows.Next() {
			rows.Scan(
				&entidade.Id,
				&entidade.Sigla,
				&entidade.Codigo,
				&entidade.Nome)
			entidade.Order = i
			i++
			entidades = append(entidades, entidade)
		}*/
		var page mdl.PageProcessos
		if errMsg != "" {
			page.ErrMsg = errMsg
		}
		if msg != "" {
			page.Msg = msg
		}
		page.Processos = radares
		page.AppName = mdl.AppName
		page.Title = "Processos" + mdl.Ambiente
		page.LoggedUser = BuildLoggedUser(GetUserInCookie(w, r))
		var tmpl = template.Must(template.ParseGlob("tiles/radares/*"))
		tmpl.ParseGlob("tiles/*")
		tmpl.ExecuteTemplate(w, "Main-Processos", page)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func LoadQuestoesByProcessoId(w http.ResponseWriter, r *http.Request) {
	log.Println("Load Questoes Processos By Processo Id")
	r.ParseForm()
	var radarId = r.FormValue("radarId")
	log.Println("radarId: " + radarId)
	/*questoesProcesso := ListQuestoesByProcessoId(radarId)
	jsonQuestoesProcesso, _ := json.Marshal(questoesProcesso)
	w.Write([]byte(jsonQuestoesProcesso))*/
	log.Println("JSON Questoes de Processos")
}
