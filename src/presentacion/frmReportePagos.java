
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
public class frmReportePagos extends javax.swing.JInternalFrame {





    
    // ========== COMPONENTES ==========
   
    
    // ========== VARIABLES DE CONTROL ==========
    private PagoDAO pagoDAO;
    private EventoDAO eventoDAO;
    private DefaultTableModel modeloTabla;
    private DecimalFormat formatoCOP = new DecimalFormat("#,###");
    private SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
    
    public frmReportePagos() {
        initComponentes();
        iniciar();
        setVisible(true);
    }
    
    private void iniciar() {
        pagoDAO = new PagoDAO();
        eventoDAO = new EventoDAO();
        
        // Configurar modelo de tabla
        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        modeloTabla.setColumnIdentifiers(new String[]{"ID", "EVENTO", "FECHA", "MONTO", "MÉTODO", "REFERENCIA", "ESTADO"});
        tablaReporte.setModel(modeloTabla);
        
        // Configurar combos
        configurarCombos();
        cargarEventos();
        
        // Cargar todos los pagos
        cargarReporte();
        
        setTitle("Reporte de Pagos");
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setSize(900, 720);
    }
    
    private void configurarCombos() {
        // Métodos de pago
        cbxMetodoPago.addItem("Todos");
        cbxMetodoPago.addItem("Efectivo");
        cbxMetodoPago.addItem("Transferencia");
        cbxMetodoPago.addItem("Tarjeta Débito");
        cbxMetodoPago.addItem("Tarjeta Crédito");
        cbxMetodoPago.addItem("Nequi");
        cbxMetodoPago.addItem("Daviplata");
    }
    
    private void cargarEventos() {
        cbxEvento.addItem("Todos");
        List<Evento> eventos = eventoDAO.listar("");
        for (Evento e : eventos) {
            if (e.isActivo()) {
                cbxEvento.addItem(e.getId() + " - " + e.getNombreEvento());
            }
        }
    }
    
    private void cargarReporte() {
        cargarReporteConFiltros("", "", "", "Todos");
    }
    
    private void cargarReporteConFiltros(String fechaInicio, String fechaFin, String evento, String metodoPago) {
        modeloTabla.setRowCount(0);
        
        List<Pago> lista = pagoDAO.listar("");
        double totalMonto = 0;
        int totalPagos = 0;
        
        for (Pago p : lista) {
            boolean mostrar = true;
            
            // Filtrar por evento
            if (!evento.equals("Todos")) {
                int eventoId = Integer.parseInt(evento.split(" - ")[0]);
                if (p.getEventoId() != eventoId) {
                    mostrar = false;
                }
            }
            
            // Filtrar por método de pago
            if (mostrar && !metodoPago.equals("Todos") && !p.getMetodoPago().equals(metodoPago)) {
                mostrar = false;
            }
            
            // Filtrar por fechas
            if (mostrar && !fechaInicio.isEmpty() && !fechaFin.isEmpty() && p.getFechaPago() != null) {
                try {
                    Date fechaPago = formatoFecha.parse(p.getFechaPago());
                    Date fechaInicioDate = formatoFecha.parse(fechaInicio);
                    Date fechaFinDate = formatoFecha.parse(fechaFin);
                    
                    if (fechaPago.before(fechaInicioDate) || fechaPago.after(fechaFinDate)) {
                        mostrar = false;
                    }
                } catch (Exception ex) {
                    // Si hay error en fecha, mostrar igual
                }
            }
            
            if (mostrar) {
                String estado = p.isActivo() ? "Activo" : "Inactivo";
                modeloTabla.addRow(new Object[]{
                    p.getId(),
                    p.getNombreEvento(),
                    p.getFechaPago() != null ? p.getFechaPago() : "",
                    "$" + formatoCOP.format(p.getMonto()),
                    p.getMetodoPago(),
                    p.getReferenciaPago() != null ? p.getReferenciaPago() : "",
                    estado
                });
                totalMonto += p.getMonto();
                totalPagos++;
            }
        }
        
        lblTotalPagos.setText("Total Pagos: " + totalPagos);
        lblTotalMonto.setText("Total Monto: $" + formatoCOP.format(totalMonto));
    }
    
