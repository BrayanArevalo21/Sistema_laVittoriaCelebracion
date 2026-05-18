
package presentacion;
import datos.PersonalMontajeDAO;
import entidades.PersonalMontaje;
import java.text.DecimalFormat;
import java.util.List;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
public class frmPersonalMontaje extends javax.swing.JInternalFrame {





    
    // ========== COMPONENTES ==========
    // Pestañas
    private javax.swing.JTabbedPane tabGeneral;
    
    // Pestaña Listado

    
    // ========== VARIABLES DE CONTROL ==========
    private PersonalMontajeDAO dao;
    private DefaultTableModel modeloTabla;
    private String accion;
    private int idActual;
    private String nombreActual;
    private String numDocumentoActual;
    private DecimalFormat formatoCOP = new DecimalFormat("#,###");
    
    public frmPersonalMontaje() {
        initComponentes();
        iniciar();
        setVisible(true);
    }
    
    private void iniciar() {
        dao = new PersonalMontajeDAO();
        accion = "guardar";
        
        // Configurar modelo de tabla
        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        modeloTabla.setColumnIdentifiers(new String[]{"ID", "NOMBRE", "TIPO DOC", "N° DOC", "TELÉFONO", "ESPECIALIDAD", "COSTO/HORA", "DISPONIBLE", "ESTADO"});
        tablaListado.setModel(modeloTabla);
        
        // Configurar JComboBoxes
        configurarCombos();
        
        listar("");
        tabGeneral.setEnabledAt(1, false);
        
        setTitle("Personal de Montaje");
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setSize(950, 700);
    }
    
    private void configurarCombos() {
        // Tipos de documento
        cbxTipoDocumento.addItem("CC");
        cbxTipoDocumento.addItem("CE");
        cbxTipoDocumento.addItem("PASAPORTE");
        
        // Especialidades
        cbxEspecialidad.addItem("Montaje");
        cbxEspecialidad.addItem("Decoración");
        cbxEspecialidad.addItem("Sonido");
        cbxEspecialidad.addItem("Iluminación");
        cbxEspecialidad.addItem("Logística");
        cbxEspecialidad.addItem("Animación");
        cbxEspecialidad.addItem("Seguridad");
    }
    
    // ========== MÉTODOS PRINCIPALES ==========
    
