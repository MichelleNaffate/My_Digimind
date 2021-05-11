package com.example.digimind.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.digimind.R
import com.example.digimind.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {

    private var adaptador: AdaptadorTareas? = null
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var storage: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    companion object{
        var tasks = ArrayList<Task>()
        var first = true
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        tasks=ArrayList()

        storage= FirebaseFirestore.getInstance()
        auth= FirebaseAuth.getInstance()

        fillTasks()


        if(!tasks.isEmpty()) {
            var gridView: GridView = root.findViewById(R.id.gridview)

            adaptador = AdaptadorTareas(root.context, tasks)
            gridView.adapter = adaptador
        }

        return root
    }

    fun fillTasks(){

        storage.collection("actividades")
            .whereEqualTo("email",auth.currentUser.email)
            .get()
            .addOnSuccessListener {
                it.forEach {
                    var dias=ArrayList<String>()

                    if(it.getBoolean("lu") == true){
                        dias.add("Monday")
                    }

                    if(it.getBoolean("ma") == true){
                        dias.add("Tuesday")
                    }

                    if(it.getBoolean("mi") == true){
                        dias.add("Wednesday")
                    }

                    if(it.getBoolean("ju") == true){
                        dias.add("Thursday")
                    }

                    if(it.getBoolean("vi") == true){
                        dias.add("Friday")
                    }

                    if(it.getBoolean("sa") == true){
                        dias.add("Saturday")
                    }

                    if(it.getBoolean("do") == true){
                        dias.add("Sunday")
                    }

                    tasks!!.add(Task(it.getString("actividad")!!, dias, it.getString("tiempo")!!))
                }

                adaptador = AdaptadorTareas(context!!, tasks)
                gridview.adapter=adaptador
            }
            .addOnFailureListener{
                Toast.makeText(context, "Error: intente de nuevo", Toast.LENGTH_SHORT).show()
            }
    }

    private class AdaptadorTareas: BaseAdapter {
        var tareas = ArrayList<Task>()
        var context: Context? = null

        constructor(contexto: Context, tareas: ArrayList<Task>){
            this.context = contexto
            this.tareas = tareas
        }

        override fun getCount(): Int {
            return tareas.size
        }

        override fun getItem(p0: Int): Any {
            return tareas[p0]
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            var task = tareas[p0]
            var inflador = LayoutInflater.from(this.context)
            var vista = inflador.inflate(R.layout.task_view, null)

            val titulo: TextView = vista.findViewById(R.id.tv_title)
            val tiempo: TextView = vista.findViewById(R.id.tv_time)
            val dias: TextView = vista.findViewById(R.id.tv_days)

            titulo.setText(task.title)
            tiempo.setText(task.time)
            dias.setText(task.days.toString())

            return vista
        }
    }

}
