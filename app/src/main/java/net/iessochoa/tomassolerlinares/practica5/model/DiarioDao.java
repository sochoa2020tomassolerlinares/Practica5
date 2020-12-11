package net.iessochoa.tomassolerlinares.practica5.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface DiarioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(DiaDiario dia);
    @Delete
    public void deleteByDiaDiario(DiaDiario dia);

    @Update()
    public void update(DiaDiario dia);
    @Query("DELETE FROM "+DiaDiario.TABLE_NAME)
    public void deleteAll();
    @Query("SELECT * FROM "+DiaDiario.TABLE_NAME+" ORDER BY fecha")
    public LiveData<List<DiaDiario>> getAllDiario();
    @Query("SELECT * FROM Diario WHERE resumen LIKE :resumen ORDER BY :sort_By ASC")
    public LiveData<List<DiaDiario>> getDiarioOrderBy(String resumen, String sort_By);
    @Query("SELECT SUM(" +DiaDiario.VALORACION_DIA + ") FROM " + DiaDiario.TABLE_NAME)
    public Single<Float> getValoracionTotal();
}
