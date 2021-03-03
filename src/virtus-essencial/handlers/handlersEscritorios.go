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

func CreateEscritorioHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Create Escritorio")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		currentUser := GetUserInCookie(w, r)
		nome := r.FormValue("Nome")
		descricao := r.FormValue("Descricao")
		abreviatura := r.FormValue("Abreviatura")
		chefe := r.FormValue("Chefe")
		sqlStatement := "INSERT INTO virtus.escritorios" +
			" (nome, descricao, abreviatura, id_chefe, id_author, criado_em) " +
			" OUTPUT INSERTED.id_escritorio " +
			" VALUES (?, ?, ?, ?, ?, GETDATE()) "
		id := 0
		Db.QueryRow(sqlStatement, nome, descricao, abreviatura, chefe, currentUser.Id).Scan(&id)
		log.Println("INSERT: Id: " + strconv.Itoa(id) + " | Nome: " + nome + " | Descrição: " + descricao)
		if chefe != "" {
			sqlStatement = "INSERT INTO virtus.membros ( " +
				" id_escritorio, " +
				" id_usuario, " +
				" id_author, " +
				" criado_em " +
				" ) " +
				" VALUES (?, ?, ?, GETDATE()) "
			log.Println(sqlStatement)
			Db.QueryRow(
				sqlStatement,
				id,
				chefe,
				currentUser.Id)
		}
		http.Redirect(w, r, route.EscritoriosRoute+"?msg=Escritório criado com sucesso.", 301)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func UpdateEscritorioHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Update Escritorio")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		id := r.FormValue("Id")
		nome := r.FormValue("Nome")
		descricao := r.FormValue("Descricao")
		abreviatura := r.FormValue("Abreviatura")
		chefe := r.FormValue("Chefe")
		if chefe != "" {
			sqlStatement := "UPDATE virtus.escritorios SET nome=?, descricao=?, abreviatura=?, id_chefe=? WHERE id_escritorio=?"
			updtForm, _ := Db.Prepare(sqlStatement)
			updtForm.Exec(nome, descricao, abreviatura, chefe, id)
			sqlStatement = "INSERT INTO virtus.membros ( " +
				" id_escritorio, " +
				" id_usuario, " +
				" id_author, " +
				" criado_em " +
				" ) " +
				" SELECT ?, ?, ?, GETDATE() WHERE NOT EXISTS " +
				" (SELECT 1 FROM virtus.membros WHERE id_escritorio = ? AND id_usuario =?)"
			log.Println(sqlStatement)
			Db.QueryRow(
				sqlStatement,
				id,
				chefe,
				GetUserInCookie(w, r).Id,
				id,
				chefe)
		} else {
			sqlStatement := "UPDATE virtus.escritorios SET nome=?, descricao=?, abreviatura=? WHERE id_escritorio=?"
			updtForm, _ := Db.Prepare(sqlStatement)
			updtForm.Exec(nome, descricao, abreviatura, id)
		}
		log.Println("UPDATE: Id: " + id + " | Nome: " + nome + " | Abreviatura: " + abreviatura + " | Descrição: " + descricao + " | Chefe: " + chefe)
		http.Redirect(w, r, route.EscritoriosRoute+"?msg=Escritório atualizado com sucesso.", 301)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func DeleteEscritorioHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Delete Escritorio")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		id := r.FormValue("Id")
		sqlStatement := "DELETE FROM virtus.escritorios WHERE id_escritorio=?"
		deleteForm, err := Db.Prepare(sqlStatement)
		if err != nil {
			log.Println(err.Error())
		}
		_, err = deleteForm.Exec(id)
		if err != nil {
			log.Println(err.Error())
		}
		if err != nil && strings.Contains(err.Error(), "violates foreign key") {
			http.Redirect(w, r, route.EscritoriosRoute+"?errMsg=Escritório vinculado a Membro ou Jurisdicão não pode ser removido.", 301)
		} else {
			http.Redirect(w, r, route.EscritoriosRoute+"?msg=Escritório removido com sucesso.", 301)
		}
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func ListEscritoriosHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("List Escritorios")
	currentUser := GetUserInCookie(w, r)
	if sec.IsAuthenticated(w, r) && HasPermission(currentUser, "listEscritorios") {
		errMsg := r.FormValue("errMsg")
		msg := r.FormValue("msg")
		var page mdl.PageEscritorios
		page = listEscritorios(errMsg, msg)
		page.LoggedUser = BuildLoggedUser(GetUserInCookie(w, r))
		var tmpl = template.Must(template.ParseGlob("tiles/escritorios/*"))
		tmpl.ParseGlob("tiles/*")
		tmpl.ExecuteTemplate(w, "Main-Escritorios", page)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func LoadJurisdicoesByEscritorioId(w http.ResponseWriter, r *http.Request) {
	log.Println("Load Jurisdições By Escritório Id")
	r.ParseForm()
	var escritorioId = r.FormValue("escritorioId")
	log.Println("escritorioId: " + escritorioId)
	jurisdicoes := ListJurisdicoesByEscritorioId(escritorioId)
	jsonJurisdicoes, _ := json.Marshal(jurisdicoes)
	w.Write([]byte(jsonJurisdicoes))
	log.Println("JSON Jurisdições")
}

func LoadMembrosByEscritorioId(w http.ResponseWriter, r *http.Request) {
	log.Println("Load Membros By Escritório Id")
	r.ParseForm()
	var escritorioId = r.FormValue("escritorioId")
	log.Println("escritorioId: " + escritorioId)
	membros := ListMembrosByEscritorioId(escritorioId)
	jsonMembros, _ := json.Marshal(membros)
	w.Write([]byte(jsonMembros))
	log.Println("JSON Membros")
}

func listEscritorios(errorMsg string, msg string) mdl.PageEscritorios {
	sql := "SELECT " +
		" a.id_escritorio, " +
		" a.nome, " +
		" a.descricao, " +
		" a.abreviatura, " +
		" coalesce(a.id_chefe,0), " +
		" coalesce(d.name,'') as chefe_name, " +
		" a.id_author, " +
		" coalesce(b.name,'') as author_name, " +
		" format(a.criado_em, 'dd/MM/yyyy HH:mm:ss'), " +
		" coalesce(c.name,'') as cstatus, " +
		" a.id_status, " +
		" a.id_versao_origem " +
		" FROM virtus.escritorios a LEFT JOIN virtus.users b " +
		" ON a.id_author = b.id_user " +
		" LEFT JOIN virtus.status c ON a.id_status = c.id_status " +
		" LEFT JOIN virtus.users d ON a.id_chefe = d.id_user " +
		" order by a.id_escritorio asc"
	log.Println(sql)
	rows, _ := Db.Query(sql)
	defer rows.Close()
	var escritorios []mdl.Escritorio
	var escritorio mdl.Escritorio
	var i = 1
	for rows.Next() {
		rows.Scan(
			&escritorio.Id,
			&escritorio.Nome,
			&escritorio.Descricao,
			&escritorio.Abreviatura,
			&escritorio.ChefeId,
			&escritorio.ChefeNome,
			&escritorio.AuthorId,
			&escritorio.AuthorName,
			&escritorio.C_CriadoEm,
			&escritorio.CStatus,
			&escritorio.StatusId,
			&escritorio.IdVersaoOrigem)
		escritorio.Order = i
		i++
		log.Println(escritorio)
		escritorios = append(escritorios, escritorio)
	}
	var page mdl.PageEscritorios
	page.Escritorios = escritorios

	sql = "SELECT a.id_user, a.name, a.id_role, " +
		" coalesce(b.name,'SEM PERFIL') as role_name " +
		" FROM virtus.users a " +
		" LEFT JOIN virtus.roles b ON a.id_role = b.id_role " +
		" ORDER BY a.name asc"
	rows, _ = Db.Query(sql)
	defer rows.Close()
	var users []mdl.User
	var user mdl.User
	i = 1
	for rows.Next() {
		rows.Scan(&user.Id, &user.Name, &user.Role, &user.RoleName)
		user.Order = i
		i++
		users = append(users, user)
	}
	page.Users = users

	sql = "SELECT id_entidade, nome, sigla FROM virtus.entidades ORDER BY sigla asc"
	log.Println(sql)
	rows, _ = Db.Query(sql)
	defer rows.Close()
	var entidades []mdl.Entidade
	var entidade mdl.Entidade
	i = 1
	for rows.Next() {
		rows.Scan(&entidade.Id, &entidade.Nome, &entidade.Sigla)
		entidade.Order = i
		i++
		entidades = append(entidades, entidade)
	}
	page.Entidades = entidades
	page.AppName = mdl.AppName
	page.Title = "Escritórios" + mdl.Ambiente
	if errorMsg != "" {
		page.ErrMsg = errorMsg
	}
	log.Println("Mensagem: " + msg)
	if msg != "" {
		page.Msg = msg
	}
	return page
}
