package com.mcal.example

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.mcal.example.adapters.LayoutAdapters
import com.mcal.example.databinding.ActivityMainBinding
import com.mcal.example.utils.FileHelper.copyAssetsFile
import com.mcal.example.utils.Utils.isAndroidXLayout
import com.mcal.example.utils.ZipHelper.unzip
import com.mcal.uidesigner.XmlLayoutDesignActivity
import com.mcal.webview.BaseActivity
import com.mcal.webview.WebViewActivity
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import java.io.File

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar(binding.toolbar.id, "App UI Designer")
        extractRes()
        getLayouts()
        startWebView()
    }

    private fun getResDir(): File {
        return File(filesDir, "res")
    }

    private fun getLayoutDir(): File {
        return File(getResDir(), "layout")
    }

    private fun extractRes() {
        val resFile = File(filesDir, "res.zip")
        copyAssetsFile(this, "res.zip", resFile)
        unzip(resFile, getResDir().path)
    }

    private fun getLayouts() {
        val apkItemAdapter = ItemAdapter<LayoutAdapters>()
        val fastApkAdapter = FastAdapter.with(apkItemAdapter)

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = fastApkAdapter
        }

        var i = 0L
        getLayoutDir().walk().filter { !isAndroidXLayout(it.name) }.forEach { file ->
            val name = file.name
            if (name.endsWith(".xml")) {
                apkItemAdapter.add(
                    LayoutAdapters()
                        .withId(i)
                        .withTitle(name),
                )
                i++
            }
        }

        fastApkAdapter.onClickListener =
            { _: View?, _: IAdapter<LayoutAdapters>, mainMenuItem: LayoutAdapters, _: Int ->
                mainMenuItem.itemTitle?.let {
                    XmlLayoutDesignActivity.show(
                        this,
                        "xml",
                        File(getLayoutDir(), it).path,
                        false,
                        false
                    )

//                    XmlLayoutDesignActivity.showTrainer(
//                        this,
//                        "xml",
//                        File(getLayoutDir(), it).path,
//                        13,
//                        arrayOf("Hi", "Hello", "Hey"),
//                        "en",
//                        "Trainer Task",
//                        "Layout Viewer",
//                        "Run",
//                        "Run 1",
//                        true,
//                        true
//                    )
                }
                true
            }
    }

    private fun startWebView() {
        binding.openWebView.setOnClickListener {
            val intent = Intent(this, WebViewActivity::class.java)
            intent.putExtra(
                WebViewActivity.HTML_URL,
                "https://timscriptov.ru/apkeditor/doc/instructions/index.html"
            )
            startActivity(intent)
        }
    }
}