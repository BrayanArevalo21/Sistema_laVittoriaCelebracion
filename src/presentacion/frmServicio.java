package presentacion;

import datos.ServicioDAO;
import datos.TipoServicioDAO;
import entidades.Servicio;
import entidades.TipoServicio;
import negocio.ServicioControl;
import java.text.DecimalFormat;
import java.util.List;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class frmServicio extends JInternalFrame {
    

    
    // Variables de control
    private ServicioControl control;
    private ServicioDAO dao;
    private TipoServicioDAO tipoServicioDAO;
    private DefaultTableModel modeloTabla;
    private String accion;
    private int idActual;
    private String nombreActual;
    private String codigoActual;
    private DecimalFormat formatoCOP = new DecimalFormat("#,###");
    
    public frmServicio() {
        initComponents();
        configurarEventos();
        iniciar();
        setVisible(true);
    }
    
    private void iniciar() {
        control = new ServicioControl();
        dao = new ServicioDAO();
        tipoServicioDAO = new TipoServicioDAO();
        accion = "guardar";
        
        // Configurar modelo de tabla
        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        modeloTabla.setColumnIdentifiers(new String[]{"ID", "TIPO SERVICIO", "CÓDIGO", "NOMBRE", "PRECIO", "ESTADO"});
        tablaListado.setModel(modeloTabla);
        
        // Configurar combo filtro
        cbxFiltrado.removeAllItems();
        cbxFiltrado.addItem("Activos");
        cbxFiltrado.addItem("Inactivos");
        cbxFiltrado.addItem("Todos");
        cbxFiltrado.setSelectedIndex(0);
        
        // Cargar tipos de servicio
        cargarTiposServicio();
        
        listar("");
        jTabbedPane1.setEnabledAt(1, false);
        
        setTitle("Servicios");
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setSize(850, 600);
    }
    
    private void cargarTiposServicio() {
        cbxTipoServicio.removeAllItems();
        List<TipoServicio> tipos = tipoServicioDAO.listar("");
        for (TipoServicio ts : tipos) {
            if (ts.isActivo()) {
                cbxTipoServicio.addItem(ts.getId() + " - " + ts.getNombre());
            }
        }
    }
    
    private void configurarEventos() {
        btnBuscar.addActionListener(e -> listar(txtBuscar.getText()));
        btnNuevo.addActionListener(e -> nuevo());
        btnEditar.addActionListener(e -> editar());
        btnGuardar.addActionListener(e -> guardar());
        btnCancelar.addActionListener(e -> cancelar());
        btnActivar.addActionListener(e -> activar());
        btnDesactivar.addActionListener(e -> desactivar());
        cbxFiltrado.addActionListener(e -> listar(txtBuscar.getText()));
        
        tablaListado.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int fila = tablaListado.getSelectedRow();
                if (fila >= 0) {
                    idActual = Integer.parseInt(tablaListado.getValueAt(fila, 0).toString());
                    nombreActual = tablaListado.getValueAt(fila, 3).toString();
                    codigoActual = tablaListado.getValueAt(fila, 2).toString();
                }
            }
        });
        
        // Calcular precio automáticamente al escribir
        txtPrecio.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                // Solo formato, no es necesario hacer nada extra
            }
        });
    }
    
    private void listar(String texto) {
        if (cbxFiltrado.getSelectedItem() == null) return;
        
        String filtroEstado = cbxFiltrado.getSelectedItem().toString();
        modeloTabla.setRowCount(0);
        List<Servicio> lista = dao.listarConFiltro(texto, filtroEstado);
        
        for (Servicio s : lista) {
            String estado = s.isActivo() ? "Activo" : "Inactivo";
            modeloTabla.addRow(new Object[]{
                s.getId(),
                s.getNombreTipoServicio(),
                s.getCodigo(),
                s.getNombre(),
                "$" + formatoCOP.format(s.getPrecioBase()),
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
            JOptionPane.showMessageDialog(this, "Seleccione un registro para editar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        accion = "editar";
        idActual = Integer.parseInt(tablaListado.getValueAt(fila, 0).toString());
        txtId.setText(String.valueOf(idActual));
        
        String nombreTipo = tablaListado.getValueAt(fila, 1).toString();
        for (int i = 0; i < cbxTipoServicio.getItemCount(); i++) {
            if (cbxTipoServicio.getItemAt(i).contains(nombreTipo)) {
                cbxTipoServicio.setSelectedIndex(i);
                break;
            }
        }
        
        txtCodigo.setText(tablaListado.getValueAt(fila, 2).toString());
        txtNombre.setText(tablaListado.getValueAt(fila, 3).toString());
        
        String precioStr = tablaListado.getValueAt(fila, 4).toString();
        precioStr = precioStr.replace("$", "").replace(",", "").trim();
        txtPrecio.setText(precioStr);
        
        nombreActual = tablaListado.getValueAt(fila, 3).toString();
        codigoActual = tablaListado.getValueAt(fila, 2).toString();
        
        Servicio servicio = dao.buscarPorId(idActual);
        if (servicio != null) {
            txtDescripcion.setText(servicio.getDescripcion());
        }
        
        jTabbedPane1.setEnabledAt(0, false);
        jTabbedPane1.setEnabledAt(1, true);
        jTabbedPane1.setSelectedIndex(1);
        txtNombre.requestFocus();
    }
    
    private void guardar() {
        if (cbxTipoServicio.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un tipo de servicio", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int tipoServicioId = Integer.parseInt(cbxTipoServicio.getSelectedItem().toString().split(" - ")[0]);
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String precioStr = txtPrecio.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            txtNombre.requestFocus();
            return;
        }
        
        if (nombre.length() > 100) {
            JOptionPane.showMessageDialog(this, "El nombre no puede tener más de 100 caracteres", "Error", JOptionPane.ERROR_MESSAGE);
            txtNombre.requestFocus();
            return;
        }
        
        if (precioStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El precio es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            txtPrecio.requestFocus();
            return;
        }
        
        double precio;
        try {
            precio = Double.parseDouble(precioStr);
            if (precio <= 0) {
                JOptionPane.showMessageDialog(this, "El precio debe ser mayor a 0", "Error", JOptionPane.ERROR_MESSAGE);
                txtPrecio.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese un precio válido", "Error", JOptionPane.ERROR_MESSAGE);
            txtPrecio.requestFocus();
            return;
        }
        
        if (codigo.length() > 50) {
            JOptionPane.showMessageDialog(this, "El código no puede tener más de 50 caracteres", "Error", JOptionPane.ERROR_MESSAGE);
            txtCodigo.requestFocus();
            return;
        }
        
        if (descripcion.length() > 250) {
            JOptionPane.showMessageDialog(this, "La descripción no puede tener más de 250 caracteres", "Error", JOptionPane.ERROR_MESSAGE);
            txtDescripcion.requestFocus();
            return;
        }
        
        if (accion.equals("guardar")) {
            if (control.insertar(tipoServicioId, codigo, nombre, precio, descripcion).equals("OK")) {
                JOptionPane.showMessageDialog(this, "Servicio guardado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                listar("");
                cancelar();
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar el servicio", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            if (control.actualizar(idActual, tipoServicioId, codigo, nombre, nombreActual, precio, descripcion).equals("OK")) {
                JOptionPane.showMessageDialog(this, "Servicio actualizado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                listar("");
                cancelar();
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar el servicio", "Error", JOptionPane.ERROR_MESSAGE);
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
        String nombre = tablaListado.getValueAt(fila, 3).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this, "¿Activar servicio: " + nombre + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION && control.activar(idActual).equals("OK")) {
            JOptionPane.showMessageDialog(this, "Servicio activado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            listar("");
        }
    }
    
    private void desactivar() {
        int fila = tablaListado.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un registro para desactivar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        idActual = Integer.parseInt(tablaListado.getValueAt(fila, 0).toString());
        String nombre = tablaListado.getValueAt(fila, 3).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this, "¿Desactivar servicio: " + nombre + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION && control.desactivar(idActual).equals("OK")) {
            JOptionPane.showMessageDialog(this, "Servicio desactivado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            listar("");
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
        cbxTipoServicio.setSelectedIndex(0);
        txtCodigo.setText("");
        txtNombre.setText("");
        txtPrecio.setText("");
        txtDescripcion.setText("");
        idActual = 0;
        nombreActual = "";
        codigoActual = "";
    }
    

    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
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
        lblTotalRegistardos = new javax.swing.JLabel();
        cbxFiltrado = new javax.swing.JComboBox<>();
        Mantenimiento = new javax.swing.JPanel();
        lblId = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        lblTipoServcio = new javax.swing.JLabel();
        cbxTipoServicio = new javax.swing.JComboBox<>();
        lblCodigo = new javax.swing.JLabel();
        txtCodigo = new javax.swing.JTextField();
        lblNombre = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        lblPrecio = new javax.swing.JLabel();
        txtPrecio = new javax.swing.JTextField();
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

        lblTotalRegistardos.setText("Total Registrados: 0");

        cbxFiltrado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Activos", "Inactivos", "todos" }));

        javax.swing.GroupLayout ListadoLayout = new javax.swing.GroupLayout(Listado);
        Listado.setLayout(ListadoLayout);
        ListadoLayout.setHorizontalGroup(
            ListadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ListadoLayout.createSequentialGroup()
                .addGroup(ListadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ListadoLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblBuscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBuscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnNuevo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEditar)
                        .addGap(189, 189, 189)
                        .addComponent(cbxFiltrado, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(ListadoLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(ListadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(scrollTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 1022, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(ListadoLayout.createSequentialGroup()
                                .addComponent(btnActivar)
                                .addGap(18, 18, 18)
                                .addComponent(btnDesactivar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblTotalRegistardos)
                                .addGap(106, 106, 106)))))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        ListadoLayout.setVerticalGroup(
            ListadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ListadoLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(ListadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBuscar)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscar)
                    .addComponent(btnNuevo)
                    .addComponent(btnEditar)
                    .addComponent(cbxFiltrado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(scrollTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 443, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(ListadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnActivar)
                    .addComponent(btnDesactivar)
                    .addComponent(lblTotalRegistardos))
                .addContainerGap(71, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Listado", Listado);

        lblId.setText("ID:");

        lblTipoServcio.setText("Tipo Servicio:");

        lblCodigo.setText("Codigo:");

        lblNombre.setText("Nombre (*):");

        lblPrecio.setText("Precio");

        lblDescripcion.setText("Descripción:");

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
                    .addGroup(MantenimientoLayout.createSequentialGroup()
                        .addComponent(lblPrecio)
                        .addGap(18, 18, 18)
                        .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(MantenimientoLayout.createSequentialGroup()
                        .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, MantenimientoLayout.createSequentialGroup()
                                .addComponent(lblDescripcion)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtDescripcion))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, MantenimientoLayout.createSequentialGroup()
                                .addComponent(lblId)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(lblTipoServcio)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbxTipoServicio, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addComponent(lblCodigo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblNombre)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblObligatorio)
                    .addGroup(MantenimientoLayout.createSequentialGroup()
                        .addComponent(btnGuardar)
                        .addGap(18, 18, 18)
                        .addComponent(btnCancelar)))
                .addContainerGap(176, Short.MAX_VALUE))
        );
        MantenimientoLayout.setVerticalGroup(
            MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MantenimientoLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblId)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTipoServcio)
                    .addComponent(cbxTipoServicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCodigo)
                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNombre)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPrecio)
                    .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(42, 42, 42)
                .addComponent(lblObligatorio)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDescripcion)
                    .addComponent(txtDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuardar)
                    .addComponent(btnCancelar))
                .addContainerGap(237, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Mantenimiento", Mantenimiento);

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
    private javax.swing.JComboBox<String> cbxFiltrado;
    private javax.swing.JComboBox<String> cbxTipoServicio;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JLabel lblCodigo;
    private javax.swing.JLabel lblDescripcion;
    private javax.swing.JLabel lblId;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblObligatorio;
    private javax.swing.JLabel lblPrecio;
    private javax.swing.JLabel lblTipoServcio;
    private javax.swing.JLabel lblTotalRegistardos;
    private javax.swing.JScrollPane scrollTabla;
    private javax.swing.JTable tablaListado;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextField txtDescripcion;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtPrecio;
    // End of variables declaration//GEN-END:variables
}
