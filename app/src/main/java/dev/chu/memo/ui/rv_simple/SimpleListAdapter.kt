package dev.chu.memo.ui.rv_simple

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import dev.chu.memo.R

/**
 * Type parameters :
 * [T] : Any — This is the item type class. In our example, we should pass the class Article as the first type parameter.
 * [B] : ViewDataBinding — This is the item type binding class that has been generated by DataBinding from our item layout xml.
 * Based on our example in step 1, the generated class should be ItemArticleBinding.
 *
 * Constructor parameters:
 * [diffUtil] : You should implement a DiffUtil.ItemCallback unique for your item type.
 * This will be used by the ListAdapter to validate your list items. We will be using ArticleDiffUtil in our example.
 * [layoutRes] : This is the layout id of your item layout xml, which is R.layout.item_article in our example.
 * [itemClickListener] : Lastly, a callback that will be invoked every time user clicks an item from the list.
 */

class SimpleListAdapter<T: Any, VDB: ViewDataBinding>(
    diffUtil: DiffUtil.ItemCallback<T>,
    @LayoutRes private val layoutRes: Int,
    private val itemClickListener: (T) -> Unit
) : ListAdapter<T, BaseViewHolder<VDB>>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<VDB> {
        return BaseViewHolder<VDB>(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                layoutRes,
                parent,
                false)
        ).apply {
            binding
                .root
                .findViewById<View>(R.id.itemClickable)
                .setOnClickListener {
                    itemClickListener.invoke(getItem(adapterPosition))
                }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<VDB>, position: Int) {
        holder.bind(getItem(position))
    }

}