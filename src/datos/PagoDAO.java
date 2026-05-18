package datos;

import database.Conexion;
import datos.interfaces.CrudSimpleInterface;
import entidades.Pago;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class PagoDAO implements CrudSimpleInterface<Pago> {
    private final Conexion CON;
    private PreparedStatement ps;
    private ResultSet rs;
    private boolean resp;

    public PagoDAO() {
        CON = Conexion.getInstancia();
    }

    @Override
    public List<Pago> listar(String texto) {
        List<Pago> registros = new ArrayList();
        try {
            ps = CON.conectar().prepareStatement(
                "SELECT p.*, e.nombre_evento as evento_nombre " +
                "FROM pago p " +
                "INNER JOIN evento e ON p.evento_id = e.id " +
                "WHERE e.nombre_evento LIKE ? AND p.activo = 1"
            );
            ps.setString(1, "%" + texto + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                Pago p = new Pago();
                p.setId(rs.getInt("id"));
                p.setEventoId(rs.getInt("evento_id"));
                p.setNombreEvento(rs.getString("evento_nombre"));
                p.setFechaPago(rs.getString("fecha_pago"));
                p.setMonto(rs.getDouble("monto"));
                p.setMetodoPago(rs.getString("metodo_pago"));
                p.setReferenciaPago(rs.getString("referencia_pago"));
                p.setObservaciones(rs.getString("observaciones"));
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
    public boolean insertar(Pago obj) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement(
                "INSERT INTO pago (evento_id, fecha_pago, monto, metodo_pago, referencia_pago, observaciones, activo) "
                + "VALUES (?, ?, ?, ?, ?, ?, 1)"
            );
            ps.setInt(1, obj.getEventoId());
            ps.setString(2, obj.getFechaPago());
            ps.setDouble(3, obj.getMonto());
            ps.setString(4, obj.getMetodoPago());
            ps.setString(5, obj.getReferenciaPago());
            ps.setString(6, obj.getObservaciones());
            
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
    public boolean actualizar(Pago obj) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement(
                "UPDATE pago SET evento_id = ?, fecha_pago = ?, monto = ?, metodo_pago = ?, referencia_pago = ?, observaciones = ? WHERE id = ?"
            );
            ps.setInt(1, obj.getEventoId());
            ps.setString(2, obj.getFechaPago());
            ps.setDouble(3, obj.getMonto());
            ps.setString(4, obj.getMetodoPago());
            ps.setString(5, obj.getReferenciaPago());
            ps.setString(6, obj.getObservaciones());
            ps.setInt(7, obj.getId());
            
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
            ps = CON.conectar().prepareStatement("UPDATE pago SET activo = 0 WHERE id = ?");
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
            ps = CON.conectar().prepareStatement("UPDATE pago SET activo = 1 WHERE id = ?");
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
            ps = CON.conectar().prepareStatement("SELECT COUNT(id) FROM pago WHERE activo = 1");
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
            ps = CON.conectar().prepareStatement(
                "SELECT p.id FROM pago p " +
                "INNER JOIN evento e ON p.evento_id = e.id " +
                "WHERE e.nombre_evento = ?"
            );
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
    
    // Método adicional: existePorReferencia
    public boolean existePorReferencia(String referencia) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement("SELECT referencia_pago FROM pago WHERE referencia_pago = ?");
            ps.setString(1, referencia);
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
    
    // Método adicional: existePorReferenciaDistintaId
    public boolean existePorReferenciaDistintaId(String referencia, int id) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement("SELECT referencia_pago FROM pago WHERE referencia_pago = ? AND id != ?");
            ps.setString(1, referencia);
            ps.setInt(2, id);
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
    public Pago buscarPorId(int id) {
        Pago pago = null;
        try {
            ps = CON.conectar().prepareStatement(
                "SELECT p.*, e.nombre_evento as evento_nombre " +
                "FROM pago p " +
                "INNER JOIN evento e ON p.evento_id = e.id " +
                "WHERE p.id = ?"
            );
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                pago = new Pago();
                pago.setId(rs.getInt("id"));
                pago.setEventoId(rs.getInt("evento_id"));
                pago.setNombreEvento(rs.getString("evento_nombre"));
                pago.setFechaPago(rs.getString("fecha_pago"));
                pago.setMonto(rs.getDouble("monto"));
                pago.setMetodoPago(rs.getString("metodo_pago"));
                pago.setReferenciaPago(rs.getString("referencia_pago"));
                pago.setObservaciones(rs.getString("observaciones"));
                pago.setActivo(rs.getBoolean("activo"));
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
        return pago;
    }
    
    // Método adicional: listarPorEvento
    public List<Pago> listarPorEvento(int eventoId) {
        List<Pago> registros = new ArrayList();
        try {
            ps = CON.conectar().prepareStatement(
                "SELECT * FROM pago WHERE evento_id = ? AND activo = 1 ORDER BY fecha_pago DESC"
            );
            ps.setInt(1, eventoId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Pago p = new Pago();
                p.setId(rs.getInt("id"));
                p.setEventoId(rs.getInt("evento_id"));
                p.setFechaPago(rs.getString("fecha_pago"));
                p.setMonto(rs.getDouble("monto"));
                p.setMetodoPago(rs.getString("metodo_pago"));
                p.setReferenciaPago(rs.getString("referencia_pago"));
                p.setObservaciones(rs.getString("observaciones"));
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
    
    // Método adicional: totalPagadoPorEvento
    public double totalPagadoPorEvento(int eventoId) {
        double total = 0;
        try {
            ps = CON.conectar().prepareStatement(
                "SELECT SUM(monto) as total FROM pago WHERE evento_id = ? AND activo = 1"
            );
            ps.setInt(1, eventoId);
            rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getDouble("total");
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
        return total;
    }
}