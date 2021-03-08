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

func ExecuteActionHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Execute Action")
	r.ParseForm()
	var entityType = r.FormValue("entityType")
	var id = r.FormValue("id")
	var actionId = r.FormValue("actionId")
	log.Println("id: " + id)
	log.Println("actionId: " + actionId)
	// atualiza o status do Pedido e retorna o novo statusId
	sec.IsAuthenticated(w, r)
	log.Println("Update Action")
	var tableName = ""
	var idName = ""
	if entityType == "elemento" {
		tableName = "elementos"
		idName = "elemento"
	} else if entityType == "item" {
		tableName = "itens"
		idName = "item"
	} else if entityType == "usuario" {
		tableName = "users"
		idName = "user"
	} else if entityType == "chamado" {
		tableName = "chamados"
		idName = "chamado"
	}
	// verificar brecha de segurança aqui acesso GET com parametros.
	sqlStatement := "update virtus." + tableName + " set id_status = " +
		" (select id_destination_status from virtus.actions " +
		" where id_action = ?) where id_" + idName + " = ?"
	log.Println(sqlStatement)
	updtForm, err := Db.Prepare(sqlStatement)
	if err != nil {
		log.Println(err.Error())
	}
	updtForm.Exec(actionId, id)
	log.Println("UPDATE: Id: " + actionId)

	sqlStatement = "SELECT a.id_status, b.name FROM virtus." + tableName + " a LEFT JOIN virtus.status b ON a.id_status = b.id_status WHERE a.id_" + idName + " = ?"
	log.Println("Query: " + sqlStatement)
	rows, _ := Db.Query(sqlStatement, id)
	defer rows.Close()
	var status mdl.Status
	for rows.Next() {
		err = rows.Scan(&status.Id, &status.Name)
	}
	log.Println("Retornando o Status: " + strconv.FormatInt(status.Id, 10) + " - " + status.Name)
	jsonStatus, _ := json.Marshal(status)
	w.Write([]byte(jsonStatus))
	log.Println("JSON ExecuteAction")
}

func CreateActionHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Create Action")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		currentUser := GetUserInCookie(w, r)
		name := r.FormValue("Name")
		except := r.FormValue("Except")
		otherThan := 0
		if except != "" {
			otherThan = 1
		}
		log.Println(except)
		originStatus := r.Form["OriginStatusForInsert"]
		log.Println(originStatus)
		destinationStatus := r.Form["DestinationStatusForInsert"]
		log.Println(destinationStatus)
		description := r.FormValue("DescriptionForInsert")
		sqlStatement := "INSERT INTO virtus.actions(name, id_origin_status, id_destination_status, other_than, description, id_author, created_at) " +
			" OUTPUT INSERTED.id_action VALUES (?, ?, ?, ?, ?, ?, GETDATE())"
		actionId := 0
		err := Db.QueryRow(
			sqlStatement,
			name,
			originStatus[0],
			destinationStatus[0],
			otherThan,
			description,
			currentUser.Id).Scan(&actionId)
		if err != nil {
			log.Println(err.Error())
		}
		sqlStatement = "INSERT INTO virtus.actions_status(id_action,id_origin_status,id_destination_status) VALUES (?,?,?)"
		Db.QueryRow(sqlStatement, actionId, originStatus[0], destinationStatus[0])
		if err != nil {
			log.Println(err.Error())
		}
		log.Println("INSERT: Id: " + strconv.Itoa(actionId) + " | Name: " + name)
		http.Redirect(w, r, route.ActionsRoute+"?msg=Ação criada com sucesso.", 301)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func UpdateActionHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Update Action")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		actionId := r.FormValue("Id")
		name := r.FormValue("Name")
		description := r.FormValue("Description")
		except := r.FormValue("ExceptForUpdate")
		otherThan := false
		if except != "" {
			otherThan = true
		}
		log.Println(except)
		originStatus := r.Form["OriginStatusForUpdate"]
		log.Println(originStatus)
		destinationStatus := r.Form["DestinationStatusForUpdate"]
		log.Println(destinationStatus)
		query := "SELECT id_origin_status, id_destination_status FROM virtus.actions_status WHERE id_action = ? "
		log.Println("List Action -> Query: " + query)
		rows, _ := Db.Query(query, actionId)
		defer rows.Close()
		originStatusDB := ""
		destinationStatusDB := ""
		for rows.Next() {
			rows.Scan(&originStatusDB, &destinationStatusDB)
		}

		if originStatus[0] != originStatusDB || destinationStatus[0] != destinationStatusDB {
			sqlStatement := "DELETE FROM virtus.actions_status WHERE id_action=?"
			deleteForm, _ := Db.Prepare(sqlStatement)
			_, err := deleteForm.Exec(actionId)
			if err != nil && strings.Contains(err.Error(), "violates foreign key") {
				http.Redirect(w, r, route.ActionsRoute+"?errMsg=Action está vinculada e não foi removida.", 301)
			}
			log.Println("DELETE Action_Status: Id: " + actionId)
		}
		sqlStatement := "UPDATE virtus.actions SET name=?, id_origin_status=?, id_destination_status=?, other_than=?, description=? WHERE id_action=?"
		updtForm, err := Db.Prepare(sqlStatement)
		if err != nil {
			log.Println(err.Error())
		}
		_, err = updtForm.Exec(name, originStatus[0], destinationStatus[0], otherThan, description, actionId)
		if err != nil {
			log.Println(err.Error())
		}
		log.Println("UPDATE: Id: " + actionId + " | Name: " + name)
		sqlStatement = "INSERT INTO virtus.actions_status(id_action,id_origin_status,id_destination_status) " +
			" SELECT " + actionId + "," + originStatus[0] + "," + destinationStatus[0] +
			" WHERE NOT EXISTS " +
			" (SELECT 1 FROM virtus.actions_status WHERE id_origin_status = " + originStatus[0] + " AND id_destination_status = " + destinationStatus[0] + " ) "
		log.Println(sqlStatement)
		Db.QueryRow(sqlStatement, actionId, originStatus[0], destinationStatus[0], originStatus[0], destinationStatus[0])
		http.Redirect(w, r, route.ActionsRoute+"?msg=Ação atualizada com sucesso.", 301)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func containsRole(roles []mdl.Role, roleCompared mdl.Role) bool {
	for n := range roles {
		if roles[n].Id == roleCompared.Id {
			return true
		}
	}
	return false
}

