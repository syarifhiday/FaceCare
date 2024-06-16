package com.bangkit.capstone.facecare.view.result

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.capstone.facecare.databinding.ItemCarouselBinding

data class Obat(val imageResId: Int, val name: String, val description: String, val url: String)

class ObatAdapter(private val obatList: List<Obat>) : RecyclerView.Adapter<ObatAdapter.ObatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObatViewHolder {
        val binding = ItemCarouselBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ObatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ObatViewHolder, position: Int) {
        holder.bind(obatList[position])
    }

    override fun getItemCount(): Int = obatList.size

    inner class ObatViewHolder(private val binding: ItemCarouselBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(obat: Obat) {
            binding.obatImage.setImageResource(obat.imageResId)
            binding.obatName.text = obat.name
            binding.obatDescription.text = obat.description
            binding.root.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(obat.url))
                binding.root.context.startActivity(intent)
            }
        }
    }
}

