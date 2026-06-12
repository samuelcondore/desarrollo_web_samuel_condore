from flask import Flask, session, request, render_template, redirect, url_for, flash, jsonify, make_response
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
        telefono = telefono.replace(" ", "")
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
    items_per_page = 5 # no escalable aún :(
    total_paginas = ceil(db.total_act()/items_per_page)

    pagina = int(request.args.get('page', 1, type=int))
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

@app.route("/miembro")
def miembro():
    id_miembro = int(request.args.get("id"))
    items_per_page = 5 # no escalable aún :(
    total_paginas = ceil(db.total_act_by_member_id(id_miembro)/items_per_page)
    pagina = int(request.args.get('page',1,type=int))
    has_next = (pagina+1<=total_paginas)
    has_prev = (pagina-1>0)

    data_act = []
    
    miembro = db.get_member_by_id(id_miembro)
    comuna = db.get_comuna_by_id(miembro.comuna_id)
    actividades = db.get_member_activities(id_miembro, items_per_page, pagina)
    for act in actividades:
        foto = db.get_foto_by_act_id(act.id)
        data_act.append({
            "id": act.id,
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
    return render_template("miembro.html", data_act=data_act, miembro=data_miembro, has_next=has_next, has_prev=has_prev, curr_page=pagina)

@app.route("/actividad", methods=["GET","POST"])
def actividad():
    if request.method == "POST":
        nombre = request.form.get("commenter_name")
        texto = request.form.get("content")
        act_id = request.form.get("act_id")
        vd_nombre = validate_commenter_name(nombre)
        vd_texto = validate_comment(texto)

        session['nombre'] = nombre
        session['texto'] = texto

        if not vd_nombre:
            flash("Nombre inválido, chequee que el largo sea entre 3 y 80 carácteres")
        if not vd_texto:
            flash("Comentario inválido, chequee que el largo sea entre 5 y 300 carácteres")
        if vd_nombre and vd_texto:
            db.create_comment(nombre, texto, act_id)
            flash("Tu comentario ha sido registrado")
            return make_response("Success", 200)
        return make_response("Error", 500)
    else:
        id_actividad = int(request.args.get("id",type=int))
        act = db.get_activity_by_id(id_actividad)
        items_per_page = 5 # no escalable aún :(
        total_paginas = ceil(db.total_comments(act.id)/items_per_page)
        pagina = int(request.args.get('page',1,type=int))
        has_next = (pagina+1<=total_paginas)
        has_prev = (pagina-1>0)    
        
        foto = db.get_foto_by_act_id(act.id)
        data_act = {
            "id": act.id,
            "nombre": act.nombre,
            "dia": act.dia,
            "horai": act.hora_inicio,
            "duracion": act.duracion,
            "categoria": act.tipo,
            "desc": act.descripcion,
            "foto_path": foto.ruta_archivo
        }
        comments_data = []
        comments_db = db.get_comments_by_activity(act.id, items_per_page, pagina)
        for c in comments_db:
            comments_data.append({
                "id": c.id,
                "nombre": c.nombre,
                "texto": c.texto,
                "fecha": c.fecha
            })

        return render_template("actividad.html", act=data_act, comments=comments_data, has_next=has_next, has_prev=has_prev, curr_page=pagina)

@app.route("/estadisticas")
def estadisticas():
    return render_template("estadisticas.html")

@app.route("/db/registros", methods=["POST"])
def stats_registros():
    data = request.get_json()
    rango = data.get("range", 7)
    miembros = db.get_members_grouped_by_date(rango)
    #procesar datos a tuplas
    tuplas_miembros = [[fecha.isoformat(), registros] for fecha, registros in miembros]
    return jsonify(tuplas_miembros)

@app.route("/db/actividades", methods=["POST"])
def stats_actividades():
    actividades = db.get_activities_grouped_by_category()
    #procesar datos
    tuplas_actividades = [[tipo, actividades] for tipo, actividades in actividades]
    return jsonify(tuplas_actividades)

@app.route("/db/comunas", methods=["POST"])
def stats_comunas():
    actividades = db.get_activities_grouped_by_comuna()
    #procesar datos
    tuplas_actividades = [[comuna, actividades] for comuna, actividades in actividades]
    return jsonify(tuplas_actividades)

if __name__ == "__main__":
    app.run(debug=True)