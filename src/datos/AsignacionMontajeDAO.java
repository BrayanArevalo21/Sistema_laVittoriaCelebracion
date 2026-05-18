package datos;

import database.Conexion;
import datos.interfaces.CrudSimpleInterface;
import entidades.AsignacionMontaje;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class AsignacionMontajeDAO implements CrudSimpleInterface<AsignacionMontaje> {
    private final Conexion CON;
    private PreparedStatement ps;
    private ResultSet rs;
    private boolean resp;

    public AsignacionMontajeDAO() {
        CON = Conexion.getInstancia();
    }

    @Override
    public List<AsignacionMontaje> listar(String texto) {
        List<AsignacionMontaje> registros = new ArrayList();
        try {
            ps = CON.conectar().prepareStatement(
                "SELECT a.*, e.nombre_evento as evento_nombre, p.nombre as personal_nombre " +
                "FROM asignacion_montaje a " +
                "INNER JOIN evento e ON a.evento_id = e.id " +
                "INNER JOIN personal_montaje p ON a.personal_id = p.id " +
                "WHERE e.nombre_evento LIKE ? AND a.activo = 1"
            );
            ps.setString(1, "%" + texto + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                AsignacionMontaje a = new AsignacionMontaje();
                a.setId(rs.getInt("id"));
                a.setEventoId(rs.getInt("evento_id"));
                a.setNombreEvento(rs.getString("evento_nombre"));
                a.setPersonalId(rs.getInt("personal_id"));
                a.setNombrePersonal(rs.getString("personal_nombre"));
                a.setRolAsignado(rs.getString("rol_asignado"));
                a.setHorasTrabajadas(rs.getDouble("horas_trabajadas"));
                a.setCostoTotal(rs.getDouble("costo_total"));
                a.setFechaAsignacion(rs.getString("fecha_asignacion"));
                a.setObservaciones(rs.getString("observaciones"));
                a.setActivo(rs.getBoolean("activo"));
                registros.add(a);
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
    public boolean insertar(AsignacionMontaje obj) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement(
                "INSERT INTO asignacion_montaje (evento_id, personal_id, rol_asignado, horas_trabajadas, costo_total, fecha_asignacion, observaciones, activo) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, 1)"
            );
            ps.setInt(1, obj.getEventoId());
            ps.setInt(2, obj.getPersonalId());
            ps.setString(3, obj.getRolAsignado());
            ps.setDouble(4, obj.getHorasTrabajadas());
            ps.setDouble(5, obj.getCostoTotal());
            ps.setString(6, obj.getFechaAsignacion());
            ps.setString(7, obj.getObservaciones());
            
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
    public boolean actualizar(AsignacionMontaje obj) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement(
                "UPDATE asignacion_montaje SET evento_id = ?, personal_id = ?, rol_asignado = ?, " +
                "horas_trabajadas = ?, costo_total = ?, fecha_asignacion = ?, observaciones = ? WHERE id = ?"
            );
            ps.setInt(1, obj.getEventoId());
            ps.setInt(2, obj.getPersonalId());
            ps.setString(3, obj.getRolAsignado());
            ps.setDouble(4, obj.getHorasTrabajadas());
            ps.setDouble(5, obj.getCostoTotal());
            ps.setString(6, obj.getFechaAsignacion());
            ps.setString(7, obj.getObservaciones());
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
            ps = CON.conectar().prepareStatement("UPDATE asignacion_montaje SET activo = 0 WHERE id = ?");
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
            ps = CON.conectar().prepareStatement("UPDATE asignacion_montaje SET activo = 1 WHERE id = ?");
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
            ps = CON.conectar().prepareStatement("SELECT COUNT(id) FROM asignacion_montaje WHERE activo = 1");
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
                "SELECT a.id FROM asignacion_montaje a " +
                "INNER JOIN evento e ON a.evento_id = e.id " +
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
    
    // Método adicional: buscarPorId
    public AsignacionMontaje buscarPorId(int id) {
        AsignacionMontaje asignacion = null;
        try {
            ps = CON.conectar().prepareStatement(
                "SELECT a.*, e.nombre_evento as evento_nombre, p.nombre as personal_nombre " +
                "FROM asignacion_montaje a " +
                "INNER JOIN evento e ON a.evento_id = e.id " +
                "INNER JOIN personal_montaje p ON a.personal_id = p.id " +
                "WHERE a.id = ?"
            );
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                asignacion = new AsignacionMontaje();
                asignacion.setId(rs.getInt("id"));
                asignacion.setEventoId(rs.getInt("evento_id"));
                asignacion.setNombreEvento(rs.getString("evento_nombre"));
                asignacion.setPersonalId(rs.getInt("personal_id"));
                asignacion.setNombrePersonal(rs.getString("personal_nombre"));
                asignacion.setRolAsignado(rs.getString("rol_asignado"));
                asignacion.setHorasTrabajadas(rs.getDouble("horas_trabajadas"));
                asignacion.setCostoTotal(rs.getDouble("costo_total"));
                asignacion.setFechaAsignacion(rs.getString("fecha_asignacion"));
                asignacion.setObservaciones(rs.getString("observaciones"));
                asignacion.setActivo(rs.getBoolean("activo"));
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
        return asignacion;
    }
    
    // Método adicional: listarPorEvento
    public List<AsignacionMontaje> listarPorEvento(int eventoId) {
        List<AsignacionMontaje> registros = new ArrayList();
        try {
            ps = CON.conectar().prepareStatement(
                "SELECT a.*, p.nombre as personal_nombre " +
                "FROM asignacion_montaje a " +
                "INNER JOIN personal_montaje p ON a.personal_id = p.id " +
                "WHERE a.evento_id = ? AND a.activo = 1"
            );
            ps.setInt(1, eventoId);
            rs = ps.executeQuery();
            while (rs.next()) {
                AsignacionMontaje a = new AsignacionMontaje();
                a.setId(rs.getInt("id"));
                a.setEventoId(rs.getInt("evento_id"));
                a.setPersonalId(rs.getInt("personal_id"));
                a.setNombrePersonal(rs.getString("personal_nombre"));
                a.setRolAsignado(rs.getString("rol_asignado"));
                a.setHorasTrabajadas(rs.getDouble("horas_trabajadas"));
                a.setCostoTotal(rs.getDouble("costo_total"));
                a.setFechaAsignacion(rs.getString("fecha_asignacion"));
                a.setObservaciones(rs.getString("observaciones"));
                a.setActivo(rs.getBoolean("activo"));
                registros.add(a);
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
}