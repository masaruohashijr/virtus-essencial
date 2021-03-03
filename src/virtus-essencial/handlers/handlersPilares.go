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

func CreatePilarHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Create Pilar")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		currentUser := GetUserInCookie(w, r)
		nome := r.FormValue("Nome")
		descricao := r.FormValue("Descricao")
		referencia := r.FormValue("Referencia")
		sqlStatement := "INSERT INTO virtus.pilares(nome, descricao, referencia, id_author, criado_em) " +
			" OUTPUT INSERTED.id_pilar VALUES (?, ?, ?, ?, GETDATE())"
		idPilar := 0
		err := Db.QueryRow(sqlStatement, nome, descricao, referencia, currentUser.Id).Scan(&idPilar)
		if err != nil {
			log.Println(err.Error())
		}

		log.Println("INSERT: Id: " + strconv.Itoa(idPilar) + " | Nome: " + nome + " | Descrição: " + descricao)
		for key, value := range r.Form {
			if strings.HasPrefix(key, "componentePilar") {
				array := strings.Split(value[0], "#")
				log.Println(value[0])
				componentePilarId := 0
				componenteId := strings.Split(array[3], ":")[1]
				tipoMediaId := strings.Split(array[5], ":")[1]
				sonda := strings.Split(array[7], ":")[1]
				pesoPadrao := strings.Split(array[8], ":")[1]
				if pesoPadrao == "" {
					pesoPadrao = "0"
				}
				sqlStatement := " INSERT INTO " +
					" virtus.componentes_pilares( " +
					" id_pilar, " +
					" id_componente, " +
					" tipo_media, " +
					" peso_padrao, " +
					" sonda, " +
					" id_author, " +
					" criado_em ) " +
					" OUTPUT INSERTED.id_componente_pilar " +
					" VALUES (?, ?, ?, ?, ?, ?, GETDATE())"
				log.Println(sqlStatement)
				err = Db.QueryRow(
					sqlStatement,
					idPilar,
					componenteId,
					tipoMediaId,
					pesoPadrao,
					sonda,
					currentUser.Id).Scan(&componentePilarId)
				if err != nil {
					log.Println(err.Error())
				}
			}
		}
		http.Redirect(w, r, route.PilaresRoute+"?msg=Pilar criado com sucesso.", 301)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func UpdatePilarHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Update Pilar")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		currentUser := GetUserInCookie(w, r)
		pilarId := r.FormValue("Id")
		nome := r.FormValue("Nome")
		descricao := r.FormValue("Descricao")
		referencia := r.FormValue("Referencia")
		sqlStatement := "UPDATE virtus.pilares SET nome=?, descricao=?, referencia=? WHERE id_pilar=?"
		updtForm, err := Db.Prepare(sqlStatement)
		if err != nil {
			log.Println(err.Error())
		}
		updtForm.Exec(nome, descricao, referencia, pilarId)
		log.Println("UPDATE: Id: " + pilarId + " | Nome: " + nome + " | Descrição: " + descricao)

		// Componentes Pilares
		var componentesPilarDB = ListComponentesByPilarId(pilarId)
		var componentesPilarPage []mdl.ComponentePilar
		var componentePilarPage mdl.ComponentePilar
		for key, value := range r.Form {
			if strings.HasPrefix(key, "componentePilar") {
				log.Println(value[0])
				array := strings.Split(value[0], "#")
				id := strings.Split(array[1], ":")[1]
				log.Println("Id -------- " + id)
				componentePilarPage.Id, _ = strconv.ParseInt(id, 10, 64)
				componentePilarPage.PilarId, _ = strconv.ParseInt(pilarId, 10, 64)
				componenteId := strings.Split(array[3], ":")[1]
				log.Println("componenteId -------- " + componenteId)
				componentePilarPage.ComponenteId, _ = strconv.ParseInt(componenteId, 10, 64)
				componenteNome := strings.Split(array[4], ":")[1]
				log.Println("componenteNome -------- " + componenteNome)
				componentePilarPage.ComponenteNome = componenteNome
				tipoMediaId := strings.Split(array[5], ":")[1]
				log.Println("tipoMediaId -------- " + tipoMediaId)
				componentePilarPage.TipoMediaId, _ = strconv.Atoi(tipoMediaId)
				tipoMedia := strings.Split(array[6], ":")[1]
				log.Println("tipoMedia -------- " + tipoMedia)
				componentePilarPage.TipoMedia = tipoMedia
				sonda := strings.Split(array[7], ":")[1]
				log.Println("sonda -------- " + sonda)
				componentePilarPage.Sonda = sonda
				pesoPadrao := strings.Split(array[8], ":")[1]
				if pesoPadrao == "" {
					pesoPadrao = "0"
				}
				log.Println("pesoPadrao -------- " + pesoPadrao)
				componentePilarPage.PesoPadrao = pesoPadrao
				authorId := strings.Split(array[9], ":")[1]
				log.Println("authorId -------- " + authorId)
				componentePilarPage.AuthorId, _ = strconv.ParseInt(authorId, 10, 64)
				authorName := strings.Split(array[10], ":")[1]
				log.Println("authorName -------- " + authorName)
				componentePilarPage.AuthorName = authorName
				criadoEm := strings.Split(array[11], ":")[1]
				log.Println("criadoEm -------- " + criadoEm)
				componentePilarPage.CriadoEm = criadoEm
				idVersaoOrigem := strings.Split(array[12], ":")[1]
				log.Println("idVersaoOrigem -------- " + idVersaoOrigem)
				componentePilarPage.IdVersaoOrigem, _ = strconv.ParseInt(idVersaoOrigem, 10, 64)
				statusId := strings.Split(array[13], ":")[1]
				log.Println("StatusId -------- " + statusId)
				componentePilarPage.StatusId, _ = strconv.ParseInt(statusId, 10, 64)
				cStatus := strings.Split(array[14], ":")[1]
				log.Println("cStatus -------- " + cStatus)
				componentePilarPage.CStatus = cStatus
				componentesPilarPage = append(componentesPilarPage, componentePilarPage)
			}
		}
		if len(componentesPilarPage) < len(componentesPilarDB) {
			log.Println("Quantidade de Componentes do Pilar da Página: " + strconv.Itoa(len(componentesPilarPage)))
			if len(componentesPilarPage) == 0 {
				DeleteComponentesPilarByPilarId(pilarId) //DONE
			} else {
				var diffDB []mdl.ComponentePilar = componentesPilarDB
				for n := range componentesPilarPage {
					if containsComponentePilar(diffDB, componentesPilarPage[n]) {
						diffDB = removeComponentePilar(diffDB, componentesPilarPage[n])
					}
				}
				DeleteComponentesPilarHandler(diffDB) //DONE
			}
		} else {
			var diffPage []mdl.ComponentePilar = componentesPilarPage
			for n := range componentesPilarDB {
				if containsComponentePilar(diffPage, componentesPilarDB[n]) {
					diffPage = removeComponentePilar(diffPage, componentesPilarDB[n])
				}
			}
			var componentePilar mdl.ComponentePilar
			componentePilarId := 0
			statusComponenteId := GetStartStatus("pilar")
			for i := range diffPage {
				componentePilar = diffPage[i]
				log.Println("Pilar Id: " + pilarId)
				sqlStatement := "INSERT INTO virtus.componentes_pilares ( " +
					" id_pilar, " +
					" id_componente, " +
					" peso_padrao, " +
					" id_author, " +
					" criado_em, " +
					" id_status " +
					" ) " +
					" OUTPUT INSERTED.id_componente_pilar " +
					" VALUES (?, ?, ?, ?, GETDATE(), ?)"
				log.Println(sqlStatement)
				row := Db.QueryRow(
					sqlStatement,
					pilarId,
					componentePilar.ComponenteId,
					componentePilar.PesoPadrao,
					currentUser.Id,
					statusComponenteId)
				err = row.Scan(&componentePilarId)
				if err != nil {
					log.Println(err.Error())
				}
			}
		}
		UpdateComponentesPilarHandler(componentesPilarPage, componentesPilarDB)

		http.Redirect(w, r, route.PilaresRoute+"?msg=Pilar atualizado com sucesso.", 301)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func DeletePilarHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Delete Pilar")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		id := r.FormValue("Id")
		errMsg := "Pilar vinculado a registro não pode ser removido."
		sqlStatement := "DELETE FROM virtus.componentes_pilares WHERE id_pilar=?"
		deleteForm, _ := Db.Prepare(sqlStatement)
		_, err := deleteForm.Exec(id)
		sqlStatement = "DELETE FROM virtus.pilares WHERE id_pilar=?"
		deleteForm, _ = Db.Prepare(sqlStatement)
		_, err = deleteForm.Exec(id)
		if err != nil && strings.Contains(err.Error(), "violates foreign key") {
			http.Redirect(w, r, route.PilaresRoute+"?errMsg="+errMsg, 301)
		} else {
			http.Redirect(w, r, route.PilaresRoute+"?msg=Pilar removido com sucesso.", 301)
		}
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func ListPilaresHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("List Pilares")
	currentUser := GetUserInCookie(w, r)
	if sec.IsAuthenticated(w, r) && HasPermission(currentUser, "listPilares") {
		errMsg := r.FormValue("errMsg")
		msg := r.FormValue("msg")
		sql := "SELECT " +
			" a.id_pilar, " +
			" a.nome, " +
			" coalesce(a.descricao,''), " +
			" coalesce(a.referencia,''), " +
			" a.id_author, " +
			" coalesce(b.name,''), " +
			" coalesce(format(a.criado_em, 'dd/MM/yyyy HH:mm:ss'),''), " +
			" coalesce(c.name,'') as cstatus, " +
			" a.id_status, " +
			" a.id_versao_origem " +
			" FROM virtus.pilares a LEFT JOIN virtus.users b " +
			" ON a.id_author = b.id_user " +
			" LEFT JOIN virtus.status c ON a.id_status = c.id_status " +
			" order by a.id_pilar asc"
		log.Println(sql)
		rows, _ := Db.Query(sql)
		defer rows.Close()
		var pilares []mdl.Pilar
		var pilar mdl.Pilar
		var i = 1
		for rows.Next() {
			rows.Scan(
				&pilar.Id,
				&pilar.Nome,
				&pilar.Descricao,
				&pilar.Referencia,
				&pilar.AuthorId,
				&pilar.AuthorName,
				&pilar.C_CriadoEm,
				&pilar.CStatus,
				&pilar.StatusId,
				&pilar.IdVersaoOrigem)
			pilar.Order = i
			i++
			log.Println(pilar)
			pilares = append(pilares, pilar)
		}
		sql = "SELECT id_componente, nome FROM virtus.componentes ORDER BY id_componente asc"
		log.Println(sql)
		rows, _ = Db.Query(sql)
		defer rows.Close()
		var componentes []mdl.Componente
		var componente mdl.Componente
		i = 1
		for rows.Next() {
			rows.Scan(&componente.Id, &componente.Nome)
			componente.Order = i
			i++
			componentes = append(componentes, componente)
		}
		var page mdl.PagePilares
		if msg != "" {
			page.Msg = msg
		}
		if errMsg != "" {
			page.ErrMsg = errMsg
		}
		page.Pilares = pilares
		page.Componentes = componentes
		page.AppName = mdl.AppName
		page.Title = "Pilares" + mdl.Ambiente
		page.LoggedUser = BuildLoggedUser(GetUserInCookie(w, r))
		var tmpl = template.Must(template.ParseGlob("tiles/pilares/*"))
		tmpl.ParseGlob("tiles/*")
		tmpl.ExecuteTemplate(w, "Main-Pilares", page)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func LoadComponentesByPilarId(w http.ResponseWriter, r *http.Request) {
	log.Println("Load Componentes By Pilar Id")
	r.ParseForm()
	var pilarId = r.FormValue("pilarId")
	log.Println("pilarId: " + pilarId)
	componentesPilar := ListComponentesByPilarId(pilarId)
	jsonComponenesPilar, _ := json.Marshal(componentesPilar)
	w.Write([]byte(jsonComponenesPilar))
	log.Println("JSON Componentes de Pilar")
}
