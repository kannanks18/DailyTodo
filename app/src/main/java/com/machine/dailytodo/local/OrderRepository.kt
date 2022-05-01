package com.machine.dailytodo.local

import javax.inject.Inject

class OrderRepository @Inject constructor( private val userDao: UserDao) {
    suspend fun addItem(user: UserData) = userDao.insert(user)
    fun getData() = userDao.getData()
}