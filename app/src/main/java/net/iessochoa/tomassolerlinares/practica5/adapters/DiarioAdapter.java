package net.iessochoa.tomassolerlinares.practica5.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import net.iessochoa.tomassolerlinares.practica5.R;
import net.iessochoa.tomassolerlinares.practica5.model.DiaDiario;

import java.util.List;

/**
 * Clase encargada de detectar las llamadas del usuario a sus respectivas funciones
 */

public class DiarioAdapter extends RecyclerView.Adapter<DiarioAdapter.DiarioViewHolder> {
    private List<DiaDiario> listaDias;

    private OnItemClickBorrarListener listenerBorrar;
    private OnItemClickEditarListener listenerEditar;

    //Método que se inicia cuando se crea el viewmHolder
    @NonNull
    @Override
    public DiarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dia, parent, false);
        return new DiarioViewHolder(itemView);
    }
    //Método que se lanza cuando se detecta conecta el viewHolder y establece los datos almacenados en sus repectivos contenedores xml
    @Override
    public void onBindViewHolder(@NonNull DiarioAdapter.DiarioViewHolder diarioViewHolder, int position) {
        if (listaDias != null) {
            final DiaDiario dia = listaDias.get(position);
            diarioViewHolder.tvResumen.setText(dia.getResumen());
            diarioViewHolder.tvFecha.setText(dia.getFechaFormatoLocal());

            if(dia.getValoracionDia()<4){
                diarioViewHolder.ivValoracion.setImageResource(R.mipmap.ic_semaforo_rojo_foreground);
            }else if(dia.getValoracionDia()<7){
                diarioViewHolder.ivValoracion.setImageResource(R.mipmap.ic_semaforo_amarillo_foreground);
            }else{
                diarioViewHolder.ivValoracion.setImageResource(R.mipmap.ic_semaforo_verde_foreground);
            }
        }
    }

    //Devuelve el tamaño de la lista de dias
    @Override
    public int getItemCount() {
        if (listaDias!= null)
            return listaDias.size();
        else return 0;
    }

    //Carga un diario en la clase
    public void setDiario(List<DiaDiario> listTarea) {
        listaDias = listTarea;
        notifyDataSetChanged();
    }

    //Clase encargada de declarar el viewHolder del adapter
    public class DiarioViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvResumen;
        private final TextView tvFecha;
        private final ImageView ivValoracion;
        private final ImageView ivDelete;
        private final CardView cardView;

        //Constructor del ViewHolder
        private DiarioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvResumen = itemView.findViewById(R.id.tvResumen);
            tvFecha = itemView.findViewById(R.id.tvFechaMenu);
            ivValoracion = itemView.findViewById(R.id.ivValoracion);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            cardView = itemView.findViewById(R.id.cvItem);
            ivDelete.setOnClickListener(v -> {
                if (listenerBorrar != null)
                    listenerBorrar.onItemClickBorrar(listaDias.get( DiarioViewHolder.this.getAdapterPosition()));
            });
            cardView.setOnClickListener(v -> {
                if (listenerEditar != null)
                    listenerEditar.onItemClickEditar(listaDias.get( DiarioViewHolder.this.getAdapterPosition()));
            });

        }
        //Devuelve un dia seleccionado
        public DiaDiario getDia(){
            return listaDias.get(DiarioViewHolder.this.getAdapterPosition());
        }

    }

    //Interfaz que declara el listener borrar
    public interface OnItemClickBorrarListener {
        void onItemClickBorrar(DiaDiario dia);
    }

    //Interfaz que declara el listener editar
    public interface OnItemClickEditarListener{
        void onItemClickEditar(DiaDiario dia);
    }

    //Método que llama al listener editar
    public void setOnClickEditarListener(OnItemClickEditarListener listener){
        this.listenerEditar = listener;
    }

    //Método que llama al listener borrar
    public void setOnClickBorrarListener(OnItemClickBorrarListener listener){
        this.listenerBorrar = listener;
    }
}
