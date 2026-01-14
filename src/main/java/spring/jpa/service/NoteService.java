package spring.jpa.service;

import java.util.List;
import spring.jpa.entity.Note;

public interface NoteService {
    Note create(Note n);
    Note update(Long id, Note n);
    void delete(Long id);
    Note getById(Long id);
    List<Note> listAll();
    List<Note> listByCoursAndGroupe(Long coursId, Long groupeId);
}
