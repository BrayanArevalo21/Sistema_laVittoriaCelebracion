package entidades;

public class Pago {
    private int id;
    private int eventoId;
    private String nombreEvento;
    private String fechaPago;
    private double monto;
    private String metodoPago;
    private String referenciaPago;
    private String observaciones;
    private boolean activo;
    
    public Pago() {}
    
    public Pago(int id, int eventoId, String fechaPago, double monto, 
                String metodoPago, String referenciaPago, String observaciones, boolean activo) {
        this.id = id;
        this.eventoId = eventoId;
        this.fechaPago = fechaPago;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.referenciaPago = referenciaPago;
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
    
    public String getFechaPago() {
        return fechaPago;
    }
    
    public void setFechaPago(String fechaPago) {
        this.fechaPago = fechaPago;
    }
    
    public double getMonto() {
        return monto;
    }
    
    public void setMonto(double monto) {
        this.monto = monto;
    }
    
    public String getMetodoPago() {
        return metodoPago;
    }
    
    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }
    
    public String getReferenciaPago() {
        return referenciaPago;
    }
    
    public void setReferenciaPago(String referenciaPago) {
        this.referenciaPago = referenciaPago;
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