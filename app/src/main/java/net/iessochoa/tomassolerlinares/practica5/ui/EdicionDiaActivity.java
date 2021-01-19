package net.iessochoa.tomassolerlinares.practica5.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.iessochoa.tomassolerlinares.practica5.R;
import net.iessochoa.tomassolerlinares.practica5.model.DiaDiario;

import java.util.Calendar;
import java.util.Date;

public class EdicionDiaActivity extends AppCompatActivity {

    private EditText edtResumen, edtDescripcion;
    private Spinner spnValorarDia;
    private ImageButton ibFecha;
    private TextView tvFecha;
    private FloatingActionButton fabGuardar;
    private Calendar calendar = Calendar.getInstance();
    DiaDiario dia;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edicion_dia);

        edtResumen = findViewById(R.id.edtBreveResumen);
        edtDescripcion = findViewById(R.id.edtDescripcion);
        spnValorarDia = findViewById(R.id.spnValoraDia);
        ibFecha = findViewById(R.id.ibFecha);
        tvFecha = findViewById(R.id.tvFecha);
        fabGuardar = findViewById(R.id.fabGuardar);

        DiaDiario editDia = getIntent().getParcelableExtra(MainActivity.EXTRA_DIA);
        if (editDia != null) {
            this.setTitle(getString(R.string.EditTitulo));
            edtResumen.setText(editDia.getResumen());
            edtDescripcion.setText(editDia.getContenido());
            tvFecha.setText(editDia.getFechaFormatoLocal());
            for(int i = 0; i < spnValorarDia.getCount(); i++){
                if(Integer.parseInt(String.valueOf(spnValorarDia.getItemAtPosition(i))) == editDia.getValoracionDia()){
                    spnValorarDia.setSelection(i);
                }
            }
        } else {
            this.setTitle(getString(R.string.NuevoTitulo));
            dia = new DiaDiario(calendar.getTime(), 0,"","");
            dia.setFecha(calendar.getTime());
            tvFecha.setText(dia.getFechaFormatoLocal());
        }

        ibFecha.setOnClickListener(v -> {
            Calendar newCalendar = Calendar.getInstance();

            DatePickerDialog dialogo = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    calendar.set(year, monthOfYear, dayOfMonth);
                    dia.setFecha(calendar.getTime());
                    tvFecha.setText(dia.getFechaFormatoLocal());
                }

            }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
            dialogo.show();
        });

        fabGuardar.setOnClickListener( v -> {
            if(edtDescripcion.getText().length()!=0 && edtResumen.getText().length()!=0 && tvFecha.getText().length()!=0){
                String resumen = edtResumen.getText().toString();
                String contenido = edtDescripcion.getText().toString();
                Date fecha = calendar.getTime();
                int valoracion =Integer.parseInt(String.valueOf(spnValorarDia.getSelectedItem()));

                if(editDia != null){
                    editDia.setContenido(contenido);
                    editDia.setResumen(resumen);
                    editDia.setFecha(fecha);
                    editDia.setValoracionDia(valoracion);
                    dia = editDia;
                } else{
                    dia = new DiaDiario(fecha,valoracion,resumen,contenido);
                }
                Intent intent = getIntent();
                intent.putExtra(MainActivity.EXTRA_DIA, dia);
                setResult(RESULT_OK, intent);
                finish();
            }else{
                Toast.makeText(this, getString(R.string.MensajeErrorDia), Toast.LENGTH_SHORT).show();
            }
        });
    }
}