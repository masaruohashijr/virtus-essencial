

/* -- função para trocar a aba --*/
/* -- página com abas -- */



	function trocaAtivo(onode){
	    // mudar a tab ativa
	    if (document.getElementById) {
	       var atab,colelem;
	       atab=document.getElementById("oTab");
	       for (var i=0;i<atab.childNodes.length;i++){
	         colelem=atab.childNodes[i];

			 if (colelem.className=="mtaba"){
				colelem.className="mtabi";
	         }
	       }
	       onode.className="mtaba";
	      
	    }
	}

	/* -- fim da função página com abas --*/
	
	
	/* -- funções para troca de seleção em campos textarea em formularios -- */
	
	function Transporta(objSourceElement, objTargetElement) {
	var aryTempSourceOptions = new Array();
	var x = 0;
	
		//loop pela lista original para encontrar op&ccedil;&otilde;es selecionadas
		for (var i = 0; i < objSourceElement.length; i++) {
			
				if (objSourceElement.options[i].selected) {
		
					var intTargetLen = objTargetElement.length++;
					objTargetElement.options[intTargetLen].text = objSourceElement.options[i].text;
					objTargetElement.options[intTargetLen].value = objSourceElement.options[i].value;
				}
				else {
					//guarda as op&ccedil;&otilde;es que permanecer&atilde;o para recriar a lista
					var objTempValues = new Object();
					objTempValues.text = objSourceElement.options[i].text;
					objTempValues.value = objSourceElement.options[i].value;
					aryTempSourceOptions[x] = objTempValues;
					x++;
				}
		}
	
		//atualiza o tamanho da lista original
		objSourceElement.length = aryTempSourceOptions.length;
		//loop pelo array tempor&aacute;rio para recriar a lista original
		for (var i = 0; i < aryTempSourceOptions.length; i++) {
			objSourceElement.options[i].text = aryTempSourceOptions[i].text;
			objSourceElement.options[i].value = aryTempSourceOptions[i].value;
			objSourceElement.options[i].selected = false;
		}
	}

	function Salva() {
		var strValues = "";
		var boxLength = document.lista.Selecionados.length;
		var count = 0;
		
				if (boxLength != 0) {
					for (i = 0; i < boxLength; i++) {
						if (count == 0) {
							strValues = document.lista.Selecionados.options[i].value;
						}
						else {
							strValues = strValues + "," + document.lista.Selecionados.options[i].value;
						}
						count++;
			   		}
				}
			
				if (strValues.length == 0) {
				alert("Nenhum item foi selecionado");
				} else {
				alert("Itens selecionados:\r\n" + strValues);
		   	}
	}


	//SCRIPT DE ORDENA&Ccedil;&Atilde;O
	//PARA FUNCIONAR CORRETAMENTE O VALOR DEVE SER EXATAMENTE IGUAL AO NOME DO ITEM

	function Crescente(a,b) {
	  if (a<b) return -1;
	  if (a>b) return 1;
	  return 0;
	}
	
	function Decrescente(a,b) {
	  if (a<b) return 1;
	  if (a>b) return -1;
	  return 0;
	}
	
	function Ordena(ordem, campo) {
		var strValues = new Array()
		var strTexts = new Array()
		var boxLength = campo.length;
		var count = 0;
		
		if (ordem == 1){
			for (i = 0; i < boxLength; i++) {
				strValues[i] = campo.options[i].value;
				strTexts[i] = campo.options[i].text;
				
				count++;
	   	}
			
			strValues.sort(Crescente);
			strTexts.sort(Crescente);
			
			for (i = 0; i < boxLength; i++) {
				campo.options[i].value = strValues[i];
				campo.options[i].text = strTexts[i];
				
				count++;
	   	}
	   	
		} else {
			
			for (i = 0; i < boxLength; i++) {
				strValues[i] = campo.options[i].value;
				strTexts[i] = campo.options[i].text;
				
				count++;
	   	}
			
			strValues.sort(Decrescente);
			strTexts.sort(Decrescente);
			
			for (i = 0; i < boxLength; i++) {
				campo.options[i].value = strValues[i];
				campo.options[i].text = strTexts[i];
				
				count++;
	   	}
	   	
		}
	}

	/* -- Move ITENS DENTRO DE UMA MESMO CAMPO --*/
	
	
	function Move(campo,to) {		
		
		var total = campo.options.length-1;
		
		if (campo.selectedIndex == -1) return false;
		if (to == +1 && campo.selectedIndex == total) return false;
		if (to == -1 && campo.selectedIndex == 0) return false;
		
		var items = new Array;
		var values = new Array;
		
		for (i = total; i >= 0; i--) {	
			items[i] = campo.options[i].text;			
			values[i] = campo.options[i].value;
		}
		
		for (i = total; i >= 0; i--) {
			
			if (campo.selectedIndex == i) {
				
				campo.options[i + to] = new Option(items[i],values[i], 0, 1);				
				campo.options[i] = new Option(items[i + to], values[i + to]);
				i--;
			} else {				
				campo.options[i] = new Option(items[i], values[i]);
		  }
		  
		}		
		campo.focus();
	}
	
	/* -- Contagem de caracteres no campo textarea --*/
	
	
	 function contagem(campo, tamanhoMax) {
	  
	 		obj = document.getElementById(campo);	 		
	 		 
      if (tamanhoMax !=0) {
         if (obj.value.length > tamanhoMax) {
            obj.value = obj.value.substring(0, tamanhoMax * 1); 
         }   
      }  
      	
      if(document.all){
		     document.getElementById('contador').innerText = obj.value.length + ' caracteres digitados (m&aacute;ximo ' + tamanhoMax + ').';
			} else{
		    document.getElementById('contador').textContent = obj.value.length + ' caracteres digitados (m&aacute;ximo ' + tamanhoMax + ').';
			}
      
   } 
   
   /* -- Fim do método contagem de caracteres no campo textarea --*/   
   
   /* -- Grupo expansível --*/
   
		function adicionarEvento(func){
      if (!document.getElementById | !document.getElementsByTagName) return
      var oldonload=window.onload
      if (typeof window.onload != 'function') {window.onload=func}
      else {window.onload=function() {oldonload(); func()}}
    }
    adicionarEvento(EscondeTudo)
    
		function EscondeTudo(){      
			
			//resgata todos os objetos span e imagens
      obj=document.getElementsByTagName('div'); 
      
      //neste caso todas as imagens de grupo expansivel tem que ter o NOME abreItem     
      imagem=document.getElementsByName('abreItem');
      
      for (var i=0;i<imagem.length;i++){	
	      imagem[i].src = '/img/mais-p.gif';
    	}     
      
      for (var i=0;i<obj.length;i++){	      
	      
	      if(/grpAtivo/.test(obj[i].className)){
	      	obj[i].className = 'grpInativo';
     		} 
			} 			
			   
    }	

		function trocaAtivo(txtNode,txtTipo,obj){

        // Efeito abrir-fechar grupo de Tags Html
	 			// Navegadores NS 6.1 IE 5.5

				var txtTipo='table',estAtivo='grpAtivo',estInativo='grpInativo';
				path = document.getElementById(obj);
				queNode=document.getElementById(txtNode);

       if(queNode.className=='grpInativo'){
				queNode.className = 'grpAtivo'
				path.src = '/img/menos-p.gif'
		   }else{
				queNode.className = 'grpInativo'
				path.src = '/img/mais-p.gif'
		   }
	}
