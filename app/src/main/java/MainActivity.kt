package github.venerealulcer.village

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onClick
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    val sp by lazy { getSharedPreferences("data", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (sp.contains("id")) {
            edittext_id.setText(sp.getString("id", ""))
        } else {
            sp.edit().putString("id", "").apply()
        }

        buttonQuery.onClick {
            SaveID()
            HideKeyBoard()
            val html = "<html><body>正在查询...<body></html>"
            mWebView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
            val num = edittext_id.text.toString()
            doAsync {
                GetRank(num)
            }
        }

    }

    fun GetRank(num: String) {

        val body = FormBody.Builder()
                .add("CertNo", num)//添加键值对
                .build()

        val request = Request.Builder().url("http://ent.sipmch.sipac.gov.cn/ModuleDefaultCompany/RentManage/SearchRentNo")
                .post(body)
                .build()

        val response = OkHttpClient().newCall(request).execute()

        val t = InputStreamReader(response.body()?.byteStream()).readText()

        runOnUiThread {

            val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
            val time = df.format(Date())

            mWebView.loadDataWithBaseURL(null, time + "<br/>" + getFixStr(t), "text/html", "utf-8", null);

        }

    }

    fun HideKeyBoard() {

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm.isActive && currentFocus != null) {
            if (currentFocus.windowToken != null) {
                imm.hideSoftInputFromWindow(currentFocus.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }

    }

    fun getFixStr(str: String): String {
        return str
                .replace("\\u003c", "<")
                .replace("\\u003e", ">")
                .replace("\\u0027", "\"")
    }

    fun SaveID() {
        sp.edit().putString("id", edittext_id.text.toString()).apply()
    }

}
