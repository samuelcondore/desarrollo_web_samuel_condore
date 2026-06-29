const searchbar = document.getElementById("searchbar");
const table = document.getElementById("search_results");
searchbar.addEventListener("input", async (e) => {
    e.preventDefault();
    const query = e.target.value.trim();
    if (query.length < 3) {
        table.innerHTML = "<tr><td colspan=\"6\" >No hay resultados</td></tr>";
    } else {
        renderQueryResult(query);
    }
})

async function fetchQueryResults(query) {
    const body = {
        query: query
    }
    try {
        const response = await fetch('/buscador', {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(body)
        });
        if (!response.ok) {
            throw new Error("Error de html");
        }
        return response.json();
    } catch (error) {
        console.error('Error al llamar "fetch()"', error);
    }
}

async function renderQueryResult(query) {
    const queryResult = await fetchQueryResults(query);
    if (queryResult.length === 0){
        table.innerHTML = "<tr><td colspan=\"6\" >No hay resultados</td></tr>";
    } else {
        const regex = new RegExp(query, "gi");
        table.innerHTML = queryResult.map(act => {
            if (act.nota === null) {
                return `<tr><td>${act.actividad.miembro.nombre}</td><td>${act.actividad.nombre}</td><td>${act.actividad.tipo}</td><td>${act.actividad.miembro.comuna.nombre}</td><td>${act.actividad.dia}</td><td>${act.actividad.descripcion}</td><td> - </td><td><button type="submit" data-id="${act.actividad.id}">Poner nota</button><input type="number" id="${act.actividad.id}" name="nota" min="1" max="7" placeholder=" - "></td></tr>`;
            }
            return `<tr><td>${act.actividad.miembro.nombre}</td><td>${act.actividad.nombre}</td><td>${act.actividad.tipo}</td><td>${act.actividad.miembro.comuna.nombre}</td><td>${act.actividad.dia}</td><td>${act.actividad.descripcion}</td><td>${act.nota.nota}</td><td></td></tr>`;
        }).join("").replaceAll(regex, "<mark>$&</mark>");
    }
}

async function setNota(actId){
    const nota = parseInt(document.getElementById(actId).value);
    console.log(`nota leida: ${nota}`)
    const body = {
        act_id: actId,
        nota: nota
    }
    try {
        const response = await fetch('/nueva-nota', {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(body)
        });
        if (!response.ok) {
            throw new Error("Error de html");
        }
        console.log('Nota añadida.');
        renderQueryResult(searchbar.value);
    } catch (error) {
        console.error('Error al llamar "fetch()"', error);
    }
}

document.getElementById("form-notas").addEventListener("submit", (e) => {
    e.preventDefault();
    const id = e.submitter.dataset.id;
    console.log(`Click recibido con id: ${id}`);
    setNota(id);
})