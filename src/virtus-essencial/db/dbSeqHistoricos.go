package db

import ()

func createSeqHistoricos() {
	// SEQUENCE id_PRODUTOS_CICLOS_HISTORICOS_seq
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_produtos_ciclos_historicos_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_produtos_ciclos_historicos_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	// SEQUENCE id_PRODUTOS_PILARES_HISTORICOS_seq
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_produtos_pilares_historicos_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_produtos_pilares_historicos_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	// SEQUENCE id_PRODUTOS_COMPONENTES_HISTORICOS_seq
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_produtos_componentes_historicos_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_produtos_componentes_historicos_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	// SEQUENCE id_PRODUTOS_TIPOS_NOTAS_HISTORICOS_seq
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_produtos_tipos_notas_historicos_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_produtos_tipos_notas_historicos_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	// SEQUENCE id_PRODUTOS_ELEMENTOS_HISTORICOS_seq
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_produtos_elementos_historicos_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_produtos_elementos_historicos_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	// SEQUENCE id_PRODUTOS_ITENS_HISTORICOS_seq
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'id_produtos_itens_historicos_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE id_produtos_itens_historicos_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
}
