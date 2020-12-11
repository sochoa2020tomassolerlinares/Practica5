package net.iessochoa.tomassolerlinares.practica5.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import net.iessochoa.tomassolerlinares.practica5.model.DiaDiario;
import net.iessochoa.tomassolerlinares.practica5.model.DiarioDao;
import net.iessochoa.tomassolerlinares.practica5.model.DiarioDatabase;

import java.util.List;

import io.reactivex.Single;

public class DiarioRepository {
    //implementamos Singleton
    private static volatile DiarioRepository INSTANCE;

    private DiarioDao mdiarioDao;
    private LiveData<List<DiaDiario>> mAllDiarios;
    //singleton

    public static DiarioRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (DiarioRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DiarioRepository(application);
                }
            }
        }
        return INSTANCE;
    }

    private DiarioRepository(Application application) {
        //creamos la base de datos
        DiarioDatabase db = DiarioDatabase.getDatabase(application);
        //Recuperamos el DAO necesario para el CRUD de la base de datos
        mdiarioDao = db.DiarioDao();
        //Recuperamos la lista como un LiveData
        mAllDiarios = mdiarioDao.getAllDiario();
    }

    public LiveData<List<DiaDiario>> getAllDiarios() {
        return mAllDiarios;
    }

    public LiveData<List<DiaDiario>> getDiariosOrderBy(String nombre, String resumen) {
        mAllDiarios = mdiarioDao.getDiarioOrderBy(nombre, resumen);
        return mAllDiarios;
    }


    /*public LiveData<List<Contacto>> getByNombreFecha(String nombre, Date menorQue){
        mAllDiarios= mdiarioDao.findByNombreFecha(nombre,menorQue);
        return mAllDiarios;
    }
    //lista ordenado por columnas diferentes
    public LiveData<List<Contacto>> getContactosOrderByNombre(){
        mAllDiarios= mdiarioDao.getContactosOrderByNombre();
        return mAllDiarios;
    }
    public LiveData<List<Contacto>> getContactosOrderByFecha(){
        mAllDiarios= mdiarioDao.getContactosOrderByFecha();
        return mAllDiarios;
    }
    public LiveData<List<Contacto>> getContactosOrderBy(String order_by, String order){
        mAllDiarios= mdiarioDao.getContactosOrderBy(order_by, order);
        return mAllDiarios;
    }*/
    public Single<Float> getValoracionTotal() {
        return mdiarioDao.getValoracionTotal();
    }

    /*
    Insertar: nos obliga a crear tarea en segundo plano
     */
    public void insert(DiaDiario dia) {
        //administramos el hilo con el Executor
        DiarioDatabase.databaseWriteExecutor.execute(() -> {
            mdiarioDao.insert(dia);
        });
    }

    /*
    Borrar: nos obliga a crear tarea en segundo plano
     */
    public void delete(DiaDiario dia) {
        //administramos el hilo con el Executor
        DiarioDatabase.databaseWriteExecutor.execute(() -> {
            mdiarioDao.deleteByDiaDiario(dia);
        });
    }
    /*public void deleteById(int id){
        mContactoDao.deleteByContactoId(id);
    }*/
}
