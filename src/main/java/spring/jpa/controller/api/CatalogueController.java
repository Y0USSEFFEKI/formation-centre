package spring.jpa.controller.api;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import spring.jpa.dto.CoursDto;
import spring.jpa.dto.SpecialiteDto;
import spring.jpa.repository.CoursRepository;
import spring.jpa.repository.SpecialiteRepository;

@RestController
@RequestMapping("/api/catalogue")
public class CatalogueController {

    private final SpecialiteRepository specialiteRepository;
    private final CoursRepository coursRepository;

    public CatalogueController(SpecialiteRepository specialiteRepository, CoursRepository coursRepository) {
        this.specialiteRepository = specialiteRepository;
        this.coursRepository = coursRepository;
    }

    @GetMapping("/specialites")
    public List<SpecialiteDto> specialites() {
        return specialiteRepository.findAll().stream().map(s -> {
            SpecialiteDto dto = new SpecialiteDto();
            dto.setId(s.getId());
            dto.setName(s.getName());
            return dto;
        }).toList();
    }

    @GetMapping("/cours")
    public List<CoursDto> cours(@RequestParam(required = false) Long specialiteId) {
        var list = (specialiteId == null)
            ? coursRepository.findAll()
            : coursRepository.findBySpecialty_Id(specialiteId);

        return list.stream().map(c -> {
            CoursDto dto = new CoursDto();
            dto.setId(c.getId());
            dto.setCode(c.getCode());
            dto.setTitle(c.getTitle());
            if (c.getSpecialty() != null) {
                dto.setSpecialiteId(c.getSpecialty().getId());
                dto.setSpecialiteName(c.getSpecialty().getName());
            }
            return dto;
        }).toList();
    }
}
