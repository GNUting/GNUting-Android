package com.changs.android.gnuting_android.ui.fragment.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.data.model.InUser
import com.changs.android.gnuting_android.databinding.SearchMemberBottomSheetBinding
import com.changs.android.gnuting_android.ui.adapter.AddMemberAdapter
import com.changs.android.gnuting_android.ui.adapter.SelectedMemberAdapter
import com.changs.android.gnuting_android.util.hideSoftKeyboard
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import com.changs.android.gnuting_android.viewmodel.MemberAddViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class PostSearchMemberBottomSheetFragment :
    BottomSheetDialogFragment() {
    private var _binding: SearchMemberBottomSheetBinding? = null
    private lateinit var adapter: AddMemberAdapter
    private lateinit var selectedMemberAdapter: SelectedMemberAdapter
    private val homeViewModel: HomeMainViewModel by activityViewModels()
    private val viewModel: MemberAddViewModel by hiltNavGraphViewModels(R.id.post_graph)
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState)
        if (bottomSheetDialog is BottomSheetDialog) {
            bottomSheetDialog.behavior.skipCollapsed = true
            bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        return bottomSheetDialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = SearchMemberBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecyclerView()
        setObserver()

        binding.root.setOnClickListener {
            it.hideSoftKeyboard()
        }

        viewModel.getSearchUser("")

        binding.searchMemberBottomSheetTxtMemberAdd.setOnClickListener {
            dismiss()
        }

        binding.searchMemberBottomSheetEdit.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    viewModel.getSearchUser(binding.searchMemberBottomSheetEdit.text.toString())
                    return true
                }
                return false
            }
        })
    }

    private fun setRecyclerView() {
        adapter = AddMemberAdapter(homeViewModel.myInfo.value?.id, ::navigateListener) { inUser, isChecked ->
            homeViewModel.myInfo.value?.let {
                val currentMember = mutableListOf<InUser>()

                if (isChecked) {
                    viewModel.currentMember.value?.let {
                        currentMember.addAll(it)

                        if (currentMember.none { it.id == inUser.id }) currentMember.add(inUser)
                    }
                } else {
                    viewModel.currentMember.value?.let {
                        currentMember.addAll(it)

                        if (currentMember.any { it.id == inUser.id }) currentMember.removeIf {
                            it.id == inUser.id
                        }

                    }
                }
                viewModel.currentMember.value = currentMember
            }


        }
        binding.searchMemberBottomSheetRecyclerview.itemAnimator = null
        binding.searchMemberBottomSheetRecyclerview.adapter = adapter

        selectedMemberAdapter = SelectedMemberAdapter { inUser ->
            homeViewModel.myInfo.value?.let { myInfo ->
                if (myInfo.id != inUser.id) {
                    val currentMember = mutableListOf<InUser>()

                    viewModel.currentMember.value?.let {
                        currentMember.addAll(it)

                        val user = currentMember.count { it.id == inUser.id }

                        if (user != 0) currentMember.removeIf {
                            it.id == inUser.id
                        }
                    }
                    viewModel.currentMember.value = currentMember

                    viewModel.searchUserResponse.value?.let { userSearchResponse ->
                        if (userSearchResponse.result.id == inUser.id) {
                            val user = userSearchResponse.result.apply { isChecked = false }
                            adapter.submitList(listOf()) {
                                adapter.submitList(listOf(user))
                            }
                        }
                    }
                }
            }
        }

        binding.searchMemberBottomSheetRecyclerviewSelectedMember.adapter = selectedMemberAdapter
        binding.searchMemberBottomSheetRecyclerviewSelectedMember.itemAnimator = null
    }

    private fun setObserver() {
        viewModel.currentMember.observe(viewLifecycleOwner) {
            it?.let { selectedMemberAdapter.submitList(it) }
        }

        viewModel.searchUserResponse.observe(viewLifecycleOwner) {
            it?.let {
                if (selectedMemberAdapter.currentList.any { inUser ->
                        it.result.id == inUser.id
                    }) {
                    it.result.isChecked = true
                }
                adapter.submitList(listOf(it.result))
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.searchUserResponse.value = null
        _binding = null
    }

    private fun navigateListener(user: InUser) {
        val args = bundleOf("user" to user)
        // findNavController().navigate(R.id.action_postSearchMemberBottomSheetFragment_to_photoFragment2, args)
    }

}