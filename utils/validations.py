import re
import filetype

def validate_commenter_name(name):
    return bool(re.search(r'^.{3,79}$', name))

def validate_comment(comment):
    return bool(re.search(r'^.{5,299}$', comment))

def validate_username(value):
    return bool(re.search(r'^[a-zA-Z]+$', value))

def validate_email(value):
    return bool(re.search(r'^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$', value))

def validate_telefono(value):
    return bool(re.search(r'^(\s*\d\s*){8}$', value))

def validate_register_member(username, telefono, email):
    return validate_username(username) and validate_telefono(telefono) and validate_email(email)

def validate_nombre_act(value):
    return bool(re.search(r'^.{1,44}$', value))

def validate_categoria(value):
    return bool(value)

def validate_dia(value):
    return True

def validate_horai(value):
    return True

def validate_duracion(value):
    return True

def validate_register_act(nombre, categoria, dia, horai, duracion):
    return validate_nombre_act(nombre) and validate_categoria(categoria) and validate_dia(dia) and validate_horai(horai) and validate_duracion(duracion)

def validate_register_img(foto):
    ALLOWED_EXTENSIONS = {"png", "jpg", "jpeg"}
    ALLOWED_MIMETYPES = {"image/jpeg", "image/png"}

    # check if a file was submitted
    if foto is None:
        return False

    # check if the browser submitted an empty file
    if foto.filename == "":
        return False
    
    # check file extension
    ftype_guess = filetype.guess(foto)
    if ftype_guess.extension not in ALLOWED_EXTENSIONS:
        return False
    # check mimetype
    if ftype_guess.mime not in ALLOWED_MIMETYPES:
        return False
    return True