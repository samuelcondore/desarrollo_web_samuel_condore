package com.example.appweb.controllers;

import com.example.appweb.models.*;
import com.example.appweb.services.AppService;
import org.aspectj.weaver.ast.Not;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;


@Controller
public class AppController {
    private final AppService appService;
    public AppController(AppService appService) {
        this.appService = appService;
    }

    @GetMapping("/")
    String portada(Model model) {
        List<Miembro> miembros = appService.getRecentMembers(5);
        model.addAttribute("data", miembros);
        return "portada";
    }

    @GetMapping("/registro")
    String registro(Model model) {
        List<Comuna> comunas = appService.getComunas();
        model.addAttribute("comunas", comunas);
        return "registro";
    }
    @PostMapping("/registro")
    String registro_validacion(Model model,
                               @RequestParam("nombre") String nombre,
                               @RequestParam("email") String email,
                               @RequestParam("telefono") String telefono,
                               @RequestParam("comuna") String comuna_id,
                               @RequestParam("nombre_act") String nombre_act,
                               @RequestParam("categoria") String categoria,
                               @RequestParam("dia") String dia,
                               @RequestParam("horai") String horai,
                               @RequestParam("horas") String horas,
                               @RequestParam("desc") String desc,
                               @RequestParam("foto") MultipartFile foto,
                               @RequestParam("minutos") String minutos) throws NoSuchAlgorithmException, IOException {
        if (horas.length()==1){horas = "0"+horas;}
        if (minutos.length()==1){minutos = "0"+minutos;}
        String duracion = horas+":"+minutos;
        Boolean vd_member = Miembro.validate_register_member(nombre, telefono, email);
        Boolean vd_act = Actividad.validate_register_act(nombre_act, categoria, dia, horai, duracion);

        List<String> msg = new ArrayList<>();
        if (!vd_member){msg.add("Error al registrar miembro.");}
        else {
            Comuna comuna = appService.getComunaById(Long.parseLong(comuna_id));
            Miembro member = new Miembro(nombre, email, telefono, comuna);
            if (!vd_act) {
                msg.add("Error al registrar actividad.");
            } else {
                Actividad actividad = new Actividad(member, dia, horai, duracion, categoria, nombre_act, desc);
                Boolean vd_foto = appService.validate_register_img(foto, actividad, member);
                if (!vd_foto) {
                    msg.add("Error al registrar la foto.");
                }
                else {
                    msg.add("Registro exitoso!");
                }
            }
        }
        model.addAttribute("messages", msg);
        return "registro";
    }

    @GetMapping("/listado")
    String listado(Model model,
                   @RequestParam(name = "page", defaultValue="0") String pagina
                   ) {
        int realpagina = Integer.parseInt(pagina);
        int items_per_page = 5;
        int total_paginas = (int) Math.ceil((double) appService.getTotalActivities() / items_per_page);
        Boolean has_next = (realpagina+1)<=total_paginas;
        Boolean has_prev = (realpagina-1)>=0;
        model.addAttribute("has_next", has_next);
        model.addAttribute("has_prev", has_prev);
        model.addAttribute("curr_page", realpagina);
        List<Actividad> actividades = appService.getRecentActivities(realpagina, items_per_page);
        model.addAttribute("data", actividades);
        return "listado";
    }

    @GetMapping("/miembro")
    String miembro(Model model,
                   @RequestParam(name = "page", defaultValue="0") String pagina,
                   @RequestParam(name = "id", required = true) String member_id){
        int realpagina = Integer.parseInt(pagina);
        int items_per_page = 5;
        int total_paginas = (int) Math.ceil((double) appService.getTotalActByMember(Long.parseLong(member_id)) / items_per_page);
        Boolean has_next = (realpagina+1)<=total_paginas;
        Boolean has_prev = (realpagina-1)>=0;
        model.addAttribute("has_next", has_next);
        model.addAttribute("has_prev", has_prev);
        model.addAttribute("curr_page", realpagina);

        Miembro member = appService.getMemberById(Long.parseLong(member_id));
        model.addAttribute("miembro", member);
        List<Actividad> data_act = appService.getActivitiesByMember(Long.parseLong(member_id));
        List<ActividadConFoto> data_act_foto = data_act.stream()
                        .map(act -> new ActividadConFoto(act, appService.getFotoByActId(act.getId())))
                                .toList();
        model.addAttribute("data_act_foto", data_act_foto);
        return "miembro";
    }

