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

func CreateComponenteHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Create Componente")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		currentUser := GetUserInCookie(w, r)
		nome := r.FormValue("Nome")
		descricao := r.FormValue("Descricao")
		referencia := r.FormValue("Referencia")
		statusComponenteId := GetStartStatus("componente")
		sqlStatement := "INSERT INTO virtus.componentes(nome, descricao, referencia, pga, id_author, criado_em, id_status) " +
			" OUTPUT INSERTED.id_componente " +
			" VALUES (?, ?, ?, ?, ?, GETDATE(), ?)"
		idComponente := 0
		err := Db.QueryRow(sqlStatement, nome, descricao, referencia, 'N', currentUser.Id, statusComponenteId).Scan(&idComponente)
		if err != nil {
			log.Println(err.Error())
		}
		log.Println("INSERT: Id: " + strconv.Itoa(idComponente) + " | Nome: " + nome + " | Descrição: " + descricao)
		for key, value := range r.Form {
			if strings.HasPrefix(key, "elementoComponente") {
				array := strings.Split(value[0], "#")
				log.Println(value[0])
				elementoComponenteId := 0
				statusElementoId := GetStartStatus("elemento")
				elementoId := strings.Split(array[3], ":")[1]
				tipoNotaId := strings.Split(array[3], ":")[1]
				pesoPadrao := strings.Split(array[5], ":")[1]
				sqlStatement := " INSERT INTO " +
					" virtus.elementos_componentes( " +
					" id_componente, " +
					" id_elemento, " +
					" id_tipo_nota, " +
					" peso_padrao, " +
					" id_author, " +
					" criado_em, " +
					" id_status) " +
					" OUTPUT INSERTED.id_elemento_componente " +
					" VALUES (?, ?, ?, ?, ?, GETDATE(), ?)"
				log.Println(sqlStatement)
				err := Db.QueryRow(
					sqlStatement,
					idComponente,
					elementoId,
					tipoNotaId,
					pesoPadrao,
					currentUser.Id,
					statusElementoId).Scan(&elementoComponenteId)
				if err != nil {
					log.Println(err.Error())
				}
			}
			if strings.HasPrefix(key, "TipoNota_") {
				log.Println(value[0])
				tipoNotaComponenteId := 0
				tipoNotaId := strings.Split(key, "_")[1]
				pesoPadrao := value[0]
				statusTipoNotaId := GetStartStatus("tipo_nota")
				sqlStatement := " INSERT INTO " +
					" virtus.tipos_notas_componentes( " +
					" id_componente," +
					" id_tipo_nota," +
					" peso_padrao, " +
					" id_author, " +
					" criado_em, " +
					" id_status) " +
					" OUTPUT INSERTED.id_tipo_nota_componente " +
					" VALUES (?, ?, ?, ?, GETDATE(), ?)"
				log.Println(sqlStatement)
				err := Db.QueryRow(
					sqlStatement,
					idComponente,
					tipoNotaId,
					pesoPadrao,
					currentUser.Id,
					statusTipoNotaId).Scan(&tipoNotaComponenteId)
				if err != nil {
					log.Println(err.Error())
				}
			}
		}
		http.Redirect(w, r, route.ComponentesRoute+"?msg=Componente criado com sucesso.", 301)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func UpdateComponenteHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Update Componente")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		currentUser := GetUserInCookie(w, r)
		componenteId := r.FormValue("Id")
		nome := r.FormValue("Nome")
		descricao := r.FormValue("Descricao")
		referencia := r.FormValue("Referencia")
		log.Println("Referencia: " + referencia)
		pga := r.FormValue("PGA")
		log.Println("PGA: " + pga)
		somentePGA := "N"
		if pga != "" {
			somentePGA = "S"
		}
		sqlStatement := "UPDATE virtus.componentes SET nome=?, descricao=?, referencia=?, pga=? WHERE id_componente=?"
		updtForm, err := Db.Prepare(sqlStatement)
		if err != nil {
			log.Println(err.Error())
		}
		updtForm.Exec(nome, descricao, referencia, somentePGA, componenteId)
		log.Println("UPDATE: Id: " + componenteId + " | Nome: " + nome + " | Descrição: " + descricao)

		// Elementos Componentes
		var elementosComponenteDB = ListElementosByComponenteId(componenteId)
		var elementosComponentePage []mdl.ElementoComponente
		var elementoComponentePage mdl.ElementoComponente
		for key, value := range r.Form {
			if strings.HasPrefix(key, "elementoComponente") {
				log.Println(value[0])
				array := strings.Split(value[0], "#")
				id := strings.Split(array[1], ":")[1]
				log.Println("Id -------- " + id)
				elementoComponentePage.Id, _ = strconv.ParseInt(id, 10, 64)
				elementoComponentePage.ComponenteId, _ = strconv.ParseInt(componenteId, 10, 64)
				elementoId := strings.Split(array[3], ":")[1]
				log.Println("elementoId -------- " + elementoId)
				elementoComponentePage.ElementoId, _ = strconv.ParseInt(elementoId, 10, 64)
				elementoNome := strings.Split(array[4], ":")[1]
				log.Println("elementoNome -------- " + elementoNome)
				elementoComponentePage.ElementoNome = elementoNome
				tipoNotaId := strings.Split(array[5], ":")[1]
				log.Println("tipoNotaId -------- " + tipoNotaId)
				elementoComponentePage.TipoNotaId, _ = strconv.ParseInt(tipoNotaId, 10, 64)
				tipoNotaNome := strings.Split(array[6], ":")[1]
				log.Println("tipoNotaNome -------- " + tipoNotaNome)
				elementoComponentePage.TipoNotaNome = tipoNotaNome
				pesoPadrao := strings.Split(array[7], ":")[1]
				log.Println("pesoPadrao -------- " + pesoPadrao)
				elementoComponentePage.PesoPadrao, _ = strconv.Atoi(pesoPadrao)
				authorId := strings.Split(array[8], ":")[1]
				log.Println("authorId -------- " + authorId)
				elementoComponentePage.AuthorId, _ = strconv.ParseInt(authorId, 10, 64)
				authorName := strings.Split(array[9], ":")[1]
				log.Println("authorName -------- " + authorName)
				elementoComponentePage.AuthorName = authorName
				criadoEm := strings.Split(array[10], ":")[1]
				log.Println("criadoEm -------- " + criadoEm)
				elementoComponentePage.CriadoEm = criadoEm
				idVersaoOrigem := strings.Split(array[11], ":")[1]
				log.Println("idVersaoOrigem -------- " + idVersaoOrigem)
				elementoComponentePage.IdVersaoOrigem, _ = strconv.ParseInt(idVersaoOrigem, 10, 64)
				statusId := strings.Split(array[12], ":")[1]
				log.Println("StatusId -------- " + statusId)
				elementoComponentePage.StatusId, _ = strconv.ParseInt(statusId, 10, 64)
				cStatus := strings.Split(array[13], ":")[1]
				log.Println("cStatus -------- " + cStatus)
				elementoComponentePage.CStatus = cStatus
				elementosComponentePage = append(elementosComponentePage, elementoComponentePage)
			}
			if strings.HasPrefix(key, "TipoNota_") {
				tipoNotaId := strings.Split(key, "_")[1]
				tipoNotaPeso := value[0]
				if tipoNotaPeso != "" {
					sqlStatement = "INSERT INTO virtus.tipos_notas_componentes (id_tipo_nota,id_componente) " +
						" SELECT " + tipoNotaId + ", " + componenteId +
						" WHERE NOT EXISTS (select 1 from virtus.tipos_notas_componentes " +
						" WHERE id_tipo_nota = " + tipoNotaId + " AND id_componente = " + componenteId + ")"
					log.Println(sqlStatement)
					Db.QueryRow(sqlStatement)
					sqlStatement = "UPDATE virtus.tipos_notas_componentes SET peso_padrao=? WHERE id_tipo_nota=? AND id_componente = ?"
					updtForm, err = Db.Prepare(sqlStatement)
					if err != nil {
						log.Println(err.Error())
					}
					updtForm.Exec(value[0], tipoNotaId, componenteId)
					log.Println("UPDATE: Tipo Nota PESO: " + tipoNotaPeso + " - Id: " + tipoNotaId)
				}
			}
		}
		if len(elementosComponentePage) < len(elementosComponenteDB) {
			log.Println("Quantidade de Elementos do Componente da Página: " + strconv.Itoa(len(elementosComponentePage)))
			if len(elementosComponentePage) == 0 {
				DeleteElementosComponenteByComponenteId(componenteId) //DONE
			} else {
				var diffDB []mdl.ElementoComponente = elementosComponenteDB
				for n := range elementosComponentePage {
					if containsElementoComponente(diffDB, elementosComponentePage[n]) {
						diffDB = removeElementoComponente(diffDB, elementosComponentePage[n])
					}
				}
				DeleteElementosComponenteHandler(diffDB) //DONE
			}
		} else {
			var diffPage []mdl.ElementoComponente = elementosComponentePage
			for n := range elementosComponenteDB {
				if containsElementoComponente(diffPage, elementosComponenteDB[n]) {
					diffPage = removeElementoComponente(diffPage, elementosComponenteDB[n])
				}
			}
			var elementoComponente mdl.ElementoComponente
			elementoComponenteId := 0
			statusElementoId := GetStartStatus("elemento")
			for i := range diffPage {
				elementoComponente = diffPage[i]
				log.Println("Componente Id: " + componenteId)
				sqlStatement := "INSERT INTO virtus.elementos_componentes ( " +
					" id_componente, " +
					" id_elemento, " +
					" peso_padrao, " +
					" id_author, " +
					" criado_em, " +
					" id_status " +
					" ) " +
					" OUTPUT INSERTED.id_componente " +
					" VALUES (?, ?, ?, ?, GETDATE(), ?)"
				log.Println(sqlStatement)
				Db.QueryRow(
					sqlStatement,
					componenteId,
					elementoComponente.ElementoId,
					elementoComponente.PesoPadrao,
					currentUser.Id,
					statusElementoId).Scan(&elementoComponenteId)
			}
		}
		UpdateElementosComponenteHandler(elementosComponentePage, elementosComponenteDB)
	}
	http.Redirect(w, r, route.ComponentesRoute+"?msg=Componente atualizado com sucesso.", 301)
}

func DeleteComponenteHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Delete Componente")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		errMsg := "O Componente está associado a um registro e não pôde ser removido."
		id := r.FormValue("Id")
		sqlStatement := "DELETE FROM virtus.componentes WHERE id_componente=?"
		deleteForm, _ := Db.Prepare(sqlStatement)
		_, err := deleteForm.Exec(id)
		if err != nil && strings.Contains(err.Error(), "violates foreign key") {
			http.Redirect(w, r, route.ComponentesRoute+"?errMsg="+errMsg, 301)
		} else {
			http.Redirect(w, r, route.ComponentesRoute+"?msg=Componente removido com sucesso.", 301)
		}
	}
	http.Redirect(w, r, route.ComponentesRoute+"?msg=Componente removido com sucesso.", 301)
}

func ListComponentesHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("List Componentes")
	currentUser := GetUserInCookie(w, r)
	if sec.IsAuthenticated(w, r) && HasPermission(currentUser, "listCiclos") {
		errMsg := r.FormValue("errMsg")
		msg := r.FormValue("msg")
		sql := "SELECT " +
			" a.id_componente, " +
			" a.nome, " +
			" coalesce(a.descricao,''), " +
			" coalesce(a.referencia,''), " +
			" case when a.pga = 'S' then 'Sim' else 'Não' end as PGA, " +
			" a.id_author, " +
			" b.name, " +
			" format(a.criado_em, 'dd/MM/yyyy HH:mm:ss'), " +
			" coalesce(c.name,'') as cstatus, " +
			" a.id_status, " +
			" a.id_versao_origem " +
			" FROM virtus.componentes a LEFT JOIN virtus.users b " +
			" ON a.id_author = b.id_user " +
			" LEFT JOIN virtus.status c ON a.id_status = c.id_status " +
			" ORDER BY id_componente asc"
		log.Println(sql)
		rows, _ := Db.Query(sql)
		defer rows.Close()
		var componentes []mdl.Componente
		var componente mdl.Componente
		var i = 1
		for rows.Next() {
			rows.Scan(
				&componente.Id,
				&componente.Nome,
				&componente.Descricao,
				&componente.Referencia,
				&componente.PGA,
				&componente.AuthorId,
				&componente.AuthorName,
				&componente.C_CriadoEm,
				&componente.CStatus,
				&componente.StatusId,
				&componente.IdVersaoOrigem)
			componente.Order = i
			i++
			componentes = append(componentes, componente)
		}
		sql = "SELECT id_elemento, nome FROM virtus.elementos ORDER BY id_elemento asc"
		rows, _ = Db.Query(sql)
		defer rows.Close()
		var elementos []mdl.Elemento
		var elemento mdl.Elemento
		i = 1
		for rows.Next() {
			rows.Scan(&elemento.Id, &elemento.Nome)
			elemento.Order = i
			i++
			elementos = append(elementos, elemento)
		}

		sql = "SELECT id_tipo_nota, nome FROM virtus.tipos_notas ORDER BY nome desc"
		log.Println(sql)
		rows, _ = Db.Query(sql)
		defer rows.Close()
		var tiposNota []mdl.TipoNota
		var tipoNota mdl.TipoNota
		i = 1
		for rows.Next() {
			rows.Scan(&tipoNota.Id, &tipoNota.Nome)
			tiposNota = append(tiposNota, tipoNota)
		}

		var page mdl.PageComponentes
		if msg != "" {
			page.Msg = msg
		}
		if errMsg != "" {
			page.ErrMsg = errMsg
		}
		page.TiposNota = tiposNota
		page.Componentes = componentes
		page.Elementos = elementos
		page.AppName = mdl.AppName
		page.Title = "Componentes" + mdl.Ambiente
		page.LoggedUser = BuildLoggedUser(GetUserInCookie(w, r))
		var tmpl = template.Must(template.ParseGlob("tiles/componentes/*"))
		tmpl.ParseGlob("tiles/*")
		tmpl.ExecuteTemplate(w, "Main-Componentes", page)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func LoadElementosByComponenteId(w http.ResponseWriter, r *http.Request) {
	log.Println("Load Elementos By Componente Id")
	r.ParseForm()
	var componenteId = r.FormValue("componenteId")
	log.Println("componenteId: " + componenteId)
	elementosComponente := ListElementosByComponenteId(componenteId)
	jsonElementosComponente, _ := json.Marshal(elementosComponente)
	w.Write([]byte(jsonElementosComponente))
	log.Println("JSON Elementos de Componente")
}

func LoadTiposNotaByComponenteId(w http.ResponseWriter, r *http.Request) {
	log.Println("Load Tipos Nota By Componente Id")
	r.ParseForm()
	var componenteId = r.FormValue("componenteId")
	tiposNotasComponentes := ListTiposNotaByComponenteId(componenteId)
	jsonTiposNotasComponentes, _ := json.Marshal(tiposNotasComponentes)
	w.Write([]byte(jsonTiposNotasComponentes))
	log.Println("JSON Tipos Notas Componentes")
}
