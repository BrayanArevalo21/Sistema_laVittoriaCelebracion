package entidades;

public class Evento {
    private int id;
    private int clienteId;
    private String nombreCliente; // Para mostrar en listados
    private String nombreEvento;
    private String fechaEvento;
    private String lugarEvento;
    private int numeroInvitados;
    private double presupuestoTotal;
    private String estado;
    private boolean activo;

    public Evento() {}

    public Evento(int id, int clienteId, String nombreEvento, String fechaEvento, 
                  String lugarEvento, int numeroInvitados, double presupuestoTotal, 
                  String estado, boolean activo) {
        this.id = id;
        this.clienteId = clienteId;
        this.nombreEvento = nombreEvento;
        this.fechaEvento = fechaEvento;
        this.lugarEvento = lugarEvento;
        this.numeroInvitados = numeroInvitados;
        this.presupuestoTotal = presupuestoTotal;
        this.estado = estado;
        this.activo = activo;
    }

    // Getters y Setters
    public int getId() { 
        return id; 
    }
    public void setId(int id) { 
        this.id = id; 
    }
    public int getClienteId() { 
        return clienteId; 
    }
    public void setClienteId(int clienteId) { 
        this.clienteId = clienteId; 
    }
    public String getNombreCliente() { 
        return nombreCliente; 
    }
    public void setNombreCliente(String nombreCliente) { 
        this.nombreCliente = nombreCliente; 
    }
    public String getNombreEvento() { 
        return nombreEvento; 
    }
    public void setNombreEvento(String nombreEvento) { 
        this.nombreEvento = nombreEvento; 
    }
    public String getFechaEvento() { 
        return fechaEvento; 
    }
    public void setFechaEvento(String fechaEvento) { 
        this.fechaEvento = fechaEvento; 
    }
    public String getLugarEvento() { 
        return lugarEvento; 
    }
    public void setLugarEvento(String lugarEvento) { 
        this.lugarEvento = lugarEvento; 
    }
    public int getNumeroInvitados() { 
        return numeroInvitados; 
    }
    public void setNumeroInvitados(int numeroInvitados) { 
        this.numeroInvitados = numeroInvitados; 
    }
    public double getPresupuestoTotal() { 
        return presupuestoTotal; 
    }
    public void setPresupuestoTotal(double presupuestoTotal) { 
        this.presupuestoTotal = presupuestoTotal; 
    }
    public String getEstado() { 
        return estado; 
    }
    public void setEstado(String estado) { 
        this.estado = estado; 
    }
    public boolean isActivo() { 
        return activo; 
    }
    public void setActivo(boolean activo) { 
        this.activo = activo; 
    }
}
