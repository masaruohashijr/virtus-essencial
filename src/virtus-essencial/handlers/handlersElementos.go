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

func CreateElementoHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Create Elemento")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		currentUser := GetUserInCookie(w, r)
		statusElementoId := GetStartStatus("elemento")
		nome := r.FormValue("NomeElementoForInsert")
		descricao := r.FormValue("DescricaoElementoForInsert")
		referencia := r.FormValue("ReferenciaElementoForInsert")
		sqlStatement := "INSERT INTO virtus.elementos(nome, descricao, referencia, id_author, criado_em, id_status) " +
			" OUTPUT INSERTED.id_elemento VALUES (?, ?, ?, ?, GETDATE(), ?)"
		elementoId := 0
		authorId := strconv.FormatInt(GetUserInCookie(w, r).Id, 10)
		err := Db.QueryRow(sqlStatement, nome, descricao, referencia, authorId, statusElementoId).Scan(&elementoId)
		log.Println(sqlStatement + " :: " + nome)
		if err != nil {
			log.Println(err.Error())
		}
		log.Println("INSERT: Id: " + strconv.Itoa(elementoId) + " | Nome: " + nome)
		statusItemId := GetStartStatus("itemAAvaliar")
		for key, value := range r.Form {
			if strings.HasPrefix(key, "item") {
				array := strings.Split(value[0], "#")
				log.Println(value[0])
				itemId := 0
				nomeItem := strings.Split(array[3], ":")[1]
				descricaoItem := strings.Split(array[4], ":")[1]
				referenciaItem := strings.Split(array[5], ":")[1]
				log.Println("itemId: " + strconv.Itoa(itemId))
				sqlStatement := "INSERT INTO virtus.itens( " +
					" id_elemento, nome, descricao, referencia, criado_em, id_author, id_status ) " +
					" OUTPUT INSERTED.id_item VALUES (?, ?, ?, ?, GETDATE(), ?, ?) "
				log.Println(sqlStatement)
				log.Println("elementoId: " + strconv.Itoa(elementoId))
				err = Db.QueryRow(sqlStatement, elementoId, nomeItem, descricaoItem, referenciaItem, currentUser.Id, statusItemId).Scan(&itemId)
				log.Println("itemId: " + strconv.Itoa(itemId))
				if err != nil {
					log.Println(err.Error())
				}
			}
		}
		http.Redirect(w, r, route.ElementosRoute+"?msg=Elemento criado com sucesso.", 301)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func UpdateElementoHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Update Elemento")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		currentUser := GetUserInCookie(w, r)
		elementoId := r.FormValue("ElementoIdForUpdate")
		nome := r.FormValue("ElementoNomeForUpdate")
		descricao := r.FormValue("ElementoDescricaoForUpdate")
		referencia := r.FormValue("ElementoReferenciaForUpdate")
		sqlStatement := "UPDATE virtus.elementos SET nome=?, descricao=?, referencia=? WHERE id_elemento=?"
		updtForm, err := Db.Prepare(sqlStatement)
		if err != nil {
			log.Println(err.Error())
		}
		updtForm.Exec(nome, descricao, referencia, elementoId)
		log.Println("UPDATE: Id: " + elementoId + " | Nome: " + nome + " | Descrição: " + descricao)

		// Itens
		var itensDB = ListItensHandler(elementoId)
		var itensPage []mdl.Item
		var itemPage mdl.Item
		qtdItensPage := 0
		for key, value := range r.Form {
			if strings.HasPrefix(key, "item") {
				log.Println("key: " + key)
				qtdItensPage++
				log.Println(value[0])
				array := strings.Split(value[0], "#")
				id := strings.Split(array[1], ":")[1]
				log.Println("Id -------- " + id)
				itemPage.Id, _ = strconv.ParseInt(id, 10, 64)
				elementoId := strings.Split(array[2], ":")[1]
				log.Println("elementoId -------- " + elementoId)
				itemPage.ElementoId, _ = strconv.ParseInt(id, 10, 64)
				nome := strings.Split(array[3], ":")[1]
				log.Println("nome -------- " + nome)
				itemPage.Nome = nome
				descricao := strings.Split(array[4], ":")[1]
				log.Println("descricao -------- " + descricao)
				itemPage.Descricao = descricao
				referencia := strings.Split(array[5], ":")[1]
				log.Println("referencia -------- " + referencia)
				itemPage.Referencia = referencia
				itensPage = append(itensPage, itemPage)
			}
		}
		log.Println("-----------------------")
		log.Println("Qtd Itens Page: " + strconv.Itoa(qtdItensPage))
		log.Println("Quantidade de itens da página é: " + strconv.Itoa(len(itensPage)))
		log.Println("Quantidade de itens do banco de dados é: " + strconv.Itoa(len(itensDB)))
		log.Println("-----------------------")
		if len(itensPage) < len(itensDB) {
			log.Println("Quantidade de Itens da Página: " + strconv.Itoa(len(itensPage)))
			if len(itensPage) == 0 {
				DeleteItensByElementoHandler(elementoId) //DONE
			} else {
				var diffDB []mdl.Item = itensDB
				for n := range itensPage {
					if containsItem(diffDB, itensPage[n]) {
						diffDB = removeItem(diffDB, itensPage[n])
					}
				}
				DeleteItensHandler(diffDB) //DONE
			}
		} else {
			var diffPage []mdl.Item = itensPage
			for n := range itensDB {
				if containsItem(diffPage, itensDB[n]) {
					diffPage = removeItem(diffPage, itensDB[n])
				}
			}
			log.Println("Quantidade de itens a mais: " + strconv.Itoa(len(diffPage)))
			var item mdl.Item
			itemId := 0
			statusItemId := GetStartStatus("item")
			for i := range diffPage {
				item = diffPage[i]
				log.Println("Elemento Id: " + strconv.FormatInt(item.ElementoId, 10))
				sqlStatement := "INSERT INTO " +
					"virtus.itens(id_elemento, nome, descricao, referencia, criado_em, id_author, id_status) " +
					"OUTPUT INSERTED.id_item VALUES (?,?,?,?,GETDATE(),?,?) "
				log.Println(sqlStatement)
				err := Db.QueryRow(sqlStatement, elementoId, item.Nome, item.Descricao, item.Referencia, currentUser.Id, statusItemId).Scan(&itemId)
				log.Println("itemId cadastrado: " + strconv.Itoa(itemId))
				if err != nil {
					log.Println(err.Error())
				}
			}
		}
		UpdateItensHandler(itensPage, itensDB) // TODO
		http.Redirect(w, r, route.ElementosRoute+"?msg=Elemento atualizado com sucesso.", 301)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func containsItem(itens []mdl.Item, itemCompared mdl.Item) bool {
	for n := range itens {
		if itens[n].Id == itemCompared.Id {
			return true
		}
	}
	return false
}