    private void filtrar() {
        String fechaInicio = txtFechaInicio.getText().trim();
        String fechaFin = txtFechaFin.getText().trim();
        String evento = cbxEvento.getSelectedItem().toString();
        String metodoPago = cbxMetodoPago.getSelectedItem().toString();
        
        // Validar formato de fechas si están llenas
        if (!fechaInicio.isEmpty()) {
            try {
                formatoFecha.parse(fechaInicio);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Formato de fecha inicio inválido. Use dd/MM/yyyy", "Error", JOptionPane.ERROR_MESSAGE);
                txtFechaInicio.requestFocus();
                return;
            }
        }
        
        if (!fechaFin.isEmpty()) {
            try {
                formatoFecha.parse(fechaFin);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Formato de fecha fin inválido. Use dd/MM/yyyy", "Error", JOptionPane.ERROR_MESSAGE);
                txtFechaFin.requestFocus();
                return;
            }
        }
        
        cargarReporteConFiltros(fechaInicio, fechaFin, evento, metodoPago);
    }
    
    private void limpiar() {
        txtFechaInicio.setText("");
        txtFechaFin.setText("");
        cbxEvento.setSelectedIndex(0);
        cbxMetodoPago.setSelectedIndex(0);
        cargarReporte();
    }
    
    private void exportarExcel() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("ID,EVENTO,FECHA,MONTO,MÉTODO,REFERENCIA,ESTADO\n");
            
            for (int i = 0; i < tablaReporte.getRowCount(); i++) {
                for (int j = 0; j < tablaReporte.getColumnCount(); j++) {
                    String valor = tablaReporte.getValueAt(i, j) != null ? 
                                   tablaReporte.getValueAt(i, j).toString().replace(",", ".") : "";
                    sb.append("\"").append(valor).append("\"");
                    if (j < tablaReporte.getColumnCount() - 1) {
                        sb.append(",");
                    }
                }
                sb.append("\n");
            }
            
            java.io.FileWriter fw = new java.io.FileWriter("reporte_pagos_" + 
                new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".csv");
            fw.write(sb.toString());
            fw.close();
            
