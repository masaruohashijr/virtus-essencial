function atualizarAlerta(elementoId, isVisible) {
    var alerta = document.getElementById(elementoId);

    if (isVisible) {
        alerta.style.visibility = 'visible';
    } else {
        alerta.style.visibility = 'hidden';
    }
}

function atualizarAlertaPrincipal(idAlertaPrincipal, idElementosAlerta) {
	var isAlgumAlertaVisivel = false;

	for (i = 0; i < idElementosAlerta.length; i++) {
		var alerta = document.getElementById(idElementosAlerta[i]);

		if (alerta != null) {
		    isAlgumAlertaVisivel = isAlgumAlertaVisivel || (alerta.style.visibility == 'visible');
		}
	}

	atualizarAlerta(idAlertaPrincipal, isAlgumAlertaVisivel);
}

function atualizarBotoesVoltar(idBotaoVoltar, idBotaoVoltarSemSalvar, mostrarBotaoVoltarSemSalvar) {
	var botaoVoltar = document.getElementById(idBotaoVoltar);
	var botaoVoltarSemSalvar = document.getElementById(idBotaoVoltarSemSalvar);

	if (mostrarBotaoVoltarSemSalvar) {
		botaoVoltar.style.visibility = 'hidden';
		botaoVoltarSemSalvar.style.visibility = 'visible';
	} else {
		botaoVoltar.style.visibility = 'visible';
		botaoVoltarSemSalvar.style.visibility = 'hidden';
	}
}

function atualizarBotoesAlerta(idBotaoVoltar, idBotaoVoltarSemSalvar, idElementosAlerta) {
	var isAlgumAlertaVisivel = false;

	for (i = 0; i < idElementosAlerta.length; i++) {
		var alerta = document.getElementById(idElementosAlerta[i]);

		if (alerta != null) {
		    isAlgumAlertaVisivel = isAlgumAlertaVisivel || (alerta.style.visibility == 'visible');
		}
	}

	atualizarBotoesVoltar(idBotaoVoltar, idBotaoVoltarSemSalvar, isAlgumAlertaVisivel);
}

function atualizarBotoesSalvarInformacoes(idBotaoSalvar, idBotaoSalvarDesabilitado, mostrarBotaoSalvar) {
	var botaoSalvar = document.getElementById(idBotaoSalvar);
	var botaoSalvarDesabilitado = document.getElementById(idBotaoSalvarDesabilitado);

	if (mostrarBotaoSalvar) {
		botaoSalvar.style.visibility = 'visible';
		botaoSalvarDesabilitado.style.visibility = 'hidden';
	} else {
		botaoSalvar.style.visibility = 'hidden';
		botaoSalvarDesabilitado.style.visibility = 'visible';
	}
}
