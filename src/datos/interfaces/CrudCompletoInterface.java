package datos.interfaces;

import java.util.List;

public interface CrudCompletoInterface<T> extends CrudSimpleInterface<T> {
    
    // Buscar por ID
    public T buscarPorId(int id);
    
    // Listar por estado
    public List<T> listarActivos();
    public List<T> listarInactivos();
    
    // Operaciones masivas
    public boolean desactivarMasivo(List<Integer> ids);
    public boolean activarMasivo(List<Integer> ids);
    
    // Validaciones extras
    public boolean existeRelacion(int id);
    public int totalActivos();
    public int totalInactivos();
}