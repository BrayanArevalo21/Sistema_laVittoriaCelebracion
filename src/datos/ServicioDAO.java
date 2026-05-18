package datos;

import database.Conexion;
import datos.interfaces.CrudSimpleInterface;
import entidades.Servicio;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
                "SELECT s.*, ts.nombre as nombre_tipo " +
                "FROM servicio s " +
                "INNER JOIN tipo_servicio ts ON s.tipo_servicio_id = ts.id " +
                "WHERE s.nombre LIKE ? AND s.activo = 1"
            );
            ps.setString(1, "%" + texto + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                Servicio servicio = new Servicio();
                servicio.setId(rs.getInt("id"));
                servicio.setTipoServicioId(rs.getInt("tipo_servicio_id"));
                servicio.setNombreTipoServicio(rs.getString("nombre_tipo"));
                servicio.setCodigo(rs.getString("codigo"));
                servicio.setNombre(rs.getString("nombre"));
                servicio.setPrecioBase(rs.getDouble("precio_base"));
                servicio.setDescripcion(rs.getString("descripcion"));
                servicio.setActivo(rs.getBoolean("activo"));
                registros.add(servicio);
            }
            ps.close();
            rs.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al listar servicios: " + e.getMessage());
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
                "INSERT INTO servicio (tipo_servicio_id, codigo, nombre, precio_base, descripcion, activo) " +
                "VALUES (?, ?, ?, ?, ?, 1)"
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
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al insertar servicio: " + e.getMessage());
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
                "UPDATE servicio SET tipo_servicio_id = ?, codigo = ?, nombre = ?, " +
                "precio_base = ?, descripcion = ? WHERE id = ?"
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
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar servicio: " + e.getMessage());
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
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al desactivar servicio: " + e.getMessage());
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
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al activar servicio: " + e.getMessage());
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
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al contar servicios: " + e.getMessage());
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
            ps = CON.conectar().prepareStatement("SELECT id FROM servicio WHERE nombre = ?");
            ps.setString(1, texto);
            rs = ps.executeQuery();
            if (rs.next()) {
                resp = true;
            }
            ps.close();
            rs.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al verificar existencia: " + e.getMessage());
        } finally {
            ps = null;
            rs = null;
            CON.desconectar();
        }
        return resp;
    }
    
    
    public Servicio buscarPorId(int id) {
        Servicio servicio = null;
        try {
            ps = CON.conectar().prepareStatement(
                "SELECT s.*, ts.nombre as nombre_tipo " +
                "FROM servicio s " +
                "INNER JOIN tipo_servicio ts ON s.tipo_servicio_id = ts.id " +
                "WHERE s.id = ?"
            );
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                servicio = new Servicio();
                servicio.setId(rs.getInt("id"));
                servicio.setTipoServicioId(rs.getInt("tipo_servicio_id"));
                servicio.setNombreTipoServicio(rs.getString("nombre_tipo"));
                servicio.setCodigo(rs.getString("codigo"));
                servicio.setNombre(rs.getString("nombre"));
                servicio.setPrecioBase(rs.getDouble("precio_base"));
                servicio.setDescripcion(rs.getString("descripcion"));
                servicio.setActivo(rs.getBoolean("activo"));
            }
            ps.close();
            rs.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al buscar servicio por ID: " + e.getMessage());
        } finally {
            ps = null;
            rs = null;
            CON.desconectar();
        }
        return servicio;
    }
    
    public boolean existePorCodigo(String codigo) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement("SELECT id FROM servicio WHERE codigo = ?");
            ps.setString(1, codigo);
            rs = ps.executeQuery();
            if (rs.next()) {
                resp = true;
            }
            ps.close();
            rs.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al verificar código: " + e.getMessage());
        } finally {
            ps = null;
            rs = null;
            CON.desconectar();
        }
        return resp;
    }
    
    public List<Servicio> listarPorTipo(int tipoServicioId) {
        List<Servicio> registros = new ArrayList();
        try {
            ps = CON.conectar().prepareStatement(
                "SELECT s.*, ts.nombre as nombre_tipo " +
                "FROM servicio s " +
                "INNER JOIN tipo_servicio ts ON s.tipo_servicio_id = ts.id " +
                "WHERE s.tipo_servicio_id = ? AND s.activo = 1"
            );
            ps.setInt(1, tipoServicioId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Servicio servicio = new Servicio();
                servicio.setId(rs.getInt("id"));
                servicio.setTipoServicioId(rs.getInt("tipo_servicio_id"));
                servicio.setNombreTipoServicio(rs.getString("nombre_tipo"));
                servicio.setCodigo(rs.getString("codigo"));
                servicio.setNombre(rs.getString("nombre"));
                servicio.setPrecioBase(rs.getDouble("precio_base"));
                servicio.setDescripcion(rs.getString("descripcion"));
                servicio.setActivo(rs.getBoolean("activo"));
                registros.add(servicio);
            }
            ps.close();
            rs.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al listar servicios por tipo: " + e.getMessage());
        } finally {
            ps = null;
            rs = null;
            CON.desconectar();
        }
        return registros;
    }
    
    public List<Servicio> listarPorEvento(int eventoId) {
        List<Servicio> registros = new ArrayList();
        try {
            ps = CON.conectar().prepareStatement(
                "SELECT s.*, ts.nombre as nombre_tipo, de.cantidad, (de.cantidad * de.precio_unitario) as subtotal " +
                "FROM servicio s " +
                "INNER JOIN tipo_servicio ts ON s.tipo_servicio_id = ts.id " +
                "INNER JOIN detalle_evento de ON s.id = de.servicio_id " +
                "WHERE de.evento_id = ? AND s.activo = 1"
            );
            ps.setInt(1, eventoId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Servicio servicio = new Servicio();
                servicio.setId(rs.getInt("id"));
                servicio.setTipoServicioId(rs.getInt("tipo_servicio_id"));
                servicio.setNombreTipoServicio(rs.getString("nombre_tipo"));
                servicio.setCodigo(rs.getString("codigo"));
                servicio.setNombre(rs.getString("nombre"));
                servicio.setPrecioBase(rs.getDouble("precio_base"));
                servicio.setDescripcion(rs.getString("descripcion"));
                servicio.setActivo(rs.getBoolean("activo"));
                servicio.setCantidad(rs.getInt("cantidad"));
                servicio.setSubtotal(rs.getDouble("subtotal"));
                registros.add(servicio);
            }
            ps.close();
            rs.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al listar servicios por evento: " + e.getMessage());
        } finally {
            ps = null;
            rs = null;
            CON.desconectar();
        }
        return registros;
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
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener precio: " + e.getMessage());
        } finally {
            ps = null;
            rs = null;
            CON.desconectar();
        }
        return precio;
    }
    
    public boolean agregarAlEvento(int eventoId, int servicioId, int cantidad, double precio) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement(
                "INSERT INTO detalle_evento (evento_id, servicio_id, cantidad, precio_unitario) " +
                "VALUES (?, ?, ?, ?)"
            );
            ps.setInt(1, eventoId);
            ps.setInt(2, servicioId);
            ps.setInt(3, cantidad);
            ps.setDouble(4, precio);
            
            if (ps.executeUpdate() > 0) {
                resp = true;
            }
            ps.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al agregar servicio al evento: " + e.getMessage());
        } finally {
            ps = null;
            CON.desconectar();
        }
        return resp;
    }
    
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
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al quitar servicio del evento: " + e.getMessage());
        } finally {
            ps = null;
            CON.desconectar();
        }
        return resp;
    }
    
    public List<Servicio> listarTodos() {
        List<Servicio> registros = new ArrayList();
        try {
            ps = CON.conectar().prepareStatement(
                "SELECT s.*, ts.nombre as nombre_tipo " +
                "FROM servicio s " +
                "INNER JOIN tipo_servicio ts ON s.tipo_servicio_id = ts.id " +
                "WHERE s.activo = 1"
            );
            rs = ps.executeQuery();
            while (rs.next()) {
                Servicio servicio = new Servicio();
                servicio.setId(rs.getInt("id"));
                servicio.setTipoServicioId(rs.getInt("tipo_servicio_id"));
                servicio.setNombreTipoServicio(rs.getString("nombre_tipo"));
                servicio.setCodigo(rs.getString("codigo"));
                servicio.setNombre(rs.getString("nombre"));
                servicio.setPrecioBase(rs.getDouble("precio_base"));
                servicio.setDescripcion(rs.getString("descripcion"));
                servicio.setActivo(rs.getBoolean("activo"));
                registros.add(servicio);
            }
            ps.close();
            rs.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al listar todos los servicios: " + e.getMessage());
        } finally {
            ps = null;
            rs = null;
            CON.desconectar();
        }
        return registros;
    }
}