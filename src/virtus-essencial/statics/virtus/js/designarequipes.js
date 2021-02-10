function validarDesignarEquipe(e){
	if (e.parentNode.parentNode.childNodes[9].childNodes[1].length == 0) {
		// Na tabela de Designação de Equipes
		// campo Select dos ciclos da entidade na linha da tabela
		console.log(false);
		return false;	
	} else {
		console.log(true);
		return true;
	}
}

function atualizarSupervisorEquipe(e){
	let usuarioId = e.value;
	for (let [k, v] of subordinacoesMap) {
		if(k == usuarioId){
		  removeOpcoesDiferentesDe('SupervisorEquipeForUpdate', v);
		}
	}
}

function removeOpcoesDiferentesDe(idCampo, valor){
	let campo = document.getElementById(idCampo);
	console.log('remove todas as opcoes diferentes de: '+valor);
	for(n=0;n<campo.options.length;n++){
		let opc = campo.options[n];
		console.log(parseInt(opc.value));
		if(parseInt(opc.value) != parseInt(valor) && opc.value != ''){
			campo.remove(n);
		}
	}
}

function atualizarMembrosEquipe(e){
	let supervisorId = e.value;
	for (i=0;i<integrantes.length;i++) {
		for (let [k, v] of subordinacoesMap) {
			if(integrantes[i].usuarioId == k && v != supervisorId){
				let errorMsg = "O auditor "+membrosMap.get(k)+" já está subordinado ao supervisor "+supervisoresMap.get(""+supervisorEquipe)+".";
				errorMsg += "\nPor favor, remova o auditor integrante antes de substituir o supervisor.";
				document.getElementById("Errors").innerText = errorMsg;
				document.getElementById("error-message").style.display="block";
				document.getElementById('SupervisorEquipeForUpdate').value = parseInt(supervisorEquipe);
				return;
			}
		}
	}
	for (let [k, v] of subordinacoesMap) {
		if(v != supervisorId){
		  console.log('k: '+k);
		  removeOpcao('UsuarioForInsert', k);
		  removeOpcao('UsuarioMEForUpdate', k);
		}
	}
	for (let [k, v] of subordinacoesMap) { 
		if(v == supervisorId){
		  adicionaSeNaoExistir('UsuarioForInsert', k);	
		  adicionaSeNaoExistir('UsuarioMEForUpdate', k);	
		}
	}
	if(supervisorId==''){
		for (let [k, v] of subordinacoesMap) { 
		  adicionaSeNaoExistir('UsuarioForInsert', k);	
		  adicionaSeNaoExistir('UsuarioMEForUpdate', k);	
		}
	}
	return true;
}

function removeOpcao(idCampo, valor){
	let campo = document.getElementById(idCampo);
	console.log('remove opcao com valor: '+valor);
	for(n=0;n<campo.options.length;n++){
		let opc = campo.options[n];
		console.log(parseInt(opc.value));
		if(parseInt(opc.value) == parseInt(valor)){
			campo.remove(n);
		}
	}
}

function adicionaSeNaoExistir(idCampo, valor){
	let campo = document.getElementById(idCampo);
	let naoEncontrado = true;
	for(n=0;n<campo.options.length;n++){
		let opc = campo.options[n];
		if(parseInt(opc.value) == parseInt(valor)){
			naoEncontrado = false;
		}
	}
	if(naoEncontrado){
		let newOption = new Option(membrosMap.get(''+valor),valor);
		campo.add(newOption,0);
	}
}

function exibirMsgAlerta(){
	
}