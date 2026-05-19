
package presentacion;
import datos.EventoDAO;
import entidades.Evento;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
public class frmReporteEventos extends javax.swing.JInternalFrame {




    
    
        
    // ========== VARIABLES DE CONTROL ==========
    private EventoDAO eventoDAO;
    private DefaultTableModel modeloTabla;
    private DecimalFormat formatoCOP = new DecimalFormat("#,###");
    private SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
    
    public frmReporteEventos() {
        initComponentes();
        iniciar();
        setVisible(true);
    }
    
    private void iniciar() {
        eventoDAO = new EventoDAO();
        
        // Configurar modelo de tabla
        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        modeloTabla.setColumnIdentifiers(new String[]{"ID", "CLIENTE", "EVENTO", "FECHA", "LUGAR", "PRESUPUESTO", "ESTADO"});
        tablaReporte.setModel(modeloTabla);
        
        // Configurar combo de estados
        configurarCombos();
        
        // Cargar todos los eventos
        cargarReporte();
        
        setTitle("Reporte de Eventos");
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setSize(900, 700);
    }
    
    private void configurarCombos() {
        cbxEstado.addItem("Todos");
        cbxEstado.addItem("Cotizando");
        cbxEstado.addItem("Confirmado");
        cbxEstado.addItem("En Progreso");
        cbxEstado.addItem("Realizado");
        cbxEstado.addItem("Cancelado");
    }
    
    private void cargarReporte() {
        cargarReporteConFiltros("", "", "Todos");
    }
    
    private void cargarReporteConFiltros(String fechaInicio, String fechaFin, String estado) {
        modeloTabla.setRowCount(0);
        
        List<Evento> lista = eventoDAO.listar("");
        double totalIngresos = 0;
        int totalEventos = 0;
        
        for (Evento e : lista) {
            boolean mostrar = true;
            
            // Filtrar por estado
            if (!estado.equals("Todos") && !e.getEstado().equals(estado)) {
                mostrar = false;
            }
            
            // Filtrar por fechas
            if (mostrar && !fechaInicio.isEmpty() && !fechaFin.isEmpty()) {
                try {
                    Date fechaEvento = formatoFecha.parse(e.getFechaEvento());
                    Date fechaInicioDate = formatoFecha.parse(fechaInicio);
                    Date fechaFinDate = formatoFecha.parse(fechaFin);
                    
                    if (fechaEvento.before(fechaInicioDate) || fechaEvento.after(fechaFinDate)) {
                        mostrar = false;
                    }
                } catch (Exception ex) {
                    // Si hay error en fecha, mostrar igual
                }
            }
            
            if (mostrar) {
                modeloTabla.addRow(new Object[]{
                    e.getId(),
                    e.getNombreCliente(),
                    e.getNombreEvento(),
                    e.getFechaEvento(),
                    e.getLugarEvento() != null ? e.getLugarEvento() : "",
                    "$" + formatoCOP.format(e.getPresupuestoTotal()),
                    e.getEstado()
                });
                totalIngresos += e.getPresupuestoTotal();
                totalEventos++;
            }
        }
        
        lblTotalEventos.setText("Total Eventos: " + totalEventos);
        lblTotalIngresos.setText("Total Ingresos: $" + formatoCOP.format(totalIngresos));
    }
    
    private void filtrar() {
        String fechaInicio = txtFechaInicio.getText().trim();
        String fechaFin = txtFechaFin.getText().trim();
        String estado = cbxEstado.getSelectedItem().toString();
        
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
        
        cargarReporteConFiltros(fechaInicio, fechaFin, estado);
    }
    
    private void limpiar() {
        txtFechaInicio.setText("");
        txtFechaFin.setText("");
        cbxEstado.setSelectedIndex(0);
        cargarReporte();
    }
    
