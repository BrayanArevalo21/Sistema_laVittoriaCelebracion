package presentacion;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JDesktopPane;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class FrmPrincipal extends JFrame {
    
    
    public FrmPrincipal() {
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
        
        // ========== MENÚ PRINCIPAL ==========
        menuBar = new JMenuBar();
        
        // ----- MENÚ CATÁLOGO -----
        mnuCatalogo = new JMenu("Catálogo");
        
        mnuTipoServicio = new JMenuItem("Tipos de Servicio");
        mnuTipoServicio.addActionListener(e -> abrirFrmTipoServicio());
        mnuCatalogo.add(mnuTipoServicio);
        
        mnuServicio = new JMenuItem("Servicios");
        mnuServicio.addActionListener(e -> abrirFrmServicio());
        mnuCatalogo.add(mnuServicio);
        
        mnuAlimento = new JMenuItem("Alimentos/Bebidas");
        mnuAlimento.addActionListener(e -> abrirFrmAlimento());
        mnuCatalogo.add(mnuAlimento);
        
        menuBar.add(mnuCatalogo);
        
        // ----- MENÚ EVENTOS -----
        mnuEventos = new JMenu("Eventos");
        
        mnuCliente = new JMenuItem("Clientes");
        mnuCliente.addActionListener(e -> abrirFrmCliente());
        mnuEventos.add(mnuCliente);
        
        mnuEvento = new JMenuItem("Eventos");
        mnuEvento.addActionListener(e -> abrirFrmEvento());
        mnuEventos.add(mnuEvento);
        
        mnuPago = new JMenuItem("Pagos");
        mnuPago.addActionListener(e -> abrirFrmPago());
        mnuEventos.add(mnuPago);
        
        menuBar.add(mnuEventos);
        
        // ----- MENÚ PERSONAL -----
        mnuPersonal = new JMenu("Personal");
        
        mnuPersonalMontaje = new JMenuItem("Personal de Montaje");
        mnuPersonalMontaje.addActionListener(e -> abrirFrmPersonalMontaje());
        mnuPersonal.add(mnuPersonalMontaje);
        
        mnuAsignacion = new JMenuItem("Asignaciones");
        mnuAsignacion.addActionListener(e -> abrirFrmAsignacion());
        mnuPersonal.add(mnuAsignacion);
        
        menuBar.add(mnuPersonal);
        
        // ----- MENÚ ACCESO -----
        mnuAcceso = new JMenu("Acceso");
        
        mnuRol = new JMenuItem("Roles");
        mnuRol.addActionListener(e -> abrirFrmRol());
        mnuAcceso.add(mnuRol);
        
        mnuUsuario = new JMenuItem("Usuarios");
        mnuUsuario.addActionListener(e -> abrirFrmUsuario());
        mnuAcceso.add(mnuUsuario);
        
        menuBar.add(mnuAcceso);
        
        // ----- MENÚ CONSULTAS -----
        mnuConsultas = new JMenu("Consultas");
        
        mnuReporteEventos = new JMenuItem("Reporte de Eventos");
        mnuReporteEventos.addActionListener(e -> abrirFrmReporteEventos());
        mnuConsultas.add(mnuReporteEventos);
        
        mnuReportePagos = new JMenuItem("Reporte de Pagos");
        mnuReportePagos.addActionListener(e -> abrirFrmReportePagos());
        mnuConsultas.add(mnuReportePagos);
        
        menuBar.add(mnuConsultas);
        
        // ----- MENÚ SALIR -----
        mnuSalir = new JMenu("Salir");
        mnuSalir.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                salir();
            }
        });
        menuBar.add(mnuSalir);
        
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
        // FrmServicio frm = new FrmServicio();
        // escritorio.add(frm);
        // centrarVentana(frm);
        // frm.setVisible(true);
        JOptionPane.showMessageDialog(this, "Próximamente: Servicios");
    }
    
    private void abrirFrmAlimento() {
        JOptionPane.showMessageDialog(this, "Próximamente: Alimentos/Bebidas");
    }
    
    private void abrirFrmCliente() {
        JOptionPane.showMessageDialog(this, "Próximamente: Clientes");
    }
    
    private void abrirFrmEvento() {
        JOptionPane.showMessageDialog(this, "Próximamente: Eventos");
    }
    
    private void abrirFrmPago() {
        JOptionPane.showMessageDialog(this, "Próximamente: Pagos");
    }
    
    private void abrirFrmPersonalMontaje() {
        JOptionPane.showMessageDialog(this, "Próximamente: Personal de Montaje");
    }
    
    private void abrirFrmAsignacion() {
        JOptionPane.showMessageDialog(this, "Próximamente: Asignaciones");
    }
    
    private void abrirFrmRol() {
        JOptionPane.showMessageDialog(this, "Próximamente: Roles");
    }
    
    private void abrirFrmUsuario() {
        JOptionPane.showMessageDialog(this, "Próximamente: Usuarios");
    }
    
    private void abrirFrmReporteEventos() {
        JOptionPane.showMessageDialog(this, "Próximamente: Reporte de Eventos");
    }
    
    private void abrirFrmReportePagos() {
        JOptionPane.showMessageDialog(this, "Próximamente: Reporte de Pagos");
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
    
    private void centrarVentana(JInternalFrame frm) {
        int x = (escritorio.getWidth() - frm.getWidth()) / 2;
        int y = (escritorio.getHeight() - frm.getHeight()) / 2;
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
            new FrmPrincipal();
        });
    }
}
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Escritorio = new javax.swing.JDesktopPane(){
            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                g.setColor(Color.DARK_GRAY);
                g.fillRect(0,0, getWidth(), getHeight());
            }
        };
        menuBar = new javax.swing.JMenuBar();
        mnuCatalogo = new javax.swing.JMenu();
        mnuTipoServicio = new javax.swing.JMenuItem();
        mnuServicio = new javax.swing.JMenuItem();
        mnuAlimento = new javax.swing.JMenuItem();
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
        mnuSalir = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Sistema de Compras y Ventas");

        mnuCatalogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/presentacion/images/almacen.png"))); // NOI18N
        mnuCatalogo.setText("Catalogo");
        mnuCatalogo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCatalogoActionPerformed(evt);
            }
        });

        mnuTipoServicio.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.ALT_DOWN_MASK));
        mnuTipoServicio.setText("Tipo Servicio");
        mnuTipoServicio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTipoServicioActionPerformed(evt);
            }
        });
        mnuCatalogo.add(mnuTipoServicio);

        mnuServicio.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.ALT_DOWN_MASK));
        mnuServicio.setText("Servicio");
        mnuServicio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuServicioActionPerformed(evt);
            }
        });
        mnuCatalogo.add(mnuServicio);

        mnuAlimento.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.ALT_DOWN_MASK));
        mnuAlimento.setText("Alimento");
        mnuCatalogo.add(mnuAlimento);

        menuBar.add(mnuCatalogo);

        mnuEventos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/presentacion/images/compras.png"))); // NOI18N
        mnuEventos.setText("Eventos");

        mnuCliente.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.ALT_DOWN_MASK));
        mnuCliente.setText("Cliente");
        mnuEventos.add(mnuCliente);

        mnuEvento.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.ALT_DOWN_MASK));
        mnuEvento.setText("Evento");
        mnuEventos.add(mnuEvento);

        mnuPago.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.ALT_DOWN_MASK));
        mnuPago.setText("Pago");
        mnuEventos.add(mnuPago);

        menuBar.add(mnuEventos);

        mnuPersonal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/presentacion/images/ventas.png"))); // NOI18N
        mnuPersonal.setText("Personal");

        mnuPersonalMontaje.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.ALT_DOWN_MASK));
        mnuPersonalMontaje.setText("Personal Montaje");
        mnuPersonal.add(mnuPersonalMontaje);

        mnuAsignacion.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.ALT_DOWN_MASK));
        mnuAsignacion.setText("Asignacion Montaje");
        mnuPersonal.add(mnuAsignacion);

        menuBar.add(mnuPersonal);

        mnuAcceso.setIcon(new javax.swing.ImageIcon(getClass().getResource("/presentacion/images/acceso.png"))); // NOI18N
        mnuAcceso.setText("Acceso");

        mnuRol.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.ALT_DOWN_MASK));
        mnuRol.setText("Roles");
        mnuAcceso.add(mnuRol);

        mnuUsuario.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.ALT_DOWN_MASK));
        mnuUsuario.setText("Usuario");
        mnuAcceso.add(mnuUsuario);

        menuBar.add(mnuAcceso);

        mnuConsultas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/presentacion/images/consultas.png"))); // NOI18N
        mnuConsultas.setText("Consultas");

        mnuReporteEventos.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.ALT_DOWN_MASK));
        mnuReporteEventos.setText("Reporte de Eventos");
        mnuConsultas.add(mnuReporteEventos);

        mnuReportePagos.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.ALT_DOWN_MASK));
        mnuReportePagos.setText("Reporte de pagos");
        mnuReportePagos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuReportePagosActionPerformed(evt);
            }
        });
        mnuConsultas.add(mnuReportePagos);

        menuBar.add(mnuConsultas);

        mnuSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/presentacion/images/salir.png"))); // NOI18N
        mnuSalir.setText("Salir");
        menuBar.add(mnuSalir);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Escritorio, javax.swing.GroupLayout.DEFAULT_SIZE, 1022, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Escritorio, javax.swing.GroupLayout.DEFAULT_SIZE, 473, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mnuTipoServicioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTipoServicioActionPerformed
  
    }//GEN-LAST:event_mnuTipoServicioActionPerformed

    private void mnuCatalogoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCatalogoActionPerformed
     
    }//GEN-LAST:event_mnuCatalogoActionPerformed

    private void mnuServicioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuServicioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mnuServicioActionPerformed

    private void mnuReportePagosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuReportePagosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mnuReportePagosActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDesktopPane Escritorio;
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
    private javax.swing.JMenuItem mnuReporteEventos;
    private javax.swing.JMenuItem mnuReportePagos;
    private javax.swing.JMenuItem mnuRol;
    private javax.swing.JMenu mnuSalir;
    private javax.swing.JMenuItem mnuServicio;
    private javax.swing.JMenuItem mnuTipoServicio;
    private javax.swing.JMenuItem mnuUsuario;
    // End of variables declaration//GEN-END:variables

}
