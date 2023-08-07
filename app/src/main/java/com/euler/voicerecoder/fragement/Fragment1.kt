package com.euler.voicerecoder.fragement

import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isGone
import com.euler.voicerecoder.R
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
    var channel=AudioFormat.CHANNEL_IN_MONO
    var encoding=AudioFormat.ENCODING_PCM_8BIT
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        _binding =FragmentFragement1Binding.inflate(inflater, container, false)
        val view = binding.root

try {


    val arrayAdapter =
        ArrayAdapter.createFromResource(requireContext(), R.array.sp_bit, R.layout.item_sp)
    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    binding.spinnerBit.adapter = arrayAdapter


    binding.spinnerBit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

        override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
            when (i) {
                0 -> {
                    encoding = AudioFormat.ENCODING_PCM_8BIT
                }

                1 -> {
                    encoding = AudioFormat.ENCODING_PCM_16BIT
                }

                2 -> {
                    encoding = AudioFormat.ENCODING_PCM_16BIT
                }

            }
        }

        override fun onNothingSelected(adapterView: AdapterView<*>?) {}
    }
    val arrayAdapterChannel =
        ArrayAdapter.createFromResource(requireContext(), R.array.sp_channel, R.layout.item_sp)
    arrayAdapterChannel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    binding.spinnerStereo.adapter = arrayAdapterChannel
    binding.spinnerStereo.setSelection(1)

    binding.spinnerStereo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

        override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
            when (i) {
                0 -> {
                    channel = AudioFormat.CHANNEL_IN_MONO
                }

                1 -> {
                    channel = AudioFormat.CHANNEL_IN_STEREO
                }


            }
        }

        override fun onNothingSelected(adapterView: AdapterView<*>?) {}
    }


}catch (e:Exception){}




        val waveRecorder = WaveRecorder(getOutputFilePath())
        waveRecorder.waveConfig.sampleRate = 44100
        waveRecorder.waveConfig.channels = AudioFormat.CHANNEL_IN_MONO
        waveRecorder.waveConfig.audioEncoding = AudioFormat.ENCODING_PCM_16BIT
        waveRecorder.noiseSuppressorActive = true

        binding.layoutPlayAndValidate.isGone=true


        val   updateTimer = object : Runnable {
            override fun run() {
                val minutes = ((seconds/10) / 60)
                val remainingSeconds = ((seconds/10) % 60)
                timerText = String.format("%02d:%02d", minutes, remainingSeconds)
                if (isRecording) {
                    binding.textView.text = timerText
                    waveRecorder.onAmplitudeListener = {
                       val currentMaxAmplitude=it
                        binding.audioRecordView.update(currentMaxAmplitude)
                    }
                }else{
                    binding.textView.text = timerText
                }
                if (isRecording) {
                    seconds++
                    handler.postDelayed(this, 100) // Répéter la mise à jour toutes les 1000 ms (1 seconde)
                }
            }
        }



        binding.btn.setOnClickListener{
            if (!isRecording) {
                binding.layoutPlayAndValidate.isGone=false

                waveRecorder.startRecording()
                seconds = 0 // Réinitialise le compteur à zéro
                handler.post(updateTimer) // Démarrer le compteur en utilisant le Handler
                binding.textView.text = timerText
            }else{
                Toast.makeText(context,"Already recording",Toast.LENGTH_SHORT).show()
            }

            isRecording = !isRecording
            binding.btn.isGone=true
        }

       /* waveRecorder.onAmplitudeListener = {
            Log.i(TAG, "Amplitude : $it")
        }*/
        binding.btnPause.setOnClickListener {
           if (isRecording) {
            waveRecorder.pauseRecording()
               handler.removeCallbacks (updateTimer)
               isRecording=false
           }else{
            waveRecorder.resumeRecording()
               handler.post(updateTimer)
               isRecording=true
            }


        }

          binding.btnStop.setOnClickListener {
              waveRecorder.stopRecording()
              binding.btn.isGone=false
              binding.layoutPlayAndValidate.isGone=true
              handler.removeCallbacks(updateTimer) // Arrêter le compteur en utilisant le Handler
              seconds = 0 // Réinitialise le compteur à zéro
              timerText="00:00"
              binding.textView.text = timerText
              isRecording=false
              binding.audioRecordView.recreate()
          }




        waveRecorder.onStateChangeListener = {
            when (it) {
                RecorderState.RECORDING -> {Toast.makeText(context,"recording",Toast.LENGTH_SHORT).show()
                    binding.btnPause.setImageResource(R.drawable.baseline_stop_24)
                }
                RecorderState.STOP ->  {Toast.makeText(context,"stop",Toast.LENGTH_SHORT).show()

                }
                RecorderState.PAUSE -> { Toast.makeText(context,"pause",Toast.LENGTH_SHORT).show()
                    binding.btnPause.setImageResource(R.drawable.baseline_play_arrow_24)
                }
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
