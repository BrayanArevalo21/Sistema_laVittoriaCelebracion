
package presentacion;

import datos.EventoDAO;
import datos.PagoDAO;
import entidades.Evento;
import entidades.Pago;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
public class frmPago extends javax.swing.JInternalFrame {



      private javax.swing.JTabbedPane tabGeneral;
    
    
    
    // ========== VARIABLES DE CONTROL ==========
    private PagoDAO pagoDAO;
    private EventoDAO eventoDAO;
    private DefaultTableModel modeloTabla;
    private String accion;
    private int idActual;
    private DecimalFormat formatoCOP = new DecimalFormat("#,###");
    private SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
    
    public frmPago() {
        initComponentes();
        iniciar();
        setVisible(true);
    }
    
    private void iniciar() {
        pagoDAO = new PagoDAO();
        eventoDAO = new EventoDAO();
        accion = "guardar";
        
        // Configurar modelo de tabla
        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        modeloTabla.setColumnIdentifiers(new String[]{"ID", "EVENTO", "FECHA", "MONTO", "MÉTODO", "REFERENCIA", "ESTADO"});
        tablaListado.setModel(modeloTabla);
        
        // Configurar combo de métodos de pago
        configurarCombos();
        
        // Cargar eventos
        cargarEventos();
        
        listar("");
        tabGeneral.setEnabledAt(1, false);
        
        setTitle("Registro de Pagos");
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setSize(850, 580);
        
        // Si hoy no está lleno, poner fecha actual
        if (txtFechaPago.getText().isEmpty()) {
            txtFechaPago.setText(formatoFecha.format(new Date()));
        }
    }
    
    private void configurarCombos() {
        cbxMetodoPago.addItem("Efectivo");
        cbxMetodoPago.addItem("Transferencia");
        cbxMetodoPago.addItem("Tarjeta Débito");
        cbxMetodoPago.addItem("Tarjeta Crédito");
        cbxMetodoPago.addItem("Nequi");
        cbxMetodoPago.addItem("Daviplata");
    }
    
    private void cargarEventos() {
        cbxEvento.removeAllItems();
        List<Evento> eventos = eventoDAO.listar("");
        for (Evento e : eventos) {
            if (e.isActivo()) {
                cbxEvento.addItem(e.getId() + " - " + e.getNombreEvento() + " (Cliente: " + e.getNombreCliente() + ")");
            }
        }
    }
    
    // ========== MÉTODOS PRINCIPALES ==========
    
    private void listar(String texto) {
        modeloTabla.setRowCount(0);
        List<Pago> lista = pagoDAO.listar(texto);
        
        for (Pago p : lista) {
            String estado = p.isActivo() ? "Activo" : "Inactivo";
            modeloTabla.addRow(new Object[]{
                p.getId(),
                p.getNombreEvento(),
                p.getFechaPago(),
                "$" + formatoCOP.format(p.getMonto()),
                p.getMetodoPago(),
                p.getReferenciaPago() != null ? p.getReferenciaPago() : "",
                estado
            });
        }
        
        int total = pagoDAO.total();
        lblTotalRegistros.setText("Total registros: " + total);
    }
    
    private void nuevo() {
        accion = "guardar";
        limpiar();
        txtFechaPago.setText(formatoFecha.format(new Date()));
        tabGeneral.setEnabledAt(0, false);
        tabGeneral.setEnabledAt(1, true);
        tabGeneral.setSelectedIndex(1);
        cbxEvento.requestFocus();
    }
    
