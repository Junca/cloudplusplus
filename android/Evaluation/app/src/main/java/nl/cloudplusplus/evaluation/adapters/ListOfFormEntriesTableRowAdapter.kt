package nl.cloudplusplus.evaluation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import nl.cloudplusplus.evaluation.R
import nl.cloudplusplus.evaluation.models.Record

class ListOfFormEntriesTableRowAdapter(private var formArrayList: Array<Record>) :
    RecyclerView.Adapter<ListOfFormEntriesTableRowAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_of_form_entries_table_row_layout, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.row.tag = formArrayList[i].id.toString()
        viewHolder.formTitle.text = formArrayList[i].title
    }

    override fun getItemCount(): Int {
        return formArrayList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val row: TableRow = itemView.findViewById(R.id.row)
        val formTitle: TextView = itemView.findViewById(R.id.title)
    }
}