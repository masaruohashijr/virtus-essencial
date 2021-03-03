package handlers

import (
	"log"
	"strconv"
	"strings"
	mdl "virtus-essencial/models"
)

// AJAX (XML Assincrono)
func ListActivitiesHandler(idWF string) []mdl.Activity {
	log.Println("List Activities By WF Id")
	sql := "SELECT a.id_activity, " +
		" a.id_workflow, " +
		" a.id_action, " +
		" b.name as action_name, " +
		" a.id_expiration_action, " +
		" c.name as expiration_action_name, " +
		" coalesce(expiration_time_days,'0'), " +
		" coalesce(format(a.start_at,'dd/MM/yyyy HH:mm:ss'),'') as c_start_at, " +
		" coalesce(format(a.end_at,'dd/MM/yyyy HH:mm:ss'),'') as c_end_at " +
		" FROM virtus.activities a left outer join virtus.actions b " +
		" on a.id_action = b.id_action left outer join virtus.actions c on a.id_expiration_action = c.id_action " +
		" WHERE a.id_workflow = ?"
	log.Println(sql)
	rows, _ := Db.Query(sql, idWF)
	defer rows.Close()
	var activities []mdl.Activity
	var activity mdl.Activity
	for rows.Next() {
		rows.Scan(&activity.Id,
			&activity.WorkflowId,
			&activity.ActionId,
			&activity.ActionName,
			&activity.ExpirationActionId,
			&activity.ExpirationActionName,
			&activity.ExpirationTimeDays,
			&activity.CStartAt,
			&activity.CEndAt)
		activity = assembleRoles(activity)
		activity = assembleFeatures(activity)
		activity = translateDates(activity)
		activities = append(activities, activity)
		log.Println(activities)
	}
	return activities
}

func translateDates(activity mdl.Activity) mdl.Activity {
	// 0001-01-01 00:00:00
	if strings.Contains(activity.CStartAt, "0001") {
		activity.CStartAt = ""
	}
	if strings.Contains(activity.CEndAt, "0001") {
		activity.CEndAt = ""
	}
	return activity
}

func assembleFeatures(activity mdl.Activity) mdl.Activity {
	log.Println("List Features By Activity Id")
	sql := "SELECT a.id_feature, b.name " +
		" FROM virtus.features_activities a" +
		" LEFT OUTER JOIN virtus.features b ON a.id_feature = b.id_feature WHERE a.id_activity = ?"
	log.Println(sql + string(activity.Id))
	rows, _ := Db.Query(sql, activity.Id)
	defer rows.Close()
	featureId := 0
	featureName := "\""
	c_features := ""
	c_featureNames := ""
	for rows.Next() {
		rows.Scan(&featureId, &featureName)
		c_features += strconv.Itoa(featureId) + "."
		c_featureNames += featureName + "."
	}
	if len(c_features) > 0 {
		c_features = c_features[0 : len(c_features)-1]
		c_featureNames = c_featureNames[0 : len(c_featureNames)-1]
	}
	activity.CFeatures = c_features
	activity.CFeatureNames = c_featureNames
	return activity
}

func assembleRoles(activity mdl.Activity) mdl.Activity {
	log.Println("List Roles By Activity Id")
	sql := "SELECT a.id_role, b.name " +
		" FROM virtus.activities_roles a" +
		" LEFT OUTER JOIN virtus.roles b ON a.id_role = b.id_role WHERE a.id_activity = ?"
	log.Println(sql + string(activity.Id))
	rows, _ := Db.Query(sql, activity.Id)
	defer rows.Close()
	roleId := 0
	roleName := "\""
	c_roles := ""
	c_roleNames := ""
	for rows.Next() {
		rows.Scan(&roleId, &roleName)
		c_roles += strconv.Itoa(roleId) + "."
		c_roleNames += roleName + "."
	}
	if len(c_roles) > 0 {
		c_roles = c_roles[0 : len(c_roles)-1]
		c_roleNames = c_roleNames[0 : len(c_roleNames)-1]
	}
	activity.CRoles = c_roles
	activity.CRoleNames = c_roleNames
	return activity
}

func DeleteActivitiesByWorkflowIdHandler(wId string) {
	sqlStatement := "DELETE FROM virtus.activities_roles WHERE id_activity IN ( SELECT id_actitviy FROM virtus.activities WHERE id_workflow = ? )"
	deleteForm, _ := Db.Prepare(sqlStatement)
	deleteForm.Exec(wId)
	log.Println("DELETE Activities_Roles in Workflow Id: " + wId)
	sqlStatement = "DELETE FROM virtus.activities WHERE id_workflow=?"
	deleteForm, _ = Db.Prepare(sqlStatement)
	deleteForm.Exec(wId)
	log.Println("DELETE Activities in Workflow Id: " + wId)
}