    private void editar() {
        int fila = tablaListado.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un registro para editar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        accion = "editar";
        idActual = Integer.parseInt(tablaListado.getValueAt(fila, 0).toString());
        txtId.setText(String.valueOf(idActual));
        
        // Cargar datos del pago
        Pago pago = pagoDAO.buscarPorId(idActual);
        if (pago != null) {
            // Seleccionar evento
            for (int i = 0; i < cbxEvento.getItemCount(); i++) {
                if (cbxEvento.getItemAt(i).startsWith(String.valueOf(pago.getEventoId()))) {
                    cbxEvento.setSelectedIndex(i);
                    break;
                }
            }
            
            txtFechaPago.setText(pago.getFechaPago());
            txtMonto.setText(String.valueOf(pago.getMonto()));
            cbxMetodoPago.setSelectedItem(pago.getMetodoPago());
            txtReferencia.setText(pago.getReferenciaPago());
            txtObservaciones.setText(pago.getObservaciones());
        }
        
        tabGeneral.setEnabledAt(0, false);
        tabGeneral.setEnabledAt(1, true);
        tabGeneral.setSelectedIndex(1);
        cbxEvento.requestFocus();
    }
    
    private void guardar() {
        // Validar evento
        if (cbxEvento.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un evento", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int eventoId = Integer.parseInt(cbxEvento.getSelectedItem().toString().split(" - ")[0]);
        String fechaPago = txtFechaPago.getText().trim();
        String montoStr = txtMonto.getText().trim();
        String metodoPago = cbxMetodoPago.getSelectedItem().toString();
        String referencia = txtReferencia.getText().trim();
        String observaciones = txtObservaciones.getText().trim();
        
        // Validaciones
        if (fechaPago.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La fecha es obligatoria", "Error", JOptionPane.ERROR_MESSAGE);
            txtFechaPago.requestFocus();
            return;
        }
        
        // Validar formato de fecha
        try {
            Date fecha = formatoFecha.parse(fechaPago);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Use dd/MM/yyyy", "Error", JOptionPane.ERROR_MESSAGE);
            txtFechaPago.requestFocus();
            return;
        }
        
        if (montoStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El monto es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            txtMonto.requestFocus();
            return;
        }
        
        double monto;
        try {
            monto = Double.parseDouble(montoStr);
            if (monto <= 0) {
                JOptionPane.showMessageDialog(this, "El monto debe ser mayor a 0", "Error", JOptionPane.ERROR_MESSAGE);
                txtMonto.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese un monto válido", "Error", JOptionPane.ERROR_MESSAGE);
            txtMonto.requestFocus();
            return;
        }
        
        if (referencia.length() > 50) {
            JOptionPane.showMessageDialog(this, "La referencia no puede tener más de 50 caracteres", "Error", JOptionPane.ERROR_MESSAGE);
            txtReferencia.requestFocus();
            return;
        }
        
        if (observaciones.length() > 250) {
            JOptionPane.showMessageDialog(this, "Las observaciones no pueden tener más de 250 caracteres", "Error", JOptionPane.ERROR_MESSAGE);
            txtObservaciones.requestFocus();
            return;
        }
        
        if (accion.equals("guardar")) {
            // Validar que no exista pago duplicado (misma referencia)
            if (!referencia.isEmpty() && pagoDAO.existePorReferencia(referencia)) {
                JOptionPane.showMessageDialog(this, "Ya existe un pago con esa referencia", "Error", JOptionPane.ERROR_MESSAGE);
                txtReferencia.requestFocus();
                return;
            }
            
            Pago nuevo = new Pago();
            nuevo.setEventoId(eventoId);
            nuevo.setFechaPago(fechaPago);
            nuevo.setMonto(monto);
            nuevo.setMetodoPago(metodoPago);
            nuevo.setReferenciaPago(referencia);
            nuevo.setObservaciones(observaciones);
            nuevo.setActivo(true);
            
            if (pagoDAO.insertar(nuevo)) {
                JOptionPane.showMessageDialog(this, "Pago registrado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                listar("");
                cancelar();
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar el pago", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Editar
            if (!referencia.isEmpty() && pagoDAO.existePorReferenciaDistintaId(referencia, idActual)) {
                JOptionPane.showMessageDialog(this, "Ya existe un pago con esa referencia", "Error", JOptionPane.ERROR_MESSAGE);
                txtReferencia.requestFocus();
                return;
            }
            
            Pago editar = new Pago();
            editar.setId(idActual);
            editar.setEventoId(eventoId);
            editar.setFechaPago(fechaPago);
            editar.setMonto(monto);
            editar.setMetodoPago(metodoPago);
            editar.setReferenciaPago(referencia);
            editar.setObservaciones(observaciones);
            editar.setActivo(true);
            
            if (pagoDAO.actualizar(editar)) {
                JOptionPane.showMessageDialog(this, "Pago actualizado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                listar("");
                cancelar();
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar el pago", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void activar() {
        int fila = tablaListado.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un registro para activar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        idActual = Integer.parseInt(tablaListado.getValueAt(fila, 0).toString());
        String evento = tablaListado.getValueAt(fila, 1).toString();
        String monto = tablaListado.getValueAt(fila, 3).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Desea activar el pago de " + monto + " para el evento: " + evento + "?", 
            "Confirmar", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (pagoDAO.activar(idActual)) {
                JOptionPane.showMessageDialog(this, "Pago activado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                listar("");
            } else {
                JOptionPane.showMessageDialog(this, "Error al activar el pago", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void desactivar() {
        int fila = tablaListado.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un registro para desactivar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        idActual = Integer.parseInt(tablaListado.getValueAt(fila, 0).toString());
        String evento = tablaListado.getValueAt(fila, 1).toString();
        String monto = tablaListado.getValueAt(fila, 3).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Desea desactivar el pago de " + monto + " para el evento: " + evento + "?", 
            "Confirmar", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (pagoDAO.desactivar(idActual)) {
                JOptionPane.showMessageDialog(this, "Pago desactivado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                listar("");
            } else {
                JOptionPane.showMessageDialog(this, "Error al desactivar el pago", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void buscar() {
        listar(txtBuscar.getText());
    }
    
    private void cancelar() {
        limpiar();
        tabGeneral.setEnabledAt(0, true);
        tabGeneral.setEnabledAt(1, false);
        tabGeneral.setSelectedIndex(0);
        accion = "guardar";
    }
    
    private void limpiar() {
        txtId.setText("");
        cbxEvento.setSelectedIndex(0);
        txtFechaPago.setText(formatoFecha.format(new Date()));
        txtMonto.setText("");
        cbxMetodoPago.setSelectedIndex(0);
        txtReferencia.setText("");
        txtObservaciones.setText("");
        idActual = 0;
    }
    
    // ========== NetBeans GENERA ESTO ==========
    
    private void initComponentes() {
        // Pestañas
        tabGeneral = new javax.swing.JTabbedPane();
        
        // Panel Listado
        javax.swing.JPanel panelListado = new javax.swing.JPanel();
        panelListado.setLayout(null);
        
        lblBuscar = new javax.swing.JLabel();
        lblBuscar.setText("Buscar:");
        lblBuscar.setBounds(20, 20, 60, 25);
        panelListado.add(lblBuscar);
        
        txtBuscar = new javax.swing.JTextField();
        txtBuscar.setBounds(80, 20, 250, 25);
        panelListado.add(txtBuscar);
        
        btnBuscar = new javax.swing.JButton();
        btnBuscar.setText("Buscar");
        btnBuscar.setBounds(340, 20, 100, 25);
        btnBuscar.addActionListener(e -> buscar());
        panelListado.add(btnBuscar);
        
        btnNuevo = new javax.swing.JButton();
        btnNuevo.setText("Nuevo");
        btnNuevo.setBounds(450, 20, 100, 25);
        btnNuevo.addActionListener(e -> nuevo());
        panelListado.add(btnNuevo);
        
        btnEditar = new javax.swing.JButton();
        btnEditar.setText("Editar");
        btnEditar.setBounds(560, 20, 100, 25);
        btnEditar.addActionListener(e -> editar());
        panelListado.add(btnEditar);
        
        scrollTabla = new javax.swing.JScrollPane();
        scrollTabla.setBounds(20, 60, 800, 350);
        tablaListado = new javax.swing.JTable();
        scrollTabla.setViewportView(tablaListado);
        panelListado.add(scrollTabla);
        
        btnActivar = new javax.swing.JButton();
        btnActivar.setText("Activar");
        btnActivar.setBounds(20, 430, 100, 30);
        btnActivar.addActionListener(e -> activar());
        panelListado.add(btnActivar);
        
        btnDesactivar = new javax.swing.JButton();
        btnDesactivar.setText("Desactivar");
        btnDesactivar.setBounds(130, 430, 100, 30);
        btnDesactivar.addActionListener(e -> desactivar());
        panelListado.add(btnDesactivar);
        
        lblTotalRegistros = new javax.swing.JLabel();
        lblTotalRegistros.setText("Total registros: 0");
        lblTotalRegistros.setBounds(650, 435, 200, 25);
        panelListado.add(lblTotalRegistros);
        
        // Panel Mantenimiento
        javax.swing.JPanel panelMantenimiento = new javax.swing.JPanel();
        panelMantenimiento.setLayout(null);
        
        lblId = new javax.swing.JLabel();
        lblId.setText("ID:");
        lblId.setBounds(30, 30, 60, 25);
        panelMantenimiento.add(lblId);
        
        txtId = new javax.swing.JTextField();
        txtId.setBounds(100, 30, 80, 25);
        txtId.setEditable(false);
        txtId.setVisible(false);
        panelMantenimiento.add(txtId);
        
        lblEvento = new javax.swing.JLabel();
        lblEvento.setText("Evento (*):");
        lblEvento.setBounds(30, 70, 100, 25);
        panelMantenimiento.add(lblEvento);
        
        cbxEvento = new javax.swing.JComboBox<>();
        cbxEvento.setBounds(140, 70, 450, 25);
        panelMantenimiento.add(cbxEvento);
        
        lblFechaPago = new javax.swing.JLabel();
        lblFechaPago.setText("Fecha Pago:");
        lblFechaPago.setBounds(30, 110, 120, 25);
        panelMantenimiento.add(lblFechaPago);
        
        txtFechaPago = new javax.swing.JTextField();
        txtFechaPago.setBounds(160, 110, 150, 25);
        panelMantenimiento.add(txtFechaPago);
        
        lblMonto = new javax.swing.JLabel();
        lblMonto.setText("Monto ($):");
        lblMonto.setBounds(30, 150, 100, 25);
        panelMantenimiento.add(lblMonto);
        
        txtMonto = new javax.swing.JTextField();
        txtMonto.setBounds(140, 150, 150, 25);
        panelMantenimiento.add(txtMonto);
        
        lblMetodoPago = new javax.swing.JLabel();
        lblMetodoPago.setText("Método Pago:");
        lblMetodoPago.setBounds(30, 190, 120, 25);
        panelMantenimiento.add(lblMetodoPago);
        
        cbxMetodoPago = new javax.swing.JComboBox<>();
        cbxMetodoPago.setBounds(160, 190, 150, 25);
        panelMantenimiento.add(cbxMetodoPago);
        
        lblReferencia = new javax.swing.JLabel();
        lblReferencia.setText("Referencia:");
        lblReferencia.setBounds(30, 230, 100, 25);
        panelMantenimiento.add(lblReferencia);
        
        txtReferencia = new javax.swing.JTextField();
        txtReferencia.setBounds(140, 230, 250, 25);
        panelMantenimiento.add(txtReferencia);
        
        lblObservaciones = new javax.swing.JLabel();
        lblObservaciones.setText("Observaciones:");
        lblObservaciones.setBounds(30, 270, 100, 25);
        panelMantenimiento.add(lblObservaciones);
        
        txtObservaciones = new javax.swing.JTextField();
        txtObservaciones.setBounds(140, 270, 400, 60);
        panelMantenimiento.add(txtObservaciones);
        
        lblObligatorio = new javax.swing.JLabel();
        lblObligatorio.setText("(*) Campo obligatorio");
        lblObligatorio.setFont(new java.awt.Font("Arial", java.awt.Font.ITALIC, 11));
        lblObligatorio.setBounds(140, 350, 200, 20);
        panelMantenimiento.add(lblObligatorio);
        
        btnGuardar = new javax.swing.JButton();
        btnGuardar.setText("Guardar");
        btnGuardar.setBounds(140, 390, 100, 35);
        btnGuardar.addActionListener(e -> guardar());
        panelMantenimiento.add(btnGuardar);
        
        btnCancelar = new javax.swing.JButton();
        btnCancelar.setText("Cancelar");
        btnCancelar.setBounds(260, 390, 100, 35);
        btnCancelar.addActionListener(e -> cancelar());
        panelMantenimiento.add(btnCancelar);
        
        // Agregar pestañas
        tabGeneral.addTab("Listado", panelListado);
        tabGeneral.addTab("Mantenimiento", panelMantenimiento);
        
        // Layout principal
        setLayout(new java.awt.BorderLayout());
        add(tabGeneral, java.awt.BorderLayout.CENTER);
        
        pack();
    }
    
    // Variables declaration (NetBeans)
    // End of variables declaration

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        Mantenimiento = new javax.swing.JPanel();
        lblId = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        lblEvento = new javax.swing.JLabel();
        cbxEvento = new javax.swing.JComboBox<>();
        lblFechaPago = new javax.swing.JLabel();
        txtFechaPago = new javax.swing.JTextField();
        lblMonto = new javax.swing.JLabel();
        txtMonto = new javax.swing.JTextField();
        lblMetodoPago = new javax.swing.JLabel();
        cbxMetodoPago = new javax.swing.JComboBox<>();
        lblReferencia = new javax.swing.JLabel();
        txtReferencia = new javax.swing.JTextField();
        lblObservaciones = new javax.swing.JLabel();
        txtObservaciones = new javax.swing.JTextField();
        lblObligatorio = new javax.swing.JLabel();
        btnGuardar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        Listado = new javax.swing.JPanel();
        lblBuscar = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        btnBuscar = new javax.swing.JButton();
        btnNuevo = new javax.swing.JButton();
        btnEditar = new javax.swing.JButton();
        scrollTabla = new javax.swing.JScrollPane();
        tablaListado = new javax.swing.JTable();
        btnActivar = new javax.swing.JButton();
        btnDesactivar = new javax.swing.JButton();
        lblTotalRegistros = new javax.swing.JLabel();

        lblId.setText("ID:");

        lblEvento.setText("Evento:");

        lblFechaPago.setText("Fecha Pago:");

        txtFechaPago.setText("dd/MM/yyyy");

        lblMonto.setText("Monto ($):");

        lblMetodoPago.setText("Método de Pago:");

        cbxMetodoPago.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Efectivo", "Transferecia", "Tarjeta", "Nequi", "Daviplata" }));

        lblReferencia.setText("Referencia:");

        lblObservaciones.setText("Observaciones:");

        lblObligatorio.setText("(*) Campo Obligatorio");

        btnGuardar.setText("Guardar");

        btnCancelar.setText("Cancelar");

        javax.swing.GroupLayout MantenimientoLayout = new javax.swing.GroupLayout(Mantenimiento);
        Mantenimiento.setLayout(MantenimientoLayout);
        MantenimientoLayout.setHorizontalGroup(
            MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MantenimientoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(MantenimientoLayout.createSequentialGroup()
                                .addComponent(lblId)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(MantenimientoLayout.createSequentialGroup()
                                .addComponent(lblEvento)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbxEvento, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(MantenimientoLayout.createSequentialGroup()
                                .addComponent(lblFechaPago)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtFechaPago))
                            .addGroup(MantenimientoLayout.createSequentialGroup()
                                .addComponent(lblMonto)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtMonto)))
                        .addGroup(MantenimientoLayout.createSequentialGroup()
                            .addComponent(lblMetodoPago)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cbxMetodoPago, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(MantenimientoLayout.createSequentialGroup()
                            .addComponent(lblReferencia)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtReferencia)))
                    .addGroup(MantenimientoLayout.createSequentialGroup()
                        .addComponent(lblObservaciones)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtObservaciones, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblObligatorio)
                    .addGroup(MantenimientoLayout.createSequentialGroup()
                        .addComponent(btnGuardar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancelar)))
                .addContainerGap(687, Short.MAX_VALUE))
        );
        MantenimientoLayout.setVerticalGroup(
            MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MantenimientoLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblId)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEvento)
                    .addComponent(cbxEvento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFechaPago)
                    .addComponent(txtFechaPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMonto)
                    .addComponent(txtMonto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMetodoPago)
                    .addComponent(cbxMetodoPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblReferencia)
                    .addComponent(txtReferencia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblObservaciones)
                    .addComponent(txtObservaciones, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(lblObligatorio)
                .addGap(44, 44, 44)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuardar)
                    .addComponent(btnCancelar))
                .addContainerGap(179, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Mantenimieto", Mantenimiento);

        lblBuscar.setText("Buscar:");

        btnBuscar.setText("Buscar");

        btnNuevo.setText("Nuevo");

        btnEditar.setText("Editar");

        tablaListado.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        scrollTabla.setViewportView(tablaListado);

        btnActivar.setText("Activar");

        btnDesactivar.setText("Desactivar");

        lblTotalRegistros.setText("Total Registros: 0");

        javax.swing.GroupLayout ListadoLayout = new javax.swing.GroupLayout(Listado);
        Listado.setLayout(ListadoLayout);
        ListadoLayout.setHorizontalGroup(
            ListadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ListadoLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(ListadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ListadoLayout.createSequentialGroup()
                        .addGroup(ListadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(scrollTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 1014, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(ListadoLayout.createSequentialGroup()
                                .addComponent(lblBuscar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 703, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnBuscar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnNuevo)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnEditar)))
                        .addContainerGap(19, Short.MAX_VALUE))
                    .addGroup(ListadoLayout.createSequentialGroup()
                        .addComponent(btnActivar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDesactivar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblTotalRegistros)
                        .addGap(185, 185, 185))))
        );
        ListadoLayout.setVerticalGroup(
            ListadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ListadoLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(ListadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBuscar)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscar)
                    .addComponent(btnNuevo)
                    .addComponent(btnEditar))
                .addGap(18, 18, 18)
                .addComponent(scrollTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 443, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(ListadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnActivar)
                    .addComponent(btnDesactivar)
                    .addComponent(lblTotalRegistros))
                .addContainerGap(66, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Listado", Listado);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Listado;
    private javax.swing.JPanel Mantenimiento;
    private javax.swing.JButton btnActivar;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnDesactivar;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JComboBox<String> cbxEvento;
    private javax.swing.JComboBox<String> cbxMetodoPago;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JLabel lblEvento;
    private javax.swing.JLabel lblFechaPago;
    private javax.swing.JLabel lblId;
    private javax.swing.JLabel lblMetodoPago;
    private javax.swing.JLabel lblMonto;
    private javax.swing.JLabel lblObligatorio;
    private javax.swing.JLabel lblObservaciones;
    private javax.swing.JLabel lblReferencia;
    private javax.swing.JLabel lblTotalRegistros;
    private javax.swing.JScrollPane scrollTabla;
    private javax.swing.JTable tablaListado;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtFechaPago;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtMonto;
    private javax.swing.JTextField txtObservaciones;
    private javax.swing.JTextField txtReferencia;
    // End of variables declaration//GEN-END:variables
}
