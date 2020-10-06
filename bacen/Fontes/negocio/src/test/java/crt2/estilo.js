$(document).ready(function() {
       var index = $("#cenarios");
       if(index.length == 0) {
             index = $("#index");
       }
       if(index.length == 0){
             return;
       }
       if(!index.data("feito")) {
             index.append("<h2>Lista de cenários</h2>");
             index.append("<blockquote id='titulos'>");
             var bindex = $("#titulos");
             bindex.append("<ul>");
             $(".title").each(function(i) {
                    if(!$(this).data("feito")) {
                           $(this).prepend("<a name='h2"+i+"'></a>");
                           bindex.append("<li><a href='#h2"+i+"'>"+$(this).text()+"</a></li>");
                           $(this).append(" <span style='font-size:8px;'>(<a href='#' onclick='window.scrollTo(0,0);return false;'>Topo</a>)</span>");
                           $(this).data("feito",true);
                    }
             });
             bindex.append("</ul>");
             index.append("</blockquote>");
             index.data("feito",true);
       }

       $("h2").each(function(i) {
             if(!$(this).data("feito")) {
                    $(this).append(" <span style='font-size:8px;'>(<a href='#' onclick='window.scrollTo(0,0);return false;'>Topo</a>)</span>");
                    $(this).data("feito",true);
             }
       });

       $("h3").each(function(i) {
           if(!$(this).data("feito")) {
                  $(this).append(" <span style='font-size:8px;'>(<a href='#' onclick='window.scrollTo(0,0);return false;'>Topo</a>)</span>");
                  $(this).data("feito",true);
           }
     });
});

var cX = 0;
var cY = 0;
var rX = 0;
var rY = 0;

function UpdateCursorPosition(e) {
	cX = e.pageX;
	cY = e.pageY;
}

function UpdateCursorPositionDocAll(e) {
	cX = event.clientX;
	cY = event.clientY;
}

if (document.all) {
	document.onmousemove = UpdateCursorPositionDocAll;
} else {
	document.onmousemove = UpdateCursorPosition;
}

function AssignPosition(d) {
	if (self.pageYOffset) {
		rX = self.pageXOffset;
		rY = self.pageYOffset;
	} else if (document.documentElement && document.documentElement.scrollTop) {
		rX = document.documentElement.scrollLeft;
		rY = document.documentElement.scrollTop;
	} else if (document.body) {
		rX = document.body.scrollLeft;
		rY = document.body.scrollTop;
	}
	if (document.all) {
		cX += rX;
		cY += rY;
	}
	d.style.left = (cX + 10) + "px";
	d.style.top = (cY + 10) + "px";
}

function ocultaDados() {
	var d = 'divDados';
	if (d.length < 1) {
		return;
	}
	document.getElementById(d).style.display = "none";
}

function mostraDados() {
	carregaDados(arguments);
	var d = 'divDados';
	if (d.length < 1) {
		return;
	}
	var dd = document.getElementById(d);
	AssignPosition(dd);
	dd.style.display = "block";
}

window.onload = function() {
	var newdiv = document.createElement("div");
	newdiv.id = "divDados";
	newdiv.style.display = "none";
	newdiv.style.position = "absolute";
	newdiv.style.background = "white";
	document.body.appendChild(newdiv);
}

function carregaDados(args) {
	$("#divDados").load(args[0] + " #" + args[1]);
	$("#divDados").contents().find("tr").each(function() {
		if (args.length > 2) {
			for (i = 2; i < args.length; i++) {
				if ($(this).attr('id') == args[i]) {
					return;
				}
			}
			$(this).remove();
		}
	});
}