package net.iessochoa.tomassolerlinares.practica5.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import net.iessochoa.tomassolerlinares.practica5.model.DiaDiario;
import net.iessochoa.tomassolerlinares.practica5.model.DiarioDao_Impl;
import net.iessochoa.tomassolerlinares.practica5.repository.DiarioRepository;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Single;

public class DDOrdenarPorViewModel extends AndroidViewModel {
    private DiarioRepository mRepository;
    //utilizamos un HashMap con dos elementos: el primero nos sirve
    //para buscar por nombre y el segundo será una fecha con la que buscaremos los menores que la fecha
    private MutableLiveData<HashMap<String,Object>> condicionBusquedaLiveData;
    //ordenaremos por nombre y por fecha
    private final String ORDER_BY="order_by";
    private final String ORDER="order";
    public static final String ORDENAR_POR_NOMBRE= DiaDiario.RESUMEN;
    public static final String ORDENAR_POR_FECHA=DiaDiario.FECHA;
    //podremos elegir ascendente y descendente
    public static final String ORDENAR_ASC="ASC";
    public static final String ORDENAR_DESC="DESC";
    public enum OrderBy
    {
        RESUMEN, FECHA;
    }
    public enum  Order
    {
        ASC,DESC;
    }


    //resultado de la consulta SQL, cuando cambien la condición, se activará el observador y actualiza en pantalla el RecyclerView
    private LiveData<List<DiaDiario>> listDiariosLiveData;

    public DDOrdenarPorViewModel(@NonNull Application application) {
        super(application);
        mRepository=DiarioRepository.getInstance(application);

        condicionBusquedaLiveData =new MutableLiveData<HashMap<String,Object>>();
        //creamos el LiveData de condición de busqueda. Mantenemos los valores de la condición SQL en una sola
        //estructura HashMap para que se detecte las modificaciones de cualquiera de las dos
        HashMap<String, Object> condiciones=new HashMap<String, Object>();
        condiciones.put(ORDER_BY,DiaDiario.RESUMEN);//toda la agenda
        condiciones.put(ORDER,ORDENAR_ASC);//fecha actual

        condicionBusquedaLiveData.setValue(condiciones);
        //switchMap nos  permite cambiar el livedata de la consulta SQL
        // al modificarse la consulta de busqueda(cuando cambia condicionBusquedaLiveData)

        listDiariosLiveData = Transformations.switchMap(condicionBusquedaLiveData, new Function<HashMap<String,Object>, LiveData<List<DiaDiario>>>() {
            @Override
            public LiveData<List<DiaDiario>> apply(HashMap<String,Object> condiciones) {
                return mRepository.getDiariosOrderBy((String) condiciones.get(ORDER_BY),(String)condiciones.get(ORDER));
            }
        });

    }


    /**
     * Modifica la condición de busqueda
     * @param orderBy: ordena por Nombre o por fecha
     */
    public void setOrderBy(OrderBy orderBy) {
        HashMap<String, Object> condiciones= condicionBusquedaLiveData.getValue();
        String ordenar="";
        switch (orderBy){
            case FECHA:
                ordenar=DiaDiario.FECHA;
                break;
            case NOMBRE:
                ordenar=DiaDiario.RESUMEN;
                break;
        }
        condiciones.put(ORDER_BY,ordenar);
        condicionBusquedaLiveData.setValue(condiciones);
    }

    /**
     * Modifica la condición de busqueda
     * @param order: ordena Ascendente(ASC) o Descendente(DESC)
     */
    public void setOrder(Order order){
        HashMap<String, Object> condiciones= condicionBusquedaLiveData.getValue();

        condiciones.put(ORDER,order.toString());
        condicionBusquedaLiveData.setValue(condiciones);
    }
    public LiveData<List<DiaDiario>> getAllDiarios() {
        return listDiariosLiveData;
    }
    //rxJava
    public Single<Float> getValoracionTotal(){
        return mRepository.getValoracionTotal();
    }

    //Inserción y borrado que se reflejará automáticamente gracias al observador creado en la
    //actividad
    public void insert(DiaDiario diario){
        mRepository.insert(diario);
    }
    public void delete(DiaDiario diario){
        mRepository.delete(diario);
    }
}