package handlers

import (
	"html/template"
	"log"
	"net/http"
	"strconv"
	"strings"
	mdl "virtus-essencial/models"
	route "virtus-essencial/routes"
	sec "virtus-essencial/security"
)

func CreateTipoNotaHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Create Tipo Nota")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		currentUser := GetUserInCookie(w, r)
		r.ParseForm()
		nome := r.FormValue("Nome")
		descricao := r.FormValue("Descricao")
		referencia := r.FormValue("Referencia")
		letra := r.FormValue("Letra")
		corLetra := r.FormValue("CorLetra")
		sqlStatement := "INSERT INTO virtus.tipos_notas(nome, descricao, referencia, letra, cor_letra, id_author, criado_em)  OUTPUT INSERTED.id_tipo_nota VALUES (?, ?, ?, ?, ?, GETDATE())"
		tipoNotaId := 0
		err := Db.QueryRow(sqlStatement, nome, descricao, referencia, letra, corLetra, currentUser.Id).Scan(&tipoNotaId)
		if err != nil {
			log.Println(err.Error())
		}
		log.Println("INSERT: Id: " + strconv.Itoa(tipoNotaId) + " | Nome: " + nome)
		http.Redirect(w, r, route.TiposNotasRoute+"?msg=Tipo de Nota criado com sucesso.", 301)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func UpdateTipoNotaHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Update Tipo Nota")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		tipoNotaId := r.FormValue("Id")
		nome := r.FormValue("Nome")
		descricao := r.FormValue("Descricao")
		referencia := r.FormValue("Referencia")
		letra := r.FormValue("Letra")
		corLetra := r.FormValue("CorLetra")
		sqlStatement := "UPDATE virtus.tipos_notas SET nome=?, descricao=?, referencia=?, letra=?, cor_letra=? WHERE id_tipo_nota=?"
		updtForm, err := Db.Prepare(sqlStatement)
		if err != nil {
			log.Println(err.Error())
		}
		_, err = updtForm.Exec(nome, descricao, referencia, letra, corLetra, tipoNotaId)
		if err != nil {
			log.Println(err.Error())
		}
		log.Println("UPDATE: Id: " + tipoNotaId + " | Nome: " + nome + " | Descrição: " + descricao + " | Cor Letra: " + corLetra)
		http.Redirect(w, r, route.TiposNotasRoute+"?msg=Tipo de Nota atualizado com sucesso.", 301)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func DeleteTipoNotaHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Delete Tipo de Nota")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		id := r.FormValue("Id")
		errMsg := "Tipo de Nota vinculado a registro não pode ser removido."
		sqlStatement := "DELETE FROM virtus.tipos_notas WHERE id_tipo_nota=?"
		deleteForm, _ := Db.Prepare(sqlStatement)
		_, err := deleteForm.Exec(id)
		if err != nil && strings.Contains(err.Error(), "violates foreign key") {
			http.Redirect(w, r, route.TiposNotasRoute+"?errMsg="+errMsg, 301)
		} else {
			http.Redirect(w, r, route.TiposNotasRoute+"?msg=Tipo de Nota removido com sucesso.", 301)
		}
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func ListTiposNotasHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("List Tipos de Notas")
	currentUser := GetUserInCookie(w, r)
	if sec.IsAuthenticated(w, r) && HasPermission(currentUser, "listTiposNotas") {
		msg := r.FormValue("msg")
		errMsg := r.FormValue("errMsg")
		sql := "SELECT " +
			" a.id_tipo_nota, " +
			" coalesce(a.nome,''), " +
			" coalesce(a.descricao,''), " +
			" coalesce(a.referencia,''), " +
			" coalesce(a.letra,''), " +
			" coalesce(a.cor_letra,''), " +
			" a.id_author, " +
			" b.name, " +
			" format(a.criado_em, 'dd/MM/yyyy HH:mm:ss'), " +
			" coalesce(c.name,'') as cstatus, " +
			" a.id_status, " +
			" a.id_versao_origem " +
			" FROM virtus.tipos_notas a LEFT JOIN virtus.users b " +
			" ON a.id_author = b.id_user " +
			" LEFT JOIN virtus.status c ON a.id_status = c.id_status " +
			" order by id_tipo_nota asc"
		log.Println("sql: " + sql)
		rows, _ := Db.Query(sql)
		defer rows.Close()
		var tiposNotas []mdl.TipoNota
		var tipoNota mdl.TipoNota
		var i = 1
		for rows.Next() {
			rows.Scan(
				&tipoNota.Id,
				&tipoNota.Nome,
				&tipoNota.Descricao,
				&tipoNota.Referencia,
				&tipoNota.Letra,
				&tipoNota.CorLetra,
				&tipoNota.AuthorId,
				&tipoNota.AuthorName,
				&tipoNota.C_CreatedAt,
				&tipoNota.CStatus,
				&tipoNota.StatusId,
				&tipoNota.IdVersaoOrigem)
			tipoNota.Order = i
			i++
			tiposNotas = append(tiposNotas, tipoNota)
		}
		var page mdl.PageTiposNotas
		if msg != "" {
			page.Msg = msg
		}
		if errMsg != "" {
			page.ErrMsg = errMsg
		}
		page.TiposNotas = tiposNotas
		page.AppName = mdl.AppName
		page.Title = "Tipos de Notas" + mdl.Ambiente
		page.LoggedUser = BuildLoggedUser(GetUserInCookie(w, r))
		var tmpl = template.Must(template.ParseGlob("tiles/tiposnotas/*"))
		tmpl.ParseGlob("tiles/*")
		tmpl.ExecuteTemplate(w, "Main-Tipos-Notas", page)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}
