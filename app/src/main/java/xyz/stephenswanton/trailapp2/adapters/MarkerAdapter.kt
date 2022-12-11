package xyz.stephenswanton.trailapp2.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import xyz.stephenswanton.trailapp2.models.TrailMarker
import xyz.stephenswanton.trailapp2.databinding.ItemMarkerBinding


interface NavigateAction {
    fun onDeleteIconClick(marker: TrailMarker)
    fun onEditIconClick(marker: TrailMarker)
    fun onViewIconClick(marker: TrailMarker)
}

class MarkerAdapter(
    var markers: List<TrailMarker>,
    var listener: NavigateAction?
): RecyclerView.Adapter<MarkerAdapter.MarkerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarkerViewHolder {

        val binding = ItemMarkerBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return MarkerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MarkerViewHolder, position: Int) {
        val marker = markers[position]
        holder.bind(marker, listener)
    }

    override fun getItemCount(): Int {
        return markers.size
    }

    class MarkerViewHolder(private val binding : ItemMarkerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(marker: TrailMarker, listener: NavigateAction?) {
            binding.tvLatitude.text = "Latitude: "+ marker.latitude.toString()
            binding.tvLongitude.text = "Longitude: "+ marker.longitude.toString()
            binding.ivDelete.setOnClickListener{
                    listener?.onDeleteIconClick(marker)
                }
            binding.ivEditMarker.setOnClickListener{
                listener?.onEditIconClick(marker)
            }
            binding.ivViewMarker.setOnClickListener{
                listener?.onViewIconClick(marker)
            }
        }
            }
}

