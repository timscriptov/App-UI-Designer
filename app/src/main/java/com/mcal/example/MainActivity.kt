package com.mcal.example

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mcal.uidesigner.R
import com.mcal.uidesigner.XmlLayoutDesignActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun startUIDesigner(manifestPath: String) {
        val intent = Intent(this, XmlLayoutDesignActivity::class.java)
        intent.putExtra(XmlLayoutDesignActivity.EXTRA_FILE, manifestPath)
        intent.putExtra(XmlLayoutDesignActivity.EXTRA_LANGUAGE, "xml")
        intent.putExtra(XmlLayoutDesignActivity.EXTRA_DEMO, false)
        intent.putExtra(XmlLayoutDesignActivity.EXTRA_STANDALONE, false)
        intent.putExtra(XmlLayoutDesignActivity.EXTRA_TRAINER, false)
        startActivity(intent)
        finish()
    }
}