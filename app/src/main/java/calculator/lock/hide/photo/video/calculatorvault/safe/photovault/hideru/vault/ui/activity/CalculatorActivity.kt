package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.MyApp
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.R
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.ActivityCalculatorNewBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.base.BaseActivity
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.dialog.SetPasswordHintDialog
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.gone
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.inVisible
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.showBottomSheet
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.showToast
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.visible
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import dagger.hilt.android.AndroidEntryPoint
import kotlin.system.exitProcess

@AndroidEntryPoint
class CalculatorActivity : BaseActivity<ActivityCalculatorNewBinding>() {

    private var currentInput = ""
    private var currentOperator = ""
    private var previousValue = ""
    private var isNewInput = true

    private var isPassModeEnable = true
    private var currentPassInput = ""
    private var confirmPassInput = ""
    private var isNewInputPass = true
    private var checkPass = false
    private var isReset = false
    private var isFromLock = false
    private var counter = 0
    private lateinit var themeBg: Drawable
    private lateinit var themeBgNumber: Drawable
    private lateinit var themeBgEqual: Drawable
    private var textColor: Int = -1
    private var isFromNumberReset = false
    private var accumulatedExpression = ""
    private var lastInputValue = ""

    override fun getViewBinding() = ActivityCalculatorNewBinding.inflate(layoutInflater)

