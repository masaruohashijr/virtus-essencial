package handlers

import (
	"log"
	"strconv"
	mdl "virtus-essencial/models"
)

// AJAX
func ListElementosByComponenteId(componenteId string) []mdl.ElementoComponente {
	log.Println("List Elementos Componentes By Componente Id")
	log.Println("componenteId: " + componenteId)
	sql := "SELECT " +
		"a.id_elemento_componente, " +
		"a.id_componente, " +
		"coalesce(d.nome,'') as elemento_nome, " +
		"a.id_elemento, " +
		"a.id_tipo_nota, " +
		"e.nome as tipo_nota_nome, " +
		"a.peso_padrao, " +
		"a.id_author, " +
		"coalesce(b.name,'') as author_name, " +
		"coalesce(format(a.criado_em, 'dd/MM/yyyy HH:mm:ss'), '') as criado_em, " +
		"a.id_status, " +
		"coalesce(c.name,'') as status_name " +
		"FROM virtus.elementos_componentes a " +
		"LEFT JOIN virtus.elementos d ON a.id_elemento = d.id_elemento " +
		"LEFT JOIN virtus.users b ON a.id_author = b.id_user " +
		"LEFT JOIN virtus.status c ON a.id_status = c.id_status " +
		"LEFT JOIN virtus.tipos_notas e ON a.id_tipo_nota = e.id_tipo_nota " +
		"WHERE a.id_componente = ? ORDER BY elemento_nome"
	log.Println(sql)
	rows, _ := Db.Query(sql, componenteId)
	defer rows.Close()
	var elementosComponente []mdl.ElementoComponente
	var elementoComponente mdl.ElementoComponente
	var i = 1
	for rows.Next() {
		rows.Scan(
			&elementoComponente.Id,
			&elementoComponente.ComponenteId,
			&elementoComponente.ElementoNome,
			&elementoComponente.ElementoId,
			&elementoComponente.TipoNotaId,
			&elementoComponente.TipoNotaNome,
			&elementoComponente.PesoPadrao,
			&elementoComponente.AuthorId,
			&elementoComponente.AuthorName,
			&elementoComponente.CriadoEm,
			&elementoComponente.StatusId,
			&elementoComponente.CStatus)
		elementoComponente.Order = i
		i++
		elementosComponente = append(elementosComponente, elementoComponente)
		//log.Println(elementoComponente)
	}
	return elementosComponente
}

func UpdateElementosComponenteHandler(elementosComponentePage []mdl.ElementoComponente, elementosComponenteDB []mdl.ElementoComponente) {
	log.Println("UpdateElementosComponenteHandler")
	for i := range elementosComponentePage {
		id := elementosComponentePage[i].Id
		log.Println("id: " + strconv.FormatInt(id, 10))
		for j := range elementosComponenteDB {
			if strconv.FormatInt(elementosComponenteDB[j].Id, 10) == strconv.FormatInt(id, 10) {
				fieldsChanged := hasSomeFieldChangedElementoComponente(elementosComponentePage[i], elementosComponenteDB[j]) //DONE
				log.Println(fieldsChanged)
				if fieldsChanged {
					updateElementoComponenteHandler(elementosComponentePage[i], elementosComponenteDB[j]) // TODO
				}
				elementosComponenteDB = removeElementoComponente(elementosComponenteDB, elementosComponentePage[i])
				break
			}
		}
	}
	DeleteElementosComponenteHandler(elementosComponenteDB)
}

func hasSomeFieldChangedElementoComponente(elementoComponentePage mdl.ElementoComponente, elementoComponenteDB mdl.ElementoComponente) bool {
	log.Println("hasSomeFieldChangedElementoComponente")
	log.Println(elementoComponentePage.PesoPadrao)
	log.Println(elementoComponenteDB.PesoPadrao)
	if elementoComponentePage.PesoPadrao != elementoComponenteDB.PesoPadrao {
		return true
	} else {
		return false
	}
}

func updateElementoComponenteHandler(elementoComponente mdl.ElementoComponente, elementoComponenteDB mdl.ElementoComponente) {
	log.Println("updateElementoComponenteHandler")
	sqlStatement := "UPDATE virtus.elementos_componentes SET " +
		"peso_padrao=? WHERE id_elemento_componente=?"
	log.Println(sqlStatement)
	updtForm, _ := Db.Prepare(sqlStatement)
	_, err := updtForm.Exec(elementoComponente.PesoPadrao, elementoComponente.Id)
	if err != nil {
		log.Println(err.Error())
	}
	log.Println("Statement: " + sqlStatement)
	sqlStatement = "UPDATE virtus.componentes_pilares a " +
		" SET peso_padrao = " +
		" (SELECT round(avg(b.peso_padrao),2) " +
		" FROM virtus.elementos_componentes b " +
		" WHERE b.id_componente = a.id_componente AND " +
		" b.id_pilar = a.id_pilar AND " +
		" b.id_ciclo = a.id_ciclo AND " +
		" b.id_entidade = a.id_entidade " +
		" GROUP BY b.id_entidade, b.id_ciclo, b.id_pilar, b.id_componente) " +
		" WHERE a.id_elemento_componente=? "
	log.Println(sqlStatement)
	updtForm, _ = Db.Prepare(sqlStatement)
	_, err = updtForm.Exec(elementoComponente.ComponenteId)
	if err != nil {
		log.Println(err.Error())
	}
	log.Println("Statement: " + sqlStatement)
}

func DeleteElementosComponenteByComponenteId(componenteId string) {
	sqlStatement := "DELETE FROM virtus.elementos_componentes WHERE id_componente=?"
	deleteForm, err := Db.Prepare(sqlStatement)
	if err != nil {
		log.Println(err.Error())
	}
	deleteForm.Exec(componenteId)
	log.Println("DELETE elementos_componentes in Order Id: " + componenteId)
}

func DeleteElementosComponenteHandler(diffDB []mdl.ElementoComponente) {
	sqlStatement := "DELETE FROM virtus.elementos_componentes WHERE id_elemento_componente=?"
	deleteForm, err := Db.Prepare(sqlStatement)
	if err != nil {
		log.Println(err.Error())
	}
	for n := range diffDB {
		deleteForm.Exec(strconv.FormatInt(int64(diffDB[n].Id), 10))
		log.Println("DELETE: Elemento Componente Id: " + strconv.FormatInt(int64(diffDB[n].Id), 10))
	}
}

func containsElementoComponente(elementosComponente []mdl.ElementoComponente, elementoComponenteCompared mdl.ElementoComponente) bool {
	for n := range elementosComponente {
		if elementosComponente[n].Id == elementoComponenteCompared.Id {
			return true
		}
	}
	return false
}

func removeElementoComponente(elementosComponente []mdl.ElementoComponente, elementoComponenteToBeRemoved mdl.ElementoComponente) []mdl.ElementoComponente {
	var newElementosComponente []mdl.ElementoComponente
	for i := range elementosComponente {
		if elementosComponente[i].Id != elementoComponenteToBeRemoved.Id {
			newElementosComponente = append(newElementosComponente, elementosComponente[i])
		}
	}
	return newElementosComponente
}
