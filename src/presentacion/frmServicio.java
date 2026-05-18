
package presentacion;

import datos.ServicioDAO;
import datos.TipoServicioDAO;
import entidades.Servicio;
import entidades.TipoServicio;
import java.text.DecimalFormat;
import java.util.List;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
public class frmServicio extends javax.swing.JInternalFrame {





    
    // ========== COMPONENTES ==========
    // Pestañas
    private javax.swing.JTabbedPane tabGeneral;
    
    // Pestaña Listado
   
    private javax.swing.JLabel lblTipoServicio;

    
    // ========== VARIABLES DE CONTROL ==========
    private ServicioDAO servicioDAO;
    private TipoServicioDAO tipoServicioDAO;
    private DefaultTableModel modeloTabla;
    private String accion;
    private int idActual;
    private String nombreActual;
    private String codigoActual;
    private DecimalFormat formatoCOP = new DecimalFormat("#,###");
    
    public frmServicio() {
        initComponentes();
        iniciar();
        setVisible(true);
    }
    
    private void iniciar() {
        servicioDAO = new ServicioDAO();
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
        
        // Cargar tipos de servicio en el combo
        cargarTiposServicio();
        
        listar("");
        tabGeneral.setEnabledAt(1, false);
        
        setTitle("Servicios");
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setSize(850, 600);
    }
    
    // ========== CARGAR TIPOS DE SERVICIO ==========
    
    private void cargarTiposServicio() {
        cbxTipoServicio.removeAllItems();
        List<TipoServicio> tipos = tipoServicioDAO.listar("");
        for (TipoServicio ts : tipos) {
            if (ts.isActivo()) {
                cbxTipoServicio.addItem(ts.getId() + " - " + ts.getNombre());
            }
        }
    }
    
    // ========== MÉTODOS PRINCIPALES ==========
    
    private void listar(String texto) {
        modeloTabla.setRowCount(0);
        List<Servicio> lista = servicioDAO.listar(texto);
        
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
        
        int total = servicioDAO.total();
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
        
        String nombreTipo = tablaListado.getValueAt(fila, 1).toString();
        // Seleccionar el tipo en el combo
        for (int i = 0; i < cbxTipoServicio.getItemCount(); i++) {
            if (cbxTipoServicio.getItemAt(i).contains(nombreTipo)) {
                cbxTipoServicio.setSelectedIndex(i);
                break;
            }
        }
        
        txtCodigo.setText(tablaListado.getValueAt(fila, 2).toString());
        txtNombre.setText(tablaListado.getValueAt(fila, 3).toString());
        
        // Quitar el "$" y las comas del precio
        String precioStr = tablaListado.getValueAt(fila, 4).toString();
        precioStr = precioStr.replace("$", "").replace(",", "").trim();
        txtPrecio.setText(precioStr);
        
        nombreActual = tablaListado.getValueAt(fila, 3).toString();
        codigoActual = tablaListado.getValueAt(fila, 2).toString();
        
        // Cargar descripción
        Servicio servicio = servicioDAO.buscarPorId(idActual);
        if (servicio != null) {
            txtDescripcion.setText(servicio.getDescripcion());
        }
        
        tabGeneral.setEnabledAt(0, false);
        tabGeneral.setEnabledAt(1, true);
        tabGeneral.setSelectedIndex(1);
        txtNombre.requestFocus();
    }
    
