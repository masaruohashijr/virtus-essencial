package db

import ()

func createSeqHistoricos() {
	// Sequence PRODUTOS_CICLOS_HISTORICOS_ID_SEQ
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'produtos_ciclos_historicos_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE produtos_ciclos_historicos_id_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	// Sequence PRODUTOS_PILARES_HISTORICOS_ID_SEQ
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'produtos_pilares_historicos_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE produtos_pilares_historicos_id_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	// Sequence PRODUTOS_COMPONENTES_HISTORICOS_ID_SEQ
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'produtos_componentes_historicos_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE produtos_componentes_historicos_id_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	// Sequence PRODUTOS_TIPOS_NOTAS_HISTORICOS_ID_SEQ
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'produtos_tipos_notas_historicos_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE produtos_tipos_notas_historicos_id_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	// Sequence PRODUTOS_ELEMENTOS_HISTORICOS_ID_SEQ
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'produtos_elementos_historicos_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE produtos_elementos_historicos_id_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
	// Sequence PRODUTOS_ITENS_HISTORICOS_ID_SEQ
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'produtos_itens_historicos_id_seq') AND type in (N'SO')) BEGIN CREATE SEQUENCE produtos_itens_historicos_id_seq AS BIGINT START WITH 1 INCREMENT BY 1 END")
}
