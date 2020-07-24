package com.thanhduc91tpk.a7minuteworkouts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_exercise.*
import java.util.*
import kotlin.collections.ArrayList

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var restTimer  : CountDownTimer? = null
    private var restProgress = 0

    private var exerciseTimer  : CountDownTimer? = null
    private var exerciseProgress = 0
    private var exerciseTimerDuration: Long = 5

    private var exerciseList: ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1

    private var tts : TextToSpeech? = null

    private var exerciseAdapter : ExerciseStatusAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)

        setSupportActionBar(toolbar_exercise_activity)
        val actionbar = supportActionBar
        if(actionbar!=null){
            actionbar.setDisplayHomeAsUpEnabled(true)
        }

        toolbar_exercise_activity.setNavigationOnClickListener{
            onBackPressed()
        }

        tts = TextToSpeech(this,this)

        setupRestView()

        exerciseList = Constants.defaultExerciseList()

        setupExerciseStatusRecyclerView()
    }

    override fun onDestroy() {
        if(restTimer != null){
            restTimer!!.cancel()
            restProgress = 0
        }

        if(exerciseTimer != null){
            exerciseTimer!!.cancel()
            exerciseProgress = 0
        }

        if(tts!=null){
            tts!!.stop()
            tts!!.shutdown()
        }

        super.onDestroy()
    }

    private fun setRestProgressBar(){
        progressBar.progress = restProgress
        restTimer = object : CountDownTimer(10000,1000){
            override fun onTick(millisUntilFinished: Long) {
                restProgress++
                progressBar.progress = 10-restProgress
                tvTimer.text = (10-restProgress).toString()
            }

            override fun onFinish() {
                currentExercisePosition++
                setupExerciseView()
            }
        }.start()
    }

    private fun setupRestView(){

        llRestView.visibility = View.VISIBLE
        llExerciseView.visibility = View.GONE

        if(restTimer != null){
            restTimer!!.cancel()
            restProgress = 0
        }

        tvExerciseName.text = exerciseList!![currentExercisePosition + 1].getName()

        setRestProgressBar()
    }

    private fun setExerciseProgressBar(){
        progressBarExercise.progress = exerciseProgress
        exerciseTimer = object : CountDownTimer(exerciseTimerDuration * 1000,1000){
            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress++
                progressBarExercise.progress = exerciseTimerDuration.toInt()-exerciseProgress
                tvExerciseTimer.text = (exerciseTimerDuration.toInt()-exerciseProgress).toString()
            }

            override fun onFinish() {
                if(currentExercisePosition < exerciseList?.size!! -1){
                    setupRestView()
                }
                else{
                    Toast.makeText(this@ExerciseActivity,
                        "Chuc mung ban da hoan thanh xong bai tap",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }.start()
    }

    private fun setupExerciseView(){

        llRestView.visibility = View.GONE
        llExerciseView.visibility = View.VISIBLE

        if(exerciseTimer != null){
            exerciseTimer!!.cancel()
            exerciseProgress = 0
        }

        speakOut(exerciseList!![currentExercisePosition].getName())

        setExerciseProgressBar()

        ivImage.setImageResource(exerciseList!![currentExercisePosition].getImage())
        tvExerciseName.text = exerciseList!![currentExercisePosition].getName()
    }

    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS){
            val result = tts!!.setLanguage(Locale.US)
            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){

            }
        }else{
            Log.e("TTS","Initialization Failed!")
        }
    }

    private fun speakOut(text : String){
        tts!!.speak(text,TextToSpeech.QUEUE_FLUSH,null,"")
    }

    private fun setupExerciseStatusRecyclerView(){
        rvExerciseStatus.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!,this)
        rvExerciseStatus.adapter = exerciseAdapter
    }

}
