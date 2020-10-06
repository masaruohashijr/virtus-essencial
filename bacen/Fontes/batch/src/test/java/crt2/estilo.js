$(document).ready(function() {
	var index = $("#index");
	index.prepend("<blockquote id='bindex'>");
	var bindex = $("#bindex");
	$("h2").each(function(i) {
		$(this).prepend("<a name='h2"+i+"'></a>")
		bindex.append( (i>0?" | ":"")+"<a href='#h2"+i+"'>"+ $.trim($(this).text())+"</a>");
		$(this).append(" <span class='topo'>(<a href='#' onclick='window.scrollTo(0);return false;'>Topo</a>)</span>");
	});
	index.append("</blockquote>");
});

$(document).ready(function() {
	$("h3").each(function(i) {
		$(this).append(" <span class='topo'>(<a href='#' onclick='window.scrollTo(0);return false;'>Topo</a>)</span>");
	});
});