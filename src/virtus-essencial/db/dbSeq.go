package db

import (
	"log"
)

func createSeq() {
	stmt := "IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_features_roles_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_features_roles_seq AS BIGINT START WITH 1 INCREMENT BY 1 END"
	//	log.Println(stmt)
	_, err := db.Exec(stmt)
	if err != nil {
		log.Println(err.Error())
	}
	stmt = "IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_actions_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_actions_seq AS BIGINT START WITH 1 INCREMENT BY 1 END"
	//	log.Println(stmt)
	_, err = db.Exec(stmt)
	if err != nil {
		log.Println(err.Error())
	}
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_activities_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_activities_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_actions_status_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_actions_status_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_activities_roles_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_activities_roles_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_chamados_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_chamados_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_ciclos_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_ciclos_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_ciclos_entidades_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_ciclos_entidades_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_comentarios_anotacoes_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_comentarios_anotacoes_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_comentarios_chamados_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_comentarios_chamados_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_componentes_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_componentes_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_componentes_pilares_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_componentes_pilares_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_elementos_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_elementos_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_elementos_componentes_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_elementos_componentes_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_entidades_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_entidades_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_entidades_ciclos_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_entidades_ciclos_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_escritorios_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_escritorios_seq AS BIGINT START WITH 7 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_features_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_features_seq AS BIGINT START WITH 50 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_features_activities_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_features_activities_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_integrantes_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_integrantes_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_itens_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_itens_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_jurisdicoes_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_jurisdicoes_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_membros_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_membros_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_planos_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_planos_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_pilares_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_pilares_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_pilares_ciclos_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_pilares_ciclos_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_produtos_ciclos_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_produtos_ciclos_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_produtos_planos_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_produtos_planos_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_produtos_pilares_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_produtos_pilares_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_produtos_componentes_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_produtos_componentes_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_produtos_tipos_notas_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_produtos_tipos_notas_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_produtos_elementos_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_produtos_elementos_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_produtos_itens_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_produtos_itens_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_anotacoes_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_anotacoes_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_anotacoes_radares_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_anotacoes_radares_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_radares_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_radares_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_roles_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_roles_seq AS BIGINT START WITH 7 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_versoes_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_versoes_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_status_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_status_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_tipos_notas_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_tipos_notas_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_tipos_notas_componentes_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_tipos_notas_componentes_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_users_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_users_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_workflows_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_workflows_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
}
