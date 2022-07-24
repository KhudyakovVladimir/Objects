package com.khudyakovvladimir.objects.recyclerview

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.khudyakovvladimir.objects.R
import com.khudyakovvladimir.objects.database.ObjectEntity

class ObjectAdapter(
    var context: Context,
    var list: List<ObjectEntity>,
    private var itemClick: (ObjectEntity: ObjectEntity) -> Unit
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ObjectViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        lateinit var textView1: TextView
        lateinit var textView2: TextView
        lateinit var textView3: TextView
        lateinit var linearLayoutItem: LinearLayout

        fun bind(objectEntity: ObjectEntity) {
            textView1 = itemView.findViewById(R.id.textViewItem)
            textView2 = itemView.findViewById(R.id.textViewItem2)
            textView3 = itemView.findViewById(R.id.textViewItem3)
            linearLayoutItem = itemView.findViewById(R.id.linearLayoutItem)

            textView1.text = objectEntity.id.toString()
            textView2.text = objectEntity.title

            if (objectEntity.status == "true") {
                textView3.text = "+"
                textView3.setTextColor(Color.RED)
            }else {
                textView3.text = "-"
            }

            //textView3.text = objectEntity.status

            linearLayoutItem.setOnClickListener {
                itemClick(objectEntity)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return ObjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val holderR = holder as ObjectViewHolder
        holderR.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateAdapter(_listNews: List<ObjectEntity>) {
        this.list.apply {
            list = emptyList()
            val tempList = list.toMutableList()
            val tempList2 = _listNews.toMutableList()
            tempList.addAll(tempList2)
            val resultList = tempList2.toList()
            list = resultList
        }
    }
}