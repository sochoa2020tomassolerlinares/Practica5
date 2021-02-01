package net.iessochoa.tomassolerlinares.practica5.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.iessochoa.tomassolerlinares.practica5.R;
import net.iessochoa.tomassolerlinares.practica5.adapters.DiarioAdapter;
import net.iessochoa.tomassolerlinares.practica5.model.DiaDiario;
import net.iessochoa.tomassolerlinares.practica5.viewmodels.DiarioViewModel;

import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            rvDiario.setLayoutManager(new LinearLayoutManager(this));
        } else {
            rvDiario.setLayoutManager(new GridLayoutManager(this, 2));//2 es el número de columnas
        }
        rvDiario.setHasFixedSize(true);
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
        definirEventoSwiper();

    }

    private void borrarDia( final DiaDiario dia) {
        AlertDialog.Builder dialogo = new AlertDialog.Builder(MainActivity.this);
        dialogo.setTitle(getString(R.string.Aviso));

        dialogo.setMessage(getString(R.string.AvisoMsn));

        dialogo.setPositiveButton(android.R.string.yes, (dialogInterface, i) -> diarioViewModel.delete(dia));
        dialogo.setNegativeButton(android.R.string.no, (dialogInterface, i) -> Toast.makeText(this, R.string.CancelBorrar, Toast.LENGTH_SHORT).show());
        dialogo.show();
    }

    private void borrarDia( final DiaDiario dia, int posicion) {
        AlertDialog.Builder dialogo = new AlertDialog.Builder(MainActivity.this);
        dialogo.setTitle(getString(R.string.Aviso));

        dialogo.setMessage(getString(R.string.AvisoMsn));

        dialogo.setPositiveButton(android.R.string.yes, (dialogInterface, i) -> diarioViewModel.delete(dia));
        dialogo.setNegativeButton(android.R.string.no, (dialogInterface, i) -> Toast.makeText(this, R.string.CancelBorrar, Toast.LENGTH_SHORT).show());
        dialogo.show();

        dialogo.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        diarioAdapter.notifyItemChanged(posicion);//recuperamos la posición
                    }
                });
        dialogo.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Borramos
                        diarioViewModel.delete(dia);
                    }
                });
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
            Toast.makeText(this, getString(R.string.ErrorActivityResult), Toast.LENGTH_SHORT).show();
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
            case R.id.action_valoravida:
                onCreateDialogValoracion();
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
                .setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        return builder.create();
    }

    private void onCreateDialogValoracion() {
        diarioViewModel.getValoracionTotal()//obtenemos objeto reactivo de un solo uso 'Single' para que haga la consulta en un hilo
                .subscribeOn(Schedulers.io())//el observable(la consulta sql) se ejecuta en uno diferente
                .observeOn(AndroidSchedulers.mainThread())//indicamos que el observador es el hilo principal  de Android
                .subscribe(new SingleObserver<Integer>() { //creamos el observador
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
//cuando termine  la consulta de la base de datos recibimos el valor
                    public void onSuccess(@NonNull Integer valoracionMedia) {
                        Toast.makeText(MainActivity.this, "Valoración media: " + valoracionMedia.toString() + "/10", Toast.LENGTH_LONG).show();

                        ImageView v = new ImageView(MainActivity.this);

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle(R.string.valoravida);

                        if (valoracionMedia > 6) {
                            v.setImageResource(R.mipmap.ic_cara_feliz_foreground);
                            builder.setView(v);
                        } else if (valoracionMedia > 3) {
                            v.setImageResource(R.mipmap.ic_cara_normal_foreground);
                            builder.setView(v);
                        } else {
                            v.setImageResource(R.mipmap.ic_cara_triste_foreground);
                            builder.setView(v);
                        }


                        builder.setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                        builder.create().show();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                });
    }

    private void definirEventoSwiper() {
        //Creamos el Evento de Swiper
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT |
                        ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int
                            swipeDir) {
                        //realizamos un cast del viewHolder y obtenemos el pokemon a
                        // borrar
                        // PokemonListaPokemon
                        DiarioAdapter.DiarioViewHolder vhDiario = (DiarioAdapter.DiarioViewHolder) viewHolder;
                        DiaDiario dia = vhDiario.getDia();
                        borrarDia(dia, vhDiario.getAdapterPosition());
                    }
                };
        //Creamos el objeto de ItemTouchHelper que se encargará del trabajo
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        //lo asociamos a nuestro reciclerView
        itemTouchHelper.attachToRecyclerView(rvDiario);
    }
}
