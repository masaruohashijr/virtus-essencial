package handlers

import (
	//	"encoding/json"
	"log"
	mdl "virtus-essencial/models"
)

func ListIntegrantesByEntidadeIdByCicloId(entidadeId string, cicloId string) []mdl.Integrante {
	log.Println("List Integrantes By Entidade Id e Ciclo Id")
	log.Println("entidadeId: " + entidadeId)
	log.Println("cicloId: " + cicloId)
	sql := "SELECT " +
		"a.id, " +
		"a.id_entidade, " +
		"a.id_ciclo, " +
		"a.id_usuario, " +
		"coalesce(d.name,'') as usuario_nome, " +
		"coalesce(e.name,'') as role_name, " +
		"coalesce(format(a.inicia_em,'dd/MM/yyyy'),'') as inicia_em, " +
		"coalesce(format(a.termina_em,'dd/MM/yyyy'),'') as termina_em, " +
		"a.id_author, " +
		"coalesce(b.name,'') as author_name, " +
		"coalesce(format(a.criado_em,'dd/MM/yyyy'),'') as criado_em, " +
		"a.id_status, " +
		"coalesce(c.name,'') as status_name " +
		"FROM integrantes a " +
		"LEFT JOIN users b ON a.id_author = b.id " +
		"LEFT JOIN status c ON a.id_status = c.id " +
		"LEFT JOIN users d ON a.id_usuario = d.id " +
		"LEFT JOIN roles e ON e.id = d.id_role " +
		"WHERE a.id_entidade = ? AND a.id_ciclo = ? ORDER BY d.name ASC "
	log.Println(sql)
	rows, _ := Db.Query(sql, entidadeId, cicloId)
	defer rows.Close()
	var integrantes []mdl.Integrante
	var integrante mdl.Integrante
	var i = 1
	for rows.Next() {
		rows.Scan(
			&integrante.Id,
			&integrante.EntidadeId,
			&integrante.CicloId,
			&integrante.UsuarioId,
			&integrante.UsuarioNome,
			&integrante.UsuarioPerfil,
			&integrante.IniciaEm,
			&integrante.TerminaEm,
			&integrante.AuthorId,
			&integrante.AuthorName,
			&integrante.CriadoEm,
			&integrante.StatusId,
			&integrante.CStatus)
		integrante.Order = i
		i++
		integrantes = append(integrantes, integrante)
		//log.Println(integrante)
	}
	return integrantes
}
