package handlers

import (
	"log"
	mdl "virtus-essencial/models"
)

func ListTiposNotaByComponenteId(componenteId string) []mdl.TipoNota {
	sql := " SELECT " +
		" id_tipo_nota_componente, id_tipo_nota, " +
		" peso_padrao " +
		" FROM virtus.tipos_notas_componentes WHERE id_componente = ? "
	log.Println(sql)
	rows, _ := Db.Query(sql, componenteId)
	defer rows.Close()
	var tipos []mdl.TipoNota
	var tipo mdl.TipoNota
	for rows.Next() {
		rows.Scan(
			&tipo.Id,
			&tipo.TipoNotaId,
			&tipo.PesoPadrao)
		tipos = append(tipos, tipo)
	}
	return tipos
}
