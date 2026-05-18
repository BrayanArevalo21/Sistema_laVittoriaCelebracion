
package presentacion;
import datos.RolDAO;
import datos.UsuarioDAO;
import entidades.Rol;
import entidades.Usuario;
import java.util.List;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
public class frmUsuario extends javax.swing.JInternalFrame {


    
   
    private javax.swing.JTabbedPane tabGeneral;
    
   
    
    // ========== VARIABLES DE CONTROL ==========
    private UsuarioDAO usuarioDAO;
    private RolDAO rolDAO;
    private DefaultTableModel modeloTabla;
    private String accion;
    private int idActual;
    private String nombreActual;
    private String emailActual;
    
    public frmUsuario() {
        initComponentes();
        iniciar();
        setVisible(true);
    }
    
    private void iniciar() {
        usuarioDAO = new UsuarioDAO();
        rolDAO = new RolDAO();
        accion = "guardar";
        
        // Configurar modelo de tabla
        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        modeloTabla.setColumnIdentifiers(new String[]{"ID", "ROL", "NOMBRE", "TELÉFONO", "EMAIL", "ESTADO"});
        tablaListado.setModel(modeloTabla);
        
        // Configurar JComboBoxes
        configurarCombos();
        cargarRoles();
        
        listar("");
        tabGeneral.setEnabledAt(1, false);
        
        setTitle("Usuarios");
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setSize(850, 620);
    }
    
    private void configurarCombos() {
        // Tipos de documento
        cbxTipoDocumento.addItem("CC");
        cbxTipoDocumento.addItem("CE");
        cbxTipoDocumento.addItem("PASAPORTE");
    }
    
    private void cargarRoles() {
        cbxRol.removeAllItems();
        List<Rol> roles = rolDAO.listarTodos();
        for (Rol r : roles) {
            if (r.isActivo()) {
                cbxRol.addItem(r.getId() + " - " + r.getNombre());
            }
        }
    }
    
    // ========== MÉTODOS PRINCIPALES ==========
    
    private void listar(String texto) {
        modeloTabla.setRowCount(0);
        List<Usuario> lista = usuarioDAO.listar(texto);
        
        for (Usuario u : lista) {
            String estado = u.isActivo() ? "Activo" : "Inactivo";
            modeloTabla.addRow(new Object[]{
                u.getId(),
                u.getNombreRol(),
                u.getNombre(),
                u.getTelefono() != null ? u.getTelefono() : "",
                u.getEmail(),
                estado
            });
        }
        
        int total = usuarioDAO.total();
        lblTotalRegistros.setText("Total registros: " + total);
    }
    
    private void nuevo() {
        accion = "guardar";
        limpiar();
        txtClave.setEnabled(true);
        txtConfirmarClave.setEnabled(true);
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
        
        // Cargar datos del usuario
        Usuario usuario = usuarioDAO.buscarPorId(idActual);
        if (usuario != null) {
            // Seleccionar rol
            for (int i = 0; i < cbxRol.getItemCount(); i++) {
                if (cbxRol.getItemAt(i).startsWith(String.valueOf(usuario.getRolId()))) {
                    cbxRol.setSelectedIndex(i);
                    break;
                }
            }
            
            txtNombre.setText(usuario.getNombre());
            
            if (usuario.getTipoDocumento() != null) {
                cbxTipoDocumento.setSelectedItem(usuario.getTipoDocumento());
            }
            
            txtNumDocumento.setText(usuario.getNumDocumento());
            txtTelefono.setText(usuario.getTelefono());
            txtEmail.setText(usuario.getEmail());
            
            nombreActual = usuario.getNombre();
            emailActual = usuario.getEmail();
        }
        
        // En edición, deshabilitar campos de contraseña (opcional)
        txtClave.setEnabled(false);
        txtConfirmarClave.setEnabled(false);
        
        tabGeneral.setEnabledAt(0, false);
        tabGeneral.setEnabledAt(1, true);
        tabGeneral.setSelectedIndex(1);
        txtNombre.requestFocus();
    }
    