func removeItem(itens []mdl.Item, itemToBeRemoved mdl.Item) []mdl.Item {
	var newItens []mdl.Item
	for i := range itens {
		if itens[i].Id != itemToBeRemoved.Id {
			newItens = append(newItens, itens[i])
		}
	}
	return newItens
}

func DeleteElementoHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Delete Elemento")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		errMsg := "O Elemento está associado a um registro e não pôde ser removido."
		id := r.FormValue("Id")
		sqlStatement := "DELETE FROM virtus.elementos WHERE id_elemento=?"
		deleteForm, _ := Db.Prepare(sqlStatement)
		_, err := deleteForm.Exec(id)
		if err != nil && strings.Contains(err.Error(), "violates foreign key") {
			http.Redirect(w, r, route.ElementosRoute+"?errMsg="+errMsg, 301)
		} else {
			http.Redirect(w, r, route.ElementosRoute+"?msg=Elemento removido com sucesso.", 301)
		}
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func ListElementosHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("List Elementos")
	currentUser := GetUserInCookie(w, r)
	if sec.IsAuthenticated(w, r) && HasPermission(currentUser, "listElementos") {
		errMsg := r.FormValue("errMsg")
		msg := r.FormValue("msg")
		query := "SELECT " +
			" a.id_elemento, " +
			" a.nome, " +
			" coalesce(a.descricao,''), " +
			" coalesce(a.referencia,''), " +
			" coalesce(b.name,'') as author_name, " +
			" coalesce(format(a.criado_em, 'dd/MM/yyyy HH:mm:ss'),'') as criado_em, " +
			" a.peso, " +
			" coalesce(c.name,'') as cstatus, " +
			" a.id_status " +
			" FROM virtus.elementos a " +
			" LEFT JOIN virtus.users b ON a.id_author = b.id_user " +
			" LEFT JOIN virtus.status c ON a.id_status = c.id_status " +
			" order by a.nome asc "
		rows, _ := Db.Query(query)
		defer rows.Close()
		log.Println(query)
		var elementos []mdl.Elemento
		var elemento mdl.Elemento
		var i = 1
		for rows.Next() {
			rows.Scan(
				&elemento.Id,
				&elemento.Nome,
				&elemento.Descricao,
				&elemento.Referencia,
				&elemento.AuthorName,
				&elemento.C_CriadoEm,
				&elemento.Peso,
				&elemento.CStatus,
				&elemento.StatusId)
			elemento.Order = i
			//log.Println(elemento)
			i++
			elementos = append(elementos, elemento)
		}
		var page mdl.PageElementos
		if msg != "" {
			page.Msg = msg
		}
		if errMsg != "" {
			page.ErrMsg = errMsg
		}
		page.Elementos = elementos
		page.AppName = mdl.AppName
		page.Title = "Elementos" + mdl.Ambiente
		page.LoggedUser = BuildLoggedUser(GetUserInCookie(w, r))
		var tmpl = template.Must(template.ParseGlob("tiles/elementos/*"))
		tmpl.ParseGlob("tiles/*")
		tmpl.ExecuteTemplate(w, "Main-Elementos", page)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func LoadItensByElementoId(w http.ResponseWriter, r *http.Request) {
	log.Println("Load Itens By Elemento Id")
	r.ParseForm()
	var elementoId = r.FormValue("elementoId")
	log.Println("elementoId: " + elementoId)
	itens := ListItensHandler(elementoId)
	jsonItens, _ := json.Marshal(itens)
	w.Write([]byte(jsonItens))
	log.Println("JSON Itens de Elementos")
}
