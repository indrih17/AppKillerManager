package com.thelittlefireman.appkillermanager.deviceUi.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import com.thelittlefireman.appkillermanager.R
import com.thelittlefireman.appkillermanager.managers.KillerManager
import com.thelittlefireman.appkillermanager.models.KillerManagerAction
import com.thelittlefireman.appkillermanager.utils.KillerManagerUtils
import kotlinx.android.synthetic.main.md_dialog_footer.view.*
import kotlinx.android.synthetic.main.md_dialog_main_content.view.*
import java.util.*

class SettingPageFragment : Fragment(), SettingPageFragmentClickListener {
    //https://guides.codepath.com/android/viewpager-with-fragmentpageradapter
    private var currentPage: Int = 0

    private var contentMessage: String? = null

    private lateinit var killerManagerAction: KillerManagerAction

    private var dontShowAgain: Boolean = false

    // Store instance variables based on arguments passed
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val arguments = requireArguments()
        killerManagerAction = KillerManagerAction.fromJson(
            arguments.getString(killerManagerActionKey)
                ?: throw NullPointerException("No contains killer manager action")
        )
        dontShowAgain = arguments.getBoolean(killerManagerDontShowAgainKey)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater
            .inflate(R.layout.md_dialog_main_content, container, false)
            .also { view ->
                view.md_content_message.text = contentMessage
                view.md_button_open_settings.setOnClickListener { onClickOpenSettings() }
                view.md_button_close.setOnClickListener { onClickClose() }

                // ----  Common UI ----
                if (dontShowAgain) {
                    view.md_promptCheckbox.visibility = View.VISIBLE
                    view.md_promptCheckbox.setOnCheckedChangeListener { _, isChecked ->
                        KillerManagerUtils.setDontShowAgain(
                            view.context,
                            isChecked
                        )
                    }
                } else {
                    view.md_promptCheckbox.visibility = View.GONE
                }

                // Inflate the layout for this fragment
                view.md_help_image_viewpager.adapter = object : PagerAdapter() {
                    override fun getCount(): Int = killerManagerAction.helpImages.size

                    override fun instantiateItem(view: ViewGroup, position: Int): Any {
                        val myImageLayout =
                            inflater.inflate(
                                R.layout.md_dialog_main_content_image_item,
                                view,
                                false
                            )
                        val myImage = myImageLayout.findViewById<ImageView>(R.id.md_help_image)
                        myImage.setImageResource(killerManagerAction.helpImages[position])
                        view.addView(myImageLayout, 0)
                        return myImageLayout
                    }

                    override fun isViewFromObject(view: View, `object`: Any): Boolean {
                        return view == `object`
                    }
                }

                // Auto start of viewpager
                val handler = Handler()
                val runnable = Runnable {
                    if (currentPage == killerManagerAction.helpImages.size) {
                        currentPage = 0
                    }
                    view.md_help_image_viewpager.setCurrentItem(currentPage++, true)
                }
                val swipeTimer = Timer()
                swipeTimer.schedule(
                    object : TimerTask() {
                        override fun run() {
                            handler.post(runnable)
                        }
                    },
                    2500,
                    2500
                )
            }

    override fun onClickOpenSettings() {
        activity?.let {
            KillerManager.doAction(it, killerManagerAction.actionType)
        }
    }

    override fun onClickClose() {
    }

    override fun startActivityForResult(intent: Intent, requestCode: Int) {
        super.startActivityForResult(intent, requestCode)
        activity?.let {
            KillerManager.onActivityResult(it, killerManagerAction.actionType, requestCode)
        }
    }

    companion object {
        private const val killerManagerActionKey = "KILLER_MANAGER_ACTION"
        private const val killerManagerDontShowAgainKey = "KILLER_MANAGER_DONT_SHOW_AGAIN"

        fun newInstance(
            killerManager: KillerManagerAction,
            dontShowAgain: Boolean
        ) =
            SettingPageFragment().also {
                it.arguments = bundleOf(
                    killerManagerActionKey to KillerManagerAction.toJson(killerManager),
                    killerManagerDontShowAgainKey to dontShowAgain
                )
            }
    }
}