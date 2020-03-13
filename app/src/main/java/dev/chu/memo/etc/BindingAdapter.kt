package dev.chu.memo.etc

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import dev.chu.memo.data.local.ImageData
import dev.chu.memo.data.local.MemoData
import dev.chu.memo.view.adapter.MainAdapter
import dev.chu.memo.view_model.RoomViewModel

object BindingAdapter {
    @BindingAdapter("android:srcGlideCircle")
    @JvmStatic
    fun setImageViewCircle(view: ImageView, imageUri: String?) {
        GlideApp.with(view.context)
            .load(imageUri)
            .apply(
                RequestOptions()
                    .transforms(CenterCrop(), CircleCrop())
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            )
            .into(view)
    }

    @BindingAdapter("android:srcGlide")
    @JvmStatic
    fun setImageView(view: ImageView, imageUri: String?) {
        GlideApp.with(view.context)
            .load(imageUri)
            .apply(
                RequestOptions()
                    .transform(CenterCrop())
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            )
            .into(view)
    }

    @BindingAdapter("android:srcGlide")
    @JvmStatic
    fun setImageView(view: ImageView, imageUrls: List<ImageData>? = null) {
        if(!imageUrls.isNullOrEmpty()) {
            GlideApp.with(view.context)
                .load(imageUrls[0].imageUrl)
                .apply(
                    RequestOptions()
                        .transform(CenterCrop())
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                )
                .into(view)
        }
    }

    @BindingAdapter("android:isVisibility")
    @JvmStatic
    fun setVisibility(view: View, data: MutableList<*>? = null) {
        view.visibility =
            if(data.isNullOrEmpty()) View.GONE
            else View.VISIBLE
    }

    @BindingAdapter(value = ["android:data", "android:vm"])
    @JvmStatic
    fun RecyclerView.setListItem(data: MutableList<MemoData>, vm: RoomViewModel) {
        (adapter as? MainAdapter)?.apply {
            setItems(data)
        } ?: run {
            adapter = MainAdapter(this.context, data, vm)
        }
    }
}