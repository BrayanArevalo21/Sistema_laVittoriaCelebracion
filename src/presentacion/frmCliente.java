
package presentacion;
import datos.ClienteDAO;
import entidades.Cliente;
import java.util.List;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
public class frmCliente extends javax.swing.JInternalFrame {





    
    // ========== COMPONENTES ==========
    // Pestañas
    private javax.swing.JTabbedPane tabGeneral;
    
    // Pestaña Listado
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnActivar;
    private javax.swing.JButton btnDesactivar;
    private javax.swing.JScrollPane scrollTabla;
    private javax.swing.JTable tablaListado;
    private javax.swing.JLabel lblTotalRegistros;
    
    // Pestaña Mantenimiento
    private javax.swing.JButton btnGuardar;
    
    // ========== VARIABLES DE CONTROL ==========
    private ClienteDAO dao;
    private DefaultTableModel modeloTabla;
    private String accion;
    private int idActual;
    private String nombreActual;
    private String numDocumentoActual;
    
    public frmCliente() {
        initComponentes();
        iniciar();
        setVisible(true);
    }
    
    private void iniciar() {
        dao = new ClienteDAO();
        accion = "guardar";
        
        // Configurar modelo de tabla
        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        modeloTabla.setColumnIdentifiers(new String[]{"ID", "NOMBRE", "TIPO DOC", "N° DOC", "TELÉFONO", "EMAIL", "ESTADO"});
        tablaListado.setModel(modeloTabla);
        
        // Configurar JComboBox
        cbxTipoDocumento.addItem("CC");
        cbxTipoDocumento.addItem("CE");
        cbxTipoDocumento.addItem("NIT");
        cbxTipoDocumento.addItem("PASAPORTE");
        cbxTipoDocumento.addItem("TI");
        
        listar("");
        tabGeneral.setEnabledAt(1, false);
        
        setTitle("Clientes");
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setSize(850, 550);
    }
    
    // ========== MÉTODOS PRINCIPALES ==========
    
    private void listar(String texto) {
        modeloTabla.setRowCount(0);
        List<Cliente> lista = dao.listar(texto);
        
        for (Cliente c : lista) {
            String estado = c.isActivo() ? "Activo" : "Inactivo";
            modeloTabla.addRow(new Object[]{
                c.getId(),
                c.getNombre(),
                c.getTipoDocumento(),
                c.getNumDocumento(),
                c.getTelefono(),
                c.getEmail(),
                estado
            });
        }
        
        int total = dao.total();
        lblTotalRegistros.setText("Total registros: " + total);
    }
    
    private void nuevo() {
        accion = "guardar";
        limpiar();
        tabGeneral.setEnabledAt(0, false);
        tabGeneral.setEnabledAt(1, true);
        tabGeneral.setSelectedIndex(1);
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
        cbxTipoDocumento.setSelectedItem(tablaListado.getValueAt(fila, 2).toString());
        txtNumDocumento.setText(tablaListado.getValueAt(fila, 3).toString());
        txtTelefono.setText(tablaListado.getValueAt(fila, 4).toString());
        txtEmail.setText(tablaListado.getValueAt(fila, 5).toString());
        
        nombreActual = tablaListado.getValueAt(fila, 1).toString();
        numDocumentoActual = tablaListado.getValueAt(fila, 3).toString();
        
        tabGeneral.setEnabledAt(0, false);
        tabGeneral.setEnabledAt(1, true);
        tabGeneral.setSelectedIndex(1);
        txtNombre.requestFocus();
    }
    
