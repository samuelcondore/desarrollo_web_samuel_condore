const form = document.getElementById('register_form');
form.addEventListener('submit', checkrequired);

function checkrequired(e) {
    let nombre = document.getElementById('nombre').value;
    let email = document.getElementById('email').value;
    let telefono = document.getElementById('telefono').value;
    let comuna_id = document.getElementById('comuna').value;
    let nombre_act = document.getElementById('nombre_act').value;
    let categoria = document.getElementById('categoria').value;
    let dia = document.getElementById('dia').value;
    let horai = document.getElementById('horai').value;
    let duracion = document.getElementById('duracion').value;
    let foto = document.getElementById('foto').value;

    if ((nombre=="") || (email=="") || (telefono=="") || (comuna_id=="") || (nombre_act=="") || (categoria=="") || (dia=="") || (horai=="") || (duracion=="") || (foto=="")) {
        e.preventDefault();
        window.alert("Llene todos los datos requeridos primero.");
    } else {
        window.alert("Datos registrados.");
    }
}