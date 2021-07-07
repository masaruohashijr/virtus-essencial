function submeterListEntidadesForm(e, formId){
	console.log(e.parentNode.parentNode.childNodes[3].childNodes[0].value);
	console.log(e.parentNode.parentNode.childNodes[3].childNodes[1].value);
	//alert(e.parentNode.parentNode.childNodes[3].innerText);
	console.log(e.parentNode.parentNode.childNodes[9].childNodes[1].value);
	//alert(e.parentNode.parentNode.childNodes[7].innerText);
	console.log(document.getElementById("EntidadeId").value);
	//alert(document.getElementById("EntidadeId").value);
	console.log(document.getElementById("CicloId").value);
	//alert(document.getElementById("CicloId").value);
	document.getElementById("EntidadeId").value=e.parentNode.parentNode.childNodes[3].childNodes[0].value;
	document.getElementById("CicloId").value=e.parentNode.parentNode.childNodes[9].childNodes[1].value;
	document.getElementById(formId).submit();
}

function submeterPendencias(e, formId){
	document.getElementById("EntidadeId").value=e.parentNode.parentNode.childNodes[3].childNodes[1].value;
	document.getElementById("CicloId").value=e.parentNode.parentNode.childNodes[5].childNodes[1].value;
	document.getElementById("PilarId").value=e.parentNode.parentNode.childNodes[7].childNodes[1].value;
	document.getElementById("ComponenteId").value=e.parentNode.parentNode.childNodes[9].childNodes[1].value;
	document.getElementById(formId).submit();
}

function viewEntidade(e) {
	resetEntidadeForms();
    var editForm = document.getElementById('view-form');
    editForm.style.display = 'block';
    var entidadeId = e.parentNode.parentNode.childNodes[3].childNodes[0].value;
	console.log('entidadeId: '+entidadeId);
    var entidadeCodigo = e.parentNode.parentNode.childNodes[3].innerText;
	console.log('entidadeCodigo: '+entidadeCodigo);
    var entidadeSigla = e.parentNode.parentNode.childNodes[5].innerText;
	console.log('entidadeSigla: '+entidadeSigla);
    var entidadeNome = e.parentNode.parentNode.childNodes[7].innerText;
	console.log('entidadeNome: '+entidadeNome);
    var entidadeDescricao = e.parentNode.parentNode.childNodes[7].childNodes[1].value;
	console.log('entidadeDescricao: '+entidadeDescricao);
    var entidadeSituacao = e.parentNode.parentNode.childNodes[9].childNodes[1].value;
	console.log('entidadeSituacao: '+entidadeSituacao);
    var entidadeESI = e.parentNode.parentNode.childNodes[9].childNodes[3].value;
	console.log('entidadeESI: '+entidadeESI);
    var entidadeMunicipio = e.parentNode.parentNode.childNodes[9].childNodes[5].value;
	console.log('entidadeMunicipio: '+entidadeMunicipio);
    var entidadeSiglaUF = e.parentNode.parentNode.childNodes[9].childNodes[7].value;
	console.log('entidadeSiglaUF: '+entidadeSiglaUF);

	document.getElementById('EntidadeIdForView').value = entidadeId;
    document.getElementById('EntidadeNomeForView').value = entidadeNome;
    document.getElementById('EntidadeDescricaoForView').value = entidadeDescricao;
    document.getElementById('EntidadeSiglaForView').value = entidadeSigla;
    document.getElementById('EntidadeCodigoForView').value = entidadeCodigo;
    document.getElementById('EntidadeSituacaoForView').value = entidadeSituacao;
    document.getElementById('EntidadeESIForView').value = entidadeESI;
    document.getElementById('EntidadeMunicipioForView').value = entidadeMunicipio;
    document.getElementById('EntidadeSiglaUFForView').value = entidadeSiglaUF;
	loadPlanosByEntidadeId(entidadeId);
	loadCiclosByEntidadeId(entidadeId);
}
function editEntidade(e) {
	resetEntidadeForms();
    var editForm = document.getElementById('edit-form');
    editForm.style.display = 'block';
    var entidadeId = e.parentNode.parentNode.childNodes[3].childNodes[0].value;
	console.log('entidadeId: '+entidadeId);
    var entidadeCodigo = e.parentNode.parentNode.childNodes[3].innerText;
	console.log('entidadeCodigo: '+entidadeCodigo);
    var entidadeSigla = e.parentNode.parentNode.childNodes[5].innerText;
	console.log('entidadeSigla: '+entidadeSigla);
    var entidadeNome = e.parentNode.parentNode.childNodes[7].innerText;
	console.log('entidadeNome: '+entidadeNome);
    var entidadeDescricao = e.parentNode.parentNode.childNodes[7].childNodes[1].value;
	console.log('entidadeDescricao: '+entidadeDescricao);
    var entidadeSituacao = e.parentNode.parentNode.childNodes[9].childNodes[1].value;
	console.log('entidadeSituacao: '+entidadeSituacao);
    var entidadeESI = e.parentNode.parentNode.childNodes[9].childNodes[5].value;
	console.log('entidadeESI: '+entidadeESI);
    var entidadeMunicipio = e.parentNode.parentNode.childNodes[9].childNodes[7].value;
	console.log('entidadeMunicipio: '+entidadeMunicipio);
    var entidadeSiglaUF = e.parentNode.parentNode.childNodes[9].childNodes[9].value;
	console.log('entidadeSiglaUF: '+entidadeSiglaUF);

	document.getElementById('EntidadeIdForUpdate').value = entidadeId;
    document.getElementById('EntidadeNomeForUpdate').value = entidadeNome;
    document.getElementById('EntidadeDescricaoForUpdate').value = entidadeDescricao;
    document.getElementById('EntidadeSiglaForUpdate').value = entidadeSigla;
    document.getElementById('EntidadeCodigoForUpdate').value = entidadeCodigo;
    document.getElementById('EntidadeSituacaoForUpdate').value = entidadeSituacao;
    document.getElementById('EntidadeESIForUpdate').value = entidadeESI;
    document.getElementById('EntidadeMunicipioForUpdate').value = entidadeMunicipio;
    document.getElementById('EntidadeSiglaUFForUpdate').value = entidadeSiglaUF;
	loadPlanosByEntidadeId(entidadeId);
	loadCiclosByEntidadeId(entidadeId);
}

