const form = document.getElementById("comment_form");

form.addEventListener('submit', async (e)=>{
    e.preventDefault();
    const act_id = new URLSearchParams(window.location.search).get("id");
    const data = new FormData(form);
    data.append("act_id", act_id);
  try {
    const response = await fetch('/actividad', {
        method: "POST",
        body: data
    });
    if (response.status != 200) {
      throw new Error("Error de html");
    } 
    location.reload();
  } catch (error) {
    console.error('Error al llamar "fetch()"', error);
    location.reload();
  }
});