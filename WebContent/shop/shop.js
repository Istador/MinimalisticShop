/**
 * JavaScript für die Liefer- und Rechnungsadresse im Profil und bei der 
 * Bestellung. Anzeigen/Ausblenden Aktivieren/Deaktivieren von Inputs die
 * gebraucht oder nicht mehr gebraucht werden. 
 */

var l, r, r_control, l_val, r_val;
var l_new, l_old, r_new, r_old, r_wie_l;

//erst nachdem das DOM geladen ist. 
window.onload = function(){
	//Referenzen/Pointer auf die <input>-Elemente 
	l = document.getElementById('liefer');
	r = document.getElementById('rechnung');
	r_control = document.getElementById('r_control');
	l_new = document.getElementById('l_new');
	l_old = document.getElementById('l_old');
	r_new = document.getElementById('r_new');
	r_old = document.getElementById('r_old');
	r_wie_l = document.getElementById('r_wie_l');
	
	//initiale Werte aus der Datenbank zwischenspeichern
	l_val = l.value;
	r_val = r.value;
};

//verstecktes HTML-Element wieder einblenden 
function show(e){e.style.display = "";}
//HTML-Element ausblenden / verstecken
function hide(e){e.style.display = "none";}

//ein deaktiviertes <input> wieder aktivieren
function enable(e){e.disabled = false; e.removeAttribute("disabled");}
//ein <input> deaktivieren
function disable(e){e.disabled = true;}

//click auf RadioButton für ne neue Lieferadresse
function lnew() {
	l.value = "";
	enable(l);
}

//click auf RadioButton für die alte Lieferadresse aus der Datenbank
function lold() {
	l.value = l_val; //Wert zurücksetzen
	disable(l);
}

//click auf RadioButton für ne neue Rechnungsadresse
function rnew() {
	r.value = "";
	enable(r);
}

//click auf RadioButton für die alte Rechnungsadresse aus der Datenbank
function rold() {
	r.value = r_val; //Wert zurücksetzen
	disable(r);
}

//click auf die CheckBox die aussagt ob die Rechnungs- gleich die Lieferadresse
//sein soll.
function rwiel() {
	//Rechnungs wie Lieferadresse
	if(r_wie_l.checked){
		disable(r_old);
		disable(r_new);
		hide(r_control);
	}
	//Rechnungsadresse unabhängig von Lieferadresse
	else {
		//wenn eine alte Rechnungsadresse in der Datenbank existiert
		if(r_val != "")
			enable(r_old); //mache sie auswählbar
		enable(r_new);
		show(r_control);
	}
}