    override fun initData() {

        isReset = intent.getBooleanExtra("isReset", false)
        isFromLock = intent.getBooleanExtra("isFromLock", false)

        if (isFromLock) {
            binding.containerMsgPass.gone()
            binding.containerPass.gone()
            binding.containerInput.visible()
            binding.bottomPassView.gone()
            binding.topPassView.gone()
            binding.ivTheme.visible()
            binding.calDot.isEnabled = true
            binding.calPlus.isEnabled = true
            binding.calMinus.isEnabled = true
            binding.calMultiply.isEnabled = true
            binding.calDiv.isEnabled = true
            binding.calMod.isEnabled = true

            isPassModeEnable = false
            currentPassInput = ""
            confirmPassInput = ""
            isNewInputPass = true
            checkPass = false
            isReset = false
            counter = 0
        }
        manageTheme()

        XXPermissions.with(this@CalculatorActivity)
            .permission(Permission.MANAGE_EXTERNAL_STORAGE)
            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: List<String?>, allGranted: Boolean) {
                    if (!allGranted) {
                        return
                    }
                    initView()
                }
            })
    }

    private fun initView() {
        manageViewForFirstTime()
        updateEditText()
        binding.calEqual.setOnClickListener {
            if (isPassModeEnable) {
                managePassModeEqual()
            } else {
                handleEquals()
            }
        }

        binding.calClear.setOnClickListener { clearCalculator() }

        binding.cal00.setOnClickListener {
            if (!isPassModeEnable) {
                handleNumericButtonClick(binding.tv00.text.toString())
            }
        }
        binding.cal0.setOnClickListener {
            handleNumericButtonClick(binding.tv0.text.toString())
        }
        binding.cal1.setOnClickListener {
            handleNumericButtonClick(binding.tv1.text.toString())
        }
        binding.cal2.setOnClickListener {
            handleNumericButtonClick(binding.tv2.text.toString())
        }
        binding.cal3.setOnClickListener {
            handleNumericButtonClick(binding.tv3.text.toString())
        }
        binding.cal4.setOnClickListener {
            handleNumericButtonClick(binding.tv4.text.toString())
        }
        binding.cal5.setOnClickListener {
            handleNumericButtonClick(binding.tv5.text.toString())
        }
        binding.cal6.setOnClickListener {
            handleNumericButtonClick(binding.tv6.text.toString())
        }
        binding.cal7.setOnClickListener {
            handleNumericButtonClick(binding.tv7.text.toString())
        }
        binding.cal8.setOnClickListener {
            handleNumericButtonClick(binding.tv8.text.toString())
        }
        binding.cal9.setOnClickListener {
            handleNumericButtonClick(binding.tv9.text.toString())
        }
        binding.calDot.setOnClickListener {
            handleNumericButtonClick(binding.tvDot.text.toString())
        }

        binding.calDel.setOnClickListener {
            clearCurrentInput()
        }
        binding.calPlus.setOnClickListener {
            handleOperatorButtonClick(binding.tvPlus.text.toString())
        }
        binding.calMinus.setOnClickListener {
            handleOperatorButtonClick(binding.tvMinus.text.toString())
        }
        binding.calMultiply.setOnClickListener {
            handleOperatorButtonClick(binding.tvMultiply.text.toString())
        }
        binding.calDiv.setOnClickListener {
            handleOperatorButtonClick(binding.tvDiv.text.toString())
        }
        binding.calMod.setOnClickListener {
            handleOperatorButtonClick(binding.tvMod.text.toString())
        }
//        binding.calPlusMinus.setOnClickListener {
//            toggleSign()
//        }

        binding.ivGuide.setOnClickListener {
            showBottomSheet(this@CalculatorActivity, SetPasswordHintDialog())
        }
        binding.ivTheme.setOnClickListener {
            startActivityForResult(Intent(this@CalculatorActivity, ThemeActivity::class.java), 1010)
        }
        binding.ivReset.setOnClickListener {
            gotoResetPass()
        }


        setRippleEffect(binding.calEqual)
        setRippleEffect(binding.cal0)
        setRippleEffect(binding.cal1)
        setRippleEffect(binding.cal2)
        setRippleEffect(binding.cal3)
        setRippleEffect(binding.cal4)
        setRippleEffect(binding.cal5)
        setRippleEffect(binding.cal6)
        setRippleEffect(binding.cal7)
        setRippleEffect(binding.cal8)
        setRippleEffect(binding.cal9)
        setRippleEffect(binding.calDel)
    }

    private fun updateExpression(expression: String) {
        binding.expressionTextView.text = expression
    }

    private fun setRippleEffect(view: ConstraintLayout) {
        view.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    view.translationY = 10f
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    view.translationY = 0f
                }
            }
            false
        }
    }

    private fun removeRippleEffect(view: ConstraintLayout) {
        view.setOnTouchListener(null)
    }

    private fun toggleSign() {
        if (currentInput.isNotEmpty() && currentInput != "0") {
            currentInput = if (currentInput.startsWith("-")) {
                currentInput.substring(1) // Remove the negative sign
            } else {
                "- $currentInput" // Add a negative sign
            }

            accumulatedExpression = accumulatedExpression.replaceLast(currentInput)
            lastInputValue = currentInput
            updateExpression(accumulatedExpression)

            updateEditText()
        }
    }

    private fun String.replaceLast(newValue: String): String {
        val lastIndex = this.lastIndexOf(lastInputValue)
        if (lastIndex == -1) {
            return this
        }
        val sb = StringBuilder(this)
        sb.replace(lastIndex, lastIndex + lastInputValue.length, newValue)
        return sb.toString()
    }

    private fun clearCurrentInput() {
        if (isPassModeEnable) {
            if (currentPassInput.isNotEmpty()) {
                counter--
                currentPassInput = currentPassInput.substring(0, currentPassInput.length - 1)
                if (counter >= 0) {
                    updateView(true)
                }
            }
        } else {
            if (currentInput.isNotEmpty()) {
                currentInput = currentInput.substring(0, currentInput.length - 1)
                updateEditText()
            }
            var expressionData = binding.expressionTextView.text.toString().trim()
            if (expressionData.isNotEmpty()) {
                expressionData = expressionData.substring(0, expressionData.length - 1)
                binding.expressionTextView.text = "$expressionData"
            }
//            currentInput = ""
//            updateEditText()
        }
    }

    private fun handleOperatorButtonClick(operator: String) {
        currentOperator = ""
        currentOperator = operator
        isNewInput = true
        previousValue = currentInput
        currentInput = ""
        updateEditText()

//        val updatedExpression = "$previousValue $currentOperator "
//        updateExpression(updatedExpression)
        accumulatedExpression += currentInput + " " + operator + " "
//        accumulatedExpression += " $operator "
        updateExpression(accumulatedExpression)

    }

    private fun handleNumericButtonClick(text: String) {
        if (isPassModeEnable) {
            if (counter <= 3) {
                if (isNewInputPass) {
                    currentPassInput = text
                    isNewInputPass = false
                } else {
                    currentPassInput += text
                }
                updateView(false)
                counter++
            }
        } else {
            if (isNewInput) {
                currentInput = text
                isNewInput = false
            } else {
                currentInput += text
            }
            accumulatedExpression += text
            updateExpression(accumulatedExpression)
            updateEditText()
            lastInputValue = currentInput

        }
    }

    private fun managePassModeEqual() {
        if (checkPass) {
            if (currentPassInput == confirmPassInput) {
                myPreferences.isPassSet = true
                myPreferences.passApp = confirmPassInput
                gotoNext()
            } else {
                showToast(getString(R.string.msg_wrong_pass))
            }
        } else {
            if (currentPassInput.isNotEmpty()) {
                checkPass = true
                confirmPassInput = currentPassInput
                isNewInput = true
                currentPassInput = ""
                counter = 0
                binding.tvPass4.text = ""
                binding.tvPass3.text = ""
                binding.tvPass2.text = ""
                binding.tvPass1.text = ""
                binding.bottomPassView.visible()
                binding.topPassView.visible()
            }
        }
    }

    private fun handleEquals() {
        if (isNewInput) return

        if (currentInput.isEmpty()) {
            return
        }

        if (currentInput == getString(R.string.default_pass)) {
            askSecurityQuestion()
            return
        }

        val expressionToEvaluate = accumulatedExpression.ifEmpty {
            currentInput
        }

        if (expressionToEvaluate == myPreferences.passApp) {
            gotoNext()
            return
        }

        try {
            val expressionToEvaluate = accumulatedExpression.ifEmpty {
                currentInput
            }
            val result = evaluateExpression(expressionToEvaluate)
            currentInput = result.toString()
            lastInputValue = currentInput
            currentOperator = ""
            previousValue = currentInput
            isNewInput = true
            updateEditText()
            accumulatedExpression = "$result"
            updateExpression(accumulatedExpression)
        } catch (e: NumberFormatException) {
            clearCalculator()
            e.printStackTrace()
        }

        binding.expressionTextView.text = ""

    }

    private fun evaluateExpression(expression: String): Double {
        val sanitizedExpression = preprocessExpression(expression)
        val parts = sanitizedExpression.split(" ")
        var result = 0.0
        var operator = "+"

        for (part in parts) {
            if (part == "+" || part == "-" || part == "*" || part == "/" || part == "%") {
                operator = part
            } else {
                val operand = part.toDouble()
                when (operator) {
                    "+" -> result += operand
                    "-" -> result -= operand
                    "*" -> result *= operand
                    "/" -> result /= operand
                    "%" -> result %= operand
                }
            }
        }
        return result
    }

    private fun preprocessExpression(expression: String): String {
        var sanitizedExpression = expression.trim()

        // Ensure that the expression starts with a valid operator
        if (!sanitizedExpression.startsWith("-") && !sanitizedExpression.startsWith("+")) {
            sanitizedExpression = "+$sanitizedExpression"
        }

        // Replace instances of "+ -" with "-"
        sanitizedExpression = sanitizedExpression.replace("+ -", "-")

        // Replace instances of "- -" with "+"
        sanitizedExpression = sanitizedExpression.replace("- -", "+")

        // Replace instances of "+-" with "-"
        sanitizedExpression = sanitizedExpression.replace("\\+-", "-")

        // Add spaces between the operator and the number
        sanitizedExpression = sanitizedExpression.replace(Regex("([+-])(\\d)"), "$1 $2")

        return sanitizedExpression
    }

    private fun askSecurityQuestion() {
        if (myPreferences.securityAnswer != "") {
            startActivityForResult(
                Intent(
                    this,
                    SetSecurityQuestionActivity::class.java
                ).putExtra("isFromNumberReset", true), 5050
            )
        } else {
            showToast(getString(R.string.first_set_answer))
        }
    }

    private fun clearCalculator() {
        currentInput = ""
        currentOperator = ""
        previousValue = ""
        isNewInput = true
        binding.expressionTextView.text = ""
        accumulatedExpression = ""
        updateEditText()
    }

    private fun updateEditText() {
        binding.etValue.setText(currentInput)
        binding.etValue.setSelection(currentInput.length)
    }

    private fun manageViewForFirstTime() {
        if (isFromLock && isFromNumberReset) {
            binding.calDot.isEnabled = false
            binding.calPlus.isEnabled = false
            binding.calMinus.isEnabled = false
            binding.calMultiply.isEnabled = false
            binding.calDiv.isEnabled = false
            binding.calMod.isEnabled = false
            isPassModeEnable = true
            binding.containerMsgPass.visible()
            binding.containerPass.visible()
            binding.containerInput.gone()
            binding.bottomPassView.visible()
            binding.topPassView.gone()
            binding.ivTheme.gone()

            removeRippleEffect(binding.calDot)
            removeRippleEffect(binding.calClear)
            removeRippleEffect(binding.calPlus)
            removeRippleEffect(binding.calMinus)
            removeRippleEffect(binding.calMultiply)
            removeRippleEffect(binding.calDiv)
            removeRippleEffect(binding.calMod)
            removeRippleEffect(binding.cal00)
//            removeRippleEffect(binding.calPlusMinus)
        } else if (isFromLock) {
            binding.calDot.isEnabled = true
            binding.calPlus.isEnabled = true
            binding.calMinus.isEnabled = true
            binding.calMultiply.isEnabled = true
            binding.calDiv.isEnabled = true
            binding.calMod.isEnabled = true

            setRippleEffect(binding.calDot)
            setRippleEffect(binding.calClear)
            setRippleEffect(binding.calPlus)
            setRippleEffect(binding.calMinus)
            setRippleEffect(binding.calMultiply)
            setRippleEffect(binding.calDiv)
            setRippleEffect(binding.calMod)
            setRippleEffect(binding.cal00)
//            setRippleEffect(binding.calPlusMinus)
        } else {
            if (isReset) {
                binding.calDot.isEnabled = false
                binding.calPlus.isEnabled = false
                binding.calMinus.isEnabled = false
                binding.calMultiply.isEnabled = false
                binding.calDiv.isEnabled = false
                binding.calMod.isEnabled = false
                isPassModeEnable = true
                binding.containerMsgPass.visible()
                binding.containerPass.visible()
                binding.containerInput.gone()
                binding.bottomPassView.visible()
                binding.topPassView.gone()
                binding.ivTheme.gone()
                removeRippleEffect(binding.calDot)
                removeRippleEffect(binding.calClear)
                removeRippleEffect(binding.calPlus)
                removeRippleEffect(binding.calMinus)
                removeRippleEffect(binding.calMultiply)
                removeRippleEffect(binding.calDiv)
                removeRippleEffect(binding.calMod)
                removeRippleEffect(binding.cal00)
//                removeRippleEffect(binding.calPlusMinus)
            } else {
                if (myPreferences.isPassSet) {
                    isPassModeEnable = false
                    binding.containerMsgPass.gone()
                    binding.containerPass.gone()
                    binding.containerInput.visible()
                    binding.bottomPassView.gone()
                    binding.topPassView.gone()
                    binding.ivTheme.visible()
                    binding.calDot.isEnabled = true
                    binding.calPlus.isEnabled = true
                    binding.calMinus.isEnabled = true
                    binding.calMultiply.isEnabled = true
                    binding.calDiv.isEnabled = true
                    binding.calMod.isEnabled = true

                    setRippleEffect(binding.calDot)
                    setRippleEffect(binding.calClear)
                    setRippleEffect(binding.calPlus)
                    setRippleEffect(binding.calMinus)
                    setRippleEffect(binding.calMultiply)
                    setRippleEffect(binding.calDiv)
                    setRippleEffect(binding.calMod)
                    setRippleEffect(binding.cal00)
//                    setRippleEffect(binding.calPlusMinus)
                } else {
                    binding.calDot.isEnabled = false
                    binding.calPlus.isEnabled = false
                    binding.calMinus.isEnabled = false
                    binding.calMultiply.isEnabled = false
                    binding.calDiv.isEnabled = false
                    binding.calMod.isEnabled = false
                    isPassModeEnable = true
                    binding.containerMsgPass.visible()
                    binding.containerPass.visible()
                    binding.containerInput.gone()
                    binding.bottomPassView.visible()
                    binding.topPassView.gone()
                    binding.ivTheme.gone()
                    removeRippleEffect(binding.calDot)
                    removeRippleEffect(binding.calClear)
                    removeRippleEffect(binding.calPlus)
                    removeRippleEffect(binding.calMinus)
                    removeRippleEffect(binding.calMultiply)
                    removeRippleEffect(binding.calDiv)
                    removeRippleEffect(binding.calMod)
                    removeRippleEffect(binding.cal00)
//                    removeRippleEffect(binding.calPlusMinus)
                }
            }
        }
    }

    private fun updateView(isBackPress: Boolean) {
        if (isBackPress) {
            when (counter) {
                0 -> {
                    binding.tvPass4.text = ""
                }

                1 -> {
                    binding.tvPass3.text = ""
                }

                2 -> {
                    binding.tvPass2.text = ""
                }

                3 -> {
                    binding.tvPass1.text = ""
                }
            }
        } else {
            when (counter) {
                0 -> {
                    binding.tvPass4.text = "*"
                }

                1 -> {
                    binding.tvPass3.text = "*"
                }

                2 -> {
                    binding.tvPass2.text = "*"
                }

                3 -> {
                    binding.tvPass1.text = "*"
                }
            }
        }
    }

    private fun gotoNext() {
        if (isPassModeEnable) {
            if (isFromLock && isFromNumberReset) {
                finish()
            } else {
                if (isReset || isFromLock) {
                    onBackPressed()
                } else {
                    if (isFromNumberReset) {
                        showInterstitial {
                            startActivity(Intent(this@CalculatorActivity, MainActivity::class.java))
                            finish()
                        }
                    } else {
                        startActivity(
                            Intent(
                                this,
                                SetSecurityQuestionActivity::class.java
                            ).putExtra("isFromFirstTime", true)
                        )
                        finish()
                    }

                }
            }
        } else {
            if (isFromLock) {
                finish()
            } else {
                if (myPreferences.securityAnswer == "") {
                    startActivity(
                        Intent(
                            this,
                            SetSecurityQuestionActivity::class.java
                        ).putExtra("isFromFirstTime", true)
                    )
                    finish()
                } else {
                    showInterstitial {
                        startActivity(Intent(this@CalculatorActivity, MainActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }

    private fun gotoResetPass() {
        myPreferences.isPassSet = false
//        myPreferences.securityQuestion = ""
        currentInput = ""
        currentOperator = ""
        previousValue = ""
        isNewInput = true

        isPassModeEnable = true
        currentPassInput = ""
        confirmPassInput = ""
        isNewInputPass = true
        checkPass = false
//        isReset = false
        counter = 0
        binding.tvPass4.text = ""
        binding.tvPass3.text = ""
        binding.tvPass2.text = ""
        binding.tvPass1.text = ""
        binding.expressionTextView.text = ""
        initView()
    }

    override fun onBackPressed() {
        if (isFromLock) {
            finishAffinity()
            exitProcess(0)
        } else {
            if (isReset) {
                showInterstitial {
                    finish()
                }
            } else {
                finishAffinity()
                exitProcess(0)
            }
        }
    }

    private fun manageTheme() {

        when (myPreferences.themeSelected) {
            "Theme0" -> {
                themeBg = ContextCompat.getDrawable(this, R.drawable.btn_cal_bg_theme_0)!!
                themeBgNumber = ContextCompat.getDrawable(this, R.drawable.btn_cal_bg_theme_pri_0)!!
                themeBgEqual = ContextCompat.getDrawable(this, R.drawable.btn_cal_bg_eq_theme_0)!!
                textColor = ContextCompat.getColor(this, R.color.white)
                binding.buttonView.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        android.R.color.transparent
                    )
                )
                visibleGoneLines(false, isVisibleView = false)


                binding.calEqual.background = themeBgEqual
                binding.calClear.background = themeBg
                binding.cal0.background = themeBgNumber
                binding.cal1.background = themeBgNumber
                binding.cal2.background = themeBgNumber
                binding.cal3.background = themeBgNumber
                binding.cal4.background = themeBgNumber
                binding.cal5.background = themeBgNumber
                binding.cal6.background = themeBgNumber
                binding.cal7.background = themeBgNumber
                binding.cal8.background = themeBgNumber
                binding.cal9.background = themeBgNumber
                binding.cal00.background = themeBgNumber
                binding.calDot.background = themeBgNumber
                binding.calDel.background = themeBg
                binding.calPlus.background = themeBg
                binding.calMinus.background = themeBg
                binding.calMultiply.background = themeBg
                binding.calDiv.background = themeBg
                binding.calMod.background = themeBg
//                binding.calPlusMinus.background = themeBg

            }

            "Theme1" -> {
                themeBg = ContextCompat.getDrawable(this, R.drawable.btn_cal_bg_theme_1)!!
                themeBgNumber = ContextCompat.getDrawable(this, R.drawable.btn_cal_bg_theme_pri_0)!!
                themeBgEqual = ContextCompat.getDrawable(this, R.drawable.btn_cal_bg_eq_theme_1)!!
                textColor = ContextCompat.getColor(this, R.color.white)
                binding.buttonView.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        android.R.color.transparent
                    )
                )
                visibleGoneLines(false, isVisibleView = false)

                binding.calEqual.background = themeBgEqual
                binding.calClear.background = themeBg
                binding.cal0.background = themeBgNumber
                binding.cal1.background = themeBgNumber
                binding.cal2.background = themeBgNumber
                binding.cal3.background = themeBgNumber
                binding.cal4.background = themeBgNumber
                binding.cal5.background = themeBgNumber
                binding.cal6.background = themeBgNumber
                binding.cal7.background = themeBgNumber
                binding.cal8.background = themeBgNumber
                binding.cal9.background = themeBgNumber
                binding.cal00.background = themeBgNumber
                binding.calDot.background = themeBgNumber
                binding.calDel.background = themeBgEqual
                binding.calPlus.background = themeBgEqual
                binding.calMinus.background = themeBgEqual
                binding.calMultiply.background = themeBgEqual
                binding.calDiv.background = themeBgEqual
                binding.calMod.background = themeBg
//                binding.calPlusMinus.background = themeBg


            }

            "Theme2" -> {
                themeBg = ContextCompat.getDrawable(this, R.drawable.btn_cal_bg_theme_2)!!
//                val themeBgView = ContextCompat.getDrawable(this, R.drawable.btn_cal_bg_theme_2)!!
                themeBgNumber = ContextCompat.getDrawable(this, R.drawable.btn_cal_bg_theme_pri_2)!!
                themeBgEqual = ContextCompat.getDrawable(this, R.drawable.btn_cal_bg_eq_theme_2)!!
                textColor = ContextCompat.getColor(this, R.color.white)
//                binding.buttonView.setBackgroundColor(ContextCompat.getColor(this, R.color.bgView))
                binding.buttonView.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        android.R.color.transparent
                    )
                )
//                binding.viewCal0.background = themeBgView
//                binding.viewCal1.background = themeBgView
//                binding.viewCal2.background = themeBgView
//                binding.viewCal3.background = themeBgView
//                binding.viewCal4.background = themeBgView
//                binding.viewCal5.background = themeBgView
//                binding.viewCal6.background = themeBgView
//                binding.viewCal7.background = themeBgView
//                binding.viewCal8.background = themeBgView
//                binding.viewCal9.background = themeBgView
//                binding.viewCalDot.background = themeBgView
//                binding.viewCalEqual.background = themeBgEqual

                visibleGoneLines(false, isVisibleView = false)

                binding.calEqual.background = themeBgEqual
                binding.calClear.background = themeBg
                binding.cal0.background = themeBgNumber
                binding.cal1.background = themeBgNumber
                binding.cal2.background = themeBgNumber
                binding.cal3.background = themeBgNumber
                binding.cal4.background = themeBgNumber
                binding.cal5.background = themeBgNumber
                binding.cal6.background = themeBgNumber
                binding.cal7.background = themeBgNumber
                binding.cal8.background = themeBgNumber
                binding.cal9.background = themeBgNumber
                binding.cal00.background = themeBgNumber
                binding.calDot.background = themeBgNumber
                binding.calDel.background = themeBg
                binding.calPlus.background = themeBg
                binding.calMinus.background = themeBg
                binding.calMultiply.background = themeBg
                binding.calDiv.background = themeBg
                binding.calMod.background = themeBg
//                binding.calPlusMinus.background = themeBg


            }

            "Theme3" -> {
                themeBg = ContextCompat.getDrawable(this, R.drawable.btn_cal_bg_theme_3)!!
                themeBgEqual = ContextCompat.getDrawable(this, R.drawable.btn_cal_bg_eq_theme_3)!!
                textColor = ContextCompat.getColor(this, R.color.theme0)
//                binding.buttonView.setBackgroundColor(ContextCompat.getColor(this, R.color.bgView))
                binding.buttonView.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        android.R.color.transparent
                    )
                )
                visibleGoneLines(false, isVisibleView = false)
//                binding.viewCalEqual.visible()
//                binding.viewCalEqual.background = themeBgEqual
                binding.calEqual.background = themeBgEqual
                binding.calClear.background = themeBg
                binding.cal0.background = themeBg
                binding.cal1.background = themeBg
                binding.cal2.background = themeBg
                binding.cal3.background = themeBg
                binding.cal4.background = themeBg
                binding.cal5.background = themeBg
                binding.cal6.background = themeBg
                binding.cal7.background = themeBg
                binding.cal8.background = themeBg
                binding.cal9.background = themeBg
                binding.calDot.background = themeBg
                binding.calDel.background = themeBg
                binding.calPlus.background = themeBg
                binding.calMinus.background = themeBg
                binding.calMultiply.background = themeBg
                binding.calDiv.background = themeBg
                binding.calMod.background = themeBg
                binding.cal00.background = themeBg
//                binding.calPlusMinus.background = themeBg

            }

            else -> {
                themeBg = ContextCompat.getDrawable(this, R.drawable.btn_cal_bg_theme_0)!!
                themeBgEqual = ContextCompat.getDrawable(this, R.drawable.btn_cal_bg_eq_theme_0)!!
                textColor = ContextCompat.getColor(this, R.color.theme0)
                binding.buttonView.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        android.R.color.transparent
                    )
                )
                visibleGoneLines(false, isVisibleView = false)

                binding.calEqual.background = themeBgEqual
                binding.calClear.background = themeBg
                binding.cal0.background = themeBg
                binding.cal1.background = themeBg
                binding.cal2.background = themeBg
                binding.cal3.background = themeBg
                binding.cal4.background = themeBg
                binding.cal5.background = themeBg
                binding.cal6.background = themeBg
                binding.cal7.background = themeBg
                binding.cal8.background = themeBg
                binding.cal9.background = themeBg
                binding.calDot.background = themeBg
                binding.calDel.background = themeBg
                binding.calPlus.background = themeBg
                binding.calMinus.background = themeBg
                binding.calMultiply.background = themeBg
                binding.calDiv.background = themeBg
                binding.calMod.background = themeBg
                binding.cal00.background = themeBg
