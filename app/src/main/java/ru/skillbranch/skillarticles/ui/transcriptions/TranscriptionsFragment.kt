package ru.skillbranch.skillarticles.ui.transcriptions


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.viewmodels.transcriptions.TranscriptionsViewModel

class TranscriptionsFragment : Fragment() {

    companion object {
        fun newInstance() = TranscriptionsFragment()
    }

    private lateinit var viewModel: TranscriptionsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_transcriptions, container, false)
    }


}