package net.iessochoa.tomassolerlinares.practica5.viewmodels;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

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

/**
 * Clase viewmodel encargada de definir las funciones de nuestro diario de cara al usuario
 */
public class DiarioViewModel extends AndroidViewModel {
    private static final String PREFERENCIAS = "MisPreferencias";
    private DiarioRepository mRepository;
    private LiveData<List<DiaDiario>> mAllDiarios;
    public MutableLiveData<HashMap<String, Object>> condicionBusquedaLiveData;
    public final String RESUMEN = "resumen";
    public final String ORDER_BY = "order_by";
    public final String ORDER = "order";
    //podremos elegir ascendente y descendente
    public static final String ORDENAR_ASC = "ASC";
    public static final String ORDENAR_DESC = "DESC";
    public enum OrderBy {
        RESUMEN, FECHA, VALORACION_DIA;
    }

    public enum Order {
        ASC, DESC;
    }

    //Constructor del viewModel donde se definen las condiciones de búsqueda y se muestran como tal
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

        SharedPreferences prefs =application.getSharedPreferences(PREFERENCIAS,Context.MODE_PRIVATE);
        String resumen=prefs.getString(RESUMEN, "");
        condiciones.put(RESUMEN,resumen);//es el HashMap
        String orden=prefs.getString(ORDER_BY,DiaDiario.FECHA);
        condiciones.put(ORDER_BY,orden);

        mAllDiarios = Transformations.switchMap(condicionBusquedaLiveData, new Function<HashMap<String, Object>, LiveData<List<DiaDiario>>>() {
            @Override
            public LiveData<List<DiaDiario>> apply(HashMap<String, Object> condiciones) {
                return mRepository.getDiariosOrderBy((String) condiciones.get(RESUMEN),(String) condiciones.get(ORDER_BY));
            }
        });
    }

    //Devuelve todos los dias almacenados en el diario
    public LiveData<List<DiaDiario>> getAllDiarios() {
        return mAllDiarios;

    }
    //Devuelve todos los dias almacenados en el diario en formato List
    public List<DiaDiario> getAllDiariosList(){
        return mAllDiarios.getValue();
    }

    //Devuelve la valoración total del diario
    public Single<Integer> getValoracionTotal() {
        return mRepository.getValoracionTotal();
    }

    //Establece el resumen como condición.
    public void setResumen(String resumen) {
        HashMap<String, Object> condiciones = condicionBusquedaLiveData.getValue();
        condiciones.put(RESUMEN, resumen);
        condicionBusquedaLiveData.setValue(condiciones);
    }

    //Devuelve el resumen que se emplea como condición
    public String getResumen(){
        HashMap<String, Object> condiciones = condicionBusquedaLiveData.getValue();
        return(String)condiciones.get(RESUMEN);
    }

    //Método que se encarga de ordenar dependiendo de la elección del usuario
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

    //Devuelve el orden de la condición actual
    public String getOrder(){
        HashMap<String, Object> condiciones = condicionBusquedaLiveData.getValue();
        return(String) condiciones.get(ORDER_BY);
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
