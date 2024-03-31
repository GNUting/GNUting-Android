package com.changs.android.gnuting_android.ui.fragment

import android.os.Bundle
import android.text.InputFilter
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.data.model.ReportCategory
import com.changs.android.gnuting_android.data.model.ReportRequest
import com.changs.android.gnuting_android.data.model.UserReportRequest
import com.changs.android.gnuting_android.databinding.FragmentReportBinding
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import com.changs.android.gnuting_android.viewmodel.ReportViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class ReportFragment :
    BaseFragment<FragmentReportBinding>(FragmentReportBinding::bind, R.layout.fragment_report) {
    private val args: ReportFragmentArgs by navArgs()
    private val viewModel: HomeMainViewModel by activityViewModels()
    private val reportViewModel: ReportViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (reportViewModel.reportCategory) {
            ReportCategory.COMMERCIAL_SPAM -> {
                reportCheckBoxClear()
                binding.reportCheck1.isChecked = true
            }

            ReportCategory.ABUSIVE_LANGUAGE -> {
                reportCheckBoxClear()
                binding.reportCheck2.isChecked = true
            }

            ReportCategory.OBSCENITY -> {
                reportCheckBoxClear()
                binding.reportCheck3.isChecked = true
            }

            ReportCategory.FLOODING -> {
                reportCheckBoxClear()
                binding.reportCheck4.isChecked = true
            }

            ReportCategory.PRIVACY_VIOLATION -> {
                reportCheckBoxClear()
                binding.reportCheck5.isChecked = true
            }

            ReportCategory.OTHER -> {
                reportCheckBoxClear()
                binding.reportCheck6.isChecked = true
            }
        }

        setListener()
        setObserver()
    }

    private fun setObserver() {
        viewModel.reportResponse.eventObserve(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        viewModel.userReportResponse.eventObserve(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    private fun setListener() {
        val inputFilter = InputFilter { _, _, _, dest, dstart, _ ->
            // 입력된 텍스트에서 줄 수 계산
            val lineCount = dest.toString().substring(0, dstart).split("\n").size

            // 20줄 이상인 경우 입력 제한
            if (lineCount >= 20) ""
            else null
        }

        binding.reportEdit.filters = arrayOf(inputFilter)

        binding.reportBtnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.reportCheck1.setOnClickListener {
            reportCheckBoxClear()
            binding.reportCheck1.isChecked = true
            reportViewModel.reportCategory = ReportCategory.COMMERCIAL_SPAM
        }

        binding.reportCheck2.setOnClickListener {
            reportCheckBoxClear()
            binding.reportCheck2.isChecked = true
            reportViewModel.reportCategory = ReportCategory.ABUSIVE_LANGUAGE
        }

        binding.reportCheck3.setOnClickListener {
            reportCheckBoxClear()
            binding.reportCheck3.isChecked = true
            reportViewModel.reportCategory = ReportCategory.OBSCENITY
        }

        binding.reportCheck4.setOnClickListener {
            reportCheckBoxClear()
            binding.reportCheck4.isChecked = true
            reportViewModel.reportCategory = ReportCategory.FLOODING
        }

        binding.reportCheck5.setOnClickListener {
            reportCheckBoxClear()
            binding.reportCheck5.isChecked = true
            reportViewModel.reportCategory = ReportCategory.PRIVACY_VIOLATION
        }

        binding.reportCheck6.setOnClickListener {
            reportCheckBoxClear()
            binding.reportCheck6.isChecked = true
            reportViewModel.reportCategory = ReportCategory.OTHER
        }

        binding.reportBtnReport.setOnClickListener {
            if (args.nickname != null) {
                val reportRequest = UserReportRequest(
                    args.nickname!!,
                    reportViewModel.reportCategory.name,
                    binding.reportEdit.text.toString()
                )
                viewModel.userReport(reportRequest)
            } else {
                // 게시물 신고하기
                if (binding.reportEdit.text.isNullOrEmpty()) {
                    Snackbar.make(binding.root, "신고 사유를 작성해주세요.", Snackbar.LENGTH_SHORT).show()
                    viewModel.onSnackbarShown()
                } else {
                    val reportRequest = ReportRequest(
                        args.id,
                        reportViewModel.reportCategory.name,
                        binding.reportEdit.text.toString()
                    )
                    viewModel.report(reportRequest)
                }
            }
        }

        binding.reportImgClose.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun reportCheckBoxClear() {
        binding.reportCheck1.isChecked = false
        binding.reportCheck2.isChecked = false
        binding.reportCheck3.isChecked = false
        binding.reportCheck4.isChecked = false
        binding.reportCheck5.isChecked = false
        binding.reportCheck6.isChecked = false
    }
}