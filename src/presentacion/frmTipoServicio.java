package presentacion;

import datos.TipoServicioDAO;
import entidades.TipoServicio;
import java.util.List;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class FrmTipoServicio extends JInternalFrame {
    
    // Variables de control
    private TipoServicioDAO dao;
    private DefaultTableModel modeloTabla;
    private String accion;
    private int idActual;
    private String nombreActual;
    
    public FrmTipoServicio() {
        initComponents();
        configurarEventos();
        iniciar();
        setVisible(true);
    }
    
    private void iniciar() {
        dao = new TipoServicioDAO();
        accion = "guardar";
        
        // Configurar modelo de tabla
        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        modeloTabla.setColumnIdentifiers(new String[]{"ID", "NOMBRE", "DESCRIPCIÓN", "ESTADO"});
        tablaListado.setModel(modeloTabla);
        
        // Primero quitar el ActionListener temporalmente para evitar el error
        java.awt.event.ActionListener[] listeners = cbxFiltrado.getActionListeners();
        for (java.awt.event.ActionListener al : listeners) {
            cbxFiltrado.removeActionListener(al);
        }
        
        // Configurar combo filtro
        cbxFiltrado.removeAllItems();
        cbxFiltrado.addItem("Activos");
        cbxFiltrado.addItem("Inactivos");
        cbxFiltrado.addItem("Todos");
        cbxFiltrado.setSelectedIndex(0);  // Seleccionar "Activos" por defecto
        
        // Reagregar el listener después de configurar
        cbxFiltrado.addActionListener(e -> listar(txtBuscar.getText()));
        
        listar("");
        jTabbedPane1.setEnabledAt(1, false);
        
        setTitle("Tipos de Servicio");
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setSize(850, 550);
    }
    
    private void configurarEventos() {
        btnBuscar.addActionListener(e -> listar(txtBuscar.getText()));
        btnNuevo.addActionListener(e -> nuevo());
        btnEditar.addActionListener(e -> editar());
        btnGuardar.addActionListener(e -> guardar());
        btnCancelar.addActionListener(e -> cancelar());
        btnActivar.addActionListener(e -> activar());
        btnDesactivar.addActionListener(e -> desactivar());
        
        tablaListado.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int fila = tablaListado.getSelectedRow();
                if (fila >= 0) {
                    idActual = Integer.parseInt(tablaListado.getValueAt(fila, 0).toString());
                    nombreActual = tablaListado.getValueAt(fila, 1).toString();
                }
            }
        });
    }
    
    private void listar(String texto) {
        // Verificar que el combo tenga un elemento seleccionado
        if (cbxFiltrado.getSelectedItem() == null) {
            return;
        }
        
        modeloTabla.setRowCount(0);
        String filtroEstado = cbxFiltrado.getSelectedItem().toString();
        List<TipoServicio> lista = dao.listarConFiltro(texto, filtroEstado);
        
        for (TipoServicio ts : lista) {
            String estado = ts.isActivo() ? "Activo" : "Inactivo";
            modeloTabla.addRow(new Object[]{
                ts.getId(),
                ts.getNombre(),
                ts.getDescripcion(),
                estado
            });
        }
        
        lblTotalRegistardos.setText("Total registros: " + lista.size());
    }
    
    private void nuevo() {
        accion = "guardar";
        limpiar();
        jTabbedPane1.setEnabledAt(0, false);
        jTabbedPane1.setEnabledAt(1, true);
        jTabbedPane1.setSelectedIndex(1);
        txtNombre.requestFocus();
    }
    
    private void editar() {
        int fila = tablaListado.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un registro", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        accion = "editar";
        idActual = Integer.parseInt(tablaListado.getValueAt(fila, 0).toString());
        txtId.setText(String.valueOf(idActual));
        txtNombre.setText(tablaListado.getValueAt(fila, 1).toString());
        txtDescripcion.setText(tablaListado.getValueAt(fila, 2).toString());
        nombreActual = tablaListado.getValueAt(fila, 1).toString();
        
        jTabbedPane1.setEnabledAt(0, false);
        jTabbedPane1.setEnabledAt(1, true);
        jTabbedPane1.setSelectedIndex(1);
        txtNombre.requestFocus();
    }
    
    private void guardar() {
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            txtNombre.requestFocus();
            return;
        }
        
        if (nombre.length() > 50) {
            JOptionPane.showMessageDialog(this, "El nombre no puede tener más de 50 caracteres", "Error", JOptionPane.ERROR_MESSAGE);
            txtNombre.requestFocus();
            return;
        }
        
        if (accion.equals("guardar")) {
            if (dao.existe(nombre)) {
                JOptionPane.showMessageDialog(this, "Ya existe un tipo de servicio con ese nombre", "Error", JOptionPane.ERROR_MESSAGE);
                txtNombre.requestFocus();
                return;
            }
            
            TipoServicio nuevo = new TipoServicio();
            nuevo.setNombre(nombre);
            nuevo.setDescripcion(descripcion);
            nuevo.setActivo(true);
            
            if (dao.insertar(nuevo)) {
                JOptionPane.showMessageDialog(this, "Registro guardado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                listar("");
                cancelar();
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            if (!nombre.equals(nombreActual) && dao.existe(nombre)) {
                JOptionPane.showMessageDialog(this, "Ya existe un tipo de servicio con ese nombre", "Error", JOptionPane.ERROR_MESSAGE);
                txtNombre.requestFocus();
                return;
            }
            
            TipoServicio editar = new TipoServicio();
            editar.setId(idActual);
            editar.setNombre(nombre);
            editar.setDescripcion(descripcion);
            editar.setActivo(true);
            
            if (dao.actualizar(editar)) {
                JOptionPane.showMessageDialog(this, "Registro actualizado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                listar("");
                cancelar();
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar", "Error", JOptionPane.ERROR_MESSAGE);
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
        String nombre = tablaListado.getValueAt(fila, 1).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this, "¿Activar: " + nombre + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.activar(idActual)) {
                JOptionPane.showMessageDialog(this, "Registro activado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                listar("");
            } else {
                JOptionPane.showMessageDialog(this, "Error al activar", "Error", JOptionPane.ERROR_MESSAGE);
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
        String nombre = tablaListado.getValueAt(fila, 1).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this, "¿Desactivar: " + nombre + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.desactivar(idActual)) {
                JOptionPane.showMessageDialog(this, "Registro desactivado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                listar("");
            } else {
                JOptionPane.showMessageDialog(this, "Error al desactivar", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void cancelar() {
        limpiar();
        jTabbedPane1.setEnabledAt(0, true);
        jTabbedPane1.setEnabledAt(1, false);
        jTabbedPane1.setSelectedIndex(0);
        accion = "guardar";
    }
    
    private void limpiar() {
        txtId.setText("");
        txtNombre.setText("");
        txtDescripcion.setText("");
        idActual = 0;
        nombreActual = "";
    }
    
        


    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        lblBuscar = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        btnBuscar = new javax.swing.JButton();
        btnNuevo = new javax.swing.JButton();
        btnEditar = new javax.swing.JButton();
        scrollTabla = new javax.swing.JScrollPane();
        tablaListado = new javax.swing.JTable();
        btnActivar = new javax.swing.JButton();
        btnDesactivar = new javax.swing.JButton();
        lblTotalRegistardos = new javax.swing.JLabel();
        cbxFiltrado = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        lblId = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        lblNombre = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        lblDescripcion = new javax.swing.JLabel();
        txtDescripcion = new javax.swing.JTextField();
        lblObligatorio = new javax.swing.JLabel();
        btnGuardar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();

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

        lblTotalRegistardos.setText("Total Regustardos: 0");

        cbxFiltrado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Activos", "Inactivos", "Todos" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(scrollTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 1002, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(21, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnActivar)
                        .addGap(18, 18, 18)
                        .addComponent(btnDesactivar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblTotalRegistardos)
                        .addGap(193, 193, 193))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblBuscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnBuscar)
                        .addGap(18, 18, 18)
                        .addComponent(btnNuevo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnEditar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cbxFiltrado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(117, 117, 117))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBuscar)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscar)
                    .addComponent(btnNuevo)
                    .addComponent(btnEditar)
                    .addComponent(cbxFiltrado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(scrollTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 391, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnActivar)
                    .addComponent(btnDesactivar)
                    .addComponent(lblTotalRegistardos))
                .addContainerGap(62, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Listado", jPanel1);

        lblId.setText("ID:");

        lblNombre.setText("Nombre (*):");

        lblDescripcion.setText("Descripción:");

        lblObligatorio.setText("(*) Campo Obligatorio");

        btnGuardar.setText("Guardar");

        btnCancelar.setText("Cancelar");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblDescripcion)
                        .addGap(18, 18, 18))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btnGuardar)
                                .addGap(18, 18, 18)
                                .addComponent(btnCancelar))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(lblId)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(lblNombre)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(95, 95, 95)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblObligatorio)
                    .addComponent(txtDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 423, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(40, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDescripcion)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblId)
                        .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblNombre)
                        .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblObligatorio)
                .addGap(76, 76, 76)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuardar)
                    .addComponent(btnCancelar))
                .addContainerGap(252, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Mantenimiento", jPanel2);

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
    private javax.swing.JButton btnActivar;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnDesactivar;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JComboBox<String> cbxFiltrado;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JLabel lblDescripcion;
    private javax.swing.JLabel lblId;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblObligatorio;
    private javax.swing.JLabel lblTotalRegistardos;
    private javax.swing.JScrollPane scrollTabla;
    private javax.swing.JTable tablaListado;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtDescripcion;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtNombre;
    // End of variables declaration//GEN-END:variables
}
