async function obtenerDatosRegistros(rango = 31) {
  const options = {
    range: rango
  }
  try {
    const response = await fetch('/db/registros', {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(options)
    });
    if (!response.ok) {
      throw new Error("Error de html");
    } 
    return response.json();
  } catch (error) {
    console.error('Error al llamar "fetch()"', error);
  }
}

async function crearGraficoRegistros(rango = 31) {
  try {
    const datos = await obtenerDatosRegistros(rango);
    Highcharts.chart('linechart', {
      chart: {
        type: 'line'
      },
      title: {
        text: 'Cantidad de registros por día'
      },
      xAxis: {
        type: 'datetime',
        title: {
          text: 'Fecha'
        }
      },
      yAxis: {
        title: {
          text: 'Número de Registros'
        }
      },
      tooltip: {
        xDateFormat: '%d/%m/%Y',
        shared: true
      },
      series: [{
        name: 'Registros',
        data: datos.map(item => [item.fechas, item.registros]),
        color: '#FF9900'
      }],
      legend: {
        enabled: true
      },
      responsive: {
        rules: [{
          condition: {
            maxWidth: 500
          },
          chartOptions: {
            legend: {
              layout: 'horizontal',
              align: 'center',
              verticalAlign: 'bottom'
            }
          }
        }]
      }
    });

  } catch (error) {
    console.error('Error al crear el gráfico:', error);
    document.getElementById('linechart').innerHTML = '<p style="color:red;">No se pudieron cargar los datos.</p>';
  }
}

async function obtenerDatosActividades() {
  try {
    const response = await fetch('/db/actividades', {
        method: "POST"
    });
    if (!response.ok) {
      throw new Error("Error de html");
    } 
    return response.json();
  } catch (error) {
    console.error('Error al llamar "fetch()"', error);
  }
}

async function crearGraficoActividades() {
  try {
    const datos = await obtenerDatosActividades();
    Highcharts.chart('piechart', {
      chart: {
        type: 'pie'
      },
      title: {
        text: 'Distribución de actividades por categoría'
      },
      series: [{
        colorByPoint: true,
        data: datos.map(item => [item.tipo, item.actividades])
      }],
      legend: {
        enabled: true
      }
    });

  } catch (error) {
    console.error('Error al crear el gráfico:', error);
    document.getElementById('piechart').innerHTML = '<p style="color:red;">No se pudieron cargar los datos.</p>';
  }
}

async function obtenerDatosComunas() {
  try {
    const response = await fetch('/db/comunas', {
        method: "POST"
    });
    if (!response.ok) {
      throw new Error("Error de html");
    } 
    return response.json();
  } catch (error) {
    console.error('Error al llamar "fetch()"', error);
  }
}

async function crearGraficoComunas() {
  try {
    const datos = await obtenerDatosComunas();
    Highcharts.chart('barchart', {
      chart: {
        type: 'column'
      },
      xAxis: {
        type: 'category',
        title: {
            text: 'Comuna'
        }
      },
      yAxis: {
        title: {
            text: 'Cantidad de actividades'
        }
      },
      title: {
        text: 'Distribución de actividades por comuna'
      },
      series: [{
        data: datos.map(item => [item.comuna, item.actividades]),
      }],
      legend: {
        enabled: true
      }
    });

  } catch (error) {
    console.error('Error al crear el gráfico:', error);
    document.getElementById('barchart').innerHTML = '<p style="color:red;">No se pudieron cargar los datos.</p>';
  }
}

function crearGraficos() {
    crearGraficoRegistros();
    crearGraficoActividades();
    crearGraficoComunas();
}

const rangoForm = document.getElementById("rango");
rangoForm.addEventListener('change', (e) => {
    crearGraficoRegistros(parseInt(document.getElementById("rango").value));
});

crearGraficos();
