package nl.cloudplusplus.evaluation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import nl.cloudplusplus.evaluation.R
import nl.cloudplusplus.evaluation.models.Field

class EntrieTableRowAdapter(private var formArrayList: Array<Field>?) :
    RecyclerView.Adapter<EntrieTableRowAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.entrie_table_row_layout, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.row.tag = formArrayList?.get(i)?.id.toString()
        viewHolder.formTitle.text = formArrayList?.get(i)?.label ?: ""
    }

    override fun getItemCount(): Int {
        return formArrayList?.size ?: 0
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val row: TableRow = itemView.findViewById(R.id.row)
        val formTitle: TextView = itemView.findViewById(R.id.title)
    }
}