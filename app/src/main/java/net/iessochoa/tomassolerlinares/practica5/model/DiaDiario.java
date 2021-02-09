package net.iessochoa.tomassolerlinares.practica5.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import androidx.annotation.NonNull;
import androidx.room.*;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
@Entity(tableName = DiaDiario.TABLE_NAME,
        indices = {@Index(value = {DiaDiario.FECHA},unique = true)})

public class DiaDiario implements Parcelable {
    public static final String TABLE_NAME="diario";
    public static final String ID= BaseColumns._ID;
    public static final String FECHA="fecha";
    public static final String VALORACION_DIA="valoracion_dia";
    public static final String RESUMEN="resumen";
    public static final String CONTENIDO="contenido";
    public static final String FOTO_URI="foto_uri";
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name=ID)
    private int id;
    @NonNull
    @ColumnInfo(name=FECHA)
    private Date fecha;
    @ColumnInfo(name=VALORACION_DIA)
    private int valoracionDia;
    @NonNull
    @ColumnInfo(name=RESUMEN)
    private String resumen;
    @NonNull
    @ColumnInfo(name=CONTENIDO)
    private String contenido;
    @ColumnInfo(name=FOTO_URI)
    private String fotoUri;

    public DiaDiario(@NonNull Date fecha, int valoracionDia,@NonNull String resumen,@NonNull String contenido, String fotoUri) {
        this.fecha = fecha;
        this.valoracionDia = valoracionDia;
        this.resumen = resumen;
        this.contenido = contenido;
        this.fotoUri = fotoUri;
    }
    @Ignore
    public DiaDiario(@NonNull Date fecha, int valoracionDia,@NonNull String resumen,@NonNull String contenido) {
        this.fecha = fecha;
        this.valoracionDia = valoracionDia;
        this.resumen = resumen;
        this.contenido = contenido;
        this.fotoUri = "android.resource://net.iessochoa.tomassolerlinares.practica5/drawable/ic_menu_report_image";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public int getValoracionDia() {
        return valoracionDia;
    }

    public void setValoracionDia(int valoracionDia) {
        this.valoracionDia = valoracionDia;
    }

    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getFotoUri() {
        return fotoUri;
    }

    public void setFotoUri(String fotoUri) {
        this.fotoUri = fotoUri;
    }

    public int getValoracionResumida(){
        if(valoracionDia < 5){
            return 1;
        } else if(valoracionDia < 8){
            return 2;
        }else{
            return 3;
        }
    }

    public static int getValoracionResumida(int valoracionDia){
        if(valoracionDia < 5){
            return 1;
        } else if(valoracionDia < 8){
            return 2;
        }else{
            return 3;
        }
    }

    public String getFechaFormatoLocal() {
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM,
                Locale.getDefault());
        return df.format(fecha);
    }

    //----PARCELABLE----
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeLong(this.fecha != null ? this.fecha.getTime() : -1);
        dest.writeInt(this.valoracionDia);
        dest.writeString(this.resumen);
        dest.writeString(this.contenido);
        dest.writeString(this.fotoUri);
    }

    protected DiaDiario(Parcel in) {
        this.id = in.readInt();
        long tmpFecha = in.readLong();
        this.fecha = tmpFecha == -1 ? null : new Date(tmpFecha);
        this.valoracionDia = in.readInt();
        this.resumen = in.readString();
        this.contenido = in.readString();
        this.fotoUri = in.readString();
    }

    public static final Parcelable.Creator<DiaDiario> CREATOR = new Parcelable.Creator<DiaDiario>() {
        @Override
        public DiaDiario createFromParcel(Parcel source) {
            return new DiaDiario(source);
        }

        @Override
        public DiaDiario[] newArray(int size) {
            return new DiaDiario[size];
        }
    };
}
