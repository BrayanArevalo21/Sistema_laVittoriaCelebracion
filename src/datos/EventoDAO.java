package datos;

import database.Conexion;
import datos.interfaces.CrudSimpleInterface;
import entidades.Evento;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class EventoDAO implements CrudSimpleInterface<Evento> {
    private final Conexion CON;
    private PreparedStatement ps;
    private ResultSet rs;
    private boolean resp;

    public EventoDAO() {
        CON = Conexion.getInstancia();
    }

    @Override
    public List<Evento> listar(String texto) {
        List<Evento> registros = new ArrayList();
        try {
            ps = CON.conectar().prepareStatement(
                "SELECT e.*, p.nombre as nombre_cliente FROM evento e " +
                "INNER JOIN persona p ON e.cliente_id = p.id " +
                "WHERE e.nombre_evento LIKE ? AND e.activo = 1"
            );
            ps.setString(1, "%" + texto + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                Evento e = new Evento();
                e.setId(rs.getInt("id"));
                e.setClienteId(rs.getInt("cliente_id"));
                e.setNombreCliente(rs.getString("nombre_cliente"));
                e.setNombreEvento(rs.getString("nombre_evento"));
                e.setFechaEvento(rs.getString("fecha_evento"));
                e.setLugarEvento(rs.getString("lugar_evento"));
                e.setNumeroInvitados(rs.getInt("numero_invitados"));
                e.setPresupuestoTotal(rs.getDouble("presupuesto_total"));
                e.setEstado(rs.getString("estado"));
                e.setActivo(rs.getBoolean("activo"));
                registros.add(e);
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
    public boolean insertar(Evento obj) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement(
                "INSERT INTO evento (cliente_id, nombre_evento, fecha_evento, lugar_evento, " +
                "numero_invitados, presupuesto_total, estado, activo, fecha_registro) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, 1, NOW())"
            );
            ps.setInt(1, obj.getClienteId());
            ps.setString(2, obj.getNombreEvento());
            ps.setString(3, obj.getFechaEvento());
            ps.setString(4, obj.getLugarEvento());
            ps.setInt(5, obj.getNumeroInvitados());
            ps.setDouble(6, obj.getPresupuestoTotal());
            ps.setString(7, obj.getEstado());
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

    @Override
    public boolean actualizar(Evento obj) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement(
                "UPDATE evento SET cliente_id=?, nombre_evento=?, fecha_evento=?, lugar_evento=?, " +
                "numero_invitados=?, presupuesto_total=?, estado=? WHERE id=?"
            );
            ps.setInt(1, obj.getClienteId());
            ps.setString(2, obj.getNombreEvento());
            ps.setString(3, obj.getFechaEvento());
            ps.setString(4, obj.getLugarEvento());
            ps.setInt(5, obj.getNumeroInvitados());
            ps.setDouble(6, obj.getPresupuestoTotal());
            ps.setString(7, obj.getEstado());
            ps.setInt(8, obj.getId());
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

    @Override
    public boolean desactivar(int id) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement("UPDATE evento SET activo = 0 WHERE id = ?");
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

    @Override
    public boolean activar(int id) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement("UPDATE evento SET activo = 1 WHERE id = ?");
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

    @Override
    public int total() {
        int total = 0;
        try {
            ps = CON.conectar().prepareStatement("SELECT COUNT(*) FROM evento WHERE activo = 1");
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
    
    @Override
    public boolean existe(String texto) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement("SELECT nombre_evento FROM evento WHERE nombre_evento = ?");
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

    // Métodos adicionales que ya tienes y funcionan bien
    public Evento buscarPorId(int id) {
        Evento evento = null;
        try {
            ps = CON.conectar().prepareStatement(
                "SELECT e.*, p.nombre as nombre_cliente FROM evento e " +
                "INNER JOIN persona p ON e.cliente_id = p.id " +
                "WHERE e.id = ?"
            );
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                evento = new Evento();
                evento.setId(rs.getInt("id"));
                evento.setClienteId(rs.getInt("cliente_id"));
                evento.setNombreCliente(rs.getString("nombre_cliente"));
                evento.setNombreEvento(rs.getString("nombre_evento"));
                evento.setFechaEvento(rs.getString("fecha_evento"));
                evento.setLugarEvento(rs.getString("lugar_evento"));
                evento.setNumeroInvitados(rs.getInt("numero_invitados"));
                evento.setPresupuestoTotal(rs.getDouble("presupuesto_total"));
                evento.setEstado(rs.getString("estado"));
                evento.setActivo(rs.getBoolean("activo"));
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
        return evento;
    }

    public double obtenerPresupuesto(int eventoId) {
        double presupuesto = 0;
        try {
            ps = CON.conectar().prepareStatement("SELECT presupuesto_total FROM evento WHERE id = ?");
            ps.setInt(1, eventoId);
            rs = ps.executeQuery();
            if (rs.next()) presupuesto = rs.getDouble("presupuesto_total");
            ps.close();
            rs.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } finally {
            ps = null;
            rs = null;
            CON.desconectar();
        }
        return presupuesto;
    }
}