function editElemento(e) {
    var editForm = document.getElementById('edit-form');
    editForm.style.display = 'block';
    var elementoId = e.parentNode.parentNode.childNodes[3].innerText;
    var elementoNome = e.parentNode.parentNode.childNodes[5].innerText;
    var elementoDescricao = e.parentNode.parentNode.childNodes[7].innerText;
    var elementoReferencia = e.parentNode.parentNode.childNodes[9].innerText;
    var elementoAutor = e.parentNode.parentNode.childNodes[11].innerText;
    var elementoCriadoEm = e.parentNode.parentNode.childNodes[13].innerText;
    var elementoStatusId = e.parentNode.parentNode.childNodes[15].childNodes[1].value;
    var elementoStatus = e.parentNode.parentNode.childNodes[15].innerText;
	document.getElementById('ElementoIdForUpdate').value = elementoId;
    document.getElementById('ElementoNomeForUpdate').value = elementoNome;
    document.getElementById('ElementoDescricaoForUpdate').value = elementoDescricao;
    document.getElementById('ElementoReferenciaForUpdate').value = elementoReferencia;
    document.getElementById('ElementoAutorForUpdate').value = elementoAutor;
    document.getElementById('ElementoCriadoEmForUpdate').value = elementoCriadoEm;
    //document.getElementById('ElementoStatusIdForUpdate').value = elementoStatusId;
    document.getElementById('ElementoStatusForUpdate').value = elementoStatus;
	document.getElementById('ElementoNomeForUpdate').focus();
	// AJAX 
	loadItensByElementoId(elementoId);
	loadAllowedActions('elemento',elementoStatusId);
	let feats = loadAvailableFeatures('elemento',elementoStatusId);
	// FIM AJAX
	for(i = 0; feats && i<feats.length; i++){
		if(feats[i].code == 'createItem'){
			document.getElementById(contexto+"-form-item-btn").style.visibility = "visible";
			var btns = document.getElementsByTagName("input");
			for(i=0;i<btns.length;i++){
				if(btns[i].value == "Editar" || btns[i].value == "Apagar" ){
					btns[i].style.visibility = "visible";
				}
			}
			return;
		}
	}
	document.getElementById(contexto+"-form-item-btn").style.visibility = "hidden";
	var btns = document.getElementsByTagName("input");
	for(i=0;i<btns.length;i++){
		if(btns[i].value == "Editar" || btns[i].value == "Apagar" ){
			btns[i].style.visibility = "hidden";
		}
	}
}

function deleteElemento(e) {
    var deleteForm = document.getElementById('delete-form');
    deleteForm.style.display = 'block';
    var elementoId = e.parentNode.parentNode.childNodes[3].innerText;
    document.getElementById('ElementoIdToDelete').value = elementoId;
}

function openCreateElemento(){
	limparCamposItemForm('create'); 
	document.getElementById('create-form').style.display='block';
	document.getElementById('NomeElementoForInsert').focus();
}