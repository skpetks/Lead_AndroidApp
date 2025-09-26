package com.innovu.visitor.ui.chat

import android.annotation.SuppressLint
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.innovu.visitor.MainActivity
import com.innovu.visitor.R
import com.innovu.visitor.model.ChatItem
import com.innovu.visitor.model.ChatMessage
import com.innovu.visitor.services.SignalRManager
import com.innovu.visitor.utlis.StorePrefData
import kotlin.getValue

class AllChatFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAllAdapter
    private lateinit var searchView: SearchView


    private val chatList = mutableListOf<ChatItem>()
    private val chatViewModel: ChatViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_all_chat, container, false)
        recyclerView = view.findViewById(R.id.chatRecyclerView)
        setupRecyclerView()

        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL)
        ContextCompat.getDrawable(requireContext(), R.drawable.recycler_divider)?.let {
            dividerItemDecoration.setDrawable(it)
        }
        recyclerView.addItemDecoration(dividerItemDecoration)


        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_chat, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_info -> {
                        findNavController().navigate(
                            R.id.navigation_chatuser,

                        )
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        return view
    }


    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Setup RecyclerView
           searchView = view.findViewById(R.id.ed_search)
        searchView.setIconifiedByDefault(false)

        // Search implementation
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                chatAdapter.filter(newText ?: "")
                return true
            }
        })

        val searchEditText =searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)

//// Set hint text color
        searchEditText.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.grey))

// Optional: Set text color too
        searchEditText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

//// Optional: Force the hint again
        searchEditText.hint = "Search staff..."

        (requireActivity() as AppCompatActivity).let {
            if (it is MainActivity) {
                it.updateTitleWithColor("Chat", Color.BLACK)
            }
        }

        (requireActivity() as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.arrow_left)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                (requireActivity() as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_grid)

                // Handle back arrow click
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
        private fun setupRecyclerView() {
        chatAdapter = ChatAllAdapter() { chatItem ->
            // Handle item click (e.g. open ChatDetailActivity)
            val bundle = Bundle().apply {
                putString("userId", chatItem.userID.toString())
                putString("userName", chatItem.userName)
            }
            findNavController().navigate(
                R.id.action_allChatFragment_to_chatDetailFragment,
                bundle
            )
        }
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = chatAdapter

        SignalRManager.setonUserOnlineReceivedListener { userId ->
            requireActivity().runOnUiThread {
                chatAdapter.setUserStatus(userId, true)
            }
        }

        SignalRManager.setonUserofflineReceivedListener { userId ->
            requireActivity().runOnUiThread {
                chatAdapter.setUserStatus(userId, false)
            }
        }

        SignalRManager.setOnlineUserListReceived { onlineUsers ->
            requireActivity().runOnUiThread {
                for (id in onlineUsers) {
                    chatAdapter.setUserStatus(id, true)
                }
            }
        }
        SignalRManager.setMessageReceivedListener { senderId, message ->
            Log.d("ChatActivity", "New message from $senderId: $message")
            // chatAdapter.addMessage(...) or update your ViewModel
            requireActivity().runOnUiThread {
                chatViewModel.getChatUserList()
            }
        }

        chatViewModel.chatuser.observe(viewLifecycleOwner) { staff ->
            // Update your RecyclerView adapter
            chatAdapter.updateData(staff)
            SignalRManager.getOnlineUser()
        }

        chatViewModel.error.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
        if (chatViewModel.chatuser.value == null) {
            chatViewModel.getChatUserList()
        }
// Call API
        chatViewModel.getChatUserList()
    }

    override fun onResume() {
        super.onResume()

        // update chat UI with cached online users
        SignalRManager.getOnlineUser()

//        onlineUsers.forEach { chatAdapter.setUserOnlineStatus(it, true) }
    }
}
