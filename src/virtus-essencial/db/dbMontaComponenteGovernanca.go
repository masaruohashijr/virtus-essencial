package db

import ()

func initGovernanca() ComponenteAux {
	var componente ComponenteAux
	var tiposNotas []TipoNotaAux
	var tipoNota TipoNotaAux
	var elementos []ElementoAux
	var elemento ElementoAux
	var itens []ItemAux
	var item ItemAux

	itens = make([]ItemAux, 0)
	item.Nome = "Governança"
	item.Descricao = ""
	itens = append(itens, item)
	elemento.Itens = itens
	elemento.Nome = "Governança"
	elementos = append(elementos, elemento)

	tipoNota.Nome = "Avaliação"
	tipoNota.Elementos = elementos
	tiposNotas = append(tiposNotas, tipoNota)
	componente.Nome = "Governança"
	componente.TiposNotas = tiposNotas

	return componente
}
