package datos;

import database.Conexion;
import datos.interfaces.CrudSimpleInterface;
import entidades.Alimento;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class AlimentoDAO implements CrudSimpleInterface<Alimento> {
    private final Conexion CON;
    private PreparedStatement ps;
    private ResultSet rs;
    private boolean resp;

    public AlimentoDAO() {
        CON = Conexion.getInstancia();
    }

    @Override
    public List<Alimento> listar(String texto) {
        List<Alimento> registros = new ArrayList();
        try {
            ps = CON.conectar().prepareStatement("SELECT * FROM alimento WHERE nombre LIKE ? AND activo = 1 ORDER BY id");
            ps.setString(1, "%" + texto + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                Alimento a = new Alimento();
                a.setId(rs.getInt("id"));
                a.setCategoriaAlimento(rs.getString("categoria_alimento"));
                a.setNombre(rs.getString("nombre"));
                a.setDescripcion(rs.getString("descripcion"));
                a.setPrecioUnitario(rs.getDouble("precio_unitario"));
                a.setCostoProveedor(rs.getDouble("costo_proveedor"));
                a.setUnidadMedida(rs.getString("unidad_medida"));
                a.setRequiereRefrigeracion(rs.getBoolean("requiere_refrigeracion"));
                a.setAlergenos(rs.getString("alergenos"));
                a.setActivo(rs.getBoolean("activo"));
                registros.add(a);
            }
            ps.close();
            rs.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al listar alimentos: " + e.getMessage());
        } finally {
            ps = null;
            rs = null;
            CON.desconectar();
        }
        return registros;
    }

    @Override
    public boolean insertar(Alimento obj) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement("INSERT INTO alimento (categoria_alimento, nombre, descripcion, precio_unitario, costo_proveedor, unidad_medida, requiere_refrigeracion, alergenos, activo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 1)");
            ps.setString(1, obj.getCategoriaAlimento());
            ps.setString(2, obj.getNombre());
            ps.setString(3, obj.getDescripcion());
            ps.setDouble(4, obj.getPrecioUnitario());
            ps.setDouble(5, obj.getCostoProveedor());
            ps.setString(6, obj.getUnidadMedida());
            ps.setBoolean(7, obj.isRequiereRefrigeracion());
            ps.setString(8, obj.getAlergenos());
            if (ps.executeUpdate() > 0) {
                resp = true;
            }
            ps.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al insertar alimento: " + e.getMessage());
        } finally {
            ps = null;
            CON.desconectar();
        }
        return resp;
    }

    @Override
    public boolean actualizar(Alimento obj) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement("UPDATE alimento SET categoria_alimento = ?, nombre = ?, descripcion = ?, precio_unitario = ?, costo_proveedor = ?, unidad_medida = ?, requiere_refrigeracion = ?, alergenos = ? WHERE id = ?");
            ps.setString(1, obj.getCategoriaAlimento());
            ps.setString(2, obj.getNombre());
            ps.setString(3, obj.getDescripcion());
            ps.setDouble(4, obj.getPrecioUnitario());
            ps.setDouble(5, obj.getCostoProveedor());
            ps.setString(6, obj.getUnidadMedida());
            ps.setBoolean(7, obj.isRequiereRefrigeracion());
            ps.setString(8, obj.getAlergenos());
            ps.setInt(9, obj.getId());
            if (ps.executeUpdate() > 0) {
                resp = true;
            }
            ps.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar alimento: " + e.getMessage());
        } finally {
            ps = null;
            CON.desconectar();
        }
        return resp;
    }

    @Override
    public boolean desactivar(int id) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement("UPDATE alimento SET activo = 0 WHERE id = ?");
            ps.setInt(1, id);
            if (ps.executeUpdate() > 0) {
                resp = true;
            }
            ps.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al desactivar alimento: " + e.getMessage());
        } finally {
            ps = null;
            CON.desconectar();
        }
        return resp;
    }

    @Override
    public boolean activar(int id) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement("UPDATE alimento SET activo = 1 WHERE id = ?");
            ps.setInt(1, id);
            if (ps.executeUpdate() > 0) {
                resp = true;
            }
            ps.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al activar alimento: " + e.getMessage());
        } finally {
            ps = null;
            CON.desconectar();
        }
        return resp;
    }

    @Override
    public int total() {
        int totalRegistros = 0;
        try {
            ps = CON.conectar().prepareStatement("SELECT COUNT(id) FROM alimento WHERE activo = 1");
            rs = ps.executeQuery();
            if (rs.next()) {
                totalRegistros = rs.getInt(1);
            }
            ps.close();
            rs.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al contar alimentos: " + e.getMessage());
        } finally {
            ps = null;
            rs = null;
            CON.desconectar();
        }
        return totalRegistros;
    }

    @Override
    public boolean existe(String texto) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement("SELECT nombre FROM alimento WHERE nombre = ?");
            ps.setString(1, texto);
            rs = ps.executeQuery();
            if (rs.next()) {
                resp = true;
            }
            ps.close();
            rs.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al verificar existencia: " + e.getMessage());
        } finally {
            ps = null;
            rs = null;
            CON.desconectar();
        }
        return resp;
    }
    
   
    
    public Alimento buscarPorId(int id) {
        Alimento alimento = null;
        try {
            ps = CON.conectar().prepareStatement("SELECT * FROM alimento WHERE id = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                alimento = new Alimento();
                alimento.setId(rs.getInt("id"));
                alimento.setCategoriaAlimento(rs.getString("categoria_alimento"));
                alimento.setNombre(rs.getString("nombre"));
                alimento.setDescripcion(rs.getString("descripcion"));
                alimento.setPrecioUnitario(rs.getDouble("precio_unitario"));
                alimento.setCostoProveedor(rs.getDouble("costo_proveedor"));
                alimento.setUnidadMedida(rs.getString("unidad_medida"));
                alimento.setRequiereRefrigeracion(rs.getBoolean("requiere_refrigeracion"));
                alimento.setAlergenos(rs.getString("alergenos"));
                alimento.setActivo(rs.getBoolean("activo"));
            }
            
            ps.close();
            rs.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al buscar alimento por ID: " + e.getMessage());
        } finally {
            ps = null;
            rs = null;
            CON.desconectar();
        }
        return alimento;
    }
}