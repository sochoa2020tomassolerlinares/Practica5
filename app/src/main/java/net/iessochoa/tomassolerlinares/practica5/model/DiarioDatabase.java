package net.iessochoa.tomassolerlinares.practica5.model;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.*;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Clase abstracta que extiende de RoomDatabase donde se conecta a la base de datos
 */
@Database(entities = {DiaDiario.class}, version = 1)
@TypeConverters({TransformaFechaSQLite.class})
public abstract class DiarioDatabase extends RoomDatabase {

    public abstract DiarioDao DiarioDao();

    private static volatile DiarioDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static DiarioDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DiarioDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), DiarioDatabase.class, "diario_database").addCallback(sRoomDatabaseCallback).build();
                }
            }
        }
        return INSTANCE;
    }

    //crearemos una tarea en segundo plano que nos permite cargar los datos de ejemplo la primera
    //vez que se abre la base de datos
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            // new PopulateDbAsync(INSTANCE).execute();
            //creamos algunos contactos en un hilo
            databaseWriteExecutor.execute(() -> {
                //obtenemos la base de datos
                DiarioDao mDao = INSTANCE.DiarioDao();
                SimpleDateFormat formatoDelTexto = new SimpleDateFormat("dd-MM-yyyy");
                DiaDiario dia = null;
                //creamos unos contactos
                try {
                    dia = new DiaDiario(formatoDelTexto.parse("12-3-2020"), 5, "Un dia un poco aburrio, solo he visto Netflix.", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam rutrum lectus vestibulum, consectetur urna vel, rutrum tortor. Phasellus at leo nibh. Pellentesque lacinia blandit dui eu aliquam. Cras et suscipit nibh. Cras vehicula lobortis ante, vel hendrerit diam convallis at. Nullam egestas vel dui sed tincidunt. In placerat ac mauris eu faucibus. Nullam eu pretium justo. Suspendisse in leo nisi. Nulla hendrerit erat a finibus egestas. Nulla et libero eu purus euismod maximus.");
                    mDao.insert(dia);
                    dia = new DiaDiario(formatoDelTexto.parse("23-7-2020"), 10, "De los mejores días de mi vida.", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam rutrum lectus vestibulum, consectetur urna vel, rutrum tortor. Phasellus at leo nibh. Pellentesque lacinia blandit dui eu aliquam. Cras et suscipit nibh. Cras vehicula lobortis ante, vel hendrerit diam convallis at. Nullam egestas vel dui sed tincidunt. In placerat ac mauris eu faucibus. Nullam eu pretium justo. Suspendisse in leo nisi. Nulla hendrerit erat a finibus egestas. Nulla et libero eu purus euismod maximus.");
                    mDao.insert(dia);
                    dia = new DiaDiario(formatoDelTexto.parse("11-11-2020"), 4, "Dia decepcionante, no me ha tocado el euromillón.", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam rutrum lectus vestibulum, consectetur urna vel, rutrum tortor. Phasellus at leo nibh. Pellentesque lacinia blandit dui eu aliquam. Cras et suscipit nibh. Cras vehicula lobortis ante, vel hendrerit diam convallis at. Nullam egestas vel dui sed tincidunt. In placerat ac mauris eu faucibus. Nullam eu pretium justo. Suspendisse in leo nisi. Nulla hendrerit erat a finibus egestas. Nulla et libero eu purus euismod maximus.");
                    mDao.insert(dia);
                    dia = new DiaDiario(formatoDelTexto.parse("3-6-2020"), 7, "Buen día de fin de curso, ahora a disfrutar el verano.", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam rutrum lectus vestibulum, consectetur urna vel, rutrum tortor. Phasellus at leo nibh. Pellentesque lacinia blandit dui eu aliquam. Cras et suscipit nibh. Cras vehicula lobortis ante, vel hendrerit diam convallis at. Nullam egestas vel dui sed tincidunt. In placerat ac mauris eu faucibus. Nullam eu pretium justo. Suspendisse in leo nisi. Nulla hendrerit erat a finibus egestas. Nulla et libero eu purus euismod maximus.");
                    mDao.insert(dia);
                    dia = new DiaDiario(formatoDelTexto.parse("7-1-2020"), 8, "Buen día con la familia y abriendo regalos.", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam rutrum lectus vestibulum, consectetur urna vel, rutrum tortor. Phasellus at leo nibh. Pellentesque lacinia blandit dui eu aliquam. Cras et suscipit nibh. Cras vehicula lobortis ante, vel hendrerit diam convallis at. Nullam egestas vel dui sed tincidunt. In placerat ac mauris eu faucibus. Nullam eu pretium justo. Suspendisse in leo nisi. Nulla hendrerit erat a finibus egestas. Nulla et libero eu purus euismod maximus.");
                    mDao.insert(dia);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            });
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            //si queremos realizar alguna tarea cuando se abre
        }
    };

}