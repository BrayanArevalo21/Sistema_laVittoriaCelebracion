package datos;

import database.Conexion;
import datos.interfaces.CrudSimpleInterface;
import entidades.PersonalMontaje;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class PersonalMontajeDAO implements CrudSimpleInterface<PersonalMontaje> {
    private final Conexion CON;
    private PreparedStatement ps;
    private ResultSet rs;
    private boolean resp;

    public PersonalMontajeDAO() {
        CON = Conexion.getInstancia();
    }

    @Override
    public List<PersonalMontaje> listar(String texto) {
        List<PersonalMontaje> registros = new ArrayList();
        try {
            ps = CON.conectar().prepareStatement("SELECT * FROM personal_montaje WHERE nombre LIKE ? AND activo = 1");
            ps.setString(1, "%" + texto + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                registros.add(new PersonalMontaje(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("tipo_documento"),
                    rs.getString("num_documento"),
                    rs.getString("telefono"),
                    rs.getString("email"),
                    rs.getString("especialidad"),
                    rs.getDouble("costo_por_hora"),
                    rs.getBoolean("disponible"),
                    rs.getString("observaciones"),
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

    @Override
    public boolean insertar(PersonalMontaje obj) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement(
                "INSERT INTO personal_montaje (nombre, tipo_documento, num_documento, telefono, email, especialidad, costo_por_hora, disponible, observaciones, activo) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 1)"
            );
            ps.setString(1, obj.getNombre());
            ps.setString(2, obj.getTipoDocumento());
            ps.setString(3, obj.getNumDocumento());
            ps.setString(4, obj.getTelefono());
            ps.setString(5, obj.getEmail());
            ps.setString(6, obj.getEspecialidad());
            ps.setDouble(7, obj.getCostoPorHora());
            ps.setBoolean(8, obj.isDisponible());
            ps.setString(9, obj.getObservaciones());
            
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
    public boolean actualizar(PersonalMontaje obj) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement(
                "UPDATE personal_montaje SET nombre = ?, tipo_documento = ?, num_documento = ?, telefono = ?, email = ?, especialidad = ?, costo_por_hora = ?, disponible = ?, observaciones = ? WHERE id = ?"
            );
            ps.setString(1, obj.getNombre());
            ps.setString(2, obj.getTipoDocumento());
            ps.setString(3, obj.getNumDocumento());
            ps.setString(4, obj.getTelefono());
            ps.setString(5, obj.getEmail());
            ps.setString(6, obj.getEspecialidad());
            ps.setDouble(7, obj.getCostoPorHora());
            ps.setBoolean(8, obj.isDisponible());
            ps.setString(9, obj.getObservaciones());
            ps.setInt(10, obj.getId());
            
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
            ps = CON.conectar().prepareStatement("UPDATE personal_montaje SET activo = 0 WHERE id = ?");
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
            ps = CON.conectar().prepareStatement("UPDATE personal_montaje SET activo = 1 WHERE id = ?");
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
            ps = CON.conectar().prepareStatement("SELECT COUNT(id) FROM personal_montaje WHERE activo = 1");
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
            ps = CON.conectar().prepareStatement("SELECT nombre FROM personal_montaje WHERE nombre = ?");
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
            ps = CON.conectar().prepareStatement("SELECT num_documento FROM personal_montaje WHERE num_documento = ?");
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
    public PersonalMontaje buscarPorId(int id) {
        PersonalMontaje personal = null;
        try {
            ps = CON.conectar().prepareStatement("SELECT * FROM personal_montaje WHERE id = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                personal = new PersonalMontaje(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("tipo_documento"),
                    rs.getString("num_documento"),
                    rs.getString("telefono"),
                    rs.getString("email"),
                    rs.getString("especialidad"),
                    rs.getDouble("costo_por_hora"),
                    rs.getBoolean("disponible"),
                    rs.getString("observaciones"),
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
        return personal;
    }
}