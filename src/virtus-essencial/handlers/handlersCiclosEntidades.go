package handlers

import (
	"log"
	"strconv"
	"strings"
	mdl "virtus-essencial/models"
)

// AJAX
func ListCiclosEntidadeByEntidadeId(entidadeId string) []mdl.CicloEntidade {
	log.Println("List Ciclos Entidades By Entidade Id")
	log.Println("entidadeId: " + entidadeId)
	sql := "SELECT " +
		"a.id_ciclo_entidade, " +
		"a.id_entidade, " +
		"d.nome, " +
		"a.id_ciclo, " +
		"a.tipo_media, " +
		"a.id_author, " +
		"coalesce(b.name,'') as author_name, " +
		"coalesce(format(a.inicia_em,'dd/MM/yyyy'), '') as inicia_em, " +
		"coalesce(format(a.termina_em,'dd/MM/yyyy'), '') as termina_em, " +
		"coalesce(format(a.criado_em,'dd/MM/yyyy'), '') as criado_em, " +
		"a.id_status, " +
		"coalesce(c.name,'') as status_name " +
		"FROM virtus.ciclos_entidades a " +
		"LEFT JOIN virtus.ciclos d ON a.id_ciclo = d.id_ciclo " +
		"LEFT JOIN virtus.users b ON a.id_author = b.id_user " +
		"LEFT JOIN virtus.status c ON a.id_status = c.id_status " +
		"WHERE a.id_entidade = ? "
	log.Println(sql)
	rows, _ := Db.Query(sql, entidadeId)
	defer rows.Close()
	var ciclosEntidade []mdl.CicloEntidade
	var cicloEntidade mdl.CicloEntidade
	var i = 1
	for rows.Next() {
		rows.Scan(
			&cicloEntidade.Id,
			&cicloEntidade.EntidadeId,
			&cicloEntidade.Nome,
			&cicloEntidade.CicloId,
			&cicloEntidade.TipoMediaId,
			&cicloEntidade.AuthorId,
			&cicloEntidade.AuthorName,
			&cicloEntidade.IniciaEm,
			&cicloEntidade.TerminaEm,
			&cicloEntidade.CriadoEm,
			&cicloEntidade.StatusId,
			&cicloEntidade.CStatus)
		cicloEntidade.Order = i
		i++
		switch cicloEntidade.TipoMediaId {
		case 1:
			cicloEntidade.TipoMedia = "Aritmética"
		case 2:
			cicloEntidade.TipoMedia = "Geométrica"
		case 3:
			cicloEntidade.TipoMedia = "Harmônica"
		}
		ciclosEntidade = append(ciclosEntidade, cicloEntidade)
		log.Println(cicloEntidade)
	}
	return ciclosEntidade
}

func UpdateCiclosEntidadeHandler(ciclosEntidadePage []mdl.CicloEntidade, ciclosEntidadeDB []mdl.CicloEntidade) {
	for i := range ciclosEntidadePage {
		id := ciclosEntidadePage[i].Id
		log.Println("id: " + strconv.FormatInt(id, 10))
		for j := range ciclosEntidadeDB {
			log.Println("ciclosEntidadeDB[j].Id: " + strconv.FormatInt(ciclosEntidadeDB[j].Id, 10))
			if strconv.FormatInt(ciclosEntidadeDB[j].Id, 10) == strconv.FormatInt(id, 10) {
				log.Println("Entrei")
				fieldsChanged := hasSomeFieldChangedCicloEntidade(ciclosEntidadePage[i], ciclosEntidadeDB[j]) //DONE
				log.Println(fieldsChanged)
				if fieldsChanged {
					updateCicloEntidadeHandler(ciclosEntidadePage[i], ciclosEntidadeDB[j])
				}
				ciclosEntidadeDB = removeCicloEntidade(ciclosEntidadeDB, ciclosEntidadePage[i])
				break
			}
		}
	}
	DeleteCiclosEntidadeHandler(ciclosEntidadeDB) // CORREÇÃO
}

func hasSomeFieldChangedCicloEntidade(cicloEntidadePage mdl.CicloEntidade, cicloEntidadeDB mdl.CicloEntidade) bool {
	if cicloEntidadePage.TipoMediaId != cicloEntidadeDB.TipoMediaId {
		return true
	} else if cicloEntidadePage.IniciaEm != cicloEntidadeDB.IniciaEm {
		return true
	} else if cicloEntidadePage.TerminaEm != cicloEntidadeDB.TerminaEm {
		return true
	} else {
		return false
	}
}

func updateCicloEntidadeHandler(ce mdl.CicloEntidade, cicloEntidadeDB mdl.CicloEntidade) {
	sqlStatement := "UPDATE virtus.ciclos_entidades SET " +
		"tipo_media=?, inicia_em=?, termina_em=? WHERE id_ciclo_entidade=?"
	log.Println(sqlStatement)
	updtForm, _ := Db.Prepare(sqlStatement)
	log.Println("ce.IniciaEm: " + ce.IniciaEm)
	log.Println("ce.TerminaEm: " + ce.TerminaEm)
	_, err := updtForm.Exec(ce.TipoMediaId, ce.IniciaEm, ce.TerminaEm, ce.Id)
	if err != nil {
		log.Println(err.Error())
	}
	log.Println("Statement: " + sqlStatement)
}

func DeleteCiclosEntidadeByEntidadeId(entidadeId string) {
	sqlStatement := "DELETE FROM virtus.ciclos_entidades WHERE id_entidade=?"
	deleteForm, err := Db.Prepare(sqlStatement)
	if err != nil {
		log.Println(err.Error())
	}
	deleteForm.Exec(entidadeId)
	log.Println("DELETE virtus.ciclos_entidades in Order Id: " + entidadeId)
}

func DeleteCiclosEntidadeHandler(diffDB []mdl.CicloEntidade) string {
	sqlStatement := "DELETE FROM virtus.ciclos_entidades WHERE id_ciclo_entidade=?"
	deleteForm, _ := Db.Prepare(sqlStatement)
	for n := range diffDB {
		errMsg := ""
		_, err := deleteForm.Exec(strconv.FormatInt(int64(diffDB[n].Id), 10))
		log.Println("DELETE: Ciclo Entidade Id: " + strconv.FormatInt(int64(diffDB[n].Id), 10))
		if err != nil && strings.Contains(err.Error(), "violates foreign key") {
			errMsg = "Ciclo está associada a um registro e não pôde ser removida."
			return errMsg
		}
	}
	return ""
}

func containsCicloEntidade(ciclosEntidade []mdl.CicloEntidade, cicloEntidadeCompared mdl.CicloEntidade) bool {
	for n := range ciclosEntidade {
		if ciclosEntidade[n].Id == cicloEntidadeCompared.Id {
			return true
		}
	}
	return false
}

func removeCicloEntidade(ciclosEntidade []mdl.CicloEntidade, cicloEntidadeToBeRemoved mdl.CicloEntidade) []mdl.CicloEntidade {
	var newCiclosEntidade []mdl.CicloEntidade
	for i := range ciclosEntidade {
		if ciclosEntidade[i].Id != cicloEntidadeToBeRemoved.Id {
			newCiclosEntidade = append(newCiclosEntidade, ciclosEntidade[i])
		}
	}
	return newCiclosEntidade
}
