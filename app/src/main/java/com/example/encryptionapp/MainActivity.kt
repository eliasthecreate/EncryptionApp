package com.example.encryptionapp

import android.content.ClipboardManager
import android.content.ClipData
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.security.SecureRandom
import java.util.Collections
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var etOriginalText: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var tvResult: TextView
    private lateinit var tvInputLabel: TextView
    private lateinit var btnAction: MaterialButton
    private lateinit var btnClear: MaterialButton
    private lateinit var btnCopy: MaterialButton
    private lateinit var btnEncryptMode: MaterialButton
    private lateinit var btnDecryptMode: MaterialButton
    private lateinit var btnGuide: MaterialButton
    private lateinit var btnGeneratePassword: MaterialButton

    private var isEncryptMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupClickListeners()

        // Set initial state
        updateUIForMode()

        // Firebase Analytics: app opened
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, null)
    }

    private fun initViews() {
        etOriginalText = findViewById(R.id.etOriginalText)
        etPassword = findViewById(R.id.etPassword)
        tvResult = findViewById(R.id.tvResult)
        tvInputLabel = findViewById(R.id.tvInputLabel)
        btnAction = findViewById(R.id.btnAction)
        btnClear = findViewById(R.id.btnClear)
        btnCopy = findViewById(R.id.btnCopy)
        btnEncryptMode = findViewById(R.id.btnEncryptMode)
        btnDecryptMode = findViewById(R.id.btnDecryptMode)
        btnGuide = findViewById(R.id.btnGuide)
        btnGeneratePassword = findViewById(R.id.btnGeneratePassword)
    }

    private fun setupClickListeners() {
        btnEncryptMode.setOnClickListener {
            isEncryptMode = true
            updateUIForMode()
        }

        btnDecryptMode.setOnClickListener {
            isEncryptMode = false
            updateUIForMode()
        }

        btnAction.setOnClickListener {
            if (isEncryptMode) {
                encryptText()
            } else {
                decryptText()
            }
        }

        btnClear.setOnClickListener {
            clearAll()
        }

        btnCopy.setOnClickListener {
            copyToClipboard()
        }

        btnGuide.setOnClickListener {
            showGuideBottomSheet()
            // Firebase Analytics: user opened guide
            Firebase.analytics.logEvent(FirebaseAnalytics.Event.TUTORIAL_BEGIN, null)
        }

        btnGeneratePassword.setOnClickListener {
            val generated = generateStrongPassword(16)
            etPassword.setText(generated)
            showToast("Generated strong password")
        }
    }

    private fun updateUIForMode() {
        if (isEncryptMode) {
            tvInputLabel.text = "Text to Encrypt"
            etOriginalText.hint = "Enter your secret message here..."
            etPassword.hint = "Enter a strong password..."
            btnAction.text = "Encrypt"
            btnAction.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.holo_green_dark)

            // Update toggle buttons
            btnEncryptMode.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.holo_green_dark)
            btnDecryptMode.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.darker_gray)
        } else {
            tvInputLabel.text = "Text to Decrypt"
            etOriginalText.hint = "Paste your encrypted text here..."
            etPassword.hint = "Enter the decryption password..."
            btnAction.text = "Decrypt"
            btnAction.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.holo_blue_dark)

            // Update toggle buttons
            btnEncryptMode.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.darker_gray)
            btnDecryptMode.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.holo_blue_dark)
        }
    }

    private fun encryptText() {
        val text = etOriginalText.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (text.isEmpty()) {
            showToast("Please enter text to encrypt")
            return
        }

        if (password.isEmpty()) {
            showToast("Please enter a password")
            return
        }

        if (password.length < 4) {
            showToast("Please use a stronger password (min 4 characters)")
            return
        }

        try {
            // Enhanced encryption with password
            val encryptedText = EnhancedEncryptionUtils.encrypt(text, password)
            tvResult.text = encryptedText
            showToast("🔒 Text encrypted successfully!")
        } catch (e: Exception) {
            showToast("Encryption failed: ${e.message}")
        }
    }

    private fun decryptText() {
        val text = etOriginalText.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (text.isEmpty()) {
            showToast("Please enter text to decrypt")
            return
        }

        if (password.isEmpty()) {
            showToast("Please enter the decryption password")
            return
        }

        try {
            val decryptedText = EnhancedEncryptionUtils.decrypt(text, password)
            tvResult.text = decryptedText
            showToast("🔓 Text decrypted successfully!")
        } catch (e: Exception) {
            showToast("Decryption failed: ${e.message ?: "Invalid password or corrupted data"}")
        }
    }

    private fun copyToClipboard() {
        val resultText = tvResult.text.toString()
        if (resultText.isNotEmpty() && resultText != "Your encrypted or decrypted text will appear here...") {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Encrypted Text", resultText)
            clipboard.setPrimaryClip(clip)
            showToast("📋 Copied to clipboard!")
        } else {
            showToast("No result to copy")
        }
    }

    private fun clearAll() {
        etOriginalText.setText("")
        etPassword.setText("")
        tvResult.text = "Your encrypted or decrypted text will appear here..."
        showToast("Cleared all fields!")
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showGuideBottomSheet() {
        val dialog = BottomSheetDialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.bottomsheet_guide, null, false)
        dialog.setContentView(view)

        val btnSampleEncrypt = view.findViewById<MaterialButton>(R.id.btnSampleEncrypt)
        val btnSampleDecrypt = view.findViewById<MaterialButton>(R.id.btnSampleDecrypt)

        btnSampleEncrypt.setOnClickListener {
            val sampleText = "Secrets are safe here!"
            val samplePassword = "P@ssw0rd!2025"
            isEncryptMode = true
            updateUIForMode()
            etOriginalText.setText(sampleText)
            etPassword.setText(samplePassword)
            encryptText()
        }

        btnSampleDecrypt.setOnClickListener {
            val sampleText = "Hello Crypto"
            val samplePassword = "P@ssw0rd!2025"
            val cipher = EnhancedEncryptionUtils.encrypt(sampleText, samplePassword)
            isEncryptMode = false
            updateUIForMode()
            etOriginalText.setText(cipher)
            etPassword.setText(samplePassword)
            decryptText()
        }

        dialog.show()
    }

    private fun generateStrongPassword(length: Int = 16): String {
        val upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val lower = "abcdefghijklmnopqrstuvwxyz"
        val digits = "0123456789"
        val symbols = "!@#%&*?-_=+" // avoid confusing chars like quotes/backslashes
        val all = upper + lower + digits + symbols

        val rnd = SecureRandom()
        val chars = ArrayList<Char>(length)

        // ensure at least one of each category
        chars.add(upper[rnd.nextInt(upper.length)])
        chars.add(lower[rnd.nextInt(lower.length)])
        chars.add(digits[rnd.nextInt(digits.length)])
        chars.add(symbols[rnd.nextInt(symbols.length)])

        for (i in chars.size until length) {
            chars.add(all[rnd.nextInt(all.length)])
        }

        // shuffle using SecureRandom
        Collections.shuffle(chars, rnd)
        return chars.joinToString("")
    }
}