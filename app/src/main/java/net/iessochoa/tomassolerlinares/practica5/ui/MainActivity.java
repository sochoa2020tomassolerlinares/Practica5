package net.iessochoa.tomassolerlinares.practica5.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.iessochoa.tomassolerlinares.practica5.R;
import net.iessochoa.tomassolerlinares.practica5.model.DiaDiario;
import net.iessochoa.tomassolerlinares.practica5.viewmodels.DiarioViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_DIA = "net.iessochoa.tomassolerlinares.practica5.ui.mainactivity.extra_dia";
    public static final int OPTION_REQUEST_NUEVO = 1, OPTION_REQUEST_EDIT = 2;
    private DiarioViewModel diarioViewModel;
    private RecyclerView rvDiario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fabNuevo = findViewById(R.id.fabNuevo);

        diarioViewModel = new ViewModelProvider(this).get(DiarioViewModel.class);
        diarioViewModel.getDiarioLiveData().observe(this, new
                Observer<List<DiaDiario>>() {
                    @Override
                    public void onChanged(List<DiaDiario> diario) {
                        //adapter.setDiario(diario);
                        Log.d("P5", "tamaño: " + diario.size());
                    }
                });

        fabNuevo.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EdicionDiaActivity.class);
            startActivityForResult(intent, OPTION_REQUEST_NUEVO);
        });
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
            switch (requestCode) {
                case OPTION_REQUEST_NUEVO:
                    Bundle bundle = data.getExtras();
                    DiaDiario dia = bundle.getParcelable(EXTRA_DIA);
                    diarioViewModel.insert(dia);
                    break;
                case OPTION_REQUEST_EDIT:

                    break;
            }
        }
    }
}