    private void guardar() {
        // Validar rol
        if (cbxRol.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un rol", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int rolId = Integer.parseInt(cbxRol.getSelectedItem().toString().split(" - ")[0]);
        String nombre = txtNombre.getText().trim();
        String tipoDocumento = cbxTipoDocumento.getSelectedItem().toString();
        String numDocumento = txtNumDocumento.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String email = txtEmail.getText().trim();
        String clave = new String(txtClave.getPassword());
        String confirmarClave = new String(txtConfirmarClave.getPassword());
        
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
        
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El email es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            txtEmail.requestFocus();
            return;
        }
        
        if (email.length() > 50) {
            JOptionPane.showMessageDialog(this, "El email no puede tener más de 50 caracteres", "Error", JOptionPane.ERROR_MESSAGE);
            txtEmail.requestFocus();
            return;
        }
        
        // Validar formato de email básico
        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(this, "Ingrese un email válido", "Error", JOptionPane.ERROR_MESSAGE);
            txtEmail.requestFocus();
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
        
        if (accion.equals("guardar")) {
            // Validar contraseña
            if (clave.isEmpty()) {
                JOptionPane.showMessageDialog(this, "La contraseña es obligatoria", "Error", JOptionPane.ERROR_MESSAGE);
                txtClave.requestFocus();
                return;
            }
            
            if (clave.length() < 4) {
                JOptionPane.showMessageDialog(this, "La contraseña debe tener al menos 4 caracteres", "Error", JOptionPane.ERROR_MESSAGE);
                txtClave.requestFocus();
                return;
            }
            
            if (!clave.equals(confirmarClave)) {
                JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden", "Error", JOptionPane.ERROR_MESSAGE);
                txtClave.requestFocus();
                return;
            }
            
            // Validar que no exista por email
            if (usuarioDAO.existePorEmail(email)) {
                JOptionPane.showMessageDialog(this, "Ya existe un usuario con ese email", "Error", JOptionPane.ERROR_MESSAGE);
                txtEmail.requestFocus();
                return;
            }
            
            // Validar que no exista por nombre
            if (usuarioDAO.existe(nombre)) {
                JOptionPane.showMessageDialog(this, "Ya existe un usuario con ese nombre", "Error", JOptionPane.ERROR_MESSAGE);
                txtNombre.requestFocus();
                return;
            }
            
            Usuario nuevo = new Usuario();
            nuevo.setRolId(rolId);
            nuevo.setNombre(nombre);
            nuevo.setTipoDocumento(tipoDocumento);
            nuevo.setNumDocumento(numDocumento);
            nuevo.setTelefono(telefono);
            nuevo.setEmail(email);
            nuevo.setClave(clave);
            nuevo.setActivo(true);
            
            if (usuarioDAO.insertar(nuevo)) {
                JOptionPane.showMessageDialog(this, "Usuario guardado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                listar("");
                cancelar();
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar el usuario", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Editar
            if (!email.equals(emailActual) && usuarioDAO.existePorEmail(email)) {
                JOptionPane.showMessageDialog(this, "Ya existe un usuario con ese email", "Error", JOptionPane.ERROR_MESSAGE);
                txtEmail.requestFocus();
                return;
            }
            
            if (!nombre.equals(nombreActual) && usuarioDAO.existe(nombre)) {
                JOptionPane.showMessageDialog(this, "Ya existe un usuario con ese nombre", "Error", JOptionPane.ERROR_MESSAGE);
                txtNombre.requestFocus();
                return;
            }
            
            Usuario editar = new Usuario();
            editar.setId(idActual);
            editar.setRolId(rolId);
            editar.setNombre(nombre);
            editar.setTipoDocumento(tipoDocumento);
            editar.setNumDocumento(numDocumento);
            editar.setTelefono(telefono);
            editar.setEmail(email);
            editar.setActivo(true);
            
            // Si se ingresó una nueva contraseña, actualizarla
            if (!clave.isEmpty()) {
                if (clave.length() < 4) {
                    JOptionPane.showMessageDialog(this, "La contraseña debe tener al menos 4 caracteres", "Error", JOptionPane.ERROR_MESSAGE);
                    txtClave.requestFocus();
                    return;
                }
                if (!clave.equals(confirmarClave)) {
                    JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                editar.setClave(clave);
            }
            
            if (usuarioDAO.actualizar(editar)) {
                JOptionPane.showMessageDialog(this, "Usuario actualizado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                listar("");
                cancelar();
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar el usuario", "Error", JOptionPane.ERROR_MESSAGE);
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
        String nombre = tablaListado.getValueAt(fila, 2).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Desea activar el usuario: " + nombre + "?", 
            "Confirmar", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (usuarioDAO.activar(idActual)) {
                JOptionPane.showMessageDialog(this, "Usuario activado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                listar("");
            } else {
                JOptionPane.showMessageDialog(this, "Error al activar el usuario", "Error", JOptionPane.ERROR_MESSAGE);
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
        String nombre = tablaListado.getValueAt(fila, 2).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Desea desactivar el usuario: " + nombre + "?", 
            "Confirmar", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (usuarioDAO.desactivar(idActual)) {
                JOptionPane.showMessageDialog(this, "Usuario desactivado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                listar("");
            } else {
                JOptionPane.showMessageDialog(this, "Error al desactivar el usuario", "Error", JOptionPane.ERROR_MESSAGE);
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
        cbxRol.setSelectedIndex(0);
        txtNombre.setText("");
        cbxTipoDocumento.setSelectedIndex(0);
        txtNumDocumento.setText("");
        txtTelefono.setText("");
        txtEmail.setText("");
        txtClave.setText("");
        txtConfirmarClave.setText("");
        txtClave.setEnabled(true);
        txtConfirmarClave.setEnabled(true);
        idActual = 0;
        nombreActual = "";
        emailActual = "";
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
        
        lblRol = new javax.swing.JLabel();
        lblRol.setText("Rol (*):");
        lblRol.setBounds(30, 70, 100, 25);
        panelMantenimiento.add(lblRol);
        
        cbxRol = new javax.swing.JComboBox<>();
        cbxRol.setBounds(140, 70, 200, 25);
        panelMantenimiento.add(cbxRol);
        
        lblNombre = new javax.swing.JLabel();
        lblNombre.setText("Nombre (*):");
        lblNombre.setBounds(30, 110, 100, 25);
        panelMantenimiento.add(lblNombre);
        
        txtNombre = new javax.swing.JTextField();
        txtNombre.setBounds(140, 110, 250, 25);
        panelMantenimiento.add(txtNombre);
        
        lblTipoDocumento = new javax.swing.JLabel();
        lblTipoDocumento.setText("Tipo Documento:");
        lblTipoDocumento.setBounds(30, 150, 120, 25);
        panelMantenimiento.add(lblTipoDocumento);
        
        cbxTipoDocumento = new javax.swing.JComboBox<>();
        cbxTipoDocumento.setBounds(160, 150, 120, 25);
        panelMantenimiento.add(cbxTipoDocumento);
        
        lblNumDocumento = new javax.swing.JLabel();
        lblNumDocumento.setText("N° Documento:");
        lblNumDocumento.setBounds(30, 190, 120, 25);
        panelMantenimiento.add(lblNumDocumento);
        
        txtNumDocumento = new javax.swing.JTextField();
        txtNumDocumento.setBounds(160, 190, 200, 25);
        panelMantenimiento.add(txtNumDocumento);
        
        lblTelefono = new javax.swing.JLabel();
        lblTelefono.setText("Teléfono:");
        lblTelefono.setBounds(30, 230, 100, 25);
        panelMantenimiento.add(lblTelefono);
        
        txtTelefono = new javax.swing.JTextField();
        txtTelefono.setBounds(140, 230, 150, 25);
        panelMantenimiento.add(txtTelefono);
        
        lblEmail = new javax.swing.JLabel();
        lblEmail.setText("Email (*):");
        lblEmail.setBounds(30, 270, 100, 25);
        panelMantenimiento.add(lblEmail);
        
        txtEmail = new javax.swing.JTextField();
        txtEmail.setBounds(140, 270, 250, 25);
        panelMantenimiento.add(txtEmail);
        
        lblClave = new javax.swing.JLabel();
        lblClave.setText("Contraseña (*):");
        lblClave.setBounds(30, 310, 120, 25);
        panelMantenimiento.add(lblClave);
        
        txtClave = new javax.swing.JPasswordField();
        txtClave.setBounds(160, 310, 200, 25);
        panelMantenimiento.add(txtClave);
        
        lblConfirmarClave = new javax.swing.JLabel();
        lblConfirmarClave.setText("Confirmar Clave:");
        lblConfirmarClave.setBounds(30, 350, 120, 25);
        panelMantenimiento.add(lblConfirmarClave);
        
        txtConfirmarClave = new javax.swing.JPasswordField();
        txtConfirmarClave.setBounds(160, 350, 200, 25);
        panelMantenimiento.add(txtConfirmarClave);
        
        lblObligatorio = new javax.swing.JLabel();
        lblObligatorio.setText("(*) Campo obligatorio");
        lblObligatorio.setFont(new java.awt.Font("Arial", java.awt.Font.ITALIC, 11));
        lblObligatorio.setBounds(140, 400, 200, 20);
        panelMantenimiento.add(lblObligatorio);
        
        btnGuardar = new javax.swing.JButton();
        btnGuardar.setText("Guardar");
        btnGuardar.setBounds(140, 440, 100, 35);
        btnGuardar.addActionListener(e -> guardar());
        panelMantenimiento.add(btnGuardar);
        
        btnCancelar = new javax.swing.JButton();
        btnCancelar.setText("Cancelar");
        btnCancelar.setBounds(260, 440, 100, 35);
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
        Mantenimiento = new javax.swing.JPanel();
        lblId = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        lblRol = new javax.swing.JLabel();
        cbxRol = new javax.swing.JComboBox<>();
        lblNombre = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        lblTipoDocumento = new javax.swing.JLabel();
        cbxTipoDocumento = new javax.swing.JComboBox<>();
        lblNumDocumento = new javax.swing.JLabel();
        txtNumDocumento = new javax.swing.JTextField();
        lblTelefono = new javax.swing.JLabel();
        txtTelefono = new javax.swing.JTextField();
        lblClave = new javax.swing.JLabel();
        txtClave = new javax.swing.JPasswordField();
        lblConfirmarClave = new javax.swing.JLabel();
        txtConfirmarClave = new javax.swing.JPasswordField();
        lblObligatorio = new javax.swing.JLabel();
        btnGuardar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        lblEmail = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();

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
                .addGap(14, 14, 14)
                .addGroup(ListadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ListadoLayout.createSequentialGroup()
                        .addGroup(ListadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(scrollTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 1007, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(ListadoLayout.createSequentialGroup()
                                .addComponent(lblBuscar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnBuscar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnNuevo)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnEditar)))
                        .addContainerGap(29, Short.MAX_VALUE))
                    .addGroup(ListadoLayout.createSequentialGroup()
                        .addComponent(btnActivar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDesactivar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblTotalRegistros)
                        .addGap(115, 115, 115))))
        );
        ListadoLayout.setVerticalGroup(
            ListadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ListadoLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(ListadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBuscar)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscar)
                    .addComponent(btnNuevo)
                    .addComponent(btnEditar))
                .addGap(18, 18, 18)
                .addComponent(scrollTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 454, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(ListadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnActivar)
                    .addComponent(btnDesactivar)
                    .addComponent(lblTotalRegistros))
                .addContainerGap(52, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Listado", Listado);

        lblId.setText("ID:");

        lblRol.setText("Rol (*):");

        lblNombre.setText("Nombre (*):");

        lblTipoDocumento.setText("Tpo de Documento:");

        cbxTipoDocumento.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "CC", "CE", "PASAPORTE" }));

        lblNumDocumento.setText("N° Documento");

        lblTelefono.setText("Teléfono:");

        lblClave.setText("Clave:");

        lblConfirmarClave.setText("Confirmar Clave:");

        lblObligatorio.setText("(*) Campo Obligatorio");

        btnGuardar.setText("Guardar");

        btnCancelar.setText("Cancelar");

        lblEmail.setText("Email:");

        javax.swing.GroupLayout MantenimientoLayout = new javax.swing.GroupLayout(Mantenimiento);
        Mantenimiento.setLayout(MantenimientoLayout);
        MantenimientoLayout.setHorizontalGroup(
            MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MantenimientoLayout.createSequentialGroup()
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(MantenimientoLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblObligatorio)
                            .addGroup(MantenimientoLayout.createSequentialGroup()
                                .addComponent(lblClave)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtClave, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblConfirmarClave)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtConfirmarClave, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, MantenimientoLayout.createSequentialGroup()
                                    .addComponent(lblEmail)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtEmail))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, MantenimientoLayout.createSequentialGroup()
                                    .addComponent(lblTelefono)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtTelefono))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, MantenimientoLayout.createSequentialGroup()
                                    .addComponent(lblNumDocumento)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtNumDocumento))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, MantenimientoLayout.createSequentialGroup()
                                    .addComponent(lblTipoDocumento)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cbxTipoDocumento, 0, 142, Short.MAX_VALUE))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, MantenimientoLayout.createSequentialGroup()
                                    .addComponent(lblNombre)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtNombre))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, MantenimientoLayout.createSequentialGroup()
                                    .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(lblId)
                                        .addComponent(lblRol))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(txtId)
                                        .addComponent(cbxRol, 0, 211, Short.MAX_VALUE))))))
                    .addGroup(MantenimientoLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnGuardar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancelar)))
                .addContainerGap(476, Short.MAX_VALUE))
        );
        MantenimientoLayout.setVerticalGroup(
            MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MantenimientoLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblId)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblRol)
                    .addComponent(cbxRol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNombre)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEmail)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblClave)
                    .addComponent(txtClave, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblConfirmarClave)
                    .addComponent(txtConfirmarClave, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblObligatorio, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuardar)
                    .addComponent(btnCancelar))
                .addContainerGap(245, Short.MAX_VALUE))
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
    private javax.swing.JComboBox<String> cbxRol;
    private javax.swing.JComboBox<String> cbxTipoDocumento;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JLabel lblClave;
    private javax.swing.JLabel lblConfirmarClave;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblId;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblNumDocumento;
    private javax.swing.JLabel lblObligatorio;
    private javax.swing.JLabel lblRol;
    private javax.swing.JLabel lblTelefono;
    private javax.swing.JLabel lblTipoDocumento;
    private javax.swing.JLabel lblTotalRegistros;
    private javax.swing.JScrollPane scrollTabla;
    private javax.swing.JTable tablaListado;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JPasswordField txtClave;
    private javax.swing.JPasswordField txtConfirmarClave;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtNumDocumento;
    private javax.swing.JTextField txtTelefono;
    // End of variables declaration//GEN-END:variables
}
