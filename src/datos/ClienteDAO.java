package datos;

import database.Conexion;
import datos.interfaces.CrudSimpleInterface;
import entidades.Cliente;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class ClienteDAO implements CrudSimpleInterface<Cliente> {
    private final Conexion CON;
    private PreparedStatement ps;
    private ResultSet rs;
    private boolean resp;

    public ClienteDAO() {
        CON = Conexion.getInstancia();
    }

    @Override
    public List<Cliente> listar(String texto) {
        List<Cliente> registros = new ArrayList();
        try {
            ps = CON.conectar().prepareStatement(
                "SELECT * FROM persona WHERE tipo_persona = 'CLIENTE' AND nombre LIKE ? AND activo = 1"
            );
            ps.setString(1, "%" + texto + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                Cliente c = new Cliente();
                c.setId(rs.getInt("id"));
                c.setNombre(rs.getString("nombre"));
                c.setTipoDocumento(rs.getString("tipo_documento"));
                c.setNumDocumento(rs.getString("num_documento"));
                c.setDireccion(rs.getString("direccion"));
                c.setTelefono(rs.getString("telefono"));
                c.setEmail(rs.getString("email"));
                c.setActivo(rs.getBoolean("activo"));
                registros.add(c);
            }
            ps.close();
            rs.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en listar: " + e.getMessage());
        } finally {
            ps = null;
            rs = null;
            CON.desconectar();
        }
        return registros;
    }
    
    // Listar todos (activos e inactivos)
    public List<Cliente> listarTodos(String texto) {
        List<Cliente> registros = new ArrayList();
        try {
            ps = CON.conectar().prepareStatement(
                "SELECT * FROM persona WHERE tipo_persona = 'CLIENTE' AND nombre LIKE ?"
            );
            ps.setString(1, "%" + texto + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                Cliente c = new Cliente();
                c.setId(rs.getInt("id"));
                c.setNombre(rs.getString("nombre"));
                c.setTipoDocumento(rs.getString("tipo_documento"));
                c.setNumDocumento(rs.getString("num_documento"));
                c.setDireccion(rs.getString("direccion"));
                c.setTelefono(rs.getString("telefono"));
                c.setEmail(rs.getString("email"));
                c.setActivo(rs.getBoolean("activo"));
                registros.add(c);
            }
            ps.close();
            rs.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en listarTodos: " + e.getMessage());
        } finally {
            ps = null;
            rs = null;
            CON.desconectar();
        }
        return registros;
    }

    @Override
    public boolean insertar(Cliente obj) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement(
                "INSERT INTO persona (tipo_persona, nombre, tipo_documento, num_documento, direccion, telefono, email, activo) "
                + "VALUES ('CLIENTE', ?, ?, ?, ?, ?, ?, 1)"
            );
            ps.setString(1, obj.getNombre());
            ps.setString(2, obj.getTipoDocumento());
            ps.setString(3, obj.getNumDocumento());
            ps.setString(4, obj.getDireccion());
            ps.setString(5, obj.getTelefono());
            ps.setString(6, obj.getEmail());
            
            if (ps.executeUpdate() > 0) {
                resp = true;
            }
            ps.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en insertar: " + e.getMessage());
        } finally {
            ps = null;
            CON.desconectar();
        }
        return resp;
    }

    @Override
    public boolean actualizar(Cliente obj) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement(
                "UPDATE persona SET nombre = ?, tipo_documento = ?, num_documento = ?, direccion = ?, telefono = ?, email = ? WHERE id = ?"
            );
            ps.setString(1, obj.getNombre());
            ps.setString(2, obj.getTipoDocumento());
            ps.setString(3, obj.getNumDocumento());
            ps.setString(4, obj.getDireccion());
            ps.setString(5, obj.getTelefono());
            ps.setString(6, obj.getEmail());
            ps.setInt(7, obj.getId());
            
            if (ps.executeUpdate() > 0) {
                resp = true;
            }
            ps.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en actualizar: " + e.getMessage());
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
            JOptionPane.showMessageDialog(null, "Error en desactivar: " + e.getMessage());
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
            JOptionPane.showMessageDialog(null, "Error en activar: " + e.getMessage());
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
            ps = CON.conectar().prepareStatement("SELECT COUNT(id) FROM persona WHERE tipo_persona = 'CLIENTE' AND activo = 1");
            rs = ps.executeQuery();
            if (rs.next()) {
                totalRegistros = rs.getInt(1);
            }
            ps.close();
            rs.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en total: " + e.getMessage());
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
            ps = CON.conectar().prepareStatement("SELECT nombre FROM persona WHERE nombre = ? AND tipo_persona = 'CLIENTE'");
            ps.setString(1, texto);
            rs = ps.executeQuery();
            if (rs.next()) {
                resp = true;
            }
            ps.close();
            rs.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en existe: " + e.getMessage());
        } finally {
            ps = null;
            rs = null;
            CON.desconectar();
        }
        return resp;
    }
    
    public Cliente buscarPorId(int id) {
        Cliente cliente = null;
        try {
            ps = CON.conectar().prepareStatement("SELECT * FROM persona WHERE id = ? AND tipo_persona = 'CLIENTE'");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                cliente = new Cliente();
                cliente.setId(rs.getInt("id"));
                cliente.setNombre(rs.getString("nombre"));
                cliente.setTipoDocumento(rs.getString("tipo_documento"));
                cliente.setNumDocumento(rs.getString("num_documento"));
                cliente.setDireccion(rs.getString("direccion"));
                cliente.setTelefono(rs.getString("telefono"));
                cliente.setEmail(rs.getString("email"));
                cliente.setActivo(rs.getBoolean("activo"));
            }
            ps.close();
            rs.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en buscarPorId: " + e.getMessage());
        } finally {
            ps = null;
            rs = null;
            CON.desconectar();
        }
        return cliente;
    }
}