func removeRole(roles []mdl.Role, roleToBeRemoved mdl.Role) []mdl.Role {
	var newRoles []mdl.Role
	for i := range roles {
		if roles[i].Id != roleToBeRemoved.Id {
			newRoles = append(newRoles, roles[i])
		}
	}
	return newRoles
}

func DeleteActionHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Delete Action")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		id := r.FormValue("Id")
		sqlStatement := "DELETE FROM virtus.actions_status WHERE id_action=?"
		deleteForm, _ := Db.Prepare(sqlStatement)
		_, err := deleteForm.Exec(id)
		if err != nil && strings.Contains(err.Error(), "violates foreign key") {
			http.Redirect(w, r, route.ActionsRoute+"?errMsg=A Ação está vinculada e não foi removida.", 301)
		} else {
			http.Redirect(w, r, route.ActionsRoute+"?msg=Ação removida com sucesso.", 301)
		}
		sqlStatement = "DELETE FROM virtus.actions WHERE id_action=?"
		deleteForm, _ = Db.Prepare(sqlStatement)
		deleteForm.Exec(id)
		if err != nil && strings.Contains(err.Error(), "violates foreign key") {
			http.Redirect(w, r, route.ActionsRoute+"?errMsg=A Ação está vinculada e não foi removida.", 301)
		} else {
			http.Redirect(w, r, route.ActionsRoute+"?msg=Ação removida com sucesso.", 301)
		}
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func ListActionsHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("List Actions")
	currentUser := GetUserInCookie(w, r)
	if sec.IsAuthenticated(w, r) && HasPermission(currentUser, "listActions") {
		errMsg := r.FormValue("errMsg")
		msg := r.FormValue("msg")
		sql := " SELECT " +
			" a.id_action, " +
			" a.name, " +
			" a.description, " +
			" a.id_origin_status, " +
			" b.name as origin_name, " +
			" a.id_destination_status, " +
			" c.name as destination_name, " +
			" a.other_than, " +
			" a.id_author, " +
			" d.name, " +
			" format(a.created_at,'dd/MM/yyyy HH:mm:ss') as created_at, " +
			" coalesce(e.name,'') as cstatus, " +
			" a.id_status, " +
			" a.id_versao_origem " +
			" FROM virtus.actions a " +
			" LEFT JOIN virtus.status b ON a.id_origin_status = b.id_status " +
			" LEFT JOIN virtus.status c ON a.id_destination_status = c.id_status " +
			" LEFT JOIN virtus.users d ON a.id_author = d.id_user " +
			" LEFT JOIN virtus.status e ON a.id_status = c.id_status " +
			" ORDER BY a.id_action asc "
		log.Println("List Action -> SQL: " + sql)
		rows, _ := Db.Query(sql)
		defer rows.Close()
		var actions []mdl.Action
		var action mdl.Action
		var i = 1
		for rows.Next() {
			rows.Scan(
				&action.Id,
				&action.Name,
				&action.Description,
				&action.OriginId,
				&action.Origin,
				&action.DestinationId,
				&action.Destination,
				&action.OtherThan,
				&action.AuthorId,
				&action.AuthorName,
				&action.C_CreatedAt,
				&action.CStatus,
				&action.StatusId,
				&action.IdVersaoOrigem)
			action.Order = i
			i++
			actions = append(actions, action)
		}
		sql = "SELECT id_status, name, stereotype FROM virtus.status ORDER BY name asc"
		log.Println("List Action -> Query: " + sql)
		rows, _ = Db.Query(sql)
		defer rows.Close()
		var statuss []mdl.Status
		var status mdl.Status
		i = 1
		for rows.Next() {
			rows.Scan(&status.Id, &status.Name, &status.Stereotype)
			status.Order = i
			i++
			statuss = append(statuss, status)
		}
		var page mdl.PageActions
		if msg != "" {
			page.Msg = msg
		}
		if errMsg != "" {
			page.ErrMsg = errMsg
		}
		page.Statuss = statuss
		page.Actions = actions
		page.AppName = mdl.AppName
		page.Title = "Ação" + mdl.Ambiente
		page.LoggedUser = BuildLoggedUser(GetUserInCookie(w, r))
		var tmpl = template.Must(template.ParseGlob("tiles/actions/*"))
		tmpl.ParseGlob("tiles/*")
		tmpl.ExecuteTemplate(w, "Main-Actions", page)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func LoadRolesByActionId(w http.ResponseWriter, r *http.Request) {
	log.Println("Load Roles By Action Id")
	r.ParseForm()
	var actionId = r.FormValue("actionId")
	log.Println("actionId: " + actionId)
	roles := ListPerfisByActionIdHandler(actionId)
	jsonRoles, _ := json.Marshal(roles)
	w.Write([]byte(jsonRoles))
	log.Println("JSON")
}

func LoadAllowedActions(w http.ResponseWriter, r *http.Request) {
	log.Println("Load Allowed Actions")
	r.ParseForm()
	savedUser := GetUserInCookie(w, r)
	roleId := savedUser.Role
	var statusId = r.FormValue("statusId")
	var entityType = r.FormValue("entityType")
	log.Println("entityType: " + entityType)
	log.Println("statusId: " + statusId)
	sql := " select id_action, name from virtus.actions where " +
		" (other_than = 0 and id_origin_status = ? " +
		" and id_action in ( select a.id_action FROM virtus.activities a, virtus.activities_roles b " +
		" where a.id_workflow = ( select id_workflow from virtus.workflows where entity_type = ? and end_at is null) " +
		" and a.id_activity = b.id_activity and b.id_role = ? ) ) " +
		" or " +
		" (other_than = 1 and id_origin_status != ? " +
		" and id_action in ( select a.id_action FROM virtus.activities a, virtus.activities_roles b " +
		" where a.id_workflow = ( select id_workflow from virtus.workflows where entity_type = ? and end_at is null) ) ) " +
		" order by other_than asc "
	log.Println("Query: " + sql)
	rows, _ := Db.Query(sql, statusId, entityType, roleId, statusId, entityType)
	defer rows.Close()
	var actions []mdl.Action
	var action mdl.Action
	for rows.Next() {
		rows.Scan(&action.Id, &action.Name)
		actions = append(actions, action)
		log.Println(actions)
	}
	jsonActions, _ := json.Marshal(actions)
	w.Write([]byte(jsonActions))
	log.Println("JSON Load Actions")
}
