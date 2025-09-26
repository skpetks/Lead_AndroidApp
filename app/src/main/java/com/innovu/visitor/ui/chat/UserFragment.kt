package com.innovu.visitor.ui.chat

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.innovu.visitor.MainActivity
import com.innovu.visitor.R
import com.innovu.visitor.model.ChatUserModel

class UserFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var searchView: SearchView
    private val userList = mutableListOf<ChatUserModel>()
    private val chatViewModel: ChatViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.userRecyclerView)
        searchView = view.findViewById(R.id.ed_search)
        searchView.setIconifiedByDefault(false)

        userAdapter = UserAdapter(userList) { user ->
//            Toast.makeText(requireContext(), "Clicked: ${user.userName}", Toast.LENGTH_SHORT).show()
            // Navigate or open chat here if needed

            val bundle = Bundle().apply {
                putString("userId", user.userID.toString())
                putString("userName", user.userName)
            }
            findNavController().navigate(
                R.id.action_allChatFragment_to_chatDetailFragment,
                bundle
            )
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = userAdapter

        val searchEditText =searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)

//// Set hint text color
        searchEditText.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.grey))

// Optional: Set text color too
        searchEditText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

//// Optional: Force the hint again
        searchEditText.hint = "Search staff..."

        // Search implementation
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                userAdapter.filter(newText ?: "")
                return true
            }
        })

        chatViewModel.staffList.observe(viewLifecycleOwner) { staff ->
            // Update your RecyclerView adapter
            userAdapter.updateData(staff)
        }

        chatViewModel.error.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }

// Call API
        chatViewModel.fetChatUser()

        (requireActivity() as AppCompatActivity).let {
            if (it is MainActivity) {
                it.updateTitleWithColor("New Chat", Color.BLACK)
            }
        }

        (requireActivity() as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.arrow_left)
    }

}
