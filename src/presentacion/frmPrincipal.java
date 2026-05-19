package presentacion;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JDesktopPane;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;

public class frmPrincipal extends JFrame {
    
    // ========== COMPONENTES ==========
    private JDesktopPane escritorio;
  
    
    public frmPrincipal() {
        initComponentes();
        setExtendedState(MAXIMIZED_BOTH);
        setTitle("La Vittoria Celebraciones - Sistema de Gestión de Eventos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    
    private void initComponentes() {
        // ========== ESCRITORIO ==========
        escritorio = new JDesktopPane() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(102, 0, 102)); // Morado corporativo
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        setContentPane(escritorio);
        
        // ========== BARRA DE MENÚ ==========
        menuBar = new JMenuBar();
        
        // ----- MENÚ CATÁLOGO -----
        mnuCatalogo = new JMenu("Catálogo");
        
        mnuTipoServicio = new JMenuItem("Tipos de Servicio");
        mnuTipoServicio.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.CTRL_DOWN_MASK));
        mnuTipoServicio.addActionListener(e -> abrirFrmTipoServicio());
        mnuCatalogo.add(mnuTipoServicio);
        
        mnuServicio = new JMenuItem("Servicios");
        mnuServicio.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        mnuServicio.addActionListener(e -> abrirFrmServicio());
        mnuCatalogo.add(mnuServicio);
        
