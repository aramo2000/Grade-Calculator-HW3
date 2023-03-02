package com.example.gradecalculatorhw3

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.widget.Toast
import android.content.SharedPreferences

class MainActivity : AppCompatActivity() {

    private var participationAttendanceGrade = 0.0
    private var finalProjectGrade = 0.0
    private var groupPresentationGrade = 0.0
    private var midtermExam1Grade = 0.0
    private var midtermExam2Grade = 0.0
    private var finalGrade = 0.0
    private var homeworkGrades: MutableList<Double> = mutableListOf()

    private var participationAttendanceEditText: EditText? = null
    private var groupPresentationEditText: EditText? = null
    private var midtermExam1EditText: EditText? = null
    private var midtermExam2EditText: EditText? = null
    private var finalProjectEditText: EditText? = null
    private var homeworkGradeEditText: EditText? = null
    private var homeworkGradesTextView: TextView? = null
    private var finalGradeTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        participationAttendanceEditText = findViewById(R.id.participation_attendance_edittext)
        groupPresentationEditText = findViewById(R.id.group_presentation_edittext)
        midtermExam1EditText = findViewById(R.id.midterm_exam1_edittext)
        midtermExam2EditText = findViewById(R.id.midterm_exam2_edittext)
        finalProjectEditText = findViewById(R.id.final_project_edittext)


        homeworkGradeEditText = findViewById(R.id.homework_grade_edittext)
        homeworkGradesTextView = findViewById(R.id.homework_grades_textview)
        finalGradeTextView = findViewById(R.id.final_grade_textview)

        //for homeworks
        val addHomeworkButton: Button = findViewById(R.id.add_homework_button)
        addHomeworkButton.setOnClickListener {
            val homeworkGrade: Double = homeworkGradeEditText?.text.toString().toDoubleOrNull() ?: return@setOnClickListener
            if(homeworkGrade>100) {
                Toast.makeText(this, "Grade must be between 0 and 100", Toast.LENGTH_SHORT).show()
            } else if (homeworkGrades.size < 5) {
                homeworkGrades.add(homeworkGrade)
                val homeworkGradesText = homeworkGrades.joinToString(separator = "\n")
                homeworkGradesTextView?.text = "Homework grades (up to 5):\n$homeworkGradesText"
                homeworkGradeEditText?.text?.clear()
            } else {
                Toast.makeText(this, "Already 5 homework grades inserted", Toast.LENGTH_SHORT).show()
            }
        }
        val resetButton: Button = findViewById(R.id.reset_button)
        resetButton.setOnClickListener {
            homeworkGrades.clear()
            homeworkGradesTextView?.text = "Homework grades (up to 5):"
            homeworkGradeEditText?.text?.clear()
        }

        val calculateButton: Button = findViewById(R.id.calculate_button)
        calculateButton.setOnClickListener {
            calculateFinalGrade()
        }
    }

    private fun calculateFinalGrade() {
        participationAttendanceGrade = participationAttendanceEditText?.text.toString().toDoubleOrNull() ?: 0.0
        groupPresentationGrade = groupPresentationEditText?.text.toString().toDoubleOrNull() ?: 0.0
        midtermExam1Grade = midtermExam1EditText?.text.toString().toDoubleOrNull() ?: 0.0
        midtermExam2Grade = midtermExam2EditText?.text.toString().toDoubleOrNull() ?: 0.0
        finalProjectGrade = finalProjectEditText?.text.toString().toDoubleOrNull() ?: 0.0
        if(participationAttendanceGrade>100 || groupPresentationGrade>100 || midtermExam1Grade>100 || midtermExam2Grade>100 || finalProjectGrade>100) {
            Toast.makeText(this, "Grade must be between 0 and 100", Toast.LENGTH_SHORT).show()
        } else {
            val homeworkGradesTotal: Double = homeworkGrades.sum() / 5.0
            finalGrade =
                participationAttendanceGrade * 0.1 + homeworkGradesTotal * 0.2 + groupPresentationGrade * 0.1 + midtermExam1Grade * 0.1 + midtermExam2Grade * 0.2 + finalProjectGrade * 0.3

            finalGradeTextView?.text = "Final grade: ${"%.2f".format(finalGrade)}"
        }
    }


    override fun onPause() {
        super.onPause()

        // Get the SharedPreferences object
        val sharedPreferences = getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE)

        // Get the editor object to write to SharedPreferences
        val editor = sharedPreferences.edit()

        // Write the grades to SharedPreferences
        editor.putFloat("participation_attendance_grade", participationAttendanceGrade.toFloat())
        editor.putFloat("group_presentation_grade", groupPresentationGrade.toFloat())
        editor.putFloat("midterm_exam1_grade", midtermExam1Grade.toFloat())
        editor.putFloat("midterm_exam2_grade", midtermExam2Grade.toFloat())
        editor.putFloat("final_project_grade", finalProjectGrade.toFloat())
        editor.putStringSet("homework_grades", homeworkGrades.map { it.toString() }.toSet())
        editor.putFloat("final_grade", finalGrade.toFloat())

        // Apply the changes to SharedPreferences
        editor.apply()
    }

    override fun onResume() {
        super.onResume()

        // Get the SharedPreferences object
        val sharedPreferences = getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE)

        // Get the saved grades from SharedPreferences
        participationAttendanceGrade = sharedPreferences.getFloat("participation_attendance_grade", 0f).toDouble()
        groupPresentationGrade = sharedPreferences.getFloat("group_presentation_grade", 0f).toDouble()
        midtermExam1Grade = sharedPreferences.getFloat("midterm_exam1_grade", 0f).toDouble()
        midtermExam2Grade = sharedPreferences.getFloat("midterm_exam2_grade", 0f).toDouble()
        finalProjectGrade = sharedPreferences.getFloat("final_project_grade", 0f).toDouble()

        val savedHomeworkGrades =
            sharedPreferences.getStringSet("homework_grades", emptySet()) ?: emptySet()
        homeworkGrades = savedHomeworkGrades.mapNotNull { it.toDoubleOrNull() }.toMutableList()
        finalGrade = sharedPreferences.getFloat("final_grade", 0f).toDouble()

        // Update the UI with the saved grades
        participationAttendanceEditText?.setText(participationAttendanceGrade.toString())
        groupPresentationEditText?.setText(groupPresentationGrade.toString())
        midtermExam1EditText?.setText(midtermExam1Grade.toString())
        midtermExam2EditText?.setText(midtermExam2Grade.toString())
        finalProjectEditText?.setText(finalProjectGrade.toString())
        updateHomeworkGradesTextView()
        updateFinalGradeTextView()
    }

    private fun updateHomeworkGradesTextView() {
        val sb = StringBuilder()
        for ((index, grade) in homeworkGrades.withIndex()) {
            sb.append("Homework ${index + 1}: $grade\n")
        }
        homeworkGradesTextView?.text = sb.toString()
    }

    private fun updateFinalGradeTextView() {
        val finalGrade = calculateFinalGrade()
        finalGradeTextView?.text = "Final Grade: $finalGrade"
    }
}