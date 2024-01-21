package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.activity

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.R
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.setOnClickListener
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.ActivitySelectLanguageBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.base.BaseActivity
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.gone
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import plugin.adsdk.extras.NetworkChangeReceiver
import java.util.Locale

@AndroidEntryPoint
class SelectLanguageActivity : BaseActivity<ActivitySelectLanguageBinding>() {

    override fun getViewBinding() = ActivitySelectLanguageBinding.inflate(layoutInflater)

    private lateinit var languageAdapter: LanguageAdapter

    //    private var isSelected = false
    private var isFromSplash = false
    private var appliedClick = false

    private val languageOptions = arrayOf(
        Locale("en").toString(),
        Locale("ar").toString(),
        Locale("de").toString(),
        Locale("es").toString(),
        Locale("fr").toString(),
        Locale("hi").toString(),
        Locale("in").toString(),
        Locale("ja").toString(),
        Locale("ko").toString(),
        Locale("pt").toString(),
        Locale("ru").toString(),
        Locale("tr").toString(),
        Locale("vi").toString(),
        Locale("zh").toString()
    )

    private val languageFlag = arrayOf(
        R.drawable.flag_en,
        R.drawable.flag_ar,
        R.drawable.flag_de,
        R.drawable.flag_es,
        R.drawable.flag_fr,
        R.drawable.flag_hi,
        R.drawable.flag_in,
        R.drawable.flag_jp,
        R.drawable.flag_kr,
        R.drawable.flag_pt,
        R.drawable.flag_ru,
        R.drawable.flag_tr,
        R.drawable.flag_vi,
        R.drawable.flag_zh
    )
    var pos = 0
    override fun initData() {
        loadUI()
    }

    private fun loadUI() {
        manageMarquee()
        pos = languageOptions.indexOf(myPreferences.language)
        bannerAdLanguage(findViewById(R.id.banner_ad_container))

        isFromSplash = intent.getBooleanExtra("isFromSplash", false)
        binding.languageRecyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        languageAdapter = LanguageAdapter(this, languageFlag, languageOptions, pos)
        binding.languageRecyclerView.adapter = languageAdapter

        if (isFromSplash) {
            binding.ivBack.gone()
        } else {
            binding.ivBack.visible()
        }

        binding.ivBack.setOnClickListener(1000L) {
            finish()
        }

        binding.applyButton.setOnClickListener(500L) {
            /*val selectedLanguage = languageAdapter.getSelectedLanguage()
            myPreferences.language = selectedLanguage
            myPreferences.languageShown = true

            val locale = Locale(selectedLanguage)
            updateLocale(locale)
            if (isFromSplash) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                finish()
            }*/

            val selectedLanguage = languageAdapter.getSelectedLanguage()
            myPreferences.language = selectedLanguage
            myPreferences.languageShown = true

            val locale = Locale(selectedLanguage)
            updateLocale(locale)
            if (isFromSplash) {
                startActivity(Intent(this, CalculatorActivity::class.java))
                finish()
            } else {
                finish()
            }

        }
    }

    private fun manageMarquee() {
        binding.tvLangTitle.isSelected = true
        binding.applyButton.isSelected = true
    }

    private fun changeSelectionButton() {
        binding.applyButton.background =
            ContextCompat.getDrawable(this@SelectLanguageActivity, R.drawable.btn_red)
        binding.applyButton.setTextColor(
            ContextCompat.getColor(
                this@SelectLanguageActivity,
                R.color.white
            )
        )
    }

    inner class LanguageAdapter(
        private val context: Context,
        private val languageFlag: Array<Int>,
        private val languageOptions: Array<String>,
        private var selectedPosition: Int
    ) : RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_language, parent, false)
            return LanguageViewHolder(view)
        }

        override fun onBindViewHolder(
            holder: LanguageViewHolder, position: Int
        ) {
            holder.languageTextView.isSelected = true
            holder.bind(languageOptions[position], languageFlag[position])

            holder.itemView.setOnClickListener(500L) {
//                isSelected = true
//                changeSelectionButton()
                selectedPosition = position
                notifyDataSetChanged()
            }
            if (selectedPosition == position) {
                holder.itemView.setBackgroundColor(getColor(R.color.bgSelectLanguage))
                holder.imgCheck.setImageResource(R.drawable.ic_check_lang)
            } else {
                holder.itemView.setBackgroundColor(getColor(R.color.bgUnSelectLanguage))
                holder.imgCheck.setImageResource(R.drawable.ic_lang_uncheck)
            }
        }

        override fun getItemCount(): Int {
            return languageOptions.size
        }

        fun getSelectedLanguage(): String {
            return languageOptions[selectedPosition]
        }

        inner class LanguageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val languageTextView: TextView = itemView.findViewById(R.id.languageTextView)
            private val imgFlag: ImageView = itemView.findViewById(R.id.img_flag)
            val imgCheck: ImageView = itemView.findViewById(R.id.img_check)

            fun bind(language: String, flag: Int) {
                when (language) {
                    "en" -> {
                        languageTextView.text = "English"
                    }

                    "ar" -> {
                        languageTextView.text = "Arabic"
                    }

                    "de" -> {
                        languageTextView.text = "German"
                    }

                    "es" -> {
                        languageTextView.text = "Spanish"
                    }

                    "fr" -> {
                        languageTextView.text = "French"
                    }

                    "hi" -> {
                        languageTextView.text = "Hindi"
                    }

                    "in" -> {
                        languageTextView.text = "Indonesian"
                    }

                    "ja" -> {
                        languageTextView.text = "Japanese"
                    }

                    "ko" -> {
                        languageTextView.text = "Korean"
                    }

                    "pt" -> {
                        languageTextView.text = "Portuguese"
                    }

                    "ru" -> {
                        languageTextView.text = "Russian"
                    }

                    "tr" -> {
                        languageTextView.text = "Turkish"
                    }

                    "vi" -> {
                        languageTextView.text = "Vietnamese"
                    }

                    "zh" -> {
                        languageTextView.text = "Chinese"
                    }
                }
//                Glide.with(context).load(flag)
//                    .placeholder(R.mipmap.ic_launcher).centerCrop()
//                    .into(imgFlag)
            }
        }
    }

    override fun onBackPressed() {
        /*if (!isFromSplash)
            baseBackPressed({ finish() }, true)
        else
            finish()*/
        super.onBackPressed()
    }

    override fun networkStateChanged(state: NetworkChangeReceiver.NetworkState?) {
        super.networkStateChanged(state)
        if (state == NetworkChangeReceiver.NetworkState.CONNECTED) {
            binding.root.findViewById<FrameLayout>(R.id.banner_ad_container).visible()
        } else if (state == NetworkChangeReceiver.NetworkState.NOT_CONNECTED) {
            binding.root.findViewById<FrameLayout>(R.id.banner_ad_container).gone()
        }
    }

}