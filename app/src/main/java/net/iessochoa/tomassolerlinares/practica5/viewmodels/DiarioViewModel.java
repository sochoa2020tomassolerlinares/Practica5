package net.iessochoa.tomassolerlinares.practica5.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import net.iessochoa.tomassolerlinares.practica5.model.DiaDiario;
import net.iessochoa.tomassolerlinares.practica5.repository.DiarioRepository;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Single;

public class DiarioViewModel extends AndroidViewModel {
    private DiarioRepository mRepository;
    private LiveData<List<DiaDiario>> mAllDiarios;
    private MutableLiveData<HashMap<String, Object>> condicionBusquedaLiveData;
    private final String RESUMEN = "resumen";
    private final String ORDER_BY = "order_by";
    private final String ORDER = "order";
    //podremos elegir ascendente y descendente
    public static final String ORDENAR_ASC = "ASC";
    public static final String ORDENAR_DESC = "DESC";
    public enum OrderBy {
        RESUMEN, FECHA, VALORACION_DIA;
    }

    public enum Order {
        ASC, DESC;
    }

    public DiarioViewModel(@NonNull Application application) {
        super(application);
        mRepository = DiarioRepository.getInstance(application);
        //Recuperamos el LiveData de todos los contactos
        mAllDiarios = mRepository.getAllDiarios();

        condicionBusquedaLiveData = new MutableLiveData<HashMap<String, Object>>();
        HashMap<String, Object> condiciones = new HashMap<String, Object>();
        condiciones.put(ORDER_BY, DiaDiario.RESUMEN);//todos los diarios
        condiciones.put(ORDER, ORDENAR_ASC);//fecha actual
        condiciones.put(RESUMEN, "");//Resumen que buscamos

        condicionBusquedaLiveData.setValue(condiciones);
        //switchMap nos  permite cambiar el livedata de la consulta SQL
        // al modificarse la consulta de busqueda(cuando cambia condicionBusquedaLiveData)

        mAllDiarios = Transformations.switchMap(condicionBusquedaLiveData, new Function<HashMap<String, Object>, LiveData<List<DiaDiario>>>() {
            @Override
            public LiveData<List<DiaDiario>> apply(HashMap<String, Object> condiciones) {
                return mRepository.getDiariosOrderBy((String) condiciones.get(RESUMEN),(String) condiciones.get(ORDER_BY));
            }
        });
    }

    public LiveData<List<DiaDiario>> getAllDiarios() {
        return mAllDiarios;

    }

    public List<DiaDiario> getAllDiariosList(){
        return mAllDiarios.getValue();
    }

    public Single<Integer> getValoracionTotal() {
        return mRepository.getValoracionTotal();
    }

    public void setResumen(String resumen) {
        HashMap<String, Object> condiciones = condicionBusquedaLiveData.getValue();
        condiciones.put(RESUMEN, resumen);
        condicionBusquedaLiveData.setValue(condiciones);
    }

    public void setOrderBy(String orderBy) {
        HashMap<String, Object> condiciones = condicionBusquedaLiveData.getValue();
        String ordenar = "";
        switch (orderBy) {
            case DiaDiario.FECHA:
                ordenar = DiaDiario.FECHA;
                break;
            case DiaDiario.RESUMEN:
                ordenar = DiaDiario.RESUMEN;
                break;
            case DiaDiario.VALORACION_DIA:
                ordenar = DiaDiario.VALORACION_DIA;
        }
        condiciones.put(ORDER_BY, ordenar);
        condicionBusquedaLiveData.setValue(condiciones);
    }

    public void setOrder(Order order) {
        HashMap<String, Object> condiciones = condicionBusquedaLiveData.getValue();

        condiciones.put(ORDER, order.toString());
        condicionBusquedaLiveData.setValue(condiciones);
    }

    //Inserción y borrado que se reflejará automáticamente gracias al observador creado en la
    //actividad
    public void insert(DiaDiario dia) {
        mRepository.insert(dia);
    }

    public void delete(DiaDiario dia) {
        mRepository.delete(dia);
    }
}
