package handlers

import (
	"log"
	"strconv"
	mdl "virtus-essencial/models"
)

// AJAX
func ListPilaresByCicloId(cicloId string) []mdl.PilarCiclo {
	log.Println("List Pilares Ciclos By Ciclo Id")
	log.Println("cicloId: " + cicloId)
	sql := "SELECT " +
		"a.id_pilar_ciclo, " +
		"a.id_ciclo, " +
		"coalesce(d.nome,'') as pilar_nome, " +
		"a.id_pilar, " +
		"concat(a.peso_padrao,' %'), " +
		"a.tipo_media, " +
		"a.id_author, " +
		"coalesce(b.name,'') as author_name, " +
		"coalesce(format(a.criado_em, 'dd/MM/yyyy HH:mm:ss'), '') as criado_em, " +
		"a.id_status, " +
		"coalesce(c.name,'') as status_name " +
		"FROM virtus.pilares_ciclos a " +
		"LEFT JOIN virtus.pilares d ON a.id_pilar = d.id_pilar " +
		"LEFT JOIN virtus.users b ON a.id_author = b.id_user " +
		"LEFT JOIN virtus.status c ON a.id_status = c.id_status " +
		"WHERE a.id_ciclo = ? ORDER BY d.nome ASC "
	log.Println(sql)
	rows, _ := Db.Query(sql, cicloId)
	defer rows.Close()
	var pilaresCiclo []mdl.PilarCiclo
	var pilarCiclo mdl.PilarCiclo
	var i = 1
	for rows.Next() {
		rows.Scan(
			&pilarCiclo.Id,
			&pilarCiclo.CicloId,
			&pilarCiclo.PilarNome,
			&pilarCiclo.PilarId,
			&pilarCiclo.PesoPadrao,
			&pilarCiclo.TipoMediaId,
			&pilarCiclo.AuthorId,
			&pilarCiclo.AuthorName,
			&pilarCiclo.CriadoEm,
			&pilarCiclo.StatusId,
			&pilarCiclo.CStatus)
		pilarCiclo.Order = i
		i++
		switch pilarCiclo.TipoMediaId {
		case 1:
			pilarCiclo.TipoMedia = "Aritmética"
		case 2:
			pilarCiclo.TipoMedia = "Geométrica"
		case 3:
			pilarCiclo.TipoMedia = "Harmônica"
		}
		pilaresCiclo = append(pilaresCiclo, pilarCiclo)
		log.Println(pilarCiclo)
	}
	return pilaresCiclo
}

func UpdatePilaresCicloHandler(pilaresCicloPage []mdl.PilarCiclo, pilaresCicloDB []mdl.PilarCiclo) {
	for i := range pilaresCicloPage {
		id := pilaresCicloPage[i].Id
		log.Println("id: " + strconv.FormatInt(id, 10))
		for j := range pilaresCicloDB {
			log.Println("pilaresCicloDB[j].Id: " + strconv.FormatInt(pilaresCicloDB[j].Id, 10))
			if strconv.FormatInt(pilaresCicloDB[j].Id, 10) == strconv.FormatInt(id, 10) {
				log.Println("Entrei")
				fieldsChanged := hasSomeFieldChangedPilarCiclo(pilaresCicloPage[i], pilaresCicloDB[j]) //DONE
				log.Println(fieldsChanged)
				if fieldsChanged {
					updatePilarCicloHandler(pilaresCicloPage[i], pilaresCicloDB[j]) // TODO
				}
				pilaresCicloDB = removePilarCiclo(pilaresCicloDB, pilaresCicloPage[i])
				break
			}
		}
	}
	DeletePilaresCicloHandler(pilaresCicloDB) // CORREÇÃO
}

func hasSomeFieldChangedPilarCiclo(pilarCicloPage mdl.PilarCiclo, pilarCicloDB mdl.PilarCiclo) bool {
	if pilarCicloPage.TipoMediaId != pilarCicloDB.TipoMediaId {
		return true
	} else if pilarCicloPage.PesoPadrao != pilarCicloDB.PesoPadrao {
		return true
	} else {
		return false
	}
}

func updatePilarCicloHandler(ce mdl.PilarCiclo, pilarCicloDB mdl.PilarCiclo) {
	sqlStatement := "UPDATE virtus.pilares_ciclos SET " +
		"tipo_media=?, peso_padrao=? WHERE id_pilar_ciclo=?"
	log.Println(sqlStatement)
	updtForm, _ := Db.Prepare(sqlStatement)
	_, err := updtForm.Exec(ce.TipoMediaId, ce.PesoPadrao, ce.Id)
	if err != nil {
		log.Println(err.Error())
	}
	log.Println("Statement: " + sqlStatement)
}

func DeletePilaresCicloByCicloId(cicloId string) {
	sqlStatement := "DELETE FROM virtus.pilares_ciclos WHERE id_ciclo=?"
	deleteForm, err := Db.Prepare(sqlStatement)
	if err != nil {
		log.Println(err.Error())
	}
	deleteForm.Exec(cicloId)
	log.Println("DELETE pilares_ciclos in Order Id: " + cicloId)
}

func DeletePilaresCicloHandler(diffDB []mdl.PilarCiclo) {
	sqlStatement := "DELETE FROM virtus.pilares_ciclos WHERE id_pilar_ciclo=?"
	deleteForm, err := Db.Prepare(sqlStatement)
	if err != nil {
		log.Println(err.Error())
	}
	for n := range diffDB {
		deleteForm.Exec(strconv.FormatInt(int64(diffDB[n].Id), 10))
		log.Println("DELETE: Pilar Ciclo Id: " + strconv.FormatInt(int64(diffDB[n].Id), 10))
	}
}

func containsPilarCiclo(pilaresCiclo []mdl.PilarCiclo, pilarCicloCompared mdl.PilarCiclo) bool {
	for n := range pilaresCiclo {
		if pilaresCiclo[n].Id == pilarCicloCompared.Id {
			return true
		}
	}
	return false
}

func removePilarCiclo(pilaresCiclo []mdl.PilarCiclo, pilarCicloToBeRemoved mdl.PilarCiclo) []mdl.PilarCiclo {
	var newPilaresCiclo []mdl.PilarCiclo
	for i := range pilaresCiclo {
		if pilaresCiclo[i].Id != pilarCicloToBeRemoved.Id {
			newPilaresCiclo = append(newPilaresCiclo, pilaresCiclo[i])
		}
	}
	return newPilaresCiclo
}
