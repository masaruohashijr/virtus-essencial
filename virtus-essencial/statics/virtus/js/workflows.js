function editWorkflow(e) { 
	var editForm = document.getElementById('edit-form');
	editForm.style.display = 'block';
	var idWF = e.parentNode.parentNode.childNodes[3].innerText;
	var name = e.parentNode.parentNode.childNodes[5].innerText;
	var description = e.parentNode.parentNode.childNodes[5].childNodes[0].value;
	var entity = e.parentNode.parentNode.childNodes[7].innerText;
	document.getElementById('WorkflowIdForUpdate').value= idWF;
	document.getElementById('NameForUpdate').value= name;
	document.getElementById('DescriptionForUpdate').value= description;
	document.getElementById('EntityForUpdate').value=entity;
	loadActivitiesByWorkflowId(idWF);
}

function deleteWorkflow(e) {
	var deleteForm = document.getElementById('delete-form');
	deleteForm.style.display = 'block';
	var orderId = e.parentNode.parentNode.childNodes[3].innerText; // alterado
	document.getElementById('WorkflowIdToDelete').value = orderId;
}

function loadAvailableFeatures(type, statusId){
	var xmlhttp;
	xmlhttp=new XMLHttpRequest();
	xmlhttp.onreadystatechange=function()
	{
			if (xmlhttp.readyState==4 && xmlhttp.status==200)
			{
				let feats = JSON.parse(xmlhttp.responseText);
				return feats;
			}
	}
	xmlhttp.open("GET","/loadAvailableFeatures?entityType="+type+"&statusId="+statusId,true);
	xmlhttp.send();
}

function loadAllowedActions(type, statusId){
	var xmlhttp;
	xmlhttp=new XMLHttpRequest();
	xmlhttp.onreadystatechange=function()
	{
			if (xmlhttp.readyState==4 && xmlhttp.status==200)
			{
				var formulario = document.getElementById('formulario-edit');
				removeActions(formulario);
				// renderizar
				var actions = JSON.parse(xmlhttp.responseText);
				for(i = 0;i<actions.length;i++){
					var identificador = document.getElementById('IdForUpdate').value;
					if(!document.getElementById(actions[i].id+'-'+actions[i].name)){
						var btnAction = document.createElement('input');
						btnAction.type = "button";
						btnAction.className = "w3-btn w3-cerceta w3-margin-top w3-margin-bottom w3-right";
						btnAction.style = "margin-right: 10px; background-color: #05ffb0;";
						btnAction.id = actions[i].id+'-'+actions[i].name;
						btnAction.value = actions[i].name;
						btnAction.documentId = actions[i].id;
						btnAction.isAction = true;
						btnAction.onclick = function() {executeAction(type, identificador, this.documentId)};
						formulario.appendChild(btnAction);
						loadAvailableFeatures(type,statusId);
					}
				}
			}
	}
	xmlhttp.open("GET","/loadAllowedActions?entityType="+type+"&statusId="+statusId,true);
	xmlhttp.send();
}

function executeAction(type, id, actionId){
	var xmlhttp;
	xmlhttp=new XMLHttpRequest();
	xmlhttp.onreadystatechange=function()
	{
		if (xmlhttp.readyState==4 && xmlhttp.status==200)
		{
			var status = JSON.parse(xmlhttp.responseText);
			document.getElementById("StatusForUpdate").value = status.name; 
			loadAllowedActions(type, status.id);
		}
	}
	xmlhttp.open("GET","/executeAction?entityType="+type+"&id="+id+"&actionId="+actionId,true);
	xmlhttp.send();
}

function removeActions(formulario){
	var nodes = formulario.childNodes;
	for(i=nodes.length-1;i>=0;i--){
		if(nodes[i].isAction){
			formulario.removeChild(nodes[i]);
		}
	}
}