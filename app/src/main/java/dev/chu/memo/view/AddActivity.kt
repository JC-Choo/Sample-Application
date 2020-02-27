package dev.chu.memo.view

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dev.chu.memo.BuildConfig
import dev.chu.memo.R
import dev.chu.memo.base.BaseActivity
import dev.chu.memo.common.Const
import dev.chu.memo.data.local.ImageData
import dev.chu.memo.data.local.MemoData
import dev.chu.memo.databinding.ActivityAddBinding
import dev.chu.memo.etc.extension.*
import dev.chu.memo.view.adapter.ImageAdapter
import dev.chu.memo.view_model.AddViewModel
import dev.chu.memo.view_model.RoomViewModel
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent as Intent1
import android.graphics.Bitmap as Bitmap1

class AddActivity : BaseActivity<ActivityAddBinding>() {
    @LayoutRes
    override fun getLayoutRes(): Int = R.layout.activity_add

    private val TAG = AddActivity::class.java.simpleName

    private val addVm by lazy { ViewModelProvider(this).get(AddViewModel::class.java) }
    private val roomVM by lazy { ViewModelProvider(this)[RoomViewModel::class.java] }
    private val adapter by lazy { ImageAdapter(mutableListOf()) }
    private var title: String? = null
    private var content: String? = null

    private var usingPermissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private var photoFile: File? = null
    private var photoUri: Uri? = null
    private var timeStamp: String? = null

    // region lifecycle
    override fun initView() {
        Log.i(TAG, "initView")

        binding.activity = this
        binding.viewModel = addVm

        setActionBarHome(binding.includeToolbar.toolbar, R.drawable.arrow_back_white)
        binding.includeToolbar.toolbarTv.text = ""
        binding.includeToolbar.toolbarTvEtc.text = getString(R.string.picture)
        binding.includeToolbar.toolbarTvEtc.setOnClickListener {
            showPermissionDialog()
        }

        setRecyclerView()
        observeViewModel()
    }

    override fun onRestart() {
        super.onRestart()
        Log.i(TAG, "onRestart")
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
    }
    // endregion

    private fun showPermissionDialog() {
        if (!hasPermissions(*usingPermissions)) {
            checkUsingPermission(usingPermissions, Const.REQUEST_CODE_PERMISSIONS)
            return
        }

        val info = arrayOf<CharSequence>(
            getString(R.string.camera),
            getString(R.string.gallery)
        )
        val builder = AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.get_image))
            setItems(info) { dialogInterface, which ->
                when (which) {
                    0 -> doTakePhotoAction()
                    1 -> doTakeAlbumAction()
                }
                dialogInterface.dismiss()
            }
        }
        builder.show()
    }

    // region 사진 찍기
    private fun doTakePhotoAction() {
        val intent = Intent1(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
//            var photoFile: File? = null
            try {
                photoFile = makeImageFile()
            } catch (e: IOException) {
                Log.e(TAG, "error = $e")
            }

            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(
                    this,
                    BuildConfig.APPLICATION_ID + ".fileprovider",
                    photoFile!!
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(intent, Const.REQUEST_CODE_CAMERA_PERMISSION)
            }
        }
    }
    // endregion

    // region 앨범에서 가져오기
    private fun doTakeAlbumAction() {
        val intent = Intent1(Intent1.ACTION_PICK)
        intent.type = "image/*"
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent, Const.REQUEST_CODE_GALLERY_PERMISSION)
    }
    // endregion

    // region 앨범에 사진 저장
    private fun galleryAddPicture() {
//        imageViewVisibility()

        val mediaScanIntent = Intent1(Intent1.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val file = File("file:" + makeImageFile().absolutePath)
        val contentUri = Uri.fromFile(file) as Uri
        mediaScanIntent.data = contentUri
        sendBroadcast(mediaScanIntent)

        adapter.addItem(ImageData(imageUrl = photoUri.toString()))
        showToast(R.string.save_image)
    }
    // endregion

    // region image rotate
    private fun rotateImage(source: Bitmap1, angle: Float): Bitmap1 {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap1.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }
    // endregion

    private fun setRecyclerView() {
        binding.writeRvImage.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.writeRvImage.adapter = adapter
    }

    private fun observeViewModel() {
        addVm.title.observe(this, Observer {
            title = it
        })

        addVm.content.observe(this, Observer {
            content = it
        })
    }

    // region onClickEvent
    fun onClickSave() {
        roomVM.saveMemo(MemoData(title = title, content = content, created = Date()))
        finish()
    }
    // endregion

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Const.REQUEST_CODE_PERMISSIONS &&
            grantResults.isNotEmpty()
        ) {
            for (i in grantResults.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
//                    checkUsingPermission(usingPermissions, Const.REQUEST_CODE_PERMISSIONS)
                    showToast("이 기능을 사용하려면 권한을 허용하셔야 합니다.")
                    return
                }
            }
            showPermissionDialog()
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: android.content.Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != RESULT_OK) {
            photoUri = null
            return
        }

        when (requestCode) {
            Const.REQUEST_CODE_CAMERA_PERMISSION -> {
                timeStamp = SimpleDateFormat("yyyy_MM_dd", Locale("ko")).format(Date())
                galleryAddPicture()
            }

            Const.REQUEST_CODE_GALLERY_PERMISSION -> {
                timeStamp = SimpleDateFormat("yyyy_MM_dd", Locale("ko")).format(Date())

                photoUri = if (data?.data != null) {
                    data.data
                } else
                    null

                adapter.addItem(ImageData(imageUrl = photoUri.toString()))
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (!binding.writeEtTitle.text.isNullOrEmpty()) {
            confirmDialog(
                getString(R.string.back_memo),
                DialogInterface.OnClickListener { _, _ -> finish() },
                negativeTextResId = R.string.cancel
            )
        } else finish()
    }
}