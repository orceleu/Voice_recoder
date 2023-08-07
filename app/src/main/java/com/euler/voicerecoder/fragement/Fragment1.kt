package com.euler.voicerecoder.fragement

import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.euler.voicerecoder.databinding.FragmentFragement1Binding
import com.github.squti.androidwaverecorder.RecorderState
import com.github.squti.androidwaverecorder.WaveRecorder
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class Fragment1 : Fragment() {
    private var _binding: FragmentFragement1Binding? = null
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private var seconds = 0
    private var handler = Handler()
    var timerText=""
  var currentMaxAmplitude=0
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding =FragmentFragement1Binding.inflate(inflater, container, false)
        val view = binding.root

        val waveRecorder = WaveRecorder(getOutputFilePath())
        waveRecorder.waveConfig.sampleRate = 44100
        waveRecorder.waveConfig.channels = AudioFormat.CHANNEL_IN_MONO
        waveRecorder.waveConfig.audioEncoding = AudioFormat.ENCODING_PCM_16BIT
        waveRecorder.noiseSuppressorActive = true



        val   updateTimer = object : Runnable {
            override fun run() {
                val minutes = seconds / 60
                val remainingSeconds = seconds % 60
                timerText = String.format("%02d:%02d", minutes, remainingSeconds)
                if (isRecording) {
                    binding.textView.text = "Start Recording $timerText"
                    waveRecorder.onAmplitudeListener = {
                       val currentMaxAmplitude=it
                        binding.audioRecordView.update(currentMaxAmplitude)

                    }


                }else{
                    binding.textView.text = "Stop Recording: $timerText"
                }
                if (isRecording) {
                    seconds++
                    handler.postDelayed(this, 100) // Répéter la mise à jour toutes les 1000 ms (1 seconde)
                }
            }
        }



        binding.btn.setOnClickListener{
            if (isRecording) {
            waveRecorder.stopRecording()
                handler.removeCallbacks(updateTimer) // Arrêter le compteur en utilisant le Handler
                seconds = 0 // Réinitialise le compteur à zéro
                binding.textView.text = "Start Recording $timerText"

                binding.audioRecordView.recreate()

            }
            else{
                waveRecorder.startRecording()
                seconds = 0 // Réinitialise le compteur à zéro
                handler.post(updateTimer) // Démarrer le compteur en utilisant le Handler
                binding.textView.text = "Start Recording $timerText"

            }
            isRecording = !isRecording
        }
       /* waveRecorder.onAmplitudeListener = {
            Log.i(TAG, "Amplitude : $it")
        }*/
        binding.btnPause.setOnClickListener {
            waveRecorder.pauseRecording()

        }
        binding.btnResume.setOnClickListener {
            waveRecorder.resumeRecording()
        }



        waveRecorder.onStateChangeListener = {
            when (it) {
                RecorderState.RECORDING -> Toast.makeText(context,"recording",Toast.LENGTH_SHORT).show()
                RecorderState.STOP ->  Toast.makeText(context,"stop",Toast.LENGTH_SHORT).show()
                RecorderState.PAUSE ->  Toast.makeText(context,"pause",Toast.LENGTH_SHORT).show()
            }
        }


/*
        binding.btn.setOnClickListener {
            if (isRecording) {
                stopRecording()
                handler.removeCallbacks(updateTimer) // Arrêter le compteur en utilisant le Handler
                seconds = 0 // Réinitialise le compteur à zéro
               binding.textView.text = "Start Recording $timerText"
            } else {

                    startRecording()
                seconds = 0 // Réinitialise le compteur à zéro
                handler.post(updateTimer) // Démarrer le compteur en utilisant le Handler
                    binding.textView.text = "Stop Recording: $timerText"

            }
            isRecording = !isRecording

        }

*/
        return view

    }



    private fun startRecording() {

        // Le reste du code pour démarrer l'enregistrement reste inchangé
        val filePath = getOutputFilePath()
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
            setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
            setAudioChannels(1)
            setAudioSamplingRate(44100)
            setAudioEncodingBitRate(16 * 44100)
            setOutputFile(filePath)
            try {
                prepare()
                start()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(context, "Recording failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun stopRecording() {

        // Le reste du code pour arrêter l'enregistrement reste inchangé
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        Toast.makeText(context, "Recording saved: ${getOutputFilePath()}", Toast.LENGTH_SHORT).show()
    }

    private fun getOutputFilePath(): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "Recording_$timeStamp.wav"
        val folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        return "${folder.absolutePath}/$fileName"
    }
}