//                binding.calPlusMinus.background = themeBg

            }
        }

        binding.tvClear.setTextColor(textColor)
        binding.tvDel.setTextColor(textColor)
        binding.tvPlus.setTextColor(textColor)
        binding.tvMinus.setTextColor(textColor)
        binding.tvMultiply.setTextColor(textColor)
        binding.tvDiv.setTextColor(textColor)
        binding.tvMod.setTextColor(textColor)
//        binding.tv00.setTextColor(textColor)
//        binding.tvPlusMinus.setTextColor(textColor)
    }

    private fun visibleGoneLines(isVisible: Boolean, isVisibleView: Boolean) {
        if (isVisible) {
            binding.view9.visible()
            binding.view8.visible()
            binding.view7.visible()
            binding.view6.visible()
            binding.view5.visible()
            binding.view4.visible()
            binding.view22.visible()
            binding.view20.visible()
            binding.view19.visible()
            binding.view18.visible()
            binding.view17.visible()
            binding.view16.visible()
            binding.view15.visible()
            binding.view21.visible()
            binding.view14.visible()
            binding.view13.visible()
            binding.view12.visible()
            binding.view11.visible()
            binding.view10.visible()
            binding.view22.visible()

        } else {
            binding.view9.inVisible()
            binding.view8.inVisible()
            binding.view7.inVisible()
            binding.view6.inVisible()
            binding.view5.inVisible()
            binding.view4.inVisible()
            binding.view22.inVisible()
            binding.view20.inVisible()
            binding.view19.inVisible()
            binding.view18.inVisible()
            binding.view17.inVisible()
            binding.view16.inVisible()
            binding.view15.inVisible()
            binding.view21.inVisible()
            binding.view14.inVisible()
            binding.view13.inVisible()
            binding.view12.inVisible()
            binding.view11.inVisible()
            binding.view10.inVisible()
            binding.view22.inVisible()
        }

        if (isVisibleView) {
            binding.viewCalEqual.visible()
            binding.viewCalClear.visible()
            binding.viewCal0.visible()
            binding.viewCal1.visible()
            binding.viewCal2.visible()
            binding.viewCal3.visible()
            binding.viewCal4.visible()
            binding.viewCal5.visible()
            binding.viewCal6.visible()
            binding.viewCal7.visible()
            binding.viewCal8.visible()
            binding.viewCal9.visible()
            binding.viewCalDot.visible()
            binding.viewCalDel.visible()
            binding.viewCalPlus.visible()
            binding.viewCalMinus.visible()
            binding.viewCalMultiply.visible()
            binding.viewCalDiv.visible()
            binding.viewCalMod.visible()
            binding.viewCal00.visible()
//            binding.viewCalPlusMinus.visible()
        } else {
            binding.viewCalEqual.gone()
            binding.viewCalClear.gone()
            binding.viewCal0.gone()
            binding.viewCal1.gone()
            binding.viewCal2.gone()
            binding.viewCal3.gone()
            binding.viewCal4.gone()
            binding.viewCal5.gone()
            binding.viewCal6.gone()
            binding.viewCal7.gone()
            binding.viewCal8.gone()
            binding.viewCal9.gone()
            binding.viewCalDot.gone()
            binding.viewCalDel.gone()
            binding.viewCalPlus.gone()
            binding.viewCalMinus.gone()
            binding.viewCalMultiply.gone()
            binding.viewCalDiv.gone()
            binding.viewCalMod.gone()
            binding.viewCal00.gone()
//            binding.viewCalPlusMinus.gone()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1010 && resultCode == Activity.RESULT_OK) {
            manageTheme()
        }
        if (requestCode == 5050 && resultCode == Activity.RESULT_OK) {
            isFromNumberReset = true
            gotoResetPass()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isFromNumberReset) {
            try {
                MyApp.getInstanceAppOpen()?.hideCustomBackground()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}