package negocio;

import datos.AlimentoDAO;
import entidades.Alimento;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class AlimentoControl {
    private final AlimentoDAO DATOS;
    private DefaultTableModel modeloTabla;
    public int registroMostrado;

    public AlimentoControl() {
        this.DATOS = new AlimentoDAO();
        this.registroMostrado = 0;
    }

    public DefaultTableModel listar(String texto) {
        List<Alimento> lista = DATOS.listar(texto);
        String[] titulos = {"ID", "CATEGORÍA", "NOMBRE", "PRECIO", "COSTO PROV", "ESTADO"};
        modeloTabla = new DefaultTableModel(null, titulos);
        registroMostrado = 0;

        for (Alimento item : lista) {
            String estado = item.isActivo() ? "ACTIVO" : "INACTIVO";
            Object[] fila = {item.getId(), item.getCategoriaAlimento(), item.getNombre(),
                             item.getPrecioUnitario(), item.getCostoProveedor(), estado};
            modeloTabla.addRow(fila);
            registroMostrado++;
        }
        return modeloTabla;
    }

    public String insertar(String categoria, String nombre, String descripcion, 
                           double precioUnitario, double costoProveedor) {
        if (DATOS.existe(nombre)) {
            return "El alimento ya existe";
        }
        Alimento obj = new Alimento();
        obj.setCategoriaAlimento(categoria);
        obj.setNombre(nombre);
        obj.setDescripcion(descripcion);
        obj.setPrecioUnitario(precioUnitario);
        obj.setCostoProveedor(costoProveedor);
        return DATOS.insertar(obj) ? "OK" : "Error en el registro";
    }

    public String actualizar(int id, String categoria, String nombre, String nombreAnt,
                             String descripcion, double precioUnitario, double costoProveedor) {
        if (!nombre.equals(nombreAnt) && DATOS.existe(nombre)) {
            return "El alimento ya existe";
        }
        Alimento obj = new Alimento();
        obj.setId(id);
        obj.setCategoriaAlimento(categoria);
        obj.setNombre(nombre);
        obj.setDescripcion(descripcion);
        obj.setPrecioUnitario(precioUnitario);
        obj.setCostoProveedor(costoProveedor);
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