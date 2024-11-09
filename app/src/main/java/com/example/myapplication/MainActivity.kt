package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapter.UserAdapter

import com.example.myapplication.model.User
import kotlinx.coroutines.launch
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.adapter.AppDatabase
import com.example.myapplication.data.UserDao
import com.example.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var userAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Инициализация базы данных и DAO
        db = AppDatabase.getDatabase(this)
        userDao = db.userDao()

        // Настройка адаптера
        userAdapter = UserAdapter(listOf())
        binding.recyclerViewUsers.adapter = userAdapter
        binding.recyclerViewUsers.layoutManager = LinearLayoutManager(this)

        // Обработчик нажатия на кнопку
        binding.buttonAddUser.setOnClickListener {
            addUser()
        }

        // Загрузка пользователей при старте
        loadUsers()
    }

    private fun addUser() {
        val name = binding.editTextName.text.toString()
        val age = binding.editTextAge.text.toString().toIntOrNull()

        if (name.isNotBlank() && age != null) {
            val user = User(name = name, age = age)
            lifecycleScope.launch {
                userDao.addUser(user)
                loadUsers()
            }
            binding.editTextName.text.clear()
            binding.editTextAge.text.clear()
        }
    }

    private fun loadUsers() {
        lifecycleScope.launch {
            val users = userDao.getAllUsers()
            userAdapter.setUsers(users)
        }
    }
}
