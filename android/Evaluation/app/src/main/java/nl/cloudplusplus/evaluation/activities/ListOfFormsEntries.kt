package nl.cloudplusplus.evaluation.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import nl.cloudplusplus.evaluation.R
import nl.cloudplusplus.evaluation.adapters.ListOfFormEntriesTableRowAdapter
import nl.cloudplusplus.evaluation.database.DBHelper

class ListOfFormsEntries : AppCompatActivity() {

    private lateinit var tableRecyclerView: RecyclerView
    private lateinit var tableRowAdapter: ListOfFormEntriesTableRowAdapter
    private var id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_list_of_forms_entries)

        val b = intent.extras
        if (b != null) id = b.getInt("id")

        val db = DBHelper(this, null)

        tableRecyclerView = findViewById(R.id.table_recycler_view2)
        tableRowAdapter = ListOfFormEntriesTableRowAdapter(db.getAllEntries(id))

        tableRecyclerView.layoutManager = LinearLayoutManager(this)
        tableRecyclerView.adapter = tableRowAdapter

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.list_of_form_entries)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun clickHandlerCell(view: View) {
        val intent: Intent = Intent(
            this@ListOfFormsEntries,
            Entrie::class.java
        )
        val b = Bundle()
        b.putInt("id", view.tag.toString().toInt())
        intent.putExtras(b)
        startActivity(intent)
        finish()
    }

    fun clickHandlerNewEntrie(view: View) {
        val intent: Intent = Intent(
            this@ListOfFormsEntries,
            Entrie::class.java
        )
        val b = Bundle()
        b.putInt("defaultFormId", id)
        intent.putExtras(b)
        startActivity(intent)
        finish()
    }
}