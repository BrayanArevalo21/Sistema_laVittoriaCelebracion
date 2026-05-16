package datos;

import database.Conexion;
import entidades.Pago;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class PagoDAO {
    private final Conexion CON;
    private PreparedStatement ps;
    private ResultSet rs;
    private boolean resp;

    public PagoDAO() {
        CON = Conexion.getInstancia();
    }

    public List<Pago> listarPorEvento(int eventoId) {
        List<Pago> registros = new ArrayList();
        try {
            ps = CON.conectar().prepareStatement("SELECT * FROM pago WHERE evento_id = ?");
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

    public boolean insertar(Pago obj) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement(
                "INSERT INTO pago (evento_id, fecha_pago, monto, metodo_pago, referencia_pago, observaciones) " +
                "VALUES (?, NOW(), ?, ?, ?, ?)"
            );
            ps.setInt(1, obj.getEventoId());
            ps.setDouble(2, obj.getMonto());
            ps.setString(3, obj.getMetodoPago());
            ps.setString(4, obj.getReferenciaPago());
            ps.setString(5, obj.getObservaciones());
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
}