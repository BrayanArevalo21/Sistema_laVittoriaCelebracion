package entidades;

public class PersonalMontaje {
    private int id;
    private String nombre;
    private String tipoDocumento;
    private String numDocumento;
    private String telefono;
    private String email;
    private String especialidad;
    private double costoPorHora;
    private boolean disponible;
    private String observaciones;
    private boolean activo;
    
    public PersonalMontaje() {}
    
    public PersonalMontaje(int id, String nombre, String tipoDocumento, String numDocumento, 
                           String telefono, String email, String especialidad, double costoPorHora, 
                           boolean disponible, String observaciones, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.tipoDocumento = tipoDocumento;
        this.numDocumento = numDocumento;
        this.telefono = telefono;
        this.email = email;
        this.especialidad = especialidad;
        this.costoPorHora = costoPorHora;
        this.disponible = disponible;
        this.observaciones = observaciones;
        this.activo = activo;
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    
    public String getNumDocumento() { return numDocumento; }
    public void setNumDocumento(String numDocumento) { this.numDocumento = numDocumento; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
    
    public double getCostoPorHora() { return costoPorHora; }
    public void setCostoPorHora(double costoPorHora) { this.costoPorHora = costoPorHora; }
    
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
    
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