func DeleteActivitiesHandler(diffDB []mdl.Activity) {
	sqlStatement := "DELETE FROM virtus.activities_roles WHERE id_activity=?"
	deleteForm, _ := Db.Prepare(sqlStatement)
	for n := range diffDB {
		deleteForm.Exec(strconv.FormatInt(int64(diffDB[n].Id), 10))
		log.Println("DELETE: Activity Role Id: " + strconv.FormatInt(int64(diffDB[n].Id), 10))
	}
	sqlStatement = "DELETE FROM virtus.activities WHERE id_activity=?"
	deleteForm, _ = Db.Prepare(sqlStatement)
	for n := range diffDB {
		deleteForm.Exec(strconv.FormatInt(int64(diffDB[n].Id), 10))
		log.Println("DELETE: Activity Id: " + strconv.FormatInt(int64(diffDB[n].Id), 10))
	}
}

// REVISAR
func UpdateActivitiesHandler(actsPage []mdl.Activity, actsDB []mdl.Activity) {
	for i := range actsPage {
		id := actsPage[i].Id
		for j := range actsDB {
			if actsDB[j].Id == id {
				fieldsChanged := hasSomeActFieldChanged(actsPage[i], actsDB[j]) //DONE
				if fieldsChanged {
					updateActivityHandler(actsPage[i], actsDB[j]) // TODO
				}
				break
			}
		}
	}
}

func hasSomeActFieldChanged(actPage mdl.Activity, actDB mdl.Activity) bool {
	if actPage.ActionId != actDB.ActionId {
		return true
	} else if actPage.CEndAt != actDB.CEndAt {
		return true
	} else if actPage.CStartAt != actDB.CStartAt {
		return true
	} else if actPage.ExpirationActionId != actDB.ExpirationActionId {
		return true
	} else if actPage.ExpirationTimeDays != actDB.ExpirationTimeDays {
		return true
	} else if actPage.CRoles != actDB.CRoles {
		return true
	} else if actPage.CFeatures != actDB.CFeatures {
		return true
	} else {
		return false
	}
}

func updateActivityHandler(activityPage mdl.Activity, activityDB mdl.Activity) {
	sqlStatement := "UPDATE virtus.Activities SET " +
		" id_action=?, " +
		" id_expiration_action=?, " +
		" expiration_time_days=?, " +
		" start_at=?, " +
		" end_at=? " +
		" WHERE id_activity=?"
	updtForm, _ := Db.Prepare(sqlStatement)
	updtForm.Exec(activityPage.ActionId, activityPage.ExpirationActionId,
		activityPage.ExpirationTimeDays, activityPage.CStartAt, activityPage.CEndAt)
	if activityDB.CRoles != activityPage.CRoles {
		// 1. Apaga tudo da tabela activities_roles: Page{1.15} DB{1.14.15} => DB{1.15}
		sqlStatement := "DELETE FROM virtus.activities_roles WHERE id_activity=?"
		deleteForm, err := Db.Prepare(sqlStatement)
		if err != nil {
			log.Println(err.Error())
		}
		deleteForm.Exec(strconv.FormatInt(int64(activityPage.Id), 10))
		// 2. Insere tudo de novo
		roles := strings.Split(activityPage.CRoles, ".")
		for _, roleId := range roles {
			sqlStatement := "INSERT INTO " +
				"virtus.activities_roles(id_activity, id_role) " +
				"VALUES (?,?)"
			log.Println(sqlStatement + " - " + strconv.FormatInt(int64(activityPage.Id), 10) + " - " + roleId)
			Db.QueryRow(sqlStatement, strconv.FormatInt(int64(activityPage.Id), 10), roleId)
		}
	}
	if activityDB.CFeatures != activityPage.CFeatures {
		// 1. Apaga tudo da tabela activities_roles: Page{1.15} DB{1.14.15} => DB{1.15}
		sqlStatement := "DELETE FROM virtus.features_activities WHERE id_activity=?"
		deleteForm, err := Db.Prepare(sqlStatement)
		if err != nil {
			log.Println(err.Error())
		}
		deleteForm.Exec(strconv.FormatInt(int64(activityPage.Id), 10))
		// 2. Insere tudo de novo
		features := strings.Split(activityPage.CFeatures, ".")
		for _, featureId := range features {
			sqlStatement := "INSERT INTO " +
				"virtus.features_activities(id_activity, id_feature) " +
				"VALUES (?,?)"
			log.Println(sqlStatement + " - " + strconv.FormatInt(int64(activityPage.Id), 10) + " - " + featureId)
			Db.QueryRow(sqlStatement, strconv.FormatInt(int64(activityPage.Id), 10), featureId)
		}
	}
	log.Println("UPDATE: " + sqlStatement)
}
