package handlers

import (
	"encoding/json"
	"html/template"
	"log"
	"net/http"
	"strconv"
	"strings"
	mdl "virtus-essencial/models"
	route "virtus-essencial/routes"
	sec "virtus-essencial/security"
)

func CreateRadarHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Create Radar")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		currentUser := GetUserInCookie(w, r)
		nome := r.FormValue("Nome")
		descricao := r.FormValue("Descricao")
		referencia := r.FormValue("Referencia")
		dataRadar := r.FormValue("DataRadar")
		sqlStatement := "INSERT INTO virtus.radares(nome, descricao, referencia, "
		if dataRadar != "" {
			sqlStatement += "data_radar, "
		}
		sqlStatement += " id_author, criado_em) " +
			" OUTPUT INSERTED.id_radar VALUES ('" + nome + "', '" + descricao + "', '" + referencia + "',"
		if dataRadar != "" {
			sqlStatement += "'" + dataRadar + "',"
		}
		sqlStatement += " ?, GETDATE())"
		idRadar := 0
		row := Db.QueryRow(sqlStatement, currentUser.Id)
		err := row.Scan(&idRadar)
		if err != nil {
			log.Println(err.Error())
			http.Redirect(w, r, route.RadaresRoute+"?errMsg=Erro na criação do Radar.", 301)
		}
		log.Println(sqlStatement + " - " + nome)
		log.Println("INSERT: Id: " + strconv.Itoa(idRadar) + " - Nome: " + nome)

		for key, value := range r.Form {
			if strings.HasPrefix(key, "anotacaoRadar") {
				array := strings.Split(value[0], "#")
				log.Println(value[0])
				anotacaoRadarId := 0
				entidadeId := strings.Split(array[3], ":")[1]
				anotacaoId := strings.Split(array[4], ":")[1]
				observacoes := strings.Split(array[5], ":")[1]
				registroAta := strings.Split(array[6], ":")[1]
				sqlStatement := " INSERT INTO " +
					" virtus.anotacoes_radares( " +
					" id_radar, " +
					" id_entidade, " +
					" id_anotacao, " +
					" observacoes, " +
					" registro_ata, " +
					" id_author, " +
					" criado_em ) " +
					" OUTPUT INSERTED.id_anotacao_radar VALUES (?, ?, ?, ?, ?, ?, GETDATE())"
				log.Println(sqlStatement)
				err := Db.QueryRow(
					sqlStatement,
					idRadar,
					entidadeId,
					anotacaoId,
					observacoes,
					registroAta,
					currentUser.Id).Scan(&anotacaoRadarId)
				if err != nil {
					log.Println(err.Error())
				}
			}
		}
		http.Redirect(w, r, route.RadaresRoute+"?msg=Radar criado com sucesso.", 301)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func UpdateRadarHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Update Radar")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		currentUser := GetUserInCookie(w, r)
		radarId := r.FormValue("Id")
		log.Println(radarId)
		nome := r.FormValue("Nome")
		log.Println(nome)
		dataRadar := r.FormValue("DataRadar")
		log.Println(dataRadar)
		descricao := r.FormValue("Descricao")
		log.Println(descricao)
		referencia := r.FormValue("Referencia")
		log.Println(referencia)
		sqlStatement := " UPDATE virtus.radares SET nome = '" + nome + "', "
		if dataRadar != "" {
			sqlStatement = sqlStatement + " data_radar = '" + dataRadar + "', "
		}
		sqlStatement += " descricao = '" + descricao + "', " +
			" referencia = '" + referencia + "' " +
			" WHERE id_radar = " + radarId
		log.Println(sqlStatement)
		updtForm, _ := Db.Prepare(sqlStatement)
		_, err := updtForm.Exec()
		if err != nil {
			log.Println(err.Error())
		}
		log.Println("UPDATE: Id: " + radarId + " | Nome: " + nome + " | Descrição: " + descricao)

		// Anotações Radares
		var anotacoesRadarDB = ListAnotacoesRadarByRadarId(radarId)
		var anotacoesRadarPage []mdl.AnotacaoRadar
		var anotacaoRadarPage mdl.AnotacaoRadar
		for key, value := range r.Form {
			if strings.HasPrefix(key, "anotacaoRadar") {
				log.Println(value[0])
				array := strings.Split(value[0], "#")
				id := strings.Split(array[1], ":")[1]
				log.Println("Id -------- " + id)
				anotacaoRadarPage.Id, _ = strconv.ParseInt(id, 10, 64)
				anotacaoRadarPage.RadarId, _ = strconv.ParseInt(radarId, 10, 64)
				entidadeId := strings.Split(array[3], ":")[1]
				log.Println("entidadeId -------- " + entidadeId)
				anotacaoRadarPage.EntidadeId, _ = strconv.ParseInt(entidadeId, 10, 64)
				anotacaoId := strings.Split(array[4], ":")[1]
				log.Println("anotacaoId -------- " + anotacaoId)
				anotacaoRadarPage.AnotacaoId, _ = strconv.ParseInt(anotacaoId, 10, 64)
				observacoes := strings.Split(array[5], ":")[1]
				log.Println("observacoes -------- " + observacoes)
				anotacaoRadarPage.Observacoes = observacoes
				registroAta := strings.Split(array[6], ":")[1]
				log.Println("registroAta -------- " + registroAta)
				anotacaoRadarPage.RegistroAta = registroAta
				anotacoesRadarPage = append(anotacoesRadarPage, anotacaoRadarPage)
			}
		}
		if len(anotacoesRadarPage) < len(anotacoesRadarDB) {
			log.Println("Quantidade de Anotacoes do Radar da Página: " + strconv.Itoa(len(anotacoesRadarPage)))
			if len(anotacoesRadarPage) == 0 {
				DeleteAnotacoesRadarByRadarId(radarId) //DONE
			} else {
				var diffDB []mdl.AnotacaoRadar = anotacoesRadarDB
				for n := range anotacoesRadarPage {
					if containsAnotacaoRadar(diffDB, anotacoesRadarPage[n]) {
						diffDB = removeAnotacaoRadar(diffDB, anotacoesRadarPage[n])
					}
				}
				DeleteAnotacoesRadarHandler(diffDB) //DONE
			}
		} else {
			var diffPage []mdl.AnotacaoRadar = anotacoesRadarPage
			for n := range anotacoesRadarDB {
				if containsAnotacaoRadar(diffPage, anotacoesRadarDB[n]) {
					diffPage = removeAnotacaoRadar(diffPage, anotacoesRadarDB[n])
				}
			}
			var anotacaoRadar mdl.AnotacaoRadar
			anotacaoRadarId := 0
			// statusItemId := GetStartStatus("plano")
			for i := range diffPage {
				anotacaoRadar = diffPage[i]
				log.Println("Radar Id: " + radarId)
				sqlStatement := "INSERT INTO virtus.anotacoes_radares ( " +
					" id_anotacao, " +
					" id_radar, " +
					" observacoes, " +
					" registro_ata, " +
					" id_author, " +
					" criado_em " +
					" ) OUTPUT INSERTED.id_anotacao_radar " +
					" VALUES (?, ?, ?, ?, ?, GETDATE())"
				log.Println(sqlStatement)
				Db.QueryRow(
					sqlStatement,
					radarId,
					anotacaoRadar.AnotacaoId,
					anotacaoRadar.RadarId,
					anotacaoRadar.Observacoes,
					anotacaoRadar.RegistroAta,
					currentUser.Id).Scan(&anotacaoRadarId)
			}
		}
		UpdateAnotacoesRadarHandler(anotacoesRadarPage, anotacoesRadarDB, currentUser.Id)

		http.Redirect(w, r, route.RadaresRoute+"?msg=Radar atualizado com sucesso.", 301)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func DeleteRadarHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Delete Radar")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		errMsg := "O Radar está associado a um registro e não pôde ser removido."
		id := r.FormValue("Id")
		sqlStatement := "DELETE FROM virtus.radares WHERE id_radar=?"
		log.Println(sqlStatement)
		deleteForm, _ := Db.Prepare(sqlStatement)
		_, err := deleteForm.Exec(id)
		if err != nil && strings.Contains(err.Error(), "violates foreign key") {
			log.Println("ENTROU NO ERRO " + errMsg)
			http.Redirect(w, r, route.RadaresRoute+"?errMsg="+errMsg, 301)
		} else {
			http.Redirect(w, r, route.RadaresRoute+"?msg=Radar removido com sucesso.", 301)
		}
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func ListRadaresHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("List Radares")
	currentUser := GetUserInCookie(w, r)
	if sec.IsAuthenticated(w, r) && HasPermission(currentUser, "listRadares") {
		msg := r.FormValue("msg")
		errMsg := r.FormValue("errMsg")
		sql := "SELECT " +
			" a.id_radar, " +
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
			" FROM virtus.radares a " +
			" LEFT JOIN virtus.users b ON a.id_author = b.id_user " +
			" LEFT JOIN virtus.status c ON a.id_status = c.id_status " +
			" order by a.id_radar asc"
		log.Println(sql)
		rows, _ := Db.Query(sql)
		defer rows.Close()
		var radares []mdl.Radar
		var radar mdl.Radar
		var i = 1
		for rows.Next() {
			rows.Scan(
				&radar.Id,
				&radar.Nome,
				&radar.Descricao,
				&radar.Referencia,
				&radar.DataRadar,
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
		sql = "SELECT " +
			" a.id_anotacao, " +
			" a.assunto, " +
			" a.id_entidade, " +
			" d.sigla as entidade_sigla, " +
			" case when a.risco = 'A' then 'Baixo' when a.risco = 'M' then 'Médio' else 'Baixo' end, " +
			" case when a.tendencia = 'M' then 'Melhora' when a.tendencia = 'E' then 'Estabilidade' else 'Piora' end, " +
			" a.id_author, " +
			" b.name, " +
			" format(a.criado_em, 'dd/MM/yyyy HH:mm:ss'), " +
			" coalesce(c.name,'') as cstatus, " +
			" a.id_status, " +
			" a.id_versao_origem " +
			" FROM virtus.anotacoes a " +
			" LEFT JOIN virtus.users b ON a.id_author = b.id_user " +
			" LEFT JOIN virtus.status c ON a.id_status = c.id_status " +
			" INNER JOIN virtus.entidades d ON a.id_entidade = d.id_entidade " +
			" ORDER BY a.id_anotacao asc"
		log.Println(sql)
		rows, _ = Db.Query(sql)
		defer rows.Close()
		var anotacoes []mdl.Anotacao
		var anotacao mdl.Anotacao
		for rows.Next() {
			rows.Scan(&anotacao.Id,
				&anotacao.Assunto,
				&anotacao.EntidadeId,
				&anotacao.EntidadeSigla,
				&anotacao.Risco,
				&anotacao.Tendencia,
				&anotacao.AuthorId,
				&anotacao.AuthorName,
				&anotacao.CriadoEm,
				&anotacao.CStatus,
				&anotacao.StatusId,
				&anotacao.IdVersaoOrigem)
			anotacoes = append(anotacoes, anotacao)
		}
		var page mdl.PageRadares
		if errMsg != "" {
			page.ErrMsg = errMsg
		}
		if msg != "" {
			page.Msg = msg
		}
		page.Radares = radares
		page.Anotacoes = anotacoes
		page.AppName = mdl.AppName
		page.Title = "Radares" + mdl.Ambiente
		page.LoggedUser = BuildLoggedUser(GetUserInCookie(w, r))
		var tmpl = template.Must(template.ParseGlob("tiles/radares/*"))
		tmpl.ParseGlob("tiles/*")
		tmpl.ExecuteTemplate(w, "Main-Radares", page)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func LoadAnotacoesRadaresByRadarId(w http.ResponseWriter, r *http.Request) {
	log.Println("Load Anotacoes Radares By Radar Id")
	r.ParseForm()
	var radarId = r.FormValue("radarId")
	log.Println("radarId: " + radarId)
	anotacoesRadar := ListAnotacoesRadarByRadarId(radarId)
	jsonAnotacoesRadar, _ := json.Marshal(anotacoesRadar)
	w.Write([]byte(jsonAnotacoesRadar))
	log.Println("JSON Anotacoes de Radares")
}
