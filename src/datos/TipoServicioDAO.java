package datos;

import database.Conexion;
import datos.interfaces.CrudSimpleInterface;
import entidades.TipoServicio;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class TipoServicioDAO implements CrudSimpleInterface<TipoServicio> {
    private final Conexion CON;
    private PreparedStatement ps;
    private ResultSet rs;
    private boolean resp;

    public TipoServicioDAO() {
        CON = Conexion.getInstancia();
    }

    @Override
    public List<TipoServicio> listar(String texto) {
        List<TipoServicio> registros = new ArrayList();
        try {
            ps = CON.conectar().prepareStatement("SELECT * FROM tipo_servicio WHERE nombre LIKE ? AND activo = 1");
            ps.setString(1, "%" + texto + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                registros.add(new TipoServicio(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getBoolean("activo")
                ));
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
    
    // ========== NUEVO MÉTODO: listar con filtro de estado ==========
    public List<TipoServicio> listarConFiltro(String texto, String filtroEstado) {
        List<TipoServicio> registros = new ArrayList();
        String sql = "";
        
        // Construir SQL según el filtro
        if (filtroEstado.equals("Activos")) {
            sql = "SELECT * FROM tipo_servicio WHERE nombre LIKE ? AND activo = 1";
        } else if (filtroEstado.equals("Inactivos")) {
            sql = "SELECT * FROM tipo_servicio WHERE nombre LIKE ? AND activo = 0";
        } else { // Todos
            sql = "SELECT * FROM tipo_servicio WHERE nombre LIKE ?";
        }
        
        try {
            ps = CON.conectar().prepareStatement(sql);
            ps.setString(1, "%" + texto + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                registros.add(new TipoServicio(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getBoolean("activo")
                ));
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
    public boolean insertar(TipoServicio obj) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement("INSERT INTO tipo_servicio (nombre, descripcion, activo) VALUES (?, ?, 1)");
            ps.setString(1, obj.getNombre());
            ps.setString(2, obj.getDescripcion());
            
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
    public boolean actualizar(TipoServicio obj) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement("UPDATE tipo_servicio SET nombre = ?, descripcion = ? WHERE id = ?");
            ps.setString(1, obj.getNombre());
            ps.setString(2, obj.getDescripcion());
            ps.setInt(3, obj.getId());
            
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
            ps = CON.conectar().prepareStatement("UPDATE tipo_servicio SET activo = 0 WHERE id = ?");
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
            ps = CON.conectar().prepareStatement("UPDATE tipo_servicio SET activo = 1 WHERE id = ?");
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
            ps = CON.conectar().prepareStatement("SELECT COUNT(id) FROM tipo_servicio WHERE activo = 1");
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
            ps = CON.conectar().prepareStatement("SELECT nombre FROM tipo_servicio WHERE nombre = ?");
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
    
    // Método adicional: buscarPorId
    public TipoServicio buscarPorId(int id) {
        TipoServicio tipoServicio = null;
        try {
            ps = CON.conectar().prepareStatement("SELECT * FROM tipo_servicio WHERE id = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                tipoServicio = new TipoServicio(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getBoolean("activo")
                );
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
        return tipoServicio;
    }
    
    // Método adicional: totalActivos
    public int totalActivos() {
        int totalRegistros = 0;
        try {
            ps = CON.conectar().prepareStatement("SELECT COUNT(id) FROM tipo_servicio WHERE activo = 1");
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
    
    // Método adicional: totalInactivos
    public int totalInactivos() {
        int totalRegistros = 0;
        try {
            ps = CON.conectar().prepareStatement("SELECT COUNT(id) FROM tipo_servicio WHERE activo = 0");
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
}