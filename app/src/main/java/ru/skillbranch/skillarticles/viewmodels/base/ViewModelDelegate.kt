package ru.skillbranch.skillarticles.viewmodels.base

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/*
class AttrValue(@AttrRes private val res: Int) : ReadOnlyProperty<Context, Int> {
    private var value: Int? = null
    override fun getValue(thisRef: Context, property: KProperty<*>): Int {
        if (value == null) {
            val tv = TypedValue()
            if (thisRef.theme.resolveAttribute(res, tv, true)) value = tv.data
            else throw  Resources.NotFoundException("Resource with id $res not found")
        }
        return value!!
    }
}*/

/*    override val viewModel: ArticleViewModel by lazy {
        val vmFactory = ViewModelFactory("0")
        ViewModelProviders.of(this, vmFactory).get(ArticleViewModel::class.java)
    }*/

class ViewModelDelegate<T: ViewModel>(private val clazz: Class<T>, private val arg: Any?) : ReadOnlyProperty<FragmentActivity, T> {

    private lateinit var value: T

    override fun getValue(thisRef: FragmentActivity, property: KProperty<*>): T {
        if (!::value.isInitialized) value = when (arg) {
            null -> ViewModelProviders.of(thisRef).get(clazz)
            else -> ViewModelProviders.of(thisRef, ViewModelFactory(arg)).get(clazz)
        }
        return value
    }

}