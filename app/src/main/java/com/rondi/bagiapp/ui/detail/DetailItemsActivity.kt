package com.rondi.bagiapp.ui.detail

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.rondi.bagiapp.R
import com.rondi.bagiapp.data.remote.response.ItemsItem
import com.rondi.bagiapp.data.remote.response.MyItem
import com.rondi.bagiapp.data.remote.response.SearchItem
import com.rondi.bagiapp.databinding.ActivityDetailItemsBinding
import com.rondi.bagiapp.utils.setImageFromUrl
import com.rondi.bagiapp.utils.showOKDialog
import com.rondi.bagiapp.utils.timeStamptoString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
@AndroidEntryPoint
class DetailItemsActivity : AppCompatActivity() {
    private var _binding: ActivityDetailItemsBinding? = null
    private val binding get() = _binding!!
    private var number = ""
    private val viewModel: DetailViewModel by viewModels()
    private var token: String = ""
    private var userId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailItemsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val item = intent.getParcelableExtra("EXTRA_DETAIL") as? Any


        if (item is ItemsItem?) {
            val items = item

            number = items?.nohp.toString()

            binding.apply {
                if (items != null) {
                    detailProfile.setImageFromUrl(items.photoUrl)
                    detailPhotoItems.setImageFromUrl(items.photoItems)
                    detailName.text = items.name
                    detailNohp.text = items.nohp
                    detailTitle.text = items.title
                    detailDate.text = items.createAt.timeStamptoString()
                    detailLoc.text = items.loc
                    detailKategori.text = items.kategori
                    detailDesc.text = items.description

                    val userId = items.userId.toString()
                    checkCurrentUser(userId)
                }
            }
        }


        if (item is MyItem?) {
            val items = item

            number = items?.nohp.toString()

            binding.apply {
                if (items != null) {
                    items.photoUrl?.let { detailProfile.setImageFromUrl(it) }
                    items.photoItems?.let { detailPhotoItems.setImageFromUrl(it) }
                    detailName.text = items.name
                    detailNohp.text = items.nohp
                    detailTitle.text = items.title
                    detailDate.text = items.createAt?.timeStamptoString()
                    detailLoc.text = items.loc
                    detailKategori.text = items.kategori
                    detailDesc.text = items.description

                    val userId = items.userId.toString()
                    checkCurrentUser(userId)
                }
            }
        }

        if (item is SearchItem) {
            val items = item

            number = items.nohp

            binding.apply {
                detailProfile.setImageFromUrl(items.photoUrl)
                detailPhotoItems.setImageFromUrl(items.photoItems)
                detailName.text = items.name
                detailNohp.text = items.nohp
                detailTitle.text = items.title
                detailDate.text = items.createAt.timeStamptoString()
                detailLoc.text = items.loc
                detailKategori.text = items.kategori
                detailDesc.text = items.description

                val userId = items.userId.toString()
                checkCurrentUser(userId)
            }
        }




        binding.btnRequest.setOnClickListener {

            openWhatsAppChat(number)
        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }



    }

    fun openWhatsAppChat(number: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://wa.me/$number")

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            showOKDialog(getString(R.string.title_message), getString(R.string.message_wa_notfound))
        }
    }

    private fun checkCurrentUser(user: String){
        lifecycleScope.launchWhenResumed {
            launch {
                    viewModel.getAuthUserId().collect { idUser ->
                        if (!idUser.isNullOrEmpty()) {
                            userId = idUser

                            if(userId == user){
                                binding.btnRequest.isEnabled = false
                                binding.btnRequest.setText(R.string.your_item)
                            }
                        }

                }
            }

        }
    }

}
