package handlers

import (
	"log"
	"strconv"
	mdl "virtus-essencial/models"
)

// AJAX
func ListItensHandler(elementoId string) []mdl.Item {
	log.Println("List Itens By Elemento Id")
	sql := "SELECT " +
		" a.id_item, " +
		" a.id_elemento, " +
		" a.nome," +
		" coalesce(a.descricao,''), " +
		" coalesce(a.referencia,''), " +
		" a.id_author, " +
		" coalesce(b.name,'') as author_name, " +
		" coalesce(format(a.criado_em,'dd/MM/yyyy'),'') as data_criacao," +
		" a.id_status, " +
		" coalesce(c.name,'') as status_name " +
		" FROM virtus.itens a LEFT JOIN virtus.users b ON a.id_author = b.id_user " +
		" LEFT JOIN virtus.status c ON a.id_status = c.id_status " +
		" WHERE a.id_elemento = ? " +
		" ORDER BY a.nome ASC"
	log.Println(sql)
	rows, _ := Db.Query(sql, elementoId)
	defer rows.Close()
	var itens []mdl.Item
	var item mdl.Item
	var i = 1
	for rows.Next() {
		rows.Scan(&item.Id, &item.ElementoId, &item.Nome, &item.Descricao, &item.Referencia, &item.AuthorId, &item.AuthorName, &item.C_CriadoEm, &item.StatusId, &item.CStatus)
		item.Order = i
		i++
		itens = append(itens, item)
		log.Println(item)
	}
	return itens
}

func DeleteItensByElementoHandler(elementoId string) {
	sqlStatement := "DELETE FROM virtus.Itens WHERE id_elemento=?"
	deleteForm, err := Db.Prepare(sqlStatement)
	if err != nil {
		log.Println(err.Error())
	}
	deleteForm.Exec(elementoId)
	log.Println("DELETE Itens in Order Id: " + elementoId)
}

func DeleteItensHandler(diffDB []mdl.Item) {
	sqlStatement := "DELETE FROM virtus.itensWHERE id_item=?"
	deleteForm, err := Db.Prepare(sqlStatement)
	if err != nil {
		log.Println(err.Error())
	}
	for n := range diffDB {
		deleteForm.Exec(strconv.FormatInt(int64(diffDB[n].Id), 10))
		log.Println("DELETE: Item Id: " + strconv.FormatInt(int64(diffDB[n].Id), 10))
	}
}

func UpdateItensHandler(itensPage []mdl.Item, itensDB []mdl.Item) {
	for i := range itensPage {
		id := itensPage[i].Id
		log.Println("id: " + strconv.FormatInt(id, 10))
		for j := range itensDB {
			log.Println("itensDB[j].Id: " + strconv.FormatInt(itensDB[j].Id, 10))
			if strconv.FormatInt(itensDB[j].Id, 10) == strconv.FormatInt(id, 10) {
				log.Println("Entrei")
				fieldsChanged := hasSomeFieldChanged(itensPage[i], itensDB[j]) //DONE
				log.Println(fieldsChanged)
				if fieldsChanged {
					updateItemHandler(itensPage[i], itensDB[j]) // TODO
				}
				itensDB = removeItem(itensDB, itensPage[i]) // CORREÇÃO
				break
			}
		}
	}
	DeleteItensHandler(itensDB) // CORREÇÃO
}

func hasSomeFieldChanged(itemPage mdl.Item, itemDB mdl.Item) bool {
	log.Println("itemPage.Nome: " + itemPage.Nome)
	log.Println("itemDB.Nome: " + itemDB.Nome)
	if itemPage.Nome != itemDB.Nome {
		return true
	} else if itemPage.Descricao != itemDB.Descricao {
		return true
	} else if itemPage.Referencia != itemDB.Referencia {
		return true
	} else {
		return false
	}
}

func updateItemHandler(i mdl.Item, itemDB mdl.Item) {
	sqlStatement := "UPDATE virtus.itens SET " +
		"nome=?, descricao=?, referencia=? WHERE id_item=?"
	log.Println(sqlStatement)
	updtForm, _ := Db.Prepare(sqlStatement)
	log.Println(i.Nome)
	log.Println(i.Descricao)
	log.Println(i.Referencia)
	log.Println(i.Id)
	_, err := updtForm.Exec(i.Nome, i.Descricao, i.Referencia, i.Id)
	if err != nil {

		log.Println(err.Error())
	}
	log.Println("Statement: " + sqlStatement)
}
