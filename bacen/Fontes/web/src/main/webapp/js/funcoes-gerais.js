/**
 * Objeto responsavel por atualizar texto em componente de texto informativo.
 * parametros: - textoInformativo: objeto html para exibicao de mensagem
 * informativa; - mensagem: mensagem a ser exibida.
 */
var util = {
	atualizarTexto : function(textoInformativo, mensagem) {
		if (document.all) {
			textoInformativo.innerText = mensagem;
		} else {
			textoInformativo.textContent = mensagem;
		}
	}
}

/**
 * Funcao responsavel por inserir a mascara ###.###.###-## para campos CPF.
 */
function formatarCPF(campo) {
	texto = campo.value;
	texto = texto.replace(/\D/g, '');
	if (texto.length > 3 && texto.length <= 6) {
		texto = texto.slice(0, 3) + "." + texto.slice(3);
	} else if (texto.length > 6 && texto.length <= 9) {
		texto = texto.slice(0, 3) + "." + texto.slice(3, 6) + "."
				+ texto.slice(6);
	} else if (texto.length > 9) {
		texto = texto.slice(0, 3) + "." + texto.slice(3, 6) + "."
				+ texto.slice(6, 9) + "-" + texto.slice(9);
	}
	campo.value = texto;
}

/**
 * Funcao responsavel por inserir mascaras para campos num�ricos.
 * 
 * @param campo
 *            Campo cujo valor receber� a m�scara.
 * @param mascara
 *            Padr�o da m�scara. Na m�scara, o caractere # representa um d�gito,
 *            enquanto que qualquer outro caractere ser� impresso literalmente.
 * 
 * Exemplo: Se o valor do campo for '12345', e a m�scara for '##.##e#', o valor
 * com a m�scara ficar� '12.34e5'.
 * 
 * @return Valor com a m�scara aplicada.
 */
function formatarCampoNumerico(campo, mascara) {
	var texto = campo.value;
	texto = texto.replace(/\D/g, '');

	// Se a m�scara for vazia, deixa o texto sem modifica��es.
	if (mascara.replace(/ /g, '').length == 0 || texto.length == 0) {
		campo.value = "";
		return;
	}

	// Trunca o texto no tamanho m�ximo (definido pelo n�mero de
	// caracteres num�ricos ('#') na m�scara.
	tamanhoMax = contarCaracteresNumericosNaString(mascara);
	if (texto.length > tamanhoMax) {
		texto = texto.substring(0, tamanhoMax);
	}

	// Substitui os caracteres num�ricos da m�scara por seus correspondentes no
	// texto.
	var idxtxt = texto.length - 1;
	var idxmas = mascara.lastIndexOf('#');
	while (idxtxt != -1) {
		mascara = mascara.substring(0, idxmas) + texto.charAt(idxtxt)
				+ mascara.substring(idxmas + 1);
		idxmas = mascara.lastIndexOf('#');
		idxtxt--;
	}
	campo.value = mascara.substring(idxmas + 1);
}

// formata o campo numerico mas com a opcao de ir para o campo seguinte assim
// que a mascara tiver sido informada
function formatarCampoNumericoComAutoTab(campo, mascara, autoTab) {
	formatarCampoNumerico(campo, mascara);

	if (campo.value.length == mascara.length) {

		var index = -1;
		var i = 0;
		var found = false;

		while (i < campo.form.length && index == -1) {
			if (campo.form[i] == campo) {
				index = i;
			} else {
				i++;
			}
		}

		if (index != -1 && autoTab != null && autoTab == true) {
			campo.form[(index + 1) % campo.form.length].focus();
		}
	}
}

// Fun��o utilizada para formatar o campo Ag�ncia dos dados banc�rios da Pessoa
// F�sica.
function formatarAgencia(campo) {
	var cv = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	var s = filtrarValoresValidos(campo.value, cv);
	campo.value = formatarMascara(s, 6, [ 5 ], [ '-' ]);
}

