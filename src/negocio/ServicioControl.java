package negocio;

import datos.ServicioDAO;
import entidades.Servicio;
import java.text.DecimalFormat;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class ServicioControl {
    private final ServicioDAO DATOS;
    private DefaultTableModel modeloTabla;
    public int registroMostrado;
    private DecimalFormat formatoCOP = new DecimalFormat("#,###");
    
    public ServicioControl() {
        this.DATOS = new ServicioDAO();
        this.registroMostrado = 0;
    }
    
    // ========== LISTAR CON FILTRO ==========
    public DefaultTableModel listarConFiltro(String texto, String filtroEstado) {
        List<Servicio> lista = DATOS.listarConFiltro(texto, filtroEstado);
        
        String[] titulos = {"ID", "TIPO SERVICIO", "CÓDIGO", "NOMBRE", "PRECIO", "ESTADO"};
        modeloTabla = new DefaultTableModel(null, titulos);
        registroMostrado = 0;
        
        for (Servicio s : lista) {
            String estado = s.isActivo() ? "Activo" : "Inactivo";
            Object[] fila = {
                s.getId(),
                s.getNombreTipoServicio(),
                s.getCodigo(),
                s.getNombre(),
                "$" + formatoCOP.format(s.getPrecioBase()),
                estado
            };
            modeloTabla.addRow(fila);
            registroMostrado++;
        }
        return modeloTabla;
    }
    
    // ========== LISTAR NORMAL (SOLO ACTIVOS) ==========
    public DefaultTableModel listar(String texto) {
        List<Servicio> lista = DATOS.listar(texto);
        
        String[] titulos = {"ID", "TIPO SERVICIO", "CÓDIGO", "NOMBRE", "PRECIO", "ESTADO"};
        modeloTabla = new DefaultTableModel(null, titulos);
        registroMostrado = 0;
        
        for (Servicio s : lista) {
            String estado = s.isActivo() ? "Activo" : "Inactivo";
            Object[] fila = {
                s.getId(),
                s.getNombreTipoServicio(),
                s.getCodigo(),
                s.getNombre(),
                "$" + formatoCOP.format(s.getPrecioBase()),
                estado
            };
            modeloTabla.addRow(fila);
            registroMostrado++;
        }
        return modeloTabla;
    }
    
    public String insertar(int tipoServicioId, String codigo, String nombre, double precio, String descripcion) {
        if (DATOS.existe(nombre)) {
            return "El servicio ya existe";
        }
        if (!codigo.isEmpty() && DATOS.existePorCodigo(codigo)) {
            return "El código ya existe";
        }
        Servicio obj = new Servicio();
        obj.setTipoServicioId(tipoServicioId);
        obj.setCodigo(codigo);
        obj.setNombre(nombre);
        obj.setPrecioBase(precio);
        obj.setDescripcion(descripcion);
        return DATOS.insertar(obj) ? "OK" : "Error en el registro";
    }
    
    public String actualizar(int id, int tipoServicioId, String codigo, String nombre, 
                             String nombreAnt, double precio, String descripcion) {
        if (!nombre.equals(nombreAnt) && DATOS.existe(nombre)) {
            return "El servicio ya existe";
        }
        if (!codigo.isEmpty() && DATOS.existePorCodigo(codigo)) {
            return "El código ya existe";
        }
        Servicio obj = new Servicio();
        obj.setId(id);
        obj.setTipoServicioId(tipoServicioId);
        obj.setCodigo(codigo);
        obj.setNombre(nombre);
        obj.setPrecioBase(precio);
        obj.setDescripcion(descripcion);
        return DATOS.actualizar(obj) ? "OK" : "Error en la actualización";
    }
    
    public String desactivar(int id) {
        return DATOS.desactivar(id) ? "OK" : "No se puede desactivar";
    }
    
    public String activar(int id) {
        return DATOS.activar(id) ? "OK" : "No se puede activar";
    }
    
    public int total() {
        return DATOS.total();
    }
    
    public int totalMostrados() {
        return registroMostrado;
    }
}