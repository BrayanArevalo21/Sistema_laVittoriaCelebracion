package datos;

import database.Conexion;
import datos.interfaces.CrudSimpleInterface;
import entidades.Servicio;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class ServicioDAO implements CrudSimpleInterface<Servicio> {
    private final Conexion CON;
    private PreparedStatement ps;
    private ResultSet rs;
    private boolean resp;

    public ServicioDAO() {
        CON = Conexion.getInstancia();
    }

    @Override
    public List<Servicio> listar(String texto) {
        List<Servicio> registros = new ArrayList();
        try {
            ps = CON.conectar().prepareStatement(
                "SELECT s.*, ts.nombre as tipo FROM servicio s " +
                "INNER JOIN tipo_servicio ts ON s.tipo_servicio_id = ts.id " +
                "WHERE s.nombre LIKE ? AND s.activo = 1");
            ps.setString(1, "%" + texto + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                registros.add(new Servicio(rs.getInt("id"), rs.getInt("tipo_servicio_id"),
                        rs.getString("codigo"), rs.getString("nombre"), rs.getDouble("precio_base"),
                        rs.getString("descripcion"), rs.getBoolean("activo")));
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
    public boolean insertar(Servicio obj) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement("INSERT INTO servicio (tipo_servicio_id, codigo, nombre, precio_base, descripcion, activo) VALUES (?, ?, ?, ?, ?, 1)");
            ps.setInt(1, obj.getTipoServicioId());
            ps.setString(2, obj.getCodigo());
            ps.setString(3, obj.getNombre());
            ps.setDouble(4, obj.getPrecioBase());
            ps.setString(5, obj.getDescripcion());
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
    public boolean actualizar(Servicio obj) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement("UPDATE servicio SET tipo_servicio_id = ?, codigo = ?, nombre = ?, precio_base = ?, descripcion = ? WHERE id = ?");
            ps.setInt(1, obj.getTipoServicioId());
            ps.setString(2, obj.getCodigo());
            ps.setString(3, obj.getNombre());
            ps.setDouble(4, obj.getPrecioBase());
            ps.setString(5, obj.getDescripcion());
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
            ps = CON.conectar().prepareStatement("UPDATE servicio SET activo = 0 WHERE id = ?");
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
            ps = CON.conectar().prepareStatement("UPDATE servicio SET activo = 1 WHERE id = ?");
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
            ps = CON.conectar().prepareStatement("SELECT COUNT(id) FROM servicio WHERE activo = 1");
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
            ps = CON.conectar().prepareStatement("SELECT nombre FROM servicio WHERE nombre = ?");
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
