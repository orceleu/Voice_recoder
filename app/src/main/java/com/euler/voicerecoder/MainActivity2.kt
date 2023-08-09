package com.euler.voicerecoder

import android.media.AudioFormat
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import com.euler.voicerecoder.databinding.ActivityMain2Binding
import com.github.squti.androidwaverecorder.RecorderState
import com.github.squti.androidwaverecorder.WaveRecorder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding
    private var isRecording = true
    private var seconds = 0
    private var handler = Handler()
    private lateinit var waveRecorder:WaveRecorder
    var timerText=""
    var dividerForSampleRate=10.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val bit=intent.getIntExtra("bit", AudioFormat.ENCODING_PCM_16BIT)
        val audioChannel=intent.getIntExtra("audioChannel",AudioFormat.CHANNEL_IN_MONO)
        val sampleRate=intent.getIntExtra("sampleRate",32000)

        when(sampleRate){
          32000->{dividerForSampleRate=10.0}
          44100->{dividerForSampleRate=13.78}
          48000->{dividerForSampleRate=15.0}
        }

        waveRecorder = WaveRecorder(getOutputFilePath())
        waveRecorder.waveConfig.sampleRate = sampleRate
        waveRecorder.waveConfig.channels = audioChannel
        waveRecorder.waveConfig.audioEncoding = bit
        waveRecorder.noiseSuppressorActive = true
        binding.layoutPlayAndValidate.isGone = false


        val updateTimer = object : Runnable {
            override fun run() {
                val minutes = ((seconds / 10) / 60)
                val remainingSeconds = ((seconds / 10) % 60)
                timerText = String.format("%02d:%02d", minutes, remainingSeconds)
                if (isRecording) {
                    binding.textView.text = timerText
                    waveRecorder.onAmplitudeListener = {
                        val currentMaxAmplitude = it
                        binding.audioRecordView.update(currentMaxAmplitude)
                    }
                } else {
                    binding.textView.text = timerText
                }

                if (isRecording) {
                    seconds++
                    handler.postDelayed(
                        this,
                        100
                    ) // Répéter la mise à jour toutes les 100 ms (0.1 seconde)
                }
            }
        }

  binding.recordingReturn.setOnClickListener {

      waveRecorder.stopRecording()
      isRecording=false
      handler.removeCallbacks(updateTimer)
      Toast.makeText(this, "saved to: ${getOutputFilePath()}", Toast.LENGTH_SHORT).show()
      finish()

    // resultIntent()
  }
        binding.btnPause.setOnClickListener {
            if (isRecording) {

                waveRecorder.pauseRecording()
                handler.removeCallbacks(updateTimer)
                isRecording=false
            } else {

                waveRecorder.resumeRecording()
                handler.post(updateTimer)
                isRecording=true
            }


        }
        waveRecorder.startRecording()
        handler.post(updateTimer)




        binding.btnStop.setOnClickListener {
            waveRecorder.stopRecording()
            binding.layoutPlayAndValidate.isGone=true
            handler.removeCallbacks(updateTimer) // Arrêter le compteur en utilisant le Handler
            seconds = 0 // Réinitialise le compteur à zéro
            timerText="00:00"
            binding.textView.text = timerText
            isRecording=false
            binding.audioRecordView.recreate()
            finish()
           //resultIntent()
        }






        waveRecorder.onStateChangeListener = {
            when (it) {
                RecorderState.RECORDING -> {
                    Toast.makeText(this, "recording", Toast.LENGTH_SHORT).show()
                    binding.btnPause.setImageResource(R.drawable.baseline_stop_24)

                }

                RecorderState.STOP -> {
                    Toast.makeText(this, "saved to: ${getOutputFilePath()}", Toast.LENGTH_SHORT).show()
                    binding.btnPause.setImageResource(R.drawable.baseline_play_arrow_24)


                }

                RecorderState.PAUSE -> {
                    Toast.makeText(this, "pause", Toast.LENGTH_SHORT).show()
                    binding.btnPause.setImageResource(R.drawable.baseline_play_arrow_24)
                }
            }

        }

    }

    private fun getOutputFilePath(): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "Recording_$timeStamp.wav"
        val folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        return "${folder.absolutePath}/$fileName"
    }
 /* fun resultIntent(){
        val intent= Intent()
        intent.putExtra("result",1)
        setResult(RESULT_OK,intent)

  }*/
}