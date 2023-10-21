import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.scmpapp.R
import com.example.scmpapp.data.model.Employee

class EmployeeAdapter(private val employees: List<Employee>) : RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_employee, parent, false)
        return EmployeeViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val employee = employees[position]
        holder.tvEmail.text = employee.email
        holder.tvName.text = "${employee.first_name} ${employee.last_name}"
        Glide.with(holder.ivAvatar.context).load(employee.avatar).into(holder.ivAvatar)
    }

    override fun getItemCount(): Int = employees.size

    inner class EmployeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivAvatar: ImageView = itemView.findViewById(R.id.ivAvatar)
        val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
    }
}
