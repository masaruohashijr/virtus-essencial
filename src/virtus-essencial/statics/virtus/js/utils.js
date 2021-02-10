function regraDeTresQtd(e, form){
	var a = document.getElementById('alimento-'+form);
	var id = a.options[a.selectedIndex].value;
	var array = ar[id].split("#")
	var qtd = array[2];
	var cho = array[3];
	var kcal = array[4];
	var qtdInformada = e.value;
	var x = cho*qtdInformada/qtd;
	x = Math.round((x + Number.EPSILON) * 100) / 100
	var y = kcal*qtdInformada/qtd;
	y = Math.round((y + Number.EPSILON) * 100) / 100
	var cho = document.getElementById('cho-'+form);
	cho.value = x;
	var kcal = document.getElementById('kcal-'+form);
	kcal.value = y;
}

function regraDeTresMedida(e,form){
	var a = document.getElementById('alimento'+form);
	var id = a.options[a.selectedIndex].value;
	var array = ar[id].split("#")
	var qtd = 1;
	var cho = array[3];
	var kcal = array[4];
	var qtdInformada = e.value;
	var x = cho*qtdInformada/qtd;
	var y = kcal*qtdInformada/qtd;
	var choInput = document.getElementById('cho-'+form);
	x = Math.round((x + Number.EPSILON) * 100) / 100
	choInput.value = x;
	var kcalInput = document.getElementById('kcal-'+form);
	y = Math.round((y + Number.EPSILON) * 100) / 100
	kcalInput.value = y;
}

function convertDate(dt){
	var parts = dt.split("/");
	var dia = parts[0];
	var mes = parts[1];
	var ano = parts[2];
	return ano+"-"+mes+"-"+dia;
}

function resetFields(form){
	document.getElementById('qtdMedida-'+form).value='';
	document.getElementById('qtd-'+form).value='';
	document.getElementById('cho-'+form).value='';
	document.getElementById('kcal-'+form).value='';
}

function setTimeNow(nomeCampo){
	campo = document.getElementsByName(nomeCampo);
	if(campo){
		var now = new Date();
		var tomorrow = new Date();
		tomorrow.setDate(tomorrow.getDate() + 1);
		campo.value= now.format('yyyy-mm-dd');
		dateFormat.masks.hammerTime = 'HH:MM';
		var vl = now.format("hammerTime");
		campo.value= vl;
	}
}

function filtraTabela(input, tabelaNome, offset, colnum){
  var filter, table, tr, td, i, txtValue;
  filter = input.value.toUpperCase();
  table = document.getElementById(tabelaNome);
  tr = table.getElementsByTagName("tr");
  for (i = offset; i < tr.length; i++) {
    td = tr[i].getElementsByTagName("td")[colnum];
	console.log(td.innerText);
    if (td) {
      txtValue = td.textContent || td.innerText;
      if (txtValue.toUpperCase().indexOf(filter) > -1) {
        tr[i].style.display = "table-row";
      } else {
        tr[i].style.display = "none";
      }
    }       
  }
}

var minhaFuncaoBtnOk;

function alterarOkConfirm(anonymousFunction){
	let divElement = document.getElementById('divOKConfirm');
	let btns = divElement.getElementsByTagName("button");
	existeBtnNao = false;
	let btnNao; 
	for(i=0;i<btns.length;i++){
		if(btns[i].innerText == "Não"){
			existeBtnNao = true;
			btnNao = btns[i]; 
			break;
		}
	}
	if(!existeBtnNao){
		btnNao = document.createElement("button");
		btnNao.id = "__btnNao";
		btnNao.classList = btns[0].classList;
		minhaFuncaoBtnSim = btns[0].onclick
		btnNao.onclick = minhaFuncaoBtnSim;
		btnNao.innerText = "Não"; 
		btnNao.style = "padding: 5px; margin: 5px;"; 
	}
	btns[0].innerText = "Sim";
	btns[0].addEventListener("click", function() {
	  	anonymousFunction(); 
		document.getElementById("__btnNao");
		btnNao.remove();
		btnOk = this.cloneNode(true);
		btnOk.innerText = "OK";
		btnOk.addEventListener("click",minhaFuncaoBtnOk);
		divElement.appendChild(btnOk);
		this.remove();
	});
	divElement.appendChild(btnNao)
}
