package com.aniketsingh.foodhub.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.aniketsingh.foodhub.R

class QuestionsFragment : Fragment() {

    lateinit var txtq1 : TextView
    lateinit var txtq2 : TextView
    lateinit var txta1 : TextView
    lateinit var txta2 : TextView
    lateinit var txtq3 : TextView
    lateinit var txtq4 : TextView
    lateinit var txta3 : TextView
    lateinit var txta4 : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_questions, container, false)

        txtq1 = view.findViewById(R.id.faqs_q1)
        txtq2 = view.findViewById(R.id.faqs_q2)
        txta1 = view.findViewById(R.id.faqs_a1)
        txta2 = view.findViewById(R.id.faqs_a2)
        txtq3 = view.findViewById(R.id.faqs_q3)
        txtq4 = view.findViewById(R.id.faqs_q4)
        txta3 = view.findViewById(R.id.faqs_a3)
        txta4 = view.findViewById(R.id.faqs_a4)

        txtq1.text = "Q1. You don’t have the product I need, what should I do?"
        txta1.text = "A1. You can write to us at packaging@foodhub.in and we will try our best to include your need in our catalog."
        txtq2.text = "Q2. Is delivery free?"
        txta2.text = "A2. Yes. There are no delivery or handling charges."
        txtq3.text = "Q3. Can I buy the goods on credit?"
        txta3.text = "A3. Sorry, we don’t allow a credit on goods currently."
        txtq4.text = "Q4. Why is my location is not serviceable?"
        txta4.text = "A4. We are currently live only in a few cities. We will be expanding soon."

        return view
    }

}