function resetEntidadeForms(){
	console.log("resetEntidadeForms()");
	if(document.getElementById('formulario-create')){
		document.getElementById('formulario-create').reset();
		document.getElementById('EntidadeDescricaoForInsert').value="";
		console.log(document.getElementById('EntidadeDescricaoForInsert').value);
	}
	if(document.getElementById('formulario-edit')){
		document.getElementById('formulario-edit').reset();
		document.getElementById('EntidadeDescricaoForUpdate').value="";
		console.log(document.getElementById('EntidadeDescricaoForUpdate').value);
	}
}

function deleteEntidade(e) {
    var deleteForm = document.getElementById('delete-form');
    deleteForm.style.display = 'block';
    var entidadeId = e.parentNode.parentNode.childNodes[3].childNodes[0].value;
    document.getElementById('EntidadeIdToDelete').value = entidadeId;
}

function loadPlanosByEntidadeId(entidadeId){
	let xmlhttp;
	xmlhttp=new XMLHttpRequest();
	xmlhttp.onreadystatechange=function()
	{
			if (xmlhttp.readyState==4 && xmlhttp.status==200)
			{
				let planosEntidade = JSON.parse(xmlhttp.responseText);
				wipeRows("table-planos-"+contexto)
				planos = [];
				for(i = 0;planosEntidade != null && i <planosEntidade.length;i++){
					console.log(planosEntidade[i]);
					planos[i]=planosEntidade[i];
					addPlanoRow("table-planos-"+contexto);
				}
				return planos;
			}
	}
	xmlhttp.open("GET","/loadPlanosByEntidadeId?entidadeId="+entidadeId,true);
	xmlhttp.send();
}

function loadCiclosByEntidadeId(entidadeId){
	var xmlhttp;
	xmlhttp=new XMLHttpRequest();
	xmlhttp.onreadystatechange=function()
	{
			if (xmlhttp.readyState==4 && xmlhttp.status==200)
			{
				var ciclosEnt = JSON.parse(xmlhttp.responseText);
				wipeRows("table-ciclos-entidade-"+contexto)
				ciclosEntidade = [];
				for(order = 0;ciclosEnt != null && order<ciclosEnt.length;order++){
					ciclosEntidade[order]=ciclosEnt[order];
					addCicloEntidadeRow("table-ciclos-entidade-"+contexto);
				}
			}
	}
	xmlhttp.open("GET","/loadCiclosByEntidadeId?entidadeId="+entidadeId,true);
	xmlhttp.send();
}