
package presentacion;

import datos.ClienteDAO;
import datos.EventoDAO;
import datos.ServicioDAO;
import datos.PagoDAO;
import entidades.Cliente;
import entidades.Evento;
import entidades.Servicio;
import entidades.Pago;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class frmEvento extends JInternalFrame {
    
    // ========== VARIABLES DE CONTROL ==========
    private EventoDAO eventoDAO;
    private ClienteDAO clienteDAO;
    private ServicioDAO servicioDAO;
    private PagoDAO pagoDAO;
    private DefaultTableModel modeloTabla;
    private DefaultTableModel modeloTablaServicios;
    private DefaultTableModel modeloTablaPagos;
    private String accion;
    private int idActual;
    private int eventoIdActual;
    private double totalServicios = 0;
    private double totalPagado = 0;
    private DecimalFormat formatoCOP = new DecimalFormat("#,###");
    private SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
    
    public frmEvento() {
        initComponents();
        configurarEventos();
        iniciar();
        setVisible(true);
    }
    
    private void iniciar() {
        eventoDAO = new EventoDAO();
        clienteDAO = new ClienteDAO();
        servicioDAO = new ServicioDAO();
        pagoDAO = new PagoDAO();
        accion = "guardar";
        
        // Configurar tabla de listado
        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        modeloTabla.setColumnIdentifiers(new String[]{"ID", "CLIENTE", "EVENTO", "FECHA", "PRESUPUESTO", "ESTADO"});
        tablaListado.setModel(modeloTabla);
        
        // Configurar tabla de servicios
        modeloTablaServicios = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        modeloTablaServicios.setColumnIdentifiers(new String[]{"SERVICIO", "CANTIDAD", "PRECIO", "SUBTOTAL"});
        tablaServicios.setModel(modeloTablaServicios);
        
        // Configurar tabla de pagos
        modeloTablaPagos = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        modeloTablaPagos.setColumnIdentifiers(new String[]{"FECHA", "MONTO", "MÉTODO", "REFERENCIA"});
        tablaPagos.setModel(modeloTablaPagos);
        
        // Configurar combo filtro
        cbxFiltrado.removeAllItems();
        cbxFiltrado.addItem("Activos");
        cbxFiltrado.addItem("Inactivos");
        cbxFiltrado.addItem("Todos");
        cbxFiltrado.setSelectedIndex(0);
        
        // Cargar combos
        cargarClientes();
        cargarServicios();
        cargarEstados();
        cargarMetodosPago();
        
        // Fecha actual en campo de fecha
        txtFechaEvento.setText(formatoFecha.format(new Date()));
        
        listar("");
        jTabbedPane1.setEnabledAt(1, false);
        jTabbedPane1.setEnabledAt(2, false);
        jTabbedPane1.setEnabledAt(3, false);
        
        setTitle("Gestión de Eventos");
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setSize(1000, 700);
    }
    
    // ========== CARGAR COMBOS ==========
    
    private void cargarClientes() {
        cbxCliente.removeAllItems();
        List<Cliente> clientes = clienteDAO.listar("");
        for (Cliente c : clientes) {
            if (c.isActivo()) {
                cbxCliente.addItem(c.getId() + " - " + c.getNombre());
            }
        }
    }
    
    private void cargarServicios() {
        cbxServicio.removeAllItems();
        List<Servicio> servicios = servicioDAO.listar("");
        for (Servicio s : servicios) {
            if (s.isActivo()) {
                cbxServicio.addItem(s.getId() + " - " + s.getNombre() + " ($" + formatoCOP.format(s.getPrecioBase()) + ")");
            }
        }
        
        // Evento para mostrar precio al seleccionar servicio
        cbxServicio.addActionListener(e -> {
            if (cbxServicio.getSelectedIndex() != -1) {
                String selected = cbxServicio.getSelectedItem().toString();
                String precioStr = selected.substring(selected.lastIndexOf("$") + 1).replace(",", "").trim();
                try {
                    double precio = Double.parseDouble(precioStr);
                    txtPrecio.setText(String.valueOf(precio));
                } catch (NumberFormatException ex) {
                    txtPrecio.setText("");
                }
            }
        });
    }
    
    private void cargarEstados() {
        cbxEstado.removeAllItems();
        cbxEstado.addItem("Cotizando");
        cbxEstado.addItem("Confirmado");
        cbxEstado.addItem("En Progreso");
        cbxEstado.addItem("Realizado");
        cbxEstado.addItem("Cancelado");
    }
    
    private void cargarMetodosPago() {
        cbxMetodoPago.removeAllItems();
        cbxMetodoPago.addItem("Efectivo");
        cbxMetodoPago.addItem("Transferencia");
        cbxMetodoPago.addItem("Tarjeta Débito");
        cbxMetodoPago.addItem("Tarjeta Crédito");
        cbxMetodoPago.addItem("Nequi");
        cbxMetodoPago.addItem("Daviplata");
    }
    
    // ========== CONFIGURAR EVENTOS ==========
    
    private void configurarEventos() {
        btnBuscar.addActionListener(e -> listar(txtBuscar.getText()));
        btnNuevo.addActionListener(e -> nuevo());
        btnEditar.addActionListener(e -> editar());
        btnGuardar.addActionListener(e -> guardar());
        btnCancelar.addActionListener(e -> cancelar());
        btnActivar.addActionListener(e -> activar());
        btnDesactivar.addActionListener(e -> desactivar());
        cbxFiltrado.addActionListener(e -> listar(txtBuscar.getText()));
        
        btnAgregarServicio.addActionListener(e -> agregarServicio());
        btnQuitarServicio.addActionListener(e -> quitarServicio());
        btnRegistrarPago.addActionListener(e -> registrarPago());
        
        tablaListado.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int fila = tablaListado.getSelectedRow();
                if (fila >= 0) {
                    idActual = Integer.parseInt(tablaListado.getValueAt(fila, 0).toString());
                }
            }
        });
    }
    
    // ========== LISTAR EVENTOS ==========
    
    private void listar(String texto) {
        if (cbxFiltrado.getSelectedItem() == null) return;
        
        modeloTabla.setRowCount(0);
        String filtroEstado = cbxFiltrado.getSelectedItem().toString();
        
        List<Evento> lista;
        if (filtroEstado.equals("Activos")) {
            lista = eventoDAO.listar(texto);
        } else if (filtroEstado.equals("Inactivos")) {
            lista = eventoDAO.listarInactivos(texto);
        } else {
            lista = eventoDAO.listarTodos(texto);
        }
        
        for (Evento e : lista) {
            modeloTabla.addRow(new Object[]{
                e.getId(),
                e.getNombreCliente(),
                e.getNombreEvento(),
                e.getFechaEvento(),
                "$" + formatoCOP.format(e.getPresupuestoTotal()),
                e.getEstado()
            });
        }
        
        lblTotalRegistardos.setText("Total registros: " + lista.size());
    }
    
    // ========== NUEVO EVENTO ==========
    
    private void nuevo() {
        accion = "guardar";
        eventoIdActual = 0;
        limpiar();
        cargarClientes();
        txtFechaEvento.setText(formatoFecha.format(new Date()));
        jTabbedPane1.setEnabledAt(0, false);
        jTabbedPane1.setEnabledAt(1, true);
        jTabbedPane1.setEnabledAt(2, false);
        jTabbedPane1.setEnabledAt(3, false);
        jTabbedPane1.setSelectedIndex(1);
        txtNombreEvento.requestFocus();
    }
    
    // ========== EDITAR EVENTO ==========
    
    private void editar() {
        int fila = tablaListado.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un evento", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        accion = "editar";
        idActual = Integer.parseInt(tablaListado.getValueAt(fila, 0).toString());
        eventoIdActual = idActual;
        txtId.setText(String.valueOf(idActual));
        txtNombreEvento.setText(tablaListado.getValueAt(fila, 2).toString());
        txtFechaEvento.setText(tablaListado.getValueAt(fila, 3).toString());
        
        String presupuestoStr = tablaListado.getValueAt(fila, 4).toString();
        presupuestoStr = presupuestoStr.replace("$", "").replace(",", "").trim();
        txtPresupuesto.setText(presupuestoStr);
        
        String estado = tablaListado.getValueAt(fila, 5).toString();
        cbxEstado.setSelectedItem(estado);
        
        // Seleccionar cliente
        String clienteNombre = tablaListado.getValueAt(fila, 1).toString();
        for (int i = 0; i < cbxCliente.getItemCount(); i++) {
            if (cbxCliente.getItemAt(i).contains(clienteNombre)) {
                cbxCliente.setSelectedIndex(i);
                break;
            }
        }
        
        // Cargar datos adicionales
        Evento evento = eventoDAO.buscarPorId(idActual);
        if (evento != null) {
            txtLugarEvento.setText(evento.getLugarEvento() != null ? evento.getLugarEvento() : "");
            txtInvitados.setText(String.valueOf(evento.getNumeroInvitados()));
        }
        
        // Cargar servicios del evento
        cargarServiciosDelEvento();
        
        // Cargar pagos del evento
        cargarPagosDelEvento();
        
        // Calcular totales
        calcularTotales();
        
        jTabbedPane1.setEnabledAt(0, false);
        jTabbedPane1.setEnabledAt(1, true);
        jTabbedPane1.setEnabledAt(2, true);
        jTabbedPane1.setEnabledAt(3, true);
        jTabbedPane1.setSelectedIndex(1);
        txtNombreEvento.requestFocus();
    }
    
    // ========== GUARDAR EVENTO ==========
    
    private void guardar() {
        String nombreEvento = txtNombreEvento.getText().trim();
        String fechaEvento = txtFechaEvento.getText().trim();
        String lugarEvento = txtLugarEvento.getText().trim();
        String invitadosStr = txtInvitados.getText().trim();
        String presupuestoStr = txtPresupuesto.getText().trim();
        
        if (nombreEvento.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre del evento es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            txtNombreEvento.requestFocus();
            return;
        }
        
        if (cbxCliente.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un cliente", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int clienteId = Integer.parseInt(cbxCliente.getSelectedItem().toString().split(" - ")[0]);
        int invitados = invitadosStr.isEmpty() ? 0 : Integer.parseInt(invitadosStr);
        double presupuesto = presupuestoStr.isEmpty() ? 0 : Double.parseDouble(presupuestoStr);
        String estado = cbxEstado.getSelectedItem().toString();
        
        if (accion.equals("guardar")) {
            Evento evento = new Evento();
            evento.setClienteId(clienteId);
            evento.setNombreEvento(nombreEvento);
            evento.setFechaEvento(fechaEvento);
            evento.setLugarEvento(lugarEvento);
            evento.setNumeroInvitados(invitados);
            evento.setPresupuestoTotal(presupuesto);
            evento.setEstado(estado);
            evento.setActivo(true);
            
            if (eventoDAO.insertar(evento)) {
                JOptionPane.showMessageDialog(this, "Evento guardado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                listar("");
                cancelar();
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar el evento", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            Evento evento = new Evento();
            evento.setId(idActual);
            evento.setClienteId(clienteId);
            evento.setNombreEvento(nombreEvento);
            evento.setFechaEvento(fechaEvento);
            evento.setLugarEvento(lugarEvento);
            evento.setNumeroInvitados(invitados);
            evento.setPresupuestoTotal(presupuesto);
            evento.setEstado(estado);
            evento.setActivo(true);
            
            if (eventoDAO.actualizar(evento)) {
                JOptionPane.showMessageDialog(this, "Evento actualizado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                listar("");
                cancelar();
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar el evento", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // ========== SERVICIOS DEL EVENTO ==========
    
    private void cargarServiciosDelEvento() {
        modeloTablaServicios.setRowCount(0);
        totalServicios = 0;
        
        List<Servicio> servicios = servicioDAO.listarPorEvento(eventoIdActual);
        for (Servicio s : servicios) {
            double subtotal = s.getCantidad() * s.getPrecioBase();
            modeloTablaServicios.addRow(new Object[]{
                s.getNombre(),
                s.getCantidad(),
                "$" + formatoCOP.format(s.getPrecioBase()),
                "$" + formatoCOP.format(subtotal)
            });
            totalServicios += subtotal;
        }
        
        lblTotalServicios.setText("Total Servicios: $" + formatoCOP.format(totalServicios));
    }
    
    private void agregarServicio() {
        if (eventoIdActual == 0) {
            JOptionPane.showMessageDialog(this, "Primero guarde el evento", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (cbxServicio.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un servicio", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String cantidadStr = txtCantidad.getText().trim();
        if (cantidadStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la cantidad", "Error", JOptionPane.ERROR_MESSAGE);
            txtCantidad.requestFocus();
            return;
        }
        
        int servicioId = Integer.parseInt(cbxServicio.getSelectedItem().toString().split(" - ")[0]);
        int cantidad = Integer.parseInt(cantidadStr);
        double precio = servicioDAO.obtenerPrecio(servicioId);
        
        if (servicioDAO.agregarAlEvento(eventoIdActual, servicioId, cantidad, precio)) {
            JOptionPane.showMessageDialog(this, "Servicio agregado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarServiciosDelEvento();
            calcularTotales();
            txtCantidad.setText("");
            txtPrecio.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Error al agregar servicio", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void quitarServicio() {
        int fila = tablaServicios.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un servicio", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String servicioNombre = tablaServicios.getValueAt(fila, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "¿Quitar " + servicioNombre + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Obtener el ID del servicio (necesitas implementar)
            int servicioId = 0; // Debes obtener el ID real
            if (servicioDAO.quitarDelEvento(eventoIdActual, servicioId)) {
                JOptionPane.showMessageDialog(this, "Servicio eliminado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarServiciosDelEvento();
                calcularTotales();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar servicio", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // ========== PAGOS ==========
    
    private void cargarPagosDelEvento() {
        modeloTablaPagos.setRowCount(0);
        totalPagado = 0;
        
        List<Pago> pagos = pagoDAO.listarPorEvento(eventoIdActual);
        for (Pago p : pagos) {
            modeloTablaPagos.addRow(new Object[]{
                p.getFechaPago(),
                "$" + formatoCOP.format(p.getMonto()),
                p.getMetodoPago(),
                p.getReferenciaPago() != null ? p.getReferenciaPago() : ""
            });
            totalPagado += p.getMonto();
        }
        
        lblTotalPagado.setText("Total Pagado: $" + formatoCOP.format(totalPagado));
        double saldoPendiente = totalServicios - totalPagado;
        lblSaldoPendiente.setText("Saldo Pendiente: $" + formatoCOP.format(saldoPendiente));
    }
    
    private void registrarPago() {
        if (eventoIdActual == 0) {
            JOptionPane.showMessageDialog(this, "Primero guarde el evento", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String montoStr = txtMontoPago.getText().trim();
        if (montoStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el monto", "Error", JOptionPane.ERROR_MESSAGE);
            txtMontoPago.requestFocus();
            return;
        }
        
        double monto = Double.parseDouble(montoStr);
        String metodoPago = cbxMetodoPago.getSelectedItem().toString();
        String referencia = txtReferencia.getText().trim();
        String fechaPago = formatoFecha.format(new Date());
        
        Pago pago = new Pago();
        pago.setEventoId(eventoIdActual);
        pago.setFechaPago(fechaPago);
        pago.setMonto(monto);
        pago.setMetodoPago(metodoPago);
        pago.setReferenciaPago(referencia);
        pago.setActivo(true);
        
        if (pagoDAO.insertar(pago)) {
            JOptionPane.showMessageDialog(this, "Pago registrado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarPagosDelEvento();
            calcularTotales();
            txtMontoPago.setText("");
            txtReferencia.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar pago", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void calcularTotales() {
        lblTotalServicios.setText("Total Servicios: $" + formatoCOP.format(totalServicios));
        lblTotalPagado.setText("Total Pagado: $" + formatoCOP.format(totalPagado));
        double saldoPendiente = totalServicios - totalPagado;
        lblSaldoPendiente.setText("Saldo Pendiente: $" + formatoCOP.format(saldoPendiente));
    }
    
    // ========== ACTIVAR/DESACTIVAR ==========
    
    private void activar() {
        int fila = tablaListado.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un evento", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        idActual = Integer.parseInt(tablaListado.getValueAt(fila, 0).toString());
        String nombre = tablaListado.getValueAt(fila, 2).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this, "¿Activar evento: " + nombre + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION && eventoDAO.activar(idActual)) {
            JOptionPane.showMessageDialog(this, "Evento activado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            listar("");
        }
    }
    
    private void desactivar() {
        int fila = tablaListado.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un evento", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        idActual = Integer.parseInt(tablaListado.getValueAt(fila, 0).toString());
        String nombre = tablaListado.getValueAt(fila, 2).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this, "¿Desactivar evento: " + nombre + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION && eventoDAO.desactivar(idActual)) {
            JOptionPane.showMessageDialog(this, "Evento desactivado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            listar("");
        }
    }
    
    // ========== CANCELAR Y LIMPIAR ==========
    
    private void cancelar() {
        limpiar();
        jTabbedPane1.setEnabledAt(0, true);
        jTabbedPane1.setEnabledAt(1, false);
        jTabbedPane1.setEnabledAt(2, false);
        jTabbedPane1.setEnabledAt(3, false);
        jTabbedPane1.setSelectedIndex(0);
        accion = "guardar";
        eventoIdActual = 0;
    }
    
    private void limpiar() {
        txtId.setText("");
        txtNombreEvento.setText("");
        txtFechaEvento.setText(formatoFecha.format(new Date()));
        txtLugarEvento.setText("");
        txtInvitados.setText("");
        txtPresupuesto.setText("");
        txtCantidad.setText("");
        txtPrecio.setText("");
        txtMontoPago.setText("");
        txtReferencia.setText("");
        cbxCliente.setSelectedIndex(-1);
        cbxEstado.setSelectedIndex(0);
        modeloTablaServicios.setRowCount(0);
        modeloTablaPagos.setRowCount(0);
        totalServicios = 0;
        totalPagado = 0;
        calcularTotales();
    }
    

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        lblBuscar = new javax.swing.JLabel();
        btnBuscar = new javax.swing.JButton();
        btnNuevo = new javax.swing.JButton();
        btnEditar = new javax.swing.JButton();
        btnActivar = new javax.swing.JButton();
        btnDesactivar = new javax.swing.JButton();
        lblTotalRegistardos = new javax.swing.JLabel();
        scrollTabla = new javax.swing.JScrollPane();
        tablaListado = new javax.swing.JTable();
        cbxFiltrado = new javax.swing.JComboBox<>();
        txtBuscar = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        lblId = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        lblCliente = new javax.swing.JLabel();
        cbxCliente = new javax.swing.JComboBox<>();
        lblNombreEvento = new javax.swing.JLabel();
        txtNombreEvento = new javax.swing.JTextField();
        lblFechaEvento = new javax.swing.JLabel();
        txtFechaEvento = new javax.swing.JTextField();
        lblLugarEvento = new javax.swing.JLabel();
        txtLugarEvento = new javax.swing.JTextField();
        lblInvitados = new javax.swing.JLabel();
        txtInvitados = new javax.swing.JTextField();
        lblPresupuesto = new javax.swing.JLabel();
        txtPresupuesto = new javax.swing.JTextField();
        lblEstado = new javax.swing.JLabel();
        cbxEstado = new javax.swing.JComboBox<>();
        btnGuardar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        lblObligatorio = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        lblServicio = new javax.swing.JLabel();
        cbxServicio = new javax.swing.JComboBox<>();
        lblCantidad = new javax.swing.JLabel();
        txtCantidad = new javax.swing.JTextField();
        lblPrecio = new javax.swing.JLabel();
        txtPrecio = new javax.swing.JTextField();
        btnAgregarServicio = new javax.swing.JButton();
        btnQuitarServicio = new javax.swing.JButton();
        scrollTablaServicios = new javax.swing.JScrollPane();
        tablaServicios = new javax.swing.JTable();
        lblTotalServicios = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        lblMontoPago = new javax.swing.JLabel();
        txtMontoPago = new javax.swing.JTextField();
        lblMetodoPago = new javax.swing.JLabel();
        cbxMetodoPago = new javax.swing.JComboBox<>();
        lblReferencia = new javax.swing.JLabel();
        txtReferencia = new javax.swing.JTextField();
        btnRegistrarPago = new javax.swing.JButton();
        scrollTablaPagos = new javax.swing.JScrollPane();
        tablaPagos = new javax.swing.JTable();
        lblSaldoPendiente = new javax.swing.JLabel();
        lblTotalPagado = new javax.swing.JLabel();

        lblBuscar.setText("Buscar");

        btnBuscar.setText("Buscar");

        btnNuevo.setText("Nuevo");

        btnEditar.setText("Editar");

        btnActivar.setText("Activar");

        btnDesactivar.setText("Desactivar");

        lblTotalRegistardos.setText("Total Registros: 0");

        tablaListado.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        scrollTabla.setViewportView(tablaListado);

        cbxFiltrado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Activo", "Inactivo", "Todos" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnActivar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnDesactivar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblTotalRegistardos)
                        .addGap(143, 143, 143))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(lblBuscar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 464, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnBuscar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnNuevo)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnEditar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cbxFiltrado, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(scrollTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 1500, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(27, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBuscar)
                    .addComponent(btnBuscar)
                    .addComponent(btnNuevo)
                    .addComponent(btnEditar)
                    .addComponent(cbxFiltrado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(scrollTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 346, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnActivar)
                        .addComponent(btnDesactivar))
                    .addComponent(lblTotalRegistardos))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Listado", jPanel1);

        lblId.setText("ID:");

        lblCliente.setText("Cliente (*):");

        lblNombreEvento.setText("Nombre del evento (*):");

        lblFechaEvento.setText("Fecha evento:");

        lblLugarEvento.setText("Lugar:");

        lblInvitados.setText("N° Invitados:");

        lblPresupuesto.setText("Presupuesto:");

        lblEstado.setText("Estado:");

        cbxEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Cotizando", "Confirmado", "Realizado", "Cancelado" }));

        btnGuardar.setText("Guardar");

        btnCancelar.setText("Cancelar");

        lblObligatorio.setText("(*) Campos obligatorios\"");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblObligatorio)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lblLugarEvento)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtLugarEvento, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblInvitados)
                        .addGap(18, 18, 18)
                        .addComponent(txtInvitados, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblPresupuesto)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtPresupuesto, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblEstado)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbxEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(lblId)
                                .addGap(18, 18, 18)
                                .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(lblCliente)
                                .addGap(18, 18, 18)
                                .addComponent(cbxCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(lblNombreEvento)
                                .addGap(18, 18, 18)
                                .addComponent(txtNombreEvento, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(lblFechaEvento))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btnGuardar)
                                .addGap(18, 18, 18)
                                .addComponent(btnCancelar)))
                        .addGap(18, 18, 18)
                        .addComponent(txtFechaEvento, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(582, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblId)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCliente)
                    .addComponent(cbxCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNombreEvento)
                    .addComponent(txtNombreEvento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFechaEvento)
                    .addComponent(txtFechaEvento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(164, 164, 164)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLugarEvento)
                    .addComponent(txtLugarEvento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblInvitados)
                    .addComponent(txtInvitados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPresupuesto)
                    .addComponent(txtPresupuesto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblEstado)
                    .addComponent(cbxEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 202, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuardar)
                    .addComponent(btnCancelar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblObligatorio)
                .addGap(17, 17, 17))
        );

        jTabbedPane1.addTab("Mantenimiento", jPanel2);

        lblServicio.setText("Servicio:");

        lblCantidad.setText("Cantidad:");

        lblPrecio.setText("Precio:");

        btnAgregarServicio.setText("Agregar");

        btnQuitarServicio.setText("Quitar");

        tablaServicios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Servicio", "Cantidad", "Precio", "Subtotal"
            }
        ));
        scrollTablaServicios.setViewportView(tablaServicios);

        lblTotalServicios.setText("Total Servicios");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblServicio)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbxServicio, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblCantidad)
                        .addGap(18, 18, 18)
                        .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblPrecio)
                        .addGap(18, 18, 18)
                        .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnAgregarServicio)
                        .addGap(18, 18, 18)
                        .addComponent(btnQuitarServicio))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTotalServicios)
                            .addComponent(scrollTablaServicios, javax.swing.GroupLayout.PREFERRED_SIZE, 1009, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(525, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cbxServicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblCantidad)
                        .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblPrecio)
                        .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnAgregarServicio)
                        .addComponent(btnQuitarServicio))
                    .addComponent(lblServicio))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scrollTablaServicios, javax.swing.GroupLayout.PREFERRED_SIZE, 373, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTotalServicios)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Servicios de Eventos", jPanel3);

        lblMontoPago.setText("Monto:");

        lblMetodoPago.setText("Método");

        cbxMetodoPago.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Efectivo", "Transferencia", "Tarjeta" }));

        lblReferencia.setText("Referencia:");

        btnRegistrarPago.setText("Registrar Pago");

        tablaPagos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Fecha", "Monto", "Método", "Referencia"
            }
        ));
        scrollTablaPagos.setViewportView(tablaPagos);

        lblSaldoPendiente.setText("Saldo Pendiente: $0.00");

        lblTotalPagado.setText("Total Pagado: $0.00");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollTablaPagos)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(lblMontoPago)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtMontoPago, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(lblMetodoPago)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cbxMetodoPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(lblReferencia)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtReferencia, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnRegistrarPago))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(lblSaldoPendiente)
                                .addGap(77, 77, 77)
                                .addComponent(lblTotalPagado)))
                        .addGap(0, 880, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMontoPago)
                    .addComponent(txtMontoPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblMetodoPago)
                    .addComponent(cbxMetodoPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblReferencia)
                    .addComponent(txtReferencia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRegistrarPago))
                .addGap(18, 18, 18)
                .addComponent(scrollTablaPagos, javax.swing.GroupLayout.PREFERRED_SIZE, 401, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSaldoPendiente)
                    .addComponent(lblTotalPagado))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Pagos", jPanel4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActivar;
    private javax.swing.JButton btnAgregarServicio;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnDesactivar;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JButton btnQuitarServicio;
    private javax.swing.JButton btnRegistrarPago;
    private javax.swing.JComboBox<String> cbxCliente;
    private javax.swing.JComboBox<String> cbxEstado;
    private javax.swing.JComboBox<String> cbxFiltrado;
    private javax.swing.JComboBox<String> cbxMetodoPago;
    private javax.swing.JComboBox<String> cbxServicio;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JLabel lblCantidad;
    private javax.swing.JLabel lblCliente;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblFechaEvento;
    private javax.swing.JLabel lblId;
    private javax.swing.JLabel lblInvitados;
    private javax.swing.JLabel lblLugarEvento;
    private javax.swing.JLabel lblMetodoPago;
    private javax.swing.JLabel lblMontoPago;
    private javax.swing.JLabel lblNombreEvento;
    private javax.swing.JLabel lblObligatorio;
    private javax.swing.JLabel lblPrecio;
    private javax.swing.JLabel lblPresupuesto;
    private javax.swing.JLabel lblReferencia;
    private javax.swing.JLabel lblSaldoPendiente;
    private javax.swing.JLabel lblServicio;
    private javax.swing.JLabel lblTotalPagado;
    private javax.swing.JLabel lblTotalRegistardos;
    private javax.swing.JLabel lblTotalServicios;
    private javax.swing.JScrollPane scrollTabla;
    private javax.swing.JScrollPane scrollTablaPagos;
    private javax.swing.JScrollPane scrollTablaServicios;
    private javax.swing.JTable tablaListado;
    private javax.swing.JTable tablaPagos;
    private javax.swing.JTable tablaServicios;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtCantidad;
    private javax.swing.JTextField txtFechaEvento;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtInvitados;
    private javax.swing.JTextField txtLugarEvento;
    private javax.swing.JTextField txtMontoPago;
    private javax.swing.JTextField txtNombreEvento;
    private javax.swing.JTextField txtPrecio;
    private javax.swing.JTextField txtPresupuesto;
    private javax.swing.JTextField txtReferencia;
    // End of variables declaration//GEN-END:variables
}
