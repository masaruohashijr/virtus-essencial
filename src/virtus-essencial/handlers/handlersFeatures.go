package handlers

import (
	"encoding/json"
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

func CreateFeatureHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Create Feature")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		currentUser := GetUserInCookie(w, r)
		r.ParseForm()
		name := r.FormValue("Name")
		code := r.FormValue("Code")
		description := r.FormValue("Description")
		sqlStatement := "INSERT INTO virtus.features(name, code, description, id_author, created_at) " +
			" OUTPUT INSERTED.id_feature VALUES (?, ?, ?, ?, GETDATE()) "
		id := 0
		err := Db.QueryRow(sqlStatement, name, code, description, currentUser.Id).Scan(&id)
		if err != nil {
			log.Println(err.Error())
		}
		if err != nil && strings.Contains(err.Error(), "duplicate key") {
			page := listFeatures(e.ErroChaveDuplicada, "")
			page.LoggedUser = BuildLoggedUser(GetUserInCookie(w, r))
			var tmpl = template.Must(template.ParseGlob("tiles/features/*"))
			tmpl.ParseGlob("tiles/*")
			tmpl.ExecuteTemplate(w, "Main-Features", page)
		}
		log.Println("INSERT: Id: " + strconv.Itoa(id) + " | Name: " + name + " | Code: " + code + " | Description: " + description)
		http.Redirect(w, r, route.FeaturesRoute+"?msg=Funcionalidade criada com sucesso.", 301)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func UpdateFeatureHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Update Feature")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		id := r.FormValue("Id")
		log.Println(id)
		name := r.FormValue("Name")
		log.Println(name)
		code := r.FormValue("Code")
		log.Println(code)
		description := r.FormValue("Description")
		sqlStatement := "UPDATE virtus.features SET name=?, code=?, description=? WHERE id_feature=?"
		log.Println(sqlStatement)
		updtForm, err := Db.Prepare(sqlStatement)
		if err != nil {
			log.Println(err.Error())
		}
		_, err = updtForm.Exec(name, code, description, id)
		if err != nil {
			log.Println(err.Error())
		}
		log.Println("UPDATE: Id: " + id + " | Name: " + name + " | Code: " + code + " | Description: " + description)
		http.Redirect(w, r, route.FeaturesRoute+"?msg=Funcionalidade atualizada com sucesso.", 301)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func DeleteFeatureHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Delete Feature")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		errMsg := "Funcionalidade vinculada a registro não pode ser removida."
		id := r.FormValue("Id")
		sqlStatement := "DELETE FROM virtus.features WHERE id_feature=?"
		deleteForm, _ := Db.Prepare(sqlStatement)
		_, err := deleteForm.Exec(id)
		if err != nil && strings.Contains(err.Error(), "violates foreign key") {
			http.Redirect(w, r, route.FeaturesRoute+"?errMsg="+errMsg, 301)
		} else {
			http.Redirect(w, r, route.FeaturesRoute+"?msg=Funcionalidade removida com sucesso.", 301)
		}
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func DeleteFeaturesByRoleHandler(roleId string) {
	sqlStatement := "DELETE FROM virtus.features_roles WHERE id_role=?"
	deleteForm, err := Db.Prepare(sqlStatement)
	if err != nil {
		log.Println(err.Error())
	}
	deleteForm.Exec(roleId)
	log.Println("DELETE features_roles in Role Id: " + roleId)
}

func DeleteFeaturesHandler(diffDB []mdl.Feature) {
	sqlStatement := "DELETE FROM virtus.features_roles WHERE id_feature=?"
	deleteForm, err := Db.Prepare(sqlStatement)
	if err != nil {
		log.Println(err.Error())
	}
	for n := range diffDB {
		deleteForm.Exec(strconv.FormatInt(int64(diffDB[n].Id), 10))
		log.Println("DELETE: Feature Id: " + strconv.FormatInt(int64(diffDB[n].Id), 10))
	}
}

func ListFeaturesHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("List Features")
	currentUser := GetUserInCookie(w, r)
	if sec.IsAuthenticated(w, r) && HasPermission(currentUser, "listFeatures") {
		errMsg := r.FormValue("errMsg")
		msg := r.FormValue("msg")
		page := listFeatures(errMsg, msg)
		page.LoggedUser = BuildLoggedUser(GetUserInCookie(w, r))
		var tmpl = template.Must(template.ParseGlob("tiles/features/*"))
		tmpl.ParseGlob("tiles/*")
		tmpl.ExecuteTemplate(w, "Main-Features", page)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func listFeatures(errorMsg string, msg string) mdl.PageFeatures {
	sql := "SELECT " +
		" a.id_feature, " +
		" coalesce(a.name,''), " +
		" coalesce(a.code,''), " +
		" coalesce(a.description,'') as dsc, " +
		" a.id_author, " +
		" coalesce(b.name,''), " +
		" format(a.created_at,'dd/MM/yyyy HH:mm:ss'), " +
		" coalesce(c.name,'') as cstatus, " +
		" a.id_status, " +
		" a.id_versao_origem " +
		" FROM virtus.features a LEFT JOIN virtus.users b " +
		" ON a.id_author = b.id_user " +
		" LEFT JOIN virtus.status c ON a.id_status = c.id_status " +
		" order by a.id_feature asc"
	log.Println(sql)
	rows, _ := Db.Query(sql)
	defer rows.Close()
	var features []mdl.Feature
	var feature mdl.Feature
	var i = 1
	for rows.Next() {
		rows.Scan(
			&feature.Id,
			&feature.Name,
			&feature.Code,
			&feature.Description,
			&feature.AuthorId,
			&feature.AuthorName,
			&feature.C_CreatedAt,
			&feature.CStatus,
			&feature.StatusId,
			&feature.IdVersaoOrigem)
		//log.Println(feature.AuthorName)
		feature.Order = i
		i++
		features = append(features, feature)
	}
	var page mdl.PageFeatures
	page.Features = features
	page.AppName = mdl.AppName
	page.Title = "Funcionalidades" + mdl.Ambiente
	if errorMsg != "" {
		page.ErrMsg = errorMsg
	}
	if msg != "" {
		page.Msg = msg
	}
	return page
}

// AJAX
func ListFeaturesByRoleIdHandler(roleId string) []mdl.Feature {
	log.Println("List Features By Role Id")
	sql := "SELECT id_feature" +
		" FROM virtus.features_roles WHERE id_role= ?"
	log.Println(sql)
	rows, _ := Db.Query(sql, roleId)
	defer rows.Close()
	var features []mdl.Feature
	var feature mdl.Feature
	for rows.Next() {
		rows.Scan(&feature.Id)
		features = append(features, feature)
	}
	return features
}

func LoadAvailableFeatures(w http.ResponseWriter, r *http.Request) {
	log.Println("Load Load Available Features")
	r.ParseForm()
	savedUser := GetUserInCookie(w, r)
	var statusId = r.FormValue("statusId")
	var entityType = r.FormValue("entityType")
	log.Println("entityType: " + entityType)
	log.Println("statusId: " + statusId)
	sql := " SELECT a.id_feature, a.name, a.code " +
		" FROM virtus.features a INNER JOIN virtus.features_activities b ON a.id_feature = b.id_feature " +
		" INNER JOIN virtus.activities c ON c.id_activity = b.id_activity " +
		" INNER JOIN virtus.actions d ON c.id_action = d.id_action " +
		" INNER JOIN virtus.workflows e ON c.id_workflow = e.id_workflow " +
		" WHERE e.end_at IS null " +
		" AND e.entity_type = ? " +
		" AND d.id_origin_status = ? " +
		" AND a.id_feature in ( SELECT id_feature FROM virtus.features_roles where id_role = ? ) "
	log.Println("Query Available Features: " + sql)
	rows, _ := Db.Query(sql, entityType, statusId, savedUser.Role)
	defer rows.Close()
	var features []mdl.Feature
	var feature mdl.Feature
	for rows.Next() {
		rows.Scan(&feature.Id, &feature.Name, &feature.Code)
		features = append(features, feature)
		log.Println(features)
	}
	jsonFeatures, _ := json.Marshal(features)
	w.Write([]byte(jsonFeatures))
	log.Println("JSON Load Features")
}