    private void exportarExcel() {
        try {
            // Crear contenido en formato CSV (compatible con Excel)
            StringBuilder sb = new StringBuilder();
            sb.append("ID,CLIENTE,EVENTO,FECHA,LUGAR,PRESUPUESTO,ESTADO\n");
            
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
            
            // Guardar archivo
            java.io.FileWriter fw = new java.io.FileWriter("reporte_eventos_" + 
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
    
  
    
    private void initComponentes() {
        javax.swing.JPanel panelPrincipal = new javax.swing.JPanel();
        panelPrincipal.setLayout(null);
        
        // Título
        lblTitulo = new javax.swing.JLabel();
        lblTitulo.setText("REPORTE DE EVENTOS");
        lblTitulo.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 18));
        lblTitulo.setBounds(20, 20, 300, 30);
        panelPrincipal.add(lblTitulo);
        
        // Fecha Inicio
        lblFechaInicio = new javax.swing.JLabel();
        lblFechaInicio.setText("Fecha Inicio:");
        lblFechaInicio.setBounds(30, 70, 100, 25);
        panelPrincipal.add(lblFechaInicio);
        
        txtFechaInicio = new javax.swing.JTextField();
        txtFechaInicio.setBounds(140, 70, 120, 25);
        panelPrincipal.add(txtFechaInicio);
        
        // Fecha Fin
        lblFechaFin = new javax.swing.JLabel();
        lblFechaFin.setText("Fecha Fin:");
        lblFechaFin.setBounds(30, 110, 100, 25);
        panelPrincipal.add(lblFechaFin);
        
        txtFechaFin = new javax.swing.JTextField();
        txtFechaFin.setBounds(140, 110, 120, 25);
        panelPrincipal.add(txtFechaFin);
        
        // Estado
        lblEstado = new javax.swing.JLabel();
        lblEstado.setText("Estado:");
        lblEstado.setBounds(30, 150, 100, 25);
        panelPrincipal.add(lblEstado);
        
        cbxEstado = new javax.swing.JComboBox<>();
        cbxEstado.setBounds(140, 150, 150, 25);
        panelPrincipal.add(cbxEstado);
        
        // Botones Filtrar y Limpiar
        btnFiltrar = new javax.swing.JButton();
        btnFiltrar.setText("Filtrar");
        btnFiltrar.setBounds(320, 150, 100, 25);
        btnFiltrar.addActionListener(e -> filtrar());
        panelPrincipal.add(btnFiltrar);
        
        btnLimpiar = new javax.swing.JButton();
        btnLimpiar.setText("Limpiar");
        btnLimpiar.setBounds(430, 150, 100, 25);
        btnLimpiar.addActionListener(e -> limpiar());
        panelPrincipal.add(btnLimpiar);
        
        // Tabla
        scrollTabla = new javax.swing.JScrollPane();
        scrollTabla.setBounds(20, 200, 850, 380);
        tablaReporte = new javax.swing.JTable();
        scrollTabla.setViewportView(tablaReporte);
        panelPrincipal.add(scrollTabla);
        
        // Totales
        lblTotalEventos = new javax.swing.JLabel();
        lblTotalEventos.setText("Total Eventos: 0");
        lblTotalEventos.setBounds(20, 600, 200, 25);
        panelPrincipal.add(lblTotalEventos);
        
        lblTotalIngresos = new javax.swing.JLabel();
        lblTotalIngresos.setText("Total Ingresos: $0");
        lblTotalIngresos.setBounds(250, 600, 250, 25);
        panelPrincipal.add(lblTotalIngresos);
        
        // Botones Exportar e Imprimir
        btnExportarExcel = new javax.swing.JButton();
        btnExportarExcel.setText("Exportar a Excel");
        btnExportarExcel.setBounds(600, 600, 120, 30);
        btnExportarExcel.addActionListener(e -> exportarExcel());
        panelPrincipal.add(btnExportarExcel);
        
        btnImprimir = new javax.swing.JButton();
        btnImprimir.setText("Imprimir");
        btnImprimir.setBounds(730, 600, 100, 30);
        btnImprimir.addActionListener(e -> imprimir());
        panelPrincipal.add(btnImprimir);
        
        setLayout(new java.awt.BorderLayout());
        add(panelPrincipal, java.awt.BorderLayout.CENTER);
        
        pack();
    }
    
  

 
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane3 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        lblFechaInicio = new javax.swing.JLabel();
        txtFechaInicio = new javax.swing.JTextField();
        lblFechaFin = new javax.swing.JLabel();
        txtFechaFin = new javax.swing.JTextField();
        lblEstado = new javax.swing.JLabel();
        cbxEstado = new javax.swing.JComboBox<>();
        btnFiltrar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        btnExportarExcel = new javax.swing.JButton();
        btnImprimir = new javax.swing.JButton();
        lblTotalEventos = new javax.swing.JLabel();
        lblTotalIngresos = new javax.swing.JLabel();
        scrollTabla = new javax.swing.JScrollPane();
        tablaReporte = new javax.swing.JTable();

        lblTitulo.setText("Reporte de eventos");

        lblFechaInicio.setText("Fecha Inicio:");

        txtFechaInicio.setText("dd/MM/yyyy");

        lblFechaFin.setText("Fecha Fin:");

        txtFechaFin.setText("dd/MM/yyyy");

        lblEstado.setText("Estado:");

        cbxEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todos", "Cotizado", "Confirmado", "Realizado", "Cancelado" }));

        btnFiltrar.setText("Filtrar");

        btnLimpiar.setText("Limpiar");

        btnExportarExcel.setText("Exportar a Excel");

        btnImprimir.setText("Imprimir");

        lblTotalEventos.setText("Total Eventos: 0");

        lblTotalIngresos.setText("Total Ingresos: $0");

        tablaReporte.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        scrollTabla.setViewportView(tablaReporte);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(scrollTabla)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(lblTitulo)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(lblFechaInicio)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(txtFechaInicio, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(lblFechaFin)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtFechaFin, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(lblEstado)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cbxEstado, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnFiltrar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnLimpiar)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(btnExportarExcel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnImprimir)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 554, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTotalIngresos)
                            .addComponent(lblTotalEventos))
                        .addGap(191, 191, 191))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(lblTitulo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFechaInicio)
                    .addComponent(txtFechaInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFechaFin)
                    .addComponent(txtFechaFin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEstado)
                    .addComponent(cbxEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnFiltrar)
                    .addComponent(btnLimpiar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scrollTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnExportarExcel)
                    .addComponent(btnImprimir)
                    .addComponent(lblTotalEventos))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTotalIngresos)
                .addContainerGap(60, Short.MAX_VALUE))
        );

        jTabbedPane3.addTab("Reporte de Eventos", jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane3)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane3)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExportarExcel;
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnImprimir;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JComboBox<String> cbxEstado;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblFechaFin;
    private javax.swing.JLabel lblFechaInicio;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblTotalEventos;
    private javax.swing.JLabel lblTotalIngresos;
    private javax.swing.JScrollPane scrollTabla;
    private javax.swing.JTable tablaReporte;
    private javax.swing.JTextField txtFechaFin;
    private javax.swing.JTextField txtFechaInicio;
    // End of variables declaration//GEN-END:variables
}
