import enum
from datetime import datetime, date, timedelta
from sqlalchemy import create_engine, Column, Integer, BigInteger, String, DateTime, Enum, Text, ForeignKey, null, text
from sqlalchemy.orm import sessionmaker, declarative_base, relationship

DB_NAME = "tarea2"
DB_USERNAME = "cc5002"
DB_PASSWORD = "programacionweb"
DB_HOST = "localhost"
DB_PORT = 3306

DATABASE_URL = f"mysql+pymysql://{DB_USERNAME}:{DB_PASSWORD}@{DB_HOST}:{DB_PORT}/{DB_NAME}"

engine = create_engine(DATABASE_URL, echo=True)
SessionLocal = sessionmaker(bind=engine)
Base = declarative_base()

# ------- Modelos -------

class Region(Base):
    __tablename__ = "region"
    __table_args__ = {"schema": "tarea2"}

    id = Column(Integer, primary_key=True, autoincrement=True)
    nombre = Column(String(200), nullable=False)

    comuna = relationship("Comuna", back_populates="region")

class Comuna(Base):
    __tablename__ = "comuna"
    __table_args__ = {"schema": "tarea2"}

    id = Column(Integer, primary_key=True, autoincrement=True)
    nombre = Column(String(200), nullable=False)
    region_id = Column(Integer, ForeignKey('tarea2.region.id', ondelete='NO ACTION', onupdate='NO ACTION'), nullable=False)

    miembro = relationship("Miembro", back_populates="comuna")
    region = relationship("Region", back_populates="comuna")

class Miembro(Base):
    __tablename__ = "miembro"
    __table_args__ = {"schema": "tarea2"}

    id = Column(Integer, primary_key=True, autoincrement=True)
    nombre = Column(String(255), nullable=False)
    email = Column(String(80), nullable=False)
    telefono = Column(String(15), nullable=False)
    fecha_registro = Column(DateTime, default=datetime.now, nullable=False)
    comuna_id = Column(Integer, ForeignKey('tarea2.comuna.id', ondelete='NO ACTION', onupdate='NO ACTION'),nullable=False)

    comuna = relationship("Comuna", back_populates="miembro")
    actividad = relationship("Actividad", back_populates="miembro")

class DiaEnum(enum.Enum):
    lunes = "lunes"
    martes = "martes"
    miércoles = "miércoles"
    jueves = "jueves"
    viernes = "viernes"
    sábado = "sábado"
    domingo = "domingo"

class TipoEnum(enum.Enum):
    arte ='arte'
    deporte = 'deporte'
    tecnología = 'tecnología'
    social = 'social'
    recreación = 'recreación'
    otra = 'otra'

class Actividad(Base):
    __tablename__ = "actividad"
    __table_args__ = {"schema": "tarea2"}

    id = Column(Integer, primary_key=True, autoincrement=True)
    miembro_id = Column(Integer, ForeignKey('tarea2.miembro.id', ondelete='NO ACTION', onupdate='NO ACTION'), nullable=False)
    dia = Column(Enum(DiaEnum), nullable=False)
    hora_inicio = Column(String(5), nullable=False)
    duracion = Column(String(5), nullable=False)
    tipo = Column(Enum(TipoEnum), nullable=False)
    nombre = Column(String(45), nullable=False)
    descripcion = Column(Text(500), nullable=True)

    miembro = relationship("Miembro", back_populates="actividad")
    foto = relationship("Foto", back_populates="actividad")
    comentario = relationship("Comentario", back_populates="actividad")
    

class Foto(Base):
    __tablename__ = "foto"
    __table_args__ = {"schema": "tarea2"}

    id = Column(Integer, primary_key=True, autoincrement=True)
    ruta_archivo = Column(String(300), nullable=False)
    nombre_archivo = Column(String(300), nullable=False)
    actividad_id = Column(Integer, ForeignKey('tarea2.actividad.id', ondelete='NO ACTION', onupdate='NO ACTION'), nullable=False)

    actividad = relationship("Actividad", back_populates="foto")

class Comentario(Base):
    __tablename__ = "comentario"
    __table_args__ = {"schema": "tarea2"}

    id = Column(Integer, primary_key=True, autoincrement=True)
    nombre = Column(String(80), nullable=False)
    texto = Column(String(300), nullable=False)
    fecha = Column(DateTime, default=datetime.now, nullable=False)
    actividad_id = Column(Integer, ForeignKey('tarea2.actividad.id', ondelete='NO ACTION', onupdate='NO ACTION'), nullable=False)

    actividad = relationship("Actividad", back_populates="comentario")