    private void listar(String texto) {
        modeloTabla.setRowCount(0);
        List<PersonalMontaje> lista = dao.listar(texto);
        
        for (PersonalMontaje p : lista) {
            String estado = p.isActivo() ? "Activo" : "Inactivo";
            String disponible = p.isDisponible() ? "Sí" : "No";
            modeloTabla.addRow(new Object[]{
                p.getId(),
                p.getNombre(),
                p.getTipoDocumento() != null ? p.getTipoDocumento() : "",
                p.getNumDocumento() != null ? p.getNumDocumento() : "",
                p.getTelefono() != null ? p.getTelefono() : "",
                p.getEspecialidad(),
                "$" + formatoCOP.format(p.getCostoPorHora()),
                disponible,
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
        
        // Tipo documento
        String tipoDoc = tablaListado.getValueAt(fila, 2).toString();
        if (!tipoDoc.isEmpty()) {
            cbxTipoDocumento.setSelectedItem(tipoDoc);
        }
        
        txtNumDocumento.setText(tablaListado.getValueAt(fila, 3).toString());
        txtTelefono.setText(tablaListado.getValueAt(fila, 4).toString());
        
        // Especialidad
        String especialidad = tablaListado.getValueAt(fila, 5).toString();
        cbxEspecialidad.setSelectedItem(especialidad);
        
        // Costo (quitar $ y comas)
        String costoStr = tablaListado.getValueAt(fila, 6).toString();
        costoStr = costoStr.replace("$", "").replace(",", "").trim();
        txtCostoHora.setText(costoStr);
        
        String disponible = tablaListado.getValueAt(fila, 7).toString();
        chkDisponible.setSelected(disponible.equals("Sí"));
        
        nombreActual = tablaListado.getValueAt(fila, 1).toString();
        numDocumentoActual = tablaListado.getValueAt(fila, 3).toString();
        
        // Cargar observaciones
        PersonalMontaje personal = dao.buscarPorId(idActual);
        if (personal != null && personal.getObservaciones() != null) {
            txtObservaciones.setText(personal.getObservaciones());
        }
        
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
        String especialidad = cbxEspecialidad.getSelectedItem().toString();
        String costoStr = txtCostoHora.getText().trim();
        boolean disponible = chkDisponible.isSelected();
        String observaciones = txtObservaciones.getText().trim();
        
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
        
        if (costoStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El costo por hora es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            txtCostoHora.requestFocus();
            return;
        }
        
        double costoHora;
        try {
            costoHora = Double.parseDouble(costoStr);
            if (costoHora <= 0) {
                JOptionPane.showMessageDialog(this, "El costo por hora debe ser mayor a 0", "Error", JOptionPane.ERROR_MESSAGE);
                txtCostoHora.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese un costo por hora válido", "Error", JOptionPane.ERROR_MESSAGE);
            txtCostoHora.requestFocus();
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
        
        if (observaciones.length() > 250) {
            JOptionPane.showMessageDialog(this, "Las observaciones no pueden tener más de 250 caracteres", "Error", JOptionPane.ERROR_MESSAGE);
            txtObservaciones.requestFocus();
            return;
        }
        
        if (accion.equals("guardar")) {
            // Validar que no exista por documento
            if (!numDocumento.isEmpty() && dao.existePorDocumento(numDocumento)) {
                JOptionPane.showMessageDialog(this, "Ya existe un trabajador con ese número de documento", "Error", JOptionPane.ERROR_MESSAGE);
                txtNumDocumento.requestFocus();
                return;
            }
            
            // Validar que no exista por nombre
            if (dao.existe(nombre)) {
                JOptionPane.showMessageDialog(this, "Ya existe un trabajador con ese nombre", "Error", JOptionPane.ERROR_MESSAGE);
                txtNombre.requestFocus();
                return;
            }
            
            PersonalMontaje nuevo = new PersonalMontaje();
            nuevo.setNombre(nombre);
            nuevo.setTipoDocumento(tipoDocumento);
            nuevo.setNumDocumento(numDocumento);
            nuevo.setTelefono(telefono);
            nuevo.setEmail(email);
            nuevo.setEspecialidad(especialidad);
            nuevo.setCostoPorHora(costoHora);
            nuevo.setDisponible(disponible);
            nuevo.setObservaciones(observaciones);
            nuevo.setActivo(true);
            
            if (dao.insertar(nuevo)) {
                JOptionPane.showMessageDialog(this, "Personal guardado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                listar("");
                cancelar();
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar el personal", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Editar
            if (!numDocumento.equals(numDocumentoActual) && !numDocumento.isEmpty() && dao.existePorDocumento(numDocumento)) {
                JOptionPane.showMessageDialog(this, "Ya existe un trabajador con ese número de documento", "Error", JOptionPane.ERROR_MESSAGE);
                txtNumDocumento.requestFocus();
                return;
            }
            
            if (!nombre.equals(nombreActual) && dao.existe(nombre)) {
                JOptionPane.showMessageDialog(this, "Ya existe un trabajador con ese nombre", "Error", JOptionPane.ERROR_MESSAGE);
                txtNombre.requestFocus();
                return;
            }
            
            PersonalMontaje editar = new PersonalMontaje();
            editar.setId(idActual);
            editar.setNombre(nombre);
            editar.setTipoDocumento(tipoDocumento);
            editar.setNumDocumento(numDocumento);
            editar.setTelefono(telefono);
            editar.setEmail(email);
            editar.setEspecialidad(especialidad);
            editar.setCostoPorHora(costoHora);
            editar.setDisponible(disponible);
            editar.setObservaciones(observaciones);
            editar.setActivo(true);
            
            if (dao.actualizar(editar)) {
                JOptionPane.showMessageDialog(this, "Personal actualizado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                listar("");
                cancelar();
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar el personal", "Error", JOptionPane.ERROR_MESSAGE);
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
            "¿Desea activar al trabajador: " + nombre + "?", 
            "Confirmar", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.activar(idActual)) {
                JOptionPane.showMessageDialog(this, "Personal activado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                listar("");
            } else {
                JOptionPane.showMessageDialog(this, "Error al activar el personal", "Error", JOptionPane.ERROR_MESSAGE);
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
            "¿Desea desactivar al trabajador: " + nombre + "?", 
            "Confirmar", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.desactivar(idActual)) {
                JOptionPane.showMessageDialog(this, "Personal desactivado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                listar("");
            } else {
                JOptionPane.showMessageDialog(this, "Error al desactivar el personal", "Error", JOptionPane.ERROR_MESSAGE);
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
        cbxEspecialidad.setSelectedIndex(0);
        txtCostoHora.setText("");
        chkDisponible.setSelected(true);
        txtObservaciones.setText("");
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
        scrollTabla.setBounds(20, 60, 900, 350);
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
        lblTotalRegistros.setBounds(750, 435, 200, 25);
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
        
        lblEspecialidad = new javax.swing.JLabel();
        lblEspecialidad.setText("Especialidad (*):");
        lblEspecialidad.setBounds(30, 270, 120, 25);
        panelMantenimiento.add(lblEspecialidad);
        
        cbxEspecialidad = new javax.swing.JComboBox<>();
        cbxEspecialidad.setBounds(160, 270, 150, 25);
        panelMantenimiento.add(cbxEspecialidad);
        
        lblCostoHora = new javax.swing.JLabel();
        lblCostoHora.setText("Costo por Hora ($):");
        lblCostoHora.setBounds(30, 310, 130, 25);
        panelMantenimiento.add(lblCostoHora);
        
        txtCostoHora = new javax.swing.JTextField();
        txtCostoHora.setBounds(170, 310, 150, 25);
        panelMantenimiento.add(txtCostoHora);
        
        chkDisponible = new javax.swing.JCheckBox();
        chkDisponible.setText("Disponible");
        chkDisponible.setBounds(170, 350, 150, 25);
        panelMantenimiento.add(chkDisponible);
        
        lblObservaciones = new javax.swing.JLabel();
        lblObservaciones.setText("Observaciones:");
        lblObservaciones.setBounds(30, 390, 100, 25);
        panelMantenimiento.add(lblObservaciones);
        
        txtObservaciones = new javax.swing.JTextField();
        txtObservaciones.setBounds(140, 390, 450, 60);
        panelMantenimiento.add(txtObservaciones);
        
        lblObligatorio = new javax.swing.JLabel();
        lblObligatorio.setText("(*) Campo obligatorio");
        lblObligatorio.setFont(new java.awt.Font("Arial", java.awt.Font.ITALIC, 11));
        lblObligatorio.setBounds(140, 460, 200, 20);
        panelMantenimiento.add(lblObligatorio);
        
        btnGuardar = new javax.swing.JButton();
        btnGuardar.setText("Guardar");
        btnGuardar.setBounds(140, 500, 100, 35);
        btnGuardar.addActionListener(e -> guardar());
        panelMantenimiento.add(btnGuardar);
        
        btnCancelar = new javax.swing.JButton();
        btnCancelar.setText("Cancelar");
        btnCancelar.setBounds(260, 500, 100, 35);
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

        Listado = new javax.swing.JTabbedPane();
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
        lblTotalRegistros = new javax.swing.JLabel();
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
        lblEspecialidad = new javax.swing.JLabel();
        cbxEspecialidad = new javax.swing.JComboBox<>();
        lblCostoHora = new javax.swing.JLabel();
        txtCostoHora = new javax.swing.JTextField();
        chkDisponible = new javax.swing.JCheckBox();
        lblObservaciones = new javax.swing.JLabel();
        txtObservaciones = new javax.swing.JTextField();
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

        lblTotalRegistros.setText("Total Registros: 0");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(scrollTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 1003, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(lblBuscar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnBuscar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnNuevo)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnEditar)))
                        .addContainerGap(29, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnActivar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnDesactivar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblTotalRegistros)
                        .addGap(173, 173, 173))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBuscar)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscar)
                    .addComponent(btnNuevo)
                    .addComponent(btnEditar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scrollTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 464, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnActivar)
                    .addComponent(btnDesactivar)
                    .addComponent(lblTotalRegistros))
                .addContainerGap(41, Short.MAX_VALUE))
        );

        Listado.addTab("Listado", jPanel1);

        lblId.setText("ID:");

        lblNombre.setText("Nombre (*):");

        lblTipoDocumento.setText("Tipo Documento:");

        cbxTipoDocumento.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "CC", "CE", "PASAPORTE" }));

        lblNumDocumento.setText("N° Documento:");

        lblTelefono.setText("Teléfono:");

        lblEmail.setText("Email:");

        lblEspecialidad.setText("Especialidad (*):");

        cbxEspecialidad.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Montaje", "Decoracion", "Sonido", "Iluminacion", "Logística" }));

        lblCostoHora.setText("Costo por Hora:");

        chkDisponible.setText("Disponiblre");

        lblObservaciones.setText("Observaciones:");

        lblObligatorio.setText("(*) Campo Obligatorio ");

        btnGuardar.setText("Guardar");

        btnCancelar.setText("Cancelar");

        javax.swing.GroupLayout MantenimientoLayout = new javax.swing.GroupLayout(Mantenimiento);
        Mantenimiento.setLayout(MantenimientoLayout);
        MantenimientoLayout.setHorizontalGroup(
            MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MantenimientoLayout.createSequentialGroup()
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(MantenimientoLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(MantenimientoLayout.createSequentialGroup()
                                .addComponent(lblObservaciones)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtObservaciones))
                            .addComponent(chkDisponible)
                            .addGroup(MantenimientoLayout.createSequentialGroup()
                                .addComponent(lblCostoHora)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCostoHora, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(MantenimientoLayout.createSequentialGroup()
                                .addComponent(lblTelefono)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(MantenimientoLayout.createSequentialGroup()
                                .addComponent(lblNombre)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(MantenimientoLayout.createSequentialGroup()
                                .addComponent(lblId)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(MantenimientoLayout.createSequentialGroup()
                                    .addComponent(lblNumDocumento)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtNumDocumento))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, MantenimientoLayout.createSequentialGroup()
                                    .addComponent(lblTipoDocumento)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cbxTipoDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(MantenimientoLayout.createSequentialGroup()
                                    .addComponent(lblEspecialidad)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cbxEspecialidad, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(MantenimientoLayout.createSequentialGroup()
                                    .addComponent(lblEmail)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblObligatorio))
                    .addGroup(MantenimientoLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnGuardar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancelar)))
                .addContainerGap(613, Short.MAX_VALUE))
        );
        MantenimientoLayout.setVerticalGroup(
            MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MantenimientoLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblId)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEmail)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEspecialidad)
                    .addComponent(cbxEspecialidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCostoHora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCostoHora))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkDisponible)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblObservaciones)
                    .addComponent(txtObservaciones, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblObligatorio))
                .addGap(57, 57, 57)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuardar)
                    .addComponent(btnCancelar))
                .addContainerGap(103, Short.MAX_VALUE))
        );

        Listado.addTab("Mantenimiento", Mantenimiento);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Listado)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Listado)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane Listado;
    private javax.swing.JPanel Mantenimiento;
    private javax.swing.JButton btnActivar;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnDesactivar;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JComboBox<String> cbxEspecialidad;
    private javax.swing.JComboBox<String> cbxTipoDocumento;
    private javax.swing.JCheckBox chkDisponible;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JLabel lblCostoHora;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblEspecialidad;
    private javax.swing.JLabel lblId;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblNumDocumento;
    private javax.swing.JLabel lblObligatorio;
    private javax.swing.JLabel lblObservaciones;
    private javax.swing.JLabel lblTelefono;
    private javax.swing.JLabel lblTipoDocumento;
    private javax.swing.JLabel lblTotalRegistros;
    private javax.swing.JScrollPane scrollTabla;
    private javax.swing.JTable tablaListado;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtCostoHora;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtNumDocumento;
    private javax.swing.JTextField txtObservaciones;
    private javax.swing.JTextField txtTelefono;
    // End of variables declaration//GEN-END:variables
}
