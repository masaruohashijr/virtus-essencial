package handlers

import (
	"log"
	"net/http"
	"strconv"
	"strings"
	mdl "virtus-essencial/models"
	route "virtus-essencial/routes"
	sec "virtus-essencial/security"
)

func UpdateJurisdicaoHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Update Jurisdicao")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		currentUser := GetUserInCookie(w, r)
		escritorioId := r.FormValue("Id")
		nome := r.FormValue("Nome")
		descricao := r.FormValue("Descricao")
		chefe := r.FormValue("Chefe")
		sqlStatement := "UPDATE virtus.escritorios SET nome=?, descricao=?, id_chefe=? WHERE id_escritorio=?"
		updtForm, err := Db.Prepare(sqlStatement)
		if err != nil {
			log.Println(err.Error())
		}
		updtForm.Exec(nome, descricao, chefe, escritorioId)
		log.Println("UPDATE: Id: " + escritorioId + " | Nome: " + nome + " | Descrição: " + descricao + " | Chefe: " + chefe)

		// Jurisdições
		log.Println("===== Jurisdições =====")
		var jurisdicoesDB = ListJurisdicoesByEscritorioId(escritorioId)
		var jurisdicoesPage []mdl.Jurisdicao
		var jurisdicaoPage mdl.Jurisdicao
		for key, value := range r.Form {
			if strings.HasPrefix(key, "jurisdicao") {
				//log.Println(value[0])
				array := strings.Split(value[0], "#")
				id := strings.Split(array[1], ":")[1]
				log.Println("Id -------- " + id)
				jurisdicaoPage.Id, _ = strconv.ParseInt(id, 10, 64)
				jurisdicaoPage.EscritorioId, _ = strconv.ParseInt(escritorioId, 10, 64)
				entidadeId := strings.Split(array[3], ":")[1]
				log.Println("entidadeId -------- " + entidadeId)
				jurisdicaoPage.EntidadeId, _ = strconv.ParseInt(entidadeId, 10, 64)
				entidadeNome := strings.Split(array[4], ":")[1]
				log.Println("entidadeNome -------- " + entidadeNome)
				jurisdicaoPage.EntidadeNome = entidadeNome
				entidadeSigla := strings.Split(array[5], ":")[1]
				log.Println("entidadeSigla -------- " + entidadeSigla)
				jurisdicaoPage.EntidadeSigla = entidadeSigla
				iniciaEm := strings.Split(array[6], ":")[1]
				log.Println("iniciaEm -------- " + iniciaEm)
				jurisdicaoPage.IniciaEm = iniciaEm
				terminaEm := strings.Split(array[7], ":")[1]
				log.Println("terminaEm -------- " + terminaEm)
				jurisdicaoPage.TerminaEm = terminaEm
				autorId := strings.Split(array[8], ":")[1]
				log.Println("autorId -------- " + autorId)
				jurisdicaoPage.AuthorId, _ = strconv.ParseInt(autorId, 10, 64)
				autorNome := strings.Split(array[9], ":")[1]
				log.Println("autorNome -------- " + autorNome)
				jurisdicaoPage.AuthorName = autorNome
				criadoEm := strings.Split(array[10], ":")[1]
				log.Println("criadoEm -------- " + criadoEm)
				jurisdicaoPage.CriadoEm = criadoEm
				idVersaoOrigem := strings.Split(array[11], ":")[1]
				log.Println("idVersaoOrigem -------- " + idVersaoOrigem)
				jurisdicaoPage.IdVersaoOrigem, _ = strconv.ParseInt(idVersaoOrigem, 10, 64)
				statusId := strings.Split(array[12], ":")[1]
				log.Println("statusId -------- " + statusId)
				jurisdicaoPage.StatusId, _ = strconv.ParseInt(statusId, 10, 64)
				cStatus := strings.Split(array[13], ":")[1]
				log.Println("cStatus -------- " + cStatus)
				jurisdicaoPage.CStatus = cStatus
				jurisdicoesPage = append(jurisdicoesPage, jurisdicaoPage)
			}
		}
		if len(jurisdicoesPage) < len(jurisdicoesDB) {
			log.Println("Quantidade de Entidades do Escritório na Página: " + strconv.Itoa(len(jurisdicoesPage)))
			if len(jurisdicoesPage) == 0 {
				DeleteJurisdicoesByEscritorioId(escritorioId) //DONE
			} else {
				var diffDB []mdl.Jurisdicao = jurisdicoesDB
				for n := range jurisdicoesPage {
					if containsJurisdicao(diffDB, jurisdicoesPage[n]) {
						diffDB = removeJurisdicao(diffDB, jurisdicoesPage[n])
					}
				}
				DeleteJurisdicoesHandler(diffDB) //DONE
			}
		} else {
			var diffPage []mdl.Jurisdicao = jurisdicoesPage
			for n := range jurisdicoesDB {
				if containsJurisdicao(diffPage, jurisdicoesDB[n]) {
					diffPage = removeJurisdicao(diffPage, jurisdicoesDB[n])
				}
			}
			var jurisdicao mdl.Jurisdicao
			jurisdicaoId := 0
			statusJurisdicaoId := GetStartStatus("jurisdicao")
			for i := range diffPage {
				jurisdicao = diffPage[i]
				log.Println("Escritório Id: " + escritorioId)
				sqlStatement := "INSERT INTO virtus.jurisdicoes ( " +
					" id_escritorio, " +
					" id_entidade, " +
					" id_author, " +
					" criado_em, " +
					" id_status " +
					" ) " +
					" OUTPUT INSERTED.id_jurisdicao " +
					" VALUES (?, ?, ?, GETDATE(), ?)"
				log.Println(sqlStatement)
				err := Db.QueryRow(
					sqlStatement,
					escritorioId,
					jurisdicao.EntidadeId,
					currentUser.Id,
					statusJurisdicaoId).Scan(&jurisdicaoId)
				if err != nil {
					log.Println("ERRO AO INSERIR JURISDICAO")
					log.Println(err)
				}
				log.Println("De " + jurisdicao.IniciaEm + " a " + jurisdicao.TerminaEm)
				if jurisdicao.IniciaEm != "" {
					sqlStatement = "UPDATE virtus.jurisdicoes SET inicia_em = CAST('" + jurisdicao.IniciaEm + "' AS DATETIME) " + "WHERE id_jurisdicao = " + strconv.Itoa(jurisdicaoId)
					log.Println(sqlStatement)
					_, err = Db.Exec(sqlStatement)
					if err != nil {
						log.Println(err)
					}
				}
				if jurisdicao.TerminaEm != "" {
					sqlStatement = "UPDATE virtus.jurisdicoes SET termina_em = CAST('" + jurisdicao.TerminaEm + "' AS DATETIME) " + "WHERE id_jurisdicao = " + strconv.Itoa(jurisdicaoId)
					log.Println(sqlStatement)
					_, err = Db.Exec(sqlStatement)
					if err != nil {
						log.Println(err)
					}
				}
			}
		}
		UpdateJurisdicoesHandler(jurisdicoesPage, jurisdicoesDB)

		http.Redirect(w, r, route.EscritoriosRoute+"?msg=A jurisdição do escritório foi atualizada com sucesso.", 301)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

