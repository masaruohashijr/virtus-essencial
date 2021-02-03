package db

import (
	"log"
)

func createSeq() {
	stmt := "IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'features_roles_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE features_roles_id_seq AS BIGINT START WITH 1 INCREMENT BY 1 END"
	//	log.Println(stmt)
	_, err := db.Exec(stmt)
	if err != nil {
		log.Println(err.Error())
	}
	stmt = "IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'actions_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE actions_id_seq AS BIGINT START WITH 1 INCREMENT BY 1 END"
	//	log.Println(stmt)
	_, err = db.Exec(stmt)
	if err != nil {
		log.Println(err.Error())
	}
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'activities_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE activities_id_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'actions_status_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE actions_status_id_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'activities_roles_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE activities_roles_id_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'chamados_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE chamados_id_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'ciclos_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE ciclos_id_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'ciclos_entidades_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE ciclos_entidades_id_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'componentes_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE componentes_id_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'componentes_pilares_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE componentes_pilares_id_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'elementos_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE elementos_id_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'elementos_componentes_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE elementos_componentes_id_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'entidades_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE entidades_id_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'escritorios_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE escritorios_id_seq AS BIGINT START WITH 7 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'features_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE features_id_seq AS BIGINT START WITH 50 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'features_activities_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE features_activities_id_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'integrantes_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE integrantes_id_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'itens_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE itens_id_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'jurisdicoes_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE jurisdicoes_id_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'membros_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE membros_id_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'planos_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE planos_id_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'pilares_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE pilares_id_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'pilares_ciclos_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE pilares_ciclos_id_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'produtos_ciclos_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE produtos_ciclos_id_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'produtos_planos_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE produtos_planos_id_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'produtos_pilares_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE produtos_pilares_id_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'produtos_componentes_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE produtos_componentes_id_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'produtos_tipos_notas_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE produtos_tipos_notas_id_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'produtos_elementos_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE produtos_elementos_id_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'produtos_itens_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE produtos_itens_id_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'anotacoes_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE anotacoes_id_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'anotacoes_radares_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE anotacoes_radares_id_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'radares_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE radares_id_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'roles_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE roles_id_seq AS BIGINT START WITH 7 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'versoes_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE versoes_id_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'status_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE status_id_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'tipos_notas_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE tipos_notas_id_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'tipos_notas_componentes_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE tipos_notas_componentes_id_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'users_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE users_id_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'workflows_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE workflows_id_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
}