    private void guardar() {
        String nombre = txtNombre.getText().trim();
        String tipoDocumento = cbxTipoDocumento.getSelectedItem().toString();
        String numDocumento = txtNumDocumento.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String email = txtEmail.getText().trim();
        String direccion = txtDireccion.getText().trim();
        
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
        
        if (numDocumento.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El número de documento es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
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
        
        if (accion.equals("guardar")) {
            // Validar que no exista por documento
            if (dao.existePorDocumento(numDocumento)) {
                JOptionPane.showMessageDialog(this, "Ya existe un cliente con ese número de documento", "Error", JOptionPane.ERROR_MESSAGE);
                txtNumDocumento.requestFocus();
                return;
            }
            
            // Validar que no exista por nombre
            if (dao.existe(nombre)) {
                JOptionPane.showMessageDialog(this, "Ya existe un cliente con ese nombre", "Error", JOptionPane.ERROR_MESSAGE);
                txtNombre.requestFocus();
                return;
            }
            
            Cliente nuevo = new Cliente();
            nuevo.setTipoPersona("CLIENTE");
            nuevo.setNombre(nombre);
            nuevo.setTipoDocumento(tipoDocumento);
            nuevo.setNumDocumento(numDocumento);
            nuevo.setTelefono(telefono);
            nuevo.setEmail(email);
            nuevo.setDireccion(direccion);
            nuevo.setActivo(true);
            
            if (dao.insertar(nuevo)) {
                JOptionPane.showMessageDialog(this, "Cliente guardado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                listar("");
                cancelar();
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar el cliente", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Editar
            if (!numDocumento.equals(numDocumentoActual) && dao.existePorDocumento(numDocumento)) {
                JOptionPane.showMessageDialog(this, "Ya existe un cliente con ese número de documento", "Error", JOptionPane.ERROR_MESSAGE);
                txtNumDocumento.requestFocus();
                return;
            }
            
            if (!nombre.equals(nombreActual) && dao.existe(nombre)) {
                JOptionPane.showMessageDialog(this, "Ya existe un cliente con ese nombre", "Error", JOptionPane.ERROR_MESSAGE);
                txtNombre.requestFocus();
                return;
            }
            
            Cliente editar = new Cliente();
            editar.setId(idActual);
            editar.setTipoPersona("CLIENTE");
            editar.setNombre(nombre);
            editar.setTipoDocumento(tipoDocumento);
            editar.setNumDocumento(numDocumento);
            editar.setTelefono(telefono);
            editar.setEmail(email);
            editar.setDireccion(direccion);
            editar.setActivo(true);
            
            if (dao.actualizar(editar)) {
                JOptionPane.showMessageDialog(this, "Cliente actualizado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                listar("");
                cancelar();
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar el cliente", "Error", JOptionPane.ERROR_MESSAGE);
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
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Desea activar el cliente: " + nombre + "?", 
            "Confirmar", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.activar(idActual)) {
                JOptionPane.showMessageDialog(this, "Cliente activado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                listar("");
            } else {
                JOptionPane.showMessageDialog(this, "Error al activar el cliente", "Error", JOptionPane.ERROR_MESSAGE);
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
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Desea desactivar el cliente: " + nombre + "?", 
            "Confirmar", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.desactivar(idActual)) {
                JOptionPane.showMessageDialog(this, "Cliente desactivado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                listar("");
            } else {
                JOptionPane.showMessageDialog(this, "Error al desactivar el cliente", "Error", JOptionPane.ERROR_MESSAGE);
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
        txtNombre.setText("");
        cbxTipoDocumento.setSelectedIndex(0);
        txtNumDocumento.setText("");
        txtTelefono.setText("");
        txtEmail.setText("");
        txtDireccion.setText("");
        idActual = 0;
        nombreActual = "";
        numDocumentoActual = "";
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
        scrollTabla.setBounds(20, 60, 790, 350);
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
        
        lblNombre = new javax.swing.JLabel();
        lblNombre.setText("Nombre (*):");
        lblNombre.setBounds(30, 70, 100, 25);
        panelMantenimiento.add(lblNombre);
        
        txtNombre = new javax.swing.JTextField();
        txtNombre.setBounds(140, 70, 250, 25);
        panelMantenimiento.add(txtNombre);
        
        lblTipoDocumento = new javax.swing.JLabel();
        lblTipoDocumento.setText("Tipo Documento:");
        lblTipoDocumento.setBounds(30, 110, 120, 25);
        panelMantenimiento.add(lblTipoDocumento);
        
        cbxTipoDocumento = new javax.swing.JComboBox<>();
        cbxTipoDocumento.setBounds(160, 110, 120, 25);
        panelMantenimiento.add(cbxTipoDocumento);
        
        lblNumDocumento = new javax.swing.JLabel();
        lblNumDocumento.setText("N° Documento:");
        lblNumDocumento.setBounds(30, 150, 120, 25);
        panelMantenimiento.add(lblNumDocumento);
        
        txtNumDocumento = new javax.swing.JTextField();
        txtNumDocumento.setBounds(160, 150, 200, 25);
        panelMantenimiento.add(txtNumDocumento);
        
        lblTelefono = new javax.swing.JLabel();
        lblTelefono.setText("Teléfono:");
        lblTelefono.setBounds(30, 190, 100, 25);
        panelMantenimiento.add(lblTelefono);
        
        txtTelefono = new javax.swing.JTextField();
        txtTelefono.setBounds(140, 190, 150, 25);
        panelMantenimiento.add(txtTelefono);
        
        lblEmail = new javax.swing.JLabel();
        lblEmail.setText("Email:");
        lblEmail.setBounds(30, 230, 100, 25);
        panelMantenimiento.add(lblEmail);
        
        txtEmail = new javax.swing.JTextField();
        txtEmail.setBounds(140, 230, 250, 25);
        panelMantenimiento.add(txtEmail);
        
        lblDireccion = new javax.swing.JLabel();
        lblDireccion.setText("Dirección:");
        lblDireccion.setBounds(30, 270, 100, 25);
        panelMantenimiento.add(lblDireccion);
        
        txtDireccion = new javax.swing.JTextField();
        txtDireccion.setBounds(140, 270, 400, 25);
        panelMantenimiento.add(txtDireccion);
        
        lblObligatorio = new javax.swing.JLabel();
        lblObligatorio.setText("(*) Campo obligatorio");
        lblObligatorio.setFont(new java.awt.Font("Arial", java.awt.Font.ITALIC, 11));
        lblObligatorio.setBounds(140, 310, 200, 20);
        panelMantenimiento.add(lblObligatorio);
        
        btnGuardar = new javax.swing.JButton();
        btnGuardar.setText("Guardar");
        btnGuardar.setBounds(140, 350, 100, 35);
        btnGuardar.addActionListener(e -> guardar());
        panelMantenimiento.add(btnGuardar);
        
        btnCancelar = new javax.swing.JButton();
        btnCancelar.setText("Cancelar");
        btnCancelar.setBounds(260, 350, 100, 35);
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

        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
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
        lblObligatorio = new javax.swing.JLabel();
        btnGuargar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        lblBuscar1 = new javax.swing.JLabel();
        txtBuscar1 = new javax.swing.JTextField();
        btnBuscar1 = new javax.swing.JButton();
        btnNuevo1 = new javax.swing.JButton();
        btnEditar1 = new javax.swing.JButton();
        scrollTabla1 = new javax.swing.JScrollPane();
        tablaListado1 = new javax.swing.JTable();
        btnActivar1 = new javax.swing.JButton();
        btnDesactivar1 = new javax.swing.JButton();
        lblTotalRegistros1 = new javax.swing.JLabel();

        lblId.setText("ID:");

        lblNombre.setText("Nombre:");

        lblTipoDocumento.setText("Tipo de Documento:");

        cbxTipoDocumento.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "CC", "CE", "NIT", "PASAPORTE" }));

        lblNumDocumento.setText("N° Documento:");

        lblTelefono.setText("Teléfono:");

        lblEmail.setText("Email:");

        lblDireccion.setText("Dirección:");

        lblObligatorio.setText("Obligatorio:");

        btnGuargar.setText("Guardar");

        btnCancelar.setText("Cancelar");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(btnGuargar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnCancelar))
                    .addComponent(lblObligatorio)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(lblDireccion)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtDireccion))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(lblTelefono)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtTelefono))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(lblId)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(lblNombre)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(lblEmail)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtEmail)))
                        .addGap(18, 18, 18)
                        .addComponent(lblTipoDocumento)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbxTipoDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblNumDocumento)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNumDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(90, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(lblObligatorio)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblId)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNombre)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTipoDocumento)
                    .addComponent(cbxTipoDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNumDocumento)
                    .addComponent(txtNumDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTelefono)
                    .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblEmail)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDireccion)
                    .addComponent(txtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(41, 41, 41)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuargar)
                    .addComponent(btnCancelar))
                .addContainerGap(397, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Mantenimiento", jPanel3);

        lblBuscar1.setText("Buscar:");

        btnBuscar1.setText("Buscar");

        btnNuevo1.setText("Nuevo");

        btnEditar1.setText("Editar");

        tablaListado1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        scrollTabla1.setViewportView(tablaListado1);

        btnActivar1.setText("Activar");

        btnDesactivar1.setText("Desactivar");

        lblTotalRegistros1.setText("Total Registrado: 0");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(lblBuscar1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBuscar1, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnBuscar1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnNuevo1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnEditar1))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(scrollTabla1, javax.swing.GroupLayout.PREFERRED_SIZE, 1020, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(16, Short.MAX_VALUE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnActivar1)
                .addGap(18, 18, 18)
                .addComponent(btnDesactivar1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblTotalRegistros1)
                .addGap(194, 194, 194))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBuscar1)
                    .addComponent(txtBuscar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscar1)
                    .addComponent(btnNuevo1)
                    .addComponent(btnEditar1))
                .addGap(18, 18, 18)
                .addComponent(scrollTabla1, javax.swing.GroupLayout.PREFERRED_SIZE, 457, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnActivar1)
                    .addComponent(btnDesactivar1)
                    .addComponent(lblTotalRegistros1))
                .addContainerGap(33, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Listado", jPanel4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane2)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane2)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActivar1;
    private javax.swing.JButton btnBuscar1;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnDesactivar1;
    private javax.swing.JButton btnEditar1;
    private javax.swing.JButton btnGuargar;
    private javax.swing.JButton btnNuevo1;
    private javax.swing.JComboBox<String> cbxTipoDocumento;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JLabel lblBuscar1;
    private javax.swing.JLabel lblDireccion;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblId;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblNumDocumento;
    private javax.swing.JLabel lblObligatorio;
    private javax.swing.JLabel lblTelefono;
    private javax.swing.JLabel lblTipoDocumento;
    private javax.swing.JLabel lblTotalRegistros1;
    private javax.swing.JScrollPane scrollTabla1;
    private javax.swing.JTable tablaListado1;
    private javax.swing.JTextField txtBuscar1;
    private javax.swing.JTextField txtDireccion;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtNumDocumento;
    private javax.swing.JTextField txtTelefono;
    // End of variables declaration//GEN-END:variables
}