// AJAX
func ListJurisdicoesByEscritorioId(escritorioId string) []mdl.Jurisdicao {
	log.Println("List Jurisdições By Escritório Id")
	log.Println("escritorioId: " + escritorioId)
	sql := "SELECT " +
		"a.id_jurisdicao, " +
		"a.id_escritorio, " +
		"a.id_entidade, " +
		"coalesce(d.nome,'') as entidade_nome, " +
		"coalesce(d.sigla,'') as entidade_sigla, " +
		"coalesce(format(a.inicia_em,'dd/MM/yyyy'), '') as inicia_em, " +
		"coalesce(format(a.termina_em,'dd/MM/yyyy'), '') as termina_em, " +
		"a.id_author, " +
		"coalesce(b.name,'') as author_name, " +
		"coalesce(format(a.criado_em,'dd/MM/yyyy'), '') as criado_em, " +
		"a.id_status, " +
		"coalesce(c.name,'') as status_name " +
		"FROM virtus.jurisdicoes a " +
		"LEFT JOIN virtus.entidades d ON a.id_entidade = d.id_entidade " +
		"LEFT JOIN virtus.users b ON a.id_author = b.id_user " +
		"LEFT JOIN virtus.status c ON a.id_status = c.id_status " +
		"WHERE a.id_escritorio = ? ORDER BY d.nome ASC "
	log.Println(sql)
	rows, _ := Db.Query(sql, escritorioId)
	defer rows.Close()
	var jurisdicoes []mdl.Jurisdicao
	var jurisdicao mdl.Jurisdicao
	var i = 1
	for rows.Next() {
		rows.Scan(
			&jurisdicao.Id,
			&jurisdicao.EscritorioId,
			&jurisdicao.EntidadeId,
			&jurisdicao.EntidadeNome,
			&jurisdicao.EntidadeSigla,
			&jurisdicao.IniciaEm,
			&jurisdicao.TerminaEm,
			&jurisdicao.AuthorId,
			&jurisdicao.AuthorName,
			&jurisdicao.CriadoEm,
			&jurisdicao.StatusId,
			&jurisdicao.CStatus)
		jurisdicao.Order = i
		jurisdicao.EntidadeNome = strings.ReplaceAll(jurisdicao.EntidadeNome, ",", " ")
		i++
		jurisdicoes = append(jurisdicoes, jurisdicao)
		//log.Println(jurisdicao)
	}
	return jurisdicoes
}

