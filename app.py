from flask import Flask, request, render_template, redirect, url_for, flash
from utils.validations import *
from database import db
from werkzeug.utils import secure_filename
from math import ceil
import os

UPLOAD_FOLDER = 'static/uploads'

app = Flask(__name__)

app.secret_key = "s3cr3t_k3y"
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER
app.config['MAX_CONTENT_LENGTH'] = 16 * 1000 * 1000

@app.route("/")#, methods=["GET", "POST"])
def portada():
    #if request.method == "POST":
    #    action = request.form.get("test")
    #    if action == "add":
    #        db.create_member("samuel","samuel@ug.cl","12345678",10304)
    #    else:
    #        db.clear_all_members()
    miembros = db.get_recent_members(5)
    data = []
    for miembro in miembros:
        comuna = db.get_comuna_by_id(miembro.comuna_id)
        data.append({
            "nombre": miembro.nombre,
            "email": miembro.email,
            "telefono": miembro.telefono,
            "fecha_registro": miembro.fecha_registro,
            "comuna": comuna.nombre
        })
    return render_template("portada.html", data=data)
    
@app.route("/registro", methods=["GET", "POST"])
def registro():
    return render_template("registro.html", comunas=db.get_comunas())

@app.route("/registro-validacion", methods=["GET", "POST"])
def registro_validacion():
    if request.method == "POST":
        #data de miembro:
        nombre = request.form.get("nombre")
        email = request.form.get("email")
        telefono = request.form.get("telefono")
        comuna_id = request.form.get("comuna")
        #data de actividad:
        nombre_act = request.form.get("nombre_act")
        categoria = request.form.get("categoria")
        dia = request.form.get("dia")
        horai = request.form.get("horai")
        horas = str(request.form.get("horas"))
        minutos = str(request.form.get("minutos"))
        if len(horas)==1:
            horas = "0"+horas
        if len(minutos)==1:
            minutos = "0"+minutos
        duracion = horas+":"+minutos
        desc = request.form.get("desc")
        foto = request.files.get("foto")
        vd_member = validate_register_member(nombre, telefono, email)
        vd_act = validate_register_act(nombre_act, categoria, dia, horai, duracion)
        vd_foto = validate_register_img(foto)
        if not vd_member:
            flash("Error al registrar miembro.")
        if not vd_act:
            flash("Error al registrar actividad.")
        if not vd_foto:
            flash("Error al registrar la foto.")
        if vd_member and vd_act and vd_foto:
            db.create_member(nombre, email, telefono, comuna_id)
            member_id = db.get_recent_members(1)[0].id
            db.create_activity(nombre_act, categoria, dia, horai, duracion, member_id, desc)
            act_id = db.get_recent_activity(1)[0].id
            filename = secure_filename(foto.filename)
            filepath = os.path.join(app.config['UPLOAD_FOLDER'], filename)
            db.create_foto(filepath, filename, act_id)
            foto.save(filepath)
            flash("Registro exitoso!")
            return redirect(url_for('portada'))
        else:
            return redirect(url_for('registro'))

    return render_template("registro_validacion.html")

@app.route("/listado")
def listado():
    items_per_page = 10
    total_paginas = ceil(db.total_act()/items_per_page)

    pagina = request.args.get('page', 1, type=int)
    has_next = (pagina+1<=total_paginas)
    has_prev = (pagina-1>0)
    actividades = db.get_recent_activity(items_per_page, pagina)
    data = []
    for act in actividades:
        miembro = db.get_member_by_id(act.miembro_id)
        data.append({
            "id_miembro": miembro.id,
            "nombre_miembro": miembro.nombre,
            "email": miembro.email,
            "id_act": act.id,
            "nombre_act": act.nombre,
            "dia": act.dia.value,
            "horai": act.hora_inicio,
        })

    return render_template("listado.html", data=data, has_next=has_next, has_prev=has_prev, curr_page=pagina)

@app.route("/actividad")
def actividad():
    id_miembro = request.args.get("id")
    items_per_page = 10
    total_paginas = ceil(db.total_act_by_member_id(id_miembro)/items_per_page)
    pagina = request.args.get('page',1,type=int)
    has_next = (pagina+1<=total_paginas)
    has_prev = (pagina-1>0)

    data_act = []
    
    miembro = db.get_member_by_id(id_miembro)
    comuna = db.get_comuna_by_id(miembro.comuna_id)
    actividades = db.get_member_activities(id_miembro, items_per_page, pagina)
    for act in actividades:
        foto = db.get_foto_by_act_id(act.id)
        data_act.append({
            "nombre_act": act.nombre,
            "dia": act.dia.value,
            "horai": act.hora_inicio,
            "duracion": act.duracion,
            "categoria": act.tipo.value,
            "desc": act.descripcion,
            "foto_path": foto.ruta_archivo
        })
    data_miembro = ({
        "nombre": miembro.nombre,
        "email": miembro.email,
        "telefono": miembro.telefono,
        "fecha_registro": miembro.fecha_registro,
        "comuna": comuna.nombre
    })
    return render_template("actividad.html", data_act=data_act, miembro=data_miembro, has_next=has_next, has_prev=has_prev, curr_page=pagina)

@app.route("/estadisticas")
def estadisticas():
    return render_template("estadisticas.html")

if __name__ == "__main__":
    app.run(debug=True)