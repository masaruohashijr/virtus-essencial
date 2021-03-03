package handlers

import (
	"log"
	"net/http"
	"strconv"
	mdl "virtus-essencial/models"
	route "virtus-essencial/routes"
	sec "virtus-essencial/security"
)

func CreatePlanoHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Create Plano")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		nome := r.FormValue("Nome")
		sqlStatement := "INSERT INTO virtus.planos(nome) OUTPUT INSERTED.id_plano VALUES (?)"
		id := 0
		err := Db.QueryRow(sqlStatement, nome).Scan(&id)
		log.Println(sqlStatement + " :: " + nome)
		if err != nil {
			log.Println(err.Error())
		}
		log.Println("INSERT: Id: " + strconv.Itoa(id) + " | Nome: " + nome)
		http.Redirect(w, r, route.PlanosRoute, 301)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func UpdatePlanoHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Update Plano")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		id := r.FormValue("Id")
		nome := r.FormValue("Nome")
		descricao := r.FormValue("Descricao")
		cnpb := r.FormValue("CNPB")
		recursoGarantidor := r.FormValue("RecursoGarantidor")
		modalidade := r.FormValue("Modalidade")
		sqlStatement := "UPDATE virtus.planos SET nome=?, descricao=?, cnpb=?, recurso_garantidor=?, modalidade=? WHERE id_plano=?"
		updtForm, err := Db.Prepare(sqlStatement)
		if err != nil {
			log.Println(err.Error())
		}
		updtForm.Exec(nome, descricao, cnpb, recursoGarantidor, modalidade, id)
		log.Println("UPDATE: Id: " + id + " | Nome: " + nome)
		http.Redirect(w, r, route.PlanosRoute, 301)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func UpdatePlanosHandler(planosPage []mdl.Plano, planosDB []mdl.Plano) {
	for i := range planosPage {
		id := planosPage[i].Id
		log.Println("id: " + strconv.FormatInt(id, 10))
		for j := range planosDB {
			log.Println("planosDB[j].Id: " + strconv.FormatInt(planosDB[j].Id, 10))
			if strconv.FormatInt(planosDB[j].Id, 10) == strconv.FormatInt(id, 10) {
				log.Println("Entrei")
				fieldsChanged := hasSomeFieldChangedPlano(planosPage[i], planosDB[j]) //DONE
				log.Println(fieldsChanged)
				if fieldsChanged {
					updatePlanoHandler(planosPage[i], planosDB[j])
				}
				planosDB = removePlano(planosDB, planosPage[i])
				break
			}
		}
	}
	DeletePlanosHandler(planosDB)
}

func hasSomeFieldChangedPlano(planoPage mdl.Plano, planoDB mdl.Plano) bool {
	if planoPage.Nome != planoDB.Nome {
		return true
	} else if planoPage.Descricao != planoDB.Descricao {
		return true
	} else if planoPage.CNPB != planoDB.CNPB {
		return true
	} else if planoPage.RecursoGarantidor != planoDB.RecursoGarantidor {
		return true
	} else if planoPage.Modalidade != planoDB.Modalidade {
		return true
	} else {
		return false
	}
}

func updatePlanoHandler(p mdl.Plano, planoDB mdl.Plano) {
	sqlStatement := "UPDATE virtus.planos SET " +
		"nome='" + p.Nome + "', descricao='" + p.Descricao + "', id_modalidade='" + p.Modalidade +
		"', recurso_garantidor=" +
		p.RecursoGarantidor +
		", cnpb='" + p.CNPB +
		"' WHERE id_plano=" + strconv.FormatInt(p.Id, 10)
	log.Println(sqlStatement)
	updtForm, _ := Db.Prepare(sqlStatement)
	_, err := updtForm.Exec()
	if err != nil {
		log.Println(err.Error())
	}
	log.Println("Statement: " + sqlStatement)
}

func DeletePlanoHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Delete Plano")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		id := r.FormValue("Id")
		sqlStatement := "DELETE FROM virtus.planos WHERE id_plano=?"
		deleteForm, err := Db.Prepare(sqlStatement)
		if err != nil {
			log.Println(err.Error())
		}
		deleteForm.Exec(id)
		log.Println("DELETE: Id: " + id)
		http.Redirect(w, r, route.PlanosRoute, 301)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

