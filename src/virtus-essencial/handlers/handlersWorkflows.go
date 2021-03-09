package handlers

import (
	"encoding/json"
	"html/template"
	"log"
	"net/http"
	"strconv"
	"strings"
	"time"
	mdl "virtus-essencial/models"
	route "virtus-essencial/routes"
	sec "virtus-essencial/security"
)

func GetStartStatus(entityType string) int {
	log.Println("Get <<Start>> Status")
	query := "SELECT TOP 1 id_status FROM virtus.status WHERE id_status in (SELECT id_origin_status FROM virtus.actions_status WHERE id_action in " +
		" ( SELECT id_action FROM virtus.activities WHERE id_workflow in (SELECT id_workflow FROM virtus.workflows WHERE " +
		" entity_type = ? AND end_at IS NULL))) " +
		" AND stereotype = 'Start' "
	log.Println("List WF -> Query: " + query)
	log.Println("entityType: " + entityType)
	rows, _ := Db.Query(query, entityType)
	defer rows.Close()
	startStatusId := 0
	if rows.Next() {
		rows.Scan(&startStatusId)
	}
	log.Println("startStatusId: " + strconv.Itoa(startStatusId))
	//	log.Println("Saindo!")
	return startStatusId
}

func GetEndStatus(entityType string) int {
	log.Println("Get <<End>> Status")
	query := "SELECT TOP 1 id_status FROM virtus.status where id_status in (select id_destination_status from virtus.actions_status where id_action in " +
		" ( select id_action FROM virtus.activities where id_workflow in (select id_workflow from virtus.workflows where " +
		" entity_type = ? and end_at is null))) " +
		" and stereotype = 'End' "
	log.Println("List WF -> Query: " + query)
	log.Println("entityType: " + entityType)
	rows, _ := Db.Query(query, entityType)
	defer rows.Close()
	endStatusId := 0
	if rows.Next() {
		rows.Scan(&endStatusId)
	}
	log.Println("endStatusId: " + strconv.Itoa(endStatusId))
	//	log.Println("Saindo!")
	return endStatusId
}

func CreateWorkflowHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Create Workflow")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		currentUser := GetUserInCookie(w, r)
		name := r.FormValue("Name")
		description := r.FormValue("Description")
		entityType := r.FormValue("EntityTypeForInsert")
		sqlStatement := "UPDATE virtus.workflows SET end_at = GETDATE() WHERE entity_type = ?"
		updtForm, err := Db.Prepare(sqlStatement)
		if err != nil {
			log.Println(err.Error())
		}
		updtForm.Exec(entityType)
		sqlStatement = "INSERT INTO " +
			" virtus.workflows(name, entity_type, start_at, description, id_author, created_at) " +
			" OUTPUT INSERTED.id_workflow " +
			" VALUES (?,?,GETDATE(),?,?,GETDATE()) "
		wId := 0
		err = Db.QueryRow(
			sqlStatement,
			name,
			entityType,
			description,
			currentUser.Id).Scan(&wId)
		if err != nil {
			log.Println(err.Error())
		}
		log.Println("INSERT: Id: " + strconv.Itoa(wId) + " | Name: " + name + " | Entitity: " + entityType)
		for key, value := range r.Form {
			if strings.HasPrefix(key, "activity") {
				array := strings.Split(value[0], "#")
				log.Println(value[0])
				activityId := 0
				actionId := strings.Split(array[3], ":")[1]
				startAt, _ := time.Parse("yyyy-mm-dd", strings.Split(array[8], ":")[1])
				endAt, _ := time.Parse("yyyy-mm-dd", strings.Split(array[9], ":")[1])
				expTime := strings.Split(array[7], ":")[1]
				if expTime == "" {
					expTime = "0"
				}
				expActionId := strings.Split(array[5], ":")[1]
				strRoles := strings.Split(array[10], ":")[1]
				log.Println("actionId: " + actionId)
				sqlStatement := "INSERT INTO " +
					" virtus.activities(id_workflow, id_action, start_at, end_at, expiration_time_days, id_expiration_action) " +
					" OUTPUT INSERTED.id_activity VALUES (?,?,?,?,?,?) "
				log.Println(sqlStatement)
				log.Println("wId: " + strconv.Itoa(wId) + " | Action: " + actionId + " | ExpDays: " + expTime + " | ExpAction: " + expActionId)
				if expActionId == "" {
					err = Db.QueryRow(sqlStatement, wId, actionId, startAt, endAt, expTime, nil).Scan(&activityId)
				} else {
					err = Db.QueryRow(sqlStatement, wId, actionId, startAt, endAt, expTime, expActionId).Scan(&activityId)
				}
				if err != nil {
					log.Println("ERRO ACTIVITIES: " + err.Error())
				}
				if len(strRoles) > 0 {
					log.Println("Roles: " + strRoles)
					roles := strings.Split(strRoles, ".")
					for _, roleId := range roles {
						sqlStatement := "INSERT INTO " +
							" virtus.activities_roles(id_activity, id_role) " +
							" VALUES (?,?)"
						log.Println(sqlStatement + " - " + strconv.Itoa(activityId) + " - " + roleId)
						Db.QueryRow(sqlStatement, activityId, roleId)
					}
				}
			}
		}
		http.Redirect(w, r, route.WorkflowsRoute+"?msg=Workflow criado com sucesso.", 301)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func UpdateWorkflowHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Update Workflow")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		// Workflow
		wId := r.FormValue("Id")
		log.Println("Workflow Id: " + wId)
		name := r.FormValue("NameForUpdate")
		description := r.FormValue("DescriptionForUpdate")
		entity := r.FormValue("EntityTypeForUpdate")
		sqlStatement := "UPDATE virtus.workflows SET name=?, entity_type=?, description=? WHERE id_workflow=?"
		updtForm, err := Db.Prepare(sqlStatement)
		if err != nil {
			log.Println(err.Error())
		}
		updtForm.Exec(name, entity, description, wId)
		log.Println("UPDATE: Id: " + wId + " | Name: " + name + " | Entity: " + entity + " | Description: " + description)
		// Atividades
		var actsDB = ListActivitiesHandler(wId)
		var actsPage []mdl.Activity
		var actPage mdl.Activity
		for key, value := range r.Form {
			if strings.HasPrefix(key, "activity") {
				log.Println(value[0])
				array := strings.Split(value[0], "#")
				id := strings.Split(array[1], ":")[1]
				log.Println("Id -------- " + id)
				actPage.Id, _ = strconv.ParseInt(id, 10, 64)
				wId := strings.Split(array[2], ":")[1]
				log.Println("wId -------- " + wId)
				actPage.WorkflowId, _ = strconv.ParseInt(wId, 10, 64)
				actionId := strings.Split(array[3], ":")[1]
				log.Println("actionId -------- " + actionId)
				actPage.ActionId, _ = strconv.ParseInt(actionId, 10, 64)
				actionName := strings.Split(array[4], ":")[1]
				log.Println("actionName -------- " + actionName)
				actPage.ActionName = actionName
				//				log.Println("Id -------- " + id)
				expActionId := strings.Split(array[5], ":")[1]
				//				log.Println("Id -------- " + id)
				actPage.ExpirationActionId, _ = strconv.ParseInt(expActionId, 10, 64)
				//				log.Println("Id -------- " + id)
				expActionName := strings.Split(array[6], ":")[1]
				//				log.Println("Id -------- " + id)
				actPage.ExpirationActionName = expActionName
				//				log.Println("Id -------- " + id)
				expTime := strings.Split(array[7], ":")[1]
				//				log.Println("Id -------- " + id)
				actPage.ExpirationTimeDays, _ = strconv.Atoi(expTime)
				//				log.Println("Id -------- " + id)
				startAt := strings.Split(array[8], ":")[1]
				//				log.Println("Id -------- " + id)
				actPage.CStartAt = startAt
				//				log.Println("Id -------- " + id)
				endAt := strings.Split(array[9], ":")[1]
				//				log.Println("Id -------- " + id)
				actPage.CEndAt = endAt
				roles := strings.Split(array[10], ":")[1]
				actPage.CRoles = roles
				log.Println("Roles -------- " + roles)
				features := strings.Split(array[13], ":")[1]
				actPage.CFeatures = features
				log.Println("Features -------- " + features)
				actsPage = append(actsPage, actPage)
			}
		}
		if len(actsPage) < len(actsDB) {
			log.Println("Quantidade de Activities da Página: " + strconv.Itoa(len(actsPage)))
			if len(actsPage) == 0 {
				DeleteActivitiesByWorkflowIdHandler(wId) //DONE
			} else {
				var diffDB []mdl.Activity = actsDB
				for n := range actsPage {
					if containsAct(diffDB, actsPage[n]) {
						diffDB = removeAct(diffDB, actsPage[n])
					}
				}
				DeleteActivitiesHandler(diffDB) //DONE
			}
		} else {
			var diffPage []mdl.Activity = actsPage
			for n := range actsPage {
				log.Println("Page Action: " + actsPage[n].ActionName)
			}
			for n := range actsDB {
				if containsAct(diffPage, actsDB[n]) {
					log.Println(n)
					log.Println("actsDB[n]: " + actsDB[n].ActionName)
					diffPage = removeAct(diffPage, actsDB[n])
				}
			}
			log.Println("Tamamho: " + strconv.Itoa(len(diffPage)))
			for n := range diffPage {
				log.Println("Action Name Incluida agora " + diffPage[n].ActionName)
			}

			var act mdl.Activity
			for i := range diffPage {
				act = diffPage[i]
				log.Println("Workflow Id: " + strconv.FormatInt(act.WorkflowId, 10))
				sqlStatement := "INSERT INTO " +
					"virtus.activities(id_workflow, id_action, start_at, end_at, expiration_time_days, id_expiration_action) " +
					"OUTPUT INSERTED.id_activity VALUES (" + strconv.FormatInt(act.WorkflowId, 10) +
					"," + strconv.FormatInt(act.ActionId, 10) +
					",'" + act.CStartAt +
					"','" + act.CEndAt +
					"'," + strconv.Itoa(act.ExpirationTimeDays) +
					"," + strconv.FormatInt(act.ExpirationActionId, 10) +
					") "
				log.Println(sqlStatement)
				var activityId int
				log.Println("wId: " + wId + " | Action: " + strconv.FormatInt(act.ActionId, 10) + " | EndAt: " + act.CEndAt + " | StartAt: " + act.CStartAt + " | ExpDays: " + strconv.Itoa(act.ExpirationTimeDays) + " | ExpAction: " + strconv.FormatInt(act.ExpirationActionId, 10))
				err := Db.QueryRow(sqlStatement).Scan(&activityId)
				if err != nil {
					log.Println(err.Error())
				}
				log.Println("Papel: " + act.CRoles)
				strRoles := strings.Split(act.CRoles, ".")
				for _, roleId := range strRoles {
					sqlStatement := "INSERT INTO " +
						"virtus.activities_roles(id_activity, id_role) " +
						"VALUES (?,?)"
					log.Println(sqlStatement + " - " + strconv.Itoa(activityId) + " - " + roleId)
					Db.QueryRow(sqlStatement, activityId, roleId)
				}
			}
		}
		UpdateActivitiesHandler(actsPage, actsDB) // TODO
		http.Redirect(w, r, route.WorkflowsRoute+"?msg=Workflow atualizado com sucesso.", 301)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func containsAct(acts []mdl.Activity, actCompared mdl.Activity) bool {
	for n := range acts {
		if acts[n].Id == actCompared.Id {
			return true
		}
	}
	return false
}

