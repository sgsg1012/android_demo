package com.example.wydemo

open class Project(
    val projectId: String,
    val title: String,
    val content: String,
    val location: String,
    val image: String,
    val amount: Int,
    val contactNumber: String,
) {
}

class lostProject(
    projectId: String,
    title: String,
    content: String,
    location: String,
    image: String,
    amount: Int,
    contactNumber: String,
) : Project(projectId, title, content, location, image, amount, contactNumber) {

    companion object{
        var bottom: Boolean = false
        private var relaAddress = "/project/lostProperty/list"
        private val args = HashMap<String, String>()
        var page = 1
        private const val pageSize = 40
        val data = ArrayList<lostProject>()
        //emmm 放这吧


    }
}