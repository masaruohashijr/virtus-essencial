package db

import ()

func initEconomicoFinanceiro(nome string) ComponenteAux {
	var componente ComponenteAux
	var tiposNotas []TipoNotaAux
	var tipoNota TipoNotaAux
	var elementoA ElementoAux
	var elementoB ElementoAux
	var elementoC ElementoAux
	var elementos []ElementoAux
	var itens []ItemAux
	var item ItemAux

	itens = make([]ItemAux, 0)
	item.Nome = "A1"
	item.Descricao = ""
	itens = append(itens, item)
	item.Nome = "A2"
	item.Descricao = ""
	itens = append(itens, item)
	elementoA.Itens = itens
	elementoA.Nome = "A"
	elementos = append(elementos, elementoA)

	itens = make([]ItemAux, 0)
	item.Nome = "B1"
	item.Descricao = ""
	itens = append(itens, item)
	item.Nome = "B2"
	item.Descricao = ""
	itens = append(itens, item)
	elementoB.Itens = itens
	elementoB.Nome = "B"
	elementos = append(elementos, elementoB)

	itens = make([]ItemAux, 0)
	item.Nome = "C1"
	item.Descricao = ""
	itens = append(itens, item)
	item.Nome = "C2"
	item.Descricao = ""
	itens = append(itens, item)
	elementoC.Itens = itens
	elementoC.Nome = "C"
	elementos = append(elementos, elementoC)

	tipoNota.Nome = "Avaliação"
	tipoNota.Elementos = elementos
	tiposNotas = append(tiposNotas, tipoNota)

	componente.Nome = nome
	componente.TiposNotas = tiposNotas
	return componente
}