func removeAct(acts []mdl.Activity, actToBeRemoved mdl.Activity) []mdl.Activity {
	var newActs []mdl.Activity
	for i := range acts {
		if acts[i].Id != actToBeRemoved.Id {
			newActs = append(newActs, acts[i])
		}
	}
	return newActs
}

func DeleteWorkflowHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Delete Workflow")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		id := r.FormValue("Id")
		errMsg := "Workflow vinculado a registro não pode ser removido."
		sqlStatement := "DELETE FROM virtus.workflows WHERE id_workflow=?"
		deleteForm, _ := Db.Prepare(sqlStatement)
		_, err := deleteForm.Exec(id)
		if err != nil && strings.Contains(err.Error(), "violates foreign key") {
			http.Redirect(w, r, route.WorkflowsRoute+"?errMsg="+errMsg, 301)
		} else {
			http.Redirect(w, r, route.WorkflowsRoute+"?msg=Workflow removido com sucesso.", 301)
		}
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func ListWorkflowsHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("List Workflows")
	currentUser := GetUserInCookie(w, r)
	if sec.IsAuthenticated(w, r) && HasPermission(currentUser, "listWorkflows") {
		msg := r.FormValue("msg")
		errMsg := r.FormValue("errMsg")
		sql := "SELECT " +
			" a.id_workflow, " +
			" a.name, " +
			" a.entity_type, " +
			" coalesce(format(a.start_at,'dd/MM/yyyy'),'') as c_start_at, " +
			" coalesce(format(a.end_at,'dd/MM/yyyy'),'') as c_end_at, " +
			" coalesce(a.description,'') as dsc, " +
			" a.id_author, " +
			" b.name, " +
			" format(a.created_at,'dd/MM/yyyy HH:mm:ss'), " +
			" coalesce(c.name,'') as cstatus, " +
			" a.id_status, " +
			" a.id_versao_origem " +
			" FROM " +
			" virtus.workflows a " +
			" LEFT JOIN virtus.users b ON a.id_author = b.id_user " +
			" LEFT JOIN virtus.status c ON a.id_status = c.id_status " +
			" ORDER BY a.id_workflow ASC"

		log.Println("List WF -> SQL: " + sql)
		rows, _ := Db.Query(sql)
		defer rows.Close()
		var workflows []mdl.Workflow
		var workflow mdl.Workflow
		var i = 1
		for rows.Next() {
			rows.Scan(
				&workflow.Id,
				&workflow.Name,
				&workflow.EntityType,
				&workflow.StartAt,
				&workflow.EndAt,
				&workflow.Description,
				&workflow.AuthorId,
				&workflow.AuthorName,
				&workflow.C_CreatedAt,
				&workflow.CStatus,
				&workflow.StatusId,
				&workflow.IdVersaoOrigem)
			workflow.Order = i
			i++
			workflows = append(workflows, workflow)
		}
		sql = " SELECT " +
			" a.id_action, " +
			" a.name, " +
			" a.id_origin_status, " +
			" b.name as origin_status, " +
			" a.id_destination_status, " +
			" c.name as destination_status, " +
			" a.other_than " +
			" FROM " +
			" virtus.actions a " +
			" LEFT JOIN virtus.status b ON a.id_origin_status = b.id_status " +
			" LEFT JOIN virtus.status c ON a.id_destination_status = c.id_status " +
			" ORDER BY a.id_action asc"
		log.Println("List WF -> sql: " + sql)
		rows, _ = Db.Query(sql)
		defer rows.Close()
		var actions []mdl.Action
		var action mdl.Action
		i = 1
		for rows.Next() {
			rows.Scan(
				&action.Id,
				&action.Name,
				&action.OriginId,
				&action.Origin,
				&action.DestinationId,
				&action.Destination,
				&action.OtherThan)
			action.Order = i
			i++
			actions = append(actions, action)
		}
		sql = "SELECT id_role, name FROM virtus.roles order by name asc"
		log.Println("List WF -> Query: " + sql)
		rows, _ = Db.Query(sql)
		defer rows.Close()
		var roles []mdl.Role
		var role mdl.Role
		i = 1
		for rows.Next() {
			rows.Scan(&role.Id, &role.Name)
			role.Order = i
			i++
			roles = append(roles, role)
		}

		sql = "SELECT id_feature, name " +
			" FROM virtus.features order by id_feature desc"
		log.Println(sql)
		rows, _ = Db.Query(sql)
		defer rows.Close()
		var features []mdl.Feature
		var feature mdl.Feature
		for rows.Next() {
			rows.Scan(&feature.Id, &feature.Name)
			features = append(features, feature)
		}
		var page mdl.PageWorkflows
		if msg != "" {
			page.Msg = msg
		}
		if errMsg != "" {
			page.ErrMsg = errMsg
		}
		page.Actions = actions
		page.Features = features
		page.Roles = roles
		page.Workflows = workflows
		page.AppName = mdl.AppName
		page.Title = "Workflows" + mdl.Ambiente
		page.LoggedUser = BuildLoggedUser(GetUserInCookie(w, r))
		var tmpl = template.Must(template.ParseGlob("tiles/workflows/*"))
		tmpl.ParseGlob("tiles/*")
		tmpl.ExecuteTemplate(w, "Main-Workflows", page)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func LoadActivitiesByWorkflowId(w http.ResponseWriter, r *http.Request) {
	log.Println("Load Activities By Workflow Id")
	r.ParseForm()
	var idWF = r.FormValue("idWF")
	log.Println("idWF: " + idWF)
	activities := ListActivitiesHandler(idWF)
	jsonActivities, _ := json.Marshal(activities)
	w.Write([]byte(jsonActivities))
	log.Println("JSON")
}