        mnuAlimento = new JMenuItem("Alimentos/Bebidas");
        mnuAlimento.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK));
        mnuAlimento.addActionListener(e -> abrirFrmAlimento());
        mnuCatalogo.add(mnuAlimento);
        
        mnuProveedor = new JMenuItem("Proveedores");
        mnuProveedor.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK));
        mnuProveedor.addActionListener(e -> abrirFrmProveedor());
        mnuCatalogo.add(mnuProveedor);
        
        mnuCatalogo.addSeparator();
        
        mnuSalir = new JMenuItem("Salir");
        mnuSalir.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK));
        mnuSalir.addActionListener(e -> salir());
        mnuCatalogo.add(mnuSalir);
        
        menuBar.add(mnuCatalogo);
        
        // ----- MENÚ EVENTOS -----
        mnuEventos = new JMenu("Eventos");
        
        mnuCliente = new JMenuItem("Clientes");
        mnuCliente.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));
        mnuCliente.addActionListener(e -> abrirFrmCliente());
        mnuEventos.add(mnuCliente);
        
        mnuEvento = new JMenuItem("Eventos");
        mnuEvento.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK));
        mnuEvento.addActionListener(e -> abrirFrmEvento());
        mnuEventos.add(mnuEvento);
        
        mnuPago = new JMenuItem("Pagos");
        mnuPago.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, KeyEvent.CTRL_DOWN_MASK));
        mnuPago.addActionListener(e -> abrirFrmPago());
        mnuEventos.add(mnuPago);
        
        menuBar.add(mnuEventos);
        
        // ----- MENÚ PERSONAL -----
        mnuPersonal = new JMenu("Personal");
        
        mnuPersonalMontaje = new JMenuItem("Personal de Montaje");
        mnuPersonalMontaje.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, KeyEvent.CTRL_DOWN_MASK));
        mnuPersonalMontaje.addActionListener(e -> abrirFrmPersonalMontaje());
        mnuPersonal.add(mnuPersonalMontaje);
        
        mnuAsignacion = new JMenuItem("Asignaciones");
        mnuAsignacion.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
        mnuAsignacion.addActionListener(e -> abrirFrmAsignacion());
        mnuPersonal.add(mnuAsignacion);
        
        menuBar.add(mnuPersonal);
        
        // ----- MENÚ ACCESO -----
        mnuAcceso = new JMenu("Acceso");
        
        mnuRol = new JMenuItem("Roles");
        mnuRol.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK));
        mnuRol.addActionListener(e -> abrirFrmRol());
        mnuAcceso.add(mnuRol);
        
        mnuUsuario = new JMenuItem("Usuarios");
        mnuUsuario.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, KeyEvent.CTRL_DOWN_MASK));
        mnuUsuario.addActionListener(e -> abrirFrmUsuario());
        mnuAcceso.add(mnuUsuario);
        
        menuBar.add(mnuAcceso);
        
        // ----- MENÚ CONSULTAS -----
        mnuConsultas = new JMenu("Consultas");
        
        mnuReporteEventos = new JMenuItem("Reporte de Eventos");
        mnuReporteEventos.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_DOWN_MASK));
        mnuReporteEventos.addActionListener(e -> abrirFrmReporteEventos());
        mnuConsultas.add(mnuReporteEventos);
        
        mnuReportePagos = new JMenuItem("Reporte de Pagos");
        mnuReportePagos.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_DOWN_MASK));
        mnuReportePagos.addActionListener(e -> abrirFrmReportePagos());
        mnuConsultas.add(mnuReportePagos);
        
        menuBar.add(mnuConsultas);
        
        setJMenuBar(menuBar);
    }
    
    // ========== MÉTODOS PARA ABRIR VENTANAS ==========
    
    private void abrirFrmTipoServicio() {
        FrmTipoServicio frm = new FrmTipoServicio();
        escritorio.add(frm);
        centrarVentana(frm);
        frm.setVisible(true);
    }
    
    private void abrirFrmServicio() {
        frmServicio frm = new frmServicio();
        escritorio.add(frm);
        centrarVentana(frm);
        frm.setVisible(true);
    }
    
    private void abrirFrmAlimento() {
        frmAlimento frm = new frmAlimento();
        escritorio.add(frm);
        centrarVentana(frm);
        frm.setVisible(true);
    }
    
    private void abrirFrmProveedor() {
        frmProveedor frm = new frmProveedor();
        escritorio.add(frm);
        centrarVentana(frm);
        frm.setVisible(true);
    }
    
    private void abrirFrmCliente() {
        frmCliente frm = new frmCliente();
        escritorio.add(frm);
        centrarVentana(frm);
        frm.setVisible(true);
    }
    
    private void abrirFrmEvento() {
        frmEvento frm = new frmEvento();
        escritorio.add(frm);
        centrarVentana(frm);
        frm.setVisible(true);
    }
    
    private void abrirFrmPago() {
        frmPago frm = new frmPago();
        escritorio.add(frm);
        centrarVentana(frm);
        frm.setVisible(true);
    }
    
    private void abrirFrmPersonalMontaje() {
        frmPersonalMontaje frm = new frmPersonalMontaje();
        escritorio.add(frm);
        centrarVentana(frm);
        frm.setVisible(true);
    }
    
    private void abrirFrmAsignacion() {
        frmAsignacionMontaje frm = new frmAsignacionMontaje();
        escritorio.add(frm);
        centrarVentana(frm);
        frm.setVisible(true);
    }
    
    private void abrirFrmRol() {
        frmRol frm = new frmRol();
        escritorio.add(frm);
        centrarVentana(frm);
        frm.setVisible(true);
    }
    
    private void abrirFrmUsuario() {
        frmUsuario frm = new frmUsuario();
        escritorio.add(frm);
        centrarVentana(frm);
        frm.setVisible(true);
    }
    
    private void abrirFrmReporteEventos() {
        frmReporteEventos frm = new frmReporteEventos();
        escritorio.add(frm);
        centrarVentana(frm);
        frm.setVisible(true);
    }
    
    private void abrirFrmReportePagos() {
        frmReportePagos frm = new frmReportePagos();
        escritorio.add(frm);
        centrarVentana(frm);
        frm.setVisible(true);
    }
    
    private void salir() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro que desea salir del sistema?", 
            "Confirmar Salida", 
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
    
    private void centrarVentana(javax.swing.JInternalFrame frm) {
        int x = (escritorio.getWidth() - frm.getWidth()) / 2;
        int y = (escritorio.getHeight() - frm.getHeight()) / 2;
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        frm.setLocation(x, y);
    }
    
    // ========== MAIN ==========
    public static void main(String[] args) {
        // Estilo visual Nimbus (opcional)
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Iniciar aplicación
        java.awt.EventQueue.invokeLater(() -> {
            new frmPrincipal();
        });
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        desktopPane = new javax.swing.JDesktopPane();
        menuBar = new javax.swing.JMenuBar();
        mnuCatalogo = new javax.swing.JMenu();
        mnuTipoServicio = new javax.swing.JMenuItem();
        mnuServicio = new javax.swing.JMenuItem();
        mnuAlimento = new javax.swing.JMenuItem();
        mnuProveedor = new javax.swing.JMenuItem();
        mnuSalir = new javax.swing.JMenuItem();
        mnuEventos = new javax.swing.JMenu();
        mnuCliente = new javax.swing.JMenuItem();
        mnuEvento = new javax.swing.JMenuItem();
        mnuPago = new javax.swing.JMenuItem();
        mnuPersonal = new javax.swing.JMenu();
        mnuPersonalMontaje = new javax.swing.JMenuItem();
        mnuAsignacion = new javax.swing.JMenuItem();
        mnuAcceso = new javax.swing.JMenu();
        mnuRol = new javax.swing.JMenuItem();
        mnuUsuario = new javax.swing.JMenuItem();
        mnuConsultas = new javax.swing.JMenu();
        mnuReporteEventos = new javax.swing.JMenuItem();
        mnuReportePagos = new javax.swing.JMenuItem();
        mnusalir = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        mnuCatalogo.setText("Catálogo");

        mnuTipoServicio.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuTipoServicio.setText("Tipo de Servicio");
        mnuCatalogo.add(mnuTipoServicio);

        mnuServicio.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuServicio.setText("Servicios");
        mnuCatalogo.add(mnuServicio);

        mnuAlimento.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuAlimento.setText("Alimento/Bebidas");
        mnuCatalogo.add(mnuAlimento);

        mnuProveedor.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuProveedor.setText("Proveedor");
        mnuCatalogo.add(mnuProveedor);

        mnuSalir.setText("Salir");
        mnuCatalogo.add(mnuSalir);

        menuBar.add(mnuCatalogo);

        mnuEventos.setText("Eventos");

        mnuCliente.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuCliente.setText("Clientes");
        mnuEventos.add(mnuCliente);

        mnuEvento.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuEvento.setText("Eventos");
        mnuEventos.add(mnuEvento);

        mnuPago.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuPago.setText("Pagos");
        mnuEventos.add(mnuPago);

        menuBar.add(mnuEventos);

        mnuPersonal.setText("Personal");

        mnuPersonalMontaje.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuPersonalMontaje.setText("Personal Montaje");
        mnuPersonal.add(mnuPersonalMontaje);

        mnuAsignacion.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuAsignacion.setText("Asignaciones");
        mnuPersonal.add(mnuAsignacion);

        menuBar.add(mnuPersonal);

        mnuAcceso.setText("Acceso");

        mnuRol.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuRol.setText("Roles");
        mnuAcceso.add(mnuRol);

        mnuUsuario.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuUsuario.setText("Usuarios");
        mnuAcceso.add(mnuUsuario);

        menuBar.add(mnuAcceso);

        mnuConsultas.setText("Consultas");

        mnuReporteEventos.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuReporteEventos.setText("Reporte de Eventos");
        mnuConsultas.add(mnuReporteEventos);

        mnuReportePagos.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuReportePagos.setText("Reporte de Pagos ");
        mnuConsultas.add(mnuReportePagos);

        menuBar.add(mnuConsultas);

        mnusalir.setText("Salir");
        menuBar.add(mnusalir);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(desktopPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1060, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(desktopPane, javax.swing.GroupLayout.DEFAULT_SIZE, 647, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDesktopPane desktopPane;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu mnuAcceso;
    private javax.swing.JMenuItem mnuAlimento;
    private javax.swing.JMenuItem mnuAsignacion;
    private javax.swing.JMenu mnuCatalogo;
    private javax.swing.JMenuItem mnuCliente;
    private javax.swing.JMenu mnuConsultas;
    private javax.swing.JMenuItem mnuEvento;
    private javax.swing.JMenu mnuEventos;
    private javax.swing.JMenuItem mnuPago;
    private javax.swing.JMenu mnuPersonal;
    private javax.swing.JMenuItem mnuPersonalMontaje;
    private javax.swing.JMenuItem mnuProveedor;
    private javax.swing.JMenuItem mnuReporteEventos;
    private javax.swing.JMenuItem mnuReportePagos;
    private javax.swing.JMenuItem mnuRol;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JMenuItem mnuServicio;
    private javax.swing.JMenuItem mnuTipoServicio;
    private javax.swing.JMenuItem mnuUsuario;
    private javax.swing.JMenu mnusalir;
    // End of variables declaration//GEN-END:variables

}
