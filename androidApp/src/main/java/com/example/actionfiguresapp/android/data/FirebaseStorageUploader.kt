package com.example.actionfiguresapp.android.data

import com.example.actionfiguresapp.domain.repository.StorageUploader
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseStorageUploader : StorageUploader {

    override suspend fun uploadImageBytes(path: String, bytes: ByteArray): String {
        val ref = FirebaseStorage.getInstance().reference.child(path)

        suspendCancellableCoroutine<Unit> { cont ->
            ref.putBytes(bytes)
                .addOnSuccessListener { cont.resume(Unit) }
                .addOnFailureListener { cont.resumeWithException(it) }
        }

        return suspendCancellableCoroutine { cont ->
            ref.downloadUrl
                .addOnSuccessListener { uri -> cont.resume(uri.toString()) }
                .addOnFailureListener { cont.resumeWithException(it) }
        }
    }
}
