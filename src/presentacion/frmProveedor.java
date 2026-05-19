package presentacion;

import datos.ProveedorDAO;
import entidades.Proveedor;
import java.util.List;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class frmProveedor extends JInternalFrame {
    
  
   
    
    // Variables de control
    private ProveedorDAO dao;
    private DefaultTableModel modeloTabla;
    private String accion;
    private int idActual;
    private String nombreActual;
    private String numDocumentoActual;
    
    public frmProveedor() {
        initComponents();
        configurarEventos();
        iniciar();
        setVisible(true);
    }
    
    private void iniciar() {
        dao = new ProveedorDAO();
        accion = "guardar";
        
        // Configurar modelo de tabla
        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        modeloTabla.setColumnIdentifiers(new String[]{"ID", "NOMBRE", "TIPO DOC", "N° DOC", "TELÉFONO", "CONTACTO", "ESTADO"});
        tablaListado.setModel(modeloTabla);
        
        // Configurar JComboBox de tipos de documento
        cbxTipoDocumento.addItem("CC");
        cbxTipoDocumento.addItem("CE");
        cbxTipoDocumento.addItem("NIT");
        cbxTipoDocumento.addItem("PASAPORTE");
        
        // Configurar combo filtro
        cbxFiltrado.removeAllItems();
        cbxFiltrado.addItem("Activos");
        cbxFiltrado.addItem("Inactivos");
        cbxFiltrado.addItem("Todos");
        cbxFiltrado.setSelectedIndex(0);
        
        listar("");
        jTabbedPane1.setEnabledAt(1, false);
        
        setTitle("Proveedores");
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setSize(850, 600);
    }
    
    private void configurarEventos() {
        btnBuscar.addActionListener(e -> listar(txtBuscar.getText()));
        btnNuevo.addActionListener(e -> nuevo());
        btnEditar.addActionListener(e -> editar());
        btnGuardar.addActionListener(e -> guardar());
        btnCancelar.addActionListener(e -> cancelar());
        btnActivar.addActionListener(e -> activar());
        btnDesactivar.addActionListener(e -> desactivar());
        cbxFiltrado.addActionListener(e -> listar(txtBuscar.getText()));  // ← AGREGADO
        
        tablaListado.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int fila = tablaListado.getSelectedRow();
                if (fila >= 0) {
                    idActual = Integer.parseInt(tablaListado.getValueAt(fila, 0).toString());
                    nombreActual = tablaListado.getValueAt(fila, 1).toString();
                    numDocumentoActual = tablaListado.getValueAt(fila, 3).toString();
                }
            }
        });
    }
    
    private void listar(String texto) {
        if (cbxFiltrado.getSelectedItem() == null) return;  // ← AGREGADO
        
        modeloTabla.setRowCount(0);
        String filtroEstado = cbxFiltrado.getSelectedItem().toString();  // ← AGREGADO
        List<Proveedor> lista = dao.listarConFiltro(texto, filtroEstado);  // ← MODIFICADO
        
        for (Proveedor p : lista) {
            String estado = p.isActivo() ? "Activo" : "Inactivo";
            modeloTabla.addRow(new Object[]{
                p.getId(),
                p.getNombre(),
                p.getTipoDocumento() != null ? p.getTipoDocumento() : "",
                p.getNumDocumento() != null ? p.getNumDocumento() : "",
                p.getTelefono() != null ? p.getTelefono() : "",
                p.getContacto() != null ? p.getContacto() : "",
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
        txtNombre.setText(tablaListado.getValueAt(fila, 1).toString());
        
        String tipoDoc = tablaListado.getValueAt(fila, 2).toString();
        if (!tipoDoc.isEmpty()) {
            cbxTipoDocumento.setSelectedItem(tipoDoc);
        }
        
        txtNumDocumento.setText(tablaListado.getValueAt(fila, 3).toString());
        txtTelefono.setText(tablaListado.getValueAt(fila, 4).toString());
        txtContacto.setText(tablaListado.getValueAt(fila, 5).toString());
        
        nombreActual = tablaListado.getValueAt(fila, 1).toString();
        numDocumentoActual = tablaListado.getValueAt(fila, 3).toString();
        
        Proveedor proveedor = dao.buscarPorId(idActual);
        if (proveedor != null) {
            txtEmail.setText(proveedor.getEmail() != null ? proveedor.getEmail() : "");
            txtDireccion.setText(proveedor.getDireccion() != null ? proveedor.getDireccion() : "");
        }
        
        jTabbedPane1.setEnabledAt(0, false);
        jTabbedPane1.setEnabledAt(1, true);
        jTabbedPane1.setSelectedIndex(1);
        txtNombre.requestFocus();
    }
    
    private void guardar() {
        String nombre = txtNombre.getText().trim();
        String tipoDocumento = cbxTipoDocumento.getSelectedItem().toString();
        String numDocumento = txtNumDocumento.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String email = txtEmail.getText().trim();
        String direccion = txtDireccion.getText().trim();
        String contacto = txtContacto.getText().trim();
        
        // Validaciones
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            txtNombre.requestFocus();
            return;
        }
        
        if (nombre.length() > 70) {
            JOptionPane.showMessageDialog(this, "El nombre no puede tener más de 70 caracteres", "Error", JOptionPane.ERROR_MESSAGE);
            txtNombre.requestFocus();
            return;
        }
        
        if (numDocumento.length() > 20) {
            JOptionPane.showMessageDialog(this, "El número de documento no puede tener más de 20 caracteres", "Error", JOptionPane.ERROR_MESSAGE);
            txtNumDocumento.requestFocus();
            return;
        }
        
        if (telefono.length() > 15) {
            JOptionPane.showMessageDialog(this, "El teléfono no puede tener más de 15 caracteres", "Error", JOptionPane.ERROR_MESSAGE);
            txtTelefono.requestFocus();
            return;
        }
        
        if (email.length() > 50) {
            JOptionPane.showMessageDialog(this, "El email no puede tener más de 50 caracteres", "Error", JOptionPane.ERROR_MESSAGE);
            txtEmail.requestFocus();
            return;
        }
        
        if (direccion.length() > 70) {
            JOptionPane.showMessageDialog(this, "La dirección no puede tener más de 70 caracteres", "Error", JOptionPane.ERROR_MESSAGE);
            txtDireccion.requestFocus();
            return;
        }
        
        if (contacto.length() > 70) {
            JOptionPane.showMessageDialog(this, "El contacto no puede tener más de 70 caracteres", "Error", JOptionPane.ERROR_MESSAGE);
            txtContacto.requestFocus();
            return;
        }
        
        if (accion.equals("guardar")) {
            if (!numDocumento.isEmpty() && dao.existePorDocumento(numDocumento)) {
                JOptionPane.showMessageDialog(this, "Ya existe un proveedor con ese número de documento", "Error", JOptionPane.ERROR_MESSAGE);
                txtNumDocumento.requestFocus();
                return;
            }
            
            if (dao.existe(nombre)) {
                JOptionPane.showMessageDialog(this, "Ya existe un proveedor con ese nombre", "Error", JOptionPane.ERROR_MESSAGE);
                txtNombre.requestFocus();
                return;
            }
            
            Proveedor nuevo = new Proveedor();
            nuevo.setTipoPersona("PROVEEDOR");
            nuevo.setNombre(nombre);
            nuevo.setTipoDocumento(tipoDocumento);
            nuevo.setNumDocumento(numDocumento);
            nuevo.setTelefono(telefono);
            nuevo.setEmail(email);
            nuevo.setDireccion(direccion);
            nuevo.setContacto(contacto);
            nuevo.setActivo(true);
            
            if (dao.insertar(nuevo)) {
                JOptionPane.showMessageDialog(this, "Proveedor guardado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                listar("");
                cancelar();
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar el proveedor", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            if (!numDocumento.equals(numDocumentoActual) && !numDocumento.isEmpty() && dao.existePorDocumento(numDocumento)) {
                JOptionPane.showMessageDialog(this, "Ya existe un proveedor con ese número de documento", "Error", JOptionPane.ERROR_MESSAGE);
                txtNumDocumento.requestFocus();
                return;
            }
            
            if (!nombre.equals(nombreActual) && dao.existe(nombre)) {
                JOptionPane.showMessageDialog(this, "Ya existe un proveedor con ese nombre", "Error", JOptionPane.ERROR_MESSAGE);
                txtNombre.requestFocus();
                return;
            }
            
            Proveedor editar = new Proveedor();
            editar.setId(idActual);
            editar.setTipoPersona("PROVEEDOR");
            editar.setNombre(nombre);
            editar.setTipoDocumento(tipoDocumento);
            editar.setNumDocumento(numDocumento);
            editar.setTelefono(telefono);
            editar.setEmail(email);
            editar.setDireccion(direccion);
            editar.setContacto(contacto);
            editar.setActivo(true);
            
            if (dao.actualizar(editar)) {
                JOptionPane.showMessageDialog(this, "Proveedor actualizado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                listar("");
                cancelar();
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar el proveedor", "Error", JOptionPane.ERROR_MESSAGE);
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
        
        int confirm = JOptionPane.showConfirmDialog(this, "¿Activar proveedor: " + nombre + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION && dao.activar(idActual)) {
            JOptionPane.showMessageDialog(this, "Proveedor activado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
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
        String nombre = tablaListado.getValueAt(fila, 1).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this, "¿Desactivar proveedor: " + nombre + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION && dao.desactivar(idActual)) {
            JOptionPane.showMessageDialog(this, "Proveedor desactivado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
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
        txtNombre.setText("");
        cbxTipoDocumento.setSelectedIndex(0);
        txtNumDocumento.setText("");
        txtTelefono.setText("");
        txtEmail.setText("");
        txtDireccion.setText("");
        txtContacto.setText("");
        idActual = 0;
        nombreActual = "";
        numDocumentoActual = "";
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
        lblNombre = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        lblTipoDocumento = new javax.swing.JLabel();
        cbxTipoDocumento = new javax.swing.JComboBox<>();
        lblNumDocumento = new javax.swing.JLabel();
        txtNumDocumento = new javax.swing.JTextField();
        lblTelefono = new javax.swing.JLabel();
        txtTelefono = new javax.swing.JTextField();
        lblEmail = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        lblDireccion = new javax.swing.JLabel();
        txtDireccion = new javax.swing.JTextField();
        lblContacto = new javax.swing.JLabel();
        txtContacto = new javax.swing.JTextField();
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

        lblTotalRegistardos.setText("Total Registros: 0");

        cbxFiltrado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Activos", "Inactivos", "Todos" }));

        javax.swing.GroupLayout ListadoLayout = new javax.swing.GroupLayout(Listado);
        Listado.setLayout(ListadoLayout);
        ListadoLayout.setHorizontalGroup(
            ListadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ListadoLayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(ListadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ListadoLayout.createSequentialGroup()
                        .addGroup(ListadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(scrollTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 998, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(ListadoLayout.createSequentialGroup()
                                .addComponent(lblBuscar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 520, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnBuscar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnNuevo)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnEditar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cbxFiltrado, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(27, Short.MAX_VALUE))
                    .addGroup(ListadoLayout.createSequentialGroup()
                        .addComponent(btnActivar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnDesactivar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblTotalRegistardos)
                        .addGap(116, 116, 116))))
        );
        ListadoLayout.setVerticalGroup(
            ListadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ListadoLayout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(ListadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBuscar)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscar)
                    .addComponent(btnNuevo)
                    .addComponent(btnEditar)
                    .addComponent(cbxFiltrado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scrollTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 403, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(ListadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnActivar)
                    .addComponent(btnDesactivar)
                    .addComponent(lblTotalRegistardos))
                .addContainerGap(88, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Listado", Listado);

        lblId.setText("ID:");

        lblNombre.setText("Nombre:");

        lblTipoDocumento.setText("Tipo Documento:");

        cbxTipoDocumento.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "CC", "CE", "NIT", "PASAPORTE" }));

        lblNumDocumento.setText("N° Documento:");

        lblTelefono.setText("Teléfono:");

        lblEmail.setText("Email:");

        lblDireccion.setText("Dirección:");

        lblContacto.setText("Persona Contacto:");

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
                        .addGroup(MantenimientoLayout.createSequentialGroup()
                            .addComponent(lblId)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(MantenimientoLayout.createSequentialGroup()
                            .addComponent(lblNombre)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(MantenimientoLayout.createSequentialGroup()
                            .addComponent(lblTipoDocumento)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cbxTipoDocumento, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(MantenimientoLayout.createSequentialGroup()
                            .addComponent(lblNumDocumento)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtNumDocumento))
                        .addGroup(MantenimientoLayout.createSequentialGroup()
                            .addComponent(lblTelefono)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtTelefono))
                        .addGroup(MantenimientoLayout.createSequentialGroup()
                            .addComponent(lblEmail)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtEmail))
                        .addGroup(MantenimientoLayout.createSequentialGroup()
                            .addComponent(lblDireccion)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtDireccion))
                        .addGroup(MantenimientoLayout.createSequentialGroup()
                            .addComponent(lblContacto)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtContacto)))
                    .addComponent(lblObligatorio)
                    .addGroup(MantenimientoLayout.createSequentialGroup()
                        .addComponent(btnGuardar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancelar)))
                .addContainerGap(811, Short.MAX_VALUE))
        );
        MantenimientoLayout.setVerticalGroup(
            MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MantenimientoLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblId)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNombre)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTipoDocumento)
                    .addComponent(cbxTipoDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNumDocumento)
                    .addComponent(txtNumDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTelefono)
                    .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEmail)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDireccion)
                    .addComponent(txtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblContacto)
                    .addComponent(txtContacto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblObligatorio)
                .addGap(33, 33, 33)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuardar)
                    .addComponent(btnCancelar))
                .addContainerGap(231, Short.MAX_VALUE))
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
    private javax.swing.JComboBox<String> cbxTipoDocumento;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JLabel lblContacto;
    private javax.swing.JLabel lblDireccion;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblId;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblNumDocumento;
    private javax.swing.JLabel lblObligatorio;
    private javax.swing.JLabel lblTelefono;
    private javax.swing.JLabel lblTipoDocumento;
    private javax.swing.JLabel lblTotalRegistardos;
    private javax.swing.JScrollPane scrollTabla;
    private javax.swing.JTable tablaListado;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtContacto;
    private javax.swing.JTextField txtDireccion;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtNumDocumento;
    private javax.swing.JTextField txtTelefono;
    // End of variables declaration//GEN-END:variables
}
