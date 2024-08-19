package com.example.storydicoding.view.detail

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.storydicoding.R
import com.example.storydicoding.databinding.ActivityStoryDetailBinding
import com.example.storydicoding.view.ViewModelFactory

class StoryDetailActivity : AppCompatActivity() {
    private val viewModel by viewModels<StoryDetailViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityStoryDetailBinding
    private lateinit var id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.detail_story)

        id = intent.getStringExtra(ID).toString()

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.getUser().observe(this) { user ->
            if (user != null && user.token!!.isNotEmpty()) {
                user.token.let { viewModel.getDetail(it, id) }
            }
        }

        viewModel.detailName.observe(this) { name ->
            binding.tvTitle.text = name
        }

        viewModel.detailDesc.observe(this) { description ->
            binding.tvDesc.text = description
        }

        viewModel.detailPhotoUrl.observe(this) { photoUrl ->
            Glide.with(this)
                .load(photoUrl)
                .into(binding.imgItemPhoto)
        }
    }

    companion object {
        const val ID = "id"
    }
}
