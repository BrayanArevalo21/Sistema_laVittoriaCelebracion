package datos.interfaces;

import java.util.List;
import java.util.Date;
import java.util.Map;

public interface ReporteInterface<T> {
    
    // Reportes básicos
    public List<T> reporteGeneral();
    public List<T> reportePorFecha(Date fechaInicio, Date fechaFin);
    
    // Reportes con filtros
    public List<T> reportePorEstado(boolean activo);
    public List<T> reportePorBusqueda(String criterio, String valor);
    
    // Estadísticas
    public Map<String, Integer> estadisticas();
    public int totalPorEstado(boolean activo);
    
    // Exportación
    public boolean exportarExcel(List<T> datos, String rutaArchivo);
    public boolean exportarPDF(List<T> datos, String rutaArchivo);
}