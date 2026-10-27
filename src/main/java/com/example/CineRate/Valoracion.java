package com.example.CineRate;

public class Valoracion {

    private Long id;
    private Long PeliculaID;
    private String tipoVotante;
    private boolean positiva;
    public Valoracion() {}
    public Valoracion (Long peliculaID, String tipoVotante, boolean positiva) {
        this.PeliculaID = peliculaID;
        this.tipoVotante = tipoVotante;
        this.positiva = positiva;
    }

    public Long getId () {return id;}
    public void setId(Long id) {this.id = id;}

    public Long getPeliculaId() { return PeliculaID; }
    public void setPeliculaId(Long peliculaId) { this.PeliculaID = peliculaId; }

    public String getTipoVotante() {return tipoVotante;}
    public void setTipoVotante(String tipoVotante) {this.tipoVotante = tipoVotante;}

    public boolean isPositiva() {return positiva;}
    public void setPositiva(boolean positiva) {this.positiva = positiva;}
}
