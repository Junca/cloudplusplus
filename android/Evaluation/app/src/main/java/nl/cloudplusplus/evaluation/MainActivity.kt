package nl.cloudplusplus.evaluation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.serialization.json.Json
import nl.cloudplusplus.evaluation.activities.ListOfFormsEntries
import nl.cloudplusplus.evaluation.adapters.ListOfFormsTableRowAdapter
import nl.cloudplusplus.evaluation.database.DBHelper
import nl.cloudplusplus.evaluation.models.Form


class MainActivity : AppCompatActivity() {

    private lateinit var tableRecyclerView: RecyclerView
    private lateinit var listOfFormsTableRowAdapter: ListOfFormsTableRowAdapter

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

            fileInString = applicationContext.assets.open("all-fields.json").bufferedReader()
                .use { it.readText() }
            form = Json.decodeFromString(Form.serializer(), fileInString)
            id = db.addDefaultForm(form.title)
            form.sections?.let { db.addDefaultSections(id, it) }
            form.fields?.let { db.addDefaultFields(id, it) }
        }

        tableRecyclerView = findViewById(R.id.table_recycler_view)
        listOfFormsTableRowAdapter = ListOfFormsTableRowAdapter(db.getAllForms())

        tableRecyclerView.layoutManager = LinearLayoutManager(this)
        tableRecyclerView.adapter = listOfFormsTableRowAdapter

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun clickHandlerCell(view: View) {
        val intent: Intent = Intent(
            this@MainActivity,
            ListOfFormsEntries::class.java
        )
        val b = Bundle()
        b.putInt("id", view.tag.toString().toInt())
        intent.putExtras(b)
        startActivity(intent)
        finish()
    }
}