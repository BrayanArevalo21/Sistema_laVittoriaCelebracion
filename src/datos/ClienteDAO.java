package datos;

import database.Conexion;
import entidades.Cliente;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class ClienteDAO {
    private final Conexion CON;
    private PreparedStatement ps;
    private ResultSet rs;
    private boolean resp;

    public ClienteDAO() {
        CON = Conexion.getInstancia();
    }

    public List<Cliente> listar(String texto) {
        List<Cliente> registros = new ArrayList();
        try {
            ps = CON.conectar().prepareStatement("SELECT * FROM persona WHERE tipo_persona = 'CLIENTE' AND nombre LIKE ? AND activo = 1");
            ps.setString(1, "%" + texto + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                registros.add(new Cliente(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("tipo_documento"),
                    rs.getString("num_documento"),
                    rs.getString("direccion"),
                    rs.getString("telefono"),
                    rs.getString("email"),
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

    public Cliente buscarPorId(int id) {
        Cliente cliente = null;
        try {
            ps = CON.conectar().prepareStatement("SELECT * FROM persona WHERE id = ? AND tipo_persona = 'CLIENTE'");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                cliente = new Cliente(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("tipo_documento"),
                    rs.getString("num_documento"),
                    rs.getString("direccion"),
                    rs.getString("telefono"),
                    rs.getString("email"),
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
        return cliente;
    }

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
            if (ps.executeUpdate() > 0) resp = true;
            ps.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } finally {
            ps = null;
            CON.desconectar();
        }
        return resp;
    }

    public boolean actualizar(Cliente obj) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement(
                "UPDATE persona SET nombre=?, tipo_documento=?, num_documento=?, direccion=?, telefono=?, email=? WHERE id=?"
            );
            ps.setString(1, obj.getNombre());
            ps.setString(2, obj.getTipoDocumento());
            ps.setString(3, obj.getNumDocumento());
            ps.setString(4, obj.getDireccion());
            ps.setString(5, obj.getTelefono());
            ps.setString(6, obj.getEmail());
            ps.setInt(7, obj.getId());
            if (ps.executeUpdate() > 0) resp = true;
            ps.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } finally {
            ps = null;
            CON.desconectar();
        }
        return resp;
    }

    public boolean desactivar(int id) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement("UPDATE persona SET activo = 0 WHERE id = ?");
            ps.setInt(1, id);
            if (ps.executeUpdate() > 0) resp = true;
            ps.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } finally {
            ps = null;
            CON.desconectar();
        }
        return resp;
    }

    public boolean activar(int id) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement("UPDATE persona SET activo = 1 WHERE id = ?");
            ps.setInt(1, id);
            if (ps.executeUpdate() > 0) resp = true;
            ps.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } finally {
            ps = null;
            CON.desconectar();
        }
        return resp;
    }

    public int total() {
        int total = 0;
        try {
            ps = CON.conectar().prepareStatement("SELECT COUNT(*) FROM persona WHERE tipo_persona = 'CLIENTE' AND activo = 1");
            rs = ps.executeQuery();
            if (rs.next()) total = rs.getInt(1);
            ps.close();
            rs.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } finally {
            ps = null;
            rs = null;
            CON.desconectar();
        }
        return total;
    }

    public boolean existe(String nombre) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement("SELECT nombre FROM persona WHERE nombre = ? AND tipo_persona = 'CLIENTE'");
            ps.setString(1, nombre);
            rs = ps.executeQuery();
            if (rs.next()) resp = true;
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