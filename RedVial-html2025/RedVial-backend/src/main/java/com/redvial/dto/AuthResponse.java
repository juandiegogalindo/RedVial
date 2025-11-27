package com.redvial.dto;

public class AuthResponse {
    private String token;
    private String tipo = "Bearer";
    private Long idUsuario;
    private String rol;

    public AuthResponse() {}
    public AuthResponse(String token, Long idUsuario, String rol) {
        this.token = token;
        this.idUsuario = idUsuario;
        this.rol = rol;
    }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
