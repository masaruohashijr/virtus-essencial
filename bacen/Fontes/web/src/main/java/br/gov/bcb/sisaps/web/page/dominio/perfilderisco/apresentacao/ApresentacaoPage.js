ApresentacaoPage = function() {
	// Declarações
	var that;
	var conteudos;
	var paginaAtual;
	var btnProximo;
	var btnAnterior;
	var btnIndice;
	var btnImpressao;
	
	{
		// Inicializações
		that = {};
		paginaAtual = 0;
		indice = $("div.index");
		conteudos = $("div.content:not(.index)");
		btnProximo = $("#btnProximo");
		btnAnterior = $("#btnAnterior");
		btnIndice = $("#btnIndice");
		btnImpressao = $("#btnImpressao");
		
		// Prepara os botões.
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
		
		// Monta o índice.
		montarIndice();
		
		// Inicia na página 1.
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
			// Exibe página anterior.
			exibirPagina(paginaAtual - 1);
			break;
			
		// Page Down, seta direita, seta cima, Enter, espaço.
		case 34: case 39: case 40: case 13: case 32:
			// Exibe página posterior.
			exibirPagina(paginaAtual + 1);
			break;
			
		// Home
		case 36: exibirPagina(1); break;
		
		// End
		case 35: exibirPagina(conteudos.length); break;
		}
	});
	
	// Exibe uma página.
	function exibirPagina(numero) {
		// Declarações
		var pagina;
		var titulo;
		
		// Valida o número da página.
		if (numero < 1 || numero > conteudos.length) return;
		
		// Esconde tudo.
		indice.hide();
		conteudos.hide();
		
		// Recupera a página selecionada.
		pagina = $(conteudos[numero - 1]);
		
		// Exibe a página selecionada.
		pagina.show();
		
		// Exibe o título da página.
		titulo = pagina.find(".titulo").val();
		if (titulo == null) titulo = "";
		$("#spanTitulo").html(titulo);
		
		$("#spanTitulo2").html(titulo);
		
		// Exibe o número da página no rodapé.
		$("#spanPagina").text("SRC " + numero + "/" + conteudos.length);
		
		// Guarda o número da página atual.
		paginaAtual = numero;
		
		// Exibe/esconde os botões de navegação.
		if (paginaAtual == 1) btnAnterior.prop("disabled", "disabled");
		else btnAnterior.prop("disabled", "");
		if (paginaAtual == conteudos.length) btnProximo.prop("disabled", "disabled");
		else btnProximo.prop("disabled", "");
		btnIndice.prop("disabled", "");
	};
	
	// Exibe o índice.
	function exibirIndice() {
		// Esconde tudo.
		conteudos.hide();
		
		// Exibe o índice.
		indice.show();
		
		// Sem título.
		$("#spanTitulo").html("");
		
		// Sem número da página no rodapé.
		$("#spanPagina").html("SRC &Iacute;ndice");
		
		// Esconde os botões de navegação.
		btnAnterior.prop("disabled", "disabled");
		btnProximo.prop("disabled", "disabled");
		btnIndice.prop("disabled", "disabled");
	}
	
	// Exibe a impressao.
	function exibirImpressao() {
		window.print();
	}
	
	// Monta o índice.
	function montarIndice() {
		// Declarações
		var slide;
		var link;
		var numero;
		
		// Extrai os títulos dos conteúdos.
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
			
			// Novo item do índice.
			slide = $("<div />");
			slide.append(link);
			indice.append(slide);
		}
	}
	
	return that;
};

// Inicializador
$(function() {
	// Declarações
	apresentacao = new ApresentacaoPage();
});
