package com.example.dolcetto.data.repository

import android.util.Log
import com.example.dolcetto.domain.model.Order
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class OrderRepository @Inject constructor(
    private val db: FirebaseFirestore
) {


//    fun getOrders() = callbackFlow<List<Order>> {
//        val listener = db.collection("orders")
//            .addSnapshotListener { snapshot, error ->
//                if (error != null) {
//                    close(error)
//                    return@addSnapshotListener
//                }
//                Log.d("Firestore", "Snapshot received: ${snapshot?.documents?.size}")
//
//
//                val orders = snapshot?.documents?.mapNotNull { document ->
//                    try {
//                        document.toObject(Order::class.java)
//                    } catch (e: Exception) {
//                        Log.e("Firestore", "Ошибка маппинга документа ${document.id}: ${e.message}")
//                        null
//                    }
//                } ?: emptyList()
//
//                trySend(orders)
//            }
//
//        awaitClose { listener.remove() }
//    }

    fun getOrders() = callbackFlow<List<Order>> {
        val listener = db.collection("orders")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                Log.d("Firestore", "Snapshot received: ${snapshot?.documents?.size}")


                val orders = snapshot?.documents?.mapNotNull { document ->
                    try {
                        // Добавляем копирование document.id в объект Order
                        document.toObject(Order::class.java)?.copy(id = document.id)
                    } catch (e: Exception) {
                        Log.e("Firestore", "Ошибка маппинга документа ${document.id}: ${e.message}")
                        null
                    }
                } ?: emptyList()

                trySend(orders)
            }

        awaitClose { listener.remove() }
    }



    suspend fun updateOrder(orderId: String, newStatus: String): Resource<Unit> {
        Log.d("OrderRepo", "Updating order: $orderId")
        return try {
            db.collection("orders").document(orderId)
                .update("status", newStatus)
                .await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Log.e("OrderRepo", "Update error: ${e.message}", e)
            Resource.Error(e.message ?: "Error")
        }
    }
}