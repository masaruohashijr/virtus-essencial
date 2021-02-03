function editStatus(e) {
    var editForm = document.getElementById('edit-form');
    editForm.style.display = 'block';
    var statusId = e.parentNode.parentNode.childNodes[3].innerText;
    var statusName = e.parentNode.parentNode.childNodes[5].innerText;
	let statusDescription = '';
	if(e.parentNode.parentNode.childNodes[5].childNodes[1]){
	    statusDescription = e.parentNode.parentNode.childNodes[5].childNodes[1].value;
	}
    var statusStereotype = e.parentNode.parentNode.childNodes[7].innerText;
	document.getElementById('StatusIdForUpdate').value = statusId;
    document.getElementById('StatusNameForUpdate').value = statusName;
    document.getElementById('StatusDescriptionForUpdate').value = statusDescription;
    document.getElementById('StatusStereotypeForUpdate').value = statusStereotype;
	document.getElementById('StatusNameForUpdate').focus();
}

function deleteStatus(e) {
    var deleteForm = document.getElementById('delete-form');
    deleteForm.style.display = 'block';
    var statusId = e.parentNode.parentNode.childNodes[3].innerText;
    document.getElementById('StatusIdToDelete').value = statusId;
}

function openCreateStatus(){
	document.getElementById('create-form').style.display='block';
	document.getElementById('statusNomeForInsert').focus();
}