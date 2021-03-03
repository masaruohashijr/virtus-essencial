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

func CreateRoleHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Create Role")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		currentUser := GetUserInCookie(w, r)
		r.ParseForm()
		name := r.FormValue("Name")
		description := r.FormValue("Description")
		features := r.Form["FeaturesForInsert"]
		sqlStatement := "INSERT INTO virtus.roles(name, description, id_author, created_at) " +
			" OUTPUT INSERTED.id_role " +
			" VALUES (?, ?, ?, GETDATE())"
		roleId := 0
		err := Db.QueryRow(sqlStatement, name, description, currentUser.Id).Scan(&roleId)
		if err != nil {
			log.Println(err.Error())
		}
		for _, featureId := range features {
			sqlStatement := "INSERT INTO virtus.features_roles(id_feature,id_role) " +
				" OUTPUT INSERTED.id_feature_role VALUES (?,?)"
			featureRoleId := 0
			err = Db.QueryRow(sqlStatement, featureId, roleId).Scan(&featureRoleId)
			if err != nil {
				log.Println(err.Error())
			}
		}
		log.Println("INSERT: Id: " + strconv.Itoa(roleId) + " | Name: " + name)
		http.Redirect(w, r, route.RolesRoute+"?msg=Perfil criado com sucesso.", 301)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func UpdateRoleHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Update Role")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		roleId := r.FormValue("Id")
		name := r.FormValue("Name")
		description := r.FormValue("Description")
		sqlStatement := "UPDATE virtus.roles SET name=?, description=? WHERE id_role=?"
		updtForm, err := Db.Prepare(sqlStatement)
		if err != nil {
			log.Println(err.Error())
		}
		updtForm.Exec(name, description, roleId)
		log.Println("UPDATE: Id: " + roleId + " | Name: " + name + " | Description: " + description)

		var featuresDB = ListFeaturesByRoleIdHandler(roleId)
		var featuresPage []mdl.Feature
		var featurePage mdl.Feature
		for _, featureId := range r.Form["FeaturesForUpdate"] {
			featurePage.Id, _ = strconv.ParseInt(featureId, 10, 64)
			featuresPage = append(featuresPage, featurePage)
		}
		if len(featuresPage) < len(featuresDB) {
			log.Println("Quantidade de Features da Página: " + strconv.Itoa(len(featuresPage)))
			if len(featuresPage) == 0 {
				DeleteFeaturesByRoleHandler(roleId) //DONE
			} else {
				var diffDB []mdl.Feature = featuresDB
				for n := range featuresPage {
					if containsFeature(diffDB, featuresPage[n]) {
						diffDB = removeFeature(diffDB, featuresPage[n])
					}
				}
				DeleteFeaturesHandler(diffDB) //DONE
			}
		} else {
			var diffPage []mdl.Feature = featuresPage
			for n := range featuresDB {
				if containsFeature(diffPage, featuresDB[n]) {
					diffPage = removeFeature(diffPage, featuresDB[n])
				}
			}
			var feature mdl.Feature
			for i := range diffPage {
				feature = diffPage[i]
				log.Println("Role Id: " + roleId)
				sqlStatement := "INSERT INTO virtus.features_roles(id_role, id_feature) VALUES (?,?)"
				log.Println(sqlStatement)
				Db.QueryRow(sqlStatement, roleId, feature.Id)
			}
		}
		http.Redirect(w, r, route.RolesRoute+"?msg=Perfil atualizado com sucesso.", 301)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func containsFeature(features []mdl.Feature, featureCompared mdl.Feature) bool {
	for n := range features {
		if features[n].Id == featureCompared.Id {
			return true
		}
	}
	return false
}

func removeFeature(features []mdl.Feature, featureToBeRemoved mdl.Feature) []mdl.Feature {
	var newFeatures []mdl.Feature
	for i := range features {
		if features[i].Id != featureToBeRemoved.Id {
			newFeatures = append(newFeatures, features[i])
		}
	}
	return newFeatures
}

func DeleteRoleHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Delete Perfil")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		id := r.FormValue("Id")
		log.Println("id: " + id)
		errMsg := "Perfil vinculado a registro não pode ser removido."
		sqlStatement := "DELETE FROM virtus.features_roles WHERE id_role=?"
		deleteForm, _ := Db.Prepare(sqlStatement)
		deleteForm.Exec(id)
		sqlStatement = "DELETE FROM virtus.roles WHERE id_role=?"
		deleteForm, _ = Db.Prepare(sqlStatement)
		_, err := deleteForm.Exec(id)
		if err != nil && strings.Contains(err.Error(), "violates foreign key") {
			http.Redirect(w, r, route.RolesRoute+"?errMsg="+errMsg, 301)
		} else {
			http.Redirect(w, r, route.RolesRoute+"?msg=Perfil removido com sucesso.", 301)
		}
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func ListPerfisHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("List Perfis")
	currentUser := GetUserInCookie(w, r)
	if sec.IsAuthenticated(w, r) && HasPermission(currentUser, "listRoles") {
		errMsg := r.FormValue("errMsg")
		msg := r.FormValue("msg")
		sql := "SELECT " +
			" a.id_role, " +
			" a.name, " +
			" a.description, " +
			" a.id_author, " +
			" b.name, " +
			" format(a.created_at,'dd/MM/yyyy HH:mm:ss'), " +
			" coalesce(c.name,'') as cstatus, " +
			" a.id_status, " +
			" a.id_versao_origem " +
			" FROM virtus.roles a LEFT JOIN virtus.users b " +
			" ON a.id_author = b.id_user " +
			" LEFT JOIN virtus.status c ON a.id_status = c.id_status " +
			" order by id_role asc"
		log.Println("sql: " + sql)
		rows, _ := Db.Query(sql)
		defer rows.Close()
		var roles []mdl.Role
		var role mdl.Role
		var i = 1
		for rows.Next() {
			rows.Scan(
				&role.Id,
				&role.Name,
				&role.Description,
				&role.AuthorId,
				&role.AuthorName,
				&role.C_CreatedAt,
				&role.CStatus,
				&role.StatusId,
				&role.IdVersaoOrigem)
			role.Order = i
			i++
			roles = append(roles, role)
		}
		rows, _ = Db.Query("SELECT id_feature, name FROM virtus.features order by name asc")
		defer rows.Close()
		var features []mdl.Feature
		var feature mdl.Feature
		i = 1
		for rows.Next() {
			rows.Scan(&feature.Id, &feature.Name)
			feature.Order = i
			i++
			features = append(features, feature)
		}
		var page mdl.PageRoles
		if errMsg != "" {
			page.ErrMsg = errMsg
		}
		if msg != "" {
			page.Msg = msg
		}
		page.Roles = roles
		page.Features = features
		page.AppName = mdl.AppName
		page.Title = "Perfis" + mdl.Ambiente
		page.LoggedUser = BuildLoggedUser(GetUserInCookie(w, r))
		var tmpl = template.Must(template.ParseGlob("tiles/perfis/*"))
		tmpl.ParseGlob("tiles/*")
		tmpl.ExecuteTemplate(w, "Main-Perfis", page)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func LoadFeaturesByRoleId(w http.ResponseWriter, r *http.Request) {
	log.Println("Load Features By Role Id")
	r.ParseForm()
	var roleId = r.FormValue("roleId")
	log.Println("roleId: " + roleId)
	features := ListFeaturesByRoleIdHandler(roleId)
	jsonFeatures, _ := json.Marshal(features)
	w.Write([]byte(jsonFeatures))
	log.Println("JSON")
}

// AJAX
func ListPerfisByActionIdHandler(actionId string) []mdl.Role {
	log.Println("List Perfis By Action Id")
	sql := "SELECT id_role" +
		" FROM virtus.actions_roles WHERE id_action= ?"
	log.Println(sql)
	rows, _ := Db.Query(sql, actionId)
	defer rows.Close()
	var roles []mdl.Role
	var role mdl.Role
	for rows.Next() {
		rows.Scan(&role.Id)
		roles = append(roles, role)
	}
	return roles
}

func DeletePerfisByActionHandler(actionId string) {
	sqlStatement := "DELETE FROM virtus.actions_roles WHERE id_action=?"
	deleteForm, err := Db.Prepare(sqlStatement)
	if err != nil {
		log.Println(err.Error())
	}
	deleteForm.Exec(actionId)
	log.Println("DELETE actions_roles in Action Id: " + actionId)
}

func DeletePerfisHandler(diffDB []mdl.Role) {
	sqlStatement := "DELETE FROM virtus.actions_roles WHERE id_role=?"
	deleteForm, err := Db.Prepare(sqlStatement)
	if err != nil {
		log.Println(err.Error())
	}
	for n := range diffDB {
		deleteForm.Exec(strconv.FormatInt(int64(diffDB[n].Id), 10))
		log.Println("DELETE: Role Id: " + strconv.FormatInt(int64(diffDB[n].Id), 10))
	}
}