# ------- Funciones -------

def get_members_grouped_by_date(range):
    session = SessionLocal()
    cutoff = (date.today() - timedelta(days=range)).strftime("%Y-%m-%d")
    query = text("SELECT DATE(fecha_registro) AS fechas, COUNT(id) AS registros FROM miembro WHERE fecha_registro > '"+ cutoff +"' GROUP BY DATE(fecha_registro)")
    members = session.execute(query).all()
    session.close()
    return members

def get_activities_grouped_by_category():
    session = SessionLocal()
    query = text("SELECT tipo, COUNT(id) AS actividades FROM actividad GROUP BY tipo")
    actividades = session.execute(query).all()
    session.close()
    return actividades

def get_activities_grouped_by_comuna():
    session = SessionLocal()
    query = text("SELECT C.nombre, COUNT(A.id) AS actividades FROM actividad A JOIN miembro M ON M.id = A.miembro_id JOIN comuna C ON M.comuna_id = C.id GROUP BY C.nombre")
    actividades = session.execute(query).all()
    session.close()
    return actividades

def get_recent_members(page_size):
    session = SessionLocal()
    members = session.query(Miembro).order_by(Miembro.id.desc()).limit(page_size).all()
    session.close()
    return members

def get_member_by_id(id):
    session = SessionLocal()
    member = session.query(Miembro).filter_by(id=id).first()
    session.close()
    return member

def get_foto_by_act_id(id):
    session = SessionLocal()
    foto = session.query(Foto).filter_by(actividad_id=id).first()
    session.close()
    return foto

def get_comments_by_activity(id, page_size, page=1):
    session = SessionLocal()
    comentarios = session.query(Comentario).filter_by(actividad_id=id).order_by(Comentario.id.desc()).offset((page-1)*page_size).limit(page_size).all()
    session.close()
    return comentarios

def get_activity_by_id(id):
    session = SessionLocal()
    query = text("SELECT * FROM actividad WHERE id="+str(id))
    activity = session.execute(query).first()
    session.close()
    return activity

def get_recent_activity(page_size, page=1):
    session = SessionLocal()
    actividades = session.query(Actividad).order_by(Actividad.id.desc()).offset((page-1)*page_size).limit(page_size).all()
    session.close()
    return actividades

def get_member_activities(id, page_size, page=1):
    session = SessionLocal()
    actividades = session.query(Actividad).filter_by(miembro_id=id).order_by(Actividad.id.desc()).offset((page-1)*page_size).limit(page_size).all()
    session.close()
    return actividades

def get_comunas():
    session = SessionLocal()
    comunas = session.query(Comuna).order_by(Comuna.nombre).all()
    session.close()
    return comunas

def get_comuna_by_id(id):
    session = SessionLocal()
    comuna = session.query(Comuna).filter_by(id=id).first()
    session.close()
    return comuna

def total_act():
    session = SessionLocal()
    total = session.query(Actividad).count()
    session.close()
    return total

def total_act_by_member_id(id):
    session = SessionLocal()
    total = session.query(Actividad).filter_by(miembro_id=id).count()
    session.close()
    return total

def total_comments(id):
    session = SessionLocal()
    total = session.query(Comentario).filter_by(actividad_id=id).count()
    session.close()
    return total

def create_member(nombre, email, telefono, comuna_id):
    session = SessionLocal()
    new_member = Miembro(nombre=nombre, email=email, telefono=telefono, comuna_id=comuna_id)
    session.add(new_member)
    session.commit()
    session.close()

def create_activity(nombre, categoria, dia, horai, duracion, miembro_id, desc):
    session = SessionLocal()
    new_act = Actividad(nombre=nombre, tipo=categoria, dia=dia, hora_inicio=horai, duracion=duracion, descripcion=desc, miembro_id=miembro_id)
    session.add(new_act)
    session.commit()
    session.close()

def create_foto(ruta_archivo, nombre_archivo, actividad_id):
    session = SessionLocal()
    new_foto = Foto(ruta_archivo=ruta_archivo, nombre_archivo=nombre_archivo, actividad_id=actividad_id)
    session.add(new_foto)
    session.commit()
    session.close()

def create_comment(name, comment, act_id):
    session = SessionLocal()
    new_comment = Comentario(nombre=name, texto=comment, actividad_id=act_id)
    session.add(new_comment)
    session.commit()
    session.close()

def clear_all_members():
    session = SessionLocal()
    session.query(Miembro).delete()
    session.commit()
    session.close()