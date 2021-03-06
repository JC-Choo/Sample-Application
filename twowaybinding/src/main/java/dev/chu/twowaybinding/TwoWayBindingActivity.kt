package dev.chu.twowaybinding

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import dev.chu.twowaybinding.databinding.ActivityTwoWayBindingBinding

/**
 * https://pavan-careers5208.medium.com/android-two-way-data-binding-79242210e23
 */
class TwoWayBindingActivity  : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityTwoWayBindingBinding>(this, R.layout.activity_two_way_binding).run {
            lifecycleOwner = this@TwoWayBindingActivity
            user = SampleUser()
        }
    }
}