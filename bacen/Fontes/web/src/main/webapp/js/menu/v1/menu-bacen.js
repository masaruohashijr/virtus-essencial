/*
 * Menu Bacen
 * 
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 *
 * Versão 1.0.2
 */

function Menu(id) {

/* Public */
this.firstLevelOffset = {x: 10, y: 0};
this.secondLevelOffset = {x: -10, y: 0};
this.minWidth = 120;

/* Private */
this.id = id;
this.menus = [];
this.items = [];
this.timeout = null;
this.isIE = document.all && document.getElementById && (navigator.appVersion.indexOf('MSIE ') >= 0);
this.isIE6 = document.all && document.getElementById && (navigator.appVersion.indexOf('MSIE 6') >= 0);

this.cancelBubble = function(e) {
	if (!e) {
		var e = window.event;
	}
	e.cancelBubble = true;
	if (e.stopPropagation) {
		e.stopPropagation();
	}
};

/*
 * Obtém um objeto através do seu ID
 */
this.getObject = function(id) {
	if (document.all) {
		return document.all[id];
	} else {
		return document.getElementById(id);
	}
};

this.getHeight = function(element) {
	return element.offsetHeight;
};

this.getPos = function(element, parent) {
	var x = 0;
	var y = 0;
	while (element && element != parent) {
		x += element.offsetLeft;
		y += element.offsetTop;
		element = element.offsetParent;
	}
	return {x:x,y:y};
};

this.forEachChildTag = function(parent, tagName, func) {
	var child = parent.firstChild;
	while (child) {
		if (child.tagName == tagName) {
			func(child);
		}
		child = child.nextSibling;
	}
};

this.prepareItem = function(context, item, menuInfo) {
	if (!item.id) {
		item.id = "sm" + context.menuName + "_item" + context.itemCount;
		context.itemCount++;
	}

	var menuObj = this;
	if (item.parentNode.parentNode.id == context.menuName) {
		item.onmouseover = function(event) { menuObj.mouseOverMenuItem(this, event); };
		item.onmouseout = function(event) { menuObj.mouseOutMenuItem(this, event); };
	} else {
		item.onmouseover = function(event) { menuObj.mouseOverSubMenuItem(this, event); };
		item.onmouseout = function(event) { menuObj.mouseOutSubMenuItem(this, event); };
	}
	
	var itemInfo = {
		element: item, 
		selected: false,
		menuInfo: menuInfo
	}
	this.items[item.id] = itemInfo;
	if (menuInfo.items.length == 0) {
		this.putClass(item, "first-of-type");
	}
	menuInfo.items.push(itemInfo);

	var menuObj = this;
	this.forEachChildTag(item, "UL", function(element) {
		menuObj.prepareMenu(context, element, itemInfo);
		menuInfo.hasSubmenus = true;
		menuObj.putClass(item, "hassubmenus");
	});
};

this.prepareMenu = function(context, menu, parentItemInfo) {
	if (!menu.id) {
		menu.id = "sm" + context.menuName + "_menu" + context.menuCount;
		context.menuCount++;
	}

	var menuObj = this;
	menu.onmouseover = function(event) { menuObj.mouseOverMenu(this, event); }
	menu.onmouseout = function(event) { menuObj.mouseOutMenu(this, event); }
	
	menu.style.zIndex = "-1";
	menu.style.left = "-10000px";
	menu.style.top = "-10000px";
	
	var menuInfo = {
		element: menu,
		parentItemInfo: parentItemInfo,
		visible: false,
		hasSubmenus: false,
		items: []
	}
	this.menus[menu.id] = menuInfo;

	var menuObj = this;
	var lastItem;
	this.forEachChildTag(menu, "LI", function(element) {
		menuObj.prepareItem(context, element, menuInfo);
		lastItem = element;
	});
	if (lastItem) {
		this.putClass(lastItem, "last-of-type");
	}
},

this.start = function() {

	var context = {
		menuName: this.id,
		menuCount: 0,
		itemCount: 0
	};
	
	var main = this.getObject(this.id);
	var menuObj = this;
	this.forEachChildTag(main, "UL", function(element) {
		menuObj.prepareMenu(context, element, null);
	});
	
	/* Bug do IE... */
	
	if (this.isIE) {
		for (var menuId in this.menus) {
			var menuInfo = this.menus[menuId];
			if (menuInfo.hasSubmenus) {
				continue;
			}
			var items = menuInfo.element.getElementsByTagName("LI");
			if (items.length > 0) {
				var item = items[0];
				item.innerHTML += '<div style="visibility:hidden;position:absolute"></div>';
			}
		}
	}
};

this.log = function(msg) {
/*
	var log = this.getObject("log");
	if (log) {
		log.innerHTML += msg + " &bull; ";
	}
*/
};

this.findArrayElement = function(array, element) {
	for (var i = 0; i < array.length; i++) {
		if (array[i] == element) {
			return i;
		}
	}
	return null;
};

this.putClass = function(element, className) {
	if (element.className) {
		var classes = element.className.split(" ");
		if (this.findArrayElement(classes, className) == null) {
			classes.push(className);
		}
		element.className = classes.join(" ");
	} else {
		element.className = className;
	}
};

this.deleteClass = function(element, className) {
	if (element.className) {
		var classes = element.className.split(" ");
		var i = this.findArrayElement(classes, className);
		if (i != null) {
			classes[i] = "";
		}
		element.className = classes.join(" ");
	}
};

this.clearTimeout = function() {
	if (this.timeout) {
		this.log("timeout " + this.timeout + " cleared");
		clearTimeout(this.timeout);
		this.timeout = null;
	}
};

this.setTimeout = function(expression, timeout) {
	if (this.timeout) {
		// Não deveria ocorrer...
		this.clearTimeout();
	}
	this.timeout = setTimeout(expression, timeout);
};

this.hideAll = function() {
	this.log("hideAll");
	for (var itemId in this.items) {
		this.deselectMenuItem(this.items[itemId].element);
	}
};

this.getParentItem = function(menu) {
	if (this.parentNode.tagName == "LI") {
		return this.parentNode;
	} else {
		return null;
	}
};

this.mouseOverMenu = function(element, event) {
	this.log("over menu: " + element.id);
	this.cancelBubble(event);
	this.clearTimeout();
};

this.mouseOutMenu = function(element, event) {
	this.log("out menu: " + element.id);
	this.cancelBubble(event);
	var menuObj = this;
	this.setTimeout(function() {menuObj.hideAll();}, 100);
};

this.mouseOverMenuItem = function(element, event) {
	this.log("over item: " + element.id);
	this.cancelBubble(event);
	this.clearTimeout();
	this.selectMenuItem(element);
};

this.mouseOutTimeout = function(id) {
};

this.mouseOutMenuItem = function(element, event) {
	this.log("out item: " + element.id);
};

this.mouseOutItemTimeout = function(id) {
	this.log("timeout exec");
	this.clearTimeout();
	var item = this.getObject(id);
	this.deselectMenuItem(item);
};

this.mouseOverSubMenuItem = function(element, event) {
	this.log("over subitem: " + element.id);
	this.cancelBubble(event);
	this.clearTimeout();
	this.selectSubMenuItem(element);
};

this.mouseOutSubMenuItem = function(element, event) {
};

this.getItemSubMenu = function(menuItem) {
	var child = menuItem.firstChild;
	while (child && child.tagName != "UL") {
		child = child.nextSibling;
	}
	return child;
};

this.getParentItems = function(item) {
	var parents = [];
	var parent = item.parentNode.parentNode;
	while (this.items[parent.id]) {
		parents[parent.id] = true;
		parent = parent.parentNode.parentNode;
	}
	return parents;
};

this.hideOtherBranches = function(menuItem) {
	var letVisible = this.getParentItems(menuItem);
	letVisible[menuItem.id] = true;
	
	for (var itemId in this.items) {
		if (!letVisible[itemId]) {
			this.deselectMenuItem(this.items[itemId].element);
		}
	}
};

this.showMenu = function(menu, x, y, width) {
	menu.style.left = x + "px";
	menu.style.top = y + "px";

	if (width) {
		menu.style.width = width + "px";
	}
	
	menu.style.zIndex = "500";
	menu.style.visibility = "visible";
	this.menus[menu.id].visible = true;

	if (this.isIE6) {
		/* IE6 */
		var iwidth = menu.offsetWidth;
		var iheight = menu.offsetHeight;
		var iId = menu.id + "_iframe";

		var iframe = 
			'<iframe id="' + iId + 
			'" style="position:absolute;left:' + x + 
			'px;top:' + y + 
			'px;width:' + iwidth + 
			'px;height:'+ iheight + 'px;z-index:250" src="javascript:false">';
	
		menu.parentNode.insertAdjacentHTML("beforeEnd", iframe);
		this.menus[menu.id].iframeId = iId;
	}
};

this.hideMenu = function(menu) {
	var menuInfo = this.menus[menu.id];
	if (menuInfo.visible) {
		menu.style.visibility = "hidden";
		menu.style.zIndex = "-1";
		menu.style.left = "-10000px";
		menu.style.top = "-10000px";
		this.menus[menu.id].visible = false;
		
		if (menuInfo.iframeId) {
			var iframe = document.getElementById(menuInfo.iframeId);
			iframe.outerHTML = "";
		}
	}
};

this.selectMenuItem = function(menuItem) {

	this.hideOtherBranches(menuItem);
	
	if (!this.items[menuItem.id].selected) {
		this.putClass(menuItem, "selected");
		this.items[menuItem.id].selected = true;
	}

	var menu = this.getItemSubMenu(menuItem);
	if (!menu) {
		return;
	}
	
	if (!this.menus[menu.id].visible) {
		var where = this.getPos(menuItem);

		var offset = {x:0,y:0};
		if (menu.offsetParent) {
			offset = this.getPos(menu.offsetParent);
		}
	
		var minWidth = Math.max(this.minWidth, menuItem.offsetWidth);
		var width = null;
		if (menu.offsetWidth < minWidth) {
			width = minWidth;
		}

		this.showMenu(
			menu,
			where.x - offset.x + this.firstLevelOffset.x,
			where.y - offset.y + this.getHeight(menuItem) + this.firstLevelOffset.y,
			width
		);
	}

};

this.deselectMenuItem = function(menuItem) {
	if (!this.items[menuItem.id].selected) {
		return;
	}
	this.deleteClass(menuItem, "selected");
	this.items[menuItem.id].selected = false;
	var menu = this.getItemSubMenu(menuItem);
	if (menu) {
		this.hideMenu(menu);
	}
};

this.selectSubMenuItem = function(menuItem) {

	this.hideOtherBranches(menuItem);
	
	if (!this.items[menuItem.id].selected) {
		this.putClass(menuItem, "selected");
		this.items[menuItem.id].selected = true;
	}

	var menu = this.getItemSubMenu(menuItem);
	if (!menu) {
		return;
	}

	if (!this.menus[menu.id].visible) {
		var where = this.getPos(menuItem);

		var offset = {x:0,y:0};
		var offsetY = 0;
		if (menu.offsetParent) {
			offset = this.getPos(menu.offsetParent);
		}
		
		var width = null;
		if (menu.offsetWidth < this.minWidth) {
			width = this.minWidth;
		}

		this.showMenu(
			menu,
			where.x - offset.x + menuItem.offsetWidth + this.secondLevelOffset.x,
			where.y - offset.y + this.secondLevelOffset.y,
			width);
	}
};

}

Menus = {
	
	onloadAnterior: null,
	onunloadAnterior: null,
	menusRegistrados: [],

	registrar: function(id) {
		var menu = new Menu(id);
		Menus.menusRegistrados.push(menu);
		return menu;
	},
	
	registrarOnLoad: function() {
		if (window.onload) {
			Menus.onloadAnterior = window.onload;
		}
		if (window.onunload) {
			Menus.onunloadAnterior = window.onunload;
		}
		window.onload = Menus.onloadHandler;
		window.onunload = Menus.onunloadHandler;
	},
	
	onloadHandler: function() {
		for (var i = 0; i < Menus.menusRegistrados.length; i++) {
			var menu = Menus.menusRegistrados[i];
			menu.start();
		}
		if (Menus.onloadAnterior) {
			Menus.onloadAnterior();
		}
	},

	onunloadHandler: function() {
		for (var i = 0; i < Menus.menusRegistrados.length; i++) {
			var menu = Menus.menusRegistrados[i];
			menu.hideAll();
		}
	}
};

Menus.registrarOnLoad();