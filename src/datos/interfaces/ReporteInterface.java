package datos.interfaces;

import java.util.List;
import java.util.Map;

public interface ReporteInterface<T> {
    
    // Reportes básicos
    public List<T> reporteGeneral();
    
    // Estadísticas
    public Map<String, Integer> estadisticas();
    public int totalPorEstado(boolean activo);
    
    // Exportación (opcional)
    public boolean exportarExcel(List<T> datos, String rutaArchivo);
}