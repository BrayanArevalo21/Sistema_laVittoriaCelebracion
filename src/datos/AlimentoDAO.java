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
            ps = CON.conectar().prepareStatement("SELECT * FROM alimento WHERE nombre LIKE ? AND activo = 1");
            ps.setString(1, "%" + texto + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                registros.add(new Alimento(rs.getInt("id"), rs.getString("categoria_alimento"),
                        rs.getString("nombre"), rs.getString("descripcion"),
                        rs.getDouble("precio_unitario"), rs.getDouble("costo_proveedor"),
                        rs.getBoolean("activo")));
            }
            ps.close();
            rs.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
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
            ps = CON.conectar().prepareStatement("INSERT INTO alimento (categoria_alimento, nombre, descripcion, precio_unitario, costo_proveedor, activo) VALUES (?, ?, ?, ?, ?, 1)");
            ps.setString(1, obj.getCategoriaAlimento());
            ps.setString(2, obj.getNombre());
            ps.setString(3, obj.getDescripcion());
            ps.setDouble(4, obj.getPrecioUnitario());
            ps.setDouble(5, obj.getCostoProveedor());
            if (ps.executeUpdate() > 0) {
                resp = true;
            }
            ps.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
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
            ps = CON.conectar().prepareStatement("UPDATE alimento SET categoria_alimento = ?, nombre = ?, descripcion = ?, precio_unitario = ?, costo_proveedor = ? WHERE id = ?");
            ps.setString(1, obj.getCategoriaAlimento());
            ps.setString(2, obj.getNombre());
            ps.setString(3, obj.getDescripcion());
            ps.setDouble(4, obj.getPrecioUnitario());
            ps.setDouble(5, obj.getCostoProveedor());
            ps.setInt(6, obj.getId());
            if (ps.executeUpdate() > 0) {
                resp = true;
            }
            ps.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
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
            JOptionPane.showMessageDialog(null, e.getMessage());
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
            JOptionPane.showMessageDialog(null, e.getMessage());
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
            JOptionPane.showMessageDialog(null, e.getMessage());
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
            JOptionPane.showMessageDialog(null, e.getMessage());
        } finally {
            ps = null;
            rs = null;
            CON.desconectar();
        }
        return resp;
    }
}