// Fun��o utilizada para formatar o campo Conta dos dados banc�rios da Pessoa
// F�sica.
function formatarConta(campo) {
	var cv = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	var s = filtrarValoresValidos(campo.value, cv);
	campo.value = formatarMascara(s, 13, [ 3, 6, 9, 12, 15 ], [ '.', '.', '.',
			'-' ]);
}

// Formata uma mascara baseada nos dados passados. Vide formatarConta acima.
// valor - a ser formatado
// pontos de formata��o
// quais caracteres devem ocupar os pontos de formata��o.
function formatarMascara(valor, maximo, pontos, caracteres) {
	var s = valor;
	if (s.length > maximo) {
		s = s.substring(0, maximo);
	}
	var sZeros = s;
	while (sZeros.length < maximo) {
		sZeros = '0' + sZeros;
	}
	var res = "";
	var simbolos = 0;
	var i, j = 0;
	for (i = 0; i < sZeros.length; i++) {
		var c = sZeros.charAt(i);
		res = res.concat(c);
		if (j < pontos.length) {
			if ((i + 1) == pontos[j]) {
				res = res.concat(caracteres[j]);
				j++;
				if (pontos[j] + s.length > sZeros.length) {
					simbolos++;
				}
			}
		}
	}
	var zeros = sZeros.length - s.length;
	var naoUsados = 0;
	for (n = 0; n < pontos.length; n++) {
		if (pontos[n] <= zeros) {
			naoUsados++;
		}
	}
	return res.substring(zeros + naoUsados, res.length);
}

// elimina todo caractere n�o contido no conjunto "validos"
function filtrarValoresValidos(valor, validos) {
	s = "";
	for (var i = 0; i < valor.length; i++) {
		if (validos.indexOf(valor.substring(i, i + 1)) >= 0) {
			s = s + valor.substring(i, i + 1);
		}
	}
	return s;
}

/**
 * Conta o n�mero de caracteres '#' no texto.
 * 
 * @param texto
 *            texto onde est�o os caracteres '#'
 * @return n�mero de caracteres '#' no texto
 */
function contarCaracteresNumericosNaString(texto) {
	conta = 0;
	for (i = texto.length - 1; i >= 0; i--) {
		if (texto.charAt(i) == '#') {
			conta++;
		}
	}
	return conta;
}

/**
 * Funcao responsavel por limitar a quantidade de caracteres em um textarea.
 * parametros: - texto_res: textarea a ser limitado; - limite: inteiro que
 * informa o limite maximo de caracteres.
 */
function limitarCaracteres(texto_res, limite) {
	digitados = texto_res.value.length;
	restantes = limite - digitados;
	if (restantes < 0) {
		texto_res.value = texto_res.value.substr(0, limite);
	} else {
		mensagem = restantes + ' caracteres restantes (limite ' + limite + ')';
		textoInformativo = document.getElementById("contador");
		util.atualizarTexto(textoInformativo, mensagem);
	}
}

/**
 * Fun��o respons�vel por setar o foco no primeiro campo do formul�rio
 */
function setaFoco() {

	var forms = document.forms;

	if (forms.length > 0) {

		// Selecionar o primeiro form:
		var frm = forms[0];

		for (var i = 0; i < frm.length; i++) {

			var input = frm.elements[i];

			if (input.type == "text" || input.type == "textarea") {
				if (input.readOnly == false && input.disabled == false) {
					input.focus();
					break;
				}
			} else if (input.type == "radio") {
				if (input.readOnly == false && input.disabled == false) {
					input.focus();
					break;
				}
			} else if (input.type == "checkbox") {
				if (input.readOnly == false && input.disabled == false) {
					input.focus();
					break;
				}
			} else if (input.type == "button" || input.type == "submit") {
				if (input.disabled == false) {
					input.focus();
					break;
				}
			} else if (input.type == "select-one"
					|| input.type == "select-multiple") {
				if (input.disabled == false) {
					input.focus();
					break;
				}
			}
		} // for

	}
}

