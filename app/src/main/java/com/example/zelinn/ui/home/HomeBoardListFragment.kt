package com.example.zelinn.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.zelinn.R
import com.example.zelinn.classes.BoardModel
import com.example.zelinn.classes.RetrofitInstance
import com.example.zelinn.databinding.FragmentHomeBoardListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeBoardListFragment: Fragment() {
    private var _binding: FragmentHomeBoardListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBoardListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        RetrofitInstance.retrofit.getBoards().enqueue(object: Callback<List<BoardModel>> {
            override fun onFailure(call: Call<List<BoardModel>>, t: Throwable) {

            }
            override fun onResponse(
                call: Call<List<BoardModel>>,
                response: Response<List<BoardModel>>
            ) {
                val layout = view.findViewById<LinearLayout>(R.id.homepage_board_list_item_layout)
                val boards = response.body()

                boards?.let {
                    val boardItemFragments = mutableListOf<HomeBoardItemFragment>()

                    for (board in boards) {
                        val item = HomeBoardItemFragment()
                        boardItemFragments.add(item)


                    }


                }


            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}