package com.thelittlefireman.appkillermanager.deviceUi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.thelittlefireman.appkillermanager.R
import com.thelittlefireman.appkillermanager.deviceUi.fragments.SettingPageFragment
import com.thelittlefireman.appkillermanager.models.KillerManagerAction
import kotlinx.android.synthetic.main.md_dialog_ui.view.*

class SettingFragment : Fragment() {
    private var dontShowAgain: Boolean = false

    private val settingPageFragmentList = ArrayList<SettingPageFragment>()

    private val killerManagerActionList = ArrayList<KillerManagerAction>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val arguments = requireArguments()

        killerManagerActionList.clear()
        killerManagerActionList.addAll(
            KillerManagerAction.fromJsonList(
                arguments.getString(killerManagerListKey)
                    ?: throw NullPointerException("No contains killer manager list")
            )
        )
        dontShowAgain = arguments.getBoolean(killerManagerDontShowAgainKey)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater
        .inflate(R.layout.md_dialog_ui, container, false)
        .also { view ->
            val settingPageAdapter = SettingPageAdapter(requireFragmentManager())
            view.md_viewpager_main_content.adapter = settingPageAdapter
            initKillerManagerAction(killerManagerActionList, settingPageAdapter)
        }

    private fun initKillerManagerAction(
        killerManagerActionList: List<KillerManagerAction>,
        settingPageAdapter: SettingPageAdapter
    ) {
        for (killerManagerAction in killerManagerActionList) {
            val settingPageFragment = SettingPageFragment.newInstance(
                killerManagerAction,
                dontShowAgain
            )
            settingPageFragmentList.add(settingPageFragment)
        }
        settingPageAdapter.notifyDataSetChanged()
    }


    private inner class SettingPageAdapter(
        fm: FragmentManager
    ) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment = settingPageFragmentList[position]

        override fun getCount(): Int = settingPageFragmentList.size

        override fun isViewFromObject(view: View, `object`: Any): Boolean = false
    }

    companion object {
        private const val killerManagerListKey = "KILLER_MANAGER_LIST"
        private const val killerManagerDontShowAgainKey = "KILLER_MANAGER_DONT_SHOW_AGAIN"

        fun generateArguments(
            killerManagerActionList: List<KillerManagerAction>,
            dontShowAgain: Boolean
        ): Bundle =
            bundleOf(
                killerManagerListKey to KillerManagerAction.toJsonList(killerManagerActionList),
                killerManagerDontShowAgainKey to dontShowAgain
            )
    }
}
