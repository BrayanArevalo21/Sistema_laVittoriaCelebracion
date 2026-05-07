package datos.interfaces;

import java.util.List;

public interface CrudCompletoInterface<T> extends CrudSimpleInterface<T> {
    
    // Buscar por ID específico
    public T buscarPorId(int id);
    
    // Listar solo activos o solo inactivos
    public List<T> listarActivos();
    public List<T> listarInactivos();
    
    // Obtener detalles específicos
    public List<T> listarPorFiltro(String filtro, String valor);
    
    // Operaciones masivas
    public boolean desactivarMasivo(List<Integer> ids);
    public boolean activarMasivo(List<Integer> ids);
    
    // Validaciones adicionales
    public boolean existeRelacion(int id);
    public int totalActivos();
    public int totalInactivos();
}