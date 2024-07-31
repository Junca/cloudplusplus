package nl.cloudplusplus.evaluation

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.serialization.json.Json
import nl.cloudplusplus.evaluation.database.DBHelper
import nl.cloudplusplus.evaluation.models.Form

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val db = DBHelper(this, null)
        if (db.needImportJSON()) {

            var fileInString: String =
                applicationContext.assets.open("200-form.json").bufferedReader()
                    .use { it.readText() }
            var form: Form = Json.decodeFromString(Form.serializer(), fileInString)
            var id = db.addDefaultForm(form.title)
            form.sections?.let { db.addDefaultSections(id, it) }
            form.fields?.let { db.addDefaultFields(id, it) }

            fileInString =
                applicationContext.assets.open("all-fields.json").bufferedReader()
                    .use { it.readText() }
            form = Json.decodeFromString(Form.serializer(), fileInString)
            id = db.addDefaultForm(form.title)
            form.sections?.let { db.addDefaultSections(id, it) }
            form.fields?.let { db.addDefaultFields(id, it) }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}