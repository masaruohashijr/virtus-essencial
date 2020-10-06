$(document).ready(
		function() {
			var inicio = '<a href="#sumario" class="inicio">Início</a>';
			var inicioblack = '<a href="#sumario" class="inicioblack">Início</a>';
            // Numeração das seções
    		$("h2").each(
				function(i){
					if(i != 0) {//Exceção é o sumário
						var titulo = i + ". " + $(this).children().html();

						$(this).children().html(titulo);
						$(this).data("contador", 1);
						$(this).data("indice", i+".");
						
						$(this).html(inicio + $(this).html());
					}
				}
    		);  
    		for(var x=3;x<=6;x++) {
	    		$("h" + x).each(
						function(j){
							var anterior = $(this).prevAll("h" + (x-1)).first();
							var n = anterior.data("indice");
							var i = anterior.data("contador");
							var titulo = n + i + ". " + $(this).children().html();
							
							anterior.data("contador", i+1);
							
							$(this).children().html(titulo);
							$(this).data("contador", 1);
							$(this).data("indice", n + i + ".");
							
							if (x != 6) {
								$(this).html(inicio + $(this).html());
							} else {
								$(this).html(inicioblack + $(this).html());
							}
						}
		    	 );
    		}
			//Sumario
			$("#sumario").each(
				function(i){
					var sumario = $(this).html();
					$("a[name]").each(
						function(j){
							if ($(this).html() != '') {
							    var parent = $(this).parent();
							    var link = $(this).attr("name");
							    var nome = $(this).html();
							    
							    if ($(this).attr("class") == 'menu') {
							        sumario = sumario  + '<a href="#' + link + '" class="' + parent[0].nodeName + '">' + nome + "</a><br>";
							    }
							}
						}
		    		);	
					$(this).html(sumario);
				}
    		);	
		}
);
