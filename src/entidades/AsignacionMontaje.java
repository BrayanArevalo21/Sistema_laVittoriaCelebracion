package entidades;

public class AsignacionMontaje {
    private int id;
    private int eventoId;
    private String nombreEvento;
    private int personalId;
    private String nombrePersonal;
    private String rolAsignado;
    private double horasTrabajadas;
    private double costoTotal;
    private String fechaAsignacion;
    private String observaciones;
    private boolean activo;
    
    public AsignacionMontaje() {}
    
    public AsignacionMontaje(int id, int eventoId, int personalId, String rolAsignado,
                             double horasTrabajadas, double costoTotal, String fechaAsignacion,
                             String observaciones, boolean activo) {
        this.id = id;
        this.eventoId = eventoId;
        this.personalId = personalId;
        this.rolAsignado = rolAsignado;
        this.horasTrabajadas = horasTrabajadas;
        this.costoTotal = costoTotal;
        this.fechaAsignacion = fechaAsignacion;
        this.observaciones = observaciones;
        this.activo = activo;
    }
    
    // Getters y Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getEventoId() {
        return eventoId;
    }
    
    public void setEventoId(int eventoId) {
        this.eventoId = eventoId;
    }
    
    public String getNombreEvento() {
        return nombreEvento;
    }
    
    public void setNombreEvento(String nombreEvento) {
        this.nombreEvento = nombreEvento;
    }
    
    public int getPersonalId() {
        return personalId;
    }
    
    public void setPersonalId(int personalId) {
        this.personalId = personalId;
    }
    
    public String getNombrePersonal() {
        return nombrePersonal;
    }
    
    public void setNombrePersonal(String nombrePersonal) {
        this.nombrePersonal = nombrePersonal;
    }
    
    public String getRolAsignado() {
        return rolAsignado;
    }
    
    public void setRolAsignado(String rolAsignado) {
        this.rolAsignado = rolAsignado;
    }
    
    public double getHorasTrabajadas() {
        return horasTrabajadas;
    }
    
    public void setHorasTrabajadas(double horasTrabajadas) {
        this.horasTrabajadas = horasTrabajadas;
    }
    
    public double getCostoTotal() {
        return costoTotal;
    }
    
    public void setCostoTotal(double costoTotal) {
        this.costoTotal = costoTotal;
    }
    
    public String getFechaAsignacion() {
        return fechaAsignacion;
    }
    
    public void setFechaAsignacion(String fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }
    
    public String getObservaciones() {
        return observaciones;
    }
    
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    public boolean isActivo() {
        return activo;
    }
    
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}