// AJAX
func ListPlanosByEntidadeId(entidadeId string) []mdl.Plano {
	log.Println("List Planos By Entidade Id")
	log.Println("entidadeId: " + entidadeId)
	sql := "SELECT " +
		" a.id_plano, " +
		" a.id_entidade, " +
		" coalesce(a.nome,'')," +
		" coalesce(a.descricao,''), " +
		" a.cnpb," +
		" CASE WHEN a.recurso_garantidor > 1000000 AND a.recurso_garantidor < 1000000000 THEN concat(format(a.recurso_garantidor/1000000,'N','pt-br'),' Milhões') WHEN a.recurso_garantidor > 1000000000 THEN concat(format(a.recurso_garantidor/1000000000,'N','pt-br'),' Bilhões') ELSE concat(format(a.recurso_garantidor/1000,'N','pt-br'),' Milhares') END," +
		" cast(a.recurso_garantidor as numeric), " +
		" a.id_modalidade," +
		" a.id_author, " +
		" coalesce(b.name,'') as author_name, " +
		" coalesce(format(a.criado_em,'dd/MM/yyyy'),'') as criado_em," +
		" a.id_status, " +
		" coalesce(c.name,'') as status_name " +
		" FROM virtus.planos a LEFT JOIN virtus.users b ON a.id_author = b.id_user " +
		" LEFT JOIN virtus.status c ON a.id_status = c.id_status " +
		" WHERE a.id_entidade = ? " +
		" AND left(cnpb,1) not in ('4','5') " +
		" ORDER BY a.recurso_garantidor DESC"
	log.Println(sql)
	rows, err := Db.Query(sql, entidadeId)
	log.Println(err)
	defer rows.Close()
	var planos []mdl.Plano
	var plano mdl.Plano
	var i = 1
	for rows.Next() {
		rows.Scan(
			&plano.Id,
			&plano.EntidadeId,
			&plano.Nome,
			&plano.Descricao,
			&plano.CNPB,
			&plano.C_RecursoGarantidor,
			&plano.RecursoGarantidor,
			&plano.Modalidade,
			&plano.AuthorId,
			&plano.AuthorName,
			&plano.CriadoEm,
			&plano.StatusId,
			&plano.CStatus)
		plano.Order = i
		i++
		planos = append(planos, plano)
		log.Println(plano)
	}
	log.Println("PLANOS " + strconv.Itoa(len(planos)))
	return planos
}

func DeletePlanosByEntidadeId(entidadeId string) {
	sqlStatement := "DELETE FROM virtus.Planos WHERE id_entidade=?"
	deleteForm, err := Db.Prepare(sqlStatement)
	if err != nil {
		log.Println(err.Error())
	}
	deleteForm.Exec(entidadeId)
	log.Println("DELETE Planos in Order Id: " + entidadeId)
}

func DeletePlanosHandler(diffDB []mdl.Plano) {
	sqlStatement := "DELETE FROM virtus.Planos WHERE id_plano=?"
	deleteForm, err := Db.Prepare(sqlStatement)
	if err != nil {
		log.Println(err.Error())
	}
	for n := range diffDB {
		deleteForm.Exec(strconv.FormatInt(int64(diffDB[n].Id), 10))
		log.Println("DELETE: Plano Id: " + strconv.FormatInt(int64(diffDB[n].Id), 10))
	}
}

func containsPlano(planos []mdl.Plano, planoCompared mdl.Plano) bool {
	for n := range planos {
		if planos[n].Id == planoCompared.Id {
			return true
		}
	}
	return false
}

func removePlano(planos []mdl.Plano, planoToBeRemoved mdl.Plano) []mdl.Plano {
	var newPlanos []mdl.Plano
	for i := range planos {
		if planos[i].Id != planoToBeRemoved.Id {
			newPlanos = append(newPlanos, planos[i])
		}
	}
	return newPlanos
}

// AJAX
func ListConfigPlanos(entidadeId string, cicloId string, pilarId string, componenteId string, soPGA string) []mdl.ConfigPlano {
	log.Println("entidadeId: " + entidadeId + " - cicloId: " + cicloId + " - pilar: " + pilarId + " - componenteId: " + componenteId + " - soPGA: " + soPGA)
	log.Println("List Config Planos")
	sql := "SELECT " +
		" a.id_produto_plano, " +
		" a.id_entidade, " +
		" a.id_plano " +
		" FROM virtus.produtos_planos a " +
		" WHERE a.id_entidade = " + entidadeId +
		" AND a.id_ciclo = " + cicloId +
		" AND a.id_pilar = " + pilarId +
		" AND a.id_componente = " + componenteId
	log.Println(sql)
	rows, _ := Db.Query(sql)
	defer rows.Close()
	var configurados []mdl.ConfigPlano
	var configPlano mdl.ConfigPlano
	for rows.Next() {
		rows.Scan(
			&configPlano.Id,
			&configPlano.EntidadeId,
			&configPlano.PlanoId)
		configurados = append(configurados, configPlano)
		log.Println(configPlano)
	}
	return configurados
}