    @GetMapping("/actividad")
    String actividad(Model model,
                     @RequestParam(name = "id", required = true) String act_id,
                     @RequestParam(name = "page", defaultValue="0") String pagina){
        Actividad actividad = appService.getActivityById(Long.parseLong(act_id));
        int realpagina = Integer.parseInt(pagina);
        int items_per_page = 5;
        int total_paginas = (int) Math.ceil((double) appService.getTotalCommentsByActivity(actividad.getId()) / items_per_page);
        Boolean has_next = (realpagina+1)<=total_paginas;
        Boolean has_prev = (realpagina-1)>=0;
        model.addAttribute("has_next", has_next);
        model.addAttribute("has_prev", has_prev);
        model.addAttribute("curr_page", realpagina);

        Foto foto = appService.getFotoByActId(actividad.getId());
        ActividadConFoto data_act = new ActividadConFoto(actividad, foto);
        model.addAttribute("act", data_act);

        List<Comentario> comments_data = appService.getCommentsByActivity(actividad, realpagina, items_per_page);
        model.addAttribute("comments", comments_data);
        return "actividad";
    }
    @PostMapping("/actividad")
    ResponseEntity<String> add_comment(Model model,
                                       @RequestParam("commenter_name") String commenter_name,
                                       @RequestParam("content") String content,
                                       @RequestParam(name = "id", required = true) String act_id) {
        Boolean vd_nombre = Comentario.validate_commenter_name(commenter_name);
        Boolean vd_texto = Comentario.validate_comment(content);
        List<String> msg = new ArrayList<>();
        Actividad act = appService.getActivityById(Long.parseLong(act_id));
        if (!vd_nombre){msg.add("Nombre inválido, chequee que el largo sea entre 3 y 80 carácteres");}
        if (!vd_texto){msg.add("Comentario inválido, chequee que el largo sea entre 5 y 300 carácteres");}
        if (vd_nombre&&vd_texto){
            Comentario comment = new Comentario(commenter_name, content, act);
            appService.comentarioRepository.save(comment);
            msg.add("Tu comentario ha sido registrado");
            model.addAttribute("messages", msg);
            return ResponseEntity.ok("Success");
        } else {
            model.addAttribute("messages", msg);
            return ResponseEntity.status(500).body("Error");}
    }

    @GetMapping("/estadisticas")
    String estadisticas(){
        return "estadisticas";
    }

    @PostMapping("/db/registros")
    @ResponseBody
    List<MiembrosAgrupadosFecha> stats_registros(@RequestBody JsonBody range) {
        return appService.get_members_grouped_by_date(range.getRange());
    }

    @PostMapping("/db/actividades")
    @ResponseBody
    List<ActividadAgrupadasTipo> stats_actividades() {
        return appService.getActivitiesGroupedByCategory();
    }

    @PostMapping("/db/comunas")
    @ResponseBody
    List<ActividadAgrupadasComuna> stats_comunas() {
        return appService.getActivitiesGroupedByComuna();
    }

    @GetMapping("/buscador")
    String buscador(){
        return "buscador";
    }

    @PostMapping("/buscador")
    @ResponseBody
    List<ActividadConNota> buscar(@RequestBody JsonBody query){
        List<Actividad> actividades = appService.freeSearch(query.getQuery());
        return actividades.stream()
                .map(act -> new ActividadConNota(act, appService.getNotaByActivity(act))).toList();
    }

    @PostMapping("/nueva-nota")
    @ResponseBody
    ResponseEntity<String> addNota(@RequestBody JsonBody body){
        int nota = body.getNota();
        Actividad actividad = appService.getActivityById((long) body.getAct_id());
        Boolean vd_nota = Nota.validate_nota(nota);
        if (!vd_nota){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nota inválida.");
        } else {
            appService.notaRepository.save(new Nota(actividad, nota));
            return ResponseEntity.ok("Nota añadida con éxito.");
        }
    }

}
