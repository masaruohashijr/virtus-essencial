
function editPassword(e) {
	let editForm = document.getElementById('change-password-form');
	editForm.style.display = 'block';
	let id = e.parentNode.parentNode.childNodes[3].innerText;
	let username = e.parentNode.parentNode.childNodes[7].innerText;
	document.getElementById('userIdToChgPwd').value = id;
	document.getElementById('UsernameChgPwd').value = username;
	document.getElementById('PasswordChgPwd').focus();
}

function submeterCreateUser(){
	let form = document.getElementById("formulario-create-user");
	let passwordField = document.getElementById("Password");
	let confirmationField = document.getElementById("Confirmation");
	if(passwordField.value != confirmationField.value){
		document.getElementById("Errors").innerText = "A senha está diferente da sua confirmação.";
		document.getElementById("error-message").style.display="block";
		confirmationField.focus();
		return false;
	} else {
		form.submit();
	}
}
	
function changePassword(){
	let form = document.getElementById("formulario-change-password");
	let passwordField = document.getElementById("PasswordChgPwd");
	let confirmationField = document.getElementById("ConfirmationChgPwd");
	if(passwordField.value != confirmationField.value){
		document.getElementById("Errors").innerText = "A nova senha está diferente da sua confirmação.";
		document.getElementById("error-message").style.display="block";
		confirmationField.focus();
		return false;
	} else {
		form.submit();
	}
}
	
function editUser(e) {
	let editForm = document.getElementById('edit-form');
	editForm.style.display = 'block';
	let userId = e.parentNode.parentNode.childNodes[3].innerText;
	let userName = e.parentNode.parentNode.childNodes[5].innerText;
	let userUsername = e.parentNode.parentNode.childNodes[7].innerText;
	let userEmail = e.parentNode.parentNode.childNodes[9].innerText;
	let userMobile = e.parentNode.parentNode.childNodes[11].innerText;
	let userRole = e.parentNode.parentNode.childNodes[13].childNodes[1].value;
	let userStatus = e.parentNode.parentNode.childNodes[19].innerText;
	let userStatusId = e.parentNode.parentNode.childNodes[19].childNodes[0].value;
	document.getElementById('IdForUpdate').value = userId;
	document.getElementById('userName').value = userName;
	document.getElementById('userUsername').value = userUsername;
	document.getElementById('userEmail').value = userEmail;
	document.getElementById('userMobile').value = userMobile;
	document.getElementById('RoleForUpdate').value = userRole;
	document.getElementById('StatusForUpdate').value = userStatus;
	document.getElementById('userName').focus();
	// AJAX 
	loadAllowedActions('usuario',userStatusId);
	let feats = loadAvailableFeatures('usuario',userStatusId);
}

function deleteUser(e) {
	let deleteForm = document.getElementById('delete-form');
	deleteForm.style.display = 'block';
	let userId = e.parentNode.parentNode.childNodes[3].innerText;
	document.getElementById('userIdToDelete').value = userId;
}

function openCreateUser(){
	document.getElementById('create-form').style.display='block';
	document.getElementById('NameUserForInsert').focus();
}

function naoVazio(...fields){
  fields.forEach(field => {
		console.log(field);
		if(field.length==0) {
			return false;
		}
	});
	return true;
}

function createUser(){
	let name = document.getElementById('NameUserForInsert').value;
	let username = document.getElementById('Username').value;
	let password = document.getElementById('Password').value;
	let confirmation = document.getElementById('Confirmation').value;
	let email = document.getElementById('Email').value;
	let mobile = document.getElementById('Mobile').value;
	let role = document.getElementById('RoleForInsert').value;
	if(password == confirmation && 
		naoVazio(name, username, password, confirmation, email)){
		let xmlhttp;
		xmlhttp = new XMLHttpRequest();
		xmlhttp.onreadystatechange=function()
		{
				console.log(xmlhttp.response);
				if (xmlhttp.readyState==4 && xmlhttp.status==200)
				{
					document.getElementById('create-form').style.display='none';
					document.getElementById("messageText").innerText = "Sucesso";
					document.getElementById("message").style.display="block";
				} else {
					document.getElementById("Errors").innerText = xmlhttp.response;
					document.getElementById("error-message").style.display="block";
					return;		
				}
		}
		xmlhttp.open("POST","/createUser",true);
		let params = "name="+name+
		"&username="+username+
		"&password="+password+
		"&email="+email+
		"&mobile="+mobile+
		"&role="+role;
		xmlhttp.send(params);
	} else {
		let errorMsg = "Falta preencher campos do formulário.";
		document.getElementById("Errors").innerText = errorMsg;
		document.getElementById("error-message").style.display="block";
		return;		
	}
}
function cancelarChangePassword(){
    document.getElementById('formulario-change-password').action = '/logout';
    document.getElementById('formulario-change-password').submit();
}
function cancelarRecoverPassword() {
    document.getElementById('formulario-recover-password').action = '/logout';
    document.getElementById('formulario-recover-password').submit();
}