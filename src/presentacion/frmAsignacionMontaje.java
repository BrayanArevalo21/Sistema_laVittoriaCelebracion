
package presentacion;
import datos.AsignacionMontajeDAO;
import datos.EventoDAO;
import datos.PersonalMontajeDAO;
import entidades.AsignacionMontaje;
import entidades.Evento;
import entidades.PersonalMontaje;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
public class frmAsignacionMontaje extends javax.swing.JInternalFrame {






    private javax.swing.JTabbedPane tabGeneral;
    
   
    
    // ========== VARIABLES DE CONTROL ==========
    private AsignacionMontajeDAO dao;
    private EventoDAO eventoDAO;
    private PersonalMontajeDAO personalDAO;
    private DefaultTableModel modeloTabla;
    private String accion;
    private int idActual;
    private DecimalFormat formatoCOP = new DecimalFormat("#,###");
    private SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
    
    public frmAsignacionMontaje() {
        initComponentes();
        iniciar();
        setVisible(true);
    }
    
    private void iniciar() {
        dao = new AsignacionMontajeDAO();
        eventoDAO = new EventoDAO();
        personalDAO = new PersonalMontajeDAO();
        accion = "guardar";
        
        // Configurar modelo de tabla
        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        modeloTabla.setColumnIdentifiers(new String[]{"ID", "EVENTO", "PERSONAL", "ROL", "HORAS", "COSTO TOTAL", "FECHA", "ESTADO"});
        tablaListado.setModel(modeloTabla);
        
        // Cargar combos
        cargarEventos();
        cargarPersonal();
        
        listar("");
        tabGeneral.setEnabledAt(1, false);
        
        setTitle("Asignación de Personal");
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setSize(900, 650);
        
        // Configurar evento para calcular costo automático
        txtHoras.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calcularCostoTotal();
            }
        });
        
        cbxPersonal.addActionListener(e -> calcularCostoTotal());
    }
    
    private void cargarEventos() {
        cbxEvento.removeAllItems();
        List<Evento> eventos = eventoDAO.listar("");
        for (Evento e : eventos) {
            if (e.isActivo()) {
                cbxEvento.addItem(e.getId() + " - " + e.getNombreEvento());
            }
        }
    }
    
    private void cargarPersonal() {
        cbxPersonal.removeAllItems();
        List<PersonalMontaje> personal = personalDAO.listar("");
        for (PersonalMontaje p : personal) {
            if (p.isActivo() && p.isDisponible()) {
                cbxPersonal.addItem(p.getId() + " - " + p.getNombre() + " ($" + formatoCOP.format(p.getCostoPorHora()) + "/hora)");
            }
        }
    }
    
    private void calcularCostoTotal() {
        try {
            if (cbxPersonal.getSelectedIndex() != -1 && !txtHoras.getText().trim().isEmpty()) {
                String personalStr = cbxPersonal.getSelectedItem().toString();
                int personalId = Integer.parseInt(personalStr.split(" - ")[0]);
                PersonalMontaje personal = personalDAO.buscarPorId(personalId);
                double horas = Double.parseDouble(txtHoras.getText().trim());
                double costoTotal = personal.getCostoPorHora() * horas;
                txtCostoTotal.setText("$" + formatoCOP.format(costoTotal));
                txtCostoTotal.setEditable(false);
            } else {
                txtCostoTotal.setText("");
            }
        } catch (NumberFormatException e) {
            txtCostoTotal.setText("");
        }
    }
    
    // ========== MÉTODOS PRINCIPALES ==========
    
    private void listar(String texto) {
        modeloTabla.setRowCount(0);
        List<AsignacionMontaje> lista = dao.listar(texto);
        
        for (AsignacionMontaje a : lista) {
            String estado = a.isActivo() ? "Activo" : "Inactivo";
            modeloTabla.addRow(new Object[]{
                a.getId(),
                a.getNombreEvento(),
                a.getNombrePersonal(),
                a.getRolAsignado() != null ? a.getRolAsignado() : "",
                a.getHorasTrabajadas(),
                "$" + formatoCOP.format(a.getCostoTotal()),
                a.getFechaAsignacion(),
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
        
        // Cargar datos de la asignación
        AsignacionMontaje asignacion = dao.buscarPorId(idActual);
        if (asignacion != null) {
            // Seleccionar evento
            for (int i = 0; i < cbxEvento.getItemCount(); i++) {
                if (cbxEvento.getItemAt(i).startsWith(String.valueOf(asignacion.getEventoId()))) {
                    cbxEvento.setSelectedIndex(i);
                    break;
                }
            }
            
            // Seleccionar personal
            for (int i = 0; i < cbxPersonal.getItemCount(); i++) {
                if (cbxPersonal.getItemAt(i).startsWith(String.valueOf(asignacion.getPersonalId()))) {
                    cbxPersonal.setSelectedIndex(i);
                    break;
                }
            }
            
            txtRolAsignado.setText(asignacion.getRolAsignado());
            txtHoras.setText(String.valueOf(asignacion.getHorasTrabajadas()));
            txtCostoTotal.setText("$" + formatoCOP.format(asignacion.getCostoTotal()));
            txtCostoTotal.setEditable(false);
            txtFechaAsignacion.setText(asignacion.getFechaAsignacion());
            txtObservaciones.setText(asignacion.getObservaciones());
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
        
        // Validar personal
        if (cbxPersonal.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un personal", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int eventoId = Integer.parseInt(cbxEvento.getSelectedItem().toString().split(" - ")[0]);
        int personalId = Integer.parseInt(cbxPersonal.getSelectedItem().toString().split(" - ")[0]);
        String rolAsignado = txtRolAsignado.getText().trim();
        String horasStr = txtHoras.getText().trim();
        String fechaAsignacion = txtFechaAsignacion.getText().trim();
        String observaciones = txtObservaciones.getText().trim();
        
        // Validar horas
        double horas = 0;
        if (!horasStr.isEmpty()) {
            try {
                horas = Double.parseDouble(horasStr);
                if (horas < 0) {
                    JOptionPane.showMessageDialog(this, "Las horas no pueden ser negativas", "Error", JOptionPane.ERROR_MESSAGE);
                    txtHoras.requestFocus();
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Ingrese un número válido para horas", "Error", JOptionPane.ERROR_MESSAGE);
                txtHoras.requestFocus();
                return;
            }
        }
        
        // Obtener costo por hora del personal
        PersonalMontaje personal = personalDAO.buscarPorId(personalId);
        double costoTotal = personal.getCostoPorHora() * horas;
        
        // Validar fecha
        if (!fechaAsignacion.isEmpty()) {
            try {
                Date fecha = formatoFecha.parse(fechaAsignacion);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Use dd/MM/yyyy", "Error", JOptionPane.ERROR_MESSAGE);
                txtFechaAsignacion.requestFocus();
                return;
            }
        }
        
        if (observaciones.length() > 250) {
            JOptionPane.showMessageDialog(this, "Las observaciones no pueden tener más de 250 caracteres", "Error", JOptionPane.ERROR_MESSAGE);
            txtObservaciones.requestFocus();
            return;
        }
        
        if (accion.equals("guardar")) {
            AsignacionMontaje nueva = new AsignacionMontaje();
            nueva.setEventoId(eventoId);
            nueva.setPersonalId(personalId);
            nueva.setRolAsignado(rolAsignado);
            nueva.setHorasTrabajadas(horas);
            nueva.setCostoTotal(costoTotal);
            nueva.setFechaAsignacion(fechaAsignacion);
            nueva.setObservaciones(observaciones);
            nueva.setActivo(true);
            
            if (dao.insertar(nueva)) {
                JOptionPane.showMessageDialog(this, "Asignación guardada correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                listar("");
                cancelar();
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar la asignación", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            AsignacionMontaje editar = new AsignacionMontaje();
            editar.setId(idActual);
            editar.setEventoId(eventoId);
            editar.setPersonalId(personalId);
            editar.setRolAsignado(rolAsignado);
            editar.setHorasTrabajadas(horas);
            editar.setCostoTotal(costoTotal);
            editar.setFechaAsignacion(fechaAsignacion);
            editar.setObservaciones(observaciones);
            editar.setActivo(true);
            
            if (dao.actualizar(editar)) {
                JOptionPane.showMessageDialog(this, "Asignación actualizada correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                listar("");
                cancelar();
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar la asignación", "Error", JOptionPane.ERROR_MESSAGE);
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
        String personal = tablaListado.getValueAt(fila, 2).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Desea activar la asignación: " + evento + " - " + personal + "?", 
            "Confirmar", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.activar(idActual)) {
                JOptionPane.showMessageDialog(this, "Asignación activada correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                listar("");
            } else {
                JOptionPane.showMessageDialog(this, "Error al activar la asignación", "Error", JOptionPane.ERROR_MESSAGE);
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
        String personal = tablaListado.getValueAt(fila, 2).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Desea desactivar la asignación: " + evento + " - " + personal + "?", 
            "Confirmar", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.desactivar(idActual)) {
                JOptionPane.showMessageDialog(this, "Asignación desactivada correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                listar("");
            } else {
                JOptionPane.showMessageDialog(this, "Error al desactivar la asignación", "Error", JOptionPane.ERROR_MESSAGE);
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
        cbxPersonal.setSelectedIndex(0);
        txtRolAsignado.setText("");
        txtHoras.setText("");
        txtCostoTotal.setText("");
        txtFechaAsignacion.setText("");
        txtObservaciones.setText("");
        idActual = 0;
    }
    
    
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
        scrollTabla.setBounds(20, 60, 850, 350);
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
        lblTotalRegistros.setBounds(700, 435, 200, 25);
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
        cbxEvento.setBounds(140, 70, 350, 25);
        panelMantenimiento.add(cbxEvento);
        
        lblPersonal = new javax.swing.JLabel();
        lblPersonal.setText("Personal (*):");
        lblPersonal.setBounds(30, 110, 120, 25);
        panelMantenimiento.add(lblPersonal);
        
        cbxPersonal = new javax.swing.JComboBox<>();
        cbxPersonal.setBounds(160, 110, 350, 25);
        panelMantenimiento.add(cbxPersonal);
        
        lblRolAsignado = new javax.swing.JLabel();
        lblRolAsignado.setText("Rol Asignado:");
        lblRolAsignado.setBounds(30, 150, 120, 25);
        panelMantenimiento.add(lblRolAsignado);
        
        txtRolAsignado = new javax.swing.JTextField();
        txtRolAsignado.setBounds(160, 150, 200, 25);
        panelMantenimiento.add(txtRolAsignado);
        
        lblHoras = new javax.swing.JLabel();
        lblHoras.setText("Horas Trabajadas:");
        lblHoras.setBounds(30, 190, 130, 25);
        panelMantenimiento.add(lblHoras);
        
        txtHoras = new javax.swing.JTextField();
        txtHoras.setBounds(170, 190, 100, 25);
        panelMantenimiento.add(txtHoras);
        
        lblCostoTotal = new javax.swing.JLabel();
        lblCostoTotal.setText("Costo Total:");
        lblCostoTotal.setBounds(30, 230, 120, 25);
        panelMantenimiento.add(lblCostoTotal);
        
        txtCostoTotal = new javax.swing.JTextField();
        txtCostoTotal.setBounds(160, 230, 180, 25);
        txtCostoTotal.setEditable(false);
        panelMantenimiento.add(txtCostoTotal);
        
        lblFechaAsignacion = new javax.swing.JLabel();
        lblFechaAsignacion.setText("Fecha Asignación:");
        lblFechaAsignacion.setBounds(30, 270, 120, 25);
        panelMantenimiento.add(lblFechaAsignacion);
        
        txtFechaAsignacion = new javax.swing.JTextField();
        txtFechaAsignacion.setBounds(160, 270, 150, 25);
        panelMantenimiento.add(txtFechaAsignacion);
        
        lblObservaciones = new javax.swing.JLabel();
        lblObservaciones.setText("Observaciones:");
        lblObservaciones.setBounds(30, 310, 100, 25);
        panelMantenimiento.add(lblObservaciones);
        
        txtObservaciones = new javax.swing.JTextField();
        txtObservaciones.setBounds(140, 310, 450, 60);
        panelMantenimiento.add(txtObservaciones);
        
        lblObligatorio = new javax.swing.JLabel();
        lblObligatorio.setText("(*) Campo obligatorio");
        lblObligatorio.setFont(new java.awt.Font("Arial", java.awt.Font.ITALIC, 11));
        lblObligatorio.setBounds(140, 390, 200, 20);
        panelMantenimiento.add(lblObligatorio);
        
        btnGuardar = new javax.swing.JButton();
        btnGuardar.setText("Guardar");
        btnGuardar.setBounds(140, 430, 100, 35);
        btnGuardar.addActionListener(e -> guardar());
        panelMantenimiento.add(btnGuardar);
        
        btnCancelar = new javax.swing.JButton();
        btnCancelar.setText("Cancelar");
        btnCancelar.setBounds(260, 430, 100, 35);
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
        lblEvento = new javax.swing.JLabel();
        cbxEvento = new javax.swing.JComboBox<>();
        lblPersonal = new javax.swing.JLabel();
        cbxPersonal = new javax.swing.JComboBox<>();
        lblRolAsignado = new javax.swing.JLabel();
        txtRolAsignado = new javax.swing.JTextField();
        lblHoras = new javax.swing.JLabel();
        txtHoras = new javax.swing.JTextField();
        lblCostoTotal = new javax.swing.JLabel();
        txtCostoTotal = new javax.swing.JTextField();
        lblFechaAsignacion = new javax.swing.JLabel();
        txtFechaAsignacion = new javax.swing.JTextField();
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

        javax.swing.GroupLayout ListadoLayout = new javax.swing.GroupLayout(Listado);
        Listado.setLayout(ListadoLayout);
        ListadoLayout.setHorizontalGroup(
            ListadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ListadoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ListadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(ListadoLayout.createSequentialGroup()
                        .addComponent(lblBuscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 744, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBuscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnNuevo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEditar))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, ListadoLayout.createSequentialGroup()
                        .addComponent(btnActivar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDesactivar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblTotalRegistros)
                        .addGap(118, 118, 118))
                    .addComponent(scrollTabla, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 1009, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        ListadoLayout.setVerticalGroup(
            ListadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ListadoLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(ListadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBuscar)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscar)
                    .addComponent(btnNuevo)
                    .addComponent(btnEditar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scrollTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 457, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(ListadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnActivar)
                    .addComponent(btnDesactivar)
                    .addComponent(lblTotalRegistros))
                .addContainerGap(47, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Listado", Listado);

        lblId.setText("ID:");

        lblEvento.setText("Evento:");

        lblPersonal.setText("Personal:");

        lblRolAsignado.setText("Rol Asignado:");

        lblHoras.setText("Horas Trabajadas:");

        lblCostoTotal.setText("Costo Total:");

        lblFechaAsignacion.setText("Fecha Asignacion:");

        lblObservaciones.setText("Observaciones");

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
                                .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(MantenimientoLayout.createSequentialGroup()
                                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblEvento)
                                    .addComponent(lblPersonal))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cbxEvento, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(cbxPersonal, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGroup(MantenimientoLayout.createSequentialGroup()
                            .addComponent(lblRolAsignado)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtRolAsignado, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(MantenimientoLayout.createSequentialGroup()
                            .addComponent(lblHoras)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtHoras))
                        .addGroup(MantenimientoLayout.createSequentialGroup()
                            .addComponent(lblCostoTotal)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtCostoTotal))
                        .addGroup(MantenimientoLayout.createSequentialGroup()
                            .addComponent(lblFechaAsignacion)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtFechaAsignacion)))
                    .addGroup(MantenimientoLayout.createSequentialGroup()
                        .addComponent(lblObservaciones)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtObservaciones, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblObligatorio)
                    .addGroup(MantenimientoLayout.createSequentialGroup()
                        .addComponent(btnGuardar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancelar)))
                .addContainerGap(739, Short.MAX_VALUE))
        );
        MantenimientoLayout.setVerticalGroup(
            MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MantenimientoLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblId)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEvento)
                    .addComponent(cbxEvento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPersonal)
                    .addComponent(cbxPersonal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblRolAsignado)
                    .addComponent(txtRolAsignado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblHoras)
                    .addComponent(txtHoras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCostoTotal)
                    .addComponent(txtCostoTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFechaAsignacion)
                    .addComponent(txtFechaAsignacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblObservaciones)
                    .addComponent(txtObservaciones, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblObligatorio)
                .addGap(51, 51, 51)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuardar)
                    .addComponent(btnCancelar))
                .addContainerGap(167, Short.MAX_VALUE))
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
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1)
                .addContainerGap())
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
    private javax.swing.JComboBox<String> cbxPersonal;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JLabel lblCostoTotal;
    private javax.swing.JLabel lblEvento;
    private javax.swing.JLabel lblFechaAsignacion;
    private javax.swing.JLabel lblHoras;
    private javax.swing.JLabel lblId;
    private javax.swing.JLabel lblObligatorio;
    private javax.swing.JLabel lblObservaciones;
    private javax.swing.JLabel lblPersonal;
    private javax.swing.JLabel lblRolAsignado;
    private javax.swing.JLabel lblTotalRegistros;
    private javax.swing.JScrollPane scrollTabla;
    private javax.swing.JTable tablaListado;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtCostoTotal;
    private javax.swing.JTextField txtFechaAsignacion;
    private javax.swing.JTextField txtHoras;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtObservaciones;
    private javax.swing.JTextField txtRolAsignado;
    // End of variables declaration//GEN-END:variables
}
