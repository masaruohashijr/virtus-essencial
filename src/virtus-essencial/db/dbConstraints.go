package db

import (
	"log"
)

func createUniqueKey() {
	db.Exec(" ALTER TABLE actions_status" +
		" ADD CONSTRAINT action_status_unique_key UNIQUE (action_id, origin_status_id, destination_status_id)")
	db.Exec(" ALTER TABLE features_roles" +
		" ADD CONSTRAINT feature_role_unique_key UNIQUE (role_id, feature_id)")
	db.Exec(" ALTER TABLE users" +
		" ADD CONSTRAINT username_unique_key UNIQUE (username)")
	db.Exec(" ALTER TABLE activities_roles" +
		" ADD CONSTRAINT action_role_unique_key UNIQUE (activity_id, role_id)")
	db.Exec(" ALTER TABLE features_activities" +
		" ADD CONSTRAINT features_activities_unique_key UNIQUE (activity_id, feature_id)")
	db.Exec(" ALTER TABLE jurisdicoes" +
		" ADD CONSTRAINT jurisdicoes_unique_key UNIQUE (escritorio_id, entidade_id)")
	db.Exec(" ALTER TABLE membros" +
		" ADD CONSTRAINT membros_unique_key UNIQUE (escritorio_id, usuario_id)")
	db.Exec(" ALTER TABLE ciclos_entidades" +
		" ADD CONSTRAINT ciclos_entidades_unique_key UNIQUE (entidade_id, ciclo_id)")
	db.Exec(" ALTER TABLE pilares_ciclos" +
		" ADD CONSTRAINT pilares_ciclos_unique_key UNIQUE (ciclo_id, pilar_id)")
	db.Exec(" ALTER TABLE componentes_pilares" +
		" ADD CONSTRAINT componentes_pilares_unique_key UNIQUE (pilar_id, componente_id)")
	db.Exec(" ALTER TABLE elementos_componentes" +
		" ADD CONSTRAINT elementos_componentes_unique_key UNIQUE (componente_id, elemento_id, tipo_nota_id)")
	db.Exec(" ALTER TABLE tipos_notas_componentes" +
		" ADD CONSTRAINT tipos_notas_componentes_unique_key UNIQUE (componente_id, tipo_nota_id)")
	db.Exec(" ALTER TABLE produtos_ciclos" +
		" ADD CONSTRAINT produtos_ciclos_unique_key UNIQUE (entidade_id, ciclo_id)")
	db.Exec(" ALTER TABLE produtos_pilares" +
		" ADD CONSTRAINT produtos_pilares_unique_key UNIQUE (entidade_id, ciclo_id, pilar_id)")
	db.Exec(" ALTER TABLE produtos_componentes" +
		" ADD CONSTRAINT produtos_componentes_unique_key UNIQUE (entidade_id, ciclo_id, pilar_id, componente_id)")
	db.Exec(" ALTER TABLE produtos_planos" +
		" ADD CONSTRAINT produtos_planos_unique_key UNIQUE (entidade_id, ciclo_id, pilar_id, componente_id, plano_id)")
	db.Exec(" ALTER TABLE produtos_tipos_notas" +
		" ADD CONSTRAINT produtos_tipos_notas_unique_key UNIQUE (entidade_id, ciclo_id, pilar_id, componente_id, plano_id, tipo_nota_id)")
	db.Exec(" ALTER TABLE produtos_elementos" +
		" ADD CONSTRAINT produtos_elementos_unique_key UNIQUE (entidade_id, ciclo_id, pilar_id, componente_id, plano_id, tipo_nota_id, elemento_id)")
	db.Exec(" ALTER TABLE produtos_itens" +
		" ADD CONSTRAINT produtos_itens_unique_key UNIQUE (entidade_id, ciclo_id, pilar_id, componente_id, plano_id, tipo_nota_id, elemento_id, item_id)")
}

