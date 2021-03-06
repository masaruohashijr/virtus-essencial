function editEscritorio(e) {
	resetDetalhesEscritorio();
    var editForm = document.getElementById('edit-form');
    editForm.style.display = 'block';
    var escritorioId = e.parentNode.parentNode.childNodes[3].innerText;
    var escritorioAbreviatura = e.parentNode.parentNode.childNodes[5].innerText;
    var escritorioNome = e.parentNode.parentNode.childNodes[7].innerText;
    var escritorioDescricao = e.parentNode.parentNode.childNodes[9].innerText;
	if(e.parentNode.parentNode.childNodes[11].childNodes.length > 1){
	    var escritorioChefeId = e.parentNode.parentNode.childNodes[11].childNodes[1].value;
	    document.getElementById('EscritorioChefeForUpdate').value = escritorioChefeId;
	}
	document.getElementById('EscritorioIdForUpdate').value = escritorioId;
    document.getElementById('EscritorioNomeForUpdate').value = escritorioNome;
    document.getElementById('EscritorioDescricaoForUpdate').value = escritorioDescricao;
    document.getElementById('EscritorioAbreviaturaForUpdate').value = escritorioAbreviatura;
    document.getElementById('EscritorioNomeForUpdate').focus();
}

function editJurisdicaoEscritorio(e) {
	resetDetalhesEscritorio();
    var editForm = document.getElementById('edit-jurisdicao-escritorio-form');
    editForm.style.display = 'block';
    var escritorioId = e.parentNode.parentNode.childNodes[3].innerText;
	var escritorioAbreviatura = e.parentNode.parentNode.childNodes[5].innerText;
    var escritorioNome = e.parentNode.parentNode.childNodes[7].innerText;
    var escritorioDescricao = e.parentNode.parentNode.childNodes[9].innerText;
	// Verifica se há campo 'hidden' para seu valor ser lido.
	if(e.parentNode.parentNode.childNodes[11].childNodes.length > 1){
	    var escritorioChefeId = e.parentNode.parentNode.childNodes[11].childNodes[1].value;
	    document.getElementById('EscritorioChefeJUForUpdate').value = escritorioChefeId;
	}
	document.getElementById('EscritorioIdJUForUpdate').value = escritorioId;
    document.getElementById('EscritorioNomeJUForUpdate').value = escritorioNome;
    document.getElementById('EscritorioDescricaoJUForUpdate').value = escritorioDescricao;
    document.getElementById('EscritorioAbreviaturaJUForUpdate').value = escritorioAbreviatura;
	loadJurisdicoesByEscritorioId(escritorioId);
}

function editMembrosEscritorio(e) {
	resetDetalhesEscritorio();
    var editForm = document.getElementById('edit-equipe-form');
    editForm.style.display = 'block';
    var escritorioId = e.parentNode.parentNode.childNodes[3].innerText;
	var escritorioAbreviatura = e.parentNode.parentNode.childNodes[5].innerText;
    var escritorioNome = e.parentNode.parentNode.childNodes[7].innerText;
    var escritorioDescricao = e.parentNode.parentNode.childNodes[9].innerText;
	if(e.parentNode.parentNode.childNodes[11].childNodes.length > 1){
	    var escritorioChefeId = e.parentNode.parentNode.childNodes[11].childNodes[1].value;
	    document.getElementById('EscritorioChefeESForUpdate').value = escritorioChefeId;
	}
	document.getElementById('EscritorioIdESForUpdate').value = escritorioId;
    document.getElementById('EscritorioNomeESForUpdate').value = escritorioNome;
    document.getElementById('EscritorioDescricaoESForUpdate').value = escritorioDescricao;
    document.getElementById('EscritorioAbreviaturaESForUpdate').value = escritorioAbreviatura;
	loadMembrosByEscritorioId(escritorioId);
}

function deleteEscritorio(e) {
    var deleteForm = document.getElementById('delete-form');
    deleteForm.style.display = 'block';
    var escritorioId = e.parentNode.parentNode.childNodes[3].innerText;
    document.getElementById('EscritorioIdToDelete').value = escritorioId;
}

function resetDetalhesEscritorio(){
	document.getElementById('formulario-jurisdicao-escritorio-edit').reset();
	document.getElementById('formulario-jurisdicao-create').reset();
	document.getElementById('formulario-jurisdicao-edit').reset();
	document.getElementById('formulario-membro-escritorio-edit').reset();
	document.getElementById('formulario-membro-create').reset();
	document.getElementById('formulario-membro-edit').reset();
}

function loadJurisdicoesByEscritorioId(escritorioId){
	var xmlhttp;
	xmlhttp=new XMLHttpRequest();
	xmlhttp.onreadystatechange=function()
	{
			if (xmlhttp.readyState==4 && xmlhttp.status==200)
			{
				var jurisdicoesJson = JSON.parse(xmlhttp.responseText);
				wipeRows("table-jurisdicoes-edit")
				jurisdicoes = [];
				for(order = 0;jurisdicoesJson != null && order<jurisdicoesJson.length;order++){
					jurisdicoes[order]=jurisdicoesJson[order];
					addJurisdicaoRow("table-jurisdicoes-edit");
				}
			}
	}
	xmlhttp.open("GET","/loadJurisdicoesByEscritorioId?escritorioId="+escritorioId,true);
	xmlhttp.send();
}

function loadMembrosByEscritorioId(escritorioId){
	var xmlhttp;
	xmlhttp=new XMLHttpRequest();
	xmlhttp.onreadystatechange=function()
	{
			if (xmlhttp.readyState==4 && xmlhttp.status==200)
			{
				var membrosJson = JSON.parse(xmlhttp.responseText);
				wipeRows("table-membros-edit")
				membros = [];
				for(order = 0;membrosJson != null && order<membrosJson.length;order++){
					membros[order]=membrosJson[order];
					removerOpcoesUsuarios("UsuarioMEForInsert",membros[order].usuarioId);
					addMembroRow("table-membros-edit");
				}
			}
	}
	xmlhttp.open("GET","/loadMembrosByEscritorioId?escritorioId="+escritorioId,true);
	xmlhttp.send();
}

function openCreateEscritorio(){
	document.getElementById('create-form').style.display='block';
	document.getElementById('escritorioNomeForInsert').focus();
	
}