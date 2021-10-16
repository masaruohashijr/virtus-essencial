function  sinalizarAlteracao(e){
	e.style = "text-align:center; width: "+e.style.width+"; border: 1px solid red";
}

function loadAllowedActionsWithParentId(type, Id){
	var xmlhttp;
	xmlhttp=new XMLHttpRequest();
	statusId = document.getElementById("IdStatus_"+Id).value;
	xmlhttp.onreadystatechange=function()
	{
		if (xmlhttp.readyState==4 && xmlhttp.status==200)
		{
			let divId = 'DivStatus_'+Id;
			let produtoComponenteId = document.getElementById('IdProdutoComponente_'+Id).value;
			statusId = document.getElementById("IdStatus_"+Id).value;
			let divElemento = document.getElementById(divId);
			let nodes = divElemento.childNodes;
			for(i=nodes.length-1;i>=0;i--){					
				if(nodes[i].isAction){
					divElemento.removeChild(nodes[i]);
				}
			}
			// renderizar
			let actions = JSON.parse(xmlhttp.responseText);
			nodes = divElemento.childNodes
			for(i=nodes.length-1;i>=0;i--){					
				if(nodes[i] instanceof HTMLParagraphElement){
					divElemento.removeChild(nodes[i]);
				}
			}				
			if(!actions){
				let p = document.createElement('p');
				p.innerText = "Não permitido."
				divElemento.appendChild(p);
			} else {
				for(i = 0;actions && i<actions.length;i++){
					if(!document.getElementById(actions[i].id+'-'+actions[i].name+'-'+Id)){
						let btnAction = document.createElement('input');
						btnAction.type = "button";
						btnAction.className = "w3-btn w3-cerceta w3-margin-top w3-center";
						btnAction.style = "margin-right: 0px; background-color: #05ffb0;";
						btnAction.id = actions[i].id+'-'+actions[i].name+'-'+Id;
						btnAction.value = actions[i].name;
						btnAction.documentId = actions[i].id;
						btnAction.isAction = true;
						btnAction.onclick = function() {
							executeActionProdutoComponente(type, produtoComponenteId, this.documentId, Id);
						};
						let p = document.createElement('p');
						divElemento.appendChild(p);
						divElemento.appendChild(btnAction);
					}
				}
			}
			loadAvailableFeaturesWithFunction(type, statusId, Id, featuresControl);
		}
	}
	let apiEndpoint = "/loadAllowedActions?entityType="+type+"&statusId="+statusId;
	xmlhttp.open("GET",apiEndpoint,true);
	xmlhttp.send();
}

function unveilStatusButton(id){
	//console.log('unveilStatusButton: '+'DivStatus_'+id);
	if(document.getElementById('DivStatus_'+id)){
		document.getElementById('DivStatus_'+id).style.display='block';
	}
}

function stashStatusButton(id){
	//console.log('stashStatusButton: '+'DivStatus_'+id);
	if(document.getElementById('DivStatus_'+id)){
		document.getElementById('DivStatus_'+id).style.display='none';
	}
}

function executeActionProdutoComponente(type, produtoComponenteId, actionId, Id){
	var xmlhttp;
	xmlhttp=new XMLHttpRequest();
	xmlhttp.onreadystatechange=function()
	{
		if (xmlhttp.readyState==4 && xmlhttp.status==200)
		{
			let status = JSON.parse(xmlhttp.responseText);
			let divId = 'DivStatus_'+Id;
			let divElement = document.getElementById(divId);
			let nodes = divElement.childNodes;
			for(i=nodes.length-1;i>=0;i--){					
				if(nodes[i].isAction){
					divElement.removeChild(nodes[i]);
				}
			}
			if(status.name != ""){
				document.getElementById("StatusName_"+Id).innerText = status.name;
				dispatchIf(status.name,"Em Aberto","/distribuirAtividades")
			}
			document.getElementById("IdStatus_"+Id).value = status.id;
			loadAllowedActionsWithParentId(type, Id);
		}
	}
	xmlhttp.open("GET","/executeAction?entityType="+type+"&id="+produtoComponenteId+"&actionId="+actionId,true);
	xmlhttp.send();
}

function dispatchIf(currentStatus , statusCondition, formAction){
	if(currentStatus == statusCondition){
		entidadeId = document.getElementById('EntidadeId').value
		cicloId = document.getElementById('CicloId').value
		document.getElementById("EntidadeId_AvaliarPlanos").value=entidadeId
		document.getElementById("CicloId_AvaliarPlanos").value=cicloId
		document.getElementById("formulario-avaliar-planos").action = formAction
		document.getElementById("formulario-avaliar-planos").submit()
	}
}