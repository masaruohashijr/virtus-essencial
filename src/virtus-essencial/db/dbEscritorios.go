package db

import (
//"log"
)

func createEscritorios() {
	sql := "INSERT INTO virtus.escritorios( " +
		" id_escritorio, nome, descricao, abreviatura, id_author, criado_em) " +
		" SELECT 1, 'Escritório de Representação - Pernambuco', 'Escritório de Representação - Pernambuco','ERPE', 1, GETDATE() " +
		" WHERE NOT EXISTS (SELECT 1 FROM virtus.escritorios WHERE id_escritorio= 1)"
	//log.Println(sql)
	db.Exec(sql)
	sql = "INSERT INTO virtus.escritorios( " +
		" id_escritorio, nome, descricao, abreviatura, id_author, criado_em) " +
		" SELECT 2, 'Escritório de Representação - São Paulo', 'Escritório de Representação - São Paulo','ERSP', 1, GETDATE() " +
		" WHERE NOT EXISTS (SELECT 1 FROM virtus.escritorios WHERE id_escritorio= 2)"
	db.Exec(sql)
	sql = "INSERT INTO virtus.escritorios( " +
		" id_escritorio, nome, descricao, abreviatura, id_author, criado_em) " +
		" SELECT 3, 'Escritório de Representação - Minas Gerais', 'Escritório de Representação - Minas Gerais','ERMG', 1, GETDATE() " +
		" WHERE NOT EXISTS (SELECT 1 FROM virtus.escritorios WHERE id_escritorio= 3)"
	db.Exec(sql)
	sql = "INSERT INTO virtus.escritorios( " +
		" id_escritorio, nome, descricao, abreviatura, id_author, criado_em) " +
		" SELECT 4, 'Escritório de Representação - Rio Grande do Sul', 'Escritório de Representação - Rio Grande do Sul','ERRS', 1, GETDATE() " +
		" WHERE NOT EXISTS (SELECT 1 FROM virtus.escritorios WHERE id_escritorio= 4)"
	db.Exec(sql)
	sql = "INSERT INTO virtus.escritorios( " +
		" id_escritorio, nome, descricao, abreviatura, id_author, criado_em) " +
		" SELECT 5, 'Escritório de Representação - Rio de Janeiro', 'Escritório de Representação - Rio de Janeiro','ERRJ', 1, GETDATE() " +
		" WHERE NOT EXISTS (SELECT 1 FROM virtus.escritorios WHERE id_escritorio= 5)"
	db.Exec(sql)
	sql = "INSERT INTO virtus.escritorios( " +
		" id_escritorio, nome, descricao, abreviatura, id_author, criado_em) " +
		" SELECT 6, 'Escritório de Representação - Distrito Federal', 'Escritório de Representação - Distrito Federal','ERDF', 1, GETDATE() " +
		" WHERE NOT EXISTS (SELECT 1 FROM virtus.escritorios WHERE id_escritorio= 6)"
	db.Exec(sql)
}
