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
                s.setNombreTipoServicio(rs.getString("tipo_nombre"));
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
    
    // ========== LISTAR CON FILTRO DE ESTADO ==========
    public List<Servicio> listarConFiltro(String texto, String filtroEstado) {
        List<Servicio> registros = new ArrayList();
        String sql = "";
        
        if (filtroEstado.equals("Activos")) {
            sql = "SELECT s.*, ts.nombre as tipo_nombre FROM servicio s " +
                  "INNER JOIN tipo_servicio ts ON s.tipo_servicio_id = ts.id " +
                  "WHERE s.nombre LIKE ? AND s.activo = 1";
        } else if (filtroEstado.equals("Inactivos")) {
            sql = "SELECT s.*, ts.nombre as tipo_nombre FROM servicio s " +
                  "INNER JOIN tipo_servicio ts ON s.tipo_servicio_id = ts.id " +
                  "WHERE s.nombre LIKE ? AND s.activo = 0";
        } else {
            sql = "SELECT s.*, ts.nombre as tipo_nombre FROM servicio s " +
                  "INNER JOIN tipo_servicio ts ON s.tipo_servicio_id = ts.id " +
                  "WHERE s.nombre LIKE ?";
        }
        
        try {
            ps = CON.conectar().prepareStatement(sql);
            ps.setString(1, "%" + texto + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                Servicio s = new Servicio();
                s.setId(rs.getInt("id"));
                s.setTipoServicioId(rs.getInt("tipo_servicio_id"));
                s.setNombreTipoServicio(rs.getString("tipo_nombre"));
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

    @Override
    public boolean insertar(Servicio obj) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement(
                "INSERT INTO servicio (tipo_servicio_id, codigo, nombre, precio_base, descripcion, activo) "
                + "VALUES (?, ?, ?, ?, ?, 1)"
            );
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
            ps = CON.conectar().prepareStatement(
                "UPDATE servicio SET tipo_servicio_id = ?, codigo = ?, nombre = ?, precio_base = ?, descripcion = ? WHERE id = ?"
            );
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
    
    // ========== MÉTODOS ADICIONALES ==========
    
    // Verificar si existe un código
    public boolean existePorCodigo(String codigo) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement("SELECT codigo FROM servicio WHERE codigo = ?");
            ps.setString(1, codigo);
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
    
    // Buscar por ID
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
                servicio.setNombreTipoServicio(rs.getString("tipo_nombre"));
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
    
    // ========== MÉTODOS PARA EVENTOS ==========
    
    // Obtener precio de un servicio
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
    
    // Agregar servicio a un evento
    public boolean agregarAlEvento(int eventoId, int servicioId, int cantidad, double precio) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement(
                "INSERT INTO detalle_evento (evento_id, servicio_id, cantidad, precio_unitario) VALUES (?, ?, ?, ?)"
            );
            ps.setInt(1, eventoId);
            ps.setInt(2, servicioId);
            ps.setInt(3, cantidad);
            ps.setDouble(4, precio);
            
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
    
    // Quitar servicio de un evento
    public boolean quitarDelEvento(int eventoId, int servicioId) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement(
                "DELETE FROM detalle_evento WHERE evento_id = ? AND servicio_id = ?"
            );
            ps.setInt(1, eventoId);
            ps.setInt(2, servicioId);
            
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
    
    // Listar servicios de un evento específico
    public List<Servicio> listarPorEvento(int eventoId) {
        List<Servicio> registros = new ArrayList();
        try {
            ps = CON.conectar().prepareStatement(
                "SELECT s.*, de.cantidad, de.precio_unitario FROM detalle_evento de " +
                "INNER JOIN servicio s ON de.servicio_id = s.id " +
                "WHERE de.evento_id = ?"
            );
            ps.setInt(1, eventoId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Servicio s = new Servicio();
                s.setId(rs.getInt("id"));
                s.setTipoServicioId(rs.getInt("tipo_servicio_id"));
                s.setCodigo(rs.getString("codigo"));
                s.setNombre(rs.getString("nombre"));
                s.setPrecioBase(rs.getDouble("precio_unitario"));
                s.setCantidad(rs.getInt("cantidad"));
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
}