func createFKey() {
	// ACTIONS
	log.Println("FOREIGN KEYS")
	_, err := db.Exec("ALTER TABLE actions " +
		" ADD CONSTRAINT destination_status_fkey FOREIGN KEY (destination_status_id)" +
		" REFERENCES status (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("ALTER TABLE actions " +
		" ADD CONSTRAINT origin_status_fkey FOREIGN KEY (origin_status_id)" +
		" REFERENCES status (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("ALTER TABLE actions " +
		" ADD CONSTRAINT workflows_fkey FOREIGN KEY (workflow_id)" +
		" REFERENCES workflows (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	//  ACTIONS_STATUS
	_, err = db.Exec("ALTER TABLE actions_status " +
		" ADD CONSTRAINT actions_fkey FOREIGN KEY (action_id)" +
		" REFERENCES actions (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("ALTER TABLE actions_status " +
		" ADD CONSTRAINT origin_status_fkey FOREIGN KEY (origin_status_id)" +
		" REFERENCES status (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("ALTER TABLE actions_status " +
		" ADD CONSTRAINT destination_status_fkey FOREIGN KEY (destination_status_id)" +
		" REFERENCES status (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	// ACTIVITIES
	_, err = db.Exec("ALTER TABLE activities ADD CONSTRAINT action_fkey FOREIGN KEY (action_id)" +
		" REFERENCES actions (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("ALTER TABLE activities ADD CONSTRAINT expiration_action_fkey FOREIGN KEY (expiration_action_id)" +
		" REFERENCES actions (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("ALTER TABLE activities ADD CONSTRAINT workflow_fkey FOREIGN KEY (workflow_id)" +
		" REFERENCES workflows (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	// ACTIVITIES_ROLES
	_, err = db.Exec("ALTER TABLE activities_roles " +
		" ADD CONSTRAINT activities_fkey FOREIGN KEY (activity_id)" +
		" REFERENCES activities (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("ALTER TABLE activities_roles " +
		" ADD CONSTRAINT roles_fkey FOREIGN KEY (role_id)" +
		" REFERENCES roles (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	// CICLOS
	_, err = db.Exec("ALTER TABLE ciclos" +
		" ADD CONSTRAINT authors_fkey FOREIGN KEY (author_id)" +
		" REFERENCES users (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("ALTER TABLE ciclos" +
		" ADD CONSTRAINT status_fkey FOREIGN KEY (status_id)" +
		" REFERENCES status (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	// CICLOS ENTIDADES
	_, err = db.Exec("ALTER TABLE ciclos_entidades" +
		" ADD CONSTRAINT entidades_fkey FOREIGN KEY (entidade_id)" +
		" REFERENCES entidades (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("ALTER TABLE ciclos_entidades" +
		" ADD CONSTRAINT ciclos_fkey FOREIGN KEY (ciclo_id)" +
		" REFERENCES ciclos (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("ALTER TABLE ciclos_entidades" +
		" ADD CONSTRAINT authors_fkey FOREIGN KEY (author_id)" +
		" REFERENCES users (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("ALTER TABLE ciclos_entidades" +
		" ADD CONSTRAINT status_fkey FOREIGN KEY (status_id)" +
		" REFERENCES status (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	// COMPONENTES
	_, err = db.Exec("ALTER TABLE componentes" +
		" ADD CONSTRAINT authors_fkey FOREIGN KEY (author_id)" +
		" REFERENCES users (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("ALTER TABLE componentes" +
		" ADD CONSTRAINT status_fkey FOREIGN KEY (status_id)" +
		" REFERENCES status (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")

	// COMPONENTES PILARES
	_, err = db.Exec("ALTER TABLE componentes_pilares" +
		" ADD CONSTRAINT componentes_fkey FOREIGN KEY (componente_id)" +
		" REFERENCES componentes (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("ALTER TABLE componentes_pilares" +
		" ADD CONSTRAINT pilares_fkey FOREIGN KEY (pilar_id)" +
		" REFERENCES pilares (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("ALTER TABLE componentes_pilares" +
		" ADD CONSTRAINT authors_fkey FOREIGN KEY (author_id)" +
		" REFERENCES users (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")

	_, err = db.Exec("ALTER TABLE componentes_pilares" +
		" ADD CONSTRAINT status_fkey FOREIGN KEY (status_id)" +
		" REFERENCES status (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	// ELEMENTOS
	_, err = db.Exec("ALTER TABLE elementos" +
		" ADD CONSTRAINT users_fkey FOREIGN KEY (author_id)" +
		" REFERENCES users (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("ALTER TABLE elementos" +
		" ADD CONSTRAINT status_fkey FOREIGN KEY (status_id)" +
		" REFERENCES status (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")

	// ELEMENTOS_COMPONENTES
	_, err = db.Exec("ALTER TABLE elementos_componentes" +
		" ADD CONSTRAINT tipos_notas_fkey FOREIGN KEY (tipo_nota_id)" +
		" REFERENCES tipos_notas (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("ALTER TABLE elementos_componentes" +
		" ADD CONSTRAINT elementos_fkey FOREIGN KEY (elemento_id)" +
		" REFERENCES elementos (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("ALTER TABLE elementos_componentes" +
		" ADD CONSTRAINT componentes_fkey FOREIGN KEY (componente_id)" +
		" REFERENCES componentes (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE elementos_componentes" +
		" ADD CONSTRAINT authors_fkey FOREIGN KEY (author_id)" +
		" REFERENCES users (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE elementos_componentes" +
		" ADD CONSTRAINT status_fkey FOREIGN KEY (status_id)" +
		" REFERENCES status (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	// ENTIDADES
	_, err = db.Exec("ALTER TABLE entidades" +
		" ADD CONSTRAINT authors_fkey FOREIGN KEY (author_id)" +
		" REFERENCES users (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE entidades" +
		" ADD CONSTRAINT status_fkey FOREIGN KEY (status_id)" +
		" REFERENCES status (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	// ESCRITÓRIOS
	_, err = db.Exec("ALTER TABLE escritorios" +
		" ADD CONSTRAINT authors_fkey FOREIGN KEY (author_id)" +
		" REFERENCES users (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE escritorios" +
		" ADD CONSTRAINT status_fkey FOREIGN KEY (status_id)" +
		" REFERENCES status (id) ON DELETE NO ACTION ON UPDATE NO ACTION")
	if err != nil {
		log.Println(err.Error())
	}
	// FEATURES_ACTIVITIES
	_, err = db.Exec("ALTER TABLE features_activities " +
		" ADD CONSTRAINT activities_fkey FOREIGN KEY (activity_id)" +
		" REFERENCES activities (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE features_activities " +
		" ADD CONSTRAINT features_fkey FOREIGN KEY (feature_id)" +
		" REFERENCES features (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	// FEATURES_ROLES
	_, err = db.Exec("ALTER TABLE features_roles " +
		" ADD CONSTRAINT features_fkey FOREIGN KEY (feature_id)" +
		" REFERENCES features (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE features_roles " +
		" ADD CONSTRAINT roles_fkey FOREIGN KEY (role_id)" +
		" REFERENCES roles (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	// ITENS
	_, err = db.Exec("ALTER TABLE itens" +
		" ADD CONSTRAINT elementos_fkey FOREIGN KEY (elemento_id)" +
		" REFERENCES elementos (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	// PILARES
	_, err = db.Exec("ALTER TABLE pilares" +
		" ADD CONSTRAINT authors_fkey FOREIGN KEY (author_id)" +
		" REFERENCES users (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE pilares" +
		" ADD CONSTRAINT status_fkey FOREIGN KEY (status_id)" +
		" REFERENCES status (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	// PILARES_CICLOS
	_, err = db.Exec("ALTER TABLE pilares_ciclos" +
		" ADD CONSTRAINT pilares_fkey FOREIGN KEY (pilar_id)" +
		" REFERENCES pilares (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE pilares_ciclos" +
		" ADD CONSTRAINT ciclos_fkey FOREIGN KEY (ciclo_id)" +
		" REFERENCES ciclos (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE pilares_ciclos" +
		" ADD CONSTRAINT authors_fkey FOREIGN KEY (author_id)" +
		" REFERENCES users (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE pilares_ciclos" +
		" ADD CONSTRAINT status_fkey FOREIGN KEY (status_id)" +
		" REFERENCES status (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	// PLANOS
	_, err = db.Exec("ALTER TABLE planos" +
		" ADD CONSTRAINT entidades_fkey FOREIGN KEY (entidade_id)" +
		" REFERENCES entidades (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE planos" +
		" ADD CONSTRAINT authors_fkey FOREIGN KEY (author_id)" +
		" REFERENCES users (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE planos" +
		" ADD CONSTRAINT status_fkey FOREIGN KEY (status_id)" +
		" REFERENCES status (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	// PRODUTOS_CICLOS
	_, err = db.Exec("ALTER TABLE produtos_ciclos" +
		" ADD CONSTRAINT entidades_fkey FOREIGN KEY (entidade_id)" +
		" REFERENCES entidades (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_ciclos" +
		" ADD CONSTRAINT ciclos_fkey FOREIGN KEY (ciclo_id)" +
		" REFERENCES ciclos (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_ciclos" +
		" ADD CONSTRAINT authors_fkey FOREIGN KEY (author_id)" +
		" REFERENCES users (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_ciclos" +
		" ADD CONSTRAINT status_fkey FOREIGN KEY (status_id)" +
		" REFERENCES status (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	// PRODUTOS_PILARES
	_, err = db.Exec("ALTER TABLE produtos_pilares" +
		" ADD CONSTRAINT entidades_fkey FOREIGN KEY (entidade_id)" +
		" REFERENCES entidades (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_pilares" +
		" ADD CONSTRAINT ciclos_fkey FOREIGN KEY (ciclo_id)" +
		" REFERENCES ciclos (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_pilares" +
		" ADD CONSTRAINT pilares_fkey FOREIGN KEY (pilar_id)" +
		" REFERENCES pilares (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_pilares" +
		" ADD CONSTRAINT authors_fkey FOREIGN KEY (author_id)" +
		" REFERENCES users (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_pilares" +
		" ADD CONSTRAINT status_fkey FOREIGN KEY (status_id)" +
		" REFERENCES status (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	// PRODUTOS_COMPONENTES
	_, err = db.Exec("ALTER TABLE produtos_componentes" +
		" ADD CONSTRAINT entidades_fkey FOREIGN KEY (entidade_id)" +
		" REFERENCES entidades (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_componentes" +
		" ADD CONSTRAINT ciclos_fkey FOREIGN KEY (ciclo_id)" +
		" REFERENCES ciclos (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_componentes" +
		" ADD CONSTRAINT pilares_fkey FOREIGN KEY (pilar_id)" +
		" REFERENCES pilares (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_componentes" +
		" ADD CONSTRAINT componentes_fkey FOREIGN KEY (componente_id)" +
		" REFERENCES componentes (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_componentes" +
		" ADD CONSTRAINT authors_fkey FOREIGN KEY (author_id)" +
		" REFERENCES users (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_componentes" +
		" ADD CONSTRAINT status_fkey FOREIGN KEY (status_id)" +
		" REFERENCES status (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	// PRODUTOS_TIPOS_NOTAS
	_, err = db.Exec("ALTER TABLE produtos_tipos_notas" +
		" ADD CONSTRAINT entidades_fkey FOREIGN KEY (entidade_id)" +
		" REFERENCES entidades (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_tipos_notas" +
		" ADD CONSTRAINT ciclos_fkey FOREIGN KEY (ciclo_id)" +
		" REFERENCES ciclos (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_tipos_notas" +
		" ADD CONSTRAINT pilares_fkey FOREIGN KEY (pilar_id)" +
		" REFERENCES pilares (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_tipos_notas" +
		" ADD CONSTRAINT componentes_fkey FOREIGN KEY (componente_id)" +
		" REFERENCES componentes (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_tipos_notas" +
		" ADD CONSTRAINT tipos_notas_fkey FOREIGN KEY (tipo_nota_id)" +
		" REFERENCES tipos_notas (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_tipos_notas" +
		" ADD CONSTRAINT authors_fkey FOREIGN KEY (author_id)" +
		" REFERENCES users (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_tipos_notas" +
		" ADD CONSTRAINT status_fkey FOREIGN KEY (status_id)" +
		" REFERENCES status (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	// PRODUTOS_ELEMENTOS
	_, err = db.Exec("ALTER TABLE produtos_elementos" +
		" ADD CONSTRAINT entidades_fkey FOREIGN KEY (entidade_id)" +
		" REFERENCES entidades (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_elementos" +
		" ADD CONSTRAINT ciclos_fkey FOREIGN KEY (ciclo_id)" +
		" REFERENCES ciclos (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_elementos" +
		" ADD CONSTRAINT pilares_fkey FOREIGN KEY (pilar_id)" +
		" REFERENCES pilares (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_elementos" +
		" ADD CONSTRAINT componentes_fkey FOREIGN KEY (componente_id)" +
		" REFERENCES componentes (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_elementos" +
		" ADD CONSTRAINT tipos_notas_fkey FOREIGN KEY (tipo_nota_id)" +
		" REFERENCES tipos_notas (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_elementos" +
		" ADD CONSTRAINT elementos_fkey FOREIGN KEY (elemento_id)" +
		" REFERENCES elementos (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_elementos" +
		" ADD CONSTRAINT authors_fkey FOREIGN KEY (author_id)" +
		" REFERENCES users (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_elementos" +
		" ADD CONSTRAINT status_fkey FOREIGN KEY (status_id)" +
		" REFERENCES status (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	// PRODUTOS_ITENS
	_, err = db.Exec("ALTER TABLE produtos_itens" +
		" ADD CONSTRAINT entidades_fkey FOREIGN KEY (entidade_id)" +
		" REFERENCES entidades (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_itens" +
		" ADD CONSTRAINT ciclos_fkey FOREIGN KEY (ciclo_id)" +
		" REFERENCES ciclos (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_itens" +
		" ADD CONSTRAINT pilares_fkey FOREIGN KEY (pilar_id)" +
		" REFERENCES pilares (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_itens" +
		" ADD CONSTRAINT componentes_fkey FOREIGN KEY (componente_id)" +
		" REFERENCES componentes (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_itens" +
		" ADD CONSTRAINT elementos_fkey FOREIGN KEY (elemento_id)" +
		" REFERENCES elementos (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_itens" +
		" ADD CONSTRAINT itens_fkey FOREIGN KEY (item_id)" +
		" REFERENCES itens (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_itens" +
		" ADD CONSTRAINT authors_fkey FOREIGN KEY (author_id)" +
		" REFERENCES users (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE produtos_itens" +
		" ADD CONSTRAINT status_fkey FOREIGN KEY (status_id)" +
		" REFERENCES status (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	// TIPOS_NOTAS
	_, err = db.Exec("ALTER TABLE tipos_notas" +
		" ADD CONSTRAINT authors_fkey FOREIGN KEY (author_id)" +
		" REFERENCES users (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE tipos_notas" +
		" ADD CONSTRAINT status_fkey FOREIGN KEY (status_id)" +
		" REFERENCES status (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	// TIPOS_NOTAS_COMPONENTES
	_, err = db.Exec("ALTER TABLE tipos_notas_componentes" +
		" ADD CONSTRAINT tipos_notas_fkey FOREIGN KEY (tipo_nota_id)" +
		" REFERENCES tipos_notas (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE tipos_notas_componentes" +
		" ADD CONSTRAINT componentes_fkey FOREIGN KEY (componente_id)" +
		" REFERENCES componentes (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE tipos_notas" +
		" ADD CONSTRAINT authors_fkey FOREIGN KEY (author_id)" +
		" REFERENCES users (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE tipos_notas" +
		" ADD CONSTRAINT status_fkey FOREIGN KEY (status_id)" +
		" REFERENCES status (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	// USERS
	_, err = db.Exec("ALTER TABLE users " +
		" ADD CONSTRAINT roles_fkey FOREIGN KEY (role_id)" +
		" REFERENCES roles (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE users" +
		" ADD CONSTRAINT authors_fkey FOREIGN KEY (author_id)" +
		" REFERENCES users (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE users" +
		" ADD CONSTRAINT status_fkey FOREIGN KEY (status_id)" +
		" REFERENCES status (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	// JURISDICOES
	_, err = db.Exec("ALTER TABLE jurisdicoes" +
		" ADD CONSTRAINT entidades_fkey FOREIGN KEY (entidade_id)" +
		" REFERENCES entidades (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE jurisdicoes" +
		" ADD CONSTRAINT escritorios_fkey FOREIGN KEY (escritorio_id)" +
		" REFERENCES escritorios (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE jurisdicoes" +
		" ADD CONSTRAINT authors_fkey FOREIGN KEY (author_id)" +
		" REFERENCES users (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE jurisdicoes" +
		" ADD CONSTRAINT status_fkey FOREIGN KEY (status_id)" +
		" REFERENCES status (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	// MEMBROS
	_, err = db.Exec("ALTER TABLE membros" +
		" ADD CONSTRAINT usuarios_fkey FOREIGN KEY (usuario_id)" +
		" REFERENCES users (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE membros" +
		" ADD CONSTRAINT escritorios_fkey FOREIGN KEY (escritorio_id)" +
		" REFERENCES escritorios (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE membros" +
		" ADD CONSTRAINT authors_fkey FOREIGN KEY (author_id)" +
		" REFERENCES users (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("ALTER TABLE membros" +
		" ADD CONSTRAINT status_fkey FOREIGN KEY (status_id)" +
		" REFERENCES status (id) ON DELETE NO ACTION ON UPDATE NO ACTION ")
	if err != nil {
		log.Println(err.Error())
	}
}

func createPKey() {
	db.Exec("ALTER TABLE actions ADD CONSTRAINT actions_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE actions_status ADD CONSTRAINT actions_status_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE activities ADD CONSTRAINT activities_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE activities_roles ADD CONSTRAINT activities_roles_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE anotacoes ADD CONSTRAINT anotacoes_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE anotacoes_radares ADD CONSTRAINT anotacoes_radares_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE chamados ADD CONSTRAINT chamados_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE chamados_versoes ADD CONSTRAINT chamados_versoes_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE ciclos ADD CONSTRAINT ciclos_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE ciclos_entidades ADD CONSTRAINT ciclos_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE comentarios_chamados ADD CONSTRAINT comentarios_chamados_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE componentes ADD CONSTRAINT componentes_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE componentes_pilares ADD CONSTRAINT componentes_pilares_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE elementos ADD CONSTRAINT elementos_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE elementos_componentes ADD CONSTRAINT elementos_componentes_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE entidades ADD CONSTRAINT entidades_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE entidades_ciclos ADD CONSTRAINT entidades_ciclos_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE escritorios ADD CONSTRAINT escritorios_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE features ADD CONSTRAINT features_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE features_activities ADD CONSTRAINT features_activities_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE features_roles ADD CONSTRAINT features_roles_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE integrantes ADD CONSTRAINT integrantes_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE itens ADD CONSTRAINT itens_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE jurisdicoes ADD CONSTRAINT jurisdicoes_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE membros ADD CONSTRAINT membros_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE pilares ADD CONSTRAINT pilares_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE pilares_ciclos ADD CONSTRAINT pilares_ciclos_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE planos ADD CONSTRAINT planos_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE processos ADD CONSTRAINT processos_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE produtos_ciclos ADD CONSTRAINT produtos_ciclos_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE produtos_pilares ADD CONSTRAINT produtos_pilares_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE produtos_componentes ADD CONSTRAINT produtos_componentes_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE produtos_tipos_notas ADD CONSTRAINT produtos_tipos_notas_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE produtos_planos ADD CONSTRAINT produtos_planos_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE produtos_elementos ADD CONSTRAINT produtos_elementos_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE produtos_itens ADD CONSTRAINT produtos_itens_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE produtos_ciclos_historicos ADD CONSTRAINT produtos_ciclos_historicos_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE produtos_pilares_historicos ADD CONSTRAINT produtos_pilares_historicos_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE produtos_componentes_historicos ADD CONSTRAINT produtos_componentes_historicos_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE produtos_elementos_historicos ADD CONSTRAINT produtos_elementos_historicos_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE produtos_itens_historicos ADD CONSTRAINT produtos_itens_historicos_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE radares ADD CONSTRAINT radares_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE roles ADD CONSTRAINT roles_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE status ADD CONSTRAINT status_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE tipos_notas ADD CONSTRAINT tipos_notas_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE tipos_notas_componentes ADD CONSTRAINT tipos_notas_componentes_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE versoes ADD CONSTRAINT versoes_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE users ADD CONSTRAINT users_pkey PRIMARY KEY (id)")
	db.Exec("ALTER TABLE workflows ADD CONSTRAINT workflows_pkey PRIMARY KEY (id)")
}
