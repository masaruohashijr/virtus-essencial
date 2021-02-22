package db

import (
	"log"
)

func createUniqueKey() {
	db.Exec(" ALTER TABLE actions_status" +
		" ADD CONSTRAINT action_status_unique_key UNIQUE (id_action, id_origin_status, id_destination_status)")
	db.Exec(" ALTER TABLE features_roles" +
		" ADD CONSTRAINT feature_role_unique_key UNIQUE (id_role, id_feature)")
	db.Exec(" ALTER TABLE users" +
		" ADD CONSTRAINT username_unique_key UNIQUE (username)")
	db.Exec(" ALTER TABLE activities_roles" +
		" ADD CONSTRAINT action_role_unique_key UNIQUE (id_activity, id_role)")
	db.Exec(" ALTER TABLE features_activities" +
		" ADD CONSTRAINT features_activities_unique_key UNIQUE (id_activity, id_feature)")
	db.Exec(" ALTER TABLE jurisdicoes" +
		" ADD CONSTRAINT jurisdicoes_unique_key UNIQUE (id_escritorio, id_entidade)")
	db.Exec(" ALTER TABLE membros" +
		" ADD CONSTRAINT membros_unique_key UNIQUE (id_escritorio, id_usuario)")
	db.Exec(" ALTER TABLE ciclos_entidades" +
		" ADD CONSTRAINT ciclos_entidades_unique_key UNIQUE (id_entidade, id_ciclo)")
	db.Exec(" ALTER TABLE pilares_ciclos" +
		" ADD CONSTRAINT pilares_ciclos_unique_key UNIQUE (id_ciclo, id_pilar)")
	db.Exec(" ALTER TABLE componentes_pilares" +
		" ADD CONSTRAINT componentes_pilares_unique_key UNIQUE (id_pilar, id_componente)")
	db.Exec(" ALTER TABLE elementos_componentes" +
		" ADD CONSTRAINT elementos_componentes_unique_key UNIQUE (id_componente, id_elemento, id_tipo_nota)")
	db.Exec(" ALTER TABLE tipos_notas_componentes" +
		" ADD CONSTRAINT tipos_notas_componentes_unique_key UNIQUE (id_componente, id_tipo_nota)")
	db.Exec(" ALTER TABLE produtos_ciclos" +
		" ADD CONSTRAINT produtos_ciclos_unique_key UNIQUE (id_entidade, id_ciclo)")
	db.Exec(" ALTER TABLE produtos_pilares" +
		" ADD CONSTRAINT produtos_pilares_unique_key UNIQUE (id_entidade, id_ciclo, id_pilar)")
	db.Exec(" ALTER TABLE produtos_componentes" +
		" ADD CONSTRAINT produtos_componentes_unique_key UNIQUE (id_entidade, id_ciclo, id_pilar, id_componente)")
	db.Exec(" ALTER TABLE produtos_planos" +
		" ADD CONSTRAINT produtos_planos_unique_key UNIQUE (id_entidade, id_ciclo, id_pilar, id_componente, id_plano)")
	db.Exec(" ALTER TABLE produtos_tipos_notas" +
		" ADD CONSTRAINT produtos_tipos_notas_unique_key UNIQUE (id_entidade, id_ciclo, id_pilar, id_componente, id_plano, id_tipo_nota)")
	db.Exec(" ALTER TABLE produtos_elementos" +
		" ADD CONSTRAINT produtos_elementos_unique_key UNIQUE (id_entidade, id_ciclo, id_pilar, id_componente, id_plano, id_tipo_nota, id_elemento)")
	db.Exec(" ALTER TABLE produtos_itens" +
		" ADD CONSTRAINT produtos_itens_unique_key UNIQUE (id_entidade, id_ciclo, id_pilar, id_componente, id_plano, id_tipo_nota, id_elemento, id_item)")
}

