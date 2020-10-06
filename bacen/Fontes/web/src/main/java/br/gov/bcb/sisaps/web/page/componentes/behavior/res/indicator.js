/**
 * Inspired by the Veil component by Igor Vaynberg in wicketstuff-minis
 * http://wicketstuff.org/confluence/display/STUFFWIKI/wicketstuff-minis
 * wicketstuff-minis is released under the Apache 2 License
 * http://apache.org/licenses/LICENSE-2.0.html
 */
Mask = {};

/**
 * Shows a mask and a spinner over the element with the specified id
 */
Mask.show = function(targetId) {
	var target = document.getElementById(targetId);
	var mask = document.createElement("div");
	mask.innerHTML = "&nbsp;";
	mask.className = "wicket-mask";
	mask.style.cursor = "not-allowed";
	mask.style.zIndex = "5000";
	mask.id = "wicket_mask_" + targetId;
	document.body.appendChild(mask);
	Mask.offsetMask(mask);

	var spinner = document.createElement("div");
	spinner.innerHTML = "&nbsp;&nbsp;&nbsp;&nbsp;";
	spinner.className = "wicket-spinner";
	spinner.style.cursor = "not-allowed";
	spinner.style.zIndex = "6000";
	spinner.id = "wicket_spinner_" + targetId;
	document.body.appendChild(spinner);
	Mask.centerSpinner(spinner);
}

/**
 * Hides the mask and spinner
 */
Mask.hide = function(targetId) {
	var mask = document.getElementById("wicket_mask_" + targetId);
	if (mask != null) {
		mask.style.display = "none";
		document.body.removeChild(mask);
	}
	var spinner = document.getElementById("wicket_spinner_" + targetId);
	if (spinner != null) {
		spinner.style.display = "none";
		document.body.removeChild(spinner);
	}
}

/**
 * Places the spinner at the center of the viewport.
 */
Mask.centerSpinner = function(spinner) {
	var width = Mask.findWidth();
	var height = Mask.findHeight();

	var offsetX = document.body.scrollLeft;
	var offsetY = Mask.findYOffSet();

	var left = (width / 2) - 24 + offsetX;
	var top = (height / 2) - 24 + offsetY;

	spinner.style.left = left + "px";
	spinner.style.top = top + "px";
}

Mask.findHeight = function() {
	var myHeight = 0;
	if (typeof (window.innerWidth) == 'number') {
		// Non-IE
		myWidth = window.innerWidth;
		myHeight = window.innerHeight;
	} else if (document.documentElement
			&& document.documentElement.clientHeight) {
		// IE 6+ in 'standards compliant mode'
		myHeight = document.documentElement.clientHeight;
	} else if (document.body && document.body.clientHeight) {
		// IE 4 compatible
		myHeight = document.body.clientHeight;
	}
	return myHeight;
}
Mask.findWidth = function() {
	var myWidth = 0;
	if (typeof (window.innerWidth) == 'number') {
		// Non-IE
		myWidth = window.innerWidth;
	} else if (document.documentElement && document.documentElement.clientWidth) {
		// IE 6+ in 'standards compliant mode'
		myWidth = document.documentElement.clientWidth;
	} else if (document.body && document.body.clientWidth) {
		// IE 4 compatible
		myWidth = document.body.clientWidth;
	}
	return myWidth;
}

Mask.findYOffSet = function() {

	if (typeof window.pageYOffset == 'number') {

		Mask.findYOffSet = function() {
			return window.pageYOffset;
		};

	} else if ((typeof document.compatMode == 'string')
			&& (document.compatMode.indexOf('CSS') >= 0)
			&& (document.documentElement)
			&& (typeof document.documentElement.scrollTop == 'number')) {

		Mask.findYOffSet = function() {
			return document.documentElement.scrollTop;
		};

	} else if ((document.body) && (typeof document.body.scrollTop == 'number')) {

		Mask.findYOffSet = function() {
			return document.body.scrollTop;
		}

	} else {

		Mask.findYOffSet = function() {
			return NaN;
		};

	}

	return Mask.findYOffSet();
}

Mask.findXOffSet = function() {

	if (typeof window.pageXOffset == 'number') {

		Mask.findXOffSet = function() {
			return window.pageXOffset;
		};

	} else if ((typeof document.compatMode == 'string')
			&& (document.compatMode.indexOf('CSS') >= 0)
			&& (document.documentElement)
			&& (typeof document.documentElement.scrollLeft == 'number')) {

		Mask.findXOffSet = function() {
			return document.documentElement.scrollLeft;
		};

	} else if ((document.body) && (typeof document.body.scrollLeft == 'number')) {

		Mask.findXOffSet = function() {
			return document.body.scrollLeft;
		}

	} else {

		Mask.findXOffSet = function() {
			return NaN;
		};

	}

	return Mask.findXOffSet();
}

/**
 * Offsets the mask to the scroll position.
 */
Mask.offsetMask = function(mask) {
	var offsetX = Mask.findXOffSet();
	var offsetY = Mask.findYOffSet();

	mask.style.left = offsetX + "px";
	mask.style.top = offsetY + "px";
}