package com.max.movieapp.ui.movieDetails

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.max.movieapp.R
import com.max.movieapp.databinding.ActivityDetailsBinding
import com.max.movieapp.databinding.ActivitySearchBinding
import jp.wasabeef.glide.transformations.BlurTransformation

class DetailsActivity : AppCompatActivity() {
    private var _binding: ActivityDetailsBinding? =null
    private val binding: ActivityDetailsBinding get() =  _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


        val i = intent
        if(i!=null)
        {
            binding.apply {
                title.text = i.getStringExtra("title")
                year.text = i.getStringExtra("year")
                type.text = i.getStringExtra("type")
                Glide.with(applicationContext)
                    .load(i.getStringExtra("imageUrl"))
                    .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
                    .into(binding.blurBg)
                Glide.with(applicationContext)
                    .load(i.getStringExtra("imageUrl"))
                    .into(binding.poster)
            }
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}