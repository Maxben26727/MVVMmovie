package com.max.movieapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.max.movieapp.data.remote.response.Search
 import com.max.movieapp.databinding.MovieItemLayoutBinding
import com.max.movieapp.ui.movieDetails.DetailsActivity
import jp.wasabeef.glide.transformations.BlurTransformation

class MovieAdapter(context: Context) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {
    val mctx = context
    inner class MovieViewHolder(binding: MovieItemLayoutBinding): RecyclerView.ViewHolder(binding.root)
    {
        val binding = MovieItemLayoutBinding.bind(itemView)
    }

    private val differCallback = object : DiffUtil.ItemCallback<Search>() {
        override fun areItemsTheSame(oldItem: Search, newItem: Search): Boolean {
            return oldItem.imdbID == newItem.imdbID
        }

        override fun areContentsTheSame(oldItem: Search, newItem: Search): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = MovieItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val data = differ.currentList[position]
        with(holder) {
            Glide.with(holder.itemView)
                .load(data.Poster)
                .into(binding.iv)
            binding.title.text = data.Title
            binding.year.text = data.Year
            binding.type.text = data.Type

            holder.binding.root.setOnClickListener {
                val i = Intent(mctx,DetailsActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                i.putExtra("title",data.Title)
                i.putExtra("year",data.Year)
                i.putExtra("type",data.Type)
                i.putExtra("imageUrl",data.Poster)
                mctx.startActivity(i)
            }
        }
    }
}













