ApresentacaoPage = function() {
	// Declara��es
	var that;
	var conteudos;
	var paginaAtual;
	var btnProximo;
	var btnAnterior;
	var btnIndice;
	var btnImpressao;
	
	{
		// Inicializa��es
		that = {};
		paginaAtual = 0;
		indice = $("div.index");
		conteudos = $("div.content:not(.index)");
		btnProximo = $("#btnProximo");
		btnAnterior = $("#btnAnterior");
		btnIndice = $("#btnIndice");
		btnImpressao = $("#btnImpressao");
		
		// Prepara os bot�es.
		btnProximo.click(function() {
			exibirPagina(paginaAtual + 1);
		});
		btnAnterior.click(function() {
			exibirPagina(paginaAtual - 1);
		});
		btnIndice.click(function() {
			exibirIndice();
		});
		btnImpressao.click(function() {
			exibirImpressao();
		});
		
		// Monta o �ndice.
		montarIndice();
		
		// Inicia na p�gina 1.
		exibirPagina(1);
	}
	
	$("body")[0].onbeforeprint = function() {
		conteudos.show();
	};

	$("body")[0].onafterprint = function() {
		exibirPagina(paginaAtual);
	};

	// Captura eventos da tela.
	$("body").keydown(function(tecla) {
		//alert(tecla.which);
		switch (tecla.which) {
		// Page Up, seta esquerda, seta cima.
		case 33: case 37: case 38:
			// Exibe p�gina anterior.
			exibirPagina(paginaAtual - 1);
			break;
			
		// Page Down, seta direita, seta cima, Enter, espa�o.
		case 34: case 39: case 40: case 13: case 32:
			// Exibe p�gina posterior.
			exibirPagina(paginaAtual + 1);
			break;
			
		// Home
		case 36: exibirPagina(1); break;
		
		// End
		case 35: exibirPagina(conteudos.length); break;
		}
	});
	
	// Exibe uma p�gina.
	function exibirPagina(numero) {
		// Declara��es
		var pagina;
		var titulo;
		
		// Valida o n�mero da p�gina.
		if (numero < 1 || numero > conteudos.length) return;
		
		// Esconde tudo.
		indice.hide();
		conteudos.hide();
		
		// Recupera a p�gina selecionada.
		pagina = $(conteudos[numero - 1]);
		
		// Exibe a p�gina selecionada.
		pagina.show();
		
		// Exibe o t�tulo da p�gina.
		titulo = pagina.find(".titulo").val();
		if (titulo == null) titulo = "";
		$("#spanTitulo").html(titulo);
		
		$("#spanTitulo2").html(titulo);
		
		// Exibe o n�mero da p�gina no rodap�.
		$("#spanPagina").text("SRC " + numero + "/" + conteudos.length);
		
		// Guarda o n�mero da p�gina atual.
		paginaAtual = numero;
		
		// Exibe/esconde os bot�es de navega��o.
		if (paginaAtual == 1) btnAnterior.prop("disabled", "disabled");
		else btnAnterior.prop("disabled", "");
		if (paginaAtual == conteudos.length) btnProximo.prop("disabled", "disabled");
		else btnProximo.prop("disabled", "");
		btnIndice.prop("disabled", "");
	};
	
	// Exibe o �ndice.
	function exibirIndice() {
		// Esconde tudo.
		conteudos.hide();
		
		// Exibe o �ndice.
		indice.show();
		
		// Sem t�tulo.
		$("#spanTitulo").html("");
		
		// Sem n�mero da p�gina no rodap�.
		$("#spanPagina").html("SRC &Iacute;ndice");
		
		// Esconde os bot�es de navega��o.
		btnAnterior.prop("disabled", "disabled");
		btnProximo.prop("disabled", "disabled");
		btnIndice.prop("disabled", "disabled");
	}
	
	// Exibe a impressao.
	function exibirImpressao() {
		window.print();
	}
	
	// Monta o �ndice.
	function montarIndice() {
		// Declara��es
		var slide;
		var link;
		var numero;
		
		// Extrai os t�tulos dos conte�dos.
		for (var i = 0; i < conteudos.length; i++) {
			// Monta o link para o slide.
			link = $("<a style='cursor:pointer;' />");
			link.prop("numero", i + 1);
			link.click(function() {
				exibirPagina(this.numero);
			});
			numero = "" + (i + 1);
			while (numero.length < conteudos.length / 10) numero = "0" + numero;
			link.append("Slide " + numero + ": " + $(conteudos[i]).find(".indice").val());
			
			// Novo item do �ndice.
			slide = $("<div />");
			slide.append(link);
			indice.append(slide);
		}
	}
	
	return that;
};

// Inicializador
$(function() {
	// Declara��es
	apresentacao = new ApresentacaoPage();
});
