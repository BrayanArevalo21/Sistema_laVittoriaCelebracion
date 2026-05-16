package datos;

import database.Conexion;
import entidades.Servicio;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class ServicioDAO {
    private final Conexion CON;
    private PreparedStatement ps;
    private ResultSet rs;
    private boolean resp;

    public ServicioDAO() {
        CON = Conexion.getInstancia();
    }

    // ========== MÉTODOS BÁSICOS CRUD ==========
    
    public List<Servicio> listar(String texto) {
        List<Servicio> registros = new ArrayList();
        try {
            ps = CON.conectar().prepareStatement(
                "SELECT s.*, ts.nombre as tipo_nombre FROM servicio s " +
                "INNER JOIN tipo_servicio ts ON s.tipo_servicio_id = ts.id " +
                "WHERE s.nombre LIKE ? AND s.activo = 1"
            );
            ps.setString(1, "%" + texto + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                Servicio s = new Servicio();
                s.setId(rs.getInt("id"));
                s.setTipoServicioId(rs.getInt("tipo_servicio_id"));
                s.setTipoServicioNombre(rs.getString("tipo_nombre"));
                s.setCodigo(rs.getString("codigo"));
                s.setNombre(rs.getString("nombre"));
                s.setPrecioBase(rs.getDouble("precio_base"));
                s.setDescripcion(rs.getString("descripcion"));
                s.setActivo(rs.getBoolean("activo"));
                registros.add(s);
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

    public Servicio buscarPorId(int id) {
        Servicio servicio = null;
        try {
            ps = CON.conectar().prepareStatement(
                "SELECT s.*, ts.nombre as tipo_nombre FROM servicio s " +
                "INNER JOIN tipo_servicio ts ON s.tipo_servicio_id = ts.id " +
                "WHERE s.id = ?"
            );
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                servicio = new Servicio();
                servicio.setId(rs.getInt("id"));
                servicio.setTipoServicioId(rs.getInt("tipo_servicio_id"));
                servicio.setTipoServicioNombre(rs.getString("tipo_nombre"));
                servicio.setCodigo(rs.getString("codigo"));
                servicio.setNombre(rs.getString("nombre"));
                servicio.setPrecioBase(rs.getDouble("precio_base"));
                servicio.setDescripcion(rs.getString("descripcion"));
                servicio.setActivo(rs.getBoolean("activo"));
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
        return servicio;
    }

    public double obtenerPrecio(int servicioId) {
        double precio = 0;
        try {
            ps = CON.conectar().prepareStatement("SELECT precio_base FROM servicio WHERE id = ?");
            ps.setInt(1, servicioId);
            rs = ps.executeQuery();
            if (rs.next()) {
                precio = rs.getDouble("precio_base");
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
        return precio;
    }

    public boolean insertar(Servicio obj) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement(
                "INSERT INTO servicio (tipo_servicio_id, codigo, nombre, precio_base, descripcion, activo) " +
                "VALUES (?, ?, ?, ?, ?, 1)"
            );
            ps.setInt(1, obj.getTipoServicioId());
            ps.setString(2, obj.getCodigo());
            ps.setString(3, obj.getNombre());
            ps.setDouble(4, obj.getPrecioBase());
            ps.setString(5, obj.getDescripcion());
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

    public boolean actualizar(Servicio obj) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement(
                "UPDATE servicio SET tipo_servicio_id=?, codigo=?, nombre=?, precio_base=?, descripcion=? WHERE id=?"
            );
            ps.setInt(1, obj.getTipoServicioId());
            ps.setString(2, obj.getCodigo());
            ps.setString(3, obj.getNombre());
            ps.setDouble(4, obj.getPrecioBase());
            ps.setString(5, obj.getDescripcion());
            ps.setInt(6, obj.getId());
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
            ps = CON.conectar().prepareStatement("UPDATE servicio SET activo = 0 WHERE id = ?");
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
            ps = CON.conectar().prepareStatement("UPDATE servicio SET activo = 1 WHERE id = ?");
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
            ps = CON.conectar().prepareStatement("SELECT COUNT(*) FROM servicio WHERE activo = 1");
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
            ps = CON.conectar().prepareStatement("SELECT nombre FROM servicio WHERE nombre = ?");
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

    // ========== MÉTODOS PARA EVENTOS (los que te faltaban) ==========
    
    /**
     * Lista los servicios que pertenecen a un evento específico
     * Asume que tienes una tabla detalle_evento o similar
     */
    public List<Servicio> listarPorEvento(int eventoId) {
        List<Servicio> registros = new ArrayList();
        try {
            ps = CON.conectar().prepareStatement(
                "SELECT s.id, s.nombre, s.precio_base, de.cantidad, de.precio_unitario " +
                "FROM servicio s " +
                "INNER JOIN detalle_evento de ON s.id = de.servicio_id " +
                "WHERE de.evento_id = ?"
            );
            ps.setInt(1, eventoId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Servicio s = new Servicio();
                s.setId(rs.getInt("id"));
                s.setNombre(rs.getString("nombre"));
                s.setPrecioBase(rs.getDouble("precio_base"));
                s.setCantidad(rs.getInt("cantidad"));
                registros.add(s);
            }
            ps.close();
            rs.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en listarPorEvento: " + e.getMessage());
        } finally {
            ps = null;
            rs = null;
            CON.desconectar();
        }
        return registros;
    }

    /**
     * Agrega un servicio a un evento en la tabla detalle_evento
     */
    public boolean agregarAlEvento(int eventoId, int servicioId, int cantidad, double precio) {
        resp = false;
        try {
            // Verificar si ya existe el servicio en el evento
            ps = CON.conectar().prepareStatement(
                "SELECT id FROM detalle_evento WHERE evento_id = ? AND servicio_id = ?"
            );
            ps.setInt(1, eventoId);
            ps.setInt(2, servicioId);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                // Si ya existe, actualizar la cantidad
                ps.close();
                rs.close();
                
                ps = CON.conectar().prepareStatement(
                    "UPDATE detalle_evento SET cantidad = cantidad + ?, precio_unitario = ? " +
                    "WHERE evento_id = ? AND servicio_id = ?"
                );
                ps.setInt(1, cantidad);
                ps.setDouble(2, precio);
                ps.setInt(3, eventoId);
                ps.setInt(4, servicioId);
            } else {
                // Si no existe, insertar nuevo
                ps.close();
                rs.close();
                
                ps = CON.conectar().prepareStatement(
                    "INSERT INTO detalle_evento (evento_id, servicio_id, cantidad, precio_unitario, descuento) " +
                    "VALUES (?, ?, ?, ?, 0)"
                );
                ps.setInt(1, eventoId);
                ps.setInt(2, servicioId);
                ps.setInt(3, cantidad);
                ps.setDouble(4, precio);
            }
            
            if (ps.executeUpdate() > 0) resp = true;
            ps.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en agregarAlEvento: " + e.getMessage());
        } finally {
            ps = null;
            rs = null;
            CON.desconectar();
        }
        return resp;
    }

    /**
     * Elimina un servicio de un evento en la tabla detalle_evento
     */
    public boolean quitarDelEvento(int eventoId, int servicioId) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement(
                "DELETE FROM detalle_evento WHERE evento_id = ? AND servicio_id = ?"
            );
            ps.setInt(1, eventoId);
            ps.setInt(2, servicioId);
            if (ps.executeUpdate() > 0) resp = true;
            ps.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en quitarDelEvento: " + e.getMessage());
        } finally {
            ps = null;
            CON.desconectar();
        }
        return resp;
    }
    
    /**
     * Obtiene el costo total de los servicios de un evento
     */
    public double obtenerTotalServiciosPorEvento(int eventoId) {
        double total = 0;
        try {
            ps = CON.conectar().prepareStatement(
                "SELECT SUM(cantidad * precio_unitario) as total FROM detalle_evento WHERE evento_id = ?"
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