func UpdateJurisdicoesHandler(jurisdicoesPage []mdl.Jurisdicao, jurisdicoesDB []mdl.Jurisdicao) {
	for i := range jurisdicoesPage { // BACEN BB CAIXA (PAGE) -> (DB) BACEN BB BANESES
		id := jurisdicoesPage[i].Id
		log.Println("Jurisdicao Page id: " + strconv.FormatInt(id, 10))
		for j := range jurisdicoesDB {
			//log.Println("jurisdicoesDB[j].Id: " + strconv.FormatInt(jurisdicoesDB[j].Id, 10))
			if strconv.FormatInt(jurisdicoesDB[j].Id, 10) == strconv.FormatInt(id, 10) {
				fieldsChanged := hasSomeFieldChangedJurisdicao(jurisdicoesPage[i], jurisdicoesDB[j]) //DONE
				log.Println(fieldsChanged)
				if fieldsChanged {
					updateJurisdicaoHandler(jurisdicoesPage[i], jurisdicoesDB[j])
				}
				jurisdicoesDB = removeJurisdicao(jurisdicoesDB, jurisdicoesPage[i]) // CORREÇÃO
				break
			}
		}
	}
	DeleteJurisdicoesHandler(jurisdicoesDB) // CORREÇÃO
}

func hasSomeFieldChangedJurisdicao(jurisdicaoPage mdl.Jurisdicao, jurisdicaoDB mdl.Jurisdicao) bool {
	if jurisdicaoPage.EntidadeId != jurisdicaoDB.EntidadeId {
		return true
	} else if jurisdicaoPage.IniciaEm != jurisdicaoDB.IniciaEm {
		return true
	} else if jurisdicaoPage.TerminaEm != jurisdicaoDB.TerminaEm {
		return true
	} else {
		return false
	}
}

func updateJurisdicaoHandler(jurisdicao mdl.Jurisdicao, jurisdicaoDB mdl.Jurisdicao) {
	sqlStatement := "UPDATE virtus.jurisdicoes SET " +
		"id_entidade=? WHERE id_jurisdicao=?"
	log.Println(sqlStatement)
	updtForm, _ := Db.Prepare(sqlStatement)
	_, err := updtForm.Exec(jurisdicao.EntidadeId, jurisdicao.Id)
	if err != nil {
		log.Println(err)
	}
	log.Println("De " + jurisdicao.IniciaEm + " a " + jurisdicao.TerminaEm)
	if jurisdicao.IniciaEm != "" {
		sqlStatement = "UPDATE virtus.jurisdicoes SET inicia_em = CAST('" + jurisdicao.IniciaEm + "' AS DATETIME) " + "WHERE id_jurisdicao = " + strconv.FormatInt(jurisdicao.Id, 10)
		_, err = Db.Exec(sqlStatement)
		if err != nil {
			log.Println(err)
		}
	}
	if jurisdicao.TerminaEm != "" {
		sqlStatement = "UPDATE virtus.jurisdicoes SET termina_em = CAST('" + jurisdicao.TerminaEm + "' AS DATETIME) " + "WHERE id_jurisdicao = " + strconv.FormatInt(jurisdicao.Id, 10)
		_, err = Db.Exec(sqlStatement)
		if err != nil {
			log.Println(err)
		}
	}
	log.Println("Statement: " + sqlStatement)
}

func DeleteJurisdicoesByEscritorioId(escritorioId string) {
	sqlStatement := "DELETE FROM virtus.jurisdicoes WHERE id_escritorio=?"
	deleteForm, err := Db.Prepare(sqlStatement)
	if err != nil {
		log.Println(err.Error())
	}
	deleteForm.Exec(escritorioId)
	log.Println("DELETE jurisdicoes do Escritorio Id: " + escritorioId)
}

func DeleteJurisdicoesHandler(diffDB []mdl.Jurisdicao) {
	sqlStatement := "DELETE FROM virtus.jurisdicoes WHERE id_jurisdicao=?"
	deleteForm, err := Db.Prepare(sqlStatement)
	if err != nil {
		log.Println(err.Error())
	}
	for n := range diffDB {
		deleteForm.Exec(strconv.FormatInt(int64(diffDB[n].Id), 10))
		log.Println("DELETE: Jurisdicao Id: " + strconv.FormatInt(int64(diffDB[n].Id), 10))
	}
}

func containsJurisdicao(jurisdicoes []mdl.Jurisdicao, jurisdicaoCompared mdl.Jurisdicao) bool {
	for n := range jurisdicoes {
		if jurisdicoes[n].Id == jurisdicaoCompared.Id {
			return true
		}
	}
	return false
}

func removeJurisdicao(jurisdicoes []mdl.Jurisdicao, jurisdicaoToBeRemoved mdl.Jurisdicao) []mdl.Jurisdicao {
	var newJurisdicoes []mdl.Jurisdicao
	for i := range jurisdicoes {
		if jurisdicoes[i].Id != jurisdicaoToBeRemoved.Id {
			newJurisdicoes = append(newJurisdicoes, jurisdicoes[i])
		}
	}
	return newJurisdicoes
}
