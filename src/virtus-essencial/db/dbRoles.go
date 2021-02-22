package db

import (
//"log"
)

func createRoles() {
	query := " INSERT INTO roles (id, name, description, created_at) " +
		" SELECT 1, 'Admin', 'Admin' , GETDATE() " +
		" WHERE NOT EXISTS (SELECT id FROM roles WHERE name = 'Admin')"
	db.Exec(query)
	query = " INSERT INTO roles (id, name, description, created_at) " +
		" SELECT 2, 'Chefe', 'Chefe' , GETDATE() " +
		" WHERE NOT EXISTS (SELECT id FROM roles WHERE name = 'Chefe')"
	db.Exec(query)
	query = " INSERT INTO roles (id, name, description, created_at) " +
		" SELECT 3, 'Supervisor', 'Supervisor' , GETDATE() " +
		" WHERE NOT EXISTS (SELECT id FROM roles WHERE name = 'Supervisor')"
	db.Exec(query)
	query = " INSERT INTO roles (id, name, description, created_at) " +
		" SELECT 4, 'Auditor', 'Auditor' , GETDATE() " +
		" WHERE NOT EXISTS (SELECT id FROM roles WHERE name = 'Auditor')"
	db.Exec(query)
	query = " INSERT INTO roles (id, name, description, created_at) " +
		" SELECT 5, 'Visualizador', 'Visualizador' , GETDATE() " +
		" WHERE NOT EXISTS (SELECT id FROM roles WHERE name = 'Visualizador')"
	db.Exec(query)
	query = " INSERT INTO roles (id, name, description, created_at) " +
		" SELECT 6, 'Desenvolvedor', 'Desenvolvedor' , GETDATE() " +
		" WHERE NOT EXISTS (SELECT id FROM roles WHERE name = 'Desenvolvedor')"
	db.Exec(query)
}

func updateRoles() {
	query := " UPDATE roles SET id_author = 1 WHERE name = 'Admin' AND (SELECT id_author FROM roles WHERE name = 'Admin') IS NULL "
	//log.Println(query)
	db.Exec(query)
}
