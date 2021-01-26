package net.iessochoa.tomassolerlinares.practica5.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.iessochoa.tomassolerlinares.practica5.R;
import net.iessochoa.tomassolerlinares.practica5.adapters.DiarioAdapter;
import net.iessochoa.tomassolerlinares.practica5.model.DiaDiario;
import net.iessochoa.tomassolerlinares.practica5.viewmodels.DiarioViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_DIA = "net.iessochoa.tomassolerlinares.practica5.ui.mainactivity.extra_dia";
    public static final int OPTION_REQUEST_NUEVO = 1, OPTION_REQUEST_EDIT = 2;
    private DiarioViewModel diarioViewModel;
    private RecyclerView rvDiario;
    private DiarioAdapter diarioAdapter;
    private MenuItem ordenar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fabNuevo = findViewById(R.id.fabNuevo);

        diarioAdapter = new DiarioAdapter();
        rvDiario = findViewById(R.id.rvDiario);
        rvDiario.setHasFixedSize(true);
        rvDiario.setLayoutManager(new LinearLayoutManager(this));
        rvDiario.setAdapter(diarioAdapter);

        diarioViewModel = new ViewModelProvider(this).get(DiarioViewModel.class);
        diarioViewModel.getAllDiarios().observe(this, new
                Observer<List<DiaDiario>>() {
                    @Override
                    public void onChanged(List<DiaDiario> diario) {
                        diarioAdapter.setDiario(diarioViewModel.getAllDiariosList());
                        diarioAdapter.notifyDataSetChanged();
                    }
                });


        fabNuevo.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EdicionDiaActivity.class);
            startActivityForResult(intent, OPTION_REQUEST_NUEVO);
        });

        diarioAdapter.setOnClickBorrarListener(this::borrarDia);
        diarioAdapter.setOnClickEditarListener(this::editarDia);
    }

    private void borrarDia(final DiaDiario dia) {
        AlertDialog.Builder dialogo = new AlertDialog.Builder(MainActivity.this);
        dialogo.setTitle(getString(R.string.Aviso));

        dialogo.setMessage(getString(R.string.AvisoMsn));

        dialogo.setPositiveButton(android.R.string.yes, (dialogInterface, i) -> diarioViewModel.delete(dia));
        dialogo.setNegativeButton(android.R.string.no, (dialogInterface, i) -> Toast.makeText(this, R.string.CancelBorrar, Toast.LENGTH_SHORT).show());
        dialogo.show();
    }

    /**
     * Método encargado de editar una tarea.
     *
     * @param dia
     */
    private void editarDia(final DiaDiario dia) {
        Intent intent = new Intent(this, EdicionDiaActivity.class);
        intent.putExtra(EXTRA_DIA, dia);
        startActivityForResult(intent, OPTION_REQUEST_EDIT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, getString(R.string.ErrorActivityResult), Toast.LENGTH_SHORT);
        } else {
            Bundle bundle = data.getExtras();
            DiaDiario dia = bundle.getParcelable(EXTRA_DIA);
            diarioViewModel.insert(dia);
            /*switch (requestCode) {
                case OPTION_REQUEST_NUEVO:

                    break;
                case OPTION_REQUEST_EDIT:

                    break;
            }*/
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_ordenar:
                onCreateDialogOrdenar().show();
                return true;
            case R.id.action_about:
                onCreateDialogAbout().show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private Dialog onCreateDialogOrdenar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(R.string.ordenarPor)
                .setItems(R.array.opcionesOrdenar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String ordenadoPor = "";
                        switch (which) {
                            case 0://fecha
                                ordenadoPor = DiaDiario.FECHA;
                                break;
                            case 1://valoracion
                                ordenadoPor = DiaDiario.VALORACION_DIA;
                                break;
                            case 2://resumen
                                ordenadoPor = DiaDiario.RESUMEN;
                                break;
                        }
                        diarioViewModel.setOrderBy(ordenadoPor);
                    }
                });
        return builder.create();
    }

    private Dialog onCreateDialogAbout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.aboutInfo)
                .setTitle(R.string.about)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        return builder.create();
    }

    private Dialog onCreateDialogValoracion(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        return builder.create();
    }
}
