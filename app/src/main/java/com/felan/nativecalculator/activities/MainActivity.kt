package com.felan.nativecalculator.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableRow
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.felan.nativecalculator.R
import com.felan.nativecalculator.databinding.ActivityMainBinding
import com.felan.nativecalculator.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by lazy { ViewModelProviders.of(this).get(MainViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        initializeViews()
    }

    private fun initializeViews() {
        tbl_main.children.filter { it is ViewGroup || it is Button }
            .flatMap {
                if (it is Button) sequenceOf(it)
                else (it as ViewGroup).children.filter { it is Button }
            }
            .map { it as Button }
            .filter { it.text.length == 1 }
            .forEach {
                it.setOnClickListener { _ ->
                    viewModel.nextInput(it.text[0])
                }
            }
        btn_backspace.setOnClickListener { viewModel.nextInput('\b') }
    }
}
