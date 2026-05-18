package datos;

import database.Conexion;
import datos.interfaces.CrudSimpleInterface;
import entidades.Usuario;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class UsuarioDAO implements CrudSimpleInterface<Usuario> {
    private final Conexion CON;
    private PreparedStatement ps;
    private ResultSet rs;
    private boolean resp;

    public UsuarioDAO() {
        CON = Conexion.getInstancia();
    }

    @Override
    public List<Usuario> listar(String texto) {
        List<Usuario> registros = new ArrayList();
        try {
            ps = CON.conectar().prepareStatement(
                "SELECT u.*, r.nombre as rol_nombre FROM usuario u " +
                "INNER JOIN rol r ON u.rol_id = r.id " +
                "WHERE u.nombre LIKE ? AND u.activo = 1"
            );
            ps.setString(1, "%" + texto + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setRolId(rs.getInt("rol_id"));
                u.setNombreRol(rs.getString("rol_nombre"));
                u.setNombre(rs.getString("nombre"));
                u.setTipoDocumento(rs.getString("tipo_documento"));
                u.setNumDocumento(rs.getString("num_documento"));
                u.setTelefono(rs.getString("telefono"));
                u.setEmail(rs.getString("email"));
                u.setClave(rs.getString("clave"));
                u.setActivo(rs.getBoolean("activo"));
                registros.add(u);
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
    public boolean insertar(Usuario obj) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement(
                "INSERT INTO usuario (rol_id, nombre, tipo_documento, num_documento, telefono, email, clave, activo) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, 1)"
            );
            ps.setInt(1, obj.getRolId());
            ps.setString(2, obj.getNombre());
            ps.setString(3, obj.getTipoDocumento());
            ps.setString(4, obj.getNumDocumento());
            ps.setString(5, obj.getTelefono());
            ps.setString(6, obj.getEmail());
            ps.setString(7, obj.getClave());
            
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
    public boolean actualizar(Usuario obj) {
        resp = false;
        try {
            String sql = "UPDATE usuario SET rol_id = ?, nombre = ?, tipo_documento = ?, num_documento = ?, telefono = ?, email = ?";
            
            if (obj.getClave() != null && !obj.getClave().isEmpty()) {
                sql += ", clave = ?";
            }
            
            sql += " WHERE id = ?";
            
            ps = CON.conectar().prepareStatement(sql);
            ps.setInt(1, obj.getRolId());
            ps.setString(2, obj.getNombre());
            ps.setString(3, obj.getTipoDocumento());
            ps.setString(4, obj.getNumDocumento());
            ps.setString(5, obj.getTelefono());
            ps.setString(6, obj.getEmail());
            
            if (obj.getClave() != null && !obj.getClave().isEmpty()) {
                ps.setString(7, obj.getClave());
                ps.setInt(8, obj.getId());
            } else {
                ps.setInt(7, obj.getId());
            }
            
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
            ps = CON.conectar().prepareStatement("UPDATE usuario SET activo = 0 WHERE id = ?");
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
            ps = CON.conectar().prepareStatement("UPDATE usuario SET activo = 1 WHERE id = ?");
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
            ps = CON.conectar().prepareStatement("SELECT COUNT(id) FROM usuario WHERE activo = 1");
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
            ps = CON.conectar().prepareStatement("SELECT nombre FROM usuario WHERE nombre = ?");
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
    
    public boolean existePorEmail(String email) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement("SELECT email FROM usuario WHERE email = ?");
            ps.setString(1, email);
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
    
    public Usuario buscarPorId(int id) {
        Usuario usuario = null;
        try {
            ps = CON.conectar().prepareStatement(
                "SELECT u.*, r.nombre as rol_nombre FROM usuario u " +
                "INNER JOIN rol r ON u.rol_id = r.id WHERE u.id = ?"
            );
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setRolId(rs.getInt("rol_id"));
                usuario.setNombreRol(rs.getString("rol_nombre"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setTipoDocumento(rs.getString("tipo_documento"));
                usuario.setNumDocumento(rs.getString("num_documento"));
                usuario.setTelefono(rs.getString("telefono"));
                usuario.setEmail(rs.getString("email"));
                usuario.setClave(rs.getString("clave"));
                usuario.setActivo(rs.getBoolean("activo"));
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
        return usuario;
    }
}