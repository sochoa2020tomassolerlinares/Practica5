package net.iessochoa.tomassolerlinares.practica5.ui;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import net.iessochoa.tomassolerlinares.practica5.R;
import net.iessochoa.tomassolerlinares.practica5.model.DiaDiario;

import java.util.Calendar;
import java.util.Date;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission.CAMERA;

/**
 * Clase encargada del funcionamiento a la hora de crear o editar un dia del diario
 */
public class EdicionDiaActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS = 100;
    private EditText edtResumen, edtDescripcion;
    private Spinner spnValorarDia;
    private ImageButton ibFecha;
    private TextView tvFecha;
    private FloatingActionButton fabGuardar, fabAddImage;
    private ImageView ivFoto;
    private Calendar calendar = Calendar.getInstance();
    private Uri uriFoto = null;
    private ConstraintLayout clPrincipal;
    DiaDiario dia;

    private static final int STATUS_CODE_SELECCION_IMAGEN = 300;

    //Método que se ejecuta al iniciarse la activity
    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edicion_dia);
        //Se definen los contenedores del xml
        edtResumen = findViewById(R.id.edtBreveResumen);
        edtDescripcion = findViewById(R.id.edtDescripcion);
        spnValorarDia = findViewById(R.id.spnValoraDia);
        ibFecha = findViewById(R.id.ibFecha);
        tvFecha = findViewById(R.id.tvFecha);
        ivFoto = findViewById(R.id.ivFoto);
        clPrincipal = findViewById(R.id.clPrincipal);
        fabGuardar = findViewById(R.id.fabGuardar);
        fabAddImage = findViewById(R.id.fabAddImage);

        //Recogemos los datos del intent en caso de que se esté editando
        DiaDiario editDia = getIntent().getParcelableExtra(MainActivity.EXTRA_DIA);
        if (editDia != null) {
            this.setTitle(getString(R.string.EditTitulo));
            edtResumen.setText(editDia.getResumen());
            edtDescripcion.setText(editDia.getContenido());
            tvFecha.setText(editDia.getFechaFormatoLocal());
            for (int i = 0; i < spnValorarDia.getCount(); i++) {
                if (Integer.parseInt(String.valueOf(spnValorarDia.getItemAtPosition(i))) == editDia.getValoracionDia()) {
                    spnValorarDia.setSelection(i);
                }
            }
            if(editDia.getFotoUri()!=null){
                uriFoto=Uri.parse(editDia.getFotoUri());
                muestraFoto();
            }

        } else {
            this.setTitle(getString(R.string.NuevoTitulo));
            dia = new DiaDiario(calendar.getTime(), 0, "", "");
            dia.setFecha(calendar.getTime());
            tvFecha.setText(dia.getFechaFormatoLocal());
        }

        ibFecha.setOnClickListener(v -> {
            Calendar newCalendar = Calendar.getInstance();

            DatePickerDialog dialogo = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    calendar.set(year, monthOfYear, dayOfMonth);
                    Date fecha = calendar.getTime();
                    monthOfYear++;
                    if (editDia != null) {
                        editDia.setFecha(calendar.getTime());
                        tvFecha.setText(editDia.getFechaFormatoLocal());
                    } else {
                        dia.setFecha(calendar.getTime());
                        tvFecha.setText(dia.getFechaFormatoLocal());
                    }

                }

            }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
            dialogo.show();
        });

        //Se establencen las funciones de los botones guardar y añadir imagen
        fabGuardar.setOnClickListener(v -> {
            if (edtDescripcion.getText().length() != 0 && edtResumen.getText().length() != 0 && tvFecha.getText().length() != 0) {
                String resumen = edtResumen.getText().toString();
                String contenido = edtDescripcion.getText().toString();
                Date fecha = calendar.getTime();
                int valoracion = Integer.parseInt(String.valueOf(spnValorarDia.getSelectedItem()));

                if (editDia != null) {
                    editDia.setContenido(contenido);
                    editDia.setResumen(resumen);
                    editDia.setValoracionDia(valoracion);
                    if(uriFoto != null){
                        editDia.setFotoUri(uriFoto.toString());
                    }
                    dia = editDia;
                } else {
                    dia = new DiaDiario(fecha, valoracion, resumen, contenido);
                    if(uriFoto != null){
                        dia.setFotoUri(uriFoto.toString());
                    }
                }
                Intent intent = getIntent();
                intent.putExtra(MainActivity.EXTRA_DIA, dia);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                Toast.makeText(this, getString(R.string.MensajeErrorDia), Toast.LENGTH_SHORT).show();
            }
        });

        fabAddImage.setOnClickListener(v -> {
            ocultarTeclado();
            if (noNecesarioSolicitarPermisos()) {
                muestraOpcionesImagen();
            }

        });
    }
    //Abre la galería para seleccionar una imagen
    private void elegirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent.createChooser(intent, getString(R.string.seleccionarImagen)), STATUS_CODE_SELECCION_IMAGEN);
    }

    //Muestra la imagen almacenada en el objeto diaDiario
    private void muestraFoto() {
        if(uriFoto != null){
            Glide.with(this)
                    .load(uriFoto) // Uri of the picture
                    .into(ivFoto);//imageView
        }else{
            Uri uri = Uri.parse("android.resource://net.iessochoa.tomassolerlinares.practica5/drawable/ic_menu_report_image");
            Glide.with(this).load(uri).into(ivFoto);
        }
    }

    //Muestra diálogo donde aparecen las funciones a escoger en seleccionar imagen
    private void muestraOpcionesImagen() {
        final CharSequence[] option = {getString(R.string.elegirFoto), getString(R.string.elegirGaleria), getString(android.R.string.cancel)};
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(android.R.string.dialog_alert_title);
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        // abrirCamara();//opcional
                        break;
                    case 1:
                        elegirGaleria();
                        break;
                }
                dialog.dismiss();
            }
        });
        builder.show();
    }

    //Comprueba si es necesario solicitar permisos de acceso al usuario
    private boolean noNecesarioSolicitarPermisos() {
        //si la versión es inferior a la 6
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;
        //comprobamos si tenemos los permisos
        if ((checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) && (checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED))
            return true;
        //indicamos al usuario porqué necesitamos los permisos siempre que no haya indicado que no lo volvamos a hacer
        if ((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) || (shouldShowRequestPermissionRationale(CAMERA))) {
            Snackbar.make(clPrincipal, R.string.solicitarPermisos, Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok, v -> requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS)).show();
        } else {//pedimos permisos sin indicar el porqué
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS);
        }
        return false;//necesario pedir permisos
    }

    /**
     * Si se deniegan los permisos mostramos las opciones de la aplicación
     * para que el usuario acepte los permisos
     */
    //Muestra los mensajes necesarios para la petición de permisos
    private void muestraExplicacionDenegacionPermisos() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.peticionPermisos);
        builder.setMessage(R.string.peticionPermisosMensaje);
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            Intent intent = new Intent();

            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        });
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
            dialog.dismiss();
            finish();
        });
        builder.show();
    }

    //Comprueba los permisos obtenidos por el usuario
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS) {
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getString(R.string.permisosAceptados), Toast.LENGTH_SHORT).show();
                muestraOpcionesImagen();
            } else {//si no se aceptan los permisos
                muestraExplicacionDenegacionPermisos();
            }
        }
    }

    /**
     * Permite ocultar el teclado
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void ocultarTeclado() {
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        // mgr.showSoftInput(etDatos, InputMethodManager.HIDE_NOT_ALWAYS);
        if (imm != null) {
            imm.hideSoftInputFromWindow(edtResumen.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(edtDescripcion.getWindowToken(), 0);
        }
    }

    //Recibe los datos pasados por otra activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case STATUS_CODE_SELECCION_IMAGEN:
                    uriFoto = data.getData();
                    muestraFoto();
                    break;
            }
        }
    }

}