package handlers

import (
	"html/template"
	"log"
	"net/http"
	"strconv"
	"strings"
	e "virtus-essencial/errors"
	mdl "virtus-essencial/models"
	route "virtus-essencial/routes"
	sec "virtus-essencial/security"
)

func CreateStatusHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Create Status")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		currentUser := GetUserInCookie(w, r)
		r.ParseForm()
		name := r.FormValue("Name")
		description := r.FormValue("Descricao")
		stereotype := r.FormValue("Stereotype")
		sqlStatement := "INSERT INTO virtus.status(name, description, stereotype, id_author, created_at) " +
			" OUTPUT INSERTED.id_status VALUES ('" + name + "', '" +
			description + "', '" + stereotype + "', ?, GETDATE())"
		id := 0
		log.Println(sqlStatement)
		row := Db.QueryRow(sqlStatement, currentUser.Id)
		err := row.Scan(&id)
		if err != nil && strings.Contains(err.Error(), "duplicate key") {
			page := listStatus(e.ErroChaveDuplicada)
			page.LoggedUser = BuildLoggedUser(GetUserInCookie(w, r))
			var tmpl = template.Must(template.ParseGlob("tiles/status/*"))
			tmpl.ParseGlob("tiles/*")
			tmpl.ExecuteTemplate(w, "Main-Status", page)
		}
		log.Println("INSERT: Id: " + strconv.Itoa(id) + " | Name: " + name + " | Descricao: " + description + " | Stereotype: " + stereotype)
		http.Redirect(w, r, route.StatusRoute+"?msg=Status criado com sucesso.", 301)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func UpdateStatusHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Update Status")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		id := r.FormValue("Id")
		log.Println(id)
		name := r.FormValue("Name")
		log.Println(name)
		description := r.FormValue("Description")
		log.Println(description)
		stereotype := r.FormValue("Stereotype")
		stmt, err := Db.Prepare("UPDATE virtus.status SET name=?, description=?, stereotype=? WHERE id_status=?")
		if err != nil {
			log.Println(err.Error())
		}
		defer stmt.Close()
		_, err = stmt.Exec(name, description, stereotype, id)
		if err != nil {
			log.Println(err)
		}
		log.Println("UPDATE: Id: " + id + " | Name: " + name + " | Descricao: " + description + " | Stereotype: " + stereotype)
		http.Redirect(w, r, route.StatusRoute+"?msg=Status atualizado com sucesso.", 301)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func DeleteStatusHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Delete Status")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		id := r.FormValue("Id")
		errMsg := "Status vinculado a registro não pode ser removido."
		sqlStatement := "DELETE FROM virtus.status WHERE id_status=?"
		deleteForm, err := Db.Prepare(sqlStatement)
		_, err = deleteForm.Exec(id)
		if err != nil && strings.Contains(err.Error(), "violates foreign key") {
			http.Redirect(w, r, route.StatusRoute+"?errMsg="+errMsg, 301)
		} else {
			http.Redirect(w, r, route.StatusRoute+"?msg=Status removido com sucesso.", 301)
		}
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func ListStatusHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("List Status")
	currentUser := GetUserInCookie(w, r)
	if sec.IsAuthenticated(w, r) && HasPermission(currentUser, "listStatus") {
		msg := r.FormValue("msg")
		errMsg := r.FormValue("errMsg")
		page := listStatus("")
		if msg != "" {
			page.Msg = msg
		}
		if errMsg != "" {
			page.ErrMsg = errMsg
		}
		page.LoggedUser = BuildLoggedUser(GetUserInCookie(w, r))
		var tmpl = template.Must(template.ParseGlob("tiles/status/*"))
		tmpl.ParseGlob("tiles/*")
		tmpl.ExecuteTemplate(w, "Main-Status", page)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func listStatus(errorMsg string) mdl.PageStatus {
	sql := "SELECT " +
		" a.id_status, " +
		" a.name, " +
		" coalesce(a.description,'') as descr, " +
		" coalesce(a.stereotype,'') as stereo_type, " +
		" a.id_author, " +
		" b.name, " +
		" format(a.created_at,'dd/MM/yyyy HH:mm:ss'), " +
		" coalesce(c.name,'') as cstatus, " +
		" a.id_status, " +
		" a.id_versao_origem " +
		" FROM virtus.status a LEFT JOIN virtus.users b " +
		" ON a.id_author = b.id_user " +
		" LEFT JOIN virtus.status c ON a.id_status = c.id_status " +
		" order by a.id_status asc"
	log.Println("sql: " + sql)
	rows, _ := Db.Query(sql)
	defer rows.Close()
	var statuss []mdl.Status
	var status mdl.Status
	var i = 1
	for rows.Next() {
		rows.Scan(
			&status.Id,
			&status.Name,
			&status.Description,
			&status.Stereotype,
			&status.AuthorId,
			&status.AuthorName,
			&status.C_CreatedAt,
			&status.CStatus,
			&status.StatusId,
			&status.IdVersaoOrigem)
		status.Order = i
		i++
		statuss = append(statuss, status)
	}
	var page mdl.PageStatus
	page.Statuss = statuss
	page.AppName = mdl.AppName
	page.Title = "Status" + mdl.Ambiente
	if errorMsg != "" {
		page.ErrMsg = errorMsg
	}
	return page
}
