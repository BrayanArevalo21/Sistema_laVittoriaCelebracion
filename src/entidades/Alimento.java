package entidades;

public class Alimento {
    private int id;
    private String categoriaAlimento;
    private String nombre;
    private String descripcion;
    private double precioUnitario;
    private double costoProveedor;
    private String unidadMedida;
    private boolean requiereRefrigeracion;
    private String alergenos;
    private boolean activo;
    
    public Alimento() {
    }
    
    public Alimento(int id, String categoriaAlimento, String nombre, String descripcion,
                    double precioUnitario, double costoProveedor, String unidadMedida,
                    boolean requiereRefrigeracion, String alergenos, boolean activo) {
        this.id = id;
        this.categoriaAlimento = categoriaAlimento;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioUnitario = precioUnitario;
        this.costoProveedor = costoProveedor;
        this.unidadMedida = unidadMedida;
        this.requiereRefrigeracion = requiereRefrigeracion;
        this.alergenos = alergenos;
        this.activo = activo;
    }
    
    // ========== GETTERS Y SETTERS ==========
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getCategoriaAlimento() {
        return categoriaAlimento;
    }
    
    public void setCategoriaAlimento(String categoriaAlimento) {
        this.categoriaAlimento = categoriaAlimento;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public double getPrecioUnitario() {
        return precioUnitario;
    }
    
    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
    
    public double getCostoProveedor() {
        return costoProveedor;
    }
    
    public void setCostoProveedor(double costoProveedor) {
        this.costoProveedor = costoProveedor;
    }
    
    public String getUnidadMedida() {
        return unidadMedida;
    }
    
    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }
    
    public boolean isRequiereRefrigeracion() {
        return requiereRefrigeracion;
    }
    
    public void setRequiereRefrigeracion(boolean requiereRefrigeracion) {
        this.requiereRefrigeracion = requiereRefrigeracion;
    }
    
    public String getAlergenos() {
        return alergenos;
    }
    
    public void setAlergenos(String alergenos) {
        this.alergenos = alergenos;
    }
    
    public boolean isActivo() {
        return activo;
    }
    
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
    @Override
    public String toString() {
        return nombre;
    }
}