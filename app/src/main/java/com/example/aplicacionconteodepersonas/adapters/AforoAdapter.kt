package com.example.aplicacionconteodepersonas.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicacionconteodepersonas.databinding.ItemAforoBinding
import com.example.aplicacionconteodepersonas.models.RegistroAforo

/**
 * Adaptador para la lista de registros de aforo en RecyclerView.
 * Traduce cada [RegistroAforo] en un ítem visible con campos rotulados según el negocio.
 */
class AforoAdapter(
    private var lista: MutableList<RegistroAforo>,
    private val onItemClick: (RegistroAforo) -> Unit,
    private val onDeleteClick: (RegistroAforo) -> Unit
) : RecyclerView.Adapter<AforoAdapter.AforoViewHolder>() {

    class AforoViewHolder(val binding: ItemAforoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AforoViewHolder {
        val binding = ItemAforoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AforoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AforoViewHolder, position: Int) {
        val item = lista[position]

        // Rótulo del negocio: por ejemplo "Auditorio: 45 personas"
        holder.binding.txtItemTitulo.text = "${item.zona}: ${item.cantidadPersonas} personas"

        // Segundo rótulo del negocio con estado y fecha
        val nivel = if (item.nivelAforo.isNotBlank()) item.nivelAforo else "Normal"
        val fecha = if (item.fechaHora.isNotBlank()) item.fechaHora else "Reciente"
        holder.binding.txtItemSubtitulo.text = "Nivel: $nivel • $fecha"

        // Clic en la fila para editar
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }

        // Clic en el botón eliminar
        holder.binding.btnItemEliminar.setOnClickListener {
            onDeleteClick(item)
        }
    }

    override fun getItemCount(): Int = lista.size

    fun actualizarLista(nuevaLista: List<RegistroAforo>) {
        lista.clear()
        lista.addAll(nuevaLista)
        notifyDataSetChanged()
    }
}