            JOptionPane.showMessageDialog(this, "Reporte exportado a Excel correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al exportar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void imprimir() {
        try {
            boolean done = tablaReporte.print();
            if (done) {
                JOptionPane.showMessageDialog(this, "Impresión enviada correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Impresión cancelada", "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al imprimir: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // ========== NetBeans GENERA ESTO ==========
    
    private void initComponentes() {
        javax.swing.JPanel panelPrincipal = new javax.swing.JPanel();
        panelPrincipal.setLayout(null);
        
        // Título
        lblTitulo = new javax.swing.JLabel();
        lblTitulo.setText("REPORTE DE PAGOS");
        lblTitulo.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 18));
        lblTitulo.setBounds(20, 20, 300, 30);
        panelPrincipal.add(lblTitulo);
        
        // Evento
        lblEvento = new javax.swing.JLabel();
        lblEvento.setText("Evento:");
        lblEvento.setBounds(30, 70, 100, 25);
        panelPrincipal.add(lblEvento);
        
        cbxEvento = new javax.swing.JComboBox<>();
        cbxEvento.setBounds(140, 70, 350, 25);
        panelPrincipal.add(cbxEvento);
        
        // Fecha Inicio
        lblFechaInicio = new javax.swing.JLabel();
        lblFechaInicio.setText("Fecha Inicio:");
        lblFechaInicio.setBounds(30, 110, 100, 25);
        panelPrincipal.add(lblFechaInicio);
        
        txtFechaInicio = new javax.swing.JTextField();
        txtFechaInicio.setBounds(140, 110, 120, 25);
        panelPrincipal.add(txtFechaInicio);
        
        // Fecha Fin
        lblFechaFin = new javax.swing.JLabel();
        lblFechaFin.setText("Fecha Fin:");
        lblFechaFin.setBounds(30, 150, 100, 25);
        panelPrincipal.add(lblFechaFin);
        
        txtFechaFin = new javax.swing.JTextField();
        txtFechaFin.setBounds(140, 150, 120, 25);
        panelPrincipal.add(txtFechaFin);
        
        // Método Pago
        lblMetodoPago = new javax.swing.JLabel();
        lblMetodoPago.setText("Método Pago:");
        lblMetodoPago.setBounds(30, 190, 100, 25);
        panelPrincipal.add(lblMetodoPago);
        
        cbxMetodoPago = new javax.swing.JComboBox<>();
        cbxMetodoPago.setBounds(140, 190, 150, 25);
        panelPrincipal.add(cbxMetodoPago);
        
        // Botones Filtrar y Limpiar
        btnFiltrar = new javax.swing.JButton();
        btnFiltrar.setText("Filtrar");
        btnFiltrar.setBounds(320, 190, 100, 25);
        btnFiltrar.addActionListener(e -> filtrar());
        panelPrincipal.add(btnFiltrar);
        
        btnLimpiar = new javax.swing.JButton();
        btnLimpiar.setText("Limpiar");
        btnLimpiar.setBounds(430, 190, 100, 25);
        btnLimpiar.addActionListener(e -> limpiar());
        panelPrincipal.add(btnLimpiar);
        
        // Tabla
        scrollTabla = new javax.swing.JScrollPane();
        scrollTabla.setBounds(20, 240, 850, 350);
        tablaReporte = new javax.swing.JTable();
        scrollTabla.setViewportView(tablaReporte);
        panelPrincipal.add(scrollTabla);
        
        // Totales
        lblTotalPagos = new javax.swing.JLabel();
        lblTotalPagos.setText("Total Pagos: 0");
        lblTotalPagos.setBounds(20, 610, 200, 25);
        panelPrincipal.add(lblTotalPagos);
        
        lblTotalMonto = new javax.swing.JLabel();
        lblTotalMonto.setText("Total Monto: $0");
        lblTotalMonto.setBounds(250, 610, 200, 25);
        panelPrincipal.add(lblTotalMonto);
        
        // Botones Exportar e Imprimir
        btnExportarExcel = new javax.swing.JButton();
        btnExportarExcel.setText("Exportar a Excel");
        btnExportarExcel.setBounds(600, 610, 120, 30);
        btnExportarExcel.addActionListener(e -> exportarExcel());
        panelPrincipal.add(btnExportarExcel);
        
        btnImprimir = new javax.swing.JButton();
        btnImprimir.setText("Imprimir");
        btnImprimir.setBounds(730, 610, 100, 30);
        btnImprimir.addActionListener(e -> imprimir());
        panelPrincipal.add(btnImprimir);
        
        setLayout(new java.awt.BorderLayout());
        add(panelPrincipal, java.awt.BorderLayout.CENTER);
        
        pack();
    }
    
    // Variables declaration (NetBeans)
    // End of variables declaration

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        TotalPagos = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        lblEvento = new javax.swing.JLabel();
        cbxEvento = new javax.swing.JComboBox<>();
        lblFechaInicio = new javax.swing.JLabel();
        txtFechaInicio = new javax.swing.JTextField();
        lblFechaFin = new javax.swing.JLabel();
        txtFechaFin = new javax.swing.JTextField();
        lblMetodoPago = new javax.swing.JLabel();
        cbxMetodoPago = new javax.swing.JComboBox<>();
        btnFiltrar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        scrollTabla = new javax.swing.JScrollPane();
        tablaReporte = new javax.swing.JTable();
        btnExportarExcel = new javax.swing.JButton();
        btnImprimir = new javax.swing.JButton();
        lblTotalPagos = new javax.swing.JLabel();
        lblTotalMonto = new javax.swing.JLabel();

        lblTitulo.setText("Reporte de pagos");

        lblEvento.setText("Evento:");

        lblFechaInicio.setText("Fecha Inicio:");

        lblFechaFin.setText("Fecha Fin:");

        lblMetodoPago.setText("Metodo de Pago:");

        cbxMetodoPago.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "todos", "Efectivo", "Transferencia", "Tarjeta", "Nequi", "Daviplata" }));

        btnFiltrar.setText("Filtrar");

        btnLimpiar.setText("Limpiar");

        tablaReporte.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        scrollTabla.setViewportView(tablaReporte);

        btnExportarExcel.setText("Exportar a Excel");

        btnImprimir.setText("Imprimir");

        lblTotalPagos.setText("Total Pagos: 0");

        lblTotalMonto.setText("Total Monto: $0");

        javax.swing.GroupLayout TotalPagosLayout = new javax.swing.GroupLayout(TotalPagos);
        TotalPagos.setLayout(TotalPagosLayout);
        TotalPagosLayout.setHorizontalGroup(
            TotalPagosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TotalPagosLayout.createSequentialGroup()
                .addGroup(TotalPagosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(TotalPagosLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(TotalPagosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(TotalPagosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(lblTitulo)
                                .addGroup(TotalPagosLayout.createSequentialGroup()
                                    .addComponent(lblEvento)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cbxEvento, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(TotalPagosLayout.createSequentialGroup()
                                    .addComponent(lblFechaInicio)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtFechaInicio))
                                .addGroup(TotalPagosLayout.createSequentialGroup()
                                    .addComponent(lblFechaFin)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtFechaFin))
                                .addGroup(TotalPagosLayout.createSequentialGroup()
                                    .addComponent(lblMetodoPago)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(cbxMetodoPago, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(TotalPagosLayout.createSequentialGroup()
                                .addComponent(btnFiltrar)
                                .addGap(18, 18, 18)
                                .addComponent(btnLimpiar))))
                    .addGroup(TotalPagosLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(TotalPagosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(scrollTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 1018, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, TotalPagosLayout.createSequentialGroup()
                                .addComponent(btnExportarExcel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnImprimir)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(TotalPagosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblTotalMonto)
                                    .addComponent(lblTotalPagos))
                                .addGap(103, 103, 103)))))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        TotalPagosLayout.setVerticalGroup(
            TotalPagosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TotalPagosLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(lblTitulo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(TotalPagosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEvento)
                    .addComponent(cbxEvento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(TotalPagosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFechaInicio)
                    .addComponent(txtFechaInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(TotalPagosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFechaFin)
                    .addComponent(txtFechaFin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(TotalPagosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblMetodoPago)
                    .addComponent(cbxMetodoPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(TotalPagosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnFiltrar)
                    .addComponent(btnLimpiar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(TotalPagosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnExportarExcel)
                    .addComponent(btnImprimir)
                    .addComponent(lblTotalPagos))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTotalMonto)
                .addContainerGap(40, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Reporte de pagos", TotalPagos);

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
    private javax.swing.JPanel TotalPagos;
    private javax.swing.JButton btnExportarExcel;
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnImprimir;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JComboBox<String> cbxEvento;
    private javax.swing.JComboBox<String> cbxMetodoPago;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblEvento;
    private javax.swing.JLabel lblFechaFin;
    private javax.swing.JLabel lblFechaInicio;
    private javax.swing.JLabel lblMetodoPago;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblTotalMonto;
    private javax.swing.JLabel lblTotalPagos;
    private javax.swing.JScrollPane scrollTabla;
    private javax.swing.JTable tablaReporte;
    private javax.swing.JTextField txtFechaFin;
    private javax.swing.JTextField txtFechaInicio;
    // End of variables declaration//GEN-END:variables
}
