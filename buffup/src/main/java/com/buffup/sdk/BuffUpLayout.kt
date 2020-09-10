package com.buffup.sdk

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.buffup.sdk.custom.BuffUpViewModel
import com.buffup.sdk.custom.CustomViewState
import com.buffup.sdk.entities.BuffsEntity
import com.buffup.sdk.mvvm.MvvmLinearLayout
import kotlinx.android.synthetic.main.buff_up_layout.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream


class BuffUpLayout(
    context: Context,
    attrs: AttributeSet
): MvvmLinearLayout<CustomViewState, BuffUpViewModel>(context, attrs) {

    private var isAnswered = false
    private var timeToShow = 0

    override val viewModel = BuffUpViewModel()

    init {
        View.inflate(context, R.layout.buff_up_layout, this)
        initCloseCardListener()
    }

    private fun initCloseCardListener() {
        ivCloseCard.setOnClickListener {
            animateBuffCardOutThenHide()
        }
    }

    override fun onLifecycleOwnerAttached(lifecycleOwner: LifecycleOwner) {
        initBuffsLiveData(lifecycleOwner)
        initCountDownTimerLiveData(lifecycleOwner)
    }

    private fun initBuffsLiveData(lifecycleOwner: LifecycleOwner) {
        viewModel.getLiveData().observe(lifecycleOwner, Observer {
            renderUI(it)
        })
    }

    private fun initCountDownTimerLiveData(lifecycleOwner: LifecycleOwner) {
        viewModel.getCountDownTimerLiveData().observe(lifecycleOwner, Observer {
            tvCountDownTimer.text = it.toString()
            if(timeToShow > 0)
                pbCountDownTimer.progress = (100 - (it * 100 / timeToShow)).toFloat()

            if(it == 0)
                animateBuffCardOutThenHide()
        })
    }

    private fun renderUI(data: BuffsEntity?) {
        val result = data?.result
        tvSenderName.text = result?.author?.first_name
        tvQuestion.text = result?.question?.title

        val answers = result?.answers

        answers?.let {
            val mInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            for (answer in it.iterator()) {
                val answerView = mInflater.inflate(
                    R.layout.generic_answer_layout, null, false
                )
                llAnswersLayout.addView(answerView)

                val tvAnswer = answerView.findViewById<TextView>(R.id.tvAnswer)
                val sbAnswer = answerView.findViewById<SeekBar>(R.id.sbAnswer)

                tvAnswer.text = answer.title
                setUpAnswerGenericIcon(answer.image.image0.url, sbAnswer)
                setUpSeekBar(sbAnswer, tvAnswer)
            }
        }

        result?.time_to_show?.let {
            timeToShow = it
            viewModel.startCountDownTimer(it)
        }

        showBuffCard()
    }

    private fun setUpAnswerGenericIcon(url: String?, sbAnswer: SeekBar) {
        viewModel.viewModelScope.launch(Dispatchers.IO) {
            val image  = try {
                val iStream = java.net.URL(url).content as InputStream
                Drawable.createFromStream(iStream, "src name")
            } catch (e: Exception) {
                null
            }

            image?.let {
                withContext(Dispatchers.Main) {
                    sbAnswer.thumb = image
                }
            }
        }
    }

    private fun setUpSeekBar(
        sbAnswer: SeekBar,
        tvAnswer: TextView
    ) {
        sbAnswer.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (seekBar.progress > 50) {
                    seekBar.progress = 100
                    seekBar.isEnabled = false
                    isAnswered = true
                    viewModel.stopCountDownTimer()
                    hideCountDownTimer()
                    hideBuffCardAfterTwoSeconds()
                    changeSeekBarUI(sbAnswer, tvAnswer)
                } else {
                    seekBar.progress = 0
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(
                seekBar: SeekBar, progress: Int,
                fromUser: Boolean
            ) {
                if(isAnswered) { // To prevent the user from answering more than one time.
                    seekBar.progress = 0
                    seekBar.isEnabled = false
                }
                else
                    changeSeekBarColor(seekBar, tvAnswer, progress)
            }
        })
    }

    private fun hideCountDownTimer() {
        pbCountDownTimer.visibility = View.INVISIBLE
        tvCountDownTimer.visibility = View.INVISIBLE
    }

    private fun changeSeekBarUI(sbAnswer: SeekBar, tvAnswer: TextView) {
        tvAnswer.setTextColor(ContextCompat.getColor(context, R.color.white))
        tvQuestion.setTextColor(ContextCompat.getColor(context,
            R.color.question_text_after_answered_color))
    }

    private fun changeSeekBarColor(seekBar: SeekBar, tvAnswer: TextView, progress: Int) {
        when (progress) {
            0 -> {
                seekBar.setBackgroundColor(ContextCompat.getColor(context, R.color.answer_default_background_color))
                tvAnswer.setTextColor(ContextCompat.getColor(context, R.color.answer_text_color))
            }
            in 1..99 -> {
                seekBar.setBackgroundColor(ContextCompat.getColor(context, R.color.answer_progressed_background_color))
                tvAnswer.setTextColor(ContextCompat.getColor(context, R.color.white))
            }
            else -> {
                seekBar.setBackgroundColor(ContextCompat.getColor(context, R.color.answer_selected_background_color))
                tvAnswer.setTextColor(ContextCompat.getColor(context, R.color.white))
            }
        }
    }

    private fun animateBuffCardIn() {
        ValueAnimator.ofFloat(-360f, 0f).apply {
            duration = 700
            addUpdateListener { animation ->
                val animValue = animation.animatedValue as Float
                clRoot.translationX = animValue
                if(animValue == 0.0f)
                    animateSenderName()
            }
        }.start()
    }

    private fun animateSenderName() {
        showCardTopViews()

        ValueAnimator.ofFloat(ivSenderBox.height.toFloat(), 0f).apply {
            duration = 500
            addUpdateListener { animation ->
                val animValue = animation.animatedValue as Float
                ivSenderBox.translationY = animValue
                tvSenderName.translationY = animValue
                ivSenderImage.translationY = animValue
                ivCloseCard.translationY = animValue
            }
        }.start()
    }

    private fun hideBuffCardAfterTwoSeconds() {
        viewModel.viewModelScope.launch {
            delay(2000)
            animateBuffCardOutThenHide()
        }
    }

    private fun animateBuffCardOutThenHide() {
        ValueAnimator.ofFloat(0f, -360f).apply {
            duration = 500
            addUpdateListener { animation ->
                val animValue = animation.animatedValue as Float
                clRoot.translationX = animValue
                if(animValue == 360f)
                    clRoot.visibility = View.GONE
            }
        }.start()
    }

    private fun showBuffCard() {
        clRoot.visibility = View.VISIBLE
        hideCardTopViews()
        animateBuffCardIn()
    }

    private fun hideCardTopViews() {
        val gone = View.GONE
        ivSenderBox.visibility = gone
        tvSenderName.visibility = gone
        ivSenderImage.visibility = gone
        ivCloseCard.visibility = gone
    }

    private fun showCardTopViews() {
        val gone = View.VISIBLE
        ivSenderBox.visibility = gone
        tvSenderName.visibility = gone
        ivSenderImage.visibility = gone
        ivCloseCard.visibility = gone
    }

}
