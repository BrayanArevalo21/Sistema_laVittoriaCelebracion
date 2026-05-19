package datos;

import database.Conexion;
import datos.interfaces.CrudSimpleInterface;
import entidades.Proveedor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class ProveedorDAO implements CrudSimpleInterface<Proveedor> {
    private final Conexion CON;
    private PreparedStatement ps;
    private ResultSet rs;
    private boolean resp;

    public ProveedorDAO() {
        CON = Conexion.getInstancia();
    }

    @Override
    public List<Proveedor> listar(String texto) {
        List<Proveedor> registros = new ArrayList();
        try {
            ps = CON.conectar().prepareStatement(
                "SELECT * FROM persona WHERE tipo_persona = 'PROVEEDOR' AND nombre LIKE ? AND activo = 1"
            );
            ps.setString(1, "%" + texto + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                Proveedor p = new Proveedor();
                p.setId(rs.getInt("id"));
                p.setTipoPersona(rs.getString("tipo_persona"));
                p.setNombre(rs.getString("nombre"));
                p.setTipoDocumento(rs.getString("tipo_documento"));
                p.setNumDocumento(rs.getString("num_documento"));
                p.setDireccion(rs.getString("direccion"));
                p.setTelefono(rs.getString("telefono"));
                p.setEmail(rs.getString("email"));
                p.setContacto(rs.getString("contacto"));
                p.setActivo(rs.getBoolean("activo"));
                registros.add(p);
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
    
    // ========== MÉTODO CON FILTRO DE ESTADO ==========
    public List<Proveedor> listarConFiltro(String texto, String filtroEstado) {
        List<Proveedor> registros = new ArrayList();
        String sql = "";
        
        if (filtroEstado.equals("Activos")) {
            sql = "SELECT * FROM persona WHERE tipo_persona = 'PROVEEDOR' AND nombre LIKE ? AND activo = 1";
        } else if (filtroEstado.equals("Inactivos")) {
            sql = "SELECT * FROM persona WHERE tipo_persona = 'PROVEEDOR' AND nombre LIKE ? AND activo = 0";
        } else {
            sql = "SELECT * FROM persona WHERE tipo_persona = 'PROVEEDOR' AND nombre LIKE ?";
        }
        
        try {
            ps = CON.conectar().prepareStatement(sql);
            ps.setString(1, "%" + texto + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                Proveedor p = new Proveedor();
                p.setId(rs.getInt("id"));
                p.setTipoPersona(rs.getString("tipo_persona"));
                p.setNombre(rs.getString("nombre"));
                p.setTipoDocumento(rs.getString("tipo_documento"));
                p.setNumDocumento(rs.getString("num_documento"));
                p.setDireccion(rs.getString("direccion"));
                p.setTelefono(rs.getString("telefono"));
                p.setEmail(rs.getString("email"));
                p.setContacto(rs.getString("contacto"));
                p.setActivo(rs.getBoolean("activo"));
                registros.add(p);
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
    public boolean insertar(Proveedor obj) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement(
                "INSERT INTO persona (tipo_persona, nombre, tipo_documento, num_documento, direccion, telefono, email, contacto, activo) "
                + "VALUES ('PROVEEDOR', ?, ?, ?, ?, ?, ?, ?, 1)"
            );
            ps.setString(1, obj.getNombre());
            ps.setString(2, obj.getTipoDocumento());
            ps.setString(3, obj.getNumDocumento());
            ps.setString(4, obj.getDireccion());
            ps.setString(5, obj.getTelefono());
            ps.setString(6, obj.getEmail());
            ps.setString(7, obj.getContacto());
            
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
    public boolean actualizar(Proveedor obj) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement(
                "UPDATE persona SET nombre = ?, tipo_documento = ?, num_documento = ?, direccion = ?, telefono = ?, email = ?, contacto = ? WHERE id = ?"
            );
            ps.setString(1, obj.getNombre());
            ps.setString(2, obj.getTipoDocumento());
            ps.setString(3, obj.getNumDocumento());
            ps.setString(4, obj.getDireccion());
            ps.setString(5, obj.getTelefono());
            ps.setString(6, obj.getEmail());
            ps.setString(7, obj.getContacto());
            ps.setInt(8, obj.getId());
            
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
            ps = CON.conectar().prepareStatement("UPDATE persona SET activo = 0 WHERE id = ?");
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
            ps = CON.conectar().prepareStatement("UPDATE persona SET activo = 1 WHERE id = ?");
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
            ps = CON.conectar().prepareStatement("SELECT COUNT(id) FROM persona WHERE tipo_persona = 'PROVEEDOR' AND activo = 1");
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
            ps = CON.conectar().prepareStatement("SELECT nombre FROM persona WHERE nombre = ? AND tipo_persona = 'PROVEEDOR'");
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
    
    // Método adicional: existePorDocumento
    public boolean existePorDocumento(String numDocumento) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement("SELECT num_documento FROM persona WHERE num_documento = ? AND tipo_persona = 'PROVEEDOR'");
            ps.setString(1, numDocumento);
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
    public Proveedor buscarPorId(int id) {
        Proveedor proveedor = null;
        try {
            ps = CON.conectar().prepareStatement("SELECT * FROM persona WHERE id = ? AND tipo_persona = 'PROVEEDOR'");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                proveedor = new Proveedor();
                proveedor.setId(rs.getInt("id"));
                proveedor.setTipoPersona(rs.getString("tipo_persona"));
                proveedor.setNombre(rs.getString("nombre"));
                proveedor.setTipoDocumento(rs.getString("tipo_documento"));
                proveedor.setNumDocumento(rs.getString("num_documento"));
                proveedor.setDireccion(rs.getString("direccion"));
                proveedor.setTelefono(rs.getString("telefono"));
                proveedor.setEmail(rs.getString("email"));
                proveedor.setContacto(rs.getString("contacto"));
                proveedor.setActivo(rs.getBoolean("activo"));
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
        return proveedor;
    }
}