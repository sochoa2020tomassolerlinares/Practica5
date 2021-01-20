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

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class CCVariasCondicionesViewModel extends AndroidViewModel {

    private DiarioRepository mRepository;
    //utilizamos un HashMap con dos elementos: el primero nos sirve
    //para buscar por nombre y el segundo será un resumen que buscaremos
    private MutableLiveData<HashMap<String, Object>> condicionBusquedaLiveData;
    private final String NOMBRE = "nombre";
    private final String RESUMEN = "resumen";

    //resultado de la consulta SQL, cuando cambien la condición, se activará el observador y actualiza en pantalla el RecyclerView
    private LiveData<List<DiaDiario>> listDiariosLiveData;

    public CCVariasCondicionesViewModel(@NonNull Application application) {
        super(application);
        mRepository = DiarioRepository.getInstance(application);

        condicionBusquedaLiveData = new MutableLiveData<HashMap<String, Object>>();
        //creamos el LiveData de condición de busqueda. Mantenemos los valores de la condición SQL en una sola
        //estructura HashMap para que se detecte las modificaciones de cualquiera de las dos
        HashMap<String, Object> condiciones = new HashMap<String, Object>();
        //asignamos valores iniciales
        condiciones.put(NOMBRE, "");//todos los diarios
        condiciones.put(RESUMEN, "");//Resumen que buscamos

        condicionBusquedaLiveData.setValue(condiciones);
        //switchMap nos  permite cambiar el livedata de la consulta SQL
        // al modificarse la consulta de busqueda(cuando cambia condicionBusquedaLiveData)

        listDiariosLiveData = Transformations.switchMap(condicionBusquedaLiveData, new Function<HashMap<String, Object>, LiveData<List<DiaDiario>>>() {
            @Override
            public LiveData<List<DiaDiario>> apply(HashMap<String, Object> condiciones) {

                return mRepository.getDiariosOrderBy((String) condiciones.get(NOMBRE), (String) condiciones.get(RESUMEN));
            }
        });
    }

    /*
    cambiamos la condición de busqueda
     */
    public void setResumen(String resumen) {
        HashMap<String, Object> condiciones = condicionBusquedaLiveData.getValue();
        condiciones.put(RESUMEN, resumen);
        condicionBusquedaLiveData.setValue(condiciones);
    }

    public void setNombre(String nombre) {
        HashMap<String, Object> condiciones = condicionBusquedaLiveData.getValue();
        condiciones.put(NOMBRE, nombre);
        condicionBusquedaLiveData.setValue(condiciones);
    }

    public LiveData<List<DiaDiario>> getListDiariosLiveData() {
        return listDiariosLiveData;
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