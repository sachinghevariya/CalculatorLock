package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.R
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.setOnClickListener
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.ActivitySetSecurityQuestionBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.base.BaseActivity
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.dialog.ConfirmSecurityDialog
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.gone
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.showMyDialog
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.showToast
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetSecurityQuestionActivity : BaseActivity<ActivitySetSecurityQuestionBinding>() {

    override fun getViewBinding() = ActivitySetSecurityQuestionBinding.inflate(layoutInflater)
    private var isPopupWindowShown = false
    private var isQuestionSelected = false
    private var isFromFirstTime = false
    private var isFromNumberReset = false
    private lateinit var popupWindow: PopupWindow
    private var answer = ""

    private fun marqueEnable() {
        binding.tvHeader.isSelected = true
        binding.tvQuestion.isSelected = true
        binding.tvHeaderQ.isSelected = true
        binding.tvHeaderA.isSelected = true
        binding.tvTips.isSelected = true
        binding.tvTipsTwo.isSelected = true
    }
    override fun initData() {
        marqueEnable()

        isFromFirstTime = intent.getBooleanExtra("isFromFirstTime", false)
        isFromNumberReset = intent.getBooleanExtra("isFromNumberReset", false)


        if (isFromNumberReset) {
            binding.ivArrow.gone()
            binding.tvQuestion.text = "${myPreferences.securityQuestion}"
        } else {
            binding.ivArrow.visible()
            binding.containerQuestion.setOnClickListener(300L) {
                showQuestionMenu(binding.containerQuestion)
            }
        }

        binding.btnConfirm.setOnClickListener(1000L) {

            if (isFromNumberReset) {
                if (answer.isNotEmpty()) {
                    if (myPreferences.securityAnswer.equals(answer, true)) {
                        showMyDialog(
                            this@SetSecurityQuestionActivity,
                            ConfirmSecurityDialog(
                                myPreferences.securityQuestion!!,
                                myPreferences.securityAnswer!!
                            ) {
                                setResult(Activity.RESULT_OK,Intent())
                                finish()
                            })
                    }else{
                        showToast(getString(R.string.wrong_answer))
                    }
                } else {
                    showToast(getString(R.string.msg_answer))
                }
            } else {
                if (isQuestionSelected) {
                    if (answer.isNotEmpty()) {
                        myPreferences.securityAnswer = answer
                        showMyDialog(
                            this@SetSecurityQuestionActivity,
                            ConfirmSecurityDialog(
                                myPreferences.securityQuestion!!,
                                myPreferences.securityAnswer!!
                            ) {
                                if (isFromFirstTime) {
                                    showInterstitial {
                                        startActivity(Intent(this@SetSecurityQuestionActivity, MainActivity::class.java))
                                        finish()
                                    }
                                } else {
                                    onBackPressed()
                                }
                            })

                    } else {
                        showToast(getString(R.string.msg_answer))
                    }
                } else {
                    showToast(getString(R.string.msg_question))
                }
            }

        }

        binding.etAnswer.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                try {
                    answer = s.toString().trim()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })

    }

    private fun showQuestionMenu(showButton: ViewGroup) {
        if (isPopupWindowShown) {
            isPopupWindowShown = false
            popupWindow?.dismiss()
            return
        }

        val questions = listOf(
            "Where were you born?",
            "What is your birthday?",
            "What’s your father’s/mother’s name?",
            "What’s your brother’s/sister’s name?",
            "What’s your boyfriend’s/girlfriend’s name?"
        )

        val popupView = LayoutInflater.from(this).inflate(R.layout.popup_menu, null)
        popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val questionListView = popupView.findViewById<ListView>(R.id.questionListView)
        val adapter = CustomAdapter(this, questions)
        questionListView.adapter = adapter

        questionListView.setOnItemClickListener { _, _, position, _ ->
            val selectedQuestion = questions[position]
            myPreferences.securityQuestion = selectedQuestion
            isQuestionSelected = true
            isPopupWindowShown = false
            binding.tvQuestion.text = selectedQuestion
            popupWindow.dismiss()
        }

        popupWindow.showAsDropDown(showButton)
        isPopupWindowShown = true
    }

    inner class CustomAdapter(context: Context, private val questions: List<String>) :
        ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, questions) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getView(position, convertView, parent)
            val textView = view.findViewById<TextView>(android.R.id.text1)
            textView.setTextColor(ContextCompat.getColor(context, R.color.textColorWhite))
            return view
        }
    }

    override fun onBackPressed() {
        /*if (isFromFirstTime) {
            finish()
        } else {
            baseBackPressed({ finish() }, true)
        }*/
        super.onBackPressed()
    }
}