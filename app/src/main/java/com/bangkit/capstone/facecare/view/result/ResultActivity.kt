package com.bangkit.capstone.facecare.view.result

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.capstone.facecare.R
import com.bangkit.capstone.facecare.data.response.ScanResult
import com.bangkit.capstone.facecare.databinding.ActivityResultBinding
import com.bangkit.capstone.facecare.view.main.MainActivity
import com.bumptech.glide.Glide


class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val scanResult = intent.getParcelableExtra<ScanResult>("scanResult")

        val jenisPenyakit = binding.jenisPenyakit
        val detailPenyakit = binding.detailPenyakit

        jenisPenyakit.animate()
            .alpha(1f)
            .setDuration(1000)
            .setStartDelay(300)
            .start()

        detailPenyakit.animate()
            .alpha(1f)
            .setDuration(1000)
            .setStartDelay(600)
            .start()

        binding.back.setOnClickListener{
            val intent = Intent(this@ResultActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        if (scanResult != null) {
            Glide.with(this)
                .load(scanResult.imageUrl)
                .into(binding.facePhoto)
            binding.jenisPenyakit.text = scanResult.skinCondition
            binding.detailPenyakit.text = scanResult.treatmentTips

            val obatList = if (scanResult.skinCondition == "jerawat") {
                listOf(
                    Obat(R.drawable.labore, "Labore Mosturizer", "Bagus untuk menyembuhkan wajah jerawat", "https://www.tokopedia.com/watsons/labore-sensitive-skin-care-biomerepair-barrier-revive-cream-50-ml?extParam=ivf%3Dfalse%26keyword%3Dlabore%26search_id%3D20240616152116BB4F66F5AD4FE32CBVDO%26src%3Dsearch&refined=true"),
                    Obat(R.drawable.skintific, "Skintifc Facewash", "Bagus untuk menyembuhkan wajah jerawat", "https://www.tokopedia.com/skintific/skintific-5x-ceramide-low-ph-cleanser-for-sensitive-skin-15ml?extParam=ivf%3Dfalse%26keyword%3Dskintific%26search_id%3D20240616151744BFE2CE1CAB134713ER8T%26src%3Dsearch"),
                    Obat(R.drawable.anessa, "Anessa Suncreen", "Bagus untuk menyembuhkan wajah jerawat", "https://www.tokopedia.com/glamandglow/anessa-perfect-uv-suncreen-milk-60ml-sunblock-semi-matte-all-skin-type?extParam=ivf%3Dfalse%26keyword%3Danessa+suncreen%26search_id%3D202406161523486DEB34E2159658193ZZP%26src%3Dsearch&refined=true")
                )
            } else {
                listOf(
                    Obat(R.drawable.laneig, "Laneig Moisturizer", "Bagus untuk menjaga kulit wajah", "https://www.tokopedia.com/orviastore/produk-baru-laneige-water-blue-gel-moisturizer-intensive-50ml-moisture-repair-cream-gel-2024-new?extParam=ivf%3Dfalse%26keyword%3Dlaneige+moisturizer%26search_id%3D20240616144758BFE2CE1CAB13473686EA%26src%3Dsearch"),
                    Obat(R.drawable.skintific, "Skintific Facewash", "Bagus untuk menjaga kulit wajah", "https://www.tokopedia.com/skintific/skintific-5x-ceramide-low-ph-cleanser-for-sensitive-skin-15ml?extParam=ivf%3Dfalse%26keyword%3Dskintific%26search_id%3D20240616151744BFE2CE1CAB134713ER8T%26src%3Dsearch"),
                    Obat(R.drawable.facetology, "Facetology Suncreen", "Bagus untuk menjaga kulit wajah", "https://www.tokopedia.com/vignette3/facetology-triple-care-sunscreen-box-normal-77b9c?extParam=cmp%3D1%26ivf%3Dfalse%26keyword%3Dfacetology%26search_id%3D20240616151807A39580E87145200161RF%26src%3Dsearch")
                )
            }

            val obatAdapter = ObatAdapter(obatList)
            binding.rekomendasiObatRecyclerView.layoutManager = LinearLayoutManager(this)
            binding.rekomendasiObatRecyclerView.adapter = obatAdapter
        }
    }
}
