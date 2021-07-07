function resetChamadoForms(){
	document.getElementById('formulario-create').reset();
	document.getElementById('formulario-edit').reset();
}


function openCreateChamado(){
	document.getElementById('create-form').style.display='block';
	document.getElementById('TipoChamadoForInsert').focus();
}

function deleteChamado(e) {
    let deleteForm = document.getElementById('delete-form');
    deleteForm.style.display = 'block';
    let chamadoId = e.parentNode.parentNode.childNodes[3].innerText.substr(7);
    document.getElementById('ChamadoIdForDelete').value = chamadoId;
}

function editChamado(e){
	resetChamadoForms();
    let editForm = document.getElementById('edit-form');
    editForm.style.display = 'block';
    let chamadoId = e.parentNode.parentNode.childNodes[3].innerText.substr(7);
    let chamadoTipo = e.parentNode.parentNode.childNodes[5].innerText;
    let chamadoPrioridade = e.parentNode.parentNode.childNodes[7].innerText;
    let chamadoTitulo = e.parentNode.parentNode.childNodes[9].innerText;
    let chamadoDescricao = e.parentNode.parentNode.childNodes[11].innerText;
    let chamadoAcompanhamento = e.parentNode.parentNode.childNodes[11].childNodes[1].value;
    let chamadoResponsavel = e.parentNode.parentNode.childNodes[13].childNodes[1].value;
    let chamadoRelator = e.parentNode.parentNode.childNodes[15].childNodes[1].value;
    let chamadoIniciaEm = e.parentNode.parentNode.childNodes[17].innerText;
    let chamadoProntoEm = e.parentNode.parentNode.childNodes[19].innerText;
    let chamadoEstimativa = e.parentNode.parentNode.childNodes[21].innerText;
    let chamadoAutor = e.parentNode.parentNode.childNodes[23].innerText;
    let chamadoCriadoEm = e.parentNode.parentNode.childNodes[25].innerText;
    let chamadoStatus = e.parentNode.parentNode.childNodes[27].innerText;
    let chamadoStatusId = '0';
	if(e.parentNode.parentNode.childNodes[27].childNodes[1]){
		chamadoStatusId = e.parentNode.parentNode.childNodes[27].childNodes[1].value;
	} else {
		chamadoStatusId = e.parentNode.parentNode.childNodes[27].childNodes[0].value;
	}
	let titModal = document.getElementById('tituloModal');
	titModal.innerText = "Editar chamado VIRTUS-" +chamadoId;
	document.getElementById('IdForUpdate').value = chamadoId;
	chamadoTipo = parseNome2Valor(chamadoTipo);
    chamadoPrioridade = parseNome2Valor(chamadoPrioridade);
    document.getElementById('TipoChamadoForUpdate').value = chamadoTipo;
    document.getElementById('TituloChamadoForUpdate').value = chamadoTitulo;
    document.getElementById('DescricaoChamadoForUpdate').value = chamadoDescricao;
    document.getElementById('AcompanhamentoChamadoForUpdate').value = chamadoAcompanhamento;
    document.getElementById('PrioridadeChamadoForUpdate').value = chamadoPrioridade;
    document.getElementById('RelatorChamadoForUpdate').value = chamadoRelator;
    document.getElementById('ResponsavelChamadoForUpdate').value = chamadoResponsavel;
    document.getElementById('EstimativaChamadoForUpdate').value = chamadoEstimativa;
    document.getElementById('IniciaEmChamadoForUpdate').value = formatarData(chamadoIniciaEm);
    document.getElementById('ProntoEmChamadoForUpdate').value = formatarData(chamadoProntoEm);
	document.getElementById('AuthorNameChamadoForUpdate').value = chamadoAutor;
    document.getElementById('CriadoEmChamadoForUpdate').value = chamadoCriadoEm;
    document.getElementById('StatusForUpdate').value = chamadoStatus;
    document.getElementById('TituloChamadoForUpdate').focus();
	// AJAX 
	loadAllowedActions('chamado',chamadoStatusId);
}
