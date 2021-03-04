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

func ListDesignarEquipesHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("List Designar Equipes Handler")
	currentUser := GetUserInCookie(w, r)
	if sec.IsAuthenticated(w, r) && HasPermission(currentUser, "designarEquipes") {
		log.Println("--------------")
		msg := r.FormValue("msg")
		errMsg := r.FormValue("errMsg")
		var page mdl.PageEntidadesCiclos
		sql := " SELECT b.id_entidade, c.nome, c.codigo, a.abreviatura " +
			" FROM virtus.escritorios a " +
			" LEFT JOIN virtus.jurisdicoes b ON a.id_escritorio = b.id_escritorio " +
			" LEFT JOIN virtus.entidades c ON c.id_entidade = b.id_entidade " +
			" INNER JOIN virtus.ciclos_entidades d ON d.id_entidade = b.id_entidade " +
			" WHERE a.id_chefe = ?"
		log.Println(sql)
		rows, _ := Db.Query(sql, currentUser.Id)
		defer rows.Close()
		var entidades []mdl.Entidade
		var entidade mdl.Entidade
		var i = 1
		for rows.Next() {
			rows.Scan(
				&entidade.Id,
				&entidade.Nome,
				&entidade.Codigo,
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
				" ORDER BY id_ciclo_entidade asc"
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

		sql = " WITH subordinacoes AS " +
			"   (SELECT b.id_usuario, " +
			"           a.id_supervisor " +
			"    FROM virtus.ciclos_entidades a " +
			"    INNER JOIN virtus.integrantes b  " +
			"    ON (a.id_ciclo = b.id_ciclo AND a.id_entidade = b.id_entidade)) " +
			" SELECT DISTINCT b.id_usuario, " +
			"        coalesce(c.name, '') AS nome_auditor, " +
			"        coalesce(d.name, '') AS role_name, " +
			"        coalesce(s.id_supervisor,0) AS subordinacao " +
			" FROM virtus.escritorios a " +
			" LEFT JOIN virtus.membros b ON a.id_escritorio = b.id_escritorio " +
			" LEFT JOIN virtus.users c ON b.id_usuario = c.id_user " +
			" LEFT JOIN virtus.ROLES d ON c.id_role = d.id_role " +
			" LEFT JOIN subordinacoes s ON B.id_usuario = s.id_usuario " +
			" WHERE a.id_chefe = ? " +
			"   AND c.id_role in (2,3,4) " +
			" ORDER BY nome_auditor "
		log.Println(sql)
		rows, _ = Db.Query(sql, currentUser.Id)
		defer rows.Close()
		var membros []mdl.Membro
		var membro mdl.Membro
		i = 1
		for rows.Next() {
			rows.Scan(
				&membro.UsuarioId,
				&membro.UsuarioNome,
				&membro.UsuarioPerfil,
				&membro.SubordinacaoId)
			membros = append(membros, membro)
		}
		sql = " SELECT " +
			" b.id_usuario, coalesce(c.name,'') AS nome_usuario, " +
			" coalesce(d.name,'') as role_name " +
			" FROM virtus.escritorios a " +
			" LEFT JOIN virtus.membros b ON a.id_escritorio = b.id_escritorio " +
			" LEFT JOIN virtus.users c ON b.id_usuario = c.id_user " +
			" LEFT JOIN virtus.roles d ON c.id_role = d.id_role " +
			" WHERE a.id_chefe = ? AND c.id_role in (2,3) " +
			" ORDER BY nome_usuario"
		log.Println(sql)
		rows, _ = Db.Query(sql, currentUser.Id)
		defer rows.Close()
		var supervisores []mdl.User
		var supervisor mdl.User
		i = 1
		for rows.Next() {
			rows.Scan(&supervisor.Id, &supervisor.Name, &supervisor.RoleName)
			supervisores = append(supervisores, supervisor)
		}
		if msg != "" {
			page.Msg = msg
		}
		if errMsg != "" {
			page.ErrMsg = errMsg
		}
		page.Supervisores = supervisores
		page.Membros = membros
		page.Entidades = entidadesCiclos
		page.AppName = mdl.AppName
		page.Title = "Designar Equipes" + mdl.Ambiente
		page.LoggedUser = BuildLoggedUser(GetUserInCookie(w, r))
		var tmpl = template.Must(template.ParseGlob("tiles/designarequipes/*"))
		tmpl.ParseGlob("tiles/*")
		tmpl.ExecuteTemplate(w, "Main-Designar-Equipes", page)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func UpdateDesignarEquipeHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("************ Update Designar Equipe ************")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		currentUser := GetUserInCookie(w, r)
		entidadeId := r.FormValue("EntidadeId")
		cicloId := r.FormValue("CicloId")
		supervisorId := r.FormValue("SupervisorId")

		if supervisorId != "" {
			sqlStatement := "UPDATE virtus.ciclos_entidades SET id_supervisor=" + supervisorId +
				" WHERE id_entidade=" + entidadeId + " AND id_ciclo=" + cicloId
			log.Println(sqlStatement)
			updtForm, _ := Db.Prepare(sqlStatement)
			sqlResult, err := updtForm.Exec()
			log.Println(sqlResult.RowsAffected())
			if err != nil {
				log.Println(err.Error())
			}
			sqlStatement = "UPDATE virtus.produtos_componentes SET id_supervisor=" + supervisorId +
				" WHERE id_entidade=" + entidadeId + " AND id_ciclo=" + cicloId
			log.Println(sqlStatement)
			updtForm, _ = Db.Prepare(sqlStatement)
			sqlResult, err = updtForm.Exec()
			log.Println(sqlResult.RowsAffected())
			if err != nil {
				log.Println(err.Error())
			}
		} else {
			http.Redirect(w, r, "/listDesignarEquipes?errMsg=O supervisor da equipe não pode ser deixado em branco.", 301)
		}
		log.Println("UPDATE: EntidadeId: " + entidadeId + " | CicloId: " + cicloId + " | SupervisorId: " + supervisorId)

		// Integrantes
		var integrantesDB = ListIntegrantesByEntidadeIdByCicloId(entidadeId, cicloId)
		var integrantesPage []mdl.Integrante
		var integrantePage mdl.Integrante
		for key, value := range r.Form {
			if strings.HasPrefix(key, "integrante") {
				log.Println(value[0])
				array := strings.Split(value[0], "#")
				id := strings.Split(array[1], ":")[1]
				log.Println("Id -------- " + id)
				integrantePage.Id, _ = strconv.ParseInt(id, 10, 64)
				integrantePage.EntidadeId, _ = strconv.ParseInt(entidadeId, 10, 64)
				integrantePage.CicloId, _ = strconv.ParseInt(cicloId, 10, 64)
				usuarioId := strings.Split(array[4], ":")[1]
				log.Println("usuarioId -------- " + usuarioId)
				integrantePage.UsuarioId, _ = strconv.ParseInt(usuarioId, 10, 64)
				usuarioNome := strings.Split(array[5], ":")[1]
				log.Println("usuarioNome -------- " + usuarioNome)
				integrantePage.UsuarioNome = usuarioNome
				usuarioPerfil := strings.Split(array[6], ":")[1]
				log.Println("usuarioPerfil -------- " + usuarioPerfil)
				integrantePage.UsuarioPerfil = usuarioPerfil
				iniciaEm := strings.Split(array[7], ":")[1]
				log.Println("iniciaEm -------- " + iniciaEm)
				integrantePage.IniciaEm = iniciaEm
				terminaEm := strings.Split(array[8], ":")[1]
				log.Println("terminaEm -------- " + terminaEm)
				integrantePage.TerminaEm = terminaEm
				autorId := strings.Split(array[9], ":")[1]
				log.Println("autorId -------- " + autorId)
				integrantePage.AuthorId, _ = strconv.ParseInt(autorId, 10, 64)
				autorNome := strings.Split(array[10], ":")[1]
				log.Println("autorNome -------- " + autorNome)
				integrantePage.AuthorName = autorNome
				criadoEm := strings.Split(array[11], ":")[1]
				log.Println("criadoEm -------- " + criadoEm)
				integrantePage.CriadoEm = criadoEm
				idVersaoOrigem := strings.Split(array[12], ":")[1]
				log.Println("idVersaoOrigem -------- " + idVersaoOrigem)
				integrantePage.IdVersaoOrigem, _ = strconv.ParseInt(idVersaoOrigem, 10, 64)
				statusId := strings.Split(array[13], ":")[1]
				log.Println("statusId -------- " + statusId)
				integrantePage.StatusId, _ = strconv.ParseInt(statusId, 10, 64)
				cStatus := strings.Split(array[14], ":")[1]
				log.Println("cStatus -------- " + cStatus)
				integrantePage.CStatus = cStatus
				integrantesPage = append(integrantesPage, integrantePage)
			}
		}
		if len(integrantesPage) < len(integrantesDB) {
			log.Println("Quantidade de Integrante da EntidadeCiclo da Página: " + strconv.Itoa(len(integrantesPage)))
			if len(integrantesPage) == 0 {
				DeleteIntegrantesByEntidadeCicloId(entidadeId, cicloId) //DONE
			} else {
				var diffDB []mdl.Integrante = integrantesDB
				for n := range integrantesPage {
					if containsIntegrante(diffDB, integrantesPage[n]) {
						diffDB = removeIntegrante(diffDB, integrantesPage[n])
					}
				}
				DeleteIntegrantesHandler(diffDB) //DONE
			}
		} else {
			var diffPage []mdl.Integrante = integrantesPage
			for n := range integrantesDB {
				if containsIntegrante(diffPage, integrantesDB[n]) {
					diffPage = removeIntegrante(diffPage, integrantesDB[n])
				}
			}
			var integrante mdl.Integrante
			integranteId := 0
			statusComponenteId := GetStartStatus("integrante")
			for i := range diffPage {
				integrante = diffPage[i]
				sqlStatement := "INSERT INTO virtus.integrantes ( " +
					" id_entidade, " +
					" id_ciclo, " +
					" id_usuario, " +
					" id_author, " +
					" criado_em, " +
					" id_status " +
					" ) " +
					" OUTPUT INSERTED.id_integrante " +
					" VALUES (?, ?, ?, ?, GETDATE(), ?)"
				log.Println(sqlStatement)
				Db.QueryRow(
					sqlStatement,
					entidadeId,
					cicloId,
					integrante.UsuarioId,
					currentUser.Id,
					statusComponenteId).Scan(&integranteId)
				if integrante.IniciaEm != "" {
					log.Println("integrante.IniciaEm: " + integrante.IniciaEm)
					log.Println("integranteId: " + strconv.Itoa(integranteId))
					sqlStatement := "UPDATE virtus.integrantes SET inicia_em = to_date('" +
						integrante.IniciaEm + "','dd/MM/yyyy') " +
						"WHERE id_integrante = " + strconv.Itoa(integranteId)
					_, err := Db.Exec(sqlStatement)
					if err != nil {
						log.Println(err)
					}
				}
				if integrante.TerminaEm != "" {
					log.Println("integrante.TerminaEm: " + integrante.TerminaEm)
					log.Println("integranteId: " + strconv.Itoa(integranteId))
					sqlStatement := "UPDATE virtus.integrantes SET termina_em = to_date('" +
						integrante.TerminaEm + "','dd/MM/yyyy') " +
						"WHERE id_integrante = " + strconv.Itoa(integranteId)
					_, err := Db.Exec(sqlStatement)
					if err != nil {
						log.Println(err)
					}
				}
				log.Println("Usuário cadastrado: " + integrante.UsuarioNome)
			}
		}
		UpdateIntegrantesHandler(integrantesPage, integrantesDB)
		http.Redirect(w, r, "/listDesignarEquipes?msg=Equipe atualizada com sucesso.", 301)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func containsIntegrante(membros []mdl.Integrante, membroCompared mdl.Integrante) bool {
	for n := range membros {
		if membros[n].Id == membroCompared.Id {
			return true
		}
	}
	return false
}

func removeIntegrante(membros []mdl.Integrante, membroToBeRemoved mdl.Integrante) []mdl.Integrante {
	var newIntegrantes []mdl.Integrante
	for i := range membros {
		if membros[i].Id != membroToBeRemoved.Id {
			newIntegrantes = append(newIntegrantes, membros[i])
		}
	}
	return newIntegrantes
}

func DeleteIntegrantesByEntidadeCicloId(entidadeId string, cicloId string) {
	sqlStatement := "DELETE FROM virtus.integrantes WHERE id_entidade=? AND id_ciclo = ?"
	deleteForm, err := Db.Prepare(sqlStatement)
	if err != nil {
		log.Println(err.Error())
	}
	deleteForm.Exec(entidadeId, cicloId)
	log.Println("DELETE integrantes in Entidade Id: " + entidadeId + " - " + cicloId)
}

func DeleteIntegrantesHandler(diffDB []mdl.Integrante) {
	sqlStatement := "DELETE FROM virtus.integrantes WHERE id_integrante=?"
	deleteForm, err := Db.Prepare(sqlStatement)
	if err != nil {
		log.Println(err.Error())
	}
	for n := range diffDB {
		deleteForm.Exec(strconv.FormatInt(int64(diffDB[n].Id), 10))
		log.Println("DELETE: Integrante Id: " + strconv.FormatInt(int64(diffDB[n].Id), 10))
	}
}

func UpdateIntegrantesHandler(integrantesPage []mdl.Integrante, integrantesDB []mdl.Integrante) {
	for i := range integrantesPage {
		id := integrantesPage[i].Id
		log.Println("id: " + strconv.FormatInt(id, 10))
		for j := range integrantesDB {
			log.Println("integrantesDB[j].Id: " + strconv.FormatInt(integrantesDB[j].Id, 10))
			if strconv.FormatInt(integrantesDB[j].Id, 10) == strconv.FormatInt(id, 10) {
				fieldsChanged := hasSomeFieldChangedIntegrante(integrantesPage[i], integrantesDB[j]) //DONE
				log.Println(fieldsChanged)
				if fieldsChanged {
					updateIntegranteHandler(integrantesPage[i], integrantesDB[j]) // TODO
				}
				integrantesDB = removeIntegrante(integrantesDB, integrantesPage[i]) // CORREÇÃO
				break
			}
		}
	}
	DeleteIntegrantesHandler(integrantesDB) // CORREÇÃO
}

func hasSomeFieldChangedIntegrante(integrantePage mdl.Integrante, integranteDB mdl.Integrante) bool {
	log.Println("integrantePage.Nome: " + integrantePage.UsuarioNome)
	log.Println("integranteDB.Nome: " + integranteDB.UsuarioNome)
	if integrantePage.IniciaEm != integranteDB.IniciaEm {
		return true
	} else if integrantePage.TerminaEm != integranteDB.TerminaEm {
		return true
	} else {
		return false
	}
}

func updateIntegranteHandler(integrante mdl.Integrante, jurisdicaoDB mdl.Integrante) {
	log.Println("De " + integrante.IniciaEm + " a " + integrante.TerminaEm)
	if integrante.IniciaEm != "" {
		sqlStatement := "UPDATE virtus.integrantes SET inicia_em = to_date('" +
			integrante.IniciaEm + "','dd/MM/yyyy') " +
			"WHERE id_integrante = " + strconv.FormatInt(integrante.Id, 10)
		_, err := Db.Exec(sqlStatement)
		if err != nil {
			log.Println(err)
		}
		log.Println(sqlStatement)
	}
	if integrante.TerminaEm != "" {
		sqlStatement := "UPDATE virtus.integrantes SET termina_em = to_date('" +
			integrante.TerminaEm + "','dd/MM/yyyy') " +
			"WHERE id_integrante = " + strconv.FormatInt(integrante.Id, 10)
		_, err := Db.Exec(sqlStatement)
		log.Println("Statement: " + sqlStatement)
		if err != nil {
			log.Println(err)
		}
		log.Println(sqlStatement)
	}
}

func LoadIntegrantesByEntidadeIdCicloId(w http.ResponseWriter, r *http.Request) {
	log.Println("Load Integrantes")
	r.ParseForm()
	var entidadeId = r.FormValue("entidadeId")
	var cicloId = r.FormValue("cicloId")
	log.Println("entidadeId: " + entidadeId)
	log.Println("cicloId: " + cicloId)
	integrantes := ListIntegrantesByEntidadeIdByCicloId(entidadeId, cicloId)
	jsonIntegrantes, _ := json.Marshal(integrantes)
	w.Write([]byte(jsonIntegrantes))
	log.Println("JSON Integrantes")
}

func LoadSupervisorByEntidadeIdCicloId(w http.ResponseWriter, r *http.Request) {
	log.Println("Load Supervisor")
	r.ParseForm()
	var entidadeId = r.FormValue("entidadeId")
	var cicloId = r.FormValue("cicloId")
	log.Println("entidadeId: " + entidadeId)
	log.Println("cicloId: " + cicloId)
	sql := "SELECT a.id_supervisor, b.name " +
		"FROM virtus.ciclos_entidades a " +
		"LEFT JOIN virtus.users b ON a.id_supervisor = b.id_user " +
		"WHERE a.id_entidade = ? AND a.id_ciclo = ? "
	log.Println(sql)
	rows, _ := Db.Query(sql, entidadeId, cicloId)
	defer rows.Close()
	var supervisor mdl.User
	if rows.Next() {
		rows.Scan(
			&supervisor.Id,
			&supervisor.Name)
		log.Println(supervisor)
	}
	jsonSupervisor, _ := json.Marshal(supervisor)
	w.Write([]byte(jsonSupervisor))
	log.Println("JSON Supervisor")
}
