package net.iessochoa.tomassolerlinares.practica5.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import net.iessochoa.tomassolerlinares.practica5.model.DiaDiario;
import net.iessochoa.tomassolerlinares.practica5.model.DiarioDao;
import net.iessochoa.tomassolerlinares.practica5.model.DiarioDatabase;

import java.util.List;

import io.reactivex.Single;

/**
 * Clase encargada de trabajar con el repositorio mediante la clase Dao
 */
public class DiarioRepository {
    //implementamos Singleton
    private static volatile DiarioRepository INSTANCE;

    private DiarioDao mdiarioDao;
    private LiveData<List<DiaDiario>> mAllDiarios;
    //singleton

    //Método estático que devuelve la estancia en la que se ubica el programa
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

    //Constructor del repositorio
    private DiarioRepository(Application application) {
        //creamos la base de datos
        DiarioDatabase db = DiarioDatabase.getDatabase(application);
        //Recuperamos el DAO necesario para el CRUD de la base de datos
        mdiarioDao = db.DiarioDao();
        //Recuperamos la lista como un LiveData
        mAllDiarios = mdiarioDao.getAllDiario();
    }

    //Devuelve todos los diarios almacenados
    public LiveData<List<DiaDiario>> getAllDiarios() {
        return mAllDiarios;
    }

    //Devuelve los diarios ordenados por resumen
    public LiveData<List<DiaDiario>> getDiariosOrderBy(String nombre, String resumen) {
        mAllDiarios = mdiarioDao.getDiarioOrderBy(nombre, resumen);
        return mAllDiarios;
    }

    //Devuelve la valoración total del diario
    public Single<Integer> getValoracionTotal() {
        return mdiarioDao.getValoracionTotal();
    }

    //Insertar: nos obliga a crear tarea en segundo plano
    public void insert(DiaDiario dia) {
        //administramos el hilo con el Executor
        DiarioDatabase.databaseWriteExecutor.execute(() -> {
            mdiarioDao.insert(dia);
        });
    }
    //Borrar: nos obliga a crear tarea en segundo plano
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
