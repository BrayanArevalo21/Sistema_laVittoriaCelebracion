package presentacion;

import datos.AlimentoDAO;
import entidades.Alimento;
import java.text.DecimalFormat;
import java.util.List;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class frmAlimento extends JInternalFrame {
    
 
    
    
    // Variables de control
    private AlimentoDAO dao;
    private DefaultTableModel modeloTabla;
    private String accion;
    private int idActual;
    private String nombreActual;
    private DecimalFormat formatoCOP = new DecimalFormat("#,###");
    
    public frmAlimento() {
        initComponents();
        configurarEventos();
        iniciar();
        setVisible(true);
    }
    
    private void iniciar() {
        dao = new AlimentoDAO();
        accion = "guardar";
        
        // Configurar modelo de tabla
        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        modeloTabla.setColumnIdentifiers(new String[]{"ID", "CATEGORÍA", "NOMBRE", "PRECIO", "COSTO PROV", "UNIDAD", "REFRIG", "ESTADO"});
        tablaListado.setModel(modeloTabla);
        
        // Configurar JComboBoxes
        configurarCombos();
        
        // Configurar combo filtro
        cbxFiltrado.removeAllItems();
        cbxFiltrado.addItem("Activos");
        cbxFiltrado.addItem("Inactivos");
        cbxFiltrado.addItem("Todos");
        cbxFiltrado.setSelectedIndex(0);
        
        listar("");
        jTabbedPane1.setEnabledAt(1, false);
        
        setTitle("Alimentos y Bebidas");
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setSize(900, 650);
    }
    
    private void configurarCombos() {
        // Categorías
        cbxCategoria.addItem("Entrada");
        cbxCategoria.addItem("Plato Fuerte");
        cbxCategoria.addItem("Postre");
        cbxCategoria.addItem("Bebida");
        cbxCategoria.addItem("Snack");
        cbxCategoria.addItem("Guarnición");
        cbxCategoria.addItem("Salsa");
        
        // Unidades de medida
        cbxUnidadMedida.addItem("Porción");
        cbxUnidadMedida.addItem("Unidad");
        cbxUnidadMedida.addItem("Lata");
        cbxUnidadMedida.addItem("Botella");
        cbxUnidadMedida.addItem("Libra");
        cbxUnidadMedida.addItem("Kilo");
        cbxUnidadMedida.addItem("Gramo");
        cbxUnidadMedida.addItem("Litro");
        cbxUnidadMedida.addItem("Mililitro");
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
                    nombreActual = tablaListado.getValueAt(fila, 2).toString();
                }
            }
        });
    }
    
    private void listar(String texto) {
        if (cbxFiltrado.getSelectedItem() == null) return;  // ← AGREGADO
        
        modeloTabla.setRowCount(0);
        String filtroEstado = cbxFiltrado.getSelectedItem().toString();  // ← AGREGADO
        List<Alimento> lista = dao.listarConFiltro(texto, filtroEstado);  // ← MODIFICADO
        
        for (Alimento a : lista) {
            String estado = a.isActivo() ? "Activo" : "Inactivo";
            String refrigeracion = a.isRequiereRefrigeracion() ? "Sí" : "No";
            modeloTabla.addRow(new Object[]{
                a.getId(),
                a.getCategoriaAlimento(),
                a.getNombre(),
                "$" + formatoCOP.format(a.getPrecioUnitario()),
                "$" + formatoCOP.format(a.getCostoProveedor()),
                a.getUnidadMedida() != null ? a.getUnidadMedida() : "",
                refrigeracion,
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
        
        cbxCategoria.setSelectedItem(tablaListado.getValueAt(fila, 1).toString());
        txtNombre.setText(tablaListado.getValueAt(fila, 2).toString());
        
        String precioStr = tablaListado.getValueAt(fila, 3).toString();
        precioStr = precioStr.replace("$", "").replace(",", "").trim();
        txtPrecioUnitario.setText(precioStr);
        
        String costoStr = tablaListado.getValueAt(fila, 4).toString();
        costoStr = costoStr.replace("$", "").replace(",", "").trim();
        txtCostoProveedor.setText(costoStr);
        
        nombreActual = tablaListado.getValueAt(fila, 2).toString();
        
        Alimento alimento = dao.buscarPorId(idActual);
        if (alimento != null) {
            txtDescripcion.setText(alimento.getDescripcion());
            if (alimento.getUnidadMedida() != null) {
                cbxUnidadMedida.setSelectedItem(alimento.getUnidadMedida());
            }
            chkRefrigeracion.setSelected(alimento.isRequiereRefrigeracion());
            txtAlergenos.setText(alimento.getAlergenos());
        }
        
        jTabbedPane1.setEnabledAt(0, false);
        jTabbedPane1.setEnabledAt(1, true);
        jTabbedPane1.setSelectedIndex(1);
        txtNombre.requestFocus();
    }
    
    private void guardar() {
        String categoria = cbxCategoria.getSelectedItem().toString();
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        String precioStr = txtPrecioUnitario.getText().trim();
        String costoStr = txtCostoProveedor.getText().trim();
        String unidadMedida = cbxUnidadMedida.getSelectedItem().toString();
        boolean requiereRefrigeracion = chkRefrigeracion.isSelected();
        String alergenos = txtAlergenos.getText().trim();
        
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
            JOptionPane.showMessageDialog(this, "El precio unitario es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            txtPrecioUnitario.requestFocus();
            return;
        }
        
        double precioUnitario;
        try {
            precioUnitario = Double.parseDouble(precioStr);
            if (precioUnitario <= 0) {
                JOptionPane.showMessageDialog(this, "El precio debe ser mayor a 0", "Error", JOptionPane.ERROR_MESSAGE);
                txtPrecioUnitario.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese un precio válido", "Error", JOptionPane.ERROR_MESSAGE);
            txtPrecioUnitario.requestFocus();
            return;
        }
        
        double costoProveedor = 0;
        if (!costoStr.isEmpty()) {
            try {
                costoProveedor = Double.parseDouble(costoStr);
                if (costoProveedor < 0) {
                    JOptionPane.showMessageDialog(this, "El costo proveedor no puede ser negativo", "Error", JOptionPane.ERROR_MESSAGE);
                    txtCostoProveedor.requestFocus();
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Ingrese un costo proveedor válido", "Error", JOptionPane.ERROR_MESSAGE);
                txtCostoProveedor.requestFocus();
                return;
            }
        }
        
        if (descripcion.length() > 250) {
            JOptionPane.showMessageDialog(this, "La descripción no puede tener más de 250 caracteres", "Error", JOptionPane.ERROR_MESSAGE);
            txtDescripcion.requestFocus();
            return;
        }
        
        if (alergenos.length() > 100) {
            JOptionPane.showMessageDialog(this, "Los alérgenos no pueden tener más de 100 caracteres", "Error", JOptionPane.ERROR_MESSAGE);
            txtAlergenos.requestFocus();
            return;
        }
        
        if (accion.equals("guardar")) {
            if (dao.existe(nombre)) {
                JOptionPane.showMessageDialog(this, "Ya existe un alimento con ese nombre", "Error", JOptionPane.ERROR_MESSAGE);
                txtNombre.requestFocus();
                return;
            }
            
            Alimento nuevo = new Alimento();
            nuevo.setCategoriaAlimento(categoria);
            nuevo.setNombre(nombre);
            nuevo.setDescripcion(descripcion);
            nuevo.setPrecioUnitario(precioUnitario);
            nuevo.setCostoProveedor(costoProveedor);
            nuevo.setUnidadMedida(unidadMedida);
            nuevo.setRequiereRefrigeracion(requiereRefrigeracion);
            nuevo.setAlergenos(alergenos);
            nuevo.setActivo(true);
            
            if (dao.insertar(nuevo)) {
                JOptionPane.showMessageDialog(this, "Alimento guardado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                listar("");
                cancelar();
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar el alimento", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            if (!nombre.equals(nombreActual) && dao.existe(nombre)) {
                JOptionPane.showMessageDialog(this, "Ya existe un alimento con ese nombre", "Error", JOptionPane.ERROR_MESSAGE);
                txtNombre.requestFocus();
                return;
            }
            
            Alimento editar = new Alimento();
            editar.setId(idActual);
            editar.setCategoriaAlimento(categoria);
            editar.setNombre(nombre);
            editar.setDescripcion(descripcion);
            editar.setPrecioUnitario(precioUnitario);
            editar.setCostoProveedor(costoProveedor);
            editar.setUnidadMedida(unidadMedida);
            editar.setRequiereRefrigeracion(requiereRefrigeracion);
            editar.setAlergenos(alergenos);
            editar.setActivo(true);
            
            if (dao.actualizar(editar)) {
                JOptionPane.showMessageDialog(this, "Alimento actualizado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                listar("");
                cancelar();
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar el alimento", "Error", JOptionPane.ERROR_MESSAGE);
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
        
        int confirm = JOptionPane.showConfirmDialog(this, "¿Activar alimento: " + nombre + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION && dao.activar(idActual)) {
            JOptionPane.showMessageDialog(this, "Alimento activado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
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
        String nombre = tablaListado.getValueAt(fila, 2).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this, "¿Desactivar alimento: " + nombre + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION && dao.desactivar(idActual)) {
            JOptionPane.showMessageDialog(this, "Alimento desactivado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
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
        cbxCategoria.setSelectedIndex(0);
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtPrecioUnitario.setText("");
        txtCostoProveedor.setText("");
        cbxUnidadMedida.setSelectedIndex(0);
        chkRefrigeracion.setSelected(false);
        txtAlergenos.setText("");
        idActual = 0;
        nombreActual = "";
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
        lblCategoria = new javax.swing.JLabel();
        cbxCategoria = new javax.swing.JComboBox<>();
        lblNombre = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        lblDescripcion = new javax.swing.JLabel();
        txtDescripcion = new javax.swing.JTextField();
        lblPrecioUnitario = new javax.swing.JLabel();
        txtPrecioUnitario = new javax.swing.JTextField();
        lblCostoProveedor = new javax.swing.JLabel();
        txtCostoProveedor = new javax.swing.JTextField();
        lblUnidadMedida = new javax.swing.JLabel();
        cbxUnidadMedida = new javax.swing.JComboBox<>();
        chkRefrigeracion = new javax.swing.JCheckBox();
        lblAlergenos = new javax.swing.JLabel();
        txtAlergenos = new javax.swing.JTextField();
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

        lblTotalRegistardos.setText("Total registros: 0");

        cbxFiltrado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Activos", "Inactivos", "Todos" }));

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
                        .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBuscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnNuevo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEditar)
                        .addGap(269, 269, 269)
                        .addComponent(cbxFiltrado, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(ListadoLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(ListadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(scrollTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 1006, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(ListadoLayout.createSequentialGroup()
                                .addComponent(btnActivar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnDesactivar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblTotalRegistardos)
                                .addGap(175, 175, 175)))))
                .addContainerGap(31, Short.MAX_VALUE))
        );
        ListadoLayout.setVerticalGroup(
            ListadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ListadoLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(ListadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBuscar)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscar)
                    .addComponent(btnNuevo)
                    .addComponent(btnEditar)
                    .addComponent(cbxFiltrado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(scrollTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 447, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(ListadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnActivar)
                    .addComponent(btnDesactivar)
                    .addComponent(lblTotalRegistardos))
                .addContainerGap(63, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Listado", Listado);

        lblId.setText("ID:");

        lblCategoria.setText("Categoria (*):");

        cbxCategoria.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Entrada", "Plato Fuerte", "Postre", "Bebida", "Snack" }));

        lblNombre.setText("Nombre:");

        lblDescripcion.setText("Descripción:");

        lblPrecioUnitario.setText("Precio Unitario ($):");

        lblCostoProveedor.setText("Costo Proveedor ($):");

        lblUnidadMedida.setText("Unidad Medida:");

        cbxUnidadMedida.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Porción", "Unidad", "Lata", "Botella", "Libra", "Kilo" }));

        chkRefrigeracion.setText("Requiere Refrigeracion");

        lblAlergenos.setText("Alérgenos:");

        lblObligatorio.setText("(*) Campo Obligatorio");

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
                        .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(MantenimientoLayout.createSequentialGroup()
                                .addComponent(lblAlergenos)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtAlergenos, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(lblObligatorio))
                            .addGroup(MantenimientoLayout.createSequentialGroup()
                                .addComponent(lblPrecioUnitario)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtPrecioUnitario, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblCostoProveedor)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCostoProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblUnidadMedida)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbxUnidadMedida, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkRefrigeracion))
                            .addGroup(MantenimientoLayout.createSequentialGroup()
                                .addComponent(lblId)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblCategoria)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbxCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblNombre)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(MantenimientoLayout.createSequentialGroup()
                                .addComponent(lblDescripcion)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(MantenimientoLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnGuardar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnCancelar)))
                .addContainerGap(144, Short.MAX_VALUE))
        );
        MantenimientoLayout.setVerticalGroup(
            MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MantenimientoLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblId)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCategoria)
                    .addComponent(cbxCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNombre)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDescripcion)
                    .addComponent(txtDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPrecioUnitario)
                    .addComponent(txtPrecioUnitario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCostoProveedor)
                    .addComponent(txtCostoProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblUnidadMedida)
                    .addComponent(cbxUnidadMedida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkRefrigeracion))
                .addGap(21, 21, 21)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAlergenos)
                    .addComponent(txtAlergenos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblObligatorio))
                .addGap(18, 18, 18)
                .addGroup(MantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuardar)
                    .addComponent(btnCancelar))
                .addContainerGap(227, Short.MAX_VALUE))
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
                .addContainerGap()
                .addComponent(jTabbedPane1))
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
    private javax.swing.JComboBox<String> cbxCategoria;
    private javax.swing.JComboBox<String> cbxFiltrado;
    private javax.swing.JComboBox<String> cbxUnidadMedida;
    private javax.swing.JCheckBox chkRefrigeracion;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblAlergenos;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JLabel lblCategoria;
    private javax.swing.JLabel lblCostoProveedor;
    private javax.swing.JLabel lblDescripcion;
    private javax.swing.JLabel lblId;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblObligatorio;
    private javax.swing.JLabel lblPrecioUnitario;
    private javax.swing.JLabel lblTotalRegistardos;
    private javax.swing.JLabel lblUnidadMedida;
    private javax.swing.JScrollPane scrollTabla;
    private javax.swing.JTable tablaListado;
    private javax.swing.JTextField txtAlergenos;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtCostoProveedor;
    private javax.swing.JTextField txtDescripcion;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtPrecioUnitario;
    // End of variables declaration//GEN-END:variables
}
