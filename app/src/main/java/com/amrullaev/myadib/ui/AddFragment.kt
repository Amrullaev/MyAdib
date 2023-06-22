package com.amrullaev.myadib.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.webkit.PermissionRequest
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.amrullaev.myadib.MainActivity
import com.amrullaev.myadib.R
import com.amrullaev.myadib.databinding.FragmentAddBinding
import com.amrullaev.myadib.models.Writer
import com.github.ybq.android.spinkit.style.Circle
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.PermissionListener


class AddFragment : Fragment() {
    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private var typeSelected = false
    private var imageUri: String? = null
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var reference: StorageReference
    private lateinit var firebaseFirestore: FirebaseFirestore
    private val TAG = "AddFragment"
    private var isDownloaded = false
    private var isWait = false
    private var isIS = false
    private var listName: ArrayList<String>? = null
    private var isNameExsist = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddBinding.inflate(layoutInflater, container, false)

        val circle = Circle()
        binding.spinKit.setIndeterminateDrawable(circle)

        firebaseStorage = FirebaseStorage.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        loadData()

        reference = firebaseStorage.getReference("writters")
        val type = arrayOf("Mumtoz adabiyoti", "O\'zbek adabiyoti", "Jahon adabiyoti")
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, type)
        binding.typeSpinner.setAdapter(arrayAdapter)
        binding.typeSpinner.setOnItemClickListener { parent, view, position, id ->
            typeSelected = true
        }

        binding.getphotoBtn.setOnClickListener {
            checkGalleryPermissions()
        }

        binding.saveBtn.setOnClickListener {
            if (imageUri == null) {
                Toast.makeText(it.context, "Rasm yuklanmadi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val fullName = binding.fullnameEt.text.toString()
            val bornYear = binding.bornyearEt.text.toString()
            val deathYear = binding.deathyearEt.text.toString()
            val typeWriter = binding.typeSpinner.text.toString()
            val info = binding.aboutEt.text.toString()
            isNameExsist = false
            listName?.forEach {
                if (it == fullName) isNameExsist = true
            }
            if (fullName.isNotEmpty() && bornYear.isNotEmpty() && deathYear.isNotEmpty() && typeWriter.isNotEmpty() && info.isNotEmpty() && isIS && !isNameExsist) {
                isWait = true

                if (isDownloaded) {
                    val writter =
                        Writer(fullName, bornYear, deathYear, typeWriter, info, imageUri!!)
                    firebaseFirestore.collection("writers").add(writter)
                        .addOnSuccessListener {
                            (activity as MainActivity?)?.currentFocus?.let { view ->
                                val imm =
                                    (activity as MainActivity).getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                                imm?.hideSoftInputFromWindow(view.windowToken, 0)
                            }
                            (activity as MainActivity?)?.onBackPressed()
                        }
                } else binding.layout.visibility = View.VISIBLE
            } else if (isNameExsist) {
                Toast.makeText(
                    requireContext(),
                    "O\'xshash ma\'lumot kiritilgan!",
                    Toast.LENGTH_SHORT
                ).show()
            } else
                Toast.makeText(
                    requireContext(),
                    "Ma\'lumotni to\'liq kiriting!",
                    Toast.LENGTH_SHORT
                ).show()
        }
        return binding.root
    }

    private fun checkGalleryPermissions() {
        Dexter.withContext(requireContext())
            .withPermission(android.Manifest.permission.READ_MEDIA_IMAGES)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    getImageFromGallery()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    val alertD = AlertDialog.Builder(requireContext()).create()
                    alertD.setMessage("Ilovadan foydalanish uchun sozlamalar orqali ruxsat bering!")
                    alertD.setButton(
                        Dialog.BUTTON_POSITIVE, "Sozlamalar",
                        DialogInterface.OnClickListener { dialog, ss ->
                            val intent = Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", activity?.packageName, null)
                            )
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            dialog.dismiss()
                        })
                    alertD.setButton(
                        Dialog.BUTTON_NEGATIVE, "Ortga",
                        DialogInterface.OnClickListener { dialog, ss ->
                            dialog.dismiss()
                        })

                    alertD.show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: com.karumi.dexter.listener.PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    p1?.continuePermissionRequest()
                }

            }).check()
    }

    private fun getImageFromGallery() {
        getImageContent.launch("image/*")
    }

    private val getImageContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                //  Log.d(TAG, "uri SUCCESS")
                binding.photoIv.setImageURI(uri)
                uploadTask(uri)
                isIS = true
            }
        }

    private fun uploadTask(uri: Uri) {
        val m = System.currentTimeMillis()
        val uploadTask = reference.child(m.toString()).putFile(uri)
        binding.layout.visibility = View.VISIBLE
        uploadTask.addOnSuccessListener {
            if (it.task.isSuccessful) {
                val downloadUri = it.metadata?.reference?.downloadUrl!!
                Log.d(TAG, "upload SUCCESS")

                downloadUri.addOnSuccessListener { uri ->
                    imageUri = uri.toString()
                    binding.layout.visibility = View.GONE
                    isDownloaded = true
                    if (isWait) {
                        binding.saveBtn.performClick()
                    }
                    Toast.makeText(
                        requireContext(),
                        "Rasm muvaffaqiyatli yuklandi!",
                        Toast.LENGTH_LONG
                    ).show()
                }.addOnFailureListener {
                    binding.layout.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Rasm yuklashda xatolik yuz berdi!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }.addOnProgressListener {
            val last = String.format(
                "Ma\'lumotlar yuklanmoqda:\n\n%5.2f MB/${
                    (it.totalByteCount / 1000000.0).toString().take(4)
                } MB",
                it.bytesTransferred / 1000000.0
            )
            binding.progressTv.text = last
        }.addOnFailureListener {
            Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadData() {
        listName = arrayListOf()

        firebaseFirestore.collection("writers")
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    it.result?.forEach { queryDocumentSnapshot ->
                        val writer = queryDocumentSnapshot.toObject(Writer::class.java)
                        listName!!.add(writer.fullname!!)
                    }

                }
            }
    }

}