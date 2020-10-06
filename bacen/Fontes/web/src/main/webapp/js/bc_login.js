function autotab(event, original, destination) {
    var key;

    // se o navegador for o IE, mapear o evento de tecla para window.event.
    event = event || window.event;

    // obter o keycode
    if (event.keyCode) {
        key = event.keyCode;
    } else if (event.which) {
        key = event.which;
    } else {
        key = event.charCode;
    }

    // TAB(9) ou SHIFT(16) não são considerados.
    if ((key == 9) || (key == 16)) {
        return;
    } else {
        if (original.getAttribute) {
            if(original.value.length==original.getAttribute('maxlength')) {
                destination.focus();
                destination.select();
            }
        }
    }
}
