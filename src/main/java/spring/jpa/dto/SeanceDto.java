package spring.jpa.dto;

import java.time.LocalDateTime;

public class SeanceDto {
    private Long id;
    private LocalDateTime heureDebut;
    private LocalDateTime heureFin;
    private String salle;
    private CoursSummary cours;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getHeureDebut() {
        return heureDebut;
    }

    public void setHeureDebut(LocalDateTime heureDebut) {
        this.heureDebut = heureDebut;
    }

    public LocalDateTime getHeureFin() {
        return heureFin;
    }

    public void setHeureFin(LocalDateTime heureFin) {
        this.heureFin = heureFin;
    }

    public String getSalle() {
        return salle;
    }

    public void setSalle(String salle) {
        this.salle = salle;
    }

    public CoursSummary getCours() {
        return cours;
    }

    public void setCours(CoursSummary cours) {
        this.cours = cours;
    }

    public static class CoursSummary {
        private Long id;
        private String code;
        private String title;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
