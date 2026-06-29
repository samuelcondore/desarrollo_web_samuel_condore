package com.example.appweb.services;

import com.example.appweb.models.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Formatter;
import java.util.List;


@Service
public class AppService {

    private final String pathStatic;
    public final ActividadRepository actividadRepository;
    public final MiembroRepository miembroRepository;
    public final ComunaRepository comunaRepository;
    public final ComentarioRepository comentarioRepository;
    public final FotoRepository fotoRepository;
    public final NotaRepository notaRepository;

    public AppService(ActividadRepository actividadRepository,
                      MiembroRepository miembroRepository,
                      ComunaRepository comunaRepository,
                      ComentarioRepository comentarioRepository,
                      FotoRepository fotoRepository,
                      NotaRepository notaRepository) throws IOException {
        this.notaRepository = notaRepository;
        this.actividadRepository = actividadRepository;
        this.miembroRepository = miembroRepository;
        this.comunaRepository = comunaRepository;
        this.comentarioRepository = comentarioRepository;
        this.fotoRepository = fotoRepository;
        Path staticDir = Paths.get(ResourceUtils.getFile("classpath:static").getAbsolutePath());
        this.pathStatic = staticDir.toString();
        System.out.println("Static path resolved to: " + this.pathStatic);
    }

    // ACTIVIDAD
    public List<ActividadAgrupadasTipo> getActivitiesGroupedByCategory() {
        return actividadRepository.getActivitiesGroupedByTipo();
    }
    public List<ActividadAgrupadasComuna> getActivitiesGroupedByComuna() {
        return actividadRepository.getActivitiesGroupedByComuna();
    }
    public Actividad getActivityById(Long id){
        return actividadRepository.findById(id).orElseThrow();
    }

    public List<Actividad> getRecentActivities(int page, int pageSize){
        return actividadRepository.findAllByOrderByIdDesc(PageRequest.of(page, pageSize)).getContent();
    }

    public List<Actividad> getActivitiesByMember(Long miembro_id){
        Miembro miembro = miembroRepository.findById(miembro_id).orElseThrow();
        return actividadRepository.findAllByMiembro(miembro);
    }

    public long getTotalActivities() {
        return actividadRepository.count();
    }

    public long getTotalActByMember(Long miembro_id){
        Miembro miembro = miembroRepository.findById(miembro_id).orElseThrow();
        return actividadRepository.countByMiembro(miembro);
    }

    public List<Actividad> freeSearch(String query){
        return actividadRepository.freeSearch(query);
    }

    // MIEMBROS
    public List<MiembrosAgrupadosFecha> get_members_grouped_by_date(int range){
        LocalDate cutoff = LocalDate.now().minusDays(range);
        return miembroRepository.get_members_grouped_by_date(cutoff);
    }
    public Miembro getMemberById(Long id){
        return miembroRepository.findById(id).orElseThrow();
    }

    public List<Miembro> getRecentMembers(int pageSize){
        return miembroRepository.findAllByOrderByIdDesc(PageRequest.of(0, pageSize)).getContent();
    }

    // COMUNA

    public Comuna getComunaById(Long id){
        return comunaRepository.findById(id).orElseThrow();
    }

    public List<Comuna> getComunas() {
        return comunaRepository.findAllByOrderByNombreDesc();
    }

    //COMENTARIO

    public List<Comentario> getCommentsByActivity(Actividad act, int page, int page_size){
        return comentarioRepository.findAllByActividadOrderByIdDesc(act, PageRequest.of(page, page_size)).getContent();
    }

    public long getTotalCommentsByActivity(Long act_id){
        Actividad act = actividadRepository.findById(act_id).orElseThrow();
        return comentarioRepository.countByActividad(act);
    }

    //NOTA
    public Nota getNotaByActivity(Actividad actividad){
        return notaRepository.findByActividad(actividad);
    }


    //FOTO

    public Foto getFotoByActId(Long act_id) {
        Actividad act = actividadRepository.findById(act_id).orElseThrow();
        return fotoRepository.findByActividad(act);
    }

    public Boolean validate_register_img(MultipartFile foto, Actividad act, Miembro miembro) throws NoSuchAlgorithmException, IOException {
        String _originalFilename = foto.getOriginalFilename();
        if (_originalFilename == null || _originalFilename.isEmpty()){
            throw new IllegalArgumentException("File name is empty.");
        }
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(_originalFilename.getBytes("UTF-8"));
        byte[] hash = md.digest();
        String _filename;
        try (Formatter formatter = new Formatter()) {
            for (byte b : hash) {
                formatter.format("%02x", b);
            }
            _filename = formatter.toString();
        }
        String _extension = _originalFilename.substring(_originalFilename.lastIndexOf('.') + 1).toLowerCase();
        if (!_extension.matches("jpg|jpeg|png|gif")) {
            throw new IllegalArgumentException("Invalid file extension: " + _extension);
        }
        String imgFilename = _filename + "." + _extension;
        String relativePathImg = "/uploads/" + imgFilename;
        String finalPath = this.pathStatic + relativePathImg;

        System.out.println("Final image path: " + finalPath);

        Path directoryPath = Paths.get(pathStatic + "/uploads");
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
            System.out.println("Uploads directory created.");
        }

        Path path = Paths.get(finalPath);
        try (InputStream inputStream = foto.getInputStream()) {
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File successfully saved at: " + path.toAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("Failed to save the image file.", e);
        }
        Foto newfoto = new Foto(relativePathImg, imgFilename, act);
        miembroRepository.save(miembro);
        actividadRepository.save(act);
        fotoRepository.save(newfoto);

        return true;
    }
}