    private void guardar() {
        // Validar que haya seleccionado un tipo de servicio
        if (cbxTipoServicio.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un tipo de servicio", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int tipoServicioId = Integer.parseInt(cbxTipoServicio.getSelectedItem().toString().split(" - ")[0]);
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String precioStr = txtPrecio.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        
        // Validaciones
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
            // Validar que no exista por código
            if (!codigo.isEmpty() && servicioDAO.existePorCodigo(codigo)) {
                JOptionPane.showMessageDialog(this, "Ya existe un servicio con ese código", "Error", JOptionPane.ERROR_MESSAGE);
                txtCodigo.requestFocus();
                return;
            }
            
            // Validar que no exista por nombre
            if (servicioDAO.existe(nombre)) {
                JOptionPane.showMessageDialog(this, "Ya existe un servicio con ese nombre", "Error", JOptionPane.ERROR_MESSAGE);
                txtNombre.requestFocus();
                return;
            }
            
            Servicio nuevo = new Servicio();
            nuevo.setTipoServicioId(tipoServicioId);
            nuevo.setCodigo(codigo);
            nuevo.setNombre(nombre);
            nuevo.setPrecioBase(precio);
            nuevo.setDescripcion(descripcion);
            nuevo.setActivo(true);
            
            if (servicioDAO.insertar(nuevo)) {
                JOptionPane.showMessageDialog(this, "Servicio guardado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                listar("");
                cancelar();
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar el servicio", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Editar
            if (!codigo.equals(codigoActual) && !codigo.isEmpty() && servicioDAO.existePorCodigo(codigo)) {
                JOptionPane.showMessageDialog(this, "Ya existe un servicio con ese código", "Error", JOptionPane.ERROR_MESSAGE);
                txtCodigo.requestFocus();
                return;
            }
            
            if (!nombre.equals(nombreActual) && servicioDAO.existe(nombre)) {
                JOptionPane.showMessageDialog(this, "Ya existe un servicio con ese nombre", "Error", JOptionPane.ERROR_MESSAGE);
                txtNombre.requestFocus();
                return;
            }
            
            Servicio editar = new Servicio();
            editar.setId(idActual);
            editar.setTipoServicioId(tipoServicioId);
            editar.setCodigo(codigo);
            editar.setNombre(nombre);
            editar.setPrecioBase(precio);
            editar.setDescripcion(descripcion);
            editar.setActivo(true);
            
            if (servicioDAO.actualizar(editar)) {
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
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Desea activar el servicio: " + nombre + "?", 
            "Confirmar", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (servicioDAO.activar(idActual)) {
                JOptionPane.showMessageDialog(this, "Servicio activado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                listar("");
            } else {
                JOptionPane.showMessageDialog(this, "Error al activar el servicio", "Error", JOptionPane.ERROR_MESSAGE);
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
        String nombre = tablaListado.getValueAt(fila, 3).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Desea desactivar el servicio: " + nombre + "?", 
            "Confirmar", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (servicioDAO.desactivar(idActual)) {
                JOptionPane.showMessageDialog(this, "Servicio desactivado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                listar("");
            } else {
                JOptionPane.showMessageDialog(this, "Error al desactivar el servicio", "Error", JOptionPane.ERROR_MESSAGE);
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
        cbxTipoServicio.setSelectedIndex(0);
        txtCodigo.setText("");
        txtNombre.setText("");
        txtPrecio.setText("");
        txtDescripcion.setText("");
        idActual = 0;
        nombreActual = "";
        codigoActual = "";
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
        
        lblTipoServicio = new javax.swing.JLabel();
        lblTipoServicio.setText("Tipo Servicio (*):");
        lblTipoServicio.setBounds(30, 70, 120, 25);
        panelMantenimiento.add(lblTipoServicio);
        
        cbxTipoServicio = new javax.swing.JComboBox<>();
        cbxTipoServicio.setBounds(160, 70, 200, 25);
        panelMantenimiento.add(cbxTipoServicio);
        
        lblCodigo = new javax.swing.JLabel();
        lblCodigo.setText("Código:");
        lblCodigo.setBounds(30, 110, 100, 25);
        panelMantenimiento.add(lblCodigo);
        
        txtCodigo = new javax.swing.JTextField();
        txtCodigo.setBounds(140, 110, 150, 25);
        panelMantenimiento.add(txtCodigo);
        
        lblNombre = new javax.swing.JLabel();
        lblNombre.setText("Nombre (*):");
        lblNombre.setBounds(30, 150, 100, 25);
        panelMantenimiento.add(lblNombre);
        
        txtNombre = new javax.swing.JTextField();
        txtNombre.setBounds(140, 150, 300, 25);
        panelMantenimiento.add(txtNombre);
        
        lblPrecio = new javax.swing.JLabel();
        lblPrecio.setText("Precio ($):");
        lblPrecio.setBounds(30, 190, 100, 25);
        panelMantenimiento.add(lblPrecio);
        
        txtPrecio = new javax.swing.JTextField();
        txtPrecio.setBounds(140, 190, 150, 25);
        panelMantenimiento.add(txtPrecio);
        
        lblDescripcion = new javax.swing.JLabel();
        lblDescripcion.setText("Descripción:");
        lblDescripcion.setBounds(30, 230, 100, 25);
        panelMantenimiento.add(lblDescripcion);
        
        txtDescripcion = new javax.swing.JTextField();
        txtDescripcion.setBounds(140, 230, 450, 80);
        panelMantenimiento.add(txtDescripcion);
        
        lblObligatorio = new javax.swing.JLabel();
        lblObligatorio.setText("(*) Campo obligatorio");
        lblObligatorio.setFont(new java.awt.Font("Arial", java.awt.Font.ITALIC, 11));
        lblObligatorio.setBounds(140, 320, 200, 20);
        panelMantenimiento.add(lblObligatorio);
        
        btnGuardar = new javax.swing.JButton();
        btnGuardar.setText("Guardar");
        btnGuardar.setBounds(140, 360, 100, 35);
        btnGuardar.addActionListener(e -> guardar());
        panelMantenimiento.add(btnGuardar);
        
        btnCancelar = new javax.swing.JButton();
        btnCancelar.setText("Cancelar");
        btnCancelar.setBounds(260, 360, 100, 35);
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

        lblTotalRegistros.setText("Total Registrados: 0");

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
                        .addComponent(btnEditar))
                    .addGroup(ListadoLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(ListadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(scrollTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 1022, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(ListadoLayout.createSequentialGroup()
                                .addComponent(btnActivar)
                                .addGap(18, 18, 18)
                                .addComponent(btnDesactivar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblTotalRegistros)
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
                    .addComponent(btnEditar))
                .addGap(18, 18, 18)
                .addComponent(scrollTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 443, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(ListadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnActivar)
                    .addComponent(btnDesactivar)
                    .addComponent(lblTotalRegistros))
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

        btnCancelar.setText("jButton2");

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
    private javax.swing.JLabel lblTotalRegistros;
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