func createFKey() {
	// ACTIONS
	log.Println("FOREIGN KEYS")
	_, err := db.Exec("ALTER TABLE actions " +
		" ADD CONSTRAINT destination_status_fkey FOREIGN KEY (id_destination_status)" +
		" REFERENCES status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("ALTER TABLE actions " +
		" ADD CONSTRAINT origin_status_fkey FOREIGN KEY (id_origin_status)" +
		" REFERENCES status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	//  ACTIONS_STATUS
	_, err = db.Exec("ALTER TABLE actions_status " +
		" ADD CONSTRAINT actions_fkey FOREIGN KEY (id_action)" +
		" REFERENCES actions (id_action) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("ALTER TABLE actions_status " +
		" ADD CONSTRAINT origin_actions_status_fkey FOREIGN KEY (id_origin_status)" +
		" REFERENCES status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("ALTER TABLE actions_status " +
		" ADD CONSTRAINT destination_actions_status_fkey FOREIGN KEY (id_destination_status)" +
		" REFERENCES status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	// ACTIVITIES
	_, err = db.Exec("ALTER TABLE activities ADD CONSTRAINT action_fkey FOREIGN KEY (id_action)" +
		" REFERENCES actions (id_action) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("ALTER TABLE activities ADD CONSTRAINT expiration_action_fkey FOREIGN KEY (id_expiration_action)" +
		" REFERENCES actions (id_action) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("ALTER TABLE activities ADD CONSTRAINT workflow_fkey FOREIGN KEY (id_workflow)" +
		" REFERENCES workflows (id_workflow) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	// ACTIVITIES_ROLES
	_, err = db.Exec("ALTER TABLE activities_roles " +
		" ADD CONSTRAINT activities_fkey FOREIGN KEY (id_activity)" +
		" REFERENCES activities (id_activity) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("ALTER TABLE activities_roles " +
		" ADD CONSTRAINT roles_fkey FOREIGN KEY (id_role)" +
		" REFERENCES roles (id_role) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	// CICLOS
	_, err = db.Exec("ALTER TABLE ciclos" +
		" ADD CONSTRAINT authors_ciclos_fkey FOREIGN KEY (id_author)" +
		" REFERENCES users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("ALTER TABLE ciclos" +
		" ADD CONSTRAINT status_ciclos_fkey FOREIGN KEY (id_status)" +
		" REFERENCES status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	// CICLOS ENTIDADES
	_, err = db.Exec("ALTER TABLE ciclos_entidades" +
		" ADD CONSTRAINT entidades_ciclos_entidades_fkey FOREIGN KEY (id_entidade)" +
		" REFERENCES entidades (id_entidade) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("ALTER TABLE ciclos_entidades" +
		" ADD CONSTRAINT ciclos_ciclos_entidades_fkey FOREIGN KEY (id_ciclo)" +
		" REFERENCES ciclos (id_ciclo) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("ALTER TABLE ciclos_entidades" +
		" ADD CONSTRAINT authors_ciclos_entidades_fkey FOREIGN KEY (id_author)" +
		" REFERENCES users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("ALTER TABLE ciclos_entidades" +
		" ADD CONSTRAINT status_ciclos_entidades_fkey FOREIGN KEY (id_status)" +
		" REFERENCES status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	// COMPONENTES
	_, err = db.Exec("ALTER TABLE componentes" +
		" ADD CONSTRAINT authors_componentes_fkey FOREIGN KEY (id_author)" +
		" REFERENCES users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("ALTER TABLE componentes" +
		" ADD CONSTRAINT status_componentes_fkey FOREIGN KEY (id_status)" +
		" REFERENCES status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION ")

	// COMPONENTES PILARES
	_, err = db.Exec("ALTER TABLE componentes_pilares" +
		" ADD CONSTRAINT componentes_componentes_pilares_fkey FOREIGN KEY (id_componente)" +
		" REFERENCES componentes (id_componente) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("ALTER TABLE componentes_pilares" +
		" ADD CONSTRAINT pilares_componentes_pilares_fkey FOREIGN KEY (id_pilar)" +
		" REFERENCES pilares (id_pilar) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("ALTER TABLE componentes_pilares" +
		" ADD CONSTRAINT authors_componentes_pilares_fkey FOREIGN KEY (id_author)" +
		" REFERENCES users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION ")

	_, err = db.Exec("ALTER TABLE componentes_pilares" +
		" ADD CONSTRAINT status_componentes_pilares_fkey FOREIGN KEY (id_status)" +
		" REFERENCES status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	// ELEMENTOS
	_, err = db.Exec("ALTER TABLE elementos" +
		" ADD CONSTRAINT users_fkey FOREIGN KEY (id_author)" +
		" REFERENCES users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("ALTER TABLE elementos" +
		" ADD CONSTRAINT status_elementos_fkey FOREIGN KEY (id_status)" +
		" REFERENCES status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION ")

	// ELEMENTOS_COMPONENTES
	_, err = db.Exec("ALTER TABLE elementos_componentes" +
		" ADD CONSTRAINT tipos_notas_fkey FOREIGN KEY (id_tipo_nota)" +
		" REFERENCES tipos_notas (id_tipo_nota) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("ALTER TABLE elementos_componentes" +
		" ADD CONSTRAINT elementos_elementos_componentes_fkey FOREIGN KEY (id_elemento)" +
		" REFERENCES elementos (id_elemento) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("ALTER TABLE elementos_componentes" +
		" ADD CONSTRAINT componentes_elementos_componentes_fkey FOREIGN KEY (id_componente)" +
		" REFERENCES componentes (id_componente) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE elementos_componentes" +
		" ADD CONSTRAINT authors_elementos_componentes_fkey FOREIGN KEY (id_author)" +
		" REFERENCES users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE elementos_componentes" +
		" ADD CONSTRAINT status_elementos_componentes_fkey FOREIGN KEY (id_status)" +
		" REFERENCES status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	// ENTIDADES
	_, err = db.Exec("ALTER TABLE entidades" +
		" ADD CONSTRAINT authors_entidades_fkey FOREIGN KEY (id_author)" +
		" REFERENCES users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE entidades" +
		" ADD CONSTRAINT status_entidades_fkey FOREIGN KEY (id_status)" +
		" REFERENCES status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	// ESCRITÓRIOS
	_, err = db.Exec("ALTER TABLE escritorios" +
		" ADD CONSTRAINT authors_escritorios_fkey FOREIGN KEY (id_author)" +
		" REFERENCES users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE escritorios" +
		" ADD CONSTRAINT status_escritorios_fkey FOREIGN KEY (id_status)" +
		" REFERENCES status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION")
	if err != nil {
		log.Println(err.Error())
	}
	// FEATURES_ACTIVITIES
	_, err = db.Exec("ALTER TABLE features_activities " +
		" ADD CONSTRAINT activities_features_activities_fkey FOREIGN KEY (id_activity)" +
		" REFERENCES activities (id_activity) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE features_activities " +
		" ADD CONSTRAINT features_features_activities_fkey FOREIGN KEY (id_feature)" +
		" REFERENCES features (id_feature) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	// FEATURES_ROLES
	_, err = db.Exec("ALTER TABLE features_roles " +
		" ADD CONSTRAINT features_features_roles_fkey FOREIGN KEY (id_feature)" +
		" REFERENCES features (id_feature) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE features_roles " +
		" ADD CONSTRAINT roles_features_roles_fkey FOREIGN KEY (id_role)" +
		" REFERENCES roles (id_role) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	// ITENS
	_, err = db.Exec("ALTER TABLE itens" +
		" ADD CONSTRAINT elementos_itens_fkey FOREIGN KEY (id_elemento)" +
		" REFERENCES elementos (id_elemento) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	// PILARES
	_, err = db.Exec("ALTER TABLE pilares" +
		" ADD CONSTRAINT authors_pilares_fkey FOREIGN KEY (id_author)" +
		" REFERENCES users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE pilares" +
		" ADD CONSTRAINT status_pilares_fkey FOREIGN KEY (id_status)" +
		" REFERENCES status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	// PILARES_CICLOS
	_, err = db.Exec("ALTER TABLE pilares_ciclos" +
		" ADD CONSTRAINT pilares_pilares_ciclos_fkey FOREIGN KEY (id_pilar)" +
		" REFERENCES pilares (id_pilar) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE pilares_ciclos" +
		" ADD CONSTRAINT ciclos_pilares_ciclos_fkey FOREIGN KEY (id_ciclo)" +
		" REFERENCES ciclos (id_ciclo) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE pilares_ciclos" +
		" ADD CONSTRAINT authors_pilares_ciclos_fkey FOREIGN KEY (id_author)" +
		" REFERENCES users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE pilares_ciclos" +
		" ADD CONSTRAINT status_pilares_ciclos_fkey FOREIGN KEY (id_status)" +
		" REFERENCES status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	// PLANOS
	_, err = db.Exec("ALTER TABLE planos" +
		" ADD CONSTRAINT entidades_planos_fkey FOREIGN KEY (id_entidade)" +
		" REFERENCES entidades (id_entidade) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE planos" +
		" ADD CONSTRAINT authors_planos_fkey FOREIGN KEY (id_author)" +
		" REFERENCES users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE planos" +
		" ADD CONSTRAINT status_planos_fkey FOREIGN KEY (id_status)" +
		" REFERENCES status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	// PRODUTOS_CICLOS
	_, err = db.Exec("ALTER TABLE produtos_ciclos" +
		" ADD CONSTRAINT entidades_produtos_ciclos_fkey FOREIGN KEY (id_entidade)" +
		" REFERENCES entidades (id_entidade) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_ciclos" +
		" ADD CONSTRAINT ciclos_produtos_ciclos_fkey FOREIGN KEY (id_ciclo)" +
		" REFERENCES ciclos (id_ciclo) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_ciclos" +
		" ADD CONSTRAINT authors_produtos_ciclos_fkey FOREIGN KEY (id_author)" +
		" REFERENCES users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_ciclos" +
		" ADD CONSTRAINT status_produtos_ciclos_fkey FOREIGN KEY (id_status)" +
		" REFERENCES status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	// PRODUTOS_PILARES
	_, err = db.Exec("ALTER TABLE produtos_pilares" +
		" ADD CONSTRAINT entidades_produtos_pilares_fkey FOREIGN KEY (id_entidade)" +
		" REFERENCES entidades (id_entidade) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_pilares" +
		" ADD CONSTRAINT ciclos_produtos_pilares_fkey FOREIGN KEY (id_ciclo)" +
		" REFERENCES ciclos (id_ciclo) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_pilares" +
		" ADD CONSTRAINT pilares_produtos_pilares_fkey FOREIGN KEY (id_pilar)" +
		" REFERENCES pilares (id_pilar) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_pilares" +
		" ADD CONSTRAINT authors_produtos_pilares_fkey FOREIGN KEY (id_author)" +
		" REFERENCES users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_pilares" +
		" ADD CONSTRAINT status_produtos_pilares_fkey FOREIGN KEY (id_status)" +
		" REFERENCES status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	// PRODUTOS_COMPONENTES
	_, err = db.Exec("ALTER TABLE produtos_componentes" +
		" ADD CONSTRAINT entidades_produtos_componentes_fkey FOREIGN KEY (id_entidade)" +
		" REFERENCES entidades (id_entidade) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_componentes" +
		" ADD CONSTRAINT ciclos_produtos_componentes_fkey FOREIGN KEY (id_ciclo)" +
		" REFERENCES ciclos (id_ciclo) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_componentes" +
		" ADD CONSTRAINT pilares_produtos_componentes_fkey FOREIGN KEY (id_pilar)" +
		" REFERENCES pilares (id_pilar) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_componentes" +
		" ADD CONSTRAINT componentes_produtos_componentes_fkey FOREIGN KEY (id_componente)" +
		" REFERENCES componentes (id_componente) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_componentes" +
		" ADD CONSTRAINT authors_produtos_componentes_fkey FOREIGN KEY (id_author)" +
		" REFERENCES users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_componentes" +
		" ADD CONSTRAINT status_produtos_componentes_fkey FOREIGN KEY (id_status)" +
		" REFERENCES status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	// PRODUTOS_TIPOS_NOTAS
	_, err = db.Exec("ALTER TABLE produtos_tipos_notas" +
		" ADD CONSTRAINT entidades_produtos_tipos_notas_fkey FOREIGN KEY (id_entidade)" +
		" REFERENCES entidades (id_entidade) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_tipos_notas" +
		" ADD CONSTRAINT ciclos_produtos_tipos_notas_fkey FOREIGN KEY (id_ciclo)" +
		" REFERENCES ciclos (id_ciclo) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_tipos_notas" +
		" ADD CONSTRAINT pilares_produtos_tipos_notas_fkey FOREIGN KEY (id_pilar)" +
		" REFERENCES pilares (id_pilar) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_tipos_notas" +
		" ADD CONSTRAINT componentes_produtos_tipos_notas_fkey FOREIGN KEY (id_componente)" +
		" REFERENCES componentes (id_componente) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_tipos_notas" +
		" ADD CONSTRAINT tipos_notas_produtos_tipos_notas_fkey FOREIGN KEY (id_tipo_nota)" +
		" REFERENCES tipos_notas (id_tipo_nota) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_tipos_notas" +
		" ADD CONSTRAINT authors_produtos_tipos_notas_fkey FOREIGN KEY (id_author)" +
		" REFERENCES users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_tipos_notas" +
		" ADD CONSTRAINT status_produtos_tipos_notas_fkey FOREIGN KEY (id_status)" +
		" REFERENCES status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	// PRODUTOS_ELEMENTOS
	_, err = db.Exec("ALTER TABLE produtos_elementos" +
		" ADD CONSTRAINT entidades_produtos_elementos_fkey FOREIGN KEY (id_entidade)" +
		" REFERENCES entidades (id_entidade) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_elementos" +
		" ADD CONSTRAINT ciclos_produtos_elementos_fkey FOREIGN KEY (id_ciclo)" +
		" REFERENCES ciclos (id_ciclo) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_elementos" +
		" ADD CONSTRAINT pilares_produtos_elementos_fkey FOREIGN KEY (id_pilar)" +
		" REFERENCES pilares (id_pilar) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_elementos" +
		" ADD CONSTRAINT componentes_produtos_elementos_fkey FOREIGN KEY (id_componente)" +
		" REFERENCES componentes (id_componente) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_elementos" +
		" ADD CONSTRAINT tipos_notas_produtos_elementos_fkey FOREIGN KEY (id_tipo_nota)" +
		" REFERENCES tipos_notas (id_tipo_nota) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_elementos" +
		" ADD CONSTRAINT elementos_produtos_elementos_fkey FOREIGN KEY (id_elemento)" +
		" REFERENCES elementos (id_elemento) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_elementos" +
		" ADD CONSTRAINT authors_produtos_elementos_fkey FOREIGN KEY (id_author)" +
		" REFERENCES users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_elementos" +
		" ADD CONSTRAINT status_produtos_elementos_fkey FOREIGN KEY (id_status)" +
		" REFERENCES status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	// PRODUTOS_ITENS
	_, err = db.Exec("ALTER TABLE produtos_itens" +
		" ADD CONSTRAINT entidades_produtos_itens_fkey FOREIGN KEY (id_entidade)" +
		" REFERENCES entidades (id_entidade) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_itens" +
		" ADD CONSTRAINT ciclos_produtos_itens_fkey FOREIGN KEY (id_ciclo)" +
		" REFERENCES ciclos (id_ciclo) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_itens" +
		" ADD CONSTRAINT pilares_produtos_itens_fkey FOREIGN KEY (id_pilar)" +
		" REFERENCES pilares (id_pilar) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_itens" +
		" ADD CONSTRAINT componentes_produtos_itens_fkey FOREIGN KEY (id_componente)" +
		" REFERENCES componentes (id_componente) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_itens" +
		" ADD CONSTRAINT elementos_produtos_itens_fkey FOREIGN KEY (id_elemento)" +
		" REFERENCES elementos (id_elemento) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_itens" +
		" ADD CONSTRAINT itens_produtos_itens_fkey FOREIGN KEY (id_item)" +
		" REFERENCES itens (id_item) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_itens" +
		" ADD CONSTRAINT authors_produtos_itens_fkey FOREIGN KEY (id_author)" +
		" REFERENCES users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_itens" +
		" ADD CONSTRAINT status_produtos_itens_fkey FOREIGN KEY (id_status)" +
		" REFERENCES status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	// TIPOS_NOTAS
	_, err = db.Exec("ALTER TABLE tipos_notas" +
		" ADD CONSTRAINT authors_tipos_notas_fkey FOREIGN KEY (id_author)" +
		" REFERENCES users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	// TIPOS_NOTAS_COMPONENTES
	_, err = db.Exec("ALTER TABLE tipos_notas_componentes" +
		" ADD CONSTRAINT tipos_notas_tipos_notas_componentes_fkey FOREIGN KEY (id_tipo_nota)" +
		" REFERENCES tipos_notas (id_tipo_nota) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE tipos_notas_componentes" +
		" ADD CONSTRAINT componentes_tipos_notas_componentes_fkey FOREIGN KEY (id_componente)" +
		" REFERENCES componentes (id_componente) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE tipos_notas" +
		" ADD CONSTRAINT status_tipos_notas_fkey FOREIGN KEY (id_status)" +
		" REFERENCES status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	// USERS
	_, err = db.Exec("ALTER TABLE users " +
		" ADD CONSTRAINT roles_users_fkey FOREIGN KEY (id_role)" +
		" REFERENCES roles (id_role) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE users" +
		" ADD CONSTRAINT authors_users_fkey FOREIGN KEY (id_author)" +
		" REFERENCES users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE users" +
		" ADD CONSTRAINT status_users_fkey FOREIGN KEY (id_status)" +
		" REFERENCES status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	// JURISDICOES
	_, err = db.Exec("ALTER TABLE jurisdicoes" +
		" ADD CONSTRAINT entidades_jurisdicoes_fkey FOREIGN KEY (id_entidade)" +
		" REFERENCES entidades (id_entidade) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE jurisdicoes" +
		" ADD CONSTRAINT escritorios_jurisdicoes_fkey FOREIGN KEY (id_escritorio)" +
		" REFERENCES escritorios (id_escritorio) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE jurisdicoes" +
		" ADD CONSTRAINT authors_jurisdicoes_fkey FOREIGN KEY (id_author)" +
		" REFERENCES users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE jurisdicoes" +
		" ADD CONSTRAINT status_jurisdicoes_fkey FOREIGN KEY (id_status)" +
		" REFERENCES status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	// MEMBROS
	_, err = db.Exec("ALTER TABLE membros" +
		" ADD CONSTRAINT usuarios_membros_fkey FOREIGN KEY (id_usuario)" +
		" REFERENCES users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE membros" +
		" ADD CONSTRAINT escritorios_membros_fkey FOREIGN KEY (id_escritorio)" +
		" REFERENCES escritorios (id_escritorio) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE membros" +
		" ADD CONSTRAINT authors_membros_fkey FOREIGN KEY (id_author)" +
		" REFERENCES users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE membros" +
		" ADD CONSTRAINT status_membros_fkey FOREIGN KEY (id_status)" +
		" REFERENCES status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
}

func createPKey() {
	db.Exec("ALTER TABLE actions ADD CONSTRAINT actions_pkey PRIMARY KEY (id_action)")
	db.Exec("ALTER TABLE actions_status ADD CONSTRAINT actions_status_pkey PRIMARY KEY (id_action_status)")
	db.Exec("ALTER TABLE activities ADD CONSTRAINT activities_pkey PRIMARY KEY (id_activity)")
	db.Exec("ALTER TABLE activities_roles ADD CONSTRAINT activities_roles_pkey PRIMARY KEY (id_activity_role)")
	db.Exec("ALTER TABLE anotacoes ADD CONSTRAINT anotacoes_pkey PRIMARY KEY (id_anotacao)")
	db.Exec("ALTER TABLE anotacoes_radares ADD CONSTRAINT anotacoes_radares_pkey PRIMARY KEY (id_anotacao_radar)")
	db.Exec("ALTER TABLE chamados ADD CONSTRAINT chamados_pkey PRIMARY KEY (id_chamado)")
	db.Exec("ALTER TABLE chamados_versoes ADD CONSTRAINT chamados_versoes_pkey PRIMARY KEY (id_chamado_versao)")
	db.Exec("ALTER TABLE ciclos ADD CONSTRAINT ciclos_pkey PRIMARY KEY (id_ciclo)")
	db.Exec("ALTER TABLE ciclos_entidades ADD CONSTRAINT ciclos_pkey PRIMARY KEY (id_ciclo_entidade)")
	db.Exec("ALTER TABLE comentarios_chamados ADD CONSTRAINT comentarios_chamados_pkey PRIMARY KEY (id_comentario_chamado)")
	db.Exec("ALTER TABLE componentes ADD CONSTRAINT componentes_pkey PRIMARY KEY (id_componente)")
	db.Exec("ALTER TABLE componentes_pilares ADD CONSTRAINT componentes_pilares_pkey PRIMARY KEY (id_componente_pilar)")
	db.Exec("ALTER TABLE elementos ADD CONSTRAINT elementos_pkey PRIMARY KEY (id_elemento)")
	db.Exec("ALTER TABLE elementos_componentes ADD CONSTRAINT elementos_componentes_pkey PRIMARY KEY (id_elemento_componente)")
	db.Exec("ALTER TABLE entidades ADD CONSTRAINT entidades_pkey PRIMARY KEY (id_entidade)")
	db.Exec("ALTER TABLE entidades_ciclos ADD CONSTRAINT entidades_ciclos_pkey PRIMARY KEY (id_entidade_ciclo)")
	db.Exec("ALTER TABLE escritorios ADD CONSTRAINT escritorios_pkey PRIMARY KEY (id_escritorio)")
	db.Exec("ALTER TABLE features ADD CONSTRAINT features_pkey PRIMARY KEY (id_feature)")
	db.Exec("ALTER TABLE features_activities ADD CONSTRAINT features_activities_pkey PRIMARY KEY (id_feature_activity)")
	db.Exec("ALTER TABLE features_roles ADD CONSTRAINT features_roles_pkey PRIMARY KEY (id_feature_role)")
	db.Exec("ALTER TABLE integrantes ADD CONSTRAINT integrantes_pkey PRIMARY KEY (id_integrante)")
	db.Exec("ALTER TABLE itens ADD CONSTRAINT itens_pkey PRIMARY KEY (id_item)")
	db.Exec("ALTER TABLE jurisdicoes ADD CONSTRAINT jurisdicoes_pkey PRIMARY KEY (id_jurisdicao)")
	db.Exec("ALTER TABLE membros ADD CONSTRAINT membros_pkey PRIMARY KEY (id_membro)")
	db.Exec("ALTER TABLE pilares ADD CONSTRAINT pilares_pkey PRIMARY KEY (id_pilar)")
	db.Exec("ALTER TABLE pilares_ciclos ADD CONSTRAINT pilares_ciclos_pkey PRIMARY KEY (id_pilar_ciclo)")
	db.Exec("ALTER TABLE planos ADD CONSTRAINT planos_pkey PRIMARY KEY (id_plano)")
	db.Exec("ALTER TABLE processos ADD CONSTRAINT processos_pkey PRIMARY KEY (id_processo)")
	db.Exec("ALTER TABLE produtos_ciclos ADD CONSTRAINT produtos_ciclos_pkey PRIMARY KEY (id_produto_ciclo)")
	db.Exec("ALTER TABLE produtos_pilares ADD CONSTRAINT produtos_pilares_pkey PRIMARY KEY (id_produto_pilar)")
	db.Exec("ALTER TABLE produtos_componentes ADD CONSTRAINT produtos_componentes_pkey PRIMARY KEY (id_produto_componente)")
	db.Exec("ALTER TABLE produtos_tipos_notas ADD CONSTRAINT produtos_tipos_notas_pkey PRIMARY KEY (id_produto_tipo_nota)")
	db.Exec("ALTER TABLE produtos_planos ADD CONSTRAINT produtos_planos_pkey PRIMARY KEY (id_plano)")
	db.Exec("ALTER TABLE produtos_elementos ADD CONSTRAINT produtos_elementos_pkey PRIMARY KEY (id_elemento)")
	db.Exec("ALTER TABLE produtos_itens ADD CONSTRAINT produtos_itens_pkey PRIMARY KEY (id_item)")
	db.Exec("ALTER TABLE produtos_ciclos_historicos ADD CONSTRAINT produtos_ciclos_historicos_pkey PRIMARY KEY (id_produto_ciclo_historico)")
	db.Exec("ALTER TABLE produtos_pilares_historicos ADD CONSTRAINT produtos_pilares_historicos_pkey PRIMARY KEY (id_produto_pilar_historico)")
	db.Exec("ALTER TABLE produtos_componentes_historicos ADD CONSTRAINT produtos_componentes_historicos_pkey PRIMARY KEY (id_produto_componente_historico)")
	db.Exec("ALTER TABLE produtos_elementos_historicos ADD CONSTRAINT produtos_elementos_historicos_pkey PRIMARY KEY (id_produto_elemento_historico)")
	db.Exec("ALTER TABLE produtos_itens_historicos ADD CONSTRAINT produtos_itens_historicos_pkey PRIMARY KEY (id_produto_item_historico)")
	db.Exec("ALTER TABLE radares ADD CONSTRAINT radares_pkey PRIMARY KEY (id_radar)")
	db.Exec("ALTER TABLE roles ADD CONSTRAINT roles_pkey PRIMARY KEY (id_role)")
	db.Exec("ALTER TABLE status ADD CONSTRAINT status_pkey PRIMARY KEY (id_status)")
	db.Exec("ALTER TABLE tipos_notas ADD CONSTRAINT tipos_notas_pkey PRIMARY KEY (id_tipo_nota)")
	db.Exec("ALTER TABLE tipos_notas_componentes ADD CONSTRAINT tipos_notas_componentes_pkey PRIMARY KEY (id_tipo_nota_componente)")
	db.Exec("ALTER TABLE versoes ADD CONSTRAINT versoes_pkey PRIMARY KEY (id_versao)")
	db.Exec("ALTER TABLE users ADD CONSTRAINT users_pkey PRIMARY KEY (id_user)")
	db.Exec("ALTER TABLE workflows ADD CONSTRAINT workflows_pkey PRIMARY KEY (id_workflow)")
}
