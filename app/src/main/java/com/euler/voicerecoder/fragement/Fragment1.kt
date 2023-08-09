package com.euler.voicerecoder.fragement

import android.content.Intent
import android.media.AudioFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.euler.voicerecoder.MainActivity2
import com.euler.voicerecoder.R
import com.euler.voicerecoder.databinding.FragmentFragement1Binding


class Fragment1 : Fragment() {
    private var _binding: FragmentFragement1Binding? = null
    private var bit= AudioFormat.ENCODING_PCM_16BIT
    private var audiochannel=AudioFormat.CHANNEL_IN_MONO
    private var sampleRate=32000

    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =FragmentFragement1Binding.inflate(inflater, container, false)
        val view = binding.root

      /* val someActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    // Traitez le résultat ici
                    val data: Intent? = result.data
                    val resultss= data?.getIntExtra("result",0)
                    binding.textViewDesc.text=resultss.toString()
                }
            }*/


        binding.btn.setOnClickListener {
            val intent = Intent(context, MainActivity2::class.java)
            intent.putExtra("bit",bit)
            intent.putExtra("audioChannel",audiochannel)
            intent.putExtra("sampleRate",sampleRate)
            startActivity(intent)
            //someActivityResultLauncher.launch(intent)
        }
        binding.radioBtnStandard.isChecked=true
        binding.textViewDesc.text="universal,best compatible,hight quality sound reproduction."
  binding.RadioGroup.setOnCheckedChangeListener {_,checkedId->
      val animation= AnimationUtils.loadAnimation(context,R.anim.fade_in)
      val standardBit= AudioFormat.ENCODING_PCM_16BIT
      val standardAudiochannel=AudioFormat.CHANNEL_IN_MONO
      val standardSampleRate=32000

      val studioBit= AudioFormat.ENCODING_PCM_16BIT
      val studioAudiochannel=AudioFormat.CHANNEL_IN_STEREO
      val studioSampleRate=44100

      val meetingBit= AudioFormat.ENCODING_PCM_16BIT
      val meetingAudiochannel=AudioFormat.CHANNEL_IN_STEREO
      val meetingSampleRate=48000

      when(checkedId){
          R.id.radioBtnStandard->{binding.ImageViewdesc.setImageResource(R.drawable.ic_baseline_mic_none_24)
              binding.ImageViewdesc.startAnimation(animation)
              binding.textViewDesc.text="universal,best compatible,hight quality sound reproduction."
              binding.tvChannel.text="mono"
              binding.tvKhz.text="32Khz"
              binding.radioBtnStandard.isChecked=true
              bit=standardBit
              audiochannel=standardAudiochannel
              sampleRate=standardSampleRate
          }
          R.id.radioBtnStudio->{

              binding.ImageViewdesc.setImageResource(R.drawable.baseline_mic_none_24_bleu)
              binding.ImageViewdesc.startAnimation(animation)
              binding.textViewDesc.text="preserve the original sound,perfect for music & recitation recording"
              binding.tvChannel.text="stereo"
              binding.tvKhz.text="44.1Khz"
              binding.radioBtnStandard.isChecked=false
              bit=studioBit
              audiochannel=studioAudiochannel
              sampleRate=studioSampleRate
          }
          R.id.radioBtnDisco->{
              binding.ImageViewdesc.setImageResource(R.drawable.baseline_mic_none_24_gold)
              binding.ImageViewdesc.startAnimation(animation)
              binding.textViewDesc.text="max configuration,ideal for recording vocals in meeting"
              binding.tvChannel.text="stereo"
              binding.tvKhz.text="48Khz"
              binding.radioBtnStandard.isChecked=false
              bit=meetingBit
              audiochannel=meetingAudiochannel
              sampleRate=meetingSampleRate
          }
      }
  }
        return view

    }



}
