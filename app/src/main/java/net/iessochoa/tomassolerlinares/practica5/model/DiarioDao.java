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

/**
 * Clase Dao donde se establecen las consultas que va a realizar nuestro programa
 */
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
    @Query("SELECT * FROM  " +DiaDiario.TABLE_NAME +
            " WHERE " +DiaDiario.RESUMEN +" LIKE '%' || :resumen || '%' "+
            " ORDER BY " +
            "CASE WHEN :sort_by = '"+DiaDiario.FECHA+"'  THEN "+DiaDiario.FECHA+" END ASC, " +
            "CASE WHEN :sort_by = '"+DiaDiario.RESUMEN+"'  THEN "+DiaDiario.RESUMEN+" END ASC, " +
            "CASE WHEN :sort_by = '"+DiaDiario.VALORACION_DIA+"'  THEN "+DiaDiario.VALORACION_DIA+" END ASC"
    )
    public LiveData<List<DiaDiario>> getDiarioOrderBy(String resumen, String sort_by);
    @Query("SELECT AVG(" +DiaDiario.VALORACION_DIA + ") FROM " + DiaDiario.TABLE_NAME)
    public Single<Integer> getValoracionTotal();
}
