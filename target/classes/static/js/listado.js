const row1 = document.getElementById("1");
if (row1) {
    row1.addEventListener("click", (e) => {
        const id_miembro = document.getElementById("1").dataset.id;
        window.location.href = "/miembro?id="+id_miembro;
    });
}
const row2 = document.getElementById("2");
if (row2) {
    row2.addEventListener("click", (e) => {
        const id_miembro = document.getElementById("2").dataset.id;
        window.location.href = "/miembro?id="+id_miembro;
    });
}
const row3 = document.getElementById("3");
if (row3) {
    row3.addEventListener("click", (e) => {
        const id_miembro = document.getElementById("3").dataset.id;
        window.location.href = "/miembro?id="+id_miembro;
    });
}
const row4 = document.getElementById("4");
if (row4) {
    row4.addEventListener("click", (e) => {
        const id_miembro = document.getElementById("4").dataset.id;
        window.location.href = "/miembro?id="+id_miembro;
    });
}
const row5 = document.getElementById("5");
if (row5) {
    row5.addEventListener("click", (e) => {
        const id_miembro = document.getElementById("5").dataset.id;
        window.location.href = "/miembro?id="+id_miembro;
    });
}