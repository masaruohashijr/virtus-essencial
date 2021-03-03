package handlers

import (
	"log"
	"strconv"
	"strings"
	mdl "virtus-essencial/models"
)

// AJAX
func ListAnotacoesRadarByRadarId(radarId string) []mdl.AnotacaoRadar {
	log.Println("List Anotacoes Radares By Radar Id")
	log.Println("radarId: " + radarId)
	sql := " SELECT a.id_anotacao_radar, " +
		" a.id_radar, " +
		" d.id_entidade, " +
		" a.id_anotacao, " +
		" coalesce(a.observacoes,''), " +
		" coalesce(a.registro_ata,''), " +
		" a.id_author, " +
		" coalesce(b.name,'') as author_name, " +
		" coalesce(format(a.criado_em,'dd/MM/yyyy')) as criado_em, " +
		" a.id_status, " +
		" coalesce(c.name,'') as status_name, " +
		" a.id_ultimo_atualizador, " +
		" coalesce(e.name,'') as ultimo_atualizador_name, " +
		" coalesce(format(a.ultima_atualizacao,'dd/MM/yyyy')) " +
		" FROM virtus.anotacoes_radares a " +
		" LEFT JOIN virtus.anotacoes d ON a.id_anotacao = d.id_anotacao " +
		" LEFT JOIN virtus.users b ON a.id_author = b.id_user " +
		" LEFT JOIN virtus.status c ON a.id_status = c.id_status " +
		" LEFT JOIN virtus.users e ON a.id_ultimo_atualizador = e.id_user " +
		" WHERE a.id_radar = ? "
	log.Println(sql)
	rows, _ := Db.Query(sql, radarId)
	defer rows.Close()
	var anotacoesRadar []mdl.AnotacaoRadar
	var anotacaoRadar mdl.AnotacaoRadar
	for rows.Next() {
		rows.Scan(
			&anotacaoRadar.Id,
			&anotacaoRadar.RadarId,
			&anotacaoRadar.EntidadeId,
			&anotacaoRadar.AnotacaoId,
			&anotacaoRadar.Observacoes,
			&anotacaoRadar.RegistroAta,
			&anotacaoRadar.AuthorId,
			&anotacaoRadar.AuthorName,
			&anotacaoRadar.CriadoEm,
			&anotacaoRadar.StatusId,
			&anotacaoRadar.CStatus,
			&anotacaoRadar.UltimoAtualizadorId,
			&anotacaoRadar.UltimoAtualizadorNome,
			&anotacaoRadar.UltimaAtualizacao)
		anotacoesRadar = append(anotacoesRadar, anotacaoRadar)
		log.Println(anotacaoRadar)
	}
	return anotacoesRadar
}

func UpdateAnotacoesRadarHandler(anotacoesRadarPage []mdl.AnotacaoRadar, anotacoesRadarDB []mdl.AnotacaoRadar, currentUserId int64) {
	for i := range anotacoesRadarPage {
		id := anotacoesRadarPage[i].Id
		log.Println("id: " + strconv.FormatInt(id, 10))
		for j := range anotacoesRadarDB {
			log.Println("anotacoesRadarDB[j].Id: " + strconv.FormatInt(anotacoesRadarDB[j].Id, 10))
			if strconv.FormatInt(anotacoesRadarDB[j].Id, 10) == strconv.FormatInt(id, 10) {
				log.Println("Entrei")
				fieldsChanged := hasSomeFieldChangedAnotacaoRadar(anotacoesRadarPage[i], anotacoesRadarDB[j]) //DONE
				log.Println(fieldsChanged)
				if fieldsChanged {
					updateAnotacaoRadarHandler(anotacoesRadarPage[i], anotacoesRadarDB[j], currentUserId)
				}
				anotacoesRadarDB = removeAnotacaoRadar(anotacoesRadarDB, anotacoesRadarPage[i])
				break
			}
		}
	}
	DeleteAnotacoesRadarHandler(anotacoesRadarDB) // CORREÇÃO
}

func hasSomeFieldChangedAnotacaoRadar(anotacaoRadarPage mdl.AnotacaoRadar, anotacaoRadarDB mdl.AnotacaoRadar) bool {
	if anotacaoRadarPage.Observacoes != anotacaoRadarDB.Observacoes {
		return true
	} else if anotacaoRadarPage.RegistroAta != anotacaoRadarDB.RegistroAta {
		return true
	} else {
		return false
	}
}

func updateAnotacaoRadarHandler(anotacaoRadar mdl.AnotacaoRadar, anotacaoRadarDB mdl.AnotacaoRadar, currentUserId int64) {
	sqlStatement := "UPDATE virtus.anotacoes_radares " +
		" SET id_radar=?, id_anotacao=?, observacoes=?, registro_ata=?, " +
		" id_ultimo_atualizador=?, ultima_atualizacao=GETDATE() " +
		" WHERE id_anotacao_radar = ? "
	log.Println(sqlStatement)
	updtForm, _ := Db.Prepare(sqlStatement)
	_, err := updtForm.Exec(anotacaoRadar.RadarId,
		anotacaoRadar.AnotacaoId,
		anotacaoRadar.Observacoes,
		anotacaoRadar.RegistroAta,
		currentUserId,
		anotacaoRadar.Id)
	if err != nil {
		log.Println(err.Error())
	}
	log.Println("Statement: " + sqlStatement)
}

func DeleteAnotacoesRadarByRadarId(radarId string) {
	sqlStatement := "DELETE FROM virtus.anotacoes_radares WHERE id_radar=?"
	deleteForm, err := Db.Prepare(sqlStatement)
	if err != nil {
		log.Println(err.Error())
	}
	deleteForm.Exec(radarId)
	log.Println("DELETE anotacoes_radares in Order Id: " + radarId)
}

func DeleteAnotacoesRadarHandler(diffDB []mdl.AnotacaoRadar) string {
	sqlStatement := "DELETE FROM virtus.anotacoes_radares WHERE id_anotacao_radar=?"
	deleteForm, _ := Db.Prepare(sqlStatement)
	for n := range diffDB {
		errMsg := ""
		_, err := deleteForm.Exec(strconv.FormatInt(int64(diffDB[n].Id), 10))
		log.Println("DELETE: Anotacao Radar Id: " + strconv.FormatInt(int64(diffDB[n].Id), 10))
		if err != nil && strings.Contains(err.Error(), "violates foreign key") {
			errMsg = "Anotacao está associada a um registro e não pôde ser removida."
			return errMsg
		}
	}
	return ""
}

func containsAnotacaoRadar(anotacoesRadar []mdl.AnotacaoRadar, anotacaoRadarCompared mdl.AnotacaoRadar) bool {
	for n := range anotacoesRadar {
		if anotacoesRadar[n].Id == anotacaoRadarCompared.Id {
			return true
		}
	}
	return false
}

func removeAnotacaoRadar(anotacoesRadar []mdl.AnotacaoRadar, anotacaoRadarToBeRemoved mdl.AnotacaoRadar) []mdl.AnotacaoRadar {
	var newAnotacoesRadar []mdl.AnotacaoRadar
	for i := range anotacoesRadar {
		if anotacoesRadar[i].Id != anotacaoRadarToBeRemoved.Id {
			newAnotacoesRadar = append(newAnotacoesRadar, anotacoesRadar[i])
		}
	}
	return newAnotacoesRadar
}
