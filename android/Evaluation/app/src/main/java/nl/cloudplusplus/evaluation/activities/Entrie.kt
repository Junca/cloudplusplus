package nl.cloudplusplus.evaluation.activities

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import nl.cloudplusplus.evaluation.R
import nl.cloudplusplus.evaluation.adapters.EntrieTableRowAdapter
import nl.cloudplusplus.evaluation.database.DBHelper
import nl.cloudplusplus.evaluation.models.Record

class Entrie : AppCompatActivity() {

    private lateinit var tableRecyclerView: RecyclerView
    private lateinit var tableRowAdapter: EntrieTableRowAdapter
    private var id = 0
    private var defaultFormId = 0
    private var record: Record = Record(
        id = 0,
        defaultFormId = 0,
        form = "",
        title = "",
        sections = arrayOf(),
        fields = arrayOf(),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_entrie)

        val b = intent.extras
        if (b != null) id = b.getInt("id")
        if (b != null) defaultFormId = b.getInt("defaultFormId")

        val db = DBHelper(this, null)

        tableRecyclerView = findViewById(R.id.table_recycler_view2)

        if (id == 0) {
            val form = db.getDefaultForm(defaultFormId)
            record.id = id
            record.defaultFormId = defaultFormId
            record.form = form.title
            record.title = getRandomString()
            record.sections = form.sections
            record.fields = form.fields
        } else {
            record = db.getEntrieById(id)!!
        }

        tableRowAdapter = EntrieTableRowAdapter(record.fields)
        tableRecyclerView.layoutManager = LinearLayoutManager(this)
        tableRecyclerView.adapter = tableRowAdapter

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.entrie)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun getRandomString(): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..32).map { allowedChars.random() }.joinToString("")
    }

    fun clickHandlerSave(view: View) {
        val db = DBHelper(this, null)
        db.saveEntrie(record)
    }
}