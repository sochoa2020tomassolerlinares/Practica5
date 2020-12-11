package net.iessochoa.tomassolerlinares.practica5.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import net.iessochoa.tomassolerlinares.practica5.model.DiaDiario;
import net.iessochoa.tomassolerlinares.practica5.repository.DiarioRepository;

import java.util.List;

public class DiarioViewModel extends AndroidViewModel {
    private DiarioRepository mRepository;
    private LiveData<List<DiaDiario>> mAllDiarios;

    public DiarioViewModel(@NonNull Application application) {
        super(application);
        mRepository = DiarioRepository.getInstance(application);
        //Recuperamos el LiveData de todos los contactos
        mAllDiarios = mRepository.getAllDiarios();
    }

    public LiveData<List<DiaDiario>> getDiarioLiveData() {
        return mAllDiarios;

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