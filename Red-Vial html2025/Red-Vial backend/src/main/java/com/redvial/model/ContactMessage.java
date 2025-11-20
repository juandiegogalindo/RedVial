    package com.redvial.model;

    import jakarta.persistence.*;
    import java.time.LocalDateTime;

    @Entity
    @Table(name = "contacto")
    public class ContactMessage {
        
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private String nombre;

        @Column(nullable = false)
        private String correo;

        @Column(nullable = false)
        private String asunto;

        @Column(nullable = false, length = 2000)
        private String mensaje;

        @Column(name = "fecha_envio", nullable = false)
        private LocalDateTime fechaEnvio;

        public ContactMessage() {
            this.fechaEnvio = LocalDateTime.now();
        }

        // GETTERS & SETTERS

        public Long getId() { return id; }

        public void setId(Long id) { this.id = id; }

        public String getNombre() { return nombre; }

        public void setNombre(String nombre) { this.nombre = nombre; }

        public String getCorreo() { return correo; }

        public void setCorreo(String correo) { this.correo = correo; }

        public String getAsunto() { return asunto; }

        public void setAsunto(String asunto) { this.asunto = asunto; }

        public String getMensaje() { return mensaje; }

        public void setMensaje(String mensaje) { this.mensaje = mensaje; }

        public LocalDateTime getFechaEnvio() { return fechaEnvio; }

        public void setFechaEnvio(LocalDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; } 
    }