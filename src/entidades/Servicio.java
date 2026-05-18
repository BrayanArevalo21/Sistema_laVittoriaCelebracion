package entidades;

public class Servicio {
    private int id;
    private int tipoServicioId;
    private String nombreTipoServicio;  // ← Campo adicional para mostrar el nombre
    private String codigo;
    private String nombre;
    private double precioBase;
    private String descripcion;
    private boolean activo;
    private int cantidad;  // Para detalles del evento
    private double subtotal;  // Para detalles del evento
    
    // ========== CONSTRUCTORES ==========
    
    public Servicio() {
    }
    
    public Servicio(int id, int tipoServicioId, String codigo, String nombre, 
                    double precioBase, String descripcion, boolean activo) {
        this.id = id;
        this.tipoServicioId = tipoServicioId;
        this.codigo = codigo;
        this.nombre = nombre;
        this.precioBase = precioBase;
        this.descripcion = descripcion;
        this.activo = activo;
    }
    
    // ========== GETTERS Y SETTERS ==========
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getTipoServicioId() {
        return tipoServicioId;
    }
    
    public void setTipoServicioId(int tipoServicioId) {
        this.tipoServicioId = tipoServicioId;
    }
    
    public String getNombreTipoServicio() {
        return nombreTipoServicio;
    }
    
    public void setNombreTipoServicio(String nombreTipoServicio) {
        this.nombreTipoServicio = nombreTipoServicio;
    }
    
    public String getCodigo() {
        return codigo;
    }
    
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public double getPrecioBase() {
        return precioBase;
    }
    
    public void setPrecioBase(double precioBase) {
        this.precioBase = precioBase;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public boolean isActivo() {
        return activo;
    }
    
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
    public int getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    
    public double getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
    
    @Override
    public String toString() {
        return "Servicio{" + "id=" + id + ", nombre=" + nombre + ", precioBase=" + precioBase + '}';
    }
}