// Fun��o copiada padraoInterface.js visando a modifica��o o src da imagem para
// uma url relativa.
function expandirPainel(txtNode, txtTipo, obj) {

	// Efeito abrir-fechar grupo de Tags Html
	// Navegadores NS 6.1 IE 5.5

	var txtTipo = 'table', estAtivo = 'grpAtivo', estInativo = 'grpInativo';
	path = document.getElementById(obj);
	queNode = document.getElementById(txtNode);

	if (queNode.className == 'grpInativo') {
		queNode.className = 'grpAtivo'
		path.src = '/img/menos-p.gif'
	} else {
		queNode.className = 'grpInativo'
		path.src = '/img/mais-p.gif'
	}
}

function formatadorMatricula(campo, e) {
	var tecla = e ? e.keyCode : 0;
	var validos = "0123456789xX";
	var s = filtraValoresValidos(campo.value, validos);
	if (s.length > 8)
		s = s.substr(0, 8); // limita a 8 digitos

	var valido = false; // valida s� o tamanho
	var tamanho = s.length;
	if (tamanho >= 1 && tamanho < 4) {
		s = s.substr(0, 1) + '.' + s.substr(1, 3);
	} else if (s.length >= 4 && s.length < 7) {
		s = s.substr(0, 1) + '.' + s.substr(1, 3) + '.' + s.substr(4, 3);
	} else if (s.length >= 7) {
		s = s.substr(0, 1) + '.' + s.substr(1, 3) + '.' + s.substr(4, 3) + "-"
				+ s.substr(7, 1);
	}

	var formata = (tecla != 8);
	if (formata)
		campo.value = s;

	return valido;
}

/**
 * Funcao respons�vel por filtrar os valores que n�o s�o v�lidos
 */
function filtraValoresValidos(valor, validos) { // elimina todo caractere n�o
	// contido no conjunto "validos"
	s = "";
	for (var i = 0; i < valor.length; i++) {
		if (validos.indexOf(valor.substring(i, i + 1)) >= 0) {
			s = s + valor.substring(i, i + 1);
		}
	}
	return s;

}

/**
 * Obt�m a posi��o do cursor em um determinado input text
 * 
 * @param ctrl
 *            input text
 * @return
 */
function getPosicaoFinalDaSelecao(ctrl) {
	var CaretPos = 0;
	// IE Support
	if (document.selection) {

		ctrl.focus();
		var Sel = document.selection.createRange();
		var SelLength = document.selection.createRange().text.length;
		Sel.moveStart('character', -ctrl.value.length);
		CaretPos = SelLength - 1;
	}
	// Firefox support
	else if (ctrl.selectionStart || ctrl.selectionStart == '0')
		CaretPos = (ctrl.selectionEnd - 1) - ctrl.selectionStart;

	return CaretPos;
}

/*******************************************************************************
 * Fun��es para marcar e desmarcar conjutos de checkbox na fun��o
 * selecionarChecks(formulario, isSelecionador) basta passar o form e o elemento
 * check que for controlador.
 */
function selecionar() {
	if (document.form.MarcarDesmarcarTodos.checked)
		document.onclick = selecionar_tudo();
	else
		document.onclick = deselecionar_tudo();
}

function selecionar_tudo() {
	for (i = 0; i < document.form.elements.length; i++) {
		if (document.form.elements[i].type == "checkbox") {
			document.form.elements[i].checked = 1
		}
	}
}

function deselecionar_tudo() {
	for (i = 0; i < document.form.elements.length; i++) {
		if (document.form.elements[i].type == "checkbox") {
			document.form.elements[i].checked = 0
		}
	}

}

function soNumerosSinalSubtracao(elemento) {
	$(elemento).bind("keyup mousemove", function(e) {
		e.preventDefault();
		var expressao = /[^\.\^\-\^\d]/g;
		$(this).val($(this).val().replace(expressao, ''));
		$(this).maskMoney({thousands:'.', decimal:'.', precision: 0, allowNegative: true , allowZero: false, defaultZero: false});
		$(this).val($(this).val().replace('-0', ''));
	});
}

function Onlynumbers(e) {
	var unicode = e.charCode ? e.charCode : e.keyCode;
	if (unicode != 8 && unicode != 9) {
		if (unicode<48||unicode>57) {
			return false
		}
	}
}
