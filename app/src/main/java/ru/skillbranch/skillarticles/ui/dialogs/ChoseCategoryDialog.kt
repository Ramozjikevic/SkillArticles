package ru.skillbranch.skillarticles.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import ru.skillbranch.skillarticles.viewmodels.articles.ArticlesViewModel


class ChoseCategoryDialog : DialogFragment() {
    private val viewModel : ArticlesViewModel by activityViewModels()
    private val selectedCategories = mutableListOf<String>()
    private val args: ChoseCategoryDialogArgs by navArgs()


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //TODO save checked state and implement custom items
        val categories = args.categories.toList().map { "${it.title} (${it.articlesCount})" }.toTypedArray()
        val checked = BooleanArray(args.categories.size){
            args.selectedCategories.contains(args.categories[it].categoryId)
        }
        val adb = AlertDialog.Builder(requireContext())
            .setTitle("Chose category")
            .setPositiveButton("Apply") { _, _ ->
                viewModel.applyCategories(selectedCategories)
            }
            .setNegativeButton("Reset"){ _, _ ->
                viewModel.applyCategories(emptyList())
            }
            .setMultiChoiceItems(categories, checked) { dialog, whith, isChecked ->
                if(isChecked) selectedCategories.add(args.categories[whith].categoryId)
                else selectedCategories.remove(args.categories[whith].categoryId)
            }

        return adb.create()
    }
}