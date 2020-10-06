/**
 * Faz a formatação de um campo utilizando a mascara informada <br>
 * Caracteres: # - caracter a ser mascarado <br> | - separador de mascaras <br>
 * <p>
 * Exemplos: <br>
 * mascara simples: "###-####" mascara utilizando a mascara passada <br>
 * mascara composta: "###-####|####-####" mascara de acordo com o tamanho
 * (length) do valor passado <br>
 * mascara dinâmica: "[###.]###,##" multiplica o valor entre colchetes de acordo
 * com o length do valor para que a mascara seja dinâmica ex: ###.###.###.###,##
 * <br>
 * </p>
 * obs: Tratar o maxlength do objeto na página (a função não trata isso) <br>
 * 
 * @param mascara
 *            máscara utilizada
 * @param obj
 *            objeto a ser formatado
 * @param tamMax
 *            tamanho máximo do campo, incluindo pontos e vírgulas
 */
function mascara(event, mascara, obj, tamMax, permiteX, permiteNegativo) {
	var mascara_utilizar;
	var mascara_limpa;
	var temp;
	var i;
	var j;
	var caracter;
	var separador;
	var dif;
	var validar;
	var mult;
	var ret;
	var tam;
	var tvalor;
	var valorm;
	var masct;
	var valor = obj.value;
	tvalor = "";
	ret = "";
	caracter = "#";
	separador = "|";
	mascara_utilizar = "";
	if (tamMax == undefined) {
		tamMax = Number.MAX_VALUE;
	}
	// valor = trim(valor);
	if (valor == "")
		obj.value = valor;
	temp = mascara.split(separador);
	dif = 1000;
	valorm = valor;
	
	var valorNegativo = false;
	
	if (valor.substr(0,1) == '-') {
		valorNegativo = true;
	}
	
	// tirando mascara do valor já existente
	for (i = 0; i < valor.length; i++) {
		if (permiteX) {
			if (!isNaN(valor.substr(i, 1)) || valor.substr(i, 1) == 'X'
					|| (valor.substr(i, 1) == 'x')) {
				tvalor = tvalor + valor.substr(i, 1);
			}
		} else {
			if (!isNaN(valor.substr(i, 1))) {
				tvalor = tvalor + valor.substr(i, 1);
			}
		}

	}
	valor = tvalor;
	// formatar mascara dinamica
	for (i = 0; i < temp.length; i++) {
		mult = "";
		validar = 0;
		for (j = 0; j < temp[i].length; j++) {
			if (temp[i].substr(j, 1) == "]") {
				temp[i] = temp[i].substr(j + 1);
				break;
			}
			if (validar == 1)
				mult = mult + temp[i].substr(j, 1);
			if (temp[i].substr(j, 1) == "[")
				validar = 1;
		}
		for (j = 0; j < valor.length; j++) {
			temp[i] = mult + temp[i];
		}
	}
	// verificar qual mascara utilizar
	if (temp.length == 1) {
		mascara_utilizar = temp[0];
		mascara_limpa = "";
		for (j = 0; j < mascara_utilizar.length; j++) {
			if (mascara_utilizar.substr(j, 1) == caracter) {
				mascara_limpa = mascara_limpa + caracter;
			}
		}
		tam = mascara_limpa.length;
	} else {
		// limpar caracteres diferente do caracter da máscara
		for (i = 0; i < temp.length; i++) {
			mascara_limpa = "";
			for (j = 0; j < temp[i].length; j++) {
				if (temp[i].substr(j, 1) == caracter) {
					mascara_limpa = mascara_limpa + caracter;
				}
			}
			if (valor.length > mascara_limpa.length) {
				if (dif > (valor.length - mascara_limpa.length)) {
					dif = valor.length - mascara_limpa.length;
					mascara_utilizar = temp[i];
					tam = mascara_limpa.length;
				}
			} else if (valor.length < mascara_limpa.length) {
				if (dif > (mascara_limpa.length - valor.length)) {
					dif = mascara_limpa.length - valor.length;
					mascara_utilizar = temp[i];
					tam = mascara_limpa.length;
				}
			} else {
				mascara_utilizar = temp[i];
				tam = mascara_limpa.length;
				break;
			}
		}
	}
	// validar tamanho da mascara de acordo com o tamanho do valor
	if (valor.length > tam) {
		valor = valor.substr(0, tam);
	} else if (valor.length < tam) {
		masct = "";
		j = valor.length;
		for (i = mascara_utilizar.length - 1; i >= 0; i--) {
			if (j == 0)
				break;
			if (mascara_utilizar.substr(i, 1) == caracter) {
				j--;
			}
			masct = mascara_utilizar.substr(i, 1) + masct;
		}
		mascara_utilizar = masct;
	}
	// mascarar
	j = mascara_utilizar.length - 1;
	for (i = valor.length - 1; i >= 0; i--) {
		if (mascara_utilizar.substr(j, 1) != caracter) {
			ret = mascara_utilizar.substr(j, 1) + ret;
			j--;
		}
		ret = valor.substr(i, 1) + ret;
		j--;
	}
	
	if (permiteNegativo) {
		if ((event.keyCode == 109 || event.keyCode == 189) || valorNegativo) {
			obj.value = "-" + ret.substr(0, tamMax);
		} else {
			obj.value = ret.substr(0, tamMax);
		}
	} else {
		obj.value = ret.substr(0, tamMax);
	}
	
}
