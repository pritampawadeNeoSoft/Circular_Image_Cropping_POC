package com.example.circularimagecroppingpoc

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.isseiaoki.simplecropview.CropImageView
import com.isseiaoki.simplecropview.callback.LoadCallback
import androidx.core.app.ActivityCompat.startActivityForResult

import android.provider.MediaStore
import android.provider.MediaStore.Files.FileColumns

import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE

import android.os.Environment
import android.util.Config
import android.util.Log
import android.widget.ImageView
import com.isseiaoki.simplecropview.callback.CropCallback
import java.io.ByteArrayOutputStream
import java.io.File


class MainActivity : AppCompatActivity() {
    lateinit var cropper:CropImageView
    var source:Uri?=null
    companion object{
        const val IMG_REQUEST = 150
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cropper = findViewById<CropImageView>(R.id.cropImageView)
        cropper.setCropMode(CropImageView.CropMode.CIRCLE)

    }
    fun selectImage(view: View){

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMG_REQUEST)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == IMG_REQUEST && resultCode == RESULT_OK
            && data!=null)
        {
            source = data.data
            Toast.makeText(baseContext,"Source Image: "+source.toString(),Toast.LENGTH_SHORT).show()

            cropper.load(source).execute(object : LoadCallback{
                override fun onError(e: Throwable?) {
                    Toast.makeText(baseContext,"Failed ${e.toString()}",Toast.LENGTH_LONG).show()
                }

                override fun onSuccess() {
                    //image placed inside crop view
                }

            })

        }else{
            if(requestCode == IMG_REQUEST)
                Toast.makeText(baseContext,"Failed to retrieve image",Toast.LENGTH_LONG).show()
        }
    }

    fun cropImage(view:View){
        if(source!=null) {
            cropper.crop(source).execute(object: CropCallback{
                override fun onError(e: Throwable?) {
                    Log.e("CROP_ERROR",e!!.message.toString())
                }

                override fun onSuccess(cropped: Bitmap?) {
                    findViewById<ImageView>(R.id.iv_result).setImageBitmap(cropped)
                    Toast.makeText(baseContext,"Result Image URI: "+getImageUri(baseContext,cropped!!),Toast.LENGTH_LONG).show()
                }

            })
        }
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.getContentResolver(),
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }
}